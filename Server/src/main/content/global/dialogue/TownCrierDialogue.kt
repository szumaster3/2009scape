package content.global.dialogue

import content.global.plugins.item.books_and_scrolls.impl.GeneralRuleBook
import content.region.kandarin.west_ardougne.diary.dialogue.TownCrierDiaryDialogue
import core.api.*
import core.game.dialogue.*
import core.game.node.entity.player.Player
import core.game.node.entity.player.info.Rights
import core.game.node.entity.player.link.diary.Diary
import core.game.node.entity.player.link.diary.DiaryType
import core.game.world.GameWorld
import core.game.world.GameWorld.settings
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.NPCs

@Initializable
class TownCrierDialogue(player: Player? = null) : Dialogue(player)
{
    override fun open(vararg args: Any?): Boolean {
        npcl(FaceAnim.HAPPY, "Hear ye! Hear ye! Player Moderators massive help to ${GameWorld.settings?.name}!").also { stage++ }
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        val jMod = player.rights == Rights.PLAYER_MODERATOR
        val randomModDialogue = randomGuideDialogue.random()
        val randomTipDialogue = randomTipDialogue.random()
        when (stage) {
            0 -> {
                if(jMod) {
                    npcl(FaceAnim.FRIENDLY, "Oh great, a P-mod! I know all about the great job you guys do, would you like me to prove my extensive knowledge to you?!").also { stage++ }
                } else {
                    npcl(FaceAnim.HAPPY, "Oh, hello citizen. Are you here to find out about Player Moderators? Or perhaps would you like to know about the laws of the land?").also { stage++ }
                }
            }
            1 -> showTopics(
                Topic(if(jMod) "Can you show me the Guidelines please?"     else "Tell me about Player Moderators.",     2),
                Topic(if(jMod) "Do you have a copy of the code of conduct?" else "Tell me about the Rules of ${settings!!.name}.",5),
                Topic(if(jMod) "Can you give me a moderating tip please?"   else "Can you give me a handy tip please?",  2),
                IfTopic("Talk about achievement diary", 4, npc.id == NPCs.TOWN_CRIER_6138 && Diary.canClaimLevelRewards(player!!, DiaryType.ARDOUGNE, 1)),
                Topic("No thanks.", end())
            )
            2 -> npcl(FaceAnim.FRIENDLY, if(jMod) randomModDialogue else randomTipDialogue).also { stage++ }
            3 -> npcl(FaceAnim.HALF_ASKING, "Is there anything else you'd like to know?").also { stage = 1 }
            4 -> end().also { openDialogue(player, TownCrierDiaryDialogue(), npc) }
            5 -> end().also { openDialogue(player, TownCrierRulesDialogue(), npc) }
        }

        return true
    }

