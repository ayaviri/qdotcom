package ayaviri.referee;

import ayaviri.common.TurnAction;
import java.util.ArrayList;
import java.util.List;

// Represents the results of a round, indicating whether a game ending turn
// occurred during this round and containing a list of turn actions
public class RoundResult {
    private final boolean hadGameEndingTurn;
    private final List<TurnAction> turnActions;

    public RoundResult(boolean hadGameEndingTurn, List<TurnAction> turnActions) {
        this.hadGameEndingTurn = hadGameEndingTurn;
        this.turnActions = new ArrayList<>(turnActions);
    }

    public boolean hadGameEndingTurn() {
        return this.hadGameEndingTurn;
    }

    public List<TurnAction> getTurnActions() {
        return new ArrayList<>(this.turnActions);
    }
}
