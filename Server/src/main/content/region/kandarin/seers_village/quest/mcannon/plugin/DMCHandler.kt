package content.region.kandarin.seers_village.quest.mcannon.plugin

import core.api.*
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.combat.CombatSwingHandler
import core.game.node.entity.combat.ImpactHandler.HitsplatType
import core.game.node.entity.impl.Projectile
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.game.node.scenery.SceneryBuilder
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.map.Direction
import core.game.world.map.RegionManager
import core.game.world.map.zone.ZoneRestriction
import core.game.world.update.flag.context.Animation
import core.tools.RandomFunction
import org.jetbrains.annotations.NotNull
import shared.consts.Sounds

/**
 * Handles the functionality of the Dwarf Multi-cannon.
 */
class DMCHandler : LogoutListener {

    /**
     * The player who owns this cannon.
     */
    private val player: Player?

    /**
     * The cannon scenery object in the world.
     */
    var cannon: Scenery? = null

    /**
     * Number of cannonballs currently loaded in the cannon.
     */
    var cannonballs = 0

    /**
     * The current rotation direction of the cannon.
     */
    var direction = DMCRevolution.NORTH

    /**
     * Timer managing automatic firing of the cannon.
     */
    lateinit var timer: CannonTimer

    constructor() {
        this.player = null
    }

    /**
     * Creates a new DMCHandler for the specified player.
     *
     * @param player The player who will control the cannon.
     */
    constructor(player: Player) {
        this.player = player
    }

    /**
     * Rotates the cannon one step clockwise and attempts to fire at any NPCs in sight.
     *
     * @return `true` if the rotation was successful, `false` if out of ammo or invalid state.
     */
    fun rotate(): Boolean {
        val player = player ?: return false
        val cannon = cannon ?: return false

        if (cannonballs < 1) {
            player.packetDispatch.sendMessage("Your cannon has run out of ammo!")
            return false
        }

        player.packetDispatch.sendSceneryAnimation(cannon, Animation.create(direction.animationId))
        val base = cannon.location.transform(1, 1, 0)
        playGlobalAudio(base, Sounds.MCANNON_TURN_2877)

        direction = DMCRevolution.values()[(direction.ordinal + 1) % DMCRevolution.values().size]

        for (npc in RegionManager.getLocalNpcs(base, 10)) {
            if (!direction.isInSight(npc.location.x - base.x, npc.location.y - base.y)) continue
            if (!npc.isAttackable(player, CombatStyle.RANGE, false)) continue
            if (!CombatSwingHandler.isProjectileClipped(npc, base, false)) continue

            val speed = (25 + base.getDistance(npc.location) * 10).toInt()
            Projectile.create(base, npc.location, 53, 40, 36, 20, speed, 0, 128).send()
            playGlobalAudio(base, Sounds.MCANNON_FIRE_1667)
            cannonballs--

            var hit = 0
            if (player.getSwingHandler(false).isAccurateImpact(player, npc, CombatStyle.RANGE, 1.2, 1.0)) {
                hit = RandomFunction.getRandom(30)
            }

            player.skills.addExperience(Skills.RANGE, hit * 2.0)
            npc.impactHandler.manualHit(player, hit, HitsplatType.NORMAL, kotlin.math.ceil(base.getDistance(npc.location) * 0.3).toInt())
            npc.attack(player)
            break
        }
        return true
    }

    /**
     * Starts or stops the automatic firing of the cannon.
     *
     * Loads cannonballs from the player's inventory if needed, and sends messages about the action.
     */
    fun startFiring() {
        val player = player ?: return
        val cannon = cannon

        if (cannon == null || !cannon.isActive) {
            player.packetDispatch.sendMessage("You don't have a cannon active.")
            return
        }

        if (timer.isFiring) {
            timer.isFiring = false
            return
        }

        if (cannonballs < 1) {
            val amount = player.inventory.getAmount(Item(2))
            if (amount < 1) {
                player.packetDispatch.sendMessage("Your cannon is out of cannonballs.")
                return
            }

            var toUse = 30 - cannonballs
            if (toUse > amount) toUse = amount

            if (toUse > 0) {
                cannonballs = toUse
                player.packetDispatch.sendMessage("You load the cannon with $toUse cannonballs.")
                player.inventory.remove(Item(2, toUse))
            } else {
                player.sendMessage("Your cannon is already fully loaded.")
            }
        }

        timer.isFiring = true
    }

