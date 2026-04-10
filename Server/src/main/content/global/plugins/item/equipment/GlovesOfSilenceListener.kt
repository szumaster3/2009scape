package content.global.plugins.item.equipment

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.skill.Skills
import shared.consts.Items

class GlovesOfSilenceListener : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles repair the pair of gloves.
         */

        onUseWith(IntType.ITEM, Items.DARK_KEBBIT_FUR_10115, Items.GLOVES_OF_SILENCE_10075) { player, used, with ->
            val gloves = with.asItem() ?: return@onUseWith true
            val fur = used.asItem() ?: return@onUseWith true

            if (getStatLevel(player, Skills.CRAFTING) < 64)
            {
                sendMessage(player, "You need a Crafting level of 64 to repair these gloves.")
                return@onUseWith true
            }

            if(getCharge(gloves ) == 1000)
            {
                sendMessage(player, "These gloves are new.")
                return@onUseWith true
            }

            if (!allInInventory(player, Items.NEEDLE_1733, Items.THREAD_1734, Items.KNIFE_946, used.id, with.id)
            ) {
                sendMessage(player, "You need a needle, thread, knife and dark kebbit fur.")
                return@onUseWith true
            }

            val slot = gloves.slot
            setCharge(gloves, 1000)
            GlovesOfSilence.repair(gloves)

            removeItem(player, fur)
            removeItem(player, Items.THREAD_1734)
            removeItem(player, Items.NEEDLE_1733)

            sendMessages(player, "You carefully stitch the gloves back together.")
            return@onUseWith true
        }

        /*
         * Handles checking charges of them.
         */

        on(Items.GLOVES_OF_SILENCE_10075, IntType.ITEM, "operate") { p, node ->
            val durability = getCharge(node.asItem())
            val m = when(durability){
                 990 -> "These gloves are in good condition."
                 980 -> "These gloves are starting to look quite shabby."
                 970 -> "These gloves are starting to need repair."
                 960 -> "These gloves are in need of repair."
                 950 -> "These gloves are about to fall apart."
                else -> "These gloves are new."
            }
            sendMessage(p,m)
            return@on true

        }
    }
}