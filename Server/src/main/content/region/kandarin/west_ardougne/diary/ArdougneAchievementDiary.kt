package content.region.kandarin.west_ardougne.diary

import core.game.diary.DiaryEventHookBase
import core.game.event.*
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.DiaryType

class ArdougneAchievementDiary : DiaryEventHookBase(DiaryType.ARDOUGNE) {

   companion object {
       object EasyTasks {
           const val WIZARD_CROMPERTY_TELEPORT_TO_ESSENCE_MINE = 0
           const val STEAL_FROM_STALL_OR_GUARD = 1
           const val SELL_SILK_TO_TRADER = 2
           const val USE_ALTAR_IN_WEST_ARD = 3
           const val ENTER_CASTLE_WARS_WAITING_ROOM = 4
           const val FISH_ON_FISHING_TRAWLER = 5
           const val ENTER_COMBAT_TRAINING_CAMP = 6
           const val TALK_TO_CIVILIAN_ABOUT_CAT = 7
           const val KILL_UNICOW_IN_TOWER_OF_LIFE = 8
           const val GET_CIVILIAN_TO_THROW_TOMATO = 9
           const val SAIL_TO_KARAMJA = 10
           const val PICK_LOCK_DOOR_EAST_OF_GEM_STALL = 11
           const val SPEND_PENGUIN_POINTS_AT_ZOO = 12
           const val USE_SUMMONING_OBELISK = 13
           const val POP_BALLOON_IN_MONASTERY = 14
           const val BUY_WATER_VIAL_FROM_GENERAL_STORE = 15
           const val USE_NOTICEBOARD_NEAR_OBSERVATORY = 16
           const val KILL_SOMETHING_ON_KHAZARD_BATTLEFIELD = 17
           const val BUY_SKEWERED_KEBAB_AT_POISON_ARROW = 18
           const val VIEW_HUNTER_EQUIPMENT_IN_ALECKS_SHOP = 19
           const val TALK_TO_HEAD_SERVANT_AT_SERVANTS_GUILD = 20
           const val USE_RING_OF_DUELING_TO_CASTLE_WARS = 21
           const val TALK_TO_TINDEL_MARCHANT_ABOUT_SWORDS = 22
       }

       object MediumTasks {
           const val ENTER_UNICORN_PEN_USING_FAIRY_RINGS = 0
           const val TELEPORT_TO_WILDERNESS_USING_LEVER = 1
           const val GRAPPLE_OVER_YANILLE_SOUTHERN_WALL = 2
           const val CRAFT_RUNES_AT_OURANIA_ALTAR = 3
           const val SELL_RUBIUM_TO_EZEKIAL_LOVECRAFT = 4
           const val PICK_WATERMELONS_FROM_FARMING_PATCH = 5
           const val CAST_WATCHTOWER_TELEPORT = 6
           const val TRAVEL_TO_CASTLE_WARS_BY_BALLOON = 7
           const val CLAIM_BUCKETS_OF_SAND_FROM_BERT = 8
           const val RETURN_TO_PAST_AND_TALK_TO_SARAH = 9
           const val CATCH_FISH_AT_FISHING_PLATFORM = 10
           const val CROSS_RIVER_DOUGNE_USING_LOG_BALANCE = 11
           const val PICKPOCKET_MASTER_FARMER = 12
           const val STEAL_NATURE_RUNE_FROM_CHEST = 13
           const val MINE_COAL_EAST_OF_ARD = 14
           const val KILL_SWORDCHICK_IN_TOWER_OF_LIFE = 15
       }

       object HardTasks {
           const val RECHARGE_BRACELET_OR_NECKLACE_AT_LEGENDS_GUILD = 0
           const val KILL_SHADOW_WARRIOR_IN_LEGENDS_GUILD_BASEMENT = 1
           const val ENTER_MAGIC_GUILD_IN_YANILLE = 2
           const val USE_MAGIC_GUILD_PORTAL_TO_THORMACS_TOWER = 3
           const val BE_ON_WINNING_SIDE_IN_CASTLE_WARS = 4
           const val CAST_OURANIA_TELEPORT_SPELL = 5
           const val PICKPOCKET_WATCHMAN_WHILE_WEARING_GLOVES = 6
           const val KILL_FROGEEL_IN_TOWER_OF_LIFE = 7
           const val ZOOKEEPER_PUT_YOU_IN_MONKEY_CAGE = 8
           const val KILL_OWN_JADE_VINE_AFTER_BACK_TO_MY_ROOTS = 9
           const val USE_AIR_GUITAR_EMOTE_NEAR_MUSICIAN = 10
           const val CAST_ARD_TELEPORT_SPELL = 11
           const val CROSS_MONKEY_BARS_IN_AGILITY_DUNGEON = 12
           const val CATCH_RED_SALAMANDER_OUTSIDE_OURANIA_ALTAR = 13
           const val PICK_PAPAYA_OR_COCONUT_NEAR_TREE_GNOME_VILLAGE = 14
           const val STEAL_BLOOD_RUNES_FROM_CHAOS_DRUID_TOWER = 15
           const val USE_CATAPULT_IN_CASTLE_WARS_AFTER_CONSTRUCTION = 16
       }
   }

    override fun onInteracted(player: Player, event: InteractionEvent) {}
    override fun onDialogueOptionSelected(player: Player, event: DialogueOptionSelectionEvent) {}
    override fun onResourceProduced(player: Player, event: ResourceProducedEvent) {}
    override fun onNpcKilled(player: Player, event: NPCKillEvent) {}
    override fun onItemPurchasedFromShop(player: Player, event: ItemShopPurchaseEvent) {}
    override fun onItemEquipped(player: Player, event: ItemEquipEvent) {}
    override fun onFairyRingDialed(player: Player, event: FairyRingDialEvent) {}
    override fun onLightSourceLit(player: Player, event: LitLightSourceEvent) {}
    override fun onUsedWith(player: Player, event: UseWithEvent) {}
}