package content.region.asgarnia.trollheim.plugin

import core.api.getRegionBorders
import core.api.sendMessage
import core.api.teleport
import core.game.activity.ActivityManager
import core.game.global.action.ClimbActionHandler
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.world.map.Location
import shared.consts.Scenery

class TrollheimListener : InteractionListener {

    private val arenaEntrances = intArrayOf(
        3672,
        Scenery.ARENA_EXIT_3785,
        Scenery.ARENA_EXIT_3786,
        Scenery.ARENA_ENTRANCE_3782,
        Scenery.ARENA_ENTRANCE_3783
    )

    override fun defineListeners() {

        /*
         * Handles climb from inner Trollheim cave to upper circle area.
         */

        on(Scenery.ROCKS_3723, IntType.SCENERY, "climb") { player, _ ->
            teleport(player, Location(2907, 10019, 0))
            return@on true
        }

        /*
         * Handles entrance to top cave on Trollheim mountain.
         */

        on(Scenery.CAVE_ENTRANCE_3759, IntType.SCENERY, "enter") { player, node ->
            teleport(player, Location(2893, 10074, 0))
            return@on true
        }

        /*
         * Handles "leave" option teleporting back down the mountain.
         */

        on(Scenery.EXIT_3774, IntType.SCENERY, "leave") { player, _ ->
            teleport(player, Location(2840, 3690, 0))
            return@on true
        }

        /*
         * Handles cave exit leading from troll cave to two possible surface spots.
         */

        on(Scenery.CAVE_EXIT_3758, IntType.SCENERY, "exit") { player, n ->
            val nLoc = Location(2906, 10036, 0)
            val destLoc = if (n.location == nLoc) Location(2908, 3654, 0) else Location(2904, 3643, 0)
            teleport(player, destLoc)
            return@on true
        }

        /*
         * Handles entrance near the golden apple area.
         */

        on(Scenery.CAVE_ENTRANCE_4499, IntType.SCENERY, "enter") { player, _ ->
            teleport(player, Location(2808, 10002, 0))
            return@on true
        }

        /*
         * Handles tunnel exit near golden apple spot.
         */

        on(Scenery.TUNNEL_4500, IntType.SCENERY, "enter") { player, _ ->
            teleport(player, Location(2796, 3615, 0))
            return@on true
        }

        /*
         * Handles entrance to the arena cave system.
         */

        on(Scenery.CAVE_ENTRANCE_3757, IntType.SCENERY, "enter") { player, n ->
            val nLoc = Location(2907, 3652, 0)
            val destLoc = if (n.location == nLoc) Location(2907, 10035, 0) else Location(2907, 10019, 0)
            teleport(player, destLoc)
            return@on true
        }

        /*
         * Handles cave entrance leading to stronghold / herb patch area.
         */

        on(Scenery.CAVE_ENTRANCE_3735, IntType.SCENERY, "enter") { player, _ ->
            teleport(player, Location(2269, 4752, 0))
            return@on true
        }

        /*
         * Handles all arena doors (entrance/exit and secret door).
         */

        on(arenaEntrances, IntType.SCENERY, "open") { player, node ->
            if (node.id == 3672)
                sendMessage(player, "You don't know how to open the secret door.")
            else
                DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
            return@on true
        }

        /*
         * Handles danger sign — starts the “trollheim-warning” cutscene.
         */

        on(Scenery.DANGER_SIGN_3742, IntType.SCENERY, "read") { player, _ ->
            ActivityManager.start(player, "trollheim-warning", false)
            return@on true
        }

        /*
         * Handles ladder to stronghold rooftop (climb-up).
         */

        on(Scenery.TROLL_LADDER_18834, IntType.SCENERY, "climb-up") { player, _ ->
            ClimbActionHandler.climb(
                player,
                ClimbActionHandler.CLIMB_UP,
                Location(2828, 3678),
                "You clamber onto the windswept roof of the Troll Stronghold."
            )
            return@on true
        }

        /*
         * Handles ladder back down into the Troll Stronghold.
         */

        on(Scenery.TROLL_LADDER_18833, IntType.SCENERY, "climb-down") { player, _ ->
            ClimbActionHandler.climb(
                player,
                ClimbActionHandler.CLIMB_DOWN,
                Location(2831, 10076, 2),
                "You clamber back inside the Troll Stronghold."
            )
            return@on true
        }

        /*
         * Handles stronghold entry leading into the herb patch area.
         */

        on(Scenery.STRONGHOLD_3771, IntType.SCENERY, "enter") { player, _ ->
            teleport(player, Location(2837, 10090, 2))
            return@on true
        }

        /*
         * Handles stronghold exit back to Trollheim or wilderness side.
         */

        on(Scenery.CAVE_EXIT_32738, IntType.SCENERY, "exit") { player, _ ->
            val location = if (!getRegionBorders(11677).insideBorder(player)) {
                Location.create(2858, 3577, 0)
            } else {
                Location.create(2893, 3671, 0)
            }
            teleport(player, location)
            return@on true
        }
    }

    override fun defineDestinationOverrides() {

        /*
         * Handles destination adjustment for entering top cave.
         */

        setDest(IntType.SCENERY, Scenery.CAVE_ENTRANCE_3759) { _, _ ->
            return@setDest Location.create(2893, 3671, 0)
        }
    }
}
