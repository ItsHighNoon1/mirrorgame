package us.itshighnoon.mirror.lwjgl.shader;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform {
	public UniformFloat(String name) {
		super(name);
	}
	
	public void loadFloat(float f) {
		GL20.glUniform1f(getLocation(), f);
	}
}
