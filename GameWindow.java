import javax.swing.*; // need this for GUI objects
import java.awt.*; // need this for certain AWT classes
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.image.BufferStrategy; // need this to implement page flipping

public class GameWindow extends JFrame implements
		Runnable,
		KeyListener,
		MouseListener,
		MouseMotionListener {
	private static final int NUM_BUFFERS = 2; // used for page flipping

	private int pWidth, pHeight; // width and height of screen

	private Thread gameThread = null; // the thread that controls the game
	private volatile boolean isRunning = false; // used to stop the game thread

	// private BirdAnimation animation = null; // animation sprite
	// private ImageEffect imageEffect; // sprite demonstrating an image effect

	private BufferedImage image; // drawing area for each frame

	private Image quit1Image; // first image for quit button
	private Image quit2Image; // second image for quit button

	private boolean finishedOff = false; // used when the game terminates

	private volatile boolean isOverQuitButton = false;
	private Rectangle quitButtonArea; // used by the quit button

	private volatile boolean isOverPauseButton = false;
	private Rectangle pauseButtonArea; // used by the pause 'button'
	private volatile boolean isPaused = false;

	private volatile boolean isOverStopButton = false;
	private Rectangle stopButtonArea; // used by the stop 'button'
	private volatile boolean isStopped = false;

	private volatile boolean isOverShowAnimButton = false;
	private Rectangle showAnimButtonArea; // used by the show animation 'button'
	private volatile boolean isAnimShown = false;

	private volatile boolean isOverPauseAnimButton = false;
	private Rectangle pauseAnimButtonArea; // used by the pause animation 'button'
	private volatile boolean isAnimPaused = false;

	private GraphicsDevice device; // used for full-screen exclusive mode
	private Graphics gScr;
	private BufferStrategy bufferStrategy;

	private SoundManager soundManager;
	TileMapManager tileManager;
	TileMap tileMap;

	private int level = 1;
	private boolean levelChange = false;
	private boolean gameOver = false;
	private boolean gameWon = false;
	private int mapWidth = 0;

	public GameWindow() {

		super("Tiled Bat and Ball Game: Full Screen Exclusive Mode");

		initFullScreen();

		quit1Image = ImageManager.loadImage("images/Quit1.png");
		quit2Image = ImageManager.loadImage("images/Quit2.png");

		setButtonAreas();

		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		// animation = new BirdAnimation();
		soundManager = SoundManager.getInstance();
		image = new BufferedImage(pWidth, pHeight, BufferedImage.TYPE_INT_RGB);

		startGame();
		isPaused = true;
	}

	// implementation of Runnable interface

	public void run() {
		try {
			isRunning = true;
			while (isRunning) {
				if (isPaused == false) {
					gameUpdate();
				}
				screenUpdate();
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
		}

		finishOff();
	}

	/*
	 * This method performs some tasks before closing the game.
	 * The call to System.exit() should not be necessary; however,
	 * it prevents hanging when the game terminates.
	 */

	private void finishOff() {
		if (!finishedOff) {
			finishedOff = true;
			restoreScreen();
			System.exit(0);
		}
	}

	/*
	 * This method switches off full screen mode. The display
	 * mode is also reset if it has been changed.
	 */

	private void restoreScreen() {
		Window w = device.getFullScreenWindow();

		if (w != null)
			w.dispose();

		device.setFullScreenWindow(null);
	}

	private void gameUpdate() {
		if (levelChange) {
			levelChange = false;
			tileManager = new TileMapManager(this);
			try {
				String filename = "maps/map" + level + ".txt";
				tileMap = tileManager.loadMap(filename);
				isPaused = false;
				isStopped = false;
				tileMap.getPlayer().getSpears().clear();
				tileMap.setGameOver(false);
				tileMap.setGameWon(false);
				mapWidth = tileMap.getWidth();
			} catch (Exception e) {
				tileMap.setGameWon(true);
				System.out.println("Completed all levels! " + e.getMessage());
			}
			return;
		}
	
		if (gameWon || gameOver) {
			return;
		}
	
		tileMap.update();
	
		if (tileMap.isGameWon()) {
			isPaused = false;
			isStopped = true;
		}
	}

	public void endLevel() {
		level++;
		levelChange = true;
	}

	private void screenUpdate() {

		try {
			gScr = bufferStrategy.getDrawGraphics();
			gameRender(gScr);
			gScr.dispose();
			if (!bufferStrategy.contentsLost())
				bufferStrategy.show();
			else
				System.out.println("Contents of buffer lost.");

			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)

			Toolkit.getDefaultToolkit().sync();
		} catch (Exception e) {
			e.printStackTrace();
			isRunning = false;
		}
	}

	public void gameRender(Graphics gScr) { // draw the game objects

		Graphics2D imageContext = (Graphics2D) image.getGraphics();

		tileMap.draw(imageContext);

		drawButtons(imageContext); // draw the buttons

		Graphics2D g2 = (Graphics2D) gScr;
		g2.drawImage(image, 0, 0, pWidth, pHeight, null);

		imageContext.dispose();
		g2.dispose();
	}

	private void initFullScreen() { // standard procedure to get into FSEM

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = ge.getDefaultScreenDevice();

		setUndecorated(true); // no menu bar, borders, etc.
		setIgnoreRepaint(true); // turn off all paint events since doing active rendering
		setResizable(false); // screen cannot be resized

		if (!device.isFullScreenSupported()) {
			System.out.println("Full-screen exclusive mode not supported");
			System.exit(0);
		}

		device.setFullScreenWindow(this); // switch on full-screen exclusive mode

		// we can now adjust the display modes, if we wish
		showCurrentMode();

		pWidth = getBounds().width;
		pHeight = getBounds().height;

		System.out.println("Width of window is " + pWidth);
		System.out.println("Height of window is " + pHeight);

		try {
			createBufferStrategy(NUM_BUFFERS);
		} catch (Exception e) {
			System.out.println("Error while creating buffer strategy " + e);
			System.exit(0);
		}

		bufferStrategy = getBufferStrategy();
	}

	// This method provides details about the current display mode.
	private void showCurrentMode() {

		DisplayMode dm[] = device.getDisplayModes();

		for (int i = 0; i < dm.length; i++) {
			System.out.println("Current Display Mode: (" +
					dm[i].getWidth() + "," + dm[i].getHeight() + "," +
					dm[i].getBitDepth() + "," + dm[i].getRefreshRate() + ")  ");
		}

		// DisplayMode d = new DisplayMode (800, 600, 32, 60);
		// device.setDisplayMode(d);

		DisplayMode dm1 = device.getDisplayMode();

		dm1 = device.getDisplayMode();

		System.out.println("Current Display Mode: (" +
				dm1.getWidth() + "," + dm1.getHeight() + "," +
				dm1.getBitDepth() + "," + dm1.getRefreshRate() + ")  ");
	}

	// Specify screen areas for the buttons and create bounding rectangles
	private void setButtonAreas() {
		// leftOffset is the distance of a button from the left side of the window.
		// Buttons are placed at the top of the window.
		int buttonWidth = 150;
		int buttonSpacing = 20;
		int totalButtons = 2; // Pause, Stop, Quit
		int totalWidth = (totalButtons * buttonWidth) + ((totalButtons - 1) * buttonSpacing);
		int leftOffset = (pWidth - totalWidth) / 2;

		pauseButtonArea = new Rectangle(leftOffset, 60, buttonWidth, 40);
		leftOffset += buttonWidth + buttonSpacing;
		stopButtonArea = new Rectangle(leftOffset, 60, buttonWidth, 40);
		leftOffset += buttonWidth + buttonSpacing;
	}

	private void drawButtons(Graphics g) {
		Font oldFont, newFont;

		oldFont = g.getFont(); // save current font to restore when finished

		newFont = new Font("ARIAL", Font.BOLD + Font.BOLD, 28);
		g.setFont(newFont); // set this as font for text on buttons

		g.setColor(Color.black); // set outline colour of button

		// draw the pause 'button'

		g.setColor(Color.BLACK);
		g.fillOval(pauseButtonArea.x, pauseButtonArea.y,
				pauseButtonArea.width, pauseButtonArea.height);

		if (tileMap.isGameWon() || tileMap.isGameOver()) {
			g.setColor(Color.GRAY);
		} else if (isOverPauseButton && !isStopped)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.RED);

		if (isPaused && !isStopped)
			g.drawString("Play", pauseButtonArea.x + 50, pauseButtonArea.y + 30);
		else
			g.drawString("Pause", pauseButtonArea.x + 40, pauseButtonArea.y + 30);

		// draw the stop 'button'
		g.setColor(Color.BLACK);
		g.fillOval(stopButtonArea.x, stopButtonArea.y,
				stopButtonArea.width, stopButtonArea.height);

		if (isOverStopButton && !isStopped)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.RED);

		if (isStopped)
			g.drawString("Quit", stopButtonArea.x + 50, stopButtonArea.y + 30);
		else
			g.drawString("Quit", stopButtonArea.x + 50, stopButtonArea.y + 30);

		// draw the show animation 'button'
		g.setFont(oldFont); // reset font

	}

	private void startGame() {
		if (gameThread == null) {
			// soundManager.playSound ("background", true);
			tileManager = new TileMapManager(this);

			try {
				tileMap = tileManager.loadMap("maps/map1.txt");
				int w, h;
				w = tileMap.getWidth();
				h = tileMap.getHeight();
				System.out.println("Width of tilemap " + w);
				System.out.println("Height of tilemap " + h);
			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}

			// imageEffect = new ImageEffect (this);
			gameThread = new Thread(this);
			gameThread.start();

		}
	}

	// displays a message to the screen when the user stops the game
	private void gameOverMessage(Graphics g) {

		Font font = new Font("SansSerif", Font.BOLD, 24);
		FontMetrics metrics = this.getFontMetrics(font);

		String msg = "Game Over. Thanks for playing!";

		int x = (pWidth - metrics.stringWidth(msg)) / 2;
		int y = (pHeight - metrics.getHeight()) / 2;

		g.setColor(Color.BLUE);
		g.setFont(font);
		g.drawString(msg, x, y);

	}

	// implementation of methods in KeyListener interface
	public void keyPressed(KeyEvent e) {

		if (tileMap.isGameWon() || tileMap.isGameOver())
			return;

		if (isPaused)
			return;

		int keyCode = e.getKeyCode();

		if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) || (keyCode == KeyEvent.VK_END)) {
			isRunning = false; // user can quit anytime by pressing
			return; // one of these keys (ESC, Q, END)
		} else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
			tileMap.moveLeft();
		} else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
			tileMap.moveRight();
		} else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
			tileMap.moveUp();
		} else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
			tileMap.moveDown();
		} else if (keyCode == KeyEvent.VK_SPACE) {
			if (!isPaused && !tileMap.isGameOver() && !tileMap.isGameWon()) {
				tileMap.getPlayer().shootSpear();
			}
		}

	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {

	}

	// implement methods of MouseListener interface
	public void mouseClicked(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		testMousePress(e.getX(), e.getY());
	}

	public void mouseReleased(MouseEvent e) {

	}

	// implement methods of MouseMotionListener interface
	public void mouseDragged(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {
		testMouseMove(e.getX(), e.getY());
	}

	/*
	 * This method handles mouse clicks on one of the buttons
	 * (Pause, Stop, Start Anim, Pause Anim, and Quit).
	 */

	private void testMousePress(int x, int y) {
		if (!tileMap.isGameWon() && !tileMap.isGameOver()) {
			if (isOverPauseButton) { // mouse click on Pause button
				isPaused = !isPaused; // toggle pausing
			}
		} 
		if (isOverStopButton) { // mouse click on Quit button
			isRunning = false; // set running to false to terminate
		}
	}

	/*
	 * This method checks to see if the mouse is currently moving over one of
	 * the buttons (Pause, Stop, Show Anim, Pause Anim, and Quit). It sets a
	 * boolean value which will cause the button to be displayed accordingly.
	 */

	private void testMouseMove(int x, int y) {
		if (isRunning) {
			isOverPauseButton = pauseButtonArea.contains(x, y) ? true : false;
			isOverStopButton = stopButtonArea.contains(x, y) ? true : false;
		}
	}

	public int getLevel() {
		return level;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public TileMap getTileMap() {
		return tileMap;
	}

}