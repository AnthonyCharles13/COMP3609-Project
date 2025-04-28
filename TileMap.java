import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFrame;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

/**
 * The TileMap class contains the data for a tile-based
 * map, including Sprites. Each tile is a reference to an
 * Image. Images are used multiple times in the tile map.
 * map.
 */

public class TileMap {

    public static final int TILE_SIZE = 64;
    private static final int TILE_SIZE_BITS = 6;

    private Image[][] tiles;
    private int screenWidth, screenHeight;
    private int mapWidth, mapHeight;
    private int offsetY;
    private int offsetX;

    private LinkedList sprites;
    private Player player;

    BackgroundManager bgManager;

    private JFrame window;
    private Dimension dimension;

    private int currentDirection;

    private Shark shark;
    private boolean sharkDropped;

    private ArrayList<Coin> coins;
    private int score = 0;
    private int numLives = 3;

    private ArrayList<JellyFish> jellyfishList;
    private Image heartImage;

    private ArrayList<TreasureChest> treasureChests;
    private ArrayList<Submarine> submarines;
    private boolean gameWon = false;
    private boolean gameOver = false;
    private int lvl1Score = 0;

    /**
     * Creates a new TileMap with the specified width and
     * height (in number of tiles) of the map.
     */
    public TileMap(JFrame window, int width, int height) {

        heartImage = ImageManager.loadImage("images/heart.png");

        currentDirection = 2;

        this.window = window;
        dimension = window.getSize();

        screenWidth = dimension.width;
        screenHeight = dimension.height;

        mapWidth = width;
        mapHeight = height;

        // get the y offset to draw all sprites and tiles

        offsetY = screenHeight - tilesToPixels(mapHeight);
        System.out.println("offsetY: " + offsetY);

        bgManager = new BackgroundManager(window, 12);

        tiles = new Image[mapWidth][mapHeight];
        player = new Player(window, this, bgManager);
        sprites = new LinkedList();

        Image playerImage = player.getImage();
        int playerHeight = playerImage.getHeight(null);

        int x, y;
        // x = (dimension.width / 2) + TILE_SIZE; // position player in middle of screen

        x = 738;
        y = dimension.height - (TILE_SIZE * 2 + playerHeight);

        player.setX(x);
        player.setY(y);

        //System.out.println("Player coordinates: " + x + "," + y);

        shark = new Shark(window, 74, 196, player);
        sharkDropped = true;

    }

    public void setCoins(ArrayList<Coin> coins) {
        this.coins = coins;
    }

    public void setJellyFish(ArrayList<JellyFish> jellyfishList) {
        this.jellyfishList = jellyfishList;
    }

    public void setTreasureChests(ArrayList<TreasureChest> chests) {
        this.treasureChests = chests;
    }

    public void setSubmarines(ArrayList<Submarine> submarines) {
        this.submarines = submarines;
    }

    public ArrayList<Submarine> getSubmarines() {
        return submarines;
    }

    /**
     * Gets the width of this TileMap (number of pixels across).
     */
    public int getWidthPixels() {
        return tilesToPixels(mapWidth);
    }

    /**
     * Gets the width of this TileMap (number of tiles across).
     */
    public int getWidth() {
        return mapWidth;
    }

