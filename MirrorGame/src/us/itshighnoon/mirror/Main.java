package us.itshighnoon.mirror;

import org.joml.Vector2f;
import org.joml.Vector2i;

import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.Renderer;
import us.itshighnoon.mirror.lwjgl.TexturedModel;
import us.itshighnoon.mirror.lwjgl.VAO;
import us.itshighnoon.mirror.lwjgl.Window;
import us.itshighnoon.mirror.lwjgl.shader.Shader;
import us.itshighnoon.mirror.lwjgl.shader.UniformMat4;

public class Main {
	public static void main(String[] args) {
		Window window = new Window(); // window initializes the gl context so it has to go first
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		Shader shader = new Shader("res/v.glsl", "res/f.glsl");
		UniformMat4 matrix = new UniformMat4("u_mvpMatrix");
		shader.storeAllUniformLocations(matrix);
		
		VAO vao = loader.loadQuad();
		TexturedModel triangle = new TexturedModel(vao, loader.loadTexture("res/triangle.png"));
		TexturedModel square = new TexturedModel(vao, loader.loadTexture("res/square.png"));
		Entity ent = new Entity(triangle, new Vector2f(-1.0f, 1.0f), 0.3f);
		Entity ent2 = new Entity(square, new Vector2f(1.0f, 0.0f), -0.5f);
		Camera cam = new Camera(-1.0f, 1.0f);
		
		while (!window.shouldClose()) {
			renderer.prepare();
			
			Vector2i dims = window.getWindowDims();
			
			renderer.submit(ent);
			renderer.submit(ent2);
			renderer.drawBase(shader, matrix, cam, dims);
			window.poll();
		}
		loader.cleanUp();
		window.cleanUp();
	}
}
