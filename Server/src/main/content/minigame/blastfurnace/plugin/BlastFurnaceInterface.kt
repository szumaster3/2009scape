package content.minigame.blastfurnace.plugin

import content.global.skill.smithing.bar.BarItem
import core.api.animateInterface
import core.api.setComponentVisibility
import core.api.submitIndividualPulse
import core.game.interaction.InterfaceListener
import core.game.system.task.Pulse
import shared.consts.Components

class BlastFurnaceInterface : InterfaceListener {
    override fun defineInterfaceListeners() {
        onOpen(Components.BLAST_FURNACE_BAR_STOCK_28) { player, _ ->
            val state = BlastFurnace.getPlayerState(player)
            state.setBarClaimVarbits()
            state.checkBars()
            return@onOpen true
        }

        onOpen(Components.BLAST_FURNACE_TEMP_GAUGE_30) { player, _ ->
            setComponentVisibility(player, Components.BLAST_FURNACE_TEMP_GAUGE_30, 53, false)
            submitIndividualPulse(
                player,
                object : Pulse() {
                    override fun pulse(): Boolean {
                        val anim = BlastFurnace.state.furnaceTemp + 2452
                        animateInterface(player, 30, 4, anim)
                        return false
                    }
                },
            )
            return@onOpen true
        }

        onClose(Components.BLAST_FURNACE_TEMP_GAUGE_30) { player, _ ->
            player.pulseManager.clear()
            return@onClose true
        }

        on(Components.BLAST_FURNACE_BAR_STOCK_28) { player, _, _, buttonID, _, _ ->
            val (isAll, bar) = getBarForButton(buttonID)
            val state = BlastFurnace.getPlayerState(player)
            state.claimBars(bar, if (isAll) state.container.getBarAmount(bar) else 1)
            return@on true
        }
    }

    private fun getBarForButton(id: Int): Pair<Boolean, BarItem> =
        when (id) {
            43, 44 -> Pair(id == 44, BarItem.BRONZE)
            40, 41 -> Pair(id == 41, BarItem.IRON)
            36, 38 -> Pair(id == 38, BarItem.STEEL)
            33, 35 -> Pair(id == 35, BarItem.MITHRIL)
            30, 32 -> Pair(id == 32, BarItem.ADAMANT)
            27, 29 -> Pair(id == 29, BarItem.RUNITE)
            24, 26 -> Pair(id == 26, BarItem.SILVER)
            21, 23 -> Pair(id == 23, BarItem.GOLD)
            2, 4 -> Pair(id == 4, BarItem.PERFECT_GOLD)

            else -> Pair(false, BarItem.BRONZE)
        }
}
