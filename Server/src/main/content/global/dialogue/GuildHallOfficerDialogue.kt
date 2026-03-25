package content.global.dialogue

import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE

@Initializable
class GuildHallOfficerDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> npc("Hello!").also { stage = END_DIALOGUE }
        }

        return true
    }

    override fun getIds() = intArrayOf(
        8591
    )
}