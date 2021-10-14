package us.itshighnoon.mirror;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.Input;
import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.Renderer;
import us.itshighnoon.mirror.lwjgl.Window;
import us.itshighnoon.mirror.lwjgl.object.Framebuffer;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.lwjgl.object.VAO;

public class Main {
	public static void main(String[] args) {
		Window window = new Window(); // window initializes the gl context so it has to go first
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		
		VAO vao = loader.loadQuad();
		Framebuffer displayBuffer = window.getFramebuffer();
		Framebuffer preRender = loader.createFbo(2048, 2048);
		TexturedModel triangle = new TexturedModel(vao, loader.loadTexture("res/texture/triangle.png"));
		TexturedModel square = new TexturedModel(vao, loader.loadTexture("res/texture/square.png"));
		TexturedModel circle = new TexturedModel(vao, loader.loadTexture("res/texture/circle.png"));
		TexturedModel fboView = new TexturedModel(vao, preRender);
		
		Entity floor = new Entity(new TexturedModel(vao, loader.loadTexture("res/texture/grass.png")), new Vector2f(2.5f, 2.5f), 0.0f, 5.0f);
		
		Entity cam = new Entity(null, new Vector2f(-1.0f, 1.0f), 0.0f, 5.0f);
		Entity player = new Entity(circle);
		player.setScale(0.5f);
		
		List<Entity> walls = new ArrayList<Entity>();
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j <= i; j++) {
				walls.add(new Entity(square, new Vector2f(i, j), 0.0f, 1.0f));
			}
		}
		
		Entity reflectedView = new Entity(fboView, new Vector2f(0.0f, 0.0f), 0.0f, 2.0f);
		
		Input input = new Input();
		
		Mirror mirror1 = new Mirror(new Vector2f(0.0f, 0.0f), new Vector2f(0.0f, 5.0f));
		Mirror mirror2 = new Mirror(new Vector2f(0.0f, 5.0f), new Vector2f(5.0f, 0.0f));
		renderer.submitReflectors(mirror1, mirror2);
		
		while (!window.shouldClose()) {
			displayBuffer.setSize(window.getWindowDims());
			renderer.submitBase(floor);
			
			if (input.forward) player.increasePosition(0.0f, 0.05f);
			if (input.backward) player.increasePosition(0.0f, -0.05f);
			if (input.left) player.increasePosition(-0.05f, 0.0f);
			if (input.right) player.increasePosition(0.05f, 0.0f);
			cam.setPosition(player.getPosition().x, player.getPosition().y);
			renderer.submitBase(player);
			
			for (Entity e : walls) {
				renderer.submitBase(e);
			}
			
			renderer.drawBase(cam, preRender);
			renderer.drawReflected(reflectedView, cam, displayBuffer);
			
			window.poll(input);
		}
		loader.cleanUp();
		window.cleanUp();
	}
}
