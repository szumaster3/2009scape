package content.global.plugins.iface

import content.global.skill.construction.items.PlankType
import content.region.misthalin.varrock.diary.VarrockAchievementDiary
import core.api.*
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.item.Item
import shared.consts.Components
import shared.consts.Items

class SawmillInterface : InterfaceListener
{

    override fun defineInterfaceListeners()
    {

        on(Components.POH_SAWMILL_403) { player, _, _, buttonID, _, _ ->
            val plank = when (buttonID)
            {
                in 102..107 -> PlankType.WOOD
                in 109..113 -> PlankType.OAK
                in 115..119 -> PlankType.TEAK
                in 121..125 -> PlankType.MAHOGANY
                             else -> return@on true
            }

            val fullIndex = when (plank)
            {
                PlankType.WOOD     -> 107
                PlankType.OAK      -> 113
                PlankType.TEAK     -> 119
                PlankType.MAHOGANY -> 125
            }

            val index = if (plank != PlankType.WOOD) {
                fullIndex - buttonID
            } else {
                fullIndex - buttonID - if (buttonID != 107) 1 else 0
            }

            val amount = when (index) {
                0 -> 1
                1 -> 5
                2 -> 10
                3 -> -1
                4 -> amountInInventory(player, plank.log)
                else -> return@on true
            }

            if (amount == -1) {
                sendInputDialogue(player, true, "Enter the amount:") { value ->
                    val input = value as? Int ?: return@sendInputDialogue
                    createPlank(player, plank, input)
                }
                return@on true
            }

            if (amount > 0) {
                createPlank(player, plank, amount)
            }

            return@on true
        }

    }

    private fun createPlank(player: Player, plank: PlankType, requestedAmount: Int)
    {
        closeInterface(player)
        val availableLogs = amountInInventory(player, plank.log)

        if (availableLogs <= 0)
        {
            sendMessage(player, "You are not carrying any logs to cut into planks.")
            return
        }

        val amount = requestedAmount.coerceAtMost(availableLogs)
        val cost = plank.price * amount

        if (!inInventory(player, Items.COINS_995, cost))
        {
            sendDialogue(player, "Sorry, I don't have enough coins to pay for that.")
            return
        }

        if(removeItem(player, Item(plank.log, amount)) && removeItem(player, Item(Items.COINS_995, cost)))
        {
            addItem(player, plank.plank, amount)
        }

        // Diaries.
        when {
            plank == PlankType.WOOD -> {
                finishDiaryTask(
                    player,
                    DiaryType.VARROCK,
                    0,
                    VarrockAchievementDiary.Companion.EasyTasks.MAKE_PLANK_SAWMILL
                )
                setVarbit(player, 3989, 1, true)
            }

            plank == PlankType.MAHOGANY && amount >= 20 -> {
                finishDiaryTask(
                    player,
                    DiaryType.VARROCK,
                    1,
                    VarrockAchievementDiary.Companion.MediumTasks.SAWMILL_BUY_20_MAHOGANY_PLANKS
                )
                setVarbit(player, 4021, 1, true)
            }
        }
    }

}