    private val randomGuideDialogue = listOf(
    "Add other Player Moderators to your friends list for quick advice and moral support in-game. It does help!",
    "Did you know that Free-to-Play P-mods CAN visit the moderator forums, by logging into the regular forums at the top right hand side of the forum screen, despite it saying Members only.",
    "Don't be tempted to report anything you didn't see, no matter how much you trust the player who is informing you.",
    "Don't eat something messy when on a first date - it won't look good!",
    "Don't forget to add the mute if needed, before selecting the report category. This cuts down on those duplicate reports for one player.",
    "Feeling harassed? Don't forget your ignore list can be especially useful if a player seems to be picking on you!",
    "The forums are the best place to keep abreast of any procedure changes that might not yet be included in the Player Mod guidelines!",
    "Got players asking you to unban them or help them retrieve a stolen or hacked account? There's nothing you can do so just send them to the Customer Support Team. If they think you can help, send them to me; I'll put them straight!",
    "Have the P-Mod Oracle open in a separate window when playing ${GameWorld.settings?.name} - that way you'll always have the most up-to-date guidelines to hand!",
    "If a player isn't sure of the rules, send them to me! I'll be happy to remind them!",
    "If the chat is scrolling too fast for you to see that suspect line you want to double check, just run up a ladder or to a quieter spot to stop the chat screen scrolling. Then you can scroll back and read that suspect line and judge the situation in your own time!",
    "If you get harassed by a player in game, don't take it personally - the player is reacting to your silver crown, not to you!",
    "If you see a player abusing a bug in game, there's no need to report the player, just submit a bug report via the front page of the ${GameWorld.settings?.name} website.",
    "If you're a new mod, don't forget to read the guidelines before you log into the game! If you've been a P-mod for a while, why not have another read of them to refresh your memory and to get up to date with any changes you may have missed!",
    "If you're asked questions that may breach confidentiality, please refer the player to the Game Guide or the Player Moderator FAQ. If it's not there, they shouldn't know.",
    "If you've got a player asking you questions about P-mods, feel free to send them to me; I can answer questions about P-mods for you!",
    "An important part of being a P-mod is to act as an ambassador for the game. Keeping your cool even when faced with the rudest players goes a long way towards this!",
    "It's always better to report in secret. Telling a player that you are about to report them rarely makes you a friend, and can often lead to a player being offensive to you!",
    "Make sure you log out of the game and the forums whenever you leave your computer. Don't allow others to see confidential info!",
    "The P-mod forums are your friend! They're the best place to talk to and meet other P-mods and are absolutely packed with useful advice!",
    "Remember, if in doubt, report without! 48 hours is long time not to be able to speak, especially if you're innocent!",
    "Remember, if offensive language has been compeletely filtered out then there's no need to report or mute it!",
    "Think twice before applying that mute. Will it really prevent further damage from surrounding players?",
    "To avoid reporting the wrong player, add an offender to your friends list, switch your public chat to friends and then right-click to report from there. Easy!",
    "Use the P-mod team if you need to - they're there to help!",
    "Your j-mods are there to help you. If you ever need their advice, send them a query. They are always very happy to help. Don't be afraid to use them!",
    "Your Player Moderator identity must never be revealed on the regular ${GameWorld.settings?.name} forums. However, you're more than welcome to chat about all aspects of being a Player Moderator in the official Player Moderator forums!",
    )

    private val randomTipDialogue = listOf(
    "Be careful when fighting wizards! Wearing heavy armour can lower your Magic resistance!",
    "Beware of players trying to lure you into the wilderness. Your items cannot be returned if you lose them!",
    "Did you know having a bank pin can help you secure your valuable items?",
    "Did you know most skills have right click 'Make-X' options to help you train faster?",
    "Did you know that at high levels of Runecrafting you get more than one rune per essence?",
    "Did you know that mithril equipment is very light?",
    "Did you know that you can wear a shield with a crossbow?",
    "Did you know you burn food less often than the range in Lumbridge castle than other ranges?",
    "Did you know? Superheat Item means you never fail to smelt ore!",
    "Did you know? You burn food less often on a range than on a fire!",
    "Don't use your ${GameWorld.settings?.name} password on other sites. Keep your account safe!",
    "Feeling harassed? Don't forget your ignore list can be especially useful if a player seems to be picking on you!",
    "If a player isn't sure of the rules, send them to me! I'll be happy to remind them!",
    "If the chat window is moving too quickly to report a player accurately, run to a quiet spot and review the chat at your leisure!",
    "If you see someone breaking the rules, report them!",
    "If you think someone knows your password - change it!",
    "If you're lost have no idea where to go, use the Home Teleport spell for free!",
    "Administrator will never email you asking for your log-in details.",
    "Make your recovery questions and answers hard to guess but easy to remember.",
    "Melee armour actually gives you disadvantages when using magic attacks. It may be better to take off your armour entirely!",
    "Never let anyone else use your account.",
    "Never question a penguin.",
    "Never tell your password to anyone, not even your best friend!",
    "Players can not trim armour. Don't fall for this popular scam!",
    "The squirrels! The squirrels are coming! Noooo, get them out of my head!",
    "Take time to check the second trade window carefully. Don't be scammed!",
    "There are no cheats in ${GameWorld.settings?.name}! Never visit websites promising otherwise!",
    )

    override fun getIds() = intArrayOf(
        NPCs.TOWN_CRIER_6135,
        NPCs.TOWN_CRIER_6136,
        NPCs.TOWN_CRIER_6137,
        NPCs.TOWN_CRIER_6138,
        NPCs.TOWN_CRIER_6139
    )
}