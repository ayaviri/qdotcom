package chen_ayaviri.referee;

import java.util.function.Supplier;
import java.util.List;

// Represents a failure callback for the given player name who has survived up
// until the call to win. Removes them from the given list of winners if they've won
// or eliminates them if they've lost
public class MisbehavedFinisherPunishment implements Supplier<Void> {
    private final Referee referee;
    private final List<String> winners;
    private final String playerName;

    public MisbehavedFinisherPunishment(Referee referee, List<String> winners, String playerName) {
        this.referee = referee;
        this.winners = winners;
        this.playerName = playerName;
    }

    public Void get() {
        if (this.winners.contains(this.playerName)) {
            this.winners.remove(this.playerName);
        } else {
            this.referee.eliminatePlayer(this.playerName);
        }

        return null;
    }
}
