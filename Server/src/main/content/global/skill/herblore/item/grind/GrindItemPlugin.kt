package content.global.skill.herblore.item.grind

import core.api.amountInInventory
import core.game.dialogue.SkillDialogueHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.Item
import shared.consts.Items

class GrindItemPlugin : InteractionListener {

    override fun defineListeners() {
        onUseWith(IntType.ITEM, GRIND_ITEM_IDS, Items.PESTLE_AND_MORTAR_233) { player, used, _ ->
            val grind = GrindableItem.forID(used.id) ?: return@onUseWith true
            val max = amountInInventory(player, used.id)

            if (max <= 1) {
                GrindItemTask(player, grind, 1).start()
                return@onUseWith true
            }

            object : SkillDialogueHandler(
                player,
                SkillDialogue.ONE_OPTION,
                Item(grind.product)
            ) {

                override fun create(amount: Int, index: Int) {
                    GrindItemTask(player, grind, amount).start()
                }

                override fun getAll(index: Int): Int {
                    return max
                }
            }.open()

            return@onUseWith true
        }
    }

    companion object {
        val GRIND_ITEM_IDS = GrindableItem.values().flatMap { it.items }.toIntArray()
    }
}