import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;

public class HarpoonSpear {
    private static final int SPEED = 15;
    private static final int WIDTH = 80;
    private static final int HEIGHT = 16;
    
    private int x, y;
    private int direction;
    private boolean active;
    private Image image;
    private GameWindow window;

    public HarpoonSpear(int startX, int startY, int direction) {
        //this.window = window;
        this.x = startX;
        this.y = startY;
        this.direction = direction;
        this.active = true;
        this.image = ImageManager.loadImage("images/spear.png");
    }

    public void update() {
        if (!active) return;
        
        x += (direction == 2) ? SPEED : -SPEED;
        
        // Check screen bounds
        //if (x < -WIDTH || x > mapWidth) {
         //   active = false;
        //}
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if (!active) return;
        
        if (direction == 1) { 
            g2.drawImage(image, 
                         x + offsetX + WIDTH, y + offsetY, 
                         -WIDTH, HEIGHT, null);
        } else {
            g2.drawImage(image, 
                         x + offsetX, y + offsetY, 
                         WIDTH, HEIGHT, null);
        }
    }

    public Rectangle2D.Double getBoundingBox() {
        return new Rectangle2D.Double(x, y, WIDTH, HEIGHT);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}