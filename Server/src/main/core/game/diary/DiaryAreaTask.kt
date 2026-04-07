package core.game.diary

import core.api.getVarbit
import core.api.inBorders
import core.api.setVarbit
import core.game.node.entity.player.Player
import core.game.world.map.zone.ZoneBorders

class DiaryAreaTask(
    val zoneBorders: ZoneBorders,
    val diaryLevel: DiaryLevel,
    val taskId: Int,
    val varbitId: Int,
    val requiredAmount: Int = 1,
    private val condition: ((Player) -> Boolean)? = null,
) {

    fun getProgress(player: Player): Int =
        getVarbit(player, varbitId)

    fun isCompleted(player: Player): Boolean =
        getProgress(player) >= requiredAmount

    fun whenSatisfied(player: Player, then: () -> Unit) {
        val inZone = inBorders(player, zoneBorders)
        val meetsCondition = condition?.invoke(player) ?: true

        if (!inZone || !meetsCondition) return
        if (isCompleted(player)) return

        val current = getProgress(player)
        val newValue = (current + 1).coerceAtMost(requiredAmount)

        setVarbit(player, varbitId, newValue, true)

        if (newValue >= requiredAmount) {
            then()
        }
    }
}