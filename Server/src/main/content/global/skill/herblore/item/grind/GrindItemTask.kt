package content.global.skill.herblore.item.grind

import core.api.*
import core.game.node.entity.player.Player
import core.game.node.entity.skill.SkillingTask
import core.game.node.item.Item
import shared.consts.Animations
import shared.consts.Items
import shared.consts.Sounds

class GrindItemTask(
    player: Player,
    private val grind: GrindableItem,
    private var amount: Int
) : SkillingTask<Player>(player, cycleTicks = 2) {

    override fun canStart(): Boolean {
        return true
    }

    override fun canContinue(): Boolean {
        return amount > 0 &&
                inInventory(player, grind.items.first())
    }

    override fun playAnimation() {
        playAudio(player, Sounds.GRIND_2608)
        animate(player, Animations.HUMAN_USE_PESTLE_AND_MORTAR_364)
    }

    override fun process(): Boolean {
        val sourceId = grind.items.first()

        if (!inInventory(player, sourceId))
            return true


        val specialCase = if (sourceId == FISHING_BAIT)
            10.coerceAtMost(amountInInventory(player, sourceId))
         else
             1

        if (!removeItem(player, Item(sourceId, specialCase))) {
            return true
        }

        addItem(player, grind.product, specialCase)
        sendMessage(player, grind.message)

        amount -= specialCase

        return amount <= 0
    }

    companion object {
        private const val FISHING_BAIT = Items.FISHING_BAIT_313
    }
}