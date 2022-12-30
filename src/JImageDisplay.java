import javax.swing.*;
import java.awt.*;
import  java.awt.image.BufferedImage;

public class JImageDisplay extends JComponent{
    public BufferedImage picture;

    // инициализирует объект BufferedImage
    public JImageDisplay(int width, int hight) {
        this.picture = new BufferedImage(width, hight, BufferedImage.TYPE_INT_RGB);
        Dimension preferredSize = new Dimension(width, hight);
        super.setPreferredSize(preferredSize);
    }

    //Отрисовка изображения
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage (picture, 0, 0, picture.getWidth(), picture.getHeight(), null);
    }

    //все пиксели изображения в черный цвет
    public void clearImage() {
        picture.setRGB(picture.getWidth(), picture.getHeight(), 0);
    }

    // пиксель в определенный цвет
    public void drawPixel(int x, int y, int rgbColor) {
        picture.setRGB(x, y, rgbColor);
    }

}