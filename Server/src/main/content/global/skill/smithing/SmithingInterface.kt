package content.global.skill.smithing

import content.global.skill.smithing.bar.BarType
import content.global.skill.smithing.bar.BarItemProduct
import core.api.*
import core.game.interaction.InterfaceListener
import core.game.node.item.Item
import shared.consts.Components

class SmithingInterface : InterfaceListener {

    override fun defineInterfaceListeners() {
        on(Components.SMITHING_NEW_300) { player, _, _, buttonID, _, _ ->
            val barType = player.getAttribute<Any>("smith-type") as BarType

            val item = BarItemProduct.getItemId(buttonID, barType)
            val bar = BarItemProduct.forId(item) ?: return@on true

            val amount = SmithingType.forButton(player, bar, buttonID, bar.barType.barType)

            setAttribute(player, "smith-bar", bar)
            setAttribute(player, "smith-item", bar)

            if (amount == -1) {
                sendInputDialogue(player, true, "Enter the amount:") { value: Any ->
                    SmithingTask(player, Item(item, value as Int), bar, value).start()
                }
                return@on true
            }

            SmithingTask(player, Item(item, amount), bar, amount).start()
            return@on true
        }
    }

}