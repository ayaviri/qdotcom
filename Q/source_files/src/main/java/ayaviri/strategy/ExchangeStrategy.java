package ayaviri.strategy;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.common.GameState;
import ayaviri.common.TurnAction;

public class ExchangeStrategy implements Strategy {
    @Override
    public TurnAction computeTurnAction(ActivePlayerInfo currentState) {
        return new GameState.ExchangeAction();
    }

    @Override
    public TurnAction computeIteratedTurnAction(ActivePlayerInfo currentState) {
        return new GameState.ExchangeAction();
    }
}
