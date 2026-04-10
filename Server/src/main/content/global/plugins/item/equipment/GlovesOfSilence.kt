package content.global.plugins.item.equipment

import core.api.Container
import core.api.amountInInventory
import core.api.inEquipment
import core.api.removeItem
import core.game.node.entity.player.Player
import core.game.node.item.Item
import shared.consts.Items

/**
 * Gloves of silence manager.
 */
object GlovesOfSilence {

    /**
     * Default amount of charges.
     */
    const val MAX_CHARGES = 1000

    /**
     * The gloves.
     */
    private val ITEM_ID = Items.GLOVES_OF_SILENCE_10075

    private fun isGloves(itemId: Int): Boolean =
        itemId == ITEM_ID

    /**
     * Each failure reduces charge by one usage.
     * Item destroyed after 50 and 1 use.
     */
    fun reduceDurability(p: Player, item: Item): Item? {
        if (!isGloves(item.id)) return null

        val current = item.charge.takeIf { it > 0 } ?: MAX_CHARGES
        val newCharge = (current - 1).coerceAtLeast(0)
        val isEquipped = inEquipment(p, item.id, 1)
        if (newCharge <= 949) {
            if (isEquipped) {
                removeItem(p, item, Container.EQUIPMENT)
            } else {
                removeItem(p, item)
            }
            return null
        }

        return Item(item.id, item.amount, newCharge)
    }

    /**
     * Repair the gloves (recharge).
     */
    fun repair(item: Item): Item? {
        if (!isGloves(item.id)) return null
        return Item(item.id, item.amount, MAX_CHARGES)
    }
}