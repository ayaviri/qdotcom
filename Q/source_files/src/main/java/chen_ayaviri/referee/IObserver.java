package chen_ayaviri.referee;

import chen_ayaviri.common.GameState;

public interface IObserver {
    /**
     * Receive a state.
     */
    void receive(GameState state);

    /**
     * Game over
     */
    void gameOver();
}
