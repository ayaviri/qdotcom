package chen_ayaviri.visuals;

import chen_ayaviri.map_representation.Tile;
import chen_ayaviri.map_representation.shapes.AShape;
import chen_ayaviri.map_representation.shapes.Square;
import chen_ayaviri.map_representation.shapes.Circle;
import chen_ayaviri.map_representation.shapes.Diamond;
import chen_ayaviri.map_representation.shapes.Star;
import chen_ayaviri.map_representation.shapes.EightStar;
import chen_ayaviri.map_representation.shapes.Clover;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import javax.swing.JPanel;
//TODO cleanup
// Support for drawing position
public class TileRenderer {

  //TODO separate methods for render tile, render hidden tile (censored gs), render empty space...
  public static BufferedImage render(Optional<Tile> optionalTile, int size, boolean drawBorder) {
    BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gtd = bi.createGraphics();

    JPanel shape = new JPanel() {
      @Override
      public void paint(Graphics g) {
        g.drawImage(drawTile(optionalTile, drawBorder), 0, 0, size, size, this);
      }
    };
    //gtd.drawImage(drawTile(optionalTile), 0, 0, size, size, gtd);
    shape.paint(gtd);

    return bi;
  }

  //TODO: abstract this functionality to joinHorizontal(List<BufferedImage>)?
  public static BufferedImage renderRow(List<Optional<Tile>> tiles, int size, int spaceBetween, boolean drawBorder) {
    return tiles.stream()
        .map(tile -> TileRenderer.render(tile, size, drawBorder))
        .reduce(new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_ARGB),
            (accumulator, tile) -> ImageUtil.joinHorizontal(accumulator, tile, spaceBetween));
  }

  public static BufferedImage drawTile(Optional<Tile> optionalTile, boolean drawBorder) {
    BufferedImage bi = new BufferedImage(110, 110, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gtd = bi.createGraphics();

    gtd.translate(5, 5);

      gtd.setColor(Color.WHITE);
      gtd.fill(new Rectangle(100, 100));

     if (optionalTile.isPresent()) {

      gtd.setColor(Color.WHITE);
      gtd.fill(new Rectangle(100, 100));

      Tile tile = optionalTile.get();

      gtd.setColor(tile.getAWTColor());
        AShape shape = tile.getShape();

        if (shape instanceof Square) {
            gtd.fill(new Rectangle (5, 5, 90, 90));
        } else if (shape instanceof Circle) {
            gtd.translate(2, 2);
            gtd.fill(new Double(0, 0, 94, 94));
            gtd.translate(-2, -2);
        } else if (shape instanceof Diamond) {
            int[] x = {50, 96, 50, 2};
            int[] y = {4, 50, 96, 50};
            gtd.fill(new Polygon(x, y, 4));
        } else if (shape instanceof Star) {
            int[] x2 = {0, 50, 100, 74, 100, 50, 0, 26};
            int[] y2 = {0, 26, 0, 50, 100, 74, 100, 50};
            gtd.fill(new Polygon(x2, y2, 8));
        } else if (shape instanceof EightStar) {
            int[] x1 = {16, 40, 50, 60, 84, 70, 100, 70, 84, 60, 50, 40, 16, 30, 0, 30};
            int[] y1 = {16, 30, 0, 30, 16, 40, 50, 60, 84, 70, 100, 70, 84, 60, 50, 40};
            gtd.fill(new Polygon(x1, y1, 16));
        } else if (shape instanceof Clover) {
            gtd.translate(26, 26);
            gtd.fill(new Rectangle(48, 48));
            gtd.translate(-26, -26);
            gtd.fill(new Double(2, 24, 50, 50));
            gtd.fill(new Double(24, 2, 50, 50));
            gtd.fill(new Double(46, 24, 50, 50));
            gtd.fill(new Double(24, 46, 50, 50));
        }

//      gtd.setColor(convertColor(tile.getColor()));

//      switch (tile.getShape()) {
//          case SQUARE:
//              gtd.fill(new Rectangle (5, 5, 90, 90));
//              break;
//          case CIRCLE:
//            gtd.translate(2, 2);
//            gtd.fill(new Double(0, 0, 94, 94));
//            gtd.translate(-2, -2);
//            break;
//          case DIAMOND:
//            int[] x = {50, 96, 50, 2};
//            int[] y = {4, 50, 96, 50};
//            gtd.fill(new Polygon(x, y, 4));
//            break;
//          case STAR:
//            int[] x2 = {0, 50, 100, 74, 100, 50, 0, 26};
//            int[] y2 = {0, 26, 0, 50, 100, 74, 100, 50};
//            gtd.fill(new Polygon(x2, y2, 8));
//            break;
//          case EIGHT_STAR:
//            int[] x1 = {16, 40, 50, 60, 84, 70, 100, 70, 84, 60, 50, 40, 16, 30, 0, 30};
//            int[] y1 = {16, 30, 0, 30, 16, 40, 50, 60, 84, 70, 100, 70, 84, 60, 50, 40};
//            gtd.fill(new Polygon(x1, y1, 16));
//            break;
//          case CLOVER:
//            gtd.translate(26, 26);
//            gtd.fill(new Rectangle(48, 48));
//            gtd.translate(-26, -26);
//            gtd.fill(new Double(2, 24, 50, 50));
//            gtd.fill(new Double(24, 2, 50, 50));
//            gtd.fill(new Double(46, 24, 50, 50));
//            gtd.fill(new Double(24, 46, 50, 50));
//            break;
////          gtd.setColor(Color.BLACK);
////          ImageUtil.setFontSize(24F, gtd);
////          gtd.setFont(new Font("Dialog", Font.BOLD, 24));
////          gtd.drawString("(0,0)", 25, 50);
//          default:
//            gtd.fill(new Rectangle(100, 100));
//            gtd.setColor(Color.BLACK);
//            gtd.drawString("BAD", 25, 25);
//            break;
//      }
    }

    if (drawBorder) {
      gtd.translate(-5, -5);

      gtd.setColor(Color.BLACK);
      gtd.setStroke(new BasicStroke(8));
      gtd.draw(new Rectangle(110, 110));
    }

    return bi;
  }
}
