package core.game.node.entity.combat.graves

import core.cache.def.impl.DataMap
import shared.consts.Quests

enum class GraveType(val id: Int, val text: String) {
    MEM_PLAQUE(0, "In memory of @name,<br>who died here."),
    FLAG(1, MEM_PLAQUE.text),
    SMALL_GS(2, "In loving memory of our dear friend @name,<br>who died in this place @mins ago."),
    ORNATE_GS(3, SMALL_GS.text),
    FONT_OF_LIFE(4, "In your travels,<br>pause awhile to remember @name,<br>who passed away at this spot."),
    STELE(5, FONT_OF_LIFE.text),
    SARA_SYMBOL(6, "@name,<br>an enlightened servant of Saradomin,<br>perished in this place."),
    ZAM_SYMBOL(7, "@name,<br>a most bloodthirsty follower of Zamorak,<br>perished in this place."),
    GUTH_SYMBOL(8, "@name,<br>who walked with the Balance of Guthix,<br>perished in this place."),
    BAND_SYMBOL(9, "@name,<br>a vicious warrior dedicated to Bandos,<br>perished in this place."),
    ARMA_SYMBOL(10, "@name,<br>a follower of the Law of Armadyl,<br>perished in this place."),
    ZARO_SYMBOL(11, "@name,<br>servant of the Unknown Power,<br>perished in this place."),
    ANGEL_DEATH(12, "Ye frail mortals who gaze upon this sight,<br>forget not the fate of @name, once mighty, now<br>surrendered to the inescapable grasp of destiny.<br><i>Requires cat in pace.</i>");

    private val npcMap = DataMap.get(1098)
    private val costMap = DataMap.get(1101)

    val npcId: Int
        get() = npcMap.getInt(id + 1)

    val cost: Int
        get() = costMap.getInt(id + 1)

    val displayName: String
        get() = DataMap.get(1099).getString(id + 1)

    val description: String
        get() = DataMap.get(1100).getString(id + 1)

    val durationMinutes: Int
        get() = when (id) {
            0, 1 -> 2
            2, 3 -> 2
            4, 5 -> 4
            6, 7, 8, 9, 10, 11 -> 4
            12 -> 5
            else -> 2
        }

    val isMembers: Boolean
        get() = id >= 4

    val requiredQuest: String?
        get() = when (this) {
            BAND_SYMBOL -> Quests.LAND_OF_THE_GOBLINS
            ARMA_SYMBOL -> Quests.TEMPLE_OF_IKOV
            ZARO_SYMBOL -> Quests.DESERT_TREASURE
            else -> null
        }

    companion object {
        val ids = values().flatMap { listOf(it.npcId, it.npcId + 1, it.npcId + 2) }.toIntArray()
    }
}