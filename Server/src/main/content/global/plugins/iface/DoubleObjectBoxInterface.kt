package content.global.plugins.iface

import core.api.sendRepositionOnInterface
import core.game.interaction.InterfaceListener
import shared.consts.Components

class DoubleObjectBoxInterface : InterfaceListener {
    override fun defineInterfaceListeners() {
        onOpen(Components.DOUBLEOBJBOX_131) { player, _ ->
            sendRepositionOnInterface(player, Components.DOUBLEOBJBOX_131, 1, 96, 25)
            sendRepositionOnInterface(player, Components.DOUBLEOBJBOX_131, 3, 96, 98)
            return@onOpen true
        }
    }
}