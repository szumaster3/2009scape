package content.global.plugins.inter.with_scenery

import core.api.removeItem
import core.api.sendDialogue
import core.api.sendItemDialogue
import core.game.global.action.ClimbActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.link.warning.WarningManager
import core.game.node.entity.player.link.warning.WarningType
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import shared.consts.Animations
import shared.consts.Items
import shared.consts.Scenery

class SwampHoleRopePlugin : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles using rope on dark hole (Lumbridge swamp).
         */

        onUseWith(IntType.SCENERY, Items.ROPE_954, Scenery.DARK_HOLE_5947) { player, used, _ ->
            if (player.savedData.globalData.hasTiedLumbridgeRope()) {
                sendDialogue(player, "There is already a rope tied to the entrance.")
                return@onUseWith true
            }
            if (!removeItem(player, used)) {
                return@onUseWith false
            }
            sendItemDialogue(player, used, "You tie the rope to the top of the entrance and throw it down.")
            player.savedData.globalData.setLumbridgeRope(true)
            return@onUseWith true
        }

        on(Scenery.DARK_HOLE_5947, IntType.SCENERY, "climb-down") { player, node ->
            if (!player.getSavedData().globalData.hasTiedLumbridgeRope()) {
                sendDialogue(player, "There is a sheer drop below the hole. You will need a rope.")
            } else {
                WarningManager.trigger(player, WarningType.LUMBRIDGE_SWAMP_CAVE_ROPE) {
                    val insideCave = Location.create(3168, 9572, 0)
                    ClimbActionHandler.climb(player, ClimbActionHandler.CLIMB_DOWN, insideCave)
                }
            }

            return@on true
        }
    }
}
