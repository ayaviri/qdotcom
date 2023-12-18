package chen_ayaviri.visuals;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class ImageUtil {

  public static BufferedImage resize(BufferedImage image, int width, int height) {
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gtd = bi.createGraphics();

    JPanel shape = new JPanel() {
      @Override
      public void paint(Graphics g) {
        g.drawImage(image,0, 0, width, height, this);
      }
    };

    shape.paint(gtd);

    return bi;
  }

  //TODO: factor out into utils class
  public static BufferedImage joinHorizontal(BufferedImage bi1, BufferedImage bi2, int spaceBetween) {
    if (bi1.getWidth() == 1) {
      return bi2;
    }
    BufferedImage newImage = new BufferedImage(bi1.getWidth() + bi2.getWidth() + spaceBetween,
        Math.max(bi1.getHeight(), bi2.getHeight()), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = newImage.createGraphics();
    g2.drawImage(bi1, null, 0, 0);
    g2.drawImage(bi2, null, bi1.getWidth() + spaceBetween, 0);
    g2.dispose();
    return newImage;
  }

  public static BufferedImage joinHorizontal(BufferedImage bi1, BufferedImage bi2) {
    return joinHorizontal(bi1, bi2, 0);
  }

  public static BufferedImage joinVertical(BufferedImage bi1, BufferedImage bi2, int spaceBetween) {
    if (bi1.getWidth() == 1) {
      return bi2;
    }
    BufferedImage newImage = new BufferedImage(Math.max(bi1.getWidth(), bi2.getWidth()),
        bi1.getHeight() + bi2.getHeight() + spaceBetween, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = newImage.createGraphics();
    g2.drawImage(bi1, null, 0, 0);
    g2.drawImage(bi2, null, 0, bi1.getHeight() + spaceBetween);
    g2.dispose();
    return newImage;
  }

  public static BufferedImage joinVertical(BufferedImage bi1, BufferedImage bi2) {
    return joinVertical(bi1, bi2, 0);
  }

  public static void setFontSize(Float size, Graphics2D gtd) {
    Font currentFont = gtd.getFont();
    Font newFont = currentFont.deriveFont(size);
    gtd.setFont(newFont);
  }

}
