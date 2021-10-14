package us.itshighnoon.mirror;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.Input;
import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.Renderer;
import us.itshighnoon.mirror.lwjgl.Window;
import us.itshighnoon.mirror.lwjgl.object.Framebuffer;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.lwjgl.object.VAO;
import us.itshighnoon.mirror.world.Entity;
import us.itshighnoon.mirror.world.Level;

public class Main {
	public static void main(String[] args) {
		Window window = new Window(); // window initializes the gl context so it has to go first
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		
		VAO vao = loader.loadQuad();
		Framebuffer displayBuffer = window.getFramebuffer();
		Framebuffer preRender = loader.createFbo(2048, 2048);
		
		Level level = new Level("res/level/test.txt", loader);
		renderer.submitWalls(level.getWalls());
		renderer.submitReflectors(level.getMirrors());
		
		Entity reflectedView = new Entity(new TexturedModel(vao, preRender), new Vector2f(0.0f, 0.0f), 0.0f, 2.0f);
		Entity cam = new Entity(null, new Vector2f(-1.0f, 1.0f), 0.0f, 5.0f);
		Entity player = new Entity(new TexturedModel(vao, loader.loadTexture("res/texture/circle.png")));
		player.setScale(0.5f);
		player.setPosition(level.getSpawn().x, level.getSpawn().y);
		
		Input input = new Input();
		
		while (!window.shouldClose()) {
			Vector2f velocity = new Vector2f(0.0f);
			if (input.forward) velocity.y += 3.0f;
			if (input.backward) velocity.y -= 3.0f;
			if (input.left) velocity.x -= 3.0f;
			if (input.right) velocity.x += 3.0f;
			player.increasePosition(velocity.x * window.getFrameTime(), velocity.y * window.getFrameTime());
			Physics.collide(player.getPosition(), player.getScale(), level.getColliders());
			cam.setPosition(player.getPosition().x, player.getPosition().y);
			
			// Submit objects to renderer
			for (Entity e : level.getFloors()) {
				renderer.submitBase(e);
			}
			for (Entity e : level.getEnemies()) {
				renderer.submitBase(e);
			}
			renderer.submitBase(player);
			for (Entity e : level.getParticles()) {
				renderer.submitBase(e);
			}
			
			// Draw the scene
			displayBuffer.setSize(window.getWindowDims());
			renderer.drawBase(cam, preRender);
			renderer.drawReflected(reflectedView, cam, displayBuffer);
			window.poll(input);
		}
		renderer.cleanUp();
		loader.cleanUp();
		window.cleanUp();
	}
}
