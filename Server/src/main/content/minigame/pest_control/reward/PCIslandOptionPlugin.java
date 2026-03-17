package content.minigame.pest_control.reward;

import core.cache.def.impl.NPCDefinition;
import core.game.interaction.OptionHandler;
import core.game.node.Node;
import core.game.node.entity.player.Player;
import core.plugin.ClassScanner;
import core.plugin.Initializable;
import core.plugin.Plugin;
import shared.consts.NPCs;

/**
 * Represents the option plugin used to handle the pc island related nodes.
 * @author 'Vexia
 */
@Initializable
public final class PCIslandOptionPlugin extends OptionHandler {

	@Override
	public Plugin<Object> newInstance(Object arg) throws Throwable {
		for (int id : new int[] {
				NPCs.VOID_KNIGHT_3786,
				NPCs.VOID_KNIGHT_3788,
				NPCs.VOID_KNIGHT_3789,
				NPCs.VOID_KNIGHT_5956
		}) {
			NPCDefinition.forId(id).getHandlers().put("option:exchange", this);
		}
		ClassScanner.definePlugin(new PCRewardInterface());
		return this;
	}

	@Override
	public boolean handle(Player player, Node node, String option) {
		switch (option) {
		case "exchange":
			PCRewardInterface.open(player);
			break;
		}
		return true;
	}

}
