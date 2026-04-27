package content.global.skill.herblore

import content.global.skill.herblore.herbs.TarItem
import core.api.*
import core.game.dialogue.SkillDialogueHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.Item
import shared.consts.Items

class TarMakePlugin : InteractionListener {

    override fun defineListeners() {

        onUseWith(IntType.ITEM, tarIngredients, Items.SWAMP_TAR_1939) { player, used, _ ->
            val tar = TarItem.forId(used.id) ?: return@onUseWith true
            val max = amountInInventory(player, used.id)

            if (max == 1) {
                TarMakingTask(player, tar, 1).start()
                return@onUseWith true
            }

            val handler = object : SkillDialogueHandler(
                player,
                SkillDialogue.MAKE_SET_ONE_OPTION,
                Item(tar.product)
            ) {

                override fun create(amount: Int, index: Int) {
                    TarMakingTask(player, tar, amount).start()
                }

                override fun getAll(index: Int): Int {
                    return max
                }
            }

            handler.open()
            return@onUseWith true
        }
    }

    companion object {
        val tarIngredients = TarItem.values().map(TarItem::ingredient).toIntArray()
    }
}