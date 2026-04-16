package content.global.plugins.item

import core.api.Container
import core.api.removeItem
import core.api.sendItemDialogue
import core.api.utils.WeightBasedTable
import core.api.utils.WeightedItem
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.Item
import core.tools.StringUtils
import shared.consts.Items

class CasketOptionPlugin : InteractionListener {

    companion object {

        val loot = WeightBasedTable.create(
            WeightedItem(Items.COINS_995, 20, 640, 55.0, false),
            WeightedItem(Items.UNCUT_SAPPHIRE_1623, 1, 1, 32.0, false),
            WeightedItem(Items.UNCUT_EMERALD_1621, 1, 1, 16.0, false),
            WeightedItem(Items.UNCUT_RUBY_1619, 1, 1, 9.0, false),
            WeightedItem(Items.UNCUT_DIAMOND_1617, 1, 1, 2.0, false),
            WeightedItem(Items.COSMIC_TALISMAN_1454, 1, 1, 8.0, false),
            WeightedItem(Items.LOOP_HALF_OF_A_KEY_987, 1, 1, 1.0, false),
            WeightedItem(Items.TOOTH_HALF_OF_A_KEY_985, 1, 1, 1.0, false),
        )

        private val COIN_TIERS = mapOf(
            2 to Items.COINS_8890,
            3 to Items.COINS_8891,
            4 to Items.COINS_8892,
            5 to Items.COINS_8893,
            25 to Items.COINS_8894,
            100 to Items.COINS_8895,
            250 to Items.COINS_8896,
            1000 to Items.COINS_8897,
            10000 to Items.COINS_8898,
            Int.MAX_VALUE to Items.COINS_8899,
        )
    }

    override fun defineListeners() {

        /*
         * Handles opening the casket.
         */

        on(Items.CASKET_405, IntType.ITEM, "open") { player, node ->
            val casket = node.asItem()

            if (removeItem(player, casket, Container.INVENTORY)) {
                val finalLoot = loot.roll()
                finalLoot.forEach { player.inventory.add(it) }

                val reward = finalLoot[0]

                val dialogueItem = if (reward.id == Items.COINS_995) {
                    getCoinDialogueItem(reward.amount)
                } else {
                    reward
                }

                sendItemDialogue(
                    player,
                    dialogueItem,
                    buildMessage(reward)
                )
            }
            return@on true
        }
    }

    private fun getCoinDialogueItem(amount: Int): Int {
        return COIN_TIERS.entries.first { amount <= it.key }.value
    }

    private fun buildMessage(item: Item): String {
        val prefix = when {
            item.amount > 1 -> "some"
            StringUtils.isPlusN(item.name) -> "an"
            else -> "a"
        }

        return "You open the casket. Inside you find $prefix ${item.name.lowercase()}."
    }

}