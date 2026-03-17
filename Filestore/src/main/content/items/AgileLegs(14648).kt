package content.items

import com.alex.tools.ItemPacker

object `AgileLegs(14648)` {
    fun add() {
        val copy = ItemPacker.create().startAt(14648)

        copy.addItems(
            {
                name = "Agile legs"
                invModelId = 45472
                zoom2d = 1979
                xan2d = 458
                yan2d = 0
                zan2d = 0
                xOffset2d = 0
                yOffset2d = 4
                stackable = 0
                cost = 1
                membersOnly = true
                maleEquipModelId1 = 455473
                femaleEquipModelId1 = 45474
                unnoted = false
                teamId = 0
                groundOptions = arrayOfNulls<String>(5).apply { this[2] = "Take" }
                inventoryOptions = arrayOfNulls<String>(5).apply {
                    this[1] = "Wear"
                    this[4] = "Destroy"
                }
            }
        )
        copy.save()
    }
}