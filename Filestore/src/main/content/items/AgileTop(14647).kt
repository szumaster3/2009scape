package content.items

import com.alex.tools.ItemPacker

object `AgileTop(14647)` {

    fun add() {
        val copy = ItemPacker.create().startAt(14647)

        copy.addItems(
            {
                name = "Agile top"
                invModelId = 45475
                zoom2d = 1663
                xan2d = 593
                yan2d = 0
                zan2d = 0
                xOffset2d = 0
                yOffset2d = 1
                stackable = 0
                cost = 1
                membersOnly = true
                maleEquipModelId1 = 45476
                maleEquipModelId2 = 45478
                femaleEquipModelId1 = 45477
                femaleEquipModelId2 = 45479
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