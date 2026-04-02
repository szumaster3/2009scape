package content.global.skill.cooking

import core.api.*
import core.game.dialogue.DialogueFile
import core.game.node.scenery.Scenery
import core.tools.START_DIALOGUE
import shared.consts.Components
import shared.consts.Items

/**
 * Represents the cooking dialogue.
 */
class CookingDialogue(vararg val args: Any) : DialogueFile() {

    /**
     * Represents the raw item used for cooking.
     */
    var initial = 0

    /**
     * Represents the product item (e.g., cooked food or sinew).
     */
    var product = 0

    /**
     * Represents the scenery (object) used for cooking, like a fire or a stove.
     */
    var scenery: Scenery? = null

    /**
     * Whether the sinew drying option is enabled.
     */
    private var isSinew = false

    /**
     * Optional item ID, used when sinew cooking is triggered.
     */
    var itemId = 0

    /**
     * Handles interaction logic for the dialogue interface.
     *
     * @param componentID The id of the component.
     * @param buttonID The id of the button clicked by the player.
     */
    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            START_DIALOGUE -> {
                when (args.size) {
                    2 -> {
                        initial = args[0] as Int
                        if (CookableItems.intentionalBurn(initial)) {
                            // Intentional burning case
                            product = CookableItems.getIntentionalBurn(initial).id
                        } else {
                            product = CookableItems.forId(initial)!!.cooked
                        }
                        scenery = args[1] as Scenery
                    }

                    5 -> {
                        initial = args[0] as Int
                        product = args[1] as Int
                        isSinew = args[2] as Boolean
                        scenery = args[3] as Scenery
                        itemId = args[4] as Int
                        if (isSinew) {
                            options("Dry the meat into sinew", "Cook the meat")
                            stage = if (amountInInventory(player!!, initial) > 1) 100 else 101
                            return
                        }
                    }
                }
                display()
            }

            1 -> {
                end()
                when (val amount = getAmount(buttonID)) {
                    -1 -> sendInputDialogue(player!!, true, "Enter the amount:") { value ->
                        val count = if (value is String) value.toInt() else value as Int
                        CookingRewrite.cook(player!!, scenery, initial, product, count)
                    }

                    else -> {
                        end()
                        CookingRewrite.cook(player!!, scenery, initial, product, amount)
                    }
                }
            }

            100 -> {
                when (buttonID) {
                    1 -> {
                        product = Items.SINEW_9436
                        display()
                    }

                    2 -> {
                        product = CookableItems.forId(initial)!!.cooked
                        display()
                    }
                }
            }

            101 -> {
                when (buttonID) {
                    1 -> {
                        end()
                        CookingRewrite.cook(player!!, scenery, initial, Items.SINEW_9436, 1)
                    }

                    2 -> {
                        end()
                        CookingRewrite.cook(player!!, scenery, initial, CookableItems.forId(initial)!!.cooked, 1)
                    }
                }
            }
        }
    }

    /**
     * Maps the button id to the corresponding cooking amount.
     *
     * @param buttonId id of the button clicked.
     * @return Amount of items to cook or -1 if custom amount.
     */
    private fun getAmount(buttonId: Int): Int {
        return when (buttonId) {
            5 -> 1
            4 -> 5
            3 -> -1
            2 -> amountInInventory(player!!, initial)
            else -> -1
        }
    }

    /**
     * Displays the cooking interface, showing the item and repositioning components.
     */
    fun display() {
        val p = player ?: return

        sendRepositionOnInterface(p, Components.SKILL_COOKMANY_307, 2, 215, 27)
        sendItemZoomOnInterface(p, Components.SKILL_COOKMANY_307, 2, initial, 160)
        sendString(p, "<br><br><br><br>    ${getItemName(initial)}", Components.SKILL_COOKMANY_307, 6)

        val indices = intArrayOf(0, 1, 7, 3, 4, 5, 6)
        val xs = intArrayOf(12, 431, 0, 58, 58, 58, 58)
        val ys = intArrayOf(15, 15, 12, 27, 27, 27, 27)

        for (i in indices.indices) {
            sendRepositionOnInterface(p, Components.SKILL_COOKMANY_307, indices[i], xs[i], ys[i])
        }

        openChatbox(p, Components.SKILL_COOKMANY_307)
        stage = 1
    }
}
