package content.global.random.event.prison

import com.google.gson.JsonObject
import core.api.*
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.system.timer.PersistTimer
import core.game.system.timer.TimerFlag

class PrisonTimer :
    PersistTimer(
        runInterval = 1,
        identifier = "prison_timer",
        flags = arrayOf(TimerFlag.ClearOnDeath)
    ) {

    var started = false

    override fun onRegister(entity: Entity) {
        if (entity !is Player) return

        if (!started) {
            started = true
            runInterval = HOURS_24_TICKS
            nextExecution = getWorldTicks() + runInterval
        }
    }

    override fun run(entity: Entity): Boolean{
        if (entity !is Player) return false
        if (!inBorders(entity,PrisonPeteUtils.PRISON_ZONE)) return false

        applyPenalty(entity)
        return false
    }

    override fun save(root: JsonObject, entity: Entity) {
        root.addProperty("ticksLeft", nextExecution - getWorldTicks())
        root.addProperty("started", started)
    }

    override fun parse(root: JsonObject, entity: Entity) {
        runInterval = root.get("ticksLeft")?.asInt ?: HOURS_24_TICKS
        started = root.get("started")?.asBoolean ?: false
    }

    override fun beforeRegister(entity: Entity) {
        if (hasTimerActive<PrisonTimer>(entity)) {
            removeTimer(entity, this)
        }
    }

    private fun applyPenalty(player: Player) {
        sendMessage(player, "Your time in prison leaves you strangely weakened.")

        for (i in 0 until 24) {
            drainStatLevel(player, i, 1.0, 1.0)
        }
    }

    companion object {
        const val HOURS_24_TICKS = 144000

        fun start(player: Player) {
            val timer = spawnTimer<PrisonTimer>()
            registerTimer(player, timer)
        }

        fun stop(player: Player) {
            removeTimer<PrisonTimer>(player)
        }

        fun getTicksLeft(player: Player): Int {
            val timer = getTimer<PrisonTimer>(player) ?: return 0
            return timer.nextExecution - getWorldTicks()
        }
    }
}