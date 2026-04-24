package content.global.random.event.prison

import com.google.gson.JsonObject
import core.api.*
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.system.timer.PersistTimer
import core.game.system.timer.TimerFlag

// https://runescape.wiki/w/Prison_Pete?oldid=862724
class PrisonTimer : PersistTimer(
    runInterval = 0,
    identifier = "ame:prison",
    flags = arrayOf(TimerFlag.ClearOnDeath)
) {

    override fun onRegister(entity: Entity) {
        if (entity !is Player) return
        nextExecution = getWorldTicks() + DURATION
    }

    override fun run(entity: Entity): Boolean {
        if (entity !is Player) return false

        if (!inBorders(entity, PrisonPeteUtils.PRISON_ZONE)) {
            return true
        }

        applyPenalty(entity)
        return true
    }

    override fun save(root: JsonObject, entity: Entity) {
        root.addProperty("ticksLeft", nextExecution - getWorldTicks())
    }

    override fun parse(root: JsonObject, entity: Entity) {
        val ticksRemaining = root.get("ticksLeft")?.asInt ?: DURATION
        nextExecution = getWorldTicks() + ticksRemaining
    }

    private fun applyPenalty(player: Player) {
        sendMessage(player, "Your time in prison leaves you strangely weakened.")

        for (i in 0 until 24) {
            drainStatLevel(player, i, 1.0, 1.0)
        }
    }

    companion object {
        const val DURATION = 144000 // 24h

        fun start(player: Player) {
            removeTimer<PrisonTimer>(player)
            registerTimer(player, PrisonTimer())
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