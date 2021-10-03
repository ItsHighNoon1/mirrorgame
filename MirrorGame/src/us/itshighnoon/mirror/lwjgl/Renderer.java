package us.itshighnoon.mirror.lwjgl;

import org.lwjgl.opengl.GL11;

import us.itshighnoon.mirror.World;

public class Renderer {
	public Renderer() {
		
	}
	
	public void prepare() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public void render(World w) {
		
	}
}
