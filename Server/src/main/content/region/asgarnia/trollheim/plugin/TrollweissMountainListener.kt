package content.region.asgarnia.trollheim.plugin

import core.api.teleport
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.world.map.Location
import shared.consts.Scenery

class TrollweissMountainListener : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles tunnel entrance to Trollweiss (surface -> dungeon).
         */

        on(Scenery.TUNNEL_5013, IntType.SCENERY, "enter") { player, node ->
            teleport(player, Location(2796, 3719, 0))
            return@on true
        }

        /*
         * Handles tunnel exit (dungeon -> surface).
         */

        on(Scenery.TUNNEL_5012, IntType.SCENERY, "enter") { player, node ->
            teleport(player, Location(2799, 10134, 0))
            return@on true
        }

        /*
         * Handles cave exit near Trollheim summit.
         */

        on(Scenery.CAVE_EXIT_32743, IntType.SCENERY, "enter") { player, node ->
            teleport(player, Location(2822, 3744, 0))
            return@on true
        }

        /*
         * Handles cave entrance leading to Trollheim area.
         */

        on(Scenery.CAVE_ENTRANCE_5007, IntType.SCENERY, "enter") { player, node ->
            teleport(player, Location(2803, 10187, 0))
            return@on true
        }

        /*
         * Handles crevasse entrance near Trollweiss Mountain.
         */

        on(Scenery.CREVASSE_33185, IntType.SCENERY, "enter") { player, node ->
            teleport(player, Location(2778, 3869, 0))
            return@on true
        }

        /*
         * Handles tunnel passage deeper into the cave system.
         */

        on(Scenery.TUNNEL_5009, IntType.SCENERY, "enter") { player, node ->
            teleport(player, Location(2772, 10232, 0))
            return@on true
        }
    }
}