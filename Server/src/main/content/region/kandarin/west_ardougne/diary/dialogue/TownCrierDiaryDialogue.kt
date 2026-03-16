package content.region.kandarin.west_ardougne.diary.dialogue

import content.data.GameAttributes
import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.IfTopic
import core.game.dialogue.Topic
import core.game.node.entity.player.link.diary.Diary
import core.game.node.entity.player.link.diary.DiaryType
import shared.consts.Items
import shared.consts.Quests

class TownCrierDiaryDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> showTopics(
                IfTopic("Ah, the cloak-wearer! How are you finding it?", 14, Diary.canReplaceReward(player!!, DiaryType.ARDOUGNE, 1)),
                Topic("I've completed all of the Medium Tasks in the Ardougne set.", 1),
                IfTopic("Toggle Ring of Life teleport location.", 20, isDiaryComplete(player!!, DiaryType.ARDOUGNE, 1)),
                IfTopic("I'd like to change my Watchtower Teleport point.", 18, isDiaryComplete(player!!, DiaryType.ARDOUGNE, 2))
            )
            1 -> npcl(FaceAnim.FRIENDLY, "So you have. I have a special cloak I've kept aside for you.").also { stage++ }
            2 -> {
                Diary.flagRewarded(player!!, DiaryType.ARDOUGNE, 1)
                sendDoubleItemDialogue(player!!, Items.ARDOUGNE_CLOAK_2_14702, Items.ANTIQUE_LAMP_14705, "The town crier hands you a cloak and a lamp.")
                stage++
            }
            3 -> npcl(FaceAnim.FRIENDLY, "There you go. It's an interesting cloak, that one - seems to be quite magical. It's got an affinity with the vegetable patch north of the city when you can coax it into life. Oh, and someone gave me this lamp, but it's not really any use to me.").also { stage++ }
            4 -> playerl(FaceAnim.HALF_ASKING, "Thanks! What else does it do?").also { stage++ }
            5 -> npcl(FaceAnim.FRIENDLY, "Well, Cromperty told me that anyone he knows has earned it will get a nice little surprise if they talk to him every day. Oh, and it seems to have a magical effect when you use it at that Zamorakian-held runecrafting altar, south-").also { stage++ }
            6 -> npcl(FaceAnim.FRIENDLY, "west of the city. I also heard rumours from the last person who owned it that it has a use at the Tower of Life. Of course, it may do even more that I'm just not aware of.").also { stage++ }
            7 -> showTopics(
                Topic("What is the Achievement Diary?", 8),
                Topic("What are the rewards?", 11),
                Topic("How do I claim the rewards?", 12),
                Topic("See you later.", end())
            )
            8 -> npcl(FaceAnim.NEUTRAL, "It's a diary that helps you keep track of particular achievements. Here on Ardougne it can help you, discover some quite useful things. Eventually, with, enough exploration, the people of Ardougne will reward you.").also { stage++ }
            9 -> options("What are the rewards?", "How do I claim the rewards?", "Sorry, I was just leaving.").also { stage++ }
            10 -> when (buttonID) {
                1 -> player("What are the rewards?").also { stage = 5 }
                2 -> player("How do I claim the rewards?").also { stage = 6 }
                3 -> end()
            }
            11 -> npcl(FaceAnim.NEUTRAL, "For completing the Ardougne set, you are presented with a cloak. This cloak will become increasingly useful with each difficulty level of the set that you complete. When you are presented with your rewards, you will be told of their uses.").also { stage = 9 }
            12 -> npc(FaceAnim.NEUTRAL, "You need to complete all of the Diary in a set of a particular difficulty, then you can claim your reward.", "Some of the Ardougne set's Diary are simple, some will require skill levels, and some might require quests to be started or completed.").also { stage++ }
            13 -> npcl(FaceAnim.FRIENDLY, "To claim your Ardougne set rewards, speak to Doctor Orbon in East Ardougne church, Aleck in Yanille, or myself.").also { stage = 7 }
            14 -> player(FaceAnim.SAD, "I lost it.").also { stage++ }
            15 -> npcl(FaceAnim.THINKING, "Oh, no!").also { stage++ }
            16 -> npcl(FaceAnim.NEUTRAL, "Never mind, I have another. Here...").also { stage++ }
            17 -> {
                Diary.grantReplacement(player!!, DiaryType.ARDOUGNE, 1)
                sendItemDialogue(player!!, Items.ARDOUGNE_CLOAK_2_14702, "The town crier hands you a cloak.")
                stage = 0
            }
            18 -> {
                if (!isQuestComplete(player!!, Quests.WATCHTOWER)) {
                    end()
                    sendMessage(player!!, "You need to complete The Watchtower quest to do this.")
                    return
                }
                setTitle(player!!, 2)
                val altTele = getAttribute(player!!, GameAttributes.ATTRIBUTE_WATCHTOWER_ALT_TELE, false)
                sendOptions(player!!, "Toggle Watchtower Teleport to ${if (altTele) "Watchtower" else "centre of Yanille"}?", "Yes", "No")
                stage++
            }
            19 -> when (buttonID) {
                1 -> {
                    end()
                    setAttribute(player!!, GameAttributes.ATTRIBUTE_WATCHTOWER_ALT_TELE, !getAttribute(player!!, GameAttributes.ATTRIBUTE_WATCHTOWER_ALT_TELE, false))
                    val destinationText = if (getAttribute(player!!, GameAttributes.ATTRIBUTE_WATCHTOWER_ALT_TELE, false)) "centre of Yanille" else "Watchtower"
                    sendMessage(player!!, "Watchtower Teleport will now teleport you to the $destinationText.")
                }
                2 -> end()
            }
            20 -> {
                setTitle(player!!, 2)
                val altRingOfLifeTeleport =
                    getAttribute(player!!, GameAttributes.ATTRIBUTE_RING_OF_LIFE_ALT_TELE, false)
                sendOptions(player!!, "Toggle ring of life to ${if (!altRingOfLifeTeleport) "Ardougne" else "Lumbridge"}?", "Yes", "No")
                stage++
            }
            21 -> when (buttonID) {
                1 -> {
                    end()
                    setAttribute(player!!, GameAttributes.ATTRIBUTE_RING_OF_LIFE_ALT_TELE, !getAttribute(player!!, GameAttributes.ATTRIBUTE_WATCHTOWER_ALT_TELE, false))
                    val destinationText = if (getAttribute(player!!, GameAttributes.ATTRIBUTE_RING_OF_LIFE_ALT_TELE, false)) "Lumbridge" else "Ardougne"
                    sendMessage(player!!, "Rings of life will now teleport you to $destinationText.")
                }
                2 -> end()
            }
        }

    }
}
