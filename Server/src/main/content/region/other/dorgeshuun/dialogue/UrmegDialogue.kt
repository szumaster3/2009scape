package content.region.other.dorgeshuun.dialogue

import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Ur-Meg dialogue.
 */
@Initializable
class UrmegDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npc(FaceAnim.OLD_SAD, "Oh dear...")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> player(FaceAnim.HALF_ASKING, "What's the matter?").also { stage++ }
            1 -> npc(FaceAnim.OLD_SAD, "I'm worried. I think the Council is neglecting a big issue. I tried to bring it up at the last meeting, but they didn't pay attention.").also { stage++ }
            2 -> player(FaceAnim.HALF_ASKING, "What issue?").also { stage++ }
            3 -> npc(FaceAnim.OLD_SAD, "I'm very happy for you humans to come down to the city, but what about...them?").also { stage++ }
            4 -> player(FaceAnim.HALF_ASKING, "What about who?").also { stage++ }
            5 -> npc(FaceAnim.OLD_DEFAULT, "Them! *whispers* The G-O-D-S.").also { stage++ }
            6 -> player(FaceAnim.HALF_ASKING, "The gods?").also { stage++ }
            7 -> npc(FaceAnim.OLD_DEFAULT, "Don't say it! What if they hear?").also { stage = 8 }
            8 -> showTopics(
                Topic("Why are you scared of the gods?", 10, false),
                Topic("You don't need to worry!", 20, false),
                Topic("Don't you want to worship the god of the goblins?", 30, false),
                Topic("Goodbye", END_DIALOGUE, false)
            )

            10 -> player(FaceAnim.HALF_ASKING, "Why are you scared of the gods?").also { stage++ }
            11 -> npc(FaceAnim.OLD_SAD, "Because they're so powerful, of course! In the time of the great war, they forced all the goblin tribes to fight one another. Our ancestors weren't powerful enough to stand up to them.").also { stage++ }
            12 -> npc(FaceAnim.OLD_SAD, "We've hidden down here for this long, but now the way to the surface is open, what if they find us again?").also { stage = 8 }

            20 -> player(FaceAnim.HALF_ASKING, "You don't need to worry!").also { stage++ }
            21 -> npc(FaceAnim.OLD_NORMAL, "I don't?").also { stage++ }
            22 -> player(FaceAnim.CALM_TALK, "The god wars are over! The gods don't intervene directly in the affairs of mortals any more.").also { stage++ }
            23 -> npc(FaceAnim.OLD_NORMAL, "Are you sure?").also { stage++ }
            24 -> player(FaceAnim.CALM_TALK, "Yes!").also { stage++ }
            25 -> npc(FaceAnim.OLD_SAD, "I do hope you're right! I still can't help being worried, though.").also { stage = 8 }

            30 -> player(FaceAnim.HALF_ASKING, "Don't you want to worship the god of the goblins?").also { stage++ }
            31 -> npc(FaceAnim.OLD_ANGRY1, "No! How could you even suggest that?").also { stage++ }
            32 -> npc(FaceAnim.OLD_ANGRY1, "We're not going to worship some supernatural being just because it's bigger and more powerful than us.").also { stage++ }
            33 -> npc(FaceAnim.OLD_SAD, "It may seem to you that we're cowardly, hiding here from the gods all this time, but I know that every Dorgeshuun would rather die than submit to the rule of any god.").also { stage = 8 }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = UrmegDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.UR_MEG_5773)
}