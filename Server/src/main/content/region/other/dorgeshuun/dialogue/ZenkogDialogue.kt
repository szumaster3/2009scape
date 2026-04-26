package content.region.other.dorgeshuun.dialogue

import core.api.*
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import core.tools.START_DIALOGUE
import shared.consts.Items
import shared.consts.NPCs

/**
 * Represents the Zenkog dialogue.
 */
@Initializable
class ZenkogDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            START_DIALOGUE -> npcl(FaceAnim.OLD_NORMAL, "Wall beast fingers! How about a tasty snack of wall beast fingers?").also { stage++ }
            1 -> options("Yes please.", "No thanks.").also { stage++ }
            2 -> when (buttonId) {
                1 -> playerl(FaceAnim.FRIENDLY, "Yes please.").also { stage++ }
                2 -> playerl(FaceAnim.NEUTRAL, "No thanks.").also { stage = 4 }
            }
            3 -> {
                end()
                if (!removeItem(player!!, Item(Items.COINS_995, 10))) {
                    sendMessage(player!!, "You don't have enough coins.")
                } else {
                    addItem(player!!, Items.FINGERS_10965, 1)
                    npc(FaceAnim.OLD_DEFAULT, "There you go.")
                }
            }
            4 -> npcl(FaceAnim.OLD_NORMAL, "Have a good day!").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = ZenkogDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.ZENKOG_5797)
}
