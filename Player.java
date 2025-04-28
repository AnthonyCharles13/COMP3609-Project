import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Point;

public class Player {

	private static final int DX = 8; // amount of X pixels to move in one keystroke
	private static final int DY = 8; // amount of Y pixels to move in one keystroke

	private static final int TILE_SIZE = 64;

	private JFrame window; // reference to the JFrame on which player is drawn
	private TileMap tileMap;
	private BackgroundManager bgManager;

	private int x; // x-position of player's sprite
	private int y; // y-position of player's sprite

	Graphics2D g2;
	private Dimension dimension;

	private Image playerImage, playerLeftImage, playerRightImage;

	private boolean jumping;
	private int timeElapsed;
	private int startY;

	private boolean goingUp;
	private boolean goingDown;

	private boolean inAir;
	private int initialVelocity;
	private int startAir;

	private playerAnimation animation;
	private int currentDirection;

	private int playerWidth, playerHeight;

	private SoundManager soundManager;
	private ArrayList<HarpoonSpear> spears;
	private int shotsFired = 0;

	public Player(JFrame window, TileMap t, BackgroundManager b) {
		soundManager = SoundManager.getInstance();

		this.window = window;

		tileMap = t; // tile map on which the player's sprite is displayed
		bgManager = b; // instance of BackgroundManager

		goingUp = goingDown = false;
		inAir = false;

		animation = new playerAnimation();
		animation.start();

		playerWidth = 50;
		playerHeight = 100;

		spears = new ArrayList<>();
		currentDirection = 2;

	}

	public Point collidesWithTile(int newX, int newY) {
		// int playerHeight = playerImage.getHeight(null);
		// int playerWidth = playerImage.getWidth(null);
		int offsetY = tileMap.getOffsetY();
		int xTile = tileMap.pixelsToTiles(newX);
		int yTile = tileMap.pixelsToTiles(newY - offsetY);

		for (int i = 0; i < playerHeight; i++) {
			yTile = tileMap.pixelsToTiles(newY - offsetY + i);
			if (tileMap.getTile(xTile, yTile) != null) {
				Point tilePos = new Point(xTile, yTile);
				return tilePos;
			}
		}

		if (tileMap.getTile(xTile, yTile) != null) {
			Point tilePos = new Point(xTile, yTile);
			return tilePos;
		} else {
			return null;
		}
	}

	public Point collidesWithTileDown(int newX, int newY) {

		// int playerWidth = playerImage.getWidth(null);
		// int playerHeight = playerImage.getHeight(null);
		int offsetY = tileMap.getOffsetY();
		int xTile = tileMap.pixelsToTiles(newX);
		int yTileFrom = tileMap.pixelsToTiles(y - offsetY);
		int yTileTo = tileMap.pixelsToTiles(newY - offsetY + playerHeight);

		for (int yTile = yTileFrom; yTile <= yTileTo; yTile++) {
			if (tileMap.getTile(xTile, yTile) != null) {
				Point tilePos = new Point(xTile, yTile);
				return tilePos;
			} else {
				if (tileMap.getTile(xTile + 1, yTile) != null) {
					int leftSide = (xTile + 1) * TILE_SIZE;
					if (newX + playerWidth > leftSide) {
						Point tilePos = new Point(xTile + 1, yTile);
						return tilePos;
					}
				}
			}
		}

		return null;
	}

	public Point collidesWithTileUp(int newX, int newY) {

		// int playerWidth = playerImage.getWidth(null);

		int offsetY = tileMap.getOffsetY();
		int xTile = tileMap.pixelsToTiles(newX);

		int yTileFrom = tileMap.pixelsToTiles(y - offsetY);
		int yTileTo = tileMap.pixelsToTiles(newY - offsetY);

		for (int yTile = yTileFrom; yTile >= yTileTo; yTile--) {
			if (tileMap.getTile(xTile, yTile) != null) {
				Point tilePos = new Point(xTile, yTile);
				return tilePos;
			} else {
				if (tileMap.getTile(xTile + 1, yTile) != null) {
					int leftSide = (xTile + 1) * TILE_SIZE;
					if (newX + playerWidth > leftSide) {
						Point tilePos = new Point(xTile + 1, yTile);
						return tilePos;
					}
				}
			}

		}

		return null;
	}

