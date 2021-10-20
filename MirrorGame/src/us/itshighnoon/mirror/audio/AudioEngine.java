package us.itshighnoon.mirror.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioEngine {
	private Map<Sound, List<Clip>> clipPool;
	private Sound music;
	
	public AudioEngine() {
		clipPool = new HashMap<Sound, List<Clip>>();
	}
	
	public Sound loadSound(String file, float volume, int allocation) {
		Sound s = new Sound(file, volume, 0.0f);
		List<Clip> clips = new ArrayList<Clip>();
		try {
			for (int i = 0; i < allocation; i++) {
				Clip clip;
				clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(new File(file)));
				FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
				gainControl.setValue(volume);
				clips.add(clip);
			}
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		clipPool.put(s, clips);
		return s;
	}
	
	public Sound loadMusic(String file, float volume, long loopPoint) {
		Sound m = new Sound(file, volume, 0.0f, loopPoint);
		clipPool.put(m, new ArrayList<Clip>());
		return m;
	}
	
	public void playSound(Sound sound) {
		List<Clip> clips = clipPool.get(sound);
		for (Clip c : clips) {
			if (!c.isRunning()) {
				c.setFramePosition(0);
				c.start();
				return;
			}
		}
		try {
			// There was no available clip, make a new one
			Clip clip;
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(sound.getSource())));
			FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(sound.getVolume());
			if (sound.getLoopPoint() > 0) {
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				clip.setLoopPoints((int)sound.getLoopPoint(), -1);
			}
			clip.start();
			clips.add(clip);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public void playMusic(Sound sound) {
		int cutPosition = 0;
		
		if (music != null) {
			List<Clip> clips = clipPool.get(music);
			for (Clip c : clips) {
				if (c.isRunning()) {
					cutPosition = c.getFramePosition();
					c.stop();
				}
			}
		}
		
		music = sound;
		
		List<Clip> clips = clipPool.get(music);
		for (Clip c : clips) {
			if (!c.isRunning()) {
				c.setFramePosition(cutPosition);
				c.start();
				return;
			}
		}
		try {
			// There was no available clip, make a new one
			Clip clip;
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(sound.getSource())));
			FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(sound.getVolume());
			if (sound.getLoopPoint() > 0) {
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				clip.setLoopPoints((int)sound.getLoopPoint(), -1);
			}
			clip.setFramePosition(cutPosition);
			clip.start();
			clips.add(clip);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public void cleanUp() {
		for (List<Clip> clips : clipPool.values()) {
			for (Clip clip : clips) {
				clip.stop();
				clip.close();
			}
		}
	}
}
