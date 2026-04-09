package content.global.skill.firemaking;

import content.data.skill.SkillingTool;
import content.global.skill.firemaking.items.Log;
import content.region.kandarin.baxtorian.BarbarianTraining;
import core.api.Container;
import core.api.ContentAPIKt;
import core.game.event.LitFireEvent;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.SkillPulse;
import core.game.node.entity.skill.Skills;
import core.game.node.item.GroundItem;
import core.game.node.item.GroundItemManager;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;
import core.game.node.scenery.SceneryBuilder;
import core.game.world.GameWorld;
import core.game.world.map.RegionManager;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.tools.RandomFunction;
import shared.consts.Animations;
import shared.consts.Items;

import static core.api.ContentAPIKt.*;

/**
 * Represents making fire plugin (standard and barbarian firemaking).
 */
public final class FireMakingPlugin extends SkillPulse<Item> {

    private final Log fire;
    private GroundItem groundItem;
    private int ticks;

    private final FiremakingMode mode;
    private Animation animation;
    private Graphics graphics;

    /**
     * Instantiates a new Firemaking pulse.
     *
     * @param player     the player
     * @param node       the node
     * @param groundItem the ground item
     */
    public FireMakingPlugin(Player player, Item node, GroundItem groundItem, FiremakingMode mode) {
        super(player, node);
        this.fire = Log.forId(node.getId());
        this.mode = mode;

        visualize(player);

        if (groundItem == null) {
            this.groundItem = new GroundItem(node, player.getLocation(), player);
            player.setAttribute("remove-log", true);
        } else {
            this.groundItem = groundItem;
            player.removeAttribute("remove-log");
        }
    }

    private void visualize(Player player) {
        if (mode == FiremakingMode.BARBARIAN) {
            SkillingTool tool = SkillingTool.getFiremakingTool(player);
            this.animation = tool != null ? new Animation(tool.getAnimation()) : null;
            this.graphics = new Graphics(shared.consts.Graphics.BARBARIAN_FIREMAKING_1169);
        } else {
            this.animation = new Animation(Animations.HUMAN_LIGHT_FIRE_WITH_TINDERBOX_733);
            this.graphics = null;
        }
    }

    @Override
    public boolean checkRequirements() {
        if (fire == null) return false;

        if (mode == FiremakingMode.BARBARIAN) {
            if (!player.getSavedData().activityData.getBarbarianFiremaking()
                    && getAttribute(player, BarbarianTraining.INSTANCE.getFM_START(), false)) {
                sendDialogue(player,
                        "You must begin the relevant section of Otto Godblessed's barbarian training.");
                return false;
            }
        }

        if (RegionManager.getObject(player.getLocation()) != null
                || player.getZoneMonitor().isInZone("bank")) {
            player.getPacketDispatch().sendMessage("You can't light a fire here.");
            return false;
        }

        if (mode == FiremakingMode.STANDARD
                && !inInventory(player, Items.TINDERBOX_590, 1)) {
            player.getPacketDispatch()
                    .sendMessage("You do not have the required items to light this.");
            return false;
        }

        int requiredLevel =
                mode == FiremakingMode.BARBARIAN
                        ? fire.getBarbarianLevel()
                        : fire.getDefaultLevel();

        if (player.getSkills().getLevel(Skills.FIREMAKING) < requiredLevel) {
            player.getPacketDispatch().sendMessage(
                    "You need a Firemaking level of " + requiredLevel + " to light this log.");
            return false;
        }

        if (player.getAttribute("remove-log", false)) {
            player.removeAttribute("remove-log");
            if (inInventory(player, node.getId(), 1)) {
                replaceSlot(
                        player,
                        node.getSlot(),
                        new Item(node.getId(), node.getAmount() - 1),
                        node,
                        Container.INVENTORY
                );
                GroundItemManager.create(groundItem);
            }
        }
        return true;
    }

    @Override
    public void animate() {
        if (ticks == 0 && mode == FiremakingMode.BARBARIAN) {
            ContentAPIKt.visualize(player, animation, graphics);
        }
    }

    @Override
    public boolean reward() {
        if (getLastFire() >= GameWorld.getTicks()) {
            createFire();
            return true;
        }

        if (ticks == 0 && mode == FiremakingMode.STANDARD) {
            player.animate(animation);
        }

        if (++ticks % 3 != 0) return false;

        if (ticks % 12 == 0 && mode == FiremakingMode.STANDARD) {
            player.animate(animation);
        }

        if (!success()) return false;

        createFire();
        return true;
    }

    private boolean success() {
        int level = 1 + player.getSkills().getLevel(Skills.FIREMAKING);
        double req =
                mode == FiremakingMode.BARBARIAN
                        ? fire.getBarbarianLevel()
                        : fire.getDefaultLevel();

        double successChance =
                Math.ceil((level * 50 - req * 15) / req / 3 * 4);

        return successChance >= RandomFunction.random(99);
    }

    private void createFire() {
        if (!groundItem.isActive()) return;

        Scenery scenery = new Scenery(fire.getFireId(), player.getLocation());

        SceneryBuilder.add(scenery, fire.getLife(), () -> {
            GroundItemManager.create(
                    new GroundItem(
                            new Item(Items.ASHES_592),
                            scenery.getLocation(),
                            player
                    )
            );
        });

        GroundItemManager.destroy(groundItem);
        player.moveStep();
        player.faceLocation(scenery.getFaceLocation(player.getLocation()));
        player.getSkills().addExperience(Skills.FIREMAKING, fire.getXp());

        setLastFire();
        player.dispatch(new LitFireEvent(fire.getLogId()));

        if (mode == FiremakingMode.BARBARIAN
                && getAttribute(player, BarbarianTraining.INSTANCE.getFM_BASE(), false)) {
            removeAttribute(player, BarbarianTraining.INSTANCE.getFM_BASE());
            player.getSavedData().activityData.setBarbarianFiremaking(true);
            sendDialogueLines(
                    player,
                    "You feel you have learned more of barbarian ways.",
                    "Otto might wish to talk to you more."
            );
        }
    }

    private int getLastFire() {
        return player.getAttribute("last-firemake", 0);
    }

    private void setLastFire() {
        player.setAttribute("last-firemake", GameWorld.getTicks() + 2);
    }

    @Override
    public void message(int type) {
        String name =
                node.getId() == Items.JOGRE_BONES_3125 ? "bones" : "logs";

        if (type == 0) {
            player.getPacketDispatch()
                    .sendMessage("You attempt to light the " + name + "..");
        } else if (type == 1) {
            player.getPacketDispatch()
                    .sendMessage("The fire catches and the " + name + " begin to burn.");
        }
    }

    public static GroundItem getAsh(final Player player, Log fire, final Scenery scenery) {
        GroundItem ash = new GroundItem(new Item(Items.ASHES_592), scenery.getLocation(), player);
        ash.setDecayTime(fire.getLife() + 200);
        return ash;
    }
}
