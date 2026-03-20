package core.game.node.entity.player.link.warning;

import core.game.node.entity.player.Player;

@FunctionalInterface
public interface WarningAction {
    void execute(Player player);
}