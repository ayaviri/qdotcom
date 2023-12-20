package chen_ayaviri.referee;

import java.util.function.Supplier;

// Represents a success callback in which the player queue of the game
// state held by this object's referee is advanced or updated
public class PlayerQueueAdvancement implements Supplier<Void> {
    private final Referee referee;

    public PlayerQueueAdvancement(Referee referee) {
        this.referee = referee;
    }

    public Void get() {
        this.referee.advancePlayerQueue();
        return null;
    }
}
