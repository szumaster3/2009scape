package content.region.kandarin.seers_village.hemenster.quest.fishingcompo.dialogue

import core.api.addItem
import core.api.removeItem
import core.api.sendDoubleItemDialogue
import core.game.dialogue.Dialogue
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.Items
import shared.consts.NPCs

/**
 * Represents the Grandpa Jack dialogue.
 *
 * # Relations
 * - [Fishing Contest][content.region.kandarin.quest.fishingcompo.FishingContest]
 */
@Initializable
class GrandpaJackDialogue(player: Player? = null) : Dialogue(player) {
    
    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npc("Hello young'on!", "Come to visit old Grandpa Jack? I can tell ye stories", "for sure. I used to be the best fisherman these parts", "have seen!")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> showTopics(
                Topic("Tell me a story then.", 3),
                Topic("Are you entering the fishing competition?", 7),
                Topic("Sorry, I don't have time now.", 18),
                Topic("Can I buy one of your fishing rods?", 19),
                Topic("I've forgotten how to fish, can you remind me?", 24)
            )
            1 -> showTopics(
                Topic("I don't suppose you could give me any hints?", 9),
                Topic("That's less competition for me then.", 17),
            )
            2 -> showTopics(
                Topic("Very fair, I'll buy that rod!", 20),
                Topic("That's too rich for me, I'll go to Catherby.", 23),
            )
            3  -> npc("Well, when I were a young man we used", "to take fishing trips over to Catherby.", "The fishing over there, now that was something!").also { stage++ }
            4  -> npc("Anyway, we decided to do a bit of fishing with our nets,", "I wasn't having the best of days turning up", "nothing but old boots and bits of seaweed.").also { stage++ }
            5  -> npc("Then my net suddenly got really heavy!", "I pulled it up... To my amazement", "I'd caught this little chest thing!").also { stage++ }
            6  -> npc("Even more amazing was when I opened it", "it contained a diamond the size of a radish!", "That's the best catch I've ever had!").also { stage = 0 }
            7  -> npc("Ah... the Hemenster fishing competition...").also { stage++ }
            8  -> npc("I know all about that... I won that four years straight!", "I'm too old for that lark now though...").also { stage = 1 }
            9  -> npc("Well, you sometimes get these really big fish in the", "water just by the outflow pipes.").also { stage++ }
            10 -> npc("I think they're some kind of carp...").also { stage++ }
            11 -> npc("Try to get a spot round there.", "The best sort of bait for them is red vine worms.").also { stage++ }
            12 -> npc("I used to get those from McGrubor's wood, north of", "here. Just dig around in the red vines up there but be", "careful of the guard dogs.").also { stage++ }
            13 -> player("There's this weird creepy guy who says he's not a", "vampire using that spot. He keeps winning too.").also { stage++ }
            14 -> npc("Ahh well, I'm sure you'll find something to put him off.", "After all, there must be a kitchen around here with", "some garlic in it, perhaps in Seers Village or Ardougne.", "If he's pretending to be a vampire then he can pretend").also { stage++ }
            15 -> npc("to be scared of garlic!").also { stage++ }
            16 -> player("You're right! Thanks Jack!").also { stage = END_DIALOGUE }
            17 -> npc("Why you young whippersnapper!", "If I was twenty years younger I'd show you something", "that's for sure!").also { stage = 0 }
            18 -> npc("Sigh... Young people - always in such a rush.").also { stage = END_DIALOGUE }
            19 -> npc("Of course you can young man. Let's see now...", "I think 5 gold is a fair price for a rod which", "has won the Fishing contest before eh?").also { stage = 2 }
            20 -> npc("Excellent choice!").also { stage++ }
            21 -> if(!removeItem(player, Item(Items.COINS_995, 5))) {
                player("I don't have enough money for that,", "I'll go get some and come back.").also { stage++ }
            } else {
                sendDoubleItemDialogue(player, Items.FISHING_ROD_307, Items.COINS_8897, "You hand over the money and receive a fishing rod.")
                addItem(player, Items.FISHING_ROD_307, 1)
                stage = END_DIALOGUE
            }
            22 -> npc("Right you are. I'll be here.").also { stage = END_DIALOGUE }
            23 -> npc("If you're sure... passing up an opportunity of a lifetime you are.").also { stage = END_DIALOGUE }
            24 -> npc("Of course! Let me see now... You'll need a rod and bait.", "You can fish with a net too, but not in the competition.").also { stage++ }
            25 -> player("Ok... I think I can get those in Catherby.").also { stage++ }
            26 -> npc("Then simply find yourself a fishing spot,", "either in the competition near here, or wherever you can.", "I recommend net fishing in Catherby.").also { stage++ }
            27 -> npc("Net or Lure the fish in the fishing spot", "by clicking on it and then be patient...").also { stage++ }
            28 -> player("It's that simple?").also { stage++ }
            29 -> npc("Yep! Go get em tiger.").also { stage = 0 }
        }
        return true
    }

    override fun getIds(): IntArray = intArrayOf(NPCs.GRANDPA_JACK_230)
}
