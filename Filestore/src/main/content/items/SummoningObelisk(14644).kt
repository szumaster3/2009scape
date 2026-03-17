package content.items

import com.alex.tools.ItemPacker
import shared.consts.Items

object `SummoningObelisk(14644)` {

    fun add() {
        val copy = ItemPacker.create().startAt(14644)
        copy.addItems(
            {
                name = "Summoning obelisk"
                invModelId = 31686
                zoom2d = 5456
                xan2d = 210
                yan2d = 1880
                zan2d = 0
                xOffset2d = 0
                yOffset2d = -100
                stackable = 0
                cost = 1
                membersOnly = false
                dummyItem = 1
                unnoted = false
                teamId = 0
                inventoryOptions = arrayOfNulls<String>(5).apply {
                    this[4] = "Drop"
                }
                groundOptions = arrayOfNulls<String>(5).apply {
                    this[2] = "Take"
                }
                clientScriptData = HashMap<Any, Any>().apply {
                    put(211, "${Items.MARBLE_BLOCK_8786}")
                    put(212, "1")    // Amount
                    put(213, "${Items.SPIRIT_SHARDS_12183}")
                    put(214, "1000") // Amount
                    put(215, "${Items.CRIMSON_CHARM_12160}")
                    put(216, "10")   // Amount
                    put(217, "${Items.BLUE_CHARM_12163}")
                    put(218, "10")   // Amount

                }
            },
        )

        copy.save()
    }
}