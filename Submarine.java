import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

public class Submarine {
    private int x, y;
    private boolean boarded;
    private Animation animation;
    private Image subImage;
    private Rectangle2D.Double boundingBox;
    int offsetX, offsetY;

    public Submarine(int x, int y) {
        this.x = x;
        this.y = y;
        this.boarded = false;
        this.subImage = new ImageIcon("images/submarine.png").getImage();
        //this.boundingBox = new Rectangle2D.Double(x, y, 500,500);
    }

    public void update() {
        if (!boarded)
            animation.update();
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if (!boarded) {
            g2.drawImage(subImage, x + offsetX, y + offsetY, 450, 450, null);
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }

    public boolean checkCollision(Rectangle2D.Double playerRect) {
        boundingBox = new Rectangle2D.Double(x, y, 450,450);
        return !boarded && boundingBox.intersects(playerRect);
    }

    public void board() {
        boarded = true;
    }

    public boolean isboarded() {
        return boarded;
    }
}