    /**
     * Explodes the cannon, either due to decay or destruction.
     *
     * @param decay If true, the cannon decayed; if false, it was destroyed.
     */
    fun explode(decay: Boolean) {
        val player = player ?: return
        val cannon = cannon ?: return
        if (!cannon.isActive) return

        player.sendMessage("Your cannon has ${if (decay) "decayed" else "been destroyed"}!")

        for (p in RegionManager.getLocalPlayers(player)) {
            p.packetDispatch.sendPositionedGraphic(189, 0, 1, cannon.location)
        }
        clear(false)
    }

    companion object {

        /**
         * Constructs and places the cannon for the player.
         *
         * Handles all setup stages, animations, and inventory removal of cannon parts.
         *
         * @param player The player placing the cannon.
         */
        @JvmStatic
        fun construct(player: Player) {
            val spawn = RegionManager.getSpawnLocation(player, Scenery(6, player.location))
                ?: run {
                    player.packetDispatch.sendMessage("There's not enough room for your cannon.")
                    return
                }

            if (player.zoneMonitor.isRestricted(ZoneRestriction.CANNON)) {
                player.packetDispatch.sendMessage("You can't set up a cannon here.")
                return
            }

            val handler = DMCHandler(player)
            setAttribute(player, "dmc", handler)

            player.pulseManager.clear()
            player.walkingQueue.reset()
            player.lock(9)
            player.faceLocation(spawn.transform(Direction.NORTH_EAST))

            GameWorld.Pulser.submit(object : Pulse(2, player) {
                var count = 0
                var scenery: Scenery? = null

                override fun pulse(): Boolean {
                    player.animate(Animation.create(827))

                    if (!player.inventory.remove(Item(6 + count * 2))) {
                        for (i in count - 1 downTo 0) {
                            player.inventory.add(Item(6 + i * 2))
                        }
                        scenery?.let { SceneryBuilder.remove(it) }
                        return true
                    }

                    when (count) {
                        0 -> {
                            scenery = SceneryBuilder.add(Scenery(7, spawn))
                            player.packetDispatch.sendMessage("You place the cannon base on the ground.")
                        }
                        1 -> player.packetDispatch.sendMessage("You add the stand.")
                        2 -> player.packetDispatch.sendMessage("You add the barrels.")
                        3 -> {
                            player.packetDispatch.sendMessage("You add the furnace.")
                            SceneryBuilder.remove(scenery)
                            handler.configure(SceneryBuilder.add(scenery!!.transform(6)))
                            handler.timer = spawnTimer("dmc:timer", handler) as CannonTimer
                            registerTimer(player, handler.timer)
                            return true
                        }
                    }

                    playGlobalAudio(player.location, Sounds.MCANNON_SETUP_2876)

                    if (count != 0) {
                        SceneryBuilder.remove(scenery)
                        scenery = SceneryBuilder.add(scenery!!.transform(scenery!!.id + 1))
                    }

                    return ++count == 4
                }
            })
        }
    }

    /**
     * Configures the cannon scenery object for this handler.
     *
     * @param cannon The cannon scenery.
     */
    fun configure(cannon: Scenery) {
        this.cannon = cannon
    }

    /**
     * Called when the player logs out.
     *
     * Clears the cannon if the player has an active DMCHandler.
     *
     * @param player The player logging out.
     */
    override fun logout(@NotNull player: Player) {
        val handler = player.getAttribute<DMCHandler>("dmc") ?: return
        handler.clear(false)
    }

    /**
     * Clears the cannon from the world and optionally returns parts to the player's inventory.
     *
     * @param pickup If true, cannon parts are returned to inventory; if false, cannon is lost.
     */
    fun clear(pickup: Boolean) {
        cannon?.let { SceneryBuilder.remove(it) }
        if (player != null) {
            removeAttribute(player, "dmc")
        }

        if (!pickup) {
            player?.savedData?.activityData?.lostDwarfMulticannon = true
            return
        }

        for (i in 0 until 4) {
            player?.inventory?.add(Item(12 - i * 2))
        }

        if (cannonballs > 0) {
            player?.inventory?.add(Item(2, cannonballs))
            cannonballs = 0
        }
    }
}