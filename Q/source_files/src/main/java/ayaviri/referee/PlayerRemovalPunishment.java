package ayaviri.referee;

import java.util.function.Supplier;

// Represents a function object that performs the removal of the given
// player from the game in progress by the given referee by calling its
// _eliminatePlayer_ method
// NOTE: relies on the assumption that the given name corresponds to an
// exisiting player in the game
public class PlayerRemovalPunishment implements Supplier<Void> {
    private final Referee referee;
    private final String playerName;

    public PlayerRemovalPunishment(Referee referee, String playerName) {
        this.referee = referee;
        this.playerName = playerName;
    }

    public Void get() {
        this.referee.eliminatePlayer(this.playerName);
        return null;
    }
}