	public synchronized void move(int direction) {

		int newX = x;
		Point tilePos = null;
		playerImage = animation.getCurrentImage();

		if (!window.isVisible())
			return;

		if (direction == 1) { // move left
			// playerImage = animation.getCurrentImage();
			newX = x - DX;
			currentDirection = 1;
			if (newX < 0) {
				x = 0;
				return;
			}

			tilePos = collidesWithTile(newX, y);
		} else if (direction == 2) { // move right

			// int playerWidth = playerImage.getWidth(null);
			newX = x + DX;
			currentDirection = 2;
			int tileMapWidth = tileMap.getWidthPixels();

			if (newX + playerWidth >= tileMapWidth) {
				x = tileMapWidth - playerWidth;
				return;
			}

			tilePos = collidesWithTile(newX + playerWidth, y);

		} else if (direction == 3) {
			y = y - DY;

			if (y < 0) {
				y = 0;
				return;
			}

			tilePos = collidesWithTileUp(x, y);
			if (tilePos != null) { 
				System.out.println("Collision Going Up!");

				int offsetY = tileMap.getOffsetY();
				int topTileY = ((int) tilePos.getY()) * TILE_SIZE + offsetY;
				int bottomTileY = topTileY + TILE_SIZE;

				y = bottomTileY;
			} else {
				y = y - DY;
				System.out.println("Jumping: No collision.");
			}
			return;
		} else if (direction == 4) {
			y = y + DY;

			tilePos = collidesWithTileDown(x, y);
			if (tilePos != null) {
				System.out.println("Collision Going Down!");
				// int playerHeight = playerImage.getHeight(null);

				int offsetY = tileMap.getOffsetY();
				int topTileY = ((int) tilePos.getY()) * TILE_SIZE + offsetY;

				y = topTileY - playerHeight;
			} else {
				System.out.println("No collision going down.");
			}
			return;
		}

		if (tilePos != null) {
			if (direction == 1) {
				System.out.println(": Collision going left");
				x = ((int) tilePos.getX() + 1) * TILE_SIZE; 
			} else if (direction == 2) {
				System.out.println(": Collision going right");
				// int playerWidth = playerImage.getWidth(null);
				x = ((int) tilePos.getX()) * TILE_SIZE - playerWidth; 
			} else if (direction == 3) {
				System.out.println(": Collision going up");
				y = ((int) tilePos.getY()) * TILE_SIZE;
			} else if (direction == 4) {
				System.out.println(": Collision going down");
				// int playerHeight = playerImage.getHeight(null);
				y = ((int) tilePos.getY()) * TILE_SIZE - playerHeight;
			}
		} else {
			if (direction == 1) {
				x = newX;
				bgManager.moveLeft();
			} else if (direction == 2) {
				x = newX;
				bgManager.moveRight();
			}

		}
	}

	public void update() {
		int distance = 0;
		int newY = 0;

		// timeElapsed++;
		animation.update();
	}

	public void moveUp() {

		if (!window.isVisible())
			return;

		y = y - DY;
	}

	public void moveDown() {

		if (!window.isVisible())
			return;

		y = y + DY;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Image getImage() {
		Image currentImage = animation.getCurrentImage();
		if (currentImage == null) {
			return null;
		} else {
			return currentImage;
		}
	}

	public Rectangle2D.Double getBoundingRectangle() {
		return new Rectangle2D.Double(x, y, playerWidth, playerHeight);
	}

	public void playHurtSound() {
		soundManager.playSound("playerHurt", false);
	}

	public void stopHurtSound() {
		soundManager.stopSound("playerHurt");
	}

	public int getWidth() {
		return playerWidth;
	}

	public int getHeight() {
		return playerHeight;
	}

	public void shootSpear() {
		if (canShoot()) {
			int spawnX = (int) getX() + (currentDirection == 2 ? 50 : -20);
			spears.add(new HarpoonSpear(spawnX, (int) getY() + 40, 
									   currentDirection));
			shotsFired++;
		}
	}
	
	public boolean canShoot() {
		// Ammo is calculated as score + 10
		TileMap tileMap = ((GameWindow) window).getTileMap();
		return tileMap != null && 
			   tileMap.getCurrentLevel() == 2 && 
			   getAmmo() > 0;
	}
	
	public int getAmmo() {
		TileMap tileMap = ((GameWindow) window).getTileMap();
		if (tileMap == null) return 0;
		return (tileMap.getlvl1score() + 10) - shotsFired;
	}
	
	public ArrayList<HarpoonSpear> getSpears() { return spears; }

	public int getCurrentDirection() { return currentDirection; }

	public void updateSpears() {
		spears.removeIf(spear -> !spear.isActive());
	}

}