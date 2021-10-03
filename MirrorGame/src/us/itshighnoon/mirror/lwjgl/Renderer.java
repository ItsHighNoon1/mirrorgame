package us.itshighnoon.mirror.lwjgl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer {
	public Renderer() {
		GL11.glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
	}
	
	public void prepare() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public void render(VAO vao, Texture texture) {
		GL30.glBindVertexArray(vao.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, vao.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
	}
}
