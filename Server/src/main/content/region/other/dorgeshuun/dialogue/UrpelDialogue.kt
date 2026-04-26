package content.region.other.dorgeshuun.dialogue

import core.api.sendNPCDialogue
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Ur-Pel and Ur-Lun (conversation) dialogue.
 */
@Initializable
class UrpelDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        sendNPCDialogue(player, NPCs.UR_LUN_5774, "At the next council meeting, I'm going to recommend that we change the guards uniforms to be red.", FaceAnim.OLD_NORMAL)
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> sendNPCDialogue(player, NPCs.UR_PEL_5775, "Why?", FaceAnim.OLD_NORMAL).also { stage++ }
            1 -> sendNPCDialogue(player, NPCs.UR_LUN_5774, "We have surface-dwellers coming down into our mines and our city now, and remember that they don't have very good eyesight. I worry that they can't see our guards very clearly in the dark. It is important for a guard to be seen.", FaceAnim.OLD_NORMAL).also { stage++ }
            2 -> sendNPCDialogue(player, NPCs.UR_PEL_5775, "You have a point. But I don't think red would be the best colour. It's the colour of blood, so it might give the wrong impression.", FaceAnim.OLD_NORMAL).also { stage++ }
            3 -> sendNPCDialogue(player, NPCs.UR_LUN_5774, "What colour do you suggest, then?", FaceAnim.OLD_NORMAL).also { stage++ }
            4 -> sendNPCDialogue(player, NPCs.UR_PEL_5775, "I suggest green. I have heard that the plants that grow on the surface are mostly green, so surface-dwellers might find that colour soothing.", FaceAnim.OLD_NORMAL).also { stage++ }
            5 -> sendNPCDialogue(player, NPCs.UR_LUN_5774, "No, I disagree. Guards should not be soothing, they should be seen as formidable. Red will give the correct impression.", FaceAnim.OLD_NORMAL).also { stage++ }
            6 -> sendNPCDialogue(player, NPCs.UR_PEL_5775, "Oh, hello surface-dweller. We were just discussing some council business.", FaceAnim.OLD_NORMAL).also { stage++ }
            7 -> showTopics(
                Topic("Red.", 8, false),
                Topic("Green.", 9, false),
                Topic("I think the guards are fine as they are.", 10, false),
                Topic("I think I've been here before...", 11, false)
            )
            8  -> sendNPCDialogue(player, NPCs.UR_LUN_5774, "Thank you, surface-dweller. That's what I'll be recommending in the next meeting.", FaceAnim.OLD_NORMAL).also { stage = END_DIALOGUE }
            9  -> sendNPCDialogue(player, NPCs.UR_PEL_5775, "Thank you, surface-dweller. That's what I'll be recommending in the next meeting.", FaceAnim.OLD_NORMAL).also { stage = END_DIALOGUE }
            10 -> sendNPCDialogue(player, NPCs.UR_LUN_5774, "Perhaps you're right. Not making any change would certainly be the easiest option!", FaceAnim.OLD_NORMAL).also { stage = END_DIALOGUE }
            11 -> sendNPCDialogue(player, NPCs.UR_LUN_5774, "Oh? How so?", FaceAnim.OLD_NORMAL).also { stage++ }
            12 -> playerl(FaceAnim.HALF_ASKING,"This is a lot like a conversation I had with some goblins on the surface.",).also { stage++ }
            13 -> sendNPCDialogue(player, NPCs.UR_PEL_5775, "Really? Which armour colour did they pick?", FaceAnim.OLD_NORMAL).also { stage++ }
            14 -> playerl(FaceAnim.HALF_ASKING,"They ended up with the same armour colour that they started with!").also { stage++ }
            15 -> sendNPCDialogue(player, NPCs.UR_LUN_5774, "You know, I think that's probably what we'll end up doing.", FaceAnim.OLD_NORMAL).also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = UrpelDialogue(player)

    override fun getIds(): IntArray = intArrayOf(
        NPCs.UR_LUN_5774,
        NPCs.UR_PEL_5775
    )
}