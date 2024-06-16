package ayaviri.map_representation;

import ayaviri.map_representation.shapes.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.*;

// Represents a tile with a shape and color
public class Tile {
    private final QColor color;
    private final AShape shape;
    public static final double SIZE = 50; // the size of each tile (rendered as a shape) in pixels

    public Tile(String color, String shape) {
        this.color = this.getQColorFromString(color);
        this.shape = this.getShapeFromString(shape);
    }

    public Tile(QColor color, AShape shape) {
        this.color = color;
        this.shape = shape;
    }

    public static Tile fromJson(JsonObject jTile) {
        String color = jTile.get("color").getAsString();
        String shape = jTile.get("shape").getAsString();
        return new Tile(color, shape);
    }

    // TODO: why is this static ?
    public static JsonElement toJson(Tile tile) {
        JsonObject jTile = new JsonObject();
        jTile.addProperty("color", tile.getColorString());
        jTile.addProperty("shape", tile.getShapeString());
        return jTile;
    }

    private QColor getQColorFromString(String color) {
        switch (color) {
            case "red":
                return QColor.RED;
            case "green":
                return QColor.GREEN;
            case "blue":
                return QColor.BLUE;
            case "yellow":
                return QColor.YELLOW;
            case "orange":
                return QColor.ORANGE;
            case "purple":
                return QColor.PURPLE;
            default:
                throw new UnsupportedOperationException(
                        String.format("The given color is not supported: %s\n", color));
        }
    }

    private Color getColorFromQColor(QColor color) {
        switch (color) {
            case RED:
                return Color.RED;
            case GREEN:
                return Color.GREEN;
            case BLUE:
                return Color.BLUE;
            case YELLOW:
                return Color.YELLOW;
            case ORANGE:
                return Color.ORANGE;
            case PURPLE:
                return Color.MAGENTA;
            default:
                throw new UnsupportedOperationException(
                        String.format("The given color is not supported: %s\n", color));
        }
    }

    private AShape getShapeFromString(String shape) {
        switch (shape) {
            case "star":
                return new Star();
            case "8star":
                return new EightStar();
            case "square":
                return new Square();
            case "circle":
                return new Circle();
            case "diamond":
                return new Diamond();
            case "clover":
                return new Clover();
            default:
                throw new UnsupportedOperationException(
                        String.format("The given shape is not supported: %s\n", shape));
        }
    }

    public QColor getColor() {
        return this.color;
    }

    public Color getAWTColor() {
        return this.getColorFromQColor(this.color);
    }

    public String getColorString() {
        return this.color.toString().toLowerCase();
    }

    public AShape getShape() {
        return this.shape;
    }

    public String getShapeString() {
        return this.shape.toString();
    }

    // Returns set of all QColor
    public static Set<QColor> getAllColors() {
        return new HashSet<QColor>(Arrays.asList(QColor.values()));
    }

    // Returns set of all IShape
    public static Set<AShape> getAllShapes() {
        return new HashSet<>(
                Arrays.asList(
                        new Star(),
                        new EightStar(),
                        new Square(),
                        new Circle(),
                        new Diamond(),
                        new Clover()));
    }

    // Returns a set of all distinct Tiles
    public static Set<Tile> getAllDistinct() {
        Set<Tile> allDistinctTiles = new HashSet<>();

        for (QColor color : Tile.getAllColors()) {
            for (AShape shape : Tile.getAllShapes()) {
                allDistinctTiles.add(new Tile(color, shape));
            }
        }

        return allDistinctTiles;
    }

    public boolean matchesColor(Tile other) {
        return this.getColor().equals(other.getColor());
    }

    public boolean matchesShape(Tile other) {
        return this.getShape().equals(other.getShape());
    }

    // Returns true if this tile fails to match BOTH SHAPE AND COLOR
    // for ANY the tiles in the given list
    public boolean doesNotMatchAtLeastOne(List<Tile> tiles) {
        for (Tile tile : tiles) {
            if (!this.matchesShape(tile) && !this.matchesColor(tile)) {
                return true;
            }
        }

        return false;
    }

    public boolean equals(Object other) {
        return other instanceof Tile
                && this.matchesColor((Tile) other)
                && this.matchesShape((Tile) other);
    }

    public int hashCode() {
        return Objects.hash(this.color, this.shape);
    }

    public String toString() {
        return String.format("{shape: %s, color: %s}", this.getShape(), this.getColor());
    }

    public enum QColor {
        RED,
        GREEN,
        BLUE,
        YELLOW,
        ORANGE,
        PURPLE;
    }

    public static Set<Tile> getTilesNotMatchingTile(Tile t) {
        Set<Tile> allDistinct = getAllDistinct();
        // remove tiles from all tiles set that have shape or color equal to neighboringTile (6 + 5)
        allDistinct.removeAll(getTilesNotMatchingShapeOfTile(t));
        allDistinct.removeAll(getTilesNotMatchingColorOfTile(t));
        return allDistinct;
    }

    private static List<Tile> getTilesNotMatchingShapeOfTile(Tile t) {

        List<Tile> tilesWithShapeOfT = new ArrayList<>();
        Set<AShape> shapes = getAllShapes();
        shapes.remove(t.shape);

        for (AShape shape : shapes) {
            tilesWithShapeOfT.add(new Tile(t.color, shape));
        }
        return tilesWithShapeOfT;
    }

    // even if we used a stream + filter, we would separate it into a private helper anyway, nerd
    private static List<Tile> getTilesNotMatchingColorOfTile(Tile t) {

        List<Tile> tilesWithColorOfT = new ArrayList<>();
        Set<QColor> colors = getAllColors();
        colors.remove(t.color);

        for (QColor color : colors) {
            tilesWithColorOfT.add(new Tile(color, t.shape));
        }
        return tilesWithColorOfT;
    }
    // getTilesNotMatchingAttributeOfTile???????? abstraction????
}
