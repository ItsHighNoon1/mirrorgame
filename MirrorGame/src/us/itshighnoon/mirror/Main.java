package us.itshighnoon.mirror;

import java.util.Iterator;
import java.util.Random;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.Input;
import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.Renderer;
import us.itshighnoon.mirror.lwjgl.Window;
import us.itshighnoon.mirror.lwjgl.object.Framebuffer;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.lwjgl.object.VAO;
import us.itshighnoon.mirror.world.Enemy;
import us.itshighnoon.mirror.world.Entity;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Particle;

public class Main {
	public static void main(String[] args) {
		Random rand = new Random();
		Window window = new Window(); // window initializes the gl context so it has to go first
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		
		VAO vao = loader.loadQuad();
		Framebuffer displayBuffer = window.getFramebuffer();
		Framebuffer preRender = loader.createFbo(2048, 2048);
		
		Level level = new Level("res/level/test.txt", loader);
		renderer.submitWalls(level.getWalls());
		renderer.submitReflectors(level.getMirrors());
		
		TexturedModel vaporTrail = new TexturedModel(vao, loader.loadTexture("res/texture/vapor_trail.png"));
		TexturedModel muzzleFlash = new TexturedModel(vao, loader.loadTexture("res/texture/muzzle_flash.png"));
		
		Entity reflectedView = new Entity(new TexturedModel(vao, preRender), new Vector2f(0.0f, 0.0f), 0.0f, 2.0f);
		Entity cam = new Entity(null, new Vector2f(-1.0f, 1.0f), 0.0f, 5.0f);
		Entity player = new Entity(new TexturedModel(vao, loader.loadTexture("res/texture/circle.png")), new Vector2f(level.getSpawn().x, level.getSpawn().y), 0.0f, 0.5f);
		Entity gun = new Entity(new TexturedModel(vao, loader.loadTexture("res/texture/block_td.png")), new Vector2f(0.0f, 0.0f), 0.0f, 0.5f);
		
		Input input = new Input();
		
		while (!window.shouldClose()) {
			// Tick the player
			Vector2f velocity = new Vector2f(0.0f);
			velocity.y += 3.0f * input.forward;
			velocity.y -= 3.0f * input.backward;
			velocity.x -= 3.0f * input.left;
			velocity.x += 3.0f * input.right;
			player.increasePosition(velocity.x * window.getFrameTime(), velocity.y * window.getFrameTime());
			Physics.collideCircle(player.getPosition(), player.getScale(), level.getColliders());
			
			// Figure out where the player is aiming
			Vector2f aimDirection = new Vector2f(input.aimLocation.x, input.aimLocation.y);
			input.aimLocation.x = input.aimLocation.x * cam.getScale() * 0.5f + player.getPosition().x;
			input.aimLocation.y = input.aimLocation.y * cam.getScale() * 0.5f + player.getPosition().y;
			aimDirection.normalize();
			
			// Move objects that depend on player position
			cam.setPosition(player.getPosition().x, player.getPosition().y);
			gun.setPosition(player.getPosition().x + aimDirection.y * player.getScale() * 0.5f, player.getPosition().y - aimDirection.x * player.getScale() * 0.5f);
			gun.setRotation((float)Math.atan2(input.aimLocation.y - gun.getPosition().y, input.aimLocation.x - gun.getPosition().x));
			
			// Shoot the gun
			if (input.shoot) {
				Vector2f shotDirection = new Vector2f(input.aimLocation.x - gun.getPosition().x, input.aimLocation.y - gun.getPosition().y);
				shotDirection.normalize(0.1f);
				Vector2f currentLocation = new Vector2f(gun.getPosition().x, gun.getPosition().y);
				currentLocation.x += shotDirection.x * 2.5f;
				currentLocation.y += shotDirection.y * 2.5f;
				Particle flash = new Particle(muzzleFlash, new Vector2f(currentLocation.x + shotDirection.x * 2.5f, currentLocation.y + shotDirection.y * 2.5f), gun.getRotation(), 0.5f, 0.05f);
				for (int i = 0; i < 100; i++) {
					Particle trail = new Particle(vaporTrail, new Vector2f(currentLocation.x, currentLocation.y), gun.getRotation(), 0.1f, 0.2f);
					trail.setVelocity(new Vector2f(i * 0.005f * rand.nextFloat(), i * 0.005f * rand.nextFloat()), 1.0f);
					level.addParticle(trail);
					currentLocation.add(shotDirection);
				}
				level.addParticle(flash);
			}
			
			// Submit objects to renderer and tick particles
			for (Entity f : level.getFloors()) {
				renderer.submitBase(f);
			}
			for (Iterator<Particle> it = level.getParticles().iterator(); it.hasNext();) {
				Particle p = it.next();
				if(!p.tick(window.getFrameTime())) {
					it.remove();
				}
				renderer.submitBase(p);
			}
			for (Iterator<Enemy> it = level.getEnemies().iterator(); it.hasNext();) {
				Enemy e = it.next();
				if(!e.tick(player, level, window.getFrameTime())) {
					it.remove();
				}
				renderer.submitBase(e);
			}
			renderer.submitBase(player);
			renderer.submitBase(gun);
			
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
