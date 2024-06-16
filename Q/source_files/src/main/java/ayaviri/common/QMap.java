package ayaviri.common;

import ayaviri.map_representation.Placement;
import ayaviri.map_representation.Posn;
import ayaviri.map_representation.Posn.TranslateDirection;
import ayaviri.map_representation.Posn.TranslateDown;
import ayaviri.map_representation.Posn.TranslateLeft;
import ayaviri.map_representation.Posn.TranslateRight;
import ayaviri.map_representation.Posn.TranslateUp;
import ayaviri.map_representation.RowColOrderComparator;
import ayaviri.map_representation.Tile;
import ayaviri.visuals.QMapImage;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// Represents a map for the Q game consisting of contiguous tiles
public class QMap {
    private final Map<Posn, Tile> tiles;

    public QMap(Tile tile) {
        this.tiles = new HashMap<Posn, Tile>();
        this.placeTile(new Placement(tile, new Posn(0, 0)));
    }

    // A copy constructor
    public QMap(QMap map) {
        this.tiles = map.getTiles();
    }

    // Assumes a map in which all tile placements are valid
    // Used for constructing a QMap from a JMap
    protected QMap(Map<Posn, Tile> tiles) {
        this.tiles = new HashMap<Posn, Tile>(tiles);
    }

    public static QMap fromJson(JsonArray jMap) {
        Map<Posn, Tile> tiles = new HashMap<Posn, Tile>();

        for (JsonElement row : jMap) {
            Iterator<JsonElement> rowIterator = row.getAsJsonArray().iterator();
            int rowIndex = rowIterator.next().getAsInt();
            while (rowIterator.hasNext()) {
                JsonArray cell = rowIterator.next().getAsJsonArray();
                int colIndex = cell.get(0).getAsInt();
                Tile tile = Tile.fromJson(cell.get(1).getAsJsonObject());
                Posn pos = new Posn(colIndex, rowIndex);
                tiles.put(pos, tile);
            }
        }

        return new QMap(tiles);
    }

    public JsonArray toJson() {
        List<Posn> posns = new ArrayList<>(this.tiles.keySet());
        posns.sort(new RowColOrderComparator());
        Iterator<Posn> mapPosnIterator = posns.iterator();
        JsonArray jMap = new JsonArray();
        JsonArray jRow = new JsonArray();
        int rowIndex = Integer.MAX_VALUE;

        while (mapPosnIterator.hasNext()) {
            Posn posn = mapPosnIterator.next();
            int row = posn.getY();
            int column = posn.getX();
            JsonElement jCell = constructJsonFromCellData(column, this.tiles.get(posn));
            if (row != rowIndex) {
                if (!jRow.isEmpty()) {
                    jMap.add(jRow);
                }
                jRow = new JsonArray();
                jRow.add(row);
                rowIndex = row;
            }
            jRow.add(jCell);
        }
        if (!jRow.isEmpty()) {
            jMap.add(jRow); // add last jRow to jMap
        }
        return jMap;
    }

    protected static JsonElement constructJsonFromCellData(int columnIndex, Tile tile) {
        JsonArray jCell = new JsonArray();
        jCell.add(columnIndex);
        JsonElement jTile = Tile.toJson(tile);
        jCell.add(jTile);
        return jCell;
    }

    public QMapImage visualize() {
        return new QMapImage(this.tiles);
    }

    // Performs the given placement, if it maintains the contiguity invariant of this map
    public void placeTile(Placement placement) {
        Tile tile = placement.getTile();
        Posn pos = placement.getPosition();

        if (!this.isValidPlacementByPosition(pos) && !this.tiles.isEmpty()) {
            throw new RuntimeException(
                    String.format("Placement of tile %s at position %s is invalid", tile, pos));
        }

        this.tiles.put(pos, tile);
    }

    // Returns a set of positions where the given tile can be placed according to the Q Game
    // matching rules
    public Set<Posn> getValidTilePositions(Tile tile) {
        Set<Posn> validPositions = new HashSet<Posn>();

        for (Posn position : this.tiles.keySet()) {
            Tile currentTile = this.tiles.get(position);

            if (tile.matchesShape(currentTile) || tile.matchesColor(currentTile)) {
                List<Posn> candidatePositions = position.getNeighboringPositions();
                this.addValidCandidatePositions(tile, candidatePositions, validPositions);
            }
        }

        return validPositions;
    }

    public Map<Posn, Tile> getTiles() {
        return new HashMap<Posn, Tile>(this.tiles);
    }

    public int getNumberOfNeighbors(Posn position) {
        return this.getPresentNeighbors(position).size();
    }

    public String toString() {
        return this.tiles.toString();
    }

    public Optional<Tile> getTileAt(Posn pos) {
        return this.hasTileAt(pos) ? Optional.of(this.tiles.get(pos)) : Optional.empty();
    }

    // Gets the positions of the contiguous row of tiles containing the given position
    protected Set<Posn> getContiguousRow(Posn pos) {
        return this.getContiguousSequence(pos, new TranslateRight(), new TranslateLeft());
    }

    // Gets the positions of the contiguous column of tiles containing the given position
    protected Set<Posn> getContiguousColumn(Posn pos) {
        return this.getContiguousSequence(pos, new TranslateUp(), new TranslateDown());
    }

