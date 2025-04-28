import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Coin {
    private static final int ANIMATION_DELAY = 200;
    private int x, y;
    private boolean collected;
    private Animation animation;
    private Rectangle2D.Double boundingBox;
    private SoundManager soundManager;

    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
        this.collected = false;
        this.boundingBox = new Rectangle2D.Double(x, y, 36, 36);
        loadAnimation();
        soundManager = SoundManager.getInstance();
    }

    private void loadAnimation() {
        animation = new Animation(true);
        Image[] frames = {
            ImageManager.loadImage("images/coin1.png"),
            ImageManager.loadImage("images/coin2.png"),
            ImageManager.loadImage("images/coin3.png"),
            ImageManager.loadImage("images/coin4.png"),
            ImageManager.loadImage("images/coin5.png"),
            ImageManager.loadImage("images/coin4.png"),
            ImageManager.loadImage("images/coin3.png"),
            ImageManager.loadImage("images/coin2.png"),
            ImageManager.loadImage("images/coin1.png"),
        };
        for (Image frame : frames) {
            animation.addFrame(frame, ANIMATION_DELAY);
        }
        animation.start();
    }

    public void update() {
        if (!collected) {
            animation.update();
        }
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if (!collected) {
            g2.drawImage(animation.getImage(), 
                         x + offsetX, 
                         y, 
                         36, 36, null);
        }
    }

    public boolean checkCollision(Rectangle2D.Double playerRect) {
        return !collected && boundingBox.intersects(playerRect);
    }

    public void collect() {
        soundManager.playSound("coinSound", false);
        collected = true;
    }

    public void stopSound() {
        soundManager.stopSound("coinSound");
    }


}