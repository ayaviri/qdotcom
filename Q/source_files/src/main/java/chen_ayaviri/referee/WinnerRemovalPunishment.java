package chen_ayaviri.referee;

import java.util.function.Supplier;
import java.util.List;

// Represents a failure callback in which the given player name is removed
// from the given list of winners
public class WinnerRemovalPunishment implements Supplier<Void> {
    private final List<String> winners;
    private final String playerName;

    public WinnerRemovalPunishment(List<String> winners, String playerName) {
        this.winners = winners;
        this.playerName = playerName;
    }

    public Void get() {
        this.winners.remove(this.playerName);
        return null;
    }
}
