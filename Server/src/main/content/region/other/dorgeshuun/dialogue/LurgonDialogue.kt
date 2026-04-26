package content.region.other.dorgeshuun.dialogue

import content.data.GameAttributes
import core.api.getAttribute
import core.api.openNpcShop
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Lurgon dialogue.
 */
@Initializable
class LurgonDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npc(FaceAnim.OLD_NORMAL, "Welcome, surface-dweller, to the Dorgesh-Kaan marketplace! Are you here to sell your exotic surface foods?")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> showTopics(
                Topic("Yes.", 1, false),
                Topic("How can I sell food?", 2, false),
                Topic("Are you selling anything?", 9, true),
                Topic("Goodbye", END_DIALOGUE, false)
            )
            1 -> {
                if (getAttribute(player, GameAttributes.COUNCIL_PERMISSION, false)) {
                    npcl(FaceAnim.OLD_NORMAL,"You'll need to get permission from the council before you can sell food in the market. The councillor in charge of giving permission at the moment is Ur-Tag.")
                } else {
                    npcl(FaceAnim.OLD_NORMAL, "Excellent. I see you already have permission from the council.")
                }
                stage = END_DIALOGUE
            }
            2 -> npcl(FaceAnim.OLD_NORMAL, "Most of the people wandering around the market want to buy food. Since the passage to the surface opened up there's been a craze in exotic food from up there.").also { stage++ }
            3 -> npcl(FaceAnim.OLD_NORMAL, "To sell something, simply use a piece of food on one of the gourmets in the market, then name your price, and the gourmet will tell you how many they are interested in buying for that price.").also { stage++ }
            4 -> npcl(FaceAnim.OLD_NORMAL, "If you want a rough idea of how much something will sell for, show it to me and I'll try to estimate.").also { stage++ }
            5 -> npcl(FaceAnim.OLD_NORMAL, "But you should also take into account the gourmet's reactions when naming your price. If someone likes the look of a food, they will pay more for it.").also { stage++ }
            6 -> npcl(FaceAnim.OLD_NORMAL, "Also remember that the novelty of new foods quickly wears off. If people have been eating a lot of something lately, then they won't pay as much for it.").also { stage++ }
            7 -> npcl(FaceAnim.OLD_NORMAL, "If someone especially likes your food then you might even start a craze for it! If there's a food that's in fashion like that, then the gourmets will pay more for it.").also { stage++ }
            8 -> if (getAttribute(player, GameAttributes.COUNCIL_PERMISSION, false)) {
                npcl(FaceAnim.OLD_NORMAL, "I see you already have permission from the council, so you can start selling food whenever you like.").also { stage = END_DIALOGUE }
            } else {
                npcl(FaceAnim.OLD_NORMAL, "If you want to sell food in the market, you'll need to renew your permission from the council first. The councillor in charge of giving permission at the moment is Ur-Tag.").also { stage = END_DIALOGUE }
            }
            9 -> end().also { openNpcShop(player,NPCs.LURGON_5798) }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = LurgonDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.LURGON_5798)
}
