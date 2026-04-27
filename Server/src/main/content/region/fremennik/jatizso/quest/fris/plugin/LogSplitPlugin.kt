package content.region.fremennik.jatizso.quest.fris.plugin

import core.api.amountInInventory
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import shared.consts.Items
import shared.consts.Scenery

class LogSplitPlugin : InteractionListener {

    override fun defineListeners() {

        on(Scenery.WOODCUTTING_STUMP_21305, IntType.SCENERY, "cut-wood") { player, _ ->
            LogCuttingTask(player, amountInInventory(player, ARCTIC_PINE_LOG)).start()
            return@on true
        }

        onUseWith(IntType.SCENERY, ARCTIC_PINE_LOG, Scenery.WOODCUTTING_STUMP_21305) { player, _, _ ->
            LogCuttingTask(player, amountInInventory(player, ARCTIC_PINE_LOG)).start()
            return@onUseWith true
        }
    }

    companion object {
        const val ARCTIC_PINE_LOG = Items.ARCTIC_PINE_LOGS_10810
    }
}