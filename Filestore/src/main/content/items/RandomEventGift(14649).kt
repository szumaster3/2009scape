package content.items

import com.alex.tools.ItemPacker

object `RandomEventGift(14649)` {
    fun add() {
        val copy = ItemPacker.create().startAt(14649)

        copy.addItems(
            {
                name = "Random event gift"
                invModelId = 2426
                zoom2d = 1180
                xan2d = 97
                yan2d = 1895
                zan2d = 0
                xOffset2d = 0
                yOffset2d = -8
                stackable = 0
                cost = 100
                membersOnly = false
                unnoted = false
                ambience = 5
                teamId = 0
                changeModelColor(22410,60325)
                inventoryOptions = arrayOfNulls<String>(5).apply {
                    this[0] = "Open"
                    this[4] = "Drop"
                }
                groundOptions = arrayOfNulls<String>(5).apply {
                    this[2] = "Take"
                }
                clientScriptData = HashMap<Any, Any>().apply { put(59, "1") }
            }
        )
        copy.save()
    }
}