package ayaviri.visuals;

import java.util.List;

// TODO: refactor this to simply take in the Players class
public class PlayersImage {
    private final List<PlayerStateImage> states;
    private final int current;

    public PlayersImage(List<PlayerStateImage> playerStates, int current) {
        this.states = playerStates;
        this.current = current;
    }

    public List<PlayerStateImage> states() {
        return this.states;
    }

    public int current() {
        return this.current;
    }
}
