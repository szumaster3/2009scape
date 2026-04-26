package content.region.fremennik.miscellania.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import shared.consts.NPCs

class FerdDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.FERD_3937)
        when (stage) {
            0 -> npc(FaceAnim.OLD_DEFAULT, "Good day, sir.").also { stage++ }
            1 -> player(FaceAnim.THINKING, "What are you doing down here?.").also { stage++ }
            2 -> npc(FaceAnim.OLD_DEFAULT, "Shoring up the walls.").also { stage++ }
            3 -> player(FaceAnim.ASKING, "What does that do?").also { stage++ }
            4 -> npc(FaceAnim.OLD_DEFAULT, "Stops them falling down.").also { stage++ }
            5 -> player(FaceAnim.ASKING, "Oh, I see.").also { stage++ }
            6 -> npc(FaceAnim.OLD_NOT_INTERESTED, "Aye.", "If you want to chatter, you'd better talk to", "Thorodin over there. I'm working.").also { stage++ }
            7 -> player(FaceAnim.ASKING, "Okay then.").also { stage = END_DIALOGUE }
        }
    }
}
