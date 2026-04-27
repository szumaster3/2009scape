package content.global.skill.firemaking

import content.region.kandarin.baxtorian.BarbarianTraining
import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.GroundItem
import core.game.node.item.Item
import shared.consts.Items

class FireMakingListener : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles lighting logs.
         */

        onUseWith(IntType.ITEM, Items.TINDERBOX_590, *LOG_IDS) { player, _, with ->
            submitIndividualPulse(
                player, FireMakingPlugin(
                    player, with.asItem(), null, FiremakingMode.STANDARD
                )
            )
            return@onUseWith true
        }

        /*
         * Handles lighting logs on the ground using a tinderbox.
         */

        onUseWith(IntType.GROUND_ITEM, Items.TINDERBOX_590, *LOG_IDS) { player, _, with ->
            submitIndividualPulse(
                player, FireMakingPlugin(
                    player, with.asItem(), with as GroundItem, FiremakingMode.STANDARD
                )
            )
            return@onUseWith true
        }

        /*
         * Handles combining logs with a gnomish firelighter to coat logs.
         */

        onUseWith(
            IntType.ITEM,
            Items.LOGS_1511,
            Items.RED_FIRELIGHTER_7329,
            Items.GREEN_FIRELIGHTER_7330,
            Items.BLUE_FIRELIGHTER_7331,
            Items.PURPLE_FIRELIGHTER_10326,
            Items.WHITE_FIRELIGHTER_10327
        ) { player, used, with ->

            val firelighter = GnomishFirelighter.forProduct(with.id) ?: return@onUseWith false

            if (with.id == firelighter.product || used.id == firelighter.base) {
                sendMessage(player, "You can't do that.")
                return@onUseWith true
            }

            if (!removeItem(player, Item(with.id, 1))) {
                sendMessage(player, "You don't have the required items in your inventory.")
                return@onUseWith true
            }

            addItem(player, firelighter.product, 1)
            val chemicalName = getItemName(firelighter.base).replaceFirst("firelighter", "chemicals").lowercase()
            sendMessage(player, "You coat the log with the $chemicalName.")
            return@onUseWith true
        }

        /*
         * Handles barbarian fm.
         */

        onUseWith(IntType.ITEM, BARB_TOOLS, *LOG_IDS) { player, used, with ->
            if (!player.savedData.activityData.barbarianFiremaking && getAttribute(player, BarbarianTraining.FM_START, false)) {
                sendDialogue(
                    player, "You must begin the relevant section of Otto Godblessed's barbarian training."
                )
                return@onUseWith false
            }

            checkRequirements(used.asItem())?.let {
                sendDialogue(player, it)
                return@onUseWith false
            }

            submitIndividualPulse(
                player, FireMakingPlugin(
                    player, with.asItem(), null, FiremakingMode.BARBARIAN
                )
            )
            return@onUseWith true
        }
    }

    private fun checkRequirements(item: Item): String? = when {
        item.id == Items.DARK_BOW_11235 || item.id == Items.DARK_BOW_13405 -> "The innate darkness of the bow sucks all the heat from your firemaking attempt. You realise that this type of bow is useless for firelighting."
        item.name.contains("CRYSTAL BOW", true) || item.name.contains("CRYSTAL SHIELD", true) -> "The bow resists all attempts to light the fire. It seems that the sentient tools of the elves don't approve of you burning down forests."
        item.id in listOf(Items.COMP_OGRE_BOW_4827, Items.OGRE_BOW_2883) -> "This bow is vast, clumsy and most of a tree. You realise that this type of bow is useless for firelighting."
        else -> null
    }

    companion object {
        val BARB_TOOLS = intArrayOf(
            Items.OGRE_BOW_2883,
            Items.COMP_OGRE_BOW_4827,
            Items.TRAINING_BOW_9705,
            Items.LONGBOW_839,
            Items.SHORTBOW_841,
            Items.OAK_SHORTBOW_843,
            Items.OAK_LONGBOW_845,
            Items.WILLOW_LONGBOW_847,
            Items.WILLOW_SHORTBOW_849,
            Items.MAPLE_LONGBOW_851,
            Items.MAPLE_SHORTBOW_853,
            Items.YEW_LONGBOW_855,
            Items.YEW_SHORTBOW_857,
            Items.MAGIC_LONGBOW_859,
            Items.MAGIC_SHORTBOW_861,
            Items.SEERCULL_6724,
            Items.DARK_BOW_11235,
            Items.DARK_BOW_13405
        )

        val LOG_IDS = intArrayOf(
            Items.LOGS_1511,
            Items.OAK_LOGS_1521,
            Items.WILLOW_LOGS_1519,
            Items.MAPLE_LOGS_1517,
            Items.YEW_LOGS_1515,
            Items.MAGIC_LOGS_1513,
            Items.ACHEY_TREE_LOGS_2862,
            Items.PYRE_LOGS_3438,
            Items.OAK_PYRE_LOGS_3440,
            Items.WILLOW_PYRE_LOGS_3442,
            Items.MAPLE_PYRE_LOGS_3444,
            Items.YEW_PYRE_LOGS_3446,
            Items.MAGIC_PYRE_LOGS_3448,
            Items.TEAK_PYRE_LOGS_6211,
            Items.MAHOGANY_PYRE_LOG_6213,
            Items.MAHOGANY_LOGS_6332,
            Items.TEAK_LOGS_6333,
            Items.RED_LOGS_7404,
            Items.GREEN_LOGS_7405,
            Items.BLUE_LOGS_7406,
            Items.PURPLE_LOGS_10329,
            Items.WHITE_LOGS_10328,
            Items.SCRAPEY_TREE_LOGS_8934,
            Items.DREAM_LOG_9067,
            Items.ARCTIC_PYRE_LOGS_10808,
            Items.ARCTIC_PINE_LOGS_10810,
            Items.SPLIT_LOG_10812,
            Items.WINDSWEPT_LOGS_11035,
            Items.EUCALYPTUS_LOGS_12581,
            Items.EUCALYPTUS_PYRE_LOGS_12583,
            Items.JOGRE_BONES_3125
        )
    }
}
