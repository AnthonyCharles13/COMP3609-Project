/* BackgroundManager manages many backgrounds (wraparound images 
   used for the game's background). 

   Backgrounds 'further back' move slower than ones nearer the
   foreground of the game, creating a parallax distance effect.

   When a sprite is instructed to move left or right, the sprite
   doesn't actually move, instead the backgrounds move in the 
   opposite direction (right or left).

*/

import java.awt.Graphics2D;
import javax.swing.JFrame;

public class BackgroundManager {

	private String bgImages[] = { "images/bg_1.png",
			"images/bg_2.png",
			"images/bg_3.png",
			"images/bg_4.png",
			"images/bg_5.png",
			"images/bg_6.png",
			"images/bg_7.png",
			"images/bg_8.png",
			"images/bg_9.png",
			"images/bg_10.png" };

	private int moveAmount[] = { 1, 2, 3, 4, 5, 5, 6, 6, 7, 8, };
	// pixel amounts to move each background left or right
	// a move amount of 0 makes a background stationary

	private Background[] backgrounds;
	private int numBackgrounds;

	private JFrame window; // JFrame on which backgrounds are drawn

	public BackgroundManager(JFrame window, int moveSize) {
		// ignore moveSize
		this.window = window;

		numBackgrounds = bgImages.length;
		backgrounds = new Background[numBackgrounds];

		for (int i = 0; i < numBackgrounds; i++) {
			backgrounds[i] = new Background(window, bgImages[i], moveAmount[i]);
		}
	}

	public void moveRight() {
		for (int i = 0; i < numBackgrounds; i++)
			backgrounds[i].moveRight();
	}

	public void moveLeft() {
		for (int i = 0; i < numBackgrounds; i++)
			backgrounds[i].moveLeft();
	}

	// The draw method draws the backgrounds on the screen. The
	// backgrounds are drawn from the back to the front.

	public void draw(Graphics2D g2) {
		for (int i = 0; i < numBackgrounds; i++)
			backgrounds[i].draw(g2);
	}

}
