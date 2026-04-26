package content.global.plugins.iface.tab

import core.api.inEquipment
import core.api.sendMessage
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.link.emote.Emotes
import shared.consts.Components
import shared.consts.Items

/**
 * Handles the emote tab interface.
 * @author Vexia
 */
class EmoteTabInterface : InterfaceListener {
    override fun defineInterfaceListeners() {
        on(Components.EMOTES_464) { player, _, _, buttonID, _, _ ->
            if (inEquipment(player, Items.SLED_4084)) {
                sendMessage(player, "You can't do that on a sled.")
            } else {
                Emotes.handle(player, buttonID)
            }
            true
        }
    }
}