    // Gets the positions of the contiguous sequence of tiles in the given 2 directions from the
    // given position
    // The returned set DOES contain the given position, as it is a part of the contiguous sequence
    private Set<Posn> getContiguousSequence(
            Posn pos, TranslateDirection firstDirection, TranslateDirection secondDirection) {
        Set<Posn> contiguousSequence = new HashSet<Posn>();
        contiguousSequence.addAll(this.getContiguousNeighborsInDirection(pos, firstDirection));
        contiguousSequence.addAll(this.getContiguousNeighborsInDirection(pos, secondDirection));
        contiguousSequence.add(pos);
        return contiguousSequence;
    }

    // Gets the positions of the contiguous neighbor tiles in the given 1 direction from the given
    // position
    // The returned list does NOT contain the given position, only its neighbors
    private List<Posn> getContiguousNeighborsInDirection(Posn pos, TranslateDirection direction) {
        List<Posn> contiguousNeighbors = new ArrayList<Posn>();
        int distance = 1;
        Optional<Tile> neighbor;

        do {
            Posn sequencePos = direction.apply(pos, distance);
            neighbor = this.getTileAt(sequencePos);
            distance += 1;

            if (neighbor.isPresent()) {
                contiguousNeighbors.add(sequencePos);
            }
        } while (neighbor.isPresent());

        return contiguousNeighbors;
    }

    // Adds positions that are valid for the given tile from the given list of candidates to the
    // given accumulator _validPositions_
    // Also populates the _visitedCandidatePositions_ accumulator with all visited candidate
    // positions
    private void addValidCandidatePositions(
            Tile tile, List<Posn> candidatePositions, Set<Posn> validPositions) {
        for (Posn candidatePosition : candidatePositions) {
            if (!this.hasTileAt(candidatePosition)
                    && this.isValidPlacementByColorShape(tile, candidatePosition)) {
                validPositions.add(candidatePosition);
            }
        }
    }

    // Returns the list of neighboring positions (on which tiles are present) for the given position
    public List<Posn> getPresentNeighbors(Posn pos) {
        return this.getPresentPositions(pos.getNeighboringPositions());
    }

    // Returns the list of neighboring positions on the same row (on which tiles are present) for
    // the given position
    private List<Posn> getRowNeighbors(Posn pos) {
        return this.getPresentPositions(pos.getNeighboringRowPositions());
    }

    // Returns the list of neighboring positions on the same column (on which tiles are present) for
    // the given position
    private List<Posn> getColumnNeighbors(Posn pos) {
        return this.getPresentPositions(pos.getNeighboringColumnPositions());
    }

    // Returns a filtered list of positions from the given list on which tiles are present on this
    // map
    private List<Posn> getPresentPositions(List<Posn> positions) {
        List<Posn> presentPositions = new ArrayList<Posn>();

        for (Posn position : positions) {
            if (this.hasTileAt(position)) {
                presentPositions.add(position);
            }
        }

        return presentPositions;
    }

    // Returns a list of tiles that are neighbors of the given position on this map
    public List<Tile> getNeighboringTiles(Posn pos) {
        List<Tile> neighboringTiles = new ArrayList<>();
        List<Posn> neighboringPositions = this.getPresentNeighbors(pos);

        for (Posn neighboringPosition : neighboringPositions) {
            Tile tile = this.getTileAt(neighboringPosition).get();
            neighboringTiles.add(tile);
        }

        return neighboringTiles;
    }

    private boolean hasTileAdjacent(Posn pos) {
        return !this.getPresentNeighbors(pos).isEmpty();
    }

    private boolean hasTileAt(Posn pos) {
        return this.tiles.containsKey(pos);
    }

    // Determines whether the given tile can be placed in the given position based on its color and
    // shape
    // Assumes that the placement is valid by position
    private boolean isValidPlacementByColorShape(Tile tile, Posn pos) {
        List<Posn> rowNeighborPositions = this.getRowNeighbors(pos);
        List<Posn> columnNeighborPositions = this.getColumnNeighbors(pos);

        return this.isValidPlacementByColorShapeGroup(tile, rowNeighborPositions)
                && this.isValidPlacementByColorShapeGroup(tile, columnNeighborPositions);
    }

    // Assumes that each position in the list has a tile on it
    private boolean isValidPlacementByColorShapeGroup(Tile tile, List<Posn> neighboringPositions) {
        boolean allSameColor = true;
        boolean allSameShape = true;

        for (Posn neighboringPosition : neighboringPositions) {
            Tile neighborTile = this.getTileAt(neighboringPosition).get();
            allSameColor = allSameColor && tile.matchesColor(neighborTile);
            allSameShape = allSameShape && tile.matchesShape(neighborTile);

            if (!allSameColor && !allSameShape) {
                return false;
            }
        }
        return true;
    }

    // Determines whether the given tile can be placed in the given position based strictly on its
    // position:
    // Must be an empty position AND share a side with an existing tile.
    private boolean isValidPlacementByPosition(Posn pos) {
        return !this.hasTileAt(pos) && this.hasTileAdjacent(pos);
    }
}
