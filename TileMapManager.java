import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.ImageIcon;

/**
 * The ResourceManager class loads and manages tile Images and
 * "host" Sprites used in the game. Game Sprites are cloned from
 * "host" Sprites.
 */
public class TileMapManager {

    private ArrayList<Image> tiles;
    private int currentMap = 0;

    private JFrame window;

    public TileMapManager(JFrame window) {
        this.window = window;

        loadTileImages();

        // loadCreatureSprites();
        // loadPowerUpSprites();
    }

    public TileMap loadMap(String filename) throws IOException {
        ArrayList<Coin> coins = new ArrayList<>();
        ArrayList<JellyFish> jellyfishList = new ArrayList<>();
        ArrayList<TreasureChest> chests = new ArrayList<>();
        ArrayList<Submarine> submarines = new ArrayList<>();
        ArrayList<Shark> sharks = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<String>();
        int mapWidth = 0;
        int mapHeight = 0;

        // read every line in the text file into the list

        BufferedReader reader = new BufferedReader(new FileReader(filename));

        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            if (!line.startsWith("#")) {
                lines.add(line);
                mapWidth = Math.max(mapWidth, line.length());
            }
        }

        // parse the lines to create a TileMap
        mapHeight = lines.size();

        TileMap newMap = new TileMap(window, mapWidth, mapHeight);
        for (int y = 0; y < mapHeight; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);

                if (ch == 'O') {
                    int px = TileMap.tilesToPixels(x);
                    int py = TileMap.tilesToPixels(y);
                    coins.add(new Coin(px, py));
                    continue;
                }

                if (ch == 'K') {
                    Point p0 = new Point(x, y);
                    Point p1 = new Point(x+100, y+100);
                    Point p2 = new Point(x+200, y);
                    jellyfishList.add(new JellyFish(
                        TileMap.tilesToPixels(x),
                        TileMap.tilesToPixels(y),
                        p0, p1, p2
                    ));
                    continue;
                }

                if (ch == 'T') {
                    int px = TileMap.tilesToPixels(x);
                    int py = TileMap.tilesToPixels(y) + newMap.getOffsetY();
                    chests.add(new TreasureChest(px, py));
                    continue;
                }

                if (ch == 'S') {
                    int px = TileMap.tilesToPixels(x);
                    int py = TileMap.tilesToPixels(y) + newMap.getOffsetY();
                    submarines.add(new Submarine(px, py));
                    continue;
                }

                if (ch == 'X') {
                    int px = TileMap.tilesToPixels(x);
                    int py = TileMap.tilesToPixels(y) + newMap.getOffsetY();
                    sharks.add(new Shark(window, px, py, newMap.getPlayer()));
                    continue;
                }

                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, tiles.get(tile));
                }

                
                /*
                 * // check if the char represents a sprite
                 * else if (ch == 'o') {
                 * addSprite(newMap, coinSprite, x, y);
                 * }
                 * else if (ch == '!') {
                 * addSprite(newMap, musicSprite, x, y);
                 * }
                 * else if (ch == '*') {
                 * addSprite(newMap, goalSprite, x, y);
                 * }
                 * else if (ch == '1') {
                 * addSprite(newMap, grubSprite, x, y);
                 * }
                 * else if (ch == '2') {
                 * addSprite(newMap, flySprite, x, y);
                 * }
                 */
            }
        }
        newMap.setCoins(coins);
        newMap.setJellyFish(jellyfishList);
        newMap.setTreasureChests(chests);
        newMap.setSubmarines(submarines);
        newMap.setSharks(sharks);
        return newMap;
    }

    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------

    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ folder

        File file;

        System.out.println("loadTileImages called.");

        tiles = new ArrayList<Image>();
        char ch = 'A';
        while (true) {
            String filename = "images/tile_" + ch + ".png";
            file = new File(filename);
            if (!file.exists()) {
                System.out.println("Image file could not be opened: " + filename);
                break;
            } else
                System.out.println("Image file opened: " + filename);
            Image tileImage = new ImageIcon(filename).getImage();
            tiles.add(tileImage);
            ch++;
        }
    }
}
