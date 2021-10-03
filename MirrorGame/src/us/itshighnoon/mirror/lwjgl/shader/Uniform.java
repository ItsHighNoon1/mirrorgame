package us.itshighnoon.mirror.lwjgl.shader;

import org.lwjgl.opengl.GL20;

public abstract class Uniform {
	private static final int NOT_FOUND = -1;
	
	private String name;
	private int location;
	
	public Uniform(String name) {
		this.name = name;
	}
	
	protected void storeUniformLocation(int programId) {
		location = GL20.glGetUniformLocation(programId, name);
		if (location == NOT_FOUND) {
			System.err.println("Could not find uniform " + name + " in program " + programId);
		}
	}
	
	protected int getLocation() {
		return location;
	}
}
