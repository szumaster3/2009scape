package content.global.skill.cooking.brewing

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.system.timer.PersistTimer
import core.tools.secondsToTicks

/**
 * The timer for regular brewing operations.
 * @author Bishop
 */
const val brewCycleTime = 6 /*hours*/ * 60 /*minutes per hour*/ * 100 /*ticks per minute*/

class BrewGrowth : PersistTimer(brewCycleTime, "cooking:brewing", isSoft = true) {
    private val fermentingVatMap = HashMap<Int, FermentingVat>()
    lateinit var player: Player

    override fun onRegister(entity: Entity) {
        player = (entity as? Player)!!
        for (vat in fermentingVatMap.values.toList()){
            catchUp(vat.nextBrew)
        }
    }

    override fun save(root: JsonObject, entity: Entity) {
        player = (entity as? Player)!!

        val vats = JsonArray()

        for ((key, vat) in fermentingVatMap.entries.toList()) {

            val isBrewingStage = vat.stage in listOf(
                BrewingStage.MIXED,
                BrewingStage.BREWING1,
                BrewingStage.BREWING2,
                BrewingStage.DONE,
                BrewingStage.MATURE,
                BrewingStage.BAD
            )

            if (isBrewingStage) {
                val v = JsonObject()

                v.addProperty("brew-ordinal", key)
                v.addProperty("brew-stuff", vat.theStuff)
                v.addProperty("brewing-nextBrew", vat.nextBrew)
                v.addProperty("brewing-stage", vat.stage.ordinal)

                // nullable enum
                if (vat.brewingItem != null) {
                    v.addProperty("brewing-item", vat.brewingItem?.ordinal)
                }

                vats.add(v)
            }
        }

        root.add("brewing", vats)
    }

    override fun parse(root: JsonObject, entity: Entity) {
        player = (entity as? Player)!!

        val data = root.getAsJsonArray("brewing") ?: return

        for (element in data) {
            val v = element.asJsonObject

            val vatOrdinal = v.get("brew-ordinal").asInt
            val vatStuff = v.get("brew-stuff").asBoolean
            val vatNextBrew = v.get("brewing-nextBrew").asLong
            val vatStage = BrewingStage.values()[v.get("brewing-stage").asInt]

            val vatItem = if (v.has("brewing-item") && !v.get("brewing-item").isJsonNull) {
                val idx = v.get("brewing-item").asInt
                BrewProduct.values().getOrNull(idx)
            } else null

            if (fermentingVatMap[vatOrdinal] == null) {
                val brewingVat = BrewingVat.values()[vatOrdinal]
                val fermentingVat = FermentingVat(player, brewingVat, vatStuff, vatNextBrew, vatStage, vatItem)
                fermentingVatMap[vatOrdinal] = fermentingVat
                fermentingVat.updateVat()
            }
        }
    }

    fun getVat(brewingVat: BrewingVat, addVat: Boolean = true) : FermentingVat {
        return fermentingVatMap[brewingVat.ordinal] ?: (FermentingVat(player, brewingVat).also { if (addVat) fermentingVatMap[brewingVat.ordinal] = it })
    }

    private fun catchUp(timeTillGrow: Long){
        if (timeTillGrow < System.currentTimeMillis()){
            val seconds = (System.currentTimeMillis() - timeTillGrow)/1000
            if (seconds > Int.MAX_VALUE){
                execute(1024)
            }
            else{
                val cyclesToGrow = secondsToTicks(seconds.toInt()) / brewCycleTime
                execute(cyclesToGrow)
            }
        }

    }

    override fun run(entity: Entity): Boolean {
        player = (entity as? Player)!!
        return execute(0)
    }

    private fun execute(count: Int) : Boolean {
        for (vat in fermentingVatMap.entries.toList()){
            if (vat.value.canBrew())
                for (i in 0..count){
                    val resp = vat.value.brew()
                    if (!resp){
                        break
                    }
                }
            else {
                fermentingVatMap.entries.removeIf { it.value.isVatEmpty() && it.value.isBarrelEmpty()}
            }
        }
        return fermentingVatMap.isNotEmpty()
    }

    fun getVats(): MutableCollection<FermentingVat>{
        return fermentingVatMap.values
    }
}