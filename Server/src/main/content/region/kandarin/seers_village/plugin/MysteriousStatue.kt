package content.region.kandarin.seers_village.plugin

import core.api.*
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.DiaryType
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import core.tools.Vector3d
import kotlin.math.abs
import kotlin.math.sign

/**
 * Handles the logic for the Mysterious Statue area in Seers' Village.
 */
class MysteriousStatue : MapArea {

    private val origin = Vector3d(2740.5, 3490.5, 0.0)
    private val axis = Vector3d(0.0, 0.0, 1.0)

    private companion object {
        const val ATTR_START = "diary:seers:statue-start"
        const val ATTR_LAST = "diary:seers:statue-last"
        const val ATTR_ROTATION = "diary:seers:statue-rotation"
        const val ATTR_DIRECTION = "diary:seers:statue-direction"

        const val MIN_RADIUS = 1.2
        const val MAX_RADIUS = 2.5
        const val FINISH_EPSILON = 0.25
        const val FULL_CIRCLE = 360.0
    }

    override fun entityStep(entity: Entity, location: Location, lastLocation: Location) {
        val player = entity as? Player ?: return

        val current = Vector3d(location).sub(origin)

        val radius = Math.sqrt(
            current.x * current.x + current.y * current.y
        )

        // Ignore movement too close or too far from the statue.
        if (radius !in MIN_RADIUS..MAX_RADIUS) {
            return
        }


        var start = player.getAttribute<Vector3d>(ATTR_START)
        val last = player.getAttribute<Vector3d>(ATTR_LAST)
        var rotation = player.getAttribute<Double>(ATTR_ROTATION) ?: 0.0
        var direction = player.getAttribute<Double>(ATTR_DIRECTION)

        // Initialize run.
        if (start == null) {
            setAttribute(player, ATTR_START, current)
            setAttribute(player, ATTR_LAST, current)
            setAttribute(player, ATTR_ROTATION, 0.0)
            return
        }

        if (last != null) {
            val delta = Vector3d.signedAngle(last, current, axis)
            val deltaDeg = Math.toDegrees(delta)

            // Set direction on first valid movement.
            if (direction == null && abs(deltaDeg) > 0.5) {
                direction = sign(deltaDeg)
                setAttribute(player, ATTR_DIRECTION, direction)
            }

            // Accept rotation only in the chosen direction.
            if (direction != null && sign(deltaDeg) == direction) {
                rotation += abs(deltaDeg)
                setAttribute(player, ATTR_ROTATION, rotation)
            }
        }

        setAttribute(player, ATTR_LAST, current)

        // Check completion.
        if (
            rotation >= FULL_CIRCLE &&
            current.epsilonEquals(start, FINISH_EPSILON)
        ) {
            clearProgress(player)
            finishDiaryTask(player, DiaryType.SEERS_VILLAGE, 0, 1)
            setVarbit(player, 5783, 1, true)
        }
    }

    override fun areaLeave(entity: Entity, logout: Boolean) {
        if (entity is Player) {
            clearProgress(entity)
        }
    }

    override fun defineAreaBorders(): Array<ZoneBorders> =
        arrayOf(ZoneBorders(2739, 3489, 2742, 3492))

    /**
     * Clears all saved statue progress attributes.
     */
    private fun clearProgress(player: Player) {
        removeAttribute(player, ATTR_START)
        removeAttribute(player, ATTR_LAST)
        removeAttribute(player, ATTR_ROTATION)
        removeAttribute(player, ATTR_DIRECTION)
    }
}