package chen_ayaviri.visuals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PlayersRenderer {

  public static BufferedImage generateImage(PlayersImage players) {
    return ImageUtil.joinHorizontal(drawCurrentPlayerArrow(players.current()),
        ImageUtil.joinHorizontal(drawNames(players),
            ImageUtil.joinHorizontal(drawScores(players), drawTiles(players))));
  }

  private static BufferedImage drawStringImage(String text, Float fontsize, int width, int height) {
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gtd = bi.createGraphics();

    gtd.setColor(Color.BLACK);

    ImageUtil.setFontSize(fontsize, gtd);
    gtd.drawString(text, 5, 25);

    return bi;
  }

  private static BufferedImage drawCurrentPlayerArrow(int current) {

    BufferedImage bi = new BufferedImage(20, (2 + current) * 35, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gtd = bi.createGraphics();

    gtd.setColor(Color.RED);

    ImageUtil.setFontSize(24F, gtd);
    gtd.drawString(">", 5, bi.getHeight() - 10);

    return bi;
  }


  private static BufferedImage drawNames(PlayersImage players) {
    BufferedImage header = drawStringImage("ID", 12F, 100, 30);
    BufferedImage names = players.states()
        .stream()
        .map(PlayerStateImage::id)
        .map(id -> drawStringImage(id, 12F, 100, 35))
        .reduce(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            ImageUtil::joinVertical);

    return ImageUtil.joinVertical(header, names);
  }

  private static BufferedImage drawScores(PlayersImage players) {
    BufferedImage header = drawStringImage("score", 12F, 50, 30);
    BufferedImage scores = players.states()
        .stream()
        .map(PlayerStateImage::score)
        .map(score -> Integer.toString(score))
        .map(score -> drawStringImage(score, 12F, 50, 35))
        .reduce(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            ImageUtil::joinVertical);

    return ImageUtil.joinVertical(header, scores);
  }

  private static BufferedImage drawTiles(PlayersImage players) {
    BufferedImage header = drawStringImage("tiles", 12F, 50, 35);
    BufferedImage tiles = players.states()
        .stream()
        .map(PlayerStateImage::tiles)
        .map(tile -> TileRenderer.renderRow(tile, 30, 5, true))
        .reduce(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            (combined, row) -> ImageUtil.joinVertical(combined, row, 5));

    return ImageUtil.joinVertical(header, tiles);
  }

}
