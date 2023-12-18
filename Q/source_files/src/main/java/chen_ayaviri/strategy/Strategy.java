package chen_ayaviri.strategy;

import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.TurnAction;

// Represents a game playing strategy for the Q game that computes the next turn action based on the given state of the game
// TODO: add prepend the letter I to this interface's name
public interface Strategy {
    // Given the state of the game, computes either a single placement, a pass, or an exchange
    TurnAction computeTurnAction(ActivePlayerInfo currentState);

    // Given the state of the game, computes an exhaustive sequence of placements, a pass, or an exchange
    TurnAction computeIteratedTurnAction(ActivePlayerInfo currentState);

    static Strategy fromJson(String json) {
        switch (json) {
            case "dag":
                return new DAG();
            case "ldasg":
                return new LDASG();
            default:
                throw new UnsupportedOperationException(String.format("%s not a supported strategy", json));
        }
    }
}
