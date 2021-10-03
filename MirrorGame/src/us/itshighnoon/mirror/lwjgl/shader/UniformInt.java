package us.itshighnoon.mirror.lwjgl.shader;

import org.lwjgl.opengl.GL20;

public class UniformInt extends Uniform {
	public UniformInt(String name) {
		super(name);
	}
	
	public void loadInt(int i) {
		GL20.glUniform1i(getLocation(), i);
	}
}
