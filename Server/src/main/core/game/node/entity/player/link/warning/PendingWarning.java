package core.game.node.entity.player.link.warning;

public class PendingWarning {
    private final WarningType warning;
    private final WarningAction action;

    public PendingWarning(WarningType warning, WarningAction action) {
        this.warning = warning;
        this.action = action;
    }

    public WarningType getWarning() {
        return warning;
    }

    public WarningAction getAction() {
        return action;
    }
}