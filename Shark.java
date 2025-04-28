import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Random;
import java.awt.Image;

public class Shark {
    private JFrame window;

    private boolean canDamage = true;
    private long lastCollisionTime;
    private final long COOLDOWN_MS = 5000;

    private int x;
    private int y;

    private int width;
    private int height;

    private int originalX;
    private int originalY;

    private int dx; // increment to move along x-axis
    private int dy; // increment to move along y-axis

    private Dimension dimension;

    private Random random;

    private Player player;
    private SoundManager soundManager;
    private Image sharkImage;
    private Image sharkImageLeft;
    private Image sharkImageRight;
    private int topX;
    private boolean soundPlayed;

    Animation animation;
    int currentDirection;

    public boolean distracted = false;

    private long lastSpeedIncreaseTime = System.currentTimeMillis();
    private static final long SPEED_INTERVAL = 20000;
    private long distractionStartTime;
    private static final long DISTRACTION_DURATION = 5000;
    private int num;
    private boolean active = true;

    public Shark(JFrame w, int xPos, int yPos, Player player) {
        soundManager = SoundManager.getInstance();
        animation = new Animation(true);
        window = w;
        dimension = window.getSize();

        width = 200;
        height = 113;

        random = new Random();

        x = xPos;
        y = yPos;
        topX = xPos;

        dx = 5;
        dy = 5;

        this.player = player;
        sharkImageLeft = ImageManager.loadImage("images/SawSharkLeft.png");
        sharkImageRight = ImageManager.loadImage("images/SawSharkRight.png");
        sharkImage = sharkImageLeft;
        soundManager = SoundManager.getInstance();

        soundPlayed = false;
        Image[] frames = {
            ImageManager.loadImage("images/Shark1.png"),
            //ImageManager.loadImage("images/Shark2.png"),
            //ImageManager.loadImage("images/Shark3.png"),
            ImageManager.loadImage("images/Shark4.png"),
            ImageManager.loadImage("images/Shark5.png"),
            ImageManager.loadImage("images/Shark6.png"),
            ImageManager.loadImage("images/Shark7.png"),
            ImageManager.loadImage("images/Shark8.png"),
            ImageManager.loadImage("images/Shark7.png"),
            ImageManager.loadImage("images/Shark6.png"),
            ImageManager.loadImage("images/Shark5.png"),
            ImageManager.loadImage("images/Shark4.png"),
            //ImageManager.loadImage("images/Shark3.png"),
            //ImageManager.loadImage("images/Shark2.png"),
            ImageManager.loadImage("images/Shark1.png"),
        };

        for (Image frame : frames) {
            animation.addFrame(frame, 200);
        }

        animation.start();
        currentDirection = 2;
        canDamage = true;
        distracted = false;
        num = 0;
    }

    public void draw(Graphics2D g2, int offsetx, int offsety) {
        if (!animation.isStillActive())
            return;
        Image img = animation.getImage();

        if (currentDirection == 1) {
            g2.drawImage(img, x + width + offsetx, y + offsety, -width, height, null);
        } else {
            g2.drawImage(img, x + offsetx, y + offsety, width, height, null);
        }

        // g2.drawImage(sharkImage, x, y, width, height, null);

    }

    public void move() {

        if (!window.isVisible())
            return;

        if (System.currentTimeMillis() - lastSpeedIncreaseTime > SPEED_INTERVAL) {
            //increaseSharkSpeed();
            lastSpeedIncreaseTime = System.currentTimeMillis();
        }

        if (distracted &&
                System.currentTimeMillis() - distractionStartTime > DISTRACTION_DURATION) {
            distracted = false;
        }

        if (distracted) {
            return;
        } else {
            boolean collision = collidesWithFish();

            if (collision && canDamage) {
                // soundManager.playClip("sharkbite", false);
                // window.decreaseLives();
                increaseSharkSpeed();
                // window.decreaseSpeed();
                //num++;
                //canDamage = false;
                //lastCollisionTime = System.currentTimeMillis();
                System.out.println("Collision #: " + num);
                System.out.println("Shark Collision: " + lastCollisionTime);
                System.out.println("Shark location: " + x + ", " + y);
                System.out.println("Player location: " + player.getX() + ", " + player.getY());
                System.out.println("Distraction: " + distracted);
                System.out.println("Can Damage: " + canDamage + "\n");
                //System.out.println("Time Passed in seconds: " + ((System.currentTimeMillis() - lastCollisionTime) / 1000));
            }

            restoreDamage();

            if (x > player.getX() - player.getWidth() / 2) {
                x = x - dx;
                currentDirection = 1;
                // sharkImage = sharkImageLeft;
            } else if (x < player.getX() - player.getWidth() / 2) {
                x = x + dx;
                currentDirection = 2;
                // sharkImage = sharkImageRight;
            }

            if (y > player.getY() - player.getHeight() / 2) {
                y = y - dy;
            } else if (y < player.getY()) {
                y = y + dy;
            }
        }
    }

    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public boolean collidesWithFish() {
        if (!canDamage)
            return false;

        Rectangle2D.Double myRect = getBoundingRectangle();
        Rectangle2D.Double playerRect = player.getBoundingRectangle();

        return active && canDamage && myRect.intersects(playerRect);
    }

    public void increaseSharkSpeed() {
        dx = dx + 1;
        dy = dy + 1;
        if (dx >= 15) {
            dx = 15;
            dy = 15;
        }
    }

    public void decreaseSharkSpeed() {
        dx = dx - 1;
        dy = dy - 1;
        if (dx <= 5) {
            dx = 5;
            dy = 5;
        }
    }

    public void start() {
        x = 5;
        y = 10;
        animation.start();
    }

    public void update() {
        if (!animation.isStillActive())
            return;
        move();
        animation.update();
        // x = x + dx;
        // y = y + dy;
    }

    public Image getCurrentImage() {
        return animation.getImage();
    }

    public void toggleDistracted() {
        distracted = !distracted;
    }

    public boolean isDistracted() {
        return distracted;
    }

    public void startDistraction() {
        distracted = true;
        distractionStartTime = System.currentTimeMillis();
    }

    public boolean DamageStatus() {
        return canDamage;
    }

    public void setDamageStatus(boolean canDamage) {
        this.canDamage = canDamage;
    }

    public void setLastCollisionTime(long lastCollisionTime) {
        this.lastCollisionTime = lastCollisionTime;
    }

    public void sharkBite() {
        soundManager.playSound("sharkBite", false);
    }

    public void sharkHurt() {
        soundManager.playSound("sharkHurt", false);
    }

    public void stopHurtSound() {
        soundManager.stopSound("sharkHurt");
    }

    public void stopBiteSound() {
        soundManager.stopSound("sharkBite");
    }

    public long getLastCollisionTime() {
        return lastCollisionTime;
    }

    public void restoreDamage() {
        if (!canDamage && System.currentTimeMillis() - lastCollisionTime > COOLDOWN_MS) {
            canDamage = true;
            System.out.println("Time Passed in seconds: " + ((System.currentTimeMillis() - lastCollisionTime) / 1000));
        }
    }

    public boolean isActive() {
        return active;
    }
    
    public void deactivate() {
        active = false;
    }

}