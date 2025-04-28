import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

public class TreasureChest {
    private int x, y;
    private boolean opened;
    private Animation animation;
    //private Image chestImage;
    private Rectangle2D.Double boundingBox;

    public TreasureChest(int x, int y) {
        this.x = x;
        this.y = y;
        this.opened = false;
        loadAnimation();
        //this.chestImage = new ImageIcon("images/chest.png").getImage();
        this.boundingBox = new Rectangle2D.Double(x, y, 95,108);
    }

    private void loadAnimation() {
        animation = new Animation(true);
        Image[] frames = {
            ImageManager.loadImage("images/tc1.png"),
            ImageManager.loadImage("images/tc2.png"),
            ImageManager.loadImage("images/tc3.png"),
            ImageManager.loadImage("images/tc10.png"),
            ImageManager.loadImage("images/tc4.png"),
            ImageManager.loadImage("images/tc5.png"),
            ImageManager.loadImage("images/tc6.png"),
            ImageManager.loadImage("images/tc7.png"),
            ImageManager.loadImage("images/tc8.png"),
            ImageManager.loadImage("images/tc9.png"),
            ImageManager.loadImage("images/tc8.png"),
            ImageManager.loadImage("images/tc7.png"),
            ImageManager.loadImage("images/tc6.png"),
            ImageManager.loadImage("images/tc5.png"),
            ImageManager.loadImage("images/tc4.png"),
            ImageManager.loadImage("images/tc10.png"),
            ImageManager.loadImage("images/tc3.png"),
            ImageManager.loadImage("images/tc2.png"),
            ImageManager.loadImage("images/tc1.png"),
        };
        for (Image frame : frames) {
            animation.addFrame(frame, 200);
        }
        animation.start();
    }

    public void update() {
        if (!opened)
            animation.update();
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if (!opened) {
            g2.drawImage(animation.getImage(), x + offsetX, y + offsetY, 95, 108, null);
        }
    }

    public boolean checkCollision(Rectangle2D.Double playerRect) {
        return !opened && boundingBox.intersects(playerRect);
    }

    public void open() {
        opened = true;
    }

    public boolean isOpened() {
        return opened;
    }
}