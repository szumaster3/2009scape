package core.game.bots;

import content.data.consumables.Consumables;
import core.game.consumable.Consumable;
import core.game.node.entity.Entity;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.prayer.PrayerType;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.tools.RandomFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple PvM-oriented bot implementation.
 */
public class PvMBots extends AIPlayer {

    /**
     * Internal tick counter used for simple timing logic.
     */
    private int tick = 0;

    /**
     * Creates a new PvM bot at the given location.
     *
     * @param l spawn location
     */
    public PvMBots(Location l) {
        super(l);
    }

    /**
     * Creates a new PvM bot using a preset definition.
     *
     * @param copyFromFile preset/profile name
     * @param l            spawn location
     */
    public PvMBots(String copyFromFile, Location l) {
        super(copyFromFile, l);
    }

    /**
     * Finds nearby valid NPC targets within a given radius.
     *
     * @param entity the searching entity
     * @param radius search radius
     * @return list of valid targets, or {@code null} if none found
     */
    public List<Entity> FindTargets(Entity entity, int radius) {
        List<Entity> targets = new ArrayList<>(20);
        Object[] localNPCs = RegionManager.getLocalNpcs(entity, radius).toArray();

        int length = localNPCs.length;
        if (length > 5) {
            length = 5; // limit scan for performance
        }

        for (int i = 0; i < length; i++) {
            NPC npc = (NPC) localNPCs[i];
            if (checkValidTargets(npc)) {
                targets.add(npc);
            }
        }

        return targets.isEmpty() ? null : targets;
    }

    /**
     * Validates whether an NPC can be attacked.
     *
     * @param target npc to validate
     * @return {@code true} if attackable
     */
    public boolean checkValidTargets(NPC target) {
        if (!target.isActive()) {
            return false;
        }
        if (!target.getProperties().isMultiZone() && target.inCombat()) {
            return false;
        }
        if (!target.getDefinition().hasAction("attack")) {
            return false;
        }
        return true;
    }

    /**
     * Attempts to attack nearby NPCs.
     *
     * @param radius search radius
     * @return {@code true} if an attack was initiated
     */
    public boolean AttackNpcsInRadius(int radius) {
        return AttackNpcsInRadius(this, radius);
    }

    /**
     * Attempts to attack nearby NPCs for a given player.
     *
     * @param bot    player/bot instance
     * @param radius search radius
     * @return {@code true} if an attack was initiated
     */
    public boolean AttackNpcsInRadius(Player bot, int radius) {
        if (bot.inCombat()) {
            return true;
        }

        List<Entity> creatures = FindTargets(bot, radius);
        if (creatures == null) {
            return false;
        }

        bot.attack(creatures.get(RandomFunction.getRandom(creatures.size() - 1)));

        if (!creatures.isEmpty()) {
            return true;
        }

        creatures = FindTargets(bot, radius);
        if (creatures != null && !creatures.isEmpty()) {
            bot.attack(creatures.get(RandomFunction.getRandom(creatures.size() - 1)));
            return true;
        }

        return false;
    }

    /**
     * Main update loop executed each game tick.
     */
    @Override
    public void tick() {
        super.tick();

        this.tick++;

        // basic sustain: prevent death
        if (this.getSkills().getLifepoints() <= 5) {
            this.getSkills().updateHitpoints(20);
        }

        if (this.tick == 100) {
            this.tick = 0;
        }
    }

    /**
     * Toggles a set of prayers.
     *
     * @param type array of prayers to toggle
     */
    public void CheckPrayer(PrayerType type[]) {
        for (int i = 0; i < type.length; i++) {
            this.getPrayer().toggle(type[i]);
        }
    }

    /**
     * Attempts to consume food for healing.
     *
     * @param foodId item id of the food
     */
    public void eat(int foodId) {
        Item foodItem = new Item(foodId);

        if ((this.getSkills().getStaticLevel(Skills.HITPOINTS) >= this.getSkills().getLifepoints() * 3)
                && this.getInventory().containsItem(foodItem)) {

            this.lock(3);

            Item food = this.getInventory().getItem(foodItem);
            Consumable consumable = Consumables.getConsumableById(food.getId()).getConsumable();

            if (consumable == null) {
                return;
            }

            consumable.consume(food, this);
            this.getProperties().getCombatPulse().delayNextAttack(3);
        }

        if (!this.checkVictimIsPlayer()) {
            if (!this.getInventory().contains(foodId, 1)) {
                this.getInventory().add(new Item(foodId, 5));
            }
        }
    }
}