import javax.sound.sampled.AudioInputStream; // for playing sound clips
import javax.sound.sampled.*;
import java.io.*;

import java.util.HashMap; // for storing sound clips

public class SoundManager { // a Singleton class
	HashMap<String, Clip> clips;

	Clip hitClip = null; // played when bat hits ball
	Clip appearClip = null; // played when ball is re-generated
	Clip backgroundClip = null; // played continuously after ball is created

	private static SoundManager instance = null; // keeps track of Singleton instance

	private SoundManager() {
		clips = new HashMap<String, Clip>();

		Clip clip = loadClip("sounds/background.wav");
		clips.put("background", clip);

		clip = loadClip("sounds/hitSound.wav");
		clips.put("hit", clip); 

		clip = loadClip("sounds/appearSound.wav");
		clips.put("appear", clip); 

		clip = loadClip("sounds/BirdSound.wav");
		clips.put("birdSound", clip);

		clip = loadClip("sounds/super-mario-coin-sound.wav");
		clips.put("coinSound", clip); 

		clip = loadClip("sounds/hurt.wav");
		clips.put("playerHurt", clip); 

		clip = loadClip("sounds/chomp.wav");
		clips.put("sharkBite", clip); 

		clip = loadClip("sounds/shock.wav");
		clips.put("shock", clip); 

		clip = loadClip("sounds/sharkHurt.wav");
		clips.put("sharkHurt", clip);
	}

	public static SoundManager getInstance() {
		if (instance == null)
			instance = new SoundManager();

		return instance;
	}

	public Clip getClip(String title) {

		return clips.get(title); 
	}

	public Clip loadClip(String fileName) {
		AudioInputStream audioIn;
		Clip clip = null;

		try {
			File file = new File(fileName);
			audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL());
			clip = AudioSystem.getClip();
			clip.open(audioIn);
		} catch (Exception e) {
			System.out.println("Error opening sound files: " + e);
		}
		return clip;
	}

	public void playSound(String title, Boolean looping) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.setFramePosition(0);
			if (looping)
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			else
				clip.start();
		}
	}

	public void stopSound(String title) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.stop();
		}
	}

}