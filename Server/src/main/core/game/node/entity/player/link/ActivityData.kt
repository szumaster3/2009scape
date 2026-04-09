package core.game.node.entity.player.link

import com.google.gson.JsonObject
import kotlin.math.min

class ActivityData {
    // Points.
    var pestControlPoints: Int = 0
    var warriorGuildTokens: Int = 0
    var bountyHunterRate: Int = 0
    var bountyRogueRate: Int = 0

    // MA/MTA activity.
    var mageArenaStage: Int = 0
    var mageArenaBoss: Int = 0
    val godSpellCasts: IntArray = IntArray(3)
    var startMageTrainingArena: Boolean = false
    var unlockBoneToPeachSpell: Boolean = false

    // Barrows activity.
    var barrowsBrothers: BooleanArray = BooleanArray(6)
    var barrowsBrothersKillCount: Int = 0
    var barrowsTunnelIndex: Int = 0

    // Bork activity.
    var lastBorkActivity: Long = 0
    var borkKillCount: Byte = 0

    // Barbarian training.
    var barbarianFiremaking: Boolean = false
    var barbarianPyreBoat: Boolean = false
    var barbarianFishing: Boolean = false
    var barbarianBarehandFishing: Boolean = false
    var barbarianSpearSmithing: Boolean = false
    var barbarianHastaeSmithing: Boolean = false
    var barbarianHerblore: Boolean = false

    // Others.
    var elnockNPCSupplies: Boolean = false
    var lostDwarfMulticannon: Boolean = false
    var solvedMazes: Int = 0
    var fogRating: Int = 0
    var topGrabbed: Boolean = false

    fun parse(data: JsonObject?) {
        if (data == null) return

        pestControlPoints = data.int("pestControlPoints")
        warriorGuildTokens = data.int("warriorGuildTokens")
        bountyHunterRate = data.int("bountyHunterRate")
        bountyRogueRate = data.int("bountyRogueRate")
        barrowsBrothersKillCount = data.int("barrowsBrothersKillCount")

        data.arrayBool("barrowsBrothers", barrowsBrothers)
        barrowsTunnelIndex = data.int("barrowsTunnelIndex")
        mageArenaStage = data.int("mageArenaStage")

        data.arrayInt("godSpellCasts", godSpellCasts)

        mageArenaBoss = data.int("mageArenaBoss")

        elnockNPCSupplies = data.bool("elnockNPCSupplies")
        lastBorkActivity = data.long("lastBorkActivity")
        startMageTrainingArena = data.bool("startMageTrainingArena")
        lostDwarfMulticannon = data.bool("lostDwarfMulticannon")
        unlockBoneToPeachSpell = data.bool("unlockBoneToPeachSpell")

        solvedMazes = data.int("solvedMazes")
        fogRating = data.int("fogRating")
        borkKillCount = data.int("borkKillCount").toByte()

        topGrabbed = data.bool("topGrabbed")

        barbarianFiremaking = data.bool("barbarianFiremaking")
        barbarianPyreBoat = data.bool("barbarianPyreBoat")

        barbarianFishing = data.bool("barbarianFishing")
        barbarianBarehandFishing = data.bool("barbarianBarehandFishing")

        barbarianSpearSmithing = data.bool("barbarianSpearSmithing")
        barbarianHastaeSmithing = data.bool("barbarianHastaeSmithing")

        barbarianHerblore = data.bool("barbarianHerblore")
    }

    private fun JsonObject.int(key: String) =
        if (has(key) && !get(key).isJsonNull) get(key).asInt else 0

    private fun JsonObject.long(key: String) =
        if (has(key) && !get(key).isJsonNull) get(key).asLong else 0L

    private fun JsonObject.bool(key: String) =
        has(key) && !get(key).isJsonNull && get(key).asBoolean

    private fun JsonObject.arrayBool(key: String, target: BooleanArray) {
        val arr = getAsJsonArray(key) ?: return
        val len = min(arr.size(), target.size)
        for (i in 0 until len) {
            target[i] = arr[i].asBoolean
        }
    }

    private fun JsonObject.arrayInt(key: String, target: IntArray) {
        val arr = getAsJsonArray(key) ?: return
        val len = min(arr.size(), target.size)
        for (i in 0 until len) {
            target[i] = arr[i].asInt
        }
    }

    fun increasePestControlPoints(amount: Int) {
        pestControlPoints = (pestControlPoints + amount).coerceAtMost(500)
    }

    fun decreasePestControlPoints(amount: Int) {
        pestControlPoints = (pestControlPoints - amount).coerceAtLeast(0)
    }

    fun updateWarriorTokens(amount: Int) {
        warriorGuildTokens += amount
    }

    fun updateBountyHunterRate(rate: Int) {
        bountyHunterRate += rate
    }

    fun updateBountyRogueRate(rate: Int) {
        bountyRogueRate += rate
    }

    fun hasStartedMageArena() = mageArenaStage == 1
    fun hasCompletedMageArena() = mageArenaStage >= 2
    fun hasReceivedMageArenaReward() = mageArenaStage == 3

    fun hasKilledBork() = lastBorkActivity > 0

    fun hasCompleteBarbarianTraining(): Boolean =
        barbarianFiremaking &&
                barbarianPyreBoat &&
                barbarianFishing &&
                barbarianBarehandFishing &&
                barbarianSpearSmithing &&
                barbarianHastaeSmithing &&
                barbarianHerblore
}