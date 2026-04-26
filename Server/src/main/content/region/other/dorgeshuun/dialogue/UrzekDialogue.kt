package content.region.other.dorgeshuun.dialogue

import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Ur-Zek dialogue.
 */
@Initializable
class UrzekDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npc(FaceAnim.OLD_NORMAL, "I think that Ur-tag is trying to move too quickly. First, he opens the city to humans, and now he wants to invite these dwarves into the city. I think we should exercise more caution in dealing with surface-dwellers.")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> npcl(FaceAnim.OLD_NORMAL, "The dwarves are not surface dwellers. They live in an underground city, like us!").also { stage++ }
            1 -> npcl(FaceAnim.OLD_NORMAL, "Why does that mean we can trust them? Are you forgetting the machine that nearly destroyed us was made by dwarves?").also { stage++ }
            2 -> npcl(FaceAnim.OLD_NORMAL, "You shouldn't judge all dwarves because of that!").also { stage++ }
            3 -> npcl(FaceAnim.OLD_NORMAL, "I know that! And I know not all humans want to kill us, but I still never thought we should open the city.").also { stage++ }
            4 -> npcl(FaceAnim.OLD_NORMAL, "You can't deny it's done some good. Think of how happy everyone is trying surface foods in the market!").also { stage++ }
            5 -> npcl(FaceAnim.OLD_NORMAL, "I'm not denying that, I just don't think it's worth the risk. Our guards have their work cut out as it is, protecting us from renegade humans, without having to worry about dwarves as well.").also { stage++ }
            6 -> npcl(FaceAnim.OLD_NORMAL, "Please don't be offended by my friend's attitude, surface-dweller. I'm sure you understand why we need to be cautious.").also { stage++ }
            7 -> player(FaceAnim.CALM_TALK, "Of course.").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = UrpelDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.UR_ZEK_5770)
}