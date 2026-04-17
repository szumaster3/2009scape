package content.global.random.event.prison_pete

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
            val model = PPUtils.modelList.random()
            val npcId = PPUtils.MODEL_TO_NPC[model] ?: -1
            sendModelOnInterface(player, Components.MACRO_PRISON_PETE_273, 3, model)
            setAttribute(player, PPUtils.EXPECTED_NPC, npcId)
            return@onOpen true
        }
    }
}

// 0 - cs
// 1 - model(prison_bars)
// 2 - UNKNOWN_1
// 3 - model(balloon)
// 6 - close_glow(sprite)
