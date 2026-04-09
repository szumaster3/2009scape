package core.worker

import core.Server
import core.ServerConstants
import core.ServerStore
import core.api.log
import core.api.submitWorldPulse
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.repository.Repository
import core.game.world.update.UpdateSequence
import core.integration.grafana.Grafana
import core.net.packet.PacketProcessor
import core.net.packet.PacketWriteQueue
import core.plugin.type.Managers
import core.tools.Log
import core.tools.NetworkReachability
import java.lang.Long.max
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

/**
 * Primary game loop worker responsible for executing the **main server tick cycle**.
 *
 * This worker runs in a dedicated thread and performs all critical game operations
 * on a fixed interval (~600ms).
 *
 * ## Handles:
 *
 * - Processing incoming packets.
 * - Updating world pulses (tasks/events).
 * - Running entity update sequences (players, NPCs, etc).
 * - Flushing outgoing packets.
 * - Managing player connectivity and fail-safe disconnections.
 * - Executing daily/weekly reset logic.
 *
 * The update loop is tightly coupled with [GameWorld] and should be considered
 * the **heartbeat of the server**.
 *
 * ### Tick Flow Overview
 *
 * Each cycle performs:
 * 1. Network processing ([PacketProcessor])
 * 2. Disconnection queue handling
 * 3. Pulse/task updates ([GameWorld.Pulser])
 * 4. Entity update sequence ([UpdateSequence])
 * 5. Global tick increment
 * 6. Plugin manager ticking ([Managers])
 * 7. Packet flushing ([PacketWriteQueue])
 *
 * ### Offline Mode
 * If [Server.networkReachability] is not reachable:
 * - Only minimal ticking is performed.
 * - No packet processing or entity updates occur.
 *
 * ### Safety Mechanisms
 * - Players inactive without proper disconnect are forcefully removed.
 * - Players exceeding ping timeout are disconnected.
 *
 * ### Restart Handling
 * At midnight (00:00):
 * - Daily and weekly entries are cleared.
 * - Optional automatic server restart is triggered.
 *
 * @author Ceikry
 */
class MajorUpdateWorker {

    /**
     * Indicates whether the worker loop is currently running.
     */
    var running: Boolean = false

    /**
     * Indicates whether the worker thread has been started at least once.
     */
    var started = false

    /**
     * Handles entity update sequencing (players, NPCs, masks, etc.).
     */
    val sequence = UpdateSequence()

    /**
     * Time formatter used for daily reset checks (HHmmss).
     */
    val sdf = SimpleDateFormat("HHmmss")

    /**
     * Internal worker thread executing the main tick loop.
     */
    val worker = Thread {
        Thread.currentThread().name = "Major Update Worker"
        started = true

        // Small startup delay to allow server systems to initialize
        Thread.sleep(600L)

        while (running) {
            Grafana.startTick()
            val start = System.currentTimeMillis()

            // Server heartbeat hook
            Server.heartbeat()

            if (Server.networkReachability == NetworkReachability.REACHABLE) {
                handleTickActions()
            } else {
                tickOffline()
            }

            // Player safety checks (timeouts / invalid states)
            for (player in Repository.players.filter { !it.isArtificial }) {

                // Ping timeout check (20 seconds)
                if (System.currentTimeMillis() - player.session.lastPing > 20000L) {
                    player.session.lastPing = Long.MAX_VALUE
                    player.session.disconnect()
                }

                // Failsafe: player inactive but not queued for disconnect
                if (!player.isActive &&
                    !Repository.disconnectionQueue.contains(player.name) &&
                    player.getAttribute("logged-in-fully", false)
                ) {
                    player.session.disconnect()
                    log(
                        MajorUpdateWorker::class.java,
                        Log.WARN,
                        "Manually disconnecting ${player.name} due to invalid inactive state."
                    )
                }
            }

            // Daily reset handling (midnight)
            if (sdf.format(Date()).toInt() == 0) {

                // Weekly reset (Monday)
                if (GameWorld.checkDay() == 1) {
                    ServerStore.clearWeeklyEntries()
                }

                ServerStore.clearDailyEntries()

                // Optional scheduled restart
                if (ServerConstants.DAILY_RESTART) {
                    for (player in Repository.players.filter { !it.isArtificial }) {
                        player.packetDispatch.sendSystemUpdate(500)
                    }

                    ServerConstants.DAILY_RESTART = false

                    submitWorldPulse(object : Pulse(100) {
                        var counter = 0

                        override fun pulse(): Boolean {
                            counter++

                            for (player in Repository.players.filter { !it.isArtificial }) {
                                player.packetDispatch.sendSystemUpdate((5 - counter) * 100)
                            }

                            if (counter == 5) {
                                exitProcess(0)
                            }
                            return false
                        }
                    })
                }
            }

            val end = System.currentTimeMillis()

            // Metrics collection
            Grafana.totalTickTime = (end - start).toInt()
            Grafana.endTick()

            // Maintain ~600ms tick rate
            Thread.sleep(max(600 - (end - start), 0))
        }

        log(this::class.java, Log.FINE, "Update worker stopped.")
    }

    /**
     * Executes a minimal tick cycle when the server is offline.
     *
     * This ensures:
     * - Disconnection queue continues processing
     * - Global tick counter still advances
     *
     * No gameplay logic or packet processing is performed.
     */
    fun tickOffline() {
        Repository.disconnectionQueue.update()
        GameWorld.pulse()
    }

    /**
     * Executes the full tick update cycle.
     *
     * @param skipPulseUpdate If true, skips updating [GameWorld.Pulser].
     * Useful for controlled environments or debugging.
     */
    fun handleTickActions(skipPulseUpdate: Boolean = false) {
        try {
            val packetStart = System.currentTimeMillis()

            // Process incoming packets
            PacketProcessor.processQueue()
            Grafana.packetProcessTime = (System.currentTimeMillis() - packetStart).toInt()

            // Handle pending disconnections
            Repository.disconnectionQueue.update()

            // Update world pulses/tasks
            if (!skipPulseUpdate) {
                GameWorld.Pulser.updateAll()
            }

            // Tick registered listeners
            GameWorld.tickListeners.forEach { it.tick() }

            // Run entity update sequence
            sequence.start()
            sequence.run()
            sequence.end()

            // Increment global tick counter
            GameWorld.pulse()

            // Tick plugin managers
            Managers.tick()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                // Flush outgoing packets
                PacketWriteQueue.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Starts the update worker thread.
     *
     * This method is safe to call only once.
     */
    fun start() {
        if (!started) {
            running = true
            worker.start()
        }
    }

    /**
     * Stops the update worker thread.
     *
     * This will interrupt the thread and terminate the main loop.
     */
    fun stop() {
        running = false
        worker.interrupt()
    }
}