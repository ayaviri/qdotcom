package chen_ayaviri.visuals;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameStateRenderer {


  //TODO: scroll panel for rendering very large boards
  // clean up
  public static void render(GameStateImage gameStateImage) {
    JFrame f = new JFrame();

    JPanel contents = new JPanel();
    contents.setLayout(new BoxLayout(contents, BoxLayout.PAGE_AXIS));

    renderComponent(contents, mergeImagePlayerStatesTilesLeft(gameStateImage), 0, 0,
        600, 250);
    contents.add(Box.createRigidArea(new Dimension(0, 50)));
    renderComponent(contents, QMapRenderer.generateImage(gameStateImage.qMap(), 1), 50, 0,
        600, 600);

    f.add(contents);
    f.setSize(800, 800);
    f.setVisible(true);
  }

  public static BufferedImage getAsBufferedImage(GameStateImage gameStateImage) {
      return ImageUtil.joinVertical(
            mergeImagePlayerStatesTilesLeft(gameStateImage), 
            QMapRenderer.generateImage(gameStateImage.qMap(), 1)
        );
  }

  private static void renderComponent(JPanel panel, BufferedImage component, int x, int y,
      int maxWidth, int maxHeight) {
    double scale = Math.min(1.0, Math.min((double) maxWidth / component.getWidth(),
        (double) maxHeight / component.getHeight()));
    int width = (int) (component.getWidth() * scale);
    int height = (int) (component.getHeight() * scale);

    JPanel image = new JPanel() {
      @Override
      public void paint(Graphics g) {
        g.drawImage(component, x, y, width, height, this);
      }
    };

    //image.setMinimumSize(new Dimension(width, height));
    image.setMaximumSize(new Dimension(width + x, height + y));
    image.setAlignmentX(Component.LEFT_ALIGNMENT);

    //panel.setPreferredSize(new Dimension(width, height));

    //image.setMaximumSize(new Dimension(500, 200));

    panel.add(image);
  }

  private static BufferedImage mergeImagePlayerStatesTilesLeft(GameStateImage gameStateImage) {
    return ImageUtil.joinHorizontal(PlayersRenderer.generateImage(gameStateImage.playerStates()),
        generateTilesLeftImage(gameStateImage.tilesLeft()));
  }

  private static BufferedImage generateTilesLeftImage(int tilesRemaining) {

    BufferedImage bi = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gtd = bi.createGraphics();

    gtd.setColor(Color.BLACK);

    ImageUtil.setFontSize(12F, gtd);
    gtd.drawString("tiles remaining:", 50, 25);
    gtd.drawString(String.valueOf(tilesRemaining), 75, 40);

    return bi;
  }

}
