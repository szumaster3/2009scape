package content.global.skill.hunter.impling

import content.global.skill.magic.spells.ModernSpells
import core.api.*
import core.game.node.entity.Entity
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.SpellBookManager
import core.game.node.entity.player.link.audio.Audio
import core.game.node.item.Item
import core.game.world.map.path.ClipMaskSupplier
import core.tools.Log
import core.tools.RandomFunction
import shared.consts.Graphics
import shared.consts.NPCs
import shared.consts.Sounds

class ImplingNPC : NPCBehavior(*Implings.getIds()) {

    override fun onCreation(self: NPC) {
        self.isWalks = true
        self.isNeverWalks = false
    }

    override fun tick(self: NPC): Boolean {
        if (RandomFunction.roll(10)) {
            sendChat(self, "Tee-hee!")
        }
        return true
    }

    override fun onRespawn(self: NPC) {
        if (!isPuroImpling(self)) {
            log(this::class.java, Log.ERR, "Non-puro impling has respawned!")
        }
        playGlobalAudio(self.location, Sounds.II_IMPLING_SPAWN_3722, 1, 0, Audio.defaultAudioRadius)
        sendGraphics(Graphics.IMPLING_TP_WHITE_SMOKE_PUFF_1119, self.properties.teleportLocation!!)
    }

    override fun canBeAttackedBy(self: NPC, attacker: Entity, style: CombatStyle, shouldSendMessage: Boolean): Boolean {
        if (attacker !is Player) return false

        if (style != CombatStyle.MAGIC) {
            if (shouldSendMessage) {
                sendMessage(attacker, "You can't do that.")
            }
            return false
        }

        val spellBook = attacker.spellBookManager.spellBook
        if (spellBook != SpellBookManager.SpellBook.MODERN.interfaceId) {
            if (shouldSendMessage) {
                sendMessage(attacker, "The impling is too fast for that.")
            }
            return false
        }

        val spellId = attacker.properties.spell!!.spellId
        if (spellId != ModernSpells.BIND && spellId != ModernSpells.SNARE && spellId != ModernSpells.ENTANGLE) {
            if (shouldSendMessage) {
                sendMessage(attacker, "The impling is immune to that magic.")
            }
            return false
        }

        return true
    }

    override fun onDeathFinished(self: NPC, killer: Entity) {
        if (!isPuroImpling(self)) {
            ImplingController.deregister(self)
        } else if (self.originalId != self.id) {
            self.reTransform()

            self.behavior = forId(self.id)
        }
    }

    override fun onDropTableRolled(self: NPC, killer: Entity, drops: ArrayList<Item>) {
        drops.clear()
    }

    override fun getClippingSupplier(self: NPC): ClipMaskSupplier = ImplingClipper

    private fun isPuroImpling(self: NPC): Boolean =
        self.id in intArrayOf(
            NPCs.BABY_IMPLING_6055,
            NPCs.GOURMET_IMPLING_6057,
            NPCs.EARTH_IMPLING_6058,
            NPCs.ESSENCE_IMPLING_6059,
            NPCs.ECLECTIC_IMPLING_6060,
            NPCs.NINJA_IMPLING_6063,
            NPCs.DRAGON_IMPLING_6064,
            NPCs.NATURE_IMPLING_6061,
            NPCs.PIRATE_IMPLING_7846
        )
}
