package core.game.world

import core.ServerConstants
import core.api.log
import core.game.system.task.Pulse
import core.integration.grafana.Grafana
import core.tools.Log
import java.util.concurrent.LinkedBlockingQueue

class PulseRunner {
    private val pulses = LinkedBlockingQueue<Pulse>()

    val currentPulses: Array<Pulse> get() = pulses.toTypedArray()

    fun submit(pulse: Pulse) {
        pulses.add(pulse)
    }

    fun updateAll() {
        val pulseCount = pulses.size

        var totalTimePulses = 0

        for (i in 0 until pulseCount) {
            val pulse = pulses.take()

            val elapsedTime =
                measure {
                    try {
                        if (!pulse.update() && pulse.isRunning) {
                            pulses.add(pulse)
                        }
                    } catch (e: Exception) {
                        log(this::class.java, Log.ERR, "Pulse execution error. Stack trace below.")
                        e.printStackTrace()
                    }
                }

            val pulseName = pulse::class.java.name

            totalTimePulses += elapsedTime.toInt()

            Grafana.addPulseLength(pulseName, elapsedTime.toInt())
            Grafana.countPulse(pulseName)

            notifyIfTooLong(pulse, elapsedTime)
        }

        if (ServerConstants.GRAFANA_LOGGING) {
            Grafana.otherPulseTime = totalTimePulses
        }
    }

    private fun measure(logic: () -> Unit): Long {
        val startTime = System.currentTimeMillis()
        logic()
        return System.currentTimeMillis() - startTime
    }

    private fun notifyIfTooLong(pulse: Pulse, elapsedTime: Long) {
        if (elapsedTime >= 100) {
            log(
                this::class.java,
                Log.WARN,
                "CRITICALLY long running pulse - ${pulse.javaClass.name} took $elapsedTime ms",
            )
        } else if (elapsedTime >= 30) {
            log(
                this::class.java,
                Log.WARN,
                "Long running pulse - ${pulse.javaClass.name} took $elapsedTime ms",
            )
        }
    }
}