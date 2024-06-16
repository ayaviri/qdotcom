package ayaviri.player.cheaters;

import ayaviri.common.ActivePlayerInfo;
import ayaviri.common.TurnAction;
import ayaviri.player.LocalPlayer;
import ayaviri.strategy.Strategy;

public abstract class ACheatingPlayer extends LocalPlayer {
    public ACheatingPlayer(String name, Strategy strategy) {
        super(name, strategy);
    }

    public TurnAction takeTurn(ActivePlayerInfo currentState) {
        if (this.canCheat(currentState)) {
            return this.thenCheat(currentState);
        } else {
            return super.takeTurn(currentState);
        }
    }

    // Determines whether this player can perform its cheat given the current game state
    protected abstract boolean canCheat(ActivePlayerInfo currentState);

    // Returns the cheating TurnAction based on this player's cheat using the given game state
    // NOTE: Assumes this player CAN perform their cheat on the given game state
    protected abstract TurnAction thenCheat(ActivePlayerInfo currentState);
}
