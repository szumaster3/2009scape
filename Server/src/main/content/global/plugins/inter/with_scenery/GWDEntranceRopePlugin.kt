package content.global.plugins.inter.with_scenery

import core.api.removeItem
import core.api.setVarbit
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import shared.consts.Items
import shared.consts.Scenery
import shared.consts.Vars

class GWDEntranceRopePlugin : InteractionListener {
    override fun defineListeners() {

        /*
         * Handles using rope on gwd entrance.
         */

        onUseWith(IntType.SCENERY, Items.ROPE_954, Scenery.HOLE_26340) { player, used, _ ->
            if (!removeItem(player, used)) {
                return@onUseWith false
            }
            setVarbit(player, Vars.VARBIT_GWD_ROPE_ON_HOLE_3932, 1, true)
            return@onUseWith true
        }
    }
}
