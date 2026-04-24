package content.global.random.event.prison

import core.api.sendModelOnInterface
import core.api.setAttribute
import core.game.interaction.InterfaceListener
import shared.consts.Components

/**
 * Represents the Prison Pete random event interface.
 */
class PrisonPeteInterface : InterfaceListener {
    override fun defineInterfaceListeners() {
        /*
         * Handles send random balloon NPC model.
         */

        onOpen(Components.MACRO_PRISON_PETE_273) { player, _ ->
            val model = PrisonPeteUtils.modelList.random()
            val npcId = PrisonPeteUtils.MODEL_TO_NPC[model] ?: -1
            val rot = (4..16).random()
            sendModelOnInterface(player, Components.MACRO_PRISON_PETE_273, 3, model)
            player.packetDispatch.sendInterfaceAnimateRotation(Components.MACRO_PRISON_PETE_273, 3, rot, rot)
            setAttribute(player, PrisonPeteUtils.EXPECTED_NPC, npcId)
            return@onOpen true
        }
    }
}