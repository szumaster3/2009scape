package content.region.other.dorgeshuun.dialogue

import core.api.addItem
import core.api.removeItem
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.Items
import shared.consts.NPCs

/**
 * Represents the Markog dialogue.
 */
@Initializable
class MarkogDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as core.game.node.entity.npc.NPC
        npc(FaceAnim.HAPPY, "Frogspawn gumbo! Lovely, chewy and wet. Only 10gp a bowl. Do you want some, surface-dweller?")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> showTopics(
                Topic("Yes, please.", 1, false),
                Topic("No, thanks.", 2, false)
            )
            1 -> {
                if (!removeItem(player!!, Item(Items.COINS_995, 10))) {
                    addItem(player, Items.FROGSPAWN_GUMBO_10961, 1)
                    npc(FaceAnim.OLD_DEFAULT, "There you go.")
                } else {
                    player(FaceAnim.SAD, "I don't have enough coins.")
                }
                stage = END_DIALOGUE
            }
            2 -> {
                player(FaceAnim.NEUTRAL, "No, thanks.")
                stage = END_DIALOGUE
            }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = MarkogDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.MARKOG_5793)
}