import java.awt.Image;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * The FaceAnimation class creates a face animation containing six frames
 * based on three images.
 */
public class playerAnimation {

	Animation animation;

	private int x;
	private int y;

	private int width;
	private int height;

	private int dx; // increment to move along x-axis
	private int dy; // increment to move along y-axis

	public playerAnimation() {

		animation = new Animation(true);

		x = 5;
		y = 10;
		dx = 0; 
		dy = 0;

		Image animImage1 = ImageManager.loadImage("images/Diver1.png");
		Image animImage2 = ImageManager.loadImage("images/Diver2.png");
		Image animImage4 = ImageManager.loadImage("images/Diver4.png");
		Image animImage5 = ImageManager.loadImage("images/Diver5.png");
		Image animImage6 = ImageManager.loadImage("images/Diver6.png");

		if (animImage1 == null)
			System.out.println("Diver 1.png not found!");
		if (animImage2 == null)
			System.out.println("Diver 2.png not found!");
		if (animImage4 == null)
			System.out.println("Diver 4.png not found!");
		if (animImage5 == null)
			System.out.println("Diver 5.png not found!");
		if (animImage6 == null)
			System.out.println("Diver 6.png not found!");

		animation.addFrame(animImage1, 200);
		animation.addFrame(animImage2, 200);
		animation.addFrame(animImage4, 200);
		animation.addFrame(animImage5, 200);
		animation.addFrame(animImage6, 200);

	}

	public void start() {
		x = 5;
		y = 10;
		animation.start();
	}

	public void update() {
		if (!animation.isStillActive())
			return;

		animation.update();
		x = x + dx;
		y = y + dy;
	}

	public void draw(Graphics2D g2) {
		if (!animation.isStillActive())
			return;
		Image img = animation.getImage();
		g2.drawImage(img, x, y, 50, 100, null);
	}

	public Image getCurrentImage() {
		return animation.getImage();
	}

}
