package chen_ayaviri.visuals;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Offers utility functions for rendering a QMap.
 */
public class QMapRenderer {

  /**
   * Renders a QMapImage using a JFrame
   * @param qMapImage
   */
  public static void render(QMapImage qMapImage) {
    JFrame f = new JFrame();
    JPanel shape = new JPanel() {
      @Override
      public void paint(Graphics g) {
        g.drawImage(generateImage(qMapImage, 0), 250, 250, this);
      }
    };
    f.add(shape);
    f.setSize(500, 500);
    f.setVisible(true);
  }

  //TODO: implement border-width logic
  //TODO: rendering larger board

  /**
   * Assembles a QMapImage into a single BufferedImage
   * @param qMapImage
   * @return
   */
  //TODO: factor out border size
  public static BufferedImage generateImage(QMapImage qMapImage, int padded) {
    return qMapImage.getGrid(padded)
        .stream()
        .map(tiles -> TileRenderer.renderRow(tiles, 70, -2, false))
        .reduce(new BufferedImage(1, 1,
            BufferedImage.TYPE_INT_ARGB), (map, row) -> (ImageUtil.joinVertical(map, row, -2)));
  }


}
