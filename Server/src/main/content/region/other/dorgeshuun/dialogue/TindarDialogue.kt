package content.region.other.dorgeshuun.dialogue

import core.api.*
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import core.tools.START_DIALOGUE
import shared.consts.Items
import shared.consts.NPCs

/**
 * Represents the Tindar dialogue.
 */
@Initializable
class TindarDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        val p = player ?: return false

        when (stage) {

            START_DIALOGUE -> {
                npcl(FaceAnim.OLD_NORMAL, "Creeespy frogs' legs! Get your creeeespy frogs' legs! You want some crispy frogs' legs? Just 10gp.")
                stage = 1
            }

            1 -> showTopics(
                Topic("Yes please.", 2, false),
                Topic("No thanks.", 3, false)
            )

            2 -> {
                if (freeSlots(p) == 0) {
                    npcl(FaceAnim.OLD_NORMAL, "Looks like your hands are full. Free up inventory space first.")
                    stage = END_DIALOGUE
                    return true
                }

                if (amountInInventory(p, Items.COINS_995) < 10) {
                    player(FaceAnim.NEUTRAL, "But I don't have enough money.")
                    stage = END_DIALOGUE
                    return true
                }

                if (removeItem(p, Item(Items.COINS_995, 10), Container.INVENTORY)) {
                    addItem(p, Items.COATED_FROGS_LEGS_10963)
                    npcl(FaceAnim.OLD_NORMAL, "There you go.")
                } else {
                    player(FaceAnim.SAD, "I don't have enough coins.")
                }
                stage = END_DIALOGUE
            }

            3 -> {
                npcl(FaceAnim.OLD_NORMAL, "Have a good day!")
                stage = END_DIALOGUE
            }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = TindarDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.TINDAR_5795)
}