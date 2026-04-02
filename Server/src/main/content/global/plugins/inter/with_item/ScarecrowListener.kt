package content.global.plugins.inter.with_item

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.skill.Skills
import shared.consts.Items

class ScarecrowListener : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles using hay sack with watermelon to creating scarecrow.
         */

        onUseWith(IntType.ITEM, Items.HAY_SACK_6058, Items.WATERMELON_5982) { player, _, _ ->
            if (getStatLevel(player, Skills.FARMING) >= 23) {
                if (removeItem(player, Items.HAY_SACK_6058, Container.INVENTORY) && removeItem(player, Items.WATERMELON_5982, Container.INVENTORY) && addItem(player, Items.SCARECROW_6059)) {
                    rewardXP(player, Skills.FARMING, 25.0)
                    sendMessages(
                        player,
                        "You stick a watermelon on top of the hay sack.",
                        "This would be ideal for scaring birds!",
                    )
                }
            } else {
                sendMessage(player, "Your Farming level is not high enough to do this")
            }
            return@onUseWith true
        }
    }
}
