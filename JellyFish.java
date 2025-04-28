import java.awt.*;
import java.awt.geom.Rectangle2D;

public class JellyFish {
    private static final long COOLDOWN_MS = 5000;
    private static final double ANIMATION_SPEED = 0.02;
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;
    private static double START = 0;
    private static double END = 1.0;
    private static double INCR = 0.02;

    private final Point p0, p1, p2;
    private final Animation animation;
    private final Rectangle2D.Double boundingBox;

    private double t = 0;
    private double increment = ANIMATION_SPEED;
    private boolean active = true;
    private int x, y;
    private double incr = INCR;

    private SoundManager soundManager;
    private boolean canDamage = true;
    private long lastCollisionTime;

    public JellyFish(int startX, int startY, Point p0, Point p1, Point p2) {
        soundManager = SoundManager.getInstance();
        this.p0 = new Point(p0.x + startX, p0.y + startY);
        this.p1 = new Point(p1.x + startX, p1.y + startY);
        this.p2 = new Point(p2.x + startX, p2.y + startY);
        this.x = startX;
        this.y = startY;

        this.boundingBox = new Rectangle2D.Double(x, y, WIDTH, HEIGHT);
        this.animation = createAnimation();
        canDamage = true;
    }

    private Animation createAnimation() {
        Animation anim = new Animation(true);
        Image[] frames = {
                ImageManager.loadImage("images/JellyFish1.png"),
                ImageManager.loadImage("images/JellyFish2.png"),
                ImageManager.loadImage("images/JellyFish3.png"),
                ImageManager.loadImage("images/JellyFish4.png"),
                ImageManager.loadImage("images/JellyFish3.png"),
                ImageManager.loadImage("images/JellyFish2.png"),
                ImageManager.loadImage("images/JellyFish1.png")
        };
        for (Image frame : frames) {
            anim.addFrame(frame, 200);
        }
        anim.start();
        return anim;
    }

    public void update() {
        if (!active)
            return;

        t = t + incr;

        if (t > END) {
            t = END;
            incr = INCR * -1.0;
        } else if (t < START) {
            t = START;
            incr = INCR;
        }

        double u = 1 - t;
        x = (int) (u * u * p0.x + 2 * u * t * p1.x + t * t * p2.x);
        y = (int) (u * u * p0.y + 2 * u * t * p1.y + t * t * p2.y);

    
        boundingBox.x = x;
        boundingBox.y = y;
        animation.update();
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if (active) {
            g2.drawImage(animation.getImage(),
                    x + offsetX,
                    y,
                    100, 100, null);
        }
    }

    public boolean checkCollision(Rectangle2D.Double other) {
        return active && boundingBox.intersects(other);
    }

    public void deactivate() {
        active = false;
    }

    // Getters for position if needed
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void Electrocute() {
        soundManager.playSound("shock", false);
    }

    public void stopSound() {
        soundManager.stopSound("shock");
    }

    public boolean DamageStatus() {
        return canDamage;
    }

    public void setDamageStatus(boolean canDamage) {
        this.canDamage = canDamage;
    }

    public void restoreDamage() {
        if (!canDamage && System.currentTimeMillis() - lastCollisionTime > COOLDOWN_MS) {
            canDamage = true;
            System.out.println("Time Passed in seconds: " + ((System.currentTimeMillis() - lastCollisionTime) / 1000));
        }
    }

    public void setLastCollisionTime(long lastCollisionTime) {
        this.lastCollisionTime = lastCollisionTime;
    }

    public Rectangle2D.Double getBoundingRectangle() {
		return boundingBox;
	}
}