    /**
     * Gets the height of this TileMap (number of tiles down).
     */
    public int getHeight() {
        return mapHeight;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Gets the tile at the specified location. Returns null if
     * no tile is at the location or if the location is out of
     * bounds.
     */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= mapWidth ||
                y < 0 || y >= mapHeight) {
            return null;
        } else {
            return tiles[x][y];
        }
    }

    /**
     * Sets the tile at the specified location.
     */
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }

    /**
     * Gets an Iterator of all the Sprites in this map,
     * excluding the player Sprite.
     */

    public Iterator getSprites() {
        return sprites.iterator();
    }

    /**
     * Class method to convert a pixel position to a tile position.
     */

    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }

    /**
     * Class method to convert a pixel position to a tile position.
     */

    public static int pixelsToTiles(int pixels) {
        return (int) Math.floor((float) pixels / TILE_SIZE);
    }

    /**
     * Class method to convert a tile position to a pixel position.
     */

    public static int tilesToPixels(int numTiles) {
        return numTiles * TILE_SIZE;
    }

    /**
     * Draws the specified TileMap.
     */
    public void draw(Graphics2D g2) {
        int mapWidthPixels = tilesToPixels(mapWidth);

        offsetX = screenWidth / 2 - Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidthPixels);

        bgManager.draw(g2);

        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
        for (int y = 0; y < mapHeight; y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
                Image image = getTile(x, y);
                if (image != null) {
                    g2.drawImage(image, tilesToPixels(x) + offsetX, tilesToPixels(y) + offsetY, 64, 64, null);
                }
            }
        }

        for (Coin coin : coins) {
            coin.draw(g2, offsetX, offsetY);
        }

        for (JellyFish jelly : jellyfishList) {
            jelly.draw(g2, offsetX, offsetY);
        }

        for (TreasureChest chest : treasureChests) {
            chest.draw(g2, offsetX, offsetY);
        }

        for (Submarine sub : submarines) {
            sub.draw(g2, offsetX, offsetY);
        }

        // draw player
        Image player_img = player.getImage();
        int playerHeight = 100;
        int playerWidth = 50;
        if (currentDirection == 1) {
            g2.drawImage(player_img,
                    Math.round(player.getX()) + offsetX + playerWidth,
                    Math.round(player.getY()), // + offsetY,
                    -playerWidth,
                    playerHeight,
                    null);
        } else {
            g2.drawImage(player_img,
                    Math.round(player.getX()) + offsetX,
                    Math.round(player.getY()), // + offsetY,
                    playerWidth,
                    playerHeight,
                    null);
        }

        if (shark != null && sharkDropped) {
            shark.draw(g2, offsetX, offsetY);
        }

        for (HarpoonSpear spear : player.getSpears()) {
            spear.draw(g2, offsetX, offsetY);
        }

        int heartWidth = 50;
        int heartX = 15;
        for (int i = 0; i < numLives; i++) {
            g2.drawImage(heartImage, heartX + (i * heartWidth), 50, heartWidth, heartWidth, null);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 42));
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score + "  Ammo: " + player.getAmmo(), 15, 42);

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        if(((GameWindow) window).getLevel() == 1){
            g2.drawString("Objective: Collect gold and find the hidden treasure!", 15, 80+heartWidth);
        }
        else {
            g2.drawString("Objective: Defeat the enemies and escape using the submarine!", 15, 80+heartWidth);
        }

        
        if (((GameWindow) window).isPaused()) {
            drawPauseScreen(g2);
        }

        if (gameWon) {
            drawWinScreen(g2);
            return;
        }

        if (gameOver) {
            drawGameOverScreen(g2);
            return;
        }
        
    }

    public void moveLeft() {
        int x, y;
        x = player.getX();
        y = player.getY();

        String mess = "Going left. x = " + x + " y = " + y;
        System.out.println(mess);
        currentDirection = 1;
        player.move(1);

    }

    public void moveRight() {
        int x, y;
        x = player.getX();
        y = player.getY();

        String mess = "Going right. x = " + x + " y = " + y;
        System.out.println(mess);
        currentDirection = 2;
        player.move(2);

    }

    public void moveUp() {
        int x, y;
        x = player.getX();
        y = player.getY();

        String mess = "Going up. x = " + x + " y = " + y;
        System.out.println(mess);

        player.move(3);

    }

    public void moveDown() {
        int x, y;
        x = player.getX();
        y = player.getY();

        String mess = "Going down. x = " + x + " y = " + y;
        System.out.println(mess);

        player.move(4);

    }

    public void jump() {
        int x, y;
        x = player.getX();
        y = player.getY();

        String mess = "Jumping. x = " + x + " y = " + y;
        System.out.println(mess);

        player.move(3);

    }

    public void update() {

        if (gameWon || gameOver) {
            sharkDropped = false;
            return;
        }

        if (gameWon || gameOver)
            return;

        if (numLives <= 0) {
            gameOver = true;
            return;
        }

        if (sharkDropped) {
            shark.move();
            shark.update();
        }

        player.update();

        player.updateSpears();

        if (((GameWindow) window).getLevel() == 1) {
            lvl1Score = score;
        }

        // Update spears
        ArrayList<HarpoonSpear> spears = player.getSpears();
        for (HarpoonSpear spear : spears) {
            spear.update();
            if (sharkDropped && spear.getBoundingBox().intersects(shark.getBoundingRectangle())) {
                spear.setActive(false);
                //shark.deactivate();
                score += 5;
                shark.sharkHurt();
                sharkDropped = false;
            }

            // Add jellyfish collision check
            Iterator<JellyFish> jellyIter = jellyfishList.iterator();
            while (jellyIter.hasNext()) {
                JellyFish jelly = jellyIter.next();
                if (spear.getBoundingBox().intersects(jelly.getBoundingRectangle())) {
                    spear.setActive(false);
                    jelly.deactivate();
                    jellyIter.remove();
                    score += 3; // Points for killing jellyfish
                    break;
                }
            }
        }

        Rectangle2D.Double playerRect = player.getBoundingRectangle();
        for (TreasureChest chest : treasureChests) {
            if (!chest.isOpened()) chest.update();

            if (chest.checkCollision(playerRect)) {
                chest.open();
                chest.update();
                score += 10;
                //gameWon = true;
                ((GameWindow) window).endLevel();
                break;
            }
        }

        for (Submarine sub : submarines) {
            if (sub.checkCollision(playerRect)) {
                if (getCurrentLevel() == 2) {
                    ((GameWindow) window).endLevel();
                }
                sub.board();
                break;
            }
        }

        if (sharkDropped && shark.getBoundingRectangle().intersects(player.getBoundingRectangle())) {
            if (shark.DamageStatus()) {
                numLives = Math.max(0, numLives - 1);

                player.playHurtSound();
                //player.stopHurtSound();
                shark.sharkBite();
                score = Math.max(0, score - 3);
                shark.setLastCollisionTime(System.currentTimeMillis());
                shark.setDamageStatus(false);
                shark.increaseSharkSpeed();
                // soundManager.playClip("playerHurt", false);
                System.out.println("TM Shark Collision: " + shark.getLastCollisionTime()/1000);
                System.out.println("TM Player location: " + player.getX() + ", " + player.getY());
            }
            else {
                shark.restoreDamage();
            }
        }

        Iterator<Coin> coinIter = coins.iterator();
        while (coinIter.hasNext()) {
            Coin coin = coinIter.next();
            coin.update();
            if (coin.checkCollision(playerRect)) {
                coin.collect();
                score += 5; 
                coinIter.remove();
            }
        }

        // Check jellyfish collisions
        Iterator<JellyFish> jellyIter = jellyfishList.iterator();
        while (jellyIter.hasNext()) {
            JellyFish jelly = jellyIter.next();
            jelly.update();
            if (jelly.checkCollision(playerRect)) {
                if (jelly.DamageStatus()) {
                    score = Math.max(0, score - 2); // Lose 2 points, minimum 0
                    jelly.Electrocute();
                    jelly.setLastCollisionTime(System.currentTimeMillis());
                    jelly.setDamageStatus(false);
                    player.playHurtSound();
                }
                else {
                    jelly.restoreDamage();
                }
                // jelly.deactivate();
                // jellyIter.remove();
            }

            if (sharkDropped && jelly.checkCollision(shark.getBoundingRectangle())) {
                shark.decreaseSharkSpeed();
                shark.sharkBite();
                jelly.Electrocute();
                //shark.sharkHurt();
                shark.startDistraction();
                jelly.deactivate();
                jellyIter.remove();
                
            }
        }

        // System.out.println("Score: " + score);
    }

    private void drawWinScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 48));

        String winText = "You Won!";
        int textWidth = g2.getFontMetrics().stringWidth(winText);
        g2.drawString(winText, (screenWidth - textWidth) / 2, screenHeight / 2);

        // Score text
        g2.setFont(new Font("Arial", Font.PLAIN, 36));
        String scoreText = "Your Final Score: " + score;
        textWidth = g2.getFontMetrics().stringWidth(scoreText);
        g2.drawString(scoreText, (screenWidth - textWidth) / 2, screenHeight / 2 + 50);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        String restartText = "Click 'Quit' to close game";
        textWidth = g2.getFontMetrics().stringWidth(restartText);
        g2.drawString(restartText, (screenWidth - textWidth) / 2, screenHeight / 2 + 100);
    }

    private void drawGameOverScreen(Graphics2D g2) {
        // Dark overlay
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Game Over text
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 48));

        String gameOverText = "Game Over!";
        int textWidth = g2.getFontMetrics().stringWidth(gameOverText);
        g2.drawString(gameOverText, ((screenWidth - screenWidth/2) - textWidth/ 2), screenHeight / 2);

        // Score text
        String scoreText = "Final Score: " + score;
        g2.setFont(new Font("Arial", Font.PLAIN, 36));
        textWidth = g2.getFontMetrics().stringWidth(scoreText);
        g2.drawString(scoreText, ((screenWidth - screenWidth/2) - textWidth/ 2), screenHeight / 2 + 50);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        String restartText = "Click 'Quit' to exit";
        textWidth = g2.getFontMetrics().stringWidth(restartText);
        g2.drawString(restartText, ((screenWidth - screenWidth/2) - textWidth/ 2), screenHeight / 2 + 100);
    }

    public void drawPauseScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String pauseText = "Game Paused";
        int textWidth = g2.getFontMetrics().stringWidth(pauseText);
        g2.drawString(pauseText, (screenWidth - textWidth) / 2, screenHeight / 2);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        String restartText = "Click 'Play' to resume";
        textWidth = g2.getFontMetrics().stringWidth(restartText);
        g2.drawString(restartText, (screenWidth - textWidth) / 2, screenHeight / 2 + 50);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public void setGameWon(boolean won) {
        gameWon = won;
    }

    public void setGameOver(boolean over) {
        gameOver = over;
    }

    public int getScore() {
        return score;
    }

    public int getCurrentLevel() {
        return ((GameWindow) window).getLevel();
    }

    public Player getPlayer() {
        return player;
    }

    public int getlvl1score() {
        return lvl1Score;
    }

}
