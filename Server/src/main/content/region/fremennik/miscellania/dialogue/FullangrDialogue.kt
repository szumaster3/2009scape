package content.region.fremennik.miscellania.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

class FullangrDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npc(FaceAnim.OLD_DEFAULT, "Good day, ${if(player!!.isMale) "sir" else "madam"}.").also { stage++ }
            1 -> options("What are you doing down here?", "Good day.").also { stage++ }
            2 -> when (buttonID) {
                1 -> player(FaceAnim.FRIENDLY, "What are you doing down here?").also { stage++ }
                2 -> player(FaceAnim.NEUTRAL, "Good day.").also { stage = END_DIALOGUE }
            }
            3 -> npc(FaceAnim.OLD_DEFAULT, "I'm working on the digging, of course.", "It's a small excavation, so only two of us", "can work on it at a time.").also { stage = END_DIALOGUE }
        }
    }
}