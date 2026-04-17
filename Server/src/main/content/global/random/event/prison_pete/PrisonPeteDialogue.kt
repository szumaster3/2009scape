package content.global.random.event.prison_pete

import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.game.system.timer.impl.AntiMacro
import core.tools.END_DIALOGUE
import shared.consts.Animations
import shared.consts.Items
import shared.consts.NPCs

/**
 * Represents the Prison Pete random event dialogue.
 * @author szu
 */
class PrisonPeteDialogue(
    val dialOpt: Int,
) : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        val energyBarrier = getVarbit(player!!, 1547)
        npc = NPC(NPCs.PRISON_PETE_3118)
        when (dialOpt) {
            1 -> {
                when (stage) {
                    0 -> npc(FaceAnim.HAPPY, "Great, now you've got a key!", "Bring it to me so I can try it on the door.").also { stage++ }
                    1 -> end().also{
                        PrisonPeteUtils.getKey(player!!)
                    }
                }
            }

            2 -> {
                when (stage) {
                    0 -> {
                        findLocalNPC(player!!, npc!!.id)?.let {
                            face(it, player!!)
                            if (removeItem(player!!, Items.PRISON_KEY_6966))
                                npc(FaceAnim.NOD_YES, "Ooh, thanks! I'll see if it's the right one...")
                            openDialogue(player!!, PrisonPeteDialogue(dialOpt = 3))
                        }
                        stage++
                    }
                }
            }

            3 -> {
                when (stage) {
                    0 -> {
                        if (energyBarrier == 3) {
                            npc(FaceAnim.FRIENDLY, "You did it, you got all the keys right!", "Thank you! You're my friend FOREVER!")
                            stage = 1
                        } else if (energyBarrier in 1..2) {
                            npc(FaceAnim.HAPPY, "Hooray, you got the right one! Now pull the lever again", "and let's get the next lock unlocked.")
                            animate(npc!!, Animations.TAKE_THING_OUT_OF_POCKET_AND_GIVE_IT_4540)
                            stage = 2
                        } else if (getAttribute(player!!, PrisonPeteUtils.POP_KEY_FALSE, false)) {
                            playJingle(player!!, 149)
                            npc(FaceAnim.SAD, "Aww, that was the wrong key! Try the lever again", "to see which balloon you need.")
                            removeAttribute(player!!, PrisonPeteUtils.POP_KEY_FALSE)
                            stage = 2
                        }
                    }
                    1 -> player(FaceAnim.NOD_YES, "Let's get out of here before that cat notices.").also { stage = END_DIALOGUE }
                    2 -> end()
                }
            }

            5 ->  {
                // https://youtu.be/DX5ZVwvMazc?si=ZfeVeFd0agoFCEiZ&t=154
                when (stage) {
                    0 -> npc(FaceAnim.NOD_YES, "Thanks a lot for your help!", "Here, have a present:").also { stage++ }
                    1 -> player(FaceAnim.NEUTRAL, "Thanks! See you around!").also { stage++ }
                    2 -> end().also{
                        AntiMacro.rollEventLoot(player!!).forEach {
                        addItemOrDrop(player!!, it.id, it.amount)
                    }}
                }
            }
            0 -> {
                // https://youtu.be/xJA2mNsEYeg?si=58NahFdKeBbDirFE&t=9
                when (stage) {
                    0 -> {
                        setTitle(player!!, 2)
                        sendOptions(player!!, "What would you like to say?", "What is this place?", "How do I get out of here?").also { stage++ }
                    }
                    1 -> when (buttonID) {
                        1 -> player(FaceAnim.THINKING, "What is this place?").also { stage++ }
                        2 -> player(FaceAnim.THINKING, "How do I get out of here?", "I can't be held captive by a cat!").also { stage = 6 }
                    }
                    2 -> npc(FaceAnim.SAD, "Don't You remember? This is ScapeRune's prison.", "Evil Bob caught you and brought you here.").also { stage++ }
                    3 -> player(FaceAnim.ANNOYED, "What gives him the right to lock me up?", "I demand to see a lawyer! I know my rights!").also { stage++ }
                    4 -> npc(FaceAnim.SAD, "Evil Bob doesn't care about people's rights.", "He's cruel and utterly merciless. He's a cat.").also { stage++ }
                    5 -> player(FaceAnim.ANNOYED, "How do I get out of here?", "I can't be held captive by a cat!").also { stage++ }
                    6 -> npc(FaceAnim.SAD, "Some of these balloon animals have keys in them, and if", "you pull the big lever it tells you which shape animal", "contains the correct key, but I can never find it.").also { stage++ }
                    7 -> npc(FaceAnim.SAD, "You need to pull the lever to find out which shape", "animal contains the key, then pop that sort of animal to", "get the key.").also { stage++ }
                    8 -> npc(FaceAnim.SAD, "Bring me any keys you get and", "I'll try them on the doors.").also { stage++ }
                    9 -> player(FaceAnim.THINKING, "What happens if I get it wrong?").also { stage++ }
                    // TODO: Timer.
                    10 -> npc(FaceAnim.SAD, "You haven't got a life sentence, so they'll let you out in", "23 hours 59 minutes. You should be able to escape", "much faster if you go pull that lever and pop the right", "balloon animals.").also { stage = END_DIALOGUE }
                }
            }
        }
    }
}
