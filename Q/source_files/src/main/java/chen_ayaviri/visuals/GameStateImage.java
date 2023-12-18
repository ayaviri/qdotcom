package chen_ayaviri.visuals;

public class GameStateImage {
    private final QMapImage qMap;
    private final PlayersImage playerStates;
    private final int tilesLeft;

    public GameStateImage(QMapImage qMap, PlayersImage playerStates, int tilesLeft) {
        this.qMap = qMap;
        this.playerStates = playerStates;
        this.tilesLeft = tilesLeft;
    }

    public QMapImage qMap() {
        return this.qMap;
    }

    public PlayersImage playerStates() {
        return this.playerStates;
    }

    public int tilesLeft() {
        return this.tilesLeft;
    }
}
