package core.game.node.entity.player.link.warning

import core.api.*
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.world.map.Location
import shared.consts.NPCs
import shared.consts.Quests
import shared.consts.Scenery
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WarningListener : InteractionListener, InterfaceListener {

    override fun defineListeners() {
        on(Scenery.PASSAGE_37929, IntType.SCENERY, "go-through") { player, _ ->
            val offset = if (player.location.x > 2917) -4 else 4
            player.properties.teleportLocation = player.location.transform(offset, 0, 0)
            return@on true
        }

        on(Scenery.PASSAGE_38811, IntType.SCENERY, "go-through") { player, _ ->
            WarningManager.trigger(player, WarningType.CORPOREAL_BEAST_DANGEROUS) {
                val offset = if (player.location.x > 2970) -4 else 4
                player.properties.teleportLocation =
                    player.location.transform(offset, 0, 0)
            }
            return@on true
        }

        on(Scenery.HOLE_6905, IntType.SCENERY, "squeeze-through") { player, _ ->
            WarningManager.trigger(player, WarningType.LUMBRIDGE_CELLAR) {
                val targetLocation: Location
                val direction: core.game.world.map.Direction

                if (player.location.x >= 3221) {
                    targetLocation = Location.create(3219, 9618, 0)
                    direction = core.game.world.map.Direction.WEST
                } else {
                    targetLocation = Location.create(3222, 9618, 0)
                    direction = core.game.world.map.Direction.EAST
                }

                sendMessage(player, "You squeeze through the hole.")
                forceMove(player, player.location, targetLocation, 0, 90, direction, 10578)
            }
            return@on true
        }

        on(Scenery.GATE_3506, IntType.SCENERY, "go-through") { player, node ->
            val scenery = node.asScenery()
            if (player.location.y < 3458) {
                DoorActionHandler.handleAutowalkDoor(player, scenery)
                if (player.location.y == 3457) {
                    GlobalScope.launch {
                        findLocalNPC(player, NPCs.ULIZIUS_1054)
                            ?.sendChat("Oh my! You're still alive!", 2)
                    }
                } else if (!player.questRepository.hasStarted(Quests.NATURE_SPIRIT)) {
                    sendNPCDialogue(
                        player,
                        NPCs.ULIZIUS_1054,
                        "I'm sorry, but I'm afraid it's too dangerous to let you through this gate right now."
                    )
                }
            }

            return@on true
        }

        on(Scenery.GATE_3507, IntType.SCENERY, "go-through") { player, node ->
            val scenery = node.asScenery()

            if (player.location.y < 3458) {
                DoorActionHandler.handleAutowalkDoor(player, scenery)
            }

            return@on true
        }

        setDest(IntType.SCENERY, Scenery.HOLE_6905) { p, _ ->
            if (p.location.x < 3221)
                Location.create(3219, 9618, 0)
            else
                Location.create(3222, 9618, 0)
        }
    }

    override fun defineInterfaceListeners() {
        WarningType.values.forEach { warning ->
            if (warning.component != -1) {
                on(warning.component) { player, _, _, buttonId, _, _ ->
                    WarningManager.handleButton(player, warning, buttonId)
                    return@on true
                }
            }
        }
    }
}