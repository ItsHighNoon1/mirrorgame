package us.itshighnoon.mirror.lwjgl.shader;

import org.lwjgl.opengl.GL20;

public class UniformVec2 extends Uniform {
	public UniformVec2(String name) {
		super(name);
	}
	
	public void loadVec2(float x, float y) {
		GL20.glUniform2f(getLocation(), x, y);
	}
}
