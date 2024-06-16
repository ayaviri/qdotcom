package ayaviri.map_representation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

// Represents a position on a map with an X and Y coordinate
// X coordinates increase as one goes right, and Y coordinates increase as one goes down
// (computer graphics Cartesian coordinate system)
public class Posn {
    private final int x;
    private final int y;

    public Posn(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static JsonElement toJson(Posn posn) {
        JsonObject jCoord = new JsonObject();
        jCoord.addProperty("column", posn.getX());
        jCoord.addProperty("row", posn.getY());
        return jCoord;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean sameRow(Posn other) {
        return this.getY() == other.getY();
    }

    public boolean sameColumn(Posn other) {
        return this.getX() == other.getX();
    }

    // Returns a list of positions that neighbor (are adjacent to) this position
    public List<Posn> getNeighboringPositions() {
        List<Posn> list = this.getNeighboringRowPositions();
        list.addAll(this.getNeighboringColumnPositions());
        return list;
    }

    // Returns a list of positions that neighbor (are adjacent to) this position on same ROW
    public List<Posn> getNeighboringRowPositions() {
        return new ArrayList<Posn>(
                Arrays.asList(
                        new TranslateRight().apply(this, 1), new TranslateLeft().apply(this, 1)));
    }

    // Returns a list of positions that neighbor (are adjacent to) this position on same COLUMN
    public List<Posn> getNeighboringColumnPositions() {
        return new ArrayList<Posn>(
                Arrays.asList(
                        new TranslateUp().apply(this, 1), new TranslateDown().apply(this, 1)));
    }

    // Given a position, translates this position into a new position relative to that position as
    // if it were the origin
    public Posn translateRelativeTo(Posn origin) {
        return new Posn(this.x - origin.getX(), this.y - origin.getY());
    }

    public static List<TranslateDirection> getTranslationDirections() {
        return new ArrayList<TranslateDirection>(
                Arrays.asList(
                        new TranslateUp(),
                        new TranslateDown(),
                        new TranslateRight(),
                        new TranslateLeft()));
    }

    // Constructs a new position using this position's Y value and the minimum X value from this and
    // the given position
    public Posn computeNewWithSmallestX(Posn other) {
        return new Posn(Math.min(this.getX(), other.getX()), this.getY());
    }

    // Constructs a new position using this position's Y value and the minimum X value from this and
    // the given position
    public Posn computeNewWithLargestX(Posn other) {
        return new Posn(Math.max(this.getX(), other.getX()), this.getY());
    }

    // Returns the absolute value difference between this positions X value and the given position's
    // X value
    public int computeXDistance(Posn other) {
        return Math.abs(other.getX() - this.getX());
    }

    // Returns the absolute value difference between this positions Y value and the given position's
    // Y value
    public int computeYDistance(Posn other) {
        return Math.abs(other.getY() - this.getY());
    }

    public boolean equals(Object other) {
        return other instanceof Posn
                && this.x == ((Posn) other).getX()
                && this.y == ((Posn) other).getY();
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    public String toString() {
        return String.format("{X: %d, Y: %d}", this.x, this.y);
    }

    // Represents a function object that translates a given position _n_ units in a given direction
    public abstract static class TranslateDirection implements BiFunction<Posn, Integer, Posn> {}

    // Represents a function object that translates a given position _n_ units in the -Y (up)
    // direction
    public static class TranslateUp extends TranslateDirection {
        public Posn apply(Posn original, Integer units) {
            return new Posn(original.getX(), original.getY() - units);
        }
    }

    // Represents a function object that translates a given position _n_ units in the +Y (down)
    // direction
    public static class TranslateDown extends TranslateDirection {
        public Posn apply(Posn original, Integer units) {
            return new Posn(original.getX(), original.getY() + units);
        }
    }

    // Represents a function object that translates a given position _n_ units in the +X (right)
    // direction
    public static class TranslateRight extends TranslateDirection {
        public Posn apply(Posn original, Integer units) {
            return new Posn(original.getX() + units, original.getY());
        }
    }

    // Represents a function object that translates a given position _n_ units in the -X (left)
    // direction
    public static class TranslateLeft extends TranslateDirection {
        public Posn apply(Posn original, Integer units) {
            return new Posn(original.getX() - units, original.getY());
        }
    }
}
