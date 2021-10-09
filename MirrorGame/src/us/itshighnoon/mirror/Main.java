package us.itshighnoon.mirror;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import us.itshighnoon.mirror.lwjgl.Input;
import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.Renderer;
import us.itshighnoon.mirror.lwjgl.Window;
import us.itshighnoon.mirror.lwjgl.object.Framebuffer;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.lwjgl.object.VAO;
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
		Framebuffer preRender = loader.createFbo(2048, 2048);
		TexturedModel triangle = new TexturedModel(vao, loader.loadTexture("res/triangle.png"));
		TexturedModel square = new TexturedModel(vao, loader.loadTexture("res/square.png"));
		TexturedModel fboView = new TexturedModel(vao, preRender);
		
		Entity cam = new Entity(null, new Vector2f(-1.0f, 1.0f), 0.0f, 3.0f);
		Entity player = new Entity(triangle);
		player.setScale(0.5f);
		
		List<Entity> walls = new ArrayList<Entity>();
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j <= i; j++) {
				walls.add(new Entity(square, new Vector2f(i, j), 0.0f, 1.0f));
			}
		}
		
		Entity view = new Entity(fboView);
		view.setScale(3.0f);
		
		Input input = new Input();
		
		while (!window.shouldClose()) {
			Vector2i framebufferDims = new Vector2i(2048, 2048);
			Vector2i screenDims = window.getWindowDims();
			
			if (input.forward) player.increasePosition(0.0f, 0.05f);
			if (input.backward) player.increasePosition(0.0f, -0.05f);
			if (input.left) player.increasePosition(-0.05f, 0.0f);
			if (input.right) player.increasePosition(0.05f, 0.0f);
			cam.setPosition(player.getPosition().x, player.getPosition().y);
			renderer.submit(player);
			
			for (Entity e : walls) {
				renderer.submit(e);
			}
			
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, preRender.getFramebufferId());
			
			renderer.prepare();
			renderer.drawBase(shader, matrix, cam, framebufferDims);

			renderer.submit(view);
			view.increaseRotation(0.01f);
			cam.setPosition(0.0f, 0.0f);
			
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			GL11.glViewport(0, 0, screenDims.x, screenDims.y);
			renderer.prepare();
			renderer.drawBase(shader, matrix, cam, screenDims);
			
			window.poll(input);
		}
		loader.cleanUp();
		window.cleanUp();
	}
}
