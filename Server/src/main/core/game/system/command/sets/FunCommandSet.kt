package core.game.system.command.sets

import com.google.gson.Gson
import com.google.gson.JsonObject
import content.global.plugins.item.SpadeDigUtils
import content.region.other.tutorial_island.plugin.CharacterDesign
import core.api.*
import core.game.dialogue.DialogueFile
import core.game.node.entity.combat.ImpactHandler
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.player.info.login.PlayerSaver
import core.game.system.command.Privilege
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.map.Location
import core.game.world.map.RegionManager
import core.game.world.repository.Repository.getPlayerByName
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.Graphics
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import core.tools.RandomFunction
import shared.consts.Animations
import shared.consts.Sounds
import java.awt.HeadlessException
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.stream.Collectors

@Initializable
class FunCommandSet : CommandSet(Privilege.ADMIN) {

    var npcs: List<NPC> = ArrayList()

    override fun defineCommands() {

        /*
         * Command for making the player use a bike animation.
         */

        define(
            name = "bike",
            privilege = Privilege.ADMIN,
            usage = "::bike",
            description = "Test a bike animation",
        ) { player, args ->
            sendChat(player, "Go, go, Gadget arms!")
            animate(player, Animations.USE_BIKE_MACHINE_2433)
        }

        /*
         * Command for making nearby NPCs perform an animation.
         */

        define(
            name = "npcanim",
            privilege = Privilege.ADMIN,
            usage = "::npcanim <lt>Animation ID<gt>",
        ) { player, args ->
            if (args.size < 2) {
                reject(player, "Syntax error: ::npcanim <Animation ID>")
            }
            npcs = RegionManager.getLocalNpcs(player.location, 10)
            for (n in npcs) {
                n.sendChat(args.slice(1 until args.size).joinToString(" "))
                n.lock(6)
                n.faceTemporary(player, 6)
                n.animator.animate(Animation(args[1].toInt()))
                n.animate(Animation.create(-1), 6)
            }
        }

        /*
         * Command for transforming the player into a given NPC.
         */

        define(
            name = "pnpc",
            privilege = Privilege.MODERATOR,
            usage = "::pnpc <lt>NPC ID<gt>",
            description = "Transforms the player into the given NPC.",
        ) { player, args ->
            if (args.size < 2) {
                reject(player, "Usage: ::pnpc <npcid>")
                return@define
            }

            val pnpc_id = args[1].toIntOrNull()
            if (pnpc_id == null) {
                reject(player, "<npcid> must be a valid integer.")
            }

            player.appearance.transformNPC(pnpc_id!!)
            notify(player, "Transformed into NPC $pnpc_id")
        }

        /*
         * Command for opening the player's bank.
         */

        define(
            name = "bank",
            privilege = Privilege.ADMIN,
            usage = "::bank",
            description = "Opens your bank."
        ) { player, _ ->
            player.bank.open()
        }

        /*
         * Command for toggling player invisibility.
         */

        define(
            name = "invis",
            privilege = Privilege.ADMIN,
            usage = "::invis",
            description = "Makes you invisible to others.",
        ) { player, _ ->
            player.isInvisible = !player.isInvisible
            notify(player, "You are now ${if (player.isInvisible) "invisible" else "visible"} to others.")
        }

        /*
         * Command for enabling/disabling 1-hit KO mode.
         */

        define(
            name = "1hit",
            privilege = Privilege.ADMIN,
            usage = "::1hit",
            description = "Makes you kill things in 1 hit.",
        ) { player, _ ->
            player.setAttribute("1hko", !player.getAttribute("1hko", false))
            notify(player, "1-hit KO mode " + if (player.getAttribute("1hko", false)) "on." else "off.")
        }

        /*
         * Command for toggling god mode (invulnerability).
         */

        define(
            name = "god",
            privilege = Privilege.ADMIN,
            usage = "::god",
            description = "Makes you invulnerable to damage.",
        ) { player, _ ->
            player.setAttribute("godMode", !player.getAttribute("godMode", false))
            notify(player, "God mode ${if (player.getAttribute("godMode", false)) "enabled." else "disabled."}")
        }

        /*
         * Command for enabling/disabling "Mr. Bones Wild Ride" mode on a player.
         */

        define(
            name = "mrboneswildride",
            privilege = Privilege.ADMIN,
            usage = "::mrboneswildride",
            description = "mrboneswildride",
        ) { player, args ->
            val p: Player =
                if (args.size > 2) {
                    reject(player, "Usage: ::mrboneswildride <username>")
                    return@define
                } else if (args.size == 1) {
                    player
                } else if (getPlayerByName(args[1]) == null) {
                    reject(player, "ERROR: Username not found. Usage: ::mrboneswildride <username>")
                    return@define
                } else {
                    getPlayerByName(args[1]) ?: return@define
                }
            val boneMode = !p.getAttribute("boneMode", false)
            p.setAttribute("boneMode", boneMode)
            notify(p, "Bone Mode ${if (boneMode) "<col=00ff00>ENGAGED</col>." else "<col=ff0000>POWERING DOWN</col>."}")
            p.appearance.rideCart(boneMode)
            if (p.appearance.isRidingMinecart) {
                var i = 0
                GameWorld.Pulser.submit(
                    object : Pulse(1, player) {
                        override fun pulse(): Boolean {
                            if (i++ % 12 == 0) p.sendChat("I want to get off Mr. Bones Wild Ride.")
                            p.moveStep()
                            return !p.appearance.isRidingMinecart
                        }
                    },
                )
            }
        }

        /*
         * Command for opening the Character Design interface (makeover).
         */

        define(name = "makeover", privilege = Privilege.MODERATOR) { player, _ ->
            CharacterDesign.open(player)
        }

        /*
         * Command for dumping the player appearance and
         * equipment to the clipboard as JSON.
         */

        define(name = "dumpappearance", privilege = Privilege.MODERATOR) { player, _ ->
            val gson = Gson()
            val json = JsonObject().apply {
                PlayerSaver(player).saveAppearance(this)
                this.add("equipment", PlayerSaver(player).saveContainer(player.equipment))
            }

            val jsonString = gson.toJson(json)

            try {
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(StringSelection(jsonString), null)
                notify(player, "Appearance and equipment copied to clipboard.")
            } catch (e: HeadlessException) {
                reject(player, "NOTE: Paste will not be available due to remote server.")
            }
        }

        /*
         * Command for burying the player entire inventory
         * at their location.
         */

        define(
            "bury",
            privilege = Privilege.ADMIN,
            usage = "::bury",
            description = "Burying the entire inventory."
        ) { player, _ ->
            if (player.inventory.isEmpty) {
                reject(player, "You have no items to bury.")
            }

            player.dialogueInterpreter.open(
                object : DialogueFile() {
                    override fun handle(
                        componentID: Int,
                        buttonID: Int,
                    ) {
                        when (stage) {
                            0 ->
                                dialogue(
                                    "This will bury your whole inventory in this spot.",
                                    "Are you sure?",
                                ).also { stage++ }

                            1 -> options("Yes", "No").also { stage++ }
                            2 ->
                                when (buttonID) {
                                    1 -> bury(player).also { end() }
                                    2 -> stage = END_DIALOGUE
                                }
                        }
                    }
                },
            )
        }

        /*
         * Command for opening the Character Design interface.
         */

        define(
            name = "appearance",
            privilege = Privilege.ADMIN,
            usage = "::appearance",
            description = "Allows you to change your appearance.",
        ) { player, _ ->
            CharacterDesign.open(player)
        }

        /*
         * Command for casting a weak barrage spell
         * on nearby players without killing them.
         */

        define(
            name = "barrage",
            privilege = Privilege.ADMIN,
            usage = "::barrage radius ",
            description = "Cast a weak barrage on all nearby players. Will never kill players",
        ) { player, args ->
            if (args.size != 2) reject(player, "Usage: ::barrage radius[max = 50]")
            val radius = if (args[1].toInt() > 50) 50 else args[1].toInt()
            val nearbyPlayers =
                RegionManager
                    .getLocalPlayers(player, radius)
                    .stream()
                    .filter { p: Player -> p.username != player.username }
                    .collect(Collectors.toList())
            animate(player, 1978)
            playGlobalAudio(player.location, Sounds.ICE_CAST_171)
            for (p in nearbyPlayers) {
                playGlobalAudio(p.location, Sounds.ICE_BARRAGE_IMPACT_168, 20)
                val impactAmount = if (p.skills.lifepoints < 10) 0 else RandomFunction.getRandom(3)
                impact(p, impactAmount, ImpactHandler.HitsplatType.NORMAL)
                p.graphics(Graphics(369, 0))
            }
        }
    }

    fun bury(player: Player) {
        val loc = Location.create(player.location)
        val inv = player.inventory.toArray().filterNotNull()
        SpadeDigUtils.registerListener(player.location) { p ->
            for (item in inv) {
                addItemOrDrop(p, item.id, item.amount)
                sendMessage(p, "You dig and find ${if (item.amount > 1) "some" else "a"} ${item.name}")
            }
            sendNews("${player.username} has found the hidden treasure! Congratulations!!!")
            SpadeDigUtils.listeners.remove(loc)
        }
        player.inventory.clear()
        notify(player, "You have buried your loot at $loc")
    }
}
