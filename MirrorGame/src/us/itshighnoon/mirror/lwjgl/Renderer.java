package us.itshighnoon.mirror.lwjgl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import us.itshighnoon.mirror.lwjgl.shader.ShaderProgram;

public class Renderer {
	public Renderer() {
		GL11.glClearColor(0.3f, 0.1f, 0.3f, 1.0f);
	}
	
	public void prepare() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public void render(VAO vao, ShaderProgram shader) {
		GL30.glBindVertexArray(vao.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.start();
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, vao.getVertexCount());
		shader.stop();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
	}
}
