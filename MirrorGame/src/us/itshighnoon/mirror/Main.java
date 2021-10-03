package us.itshighnoon.mirror;

import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.Renderer;
import us.itshighnoon.mirror.lwjgl.Texture;
import us.itshighnoon.mirror.lwjgl.VAO;
import us.itshighnoon.mirror.lwjgl.Window;
import us.itshighnoon.mirror.lwjgl.shader.ShaderProgram;

public class Main {
	public static void main(String[] args) {
		Window window = new Window(); // window initializes the gl context so it has to go first
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		ShaderProgram shader = new ShaderProgram("res/v.glsl", "res/f.glsl");
		VAO vao = loader.loadQuad();
		Texture texture = loader.loadTexture("res/triangle.png");
		while (!window.shouldClose()) {
			renderer.prepare();
			shader.start();
			renderer.render(vao, texture);
			shader.stop();
			window.poll();
		}
		loader.cleanUp();
		window.cleanUp();
	}
}
