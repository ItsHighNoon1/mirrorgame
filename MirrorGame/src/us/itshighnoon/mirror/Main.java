package us.itshighnoon.mirror;

import org.joml.Matrix4f;
import org.joml.Vector2i;

import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.Renderer;
import us.itshighnoon.mirror.lwjgl.TexturedModel;
import us.itshighnoon.mirror.lwjgl.VAO;
import us.itshighnoon.mirror.lwjgl.Window;
import us.itshighnoon.mirror.lwjgl.shader.ShaderProgram;
import us.itshighnoon.mirror.lwjgl.shader.UniformMat4;

public class Main {
	public static void main(String[] args) {
		Window window = new Window(); // window initializes the gl context so it has to go first
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		ShaderProgram shader = new ShaderProgram("res/v.glsl", "res/f.glsl");
		UniformMat4 matrix = new UniformMat4("u_mvpMatrix");
		shader.storeAllUniformLocations(matrix);
		
		VAO vao = loader.loadQuad();
		TexturedModel triangle = new TexturedModel(vao, loader.loadTexture("res/triangle.png"));
		TexturedModel square = new TexturedModel(vao, loader.loadTexture("res/square.png"));
		
		while (!window.shouldClose()) {
			renderer.prepare();
			shader.start();
			
			Vector2i dims = window.getWindowDims();
			float aspect = (float)dims.x / (float)dims.y;
			Matrix4f mvpMatrix = new Matrix4f();
			mvpMatrix = mvpMatrix.setOrtho(-aspect, aspect, -1.0f, 1.0f, -1.0f, 1.0f);
			mvpMatrix = mvpMatrix.translate(-1.0f, 0.0f, 0.0f);
			matrix.loadMatrix(mvpMatrix);
			
			renderer.render(triangle);
			shader.stop();
			window.poll();
		}
		loader.cleanUp();
		window.cleanUp();
	}
}
