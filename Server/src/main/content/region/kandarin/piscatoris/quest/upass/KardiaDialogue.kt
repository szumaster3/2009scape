package content.region.kandarin.piscatoris.quest.upass

import content.data.GameAttributes
import content.region.misthalin.draynor.quest.swept.plugin.SweptUtils
import core.api.*
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.plugin.Initializable
import shared.consts.Items
import shared.consts.NPCs

/**
 * Represents the Eniola dialogue.
 */
@Initializable
class KardiaDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when(stage) {
            0 -> if(inInventory(player, Items.BROOMSTICK_14057) && !getAttribute(player, GameAttributes.QUEST_SWEPT_AWAY_KARDIA_ENCH_RECEIVED, false)) {
                player("Could you enchant this broom for me?").also { stage++ }
            } else {
                sendMessage(player, "Kardia doesn't seem interested in talking.")
            }
            1 -> npc("...").also { stage++ }
            2 -> {
                lock(player, 1)
                visualize(player, -1, SweptUtils.BROOM_ENCHANTMENT_GFX)
                sendItemDialogue(player, Items.BROOMSTICK_14057, "You receive 14,979 Magic experience.")
                rewardXP(player, Skills.MAGIC, 14979.0)
                setAttribute(player, GameAttributes.QUEST_SWEPT_AWAY_KARDIA_ENCH_RECEIVED, true)
                stage = 3
            }
            3 -> {
                end()
                player("Many thanks.")
            }
        }
        return true
    }

    override fun getIds(): IntArray = intArrayOf(NPCs.KARDIA_992)
}