package chen_ayaviri.strategy;

import chen_ayaviri.common.ActivePlayerInfo;
import chen_ayaviri.common.GameState;
import chen_ayaviri.common.TurnAction;

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
