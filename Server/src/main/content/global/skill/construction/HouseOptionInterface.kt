package content.global.skill.construction

import core.api.sendMessage
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.link.warning.WarningManager
import core.game.node.entity.player.link.warning.WarningType
import shared.consts.Components

/**
 * Handles the house options interface.
 */
class HouseOptionInterface : InterfaceListener {

    companion object {
        private const val BUILD_MODE_ON = 14
        private const val BUILD_MODE_OFF = 1
        private const val EXPEL_GUESTS = 15
        private const val LEAVE_HOUSE = 13
    }

    override fun defineInterfaceListeners() {
        on(Components.POH_HOUSE_OPTIONS_398) { player, _, _, buttonID, _, _ ->
            when (buttonID) {
                BUILD_MODE_ON -> {
                    WarningManager.trigger(player, WarningType.PLAYER_OWNED_HOUSES) {
                        if (player.houseManager.isInHouse(player)) {
                            player.houseManager.toggleBuildingMode(player, true)
                        } else {
                            player.houseManager.toggleBuildingMode(player, true)
                        }
                    }
                    return@on true
                }

                BUILD_MODE_OFF -> {
                    player.houseManager.toggleBuildingMode(player, false)
                    return@on true
                }

                EXPEL_GUESTS -> {
                    player.houseManager.expelGuests(player)
                    return@on true
                }

                LEAVE_HOUSE -> {
                    if (!player.houseManager.isInHouse(player)) {
                        sendMessage(player, "You can't do this outside of your house.")
                    } else {
                        HouseManager.leave(player)
                    }
                    return@on true
                }

                else -> return@on false
            }
        }
    }
}
