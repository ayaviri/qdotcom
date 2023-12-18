package chen_ayaviri.visuals;

import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.Posn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a visualizaition of a map for the Q game.
 */
public class QMapImage {

  private final Map<Posn, Tile> tiles;
  private final QMapBounds bounds;

  /**
   * Constructs a QMapImage.
   *
   * @param tiles board to base the image on.
   */
  public QMapImage(Map<Posn, Tile> tiles) {
    this.tiles = tiles;
    this.bounds = QMapBounds.create(tiles);
  }


  public QMapBounds getBounds() {
    return bounds;
  }

  /**
   * Returns a grid representation of the map, where the coordinate system derives from the
   * following: - The top left is at (0, 0) - Row 0 contains the topmost tile. - Col 0 contains the
   * leftmost tile. - The bottom right is at (N - 1, N - 1) - Row N - 1 contains the bottom-most
   * tile. - Col N - 1 contains the rightmost tile.
   *
   * @param padded
   * @return a grid representation of the map,
   */
  public List<List<Optional<Tile>>> getGrid(int padded) {
    List<List<Optional<Tile>>> grid = initializeGrid(bounds.numRows() + 2 * padded,
        bounds.numCols() + 2 * padded);
    for (Map.Entry<Posn, Tile> entry : tiles.entrySet()) {
      Posn place = entry.getKey();
      Tile tile = entry.getValue();
      int r = -place.getY() + bounds.maxY + padded;
      int c = place.getX() - bounds.minX + padded;
      grid.get(r).set(c, Optional.of(tile));
    }
    return grid;
  }

  // Return the tile at the coordinates (x, y), where
  // (0, 0) is the location of the referee's tile.

  /**
   * Return the tile at the coordinates (x, y),
   *
   * @param x row
   * @param y column
   * @return an Optional of Tile
   */
  public Optional<Tile> getXY(int x, int y) {
    Posn pos = new Posn(x, y);
    if (this.tiles.containsKey(pos)) {
      return Optional.of(tiles.get(pos));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Initializes an m by n grid of empty optionals.
   *
   * @param m number of rows
   * @param n number of columns
   * @return a grid of empty optionals
   */
  private List<List<Optional<Tile>>> initializeGrid(int m, int n) {
    List<List<Optional<Tile>>> grid = new ArrayList<>();
    for (int i = 0; i < m; i++) {
      List<Optional<Tile>> row = new ArrayList<>();
      for (int j = 0; j < n; j++) {
        row.add(Optional.empty());
      }
      grid.add(row);
    }
    return grid;
  }
}
