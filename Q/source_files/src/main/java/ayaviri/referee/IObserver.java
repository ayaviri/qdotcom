package ayaviri.referee;

import ayaviri.common.GameState;

public interface IObserver {
    /** Receive a state. */
    void receive(GameState state);

    /** Game over */
    void gameOver();
}
