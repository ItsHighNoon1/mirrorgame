package us.itshighnoon.mirror.audio;

public class Sound {
	private String file;
	private float volume;
	private float pitch;
	private long loopPoint;
	
	protected Sound(String file, float volume, float pitch) {
		this.file = file;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	protected Sound(String file, float volume, float pitch, long loopPoint) {
		this(file, volume, pitch);
		this.loopPoint = loopPoint;
	}
	
	public String getSource() {
		return file;
	}
	
	public long getLoopPoint() {
		return loopPoint;
	}
	
	protected float getVolume() {
		return volume;
	}
	
	protected float getPitch() {
		return pitch;
	}
}
