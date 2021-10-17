package us.itshighnoon.mirror;

import java.util.Iterator;
import java.util.Random;

import org.joml.Vector2f;

import us.itshighnoon.mirror.audio.AudioEngine;
import us.itshighnoon.mirror.audio.Sound;
import us.itshighnoon.mirror.editor.Editor;
import us.itshighnoon.mirror.lwjgl.Input;
import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.Renderer;
import us.itshighnoon.mirror.lwjgl.Window;
import us.itshighnoon.mirror.lwjgl.object.Framebuffer;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.lwjgl.object.VAO;
import us.itshighnoon.mirror.world.Entity;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Particle;
import us.itshighnoon.mirror.world.enemy.Enemy;

public class Main {
	public static void main(String[] args) {
		if (args.length > 0 && "e".equals(args[0])) {
			new Editor();
			return;
		}
		
		Random rand = new Random();
		Window window = new Window(); // window initializes the gl context so it has to go first
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		AudioEngine audio = new AudioEngine();
		
		VAO vao = loader.loadQuad();
		Framebuffer displayBuffer = window.getFramebuffer();
		Framebuffer preRender = loader.createFbo(2048, 2048);
		
		Level level = new Level("res/level/test.txt", loader);
		renderer.submitWalls(level.getWalls());
		renderer.submitReflectors(level.getMirrors());
		
		// These will be used a lot and we can save memory by allocating them once instead of per entity
		TexturedModel vaporTrail = new TexturedModel(vao, loader.loadTexture("res/texture/vapor_trail.png"));
		TexturedModel muzzleFlash = new TexturedModel(vao, loader.loadTexture("res/texture/muzzle_flash.png"));
		TexturedModel blood = new TexturedModel(vao, loader.loadTexture("res/texture/blood.png"));
		
		Sound shootSound = audio.loadSound("res/sound/shoot.wav", -15.0f, 10);
		Sound music = audio.loadMusic("res/sound/music.wav", -10.0f, 176003);
		audio.playSound(music);
		
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
				audio.playSound(shootSound);
				
				Vector2f shotDirection = new Vector2f(input.aimLocation.x - gun.getPosition().x, input.aimLocation.y - gun.getPosition().y);
				shotDirection.normalize(0.1f);
				Vector2f currentLocation = new Vector2f(gun.getPosition().x, gun.getPosition().y);
				currentLocation.x += shotDirection.x * 2.5f;
				currentLocation.y += shotDirection.y * 2.5f;
				Particle flash = new Particle(muzzleFlash, new Vector2f(currentLocation.x + shotDirection.x * 2.5f, currentLocation.y + shotDirection.y * 2.5f), gun.getRotation(), 0.5f, 0.05f);
				boolean hit = false;
				for (int i = 0; i < 100; i++) {
					// Draw vapor trail
					Particle trail = new Particle(vaporTrail, new Vector2f(currentLocation.x, currentLocation.y), gun.getRotation(), 0.1f, 0.2f);
					trail.setVelocity(new Vector2f(i * 0.005f * rand.nextFloat(), i * 0.005f * rand.nextFloat()), 1.0f);
					level.addParticle(trail);
					currentLocation.add(shotDirection);
					if (hit) break;
					
					// Check if we shot an enemy
					for (Enemy e : level.getEnemies()) {
						float dxEnemy = e.getPosition().x - currentLocation.x;
						float dyEnemy = e.getPosition().y - currentLocation.y;
						float enemyRadius2 = e.getScale() * e.getScale() * 0.25f;
						if (dxEnemy * dxEnemy + dyEnemy * dyEnemy < enemyRadius2) {
							if (e.shoot()) {
								hit = true;
								Vector2f bloodPos = new Vector2f(currentLocation.x + shotDirection.x * 5.0f, currentLocation.y + shotDirection.y * 5.0f);
								Particle bloodParticle = new Particle(blood, bloodPos, gun.getRotation(), 1.0f, -1.0f);
								bloodParticle.setVelocity(shotDirection.mul(10.0f), 10.0f);
								level.addParticle(bloodParticle);
								break;
							}
						}
					}
					
					// Check if we shot a wall
					float distToWall = Physics.distToLine(currentLocation, level.getColliders());
					if (distToWall < 0.1f) hit = true;
				}
				level.addParticle(flash);
			}
			
			// Submit objects to renderer and tick enemies + particles
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
				e.tick(player, level, window.getFrameTime());
				if(e.getHp() <= 0) {
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
		
		// Delete OpenGL objects. Society if Java had destructors
		renderer.cleanUp();
		loader.cleanUp();
		window.cleanUp();
		
		// Delete clips that havent killed themselves
		audio.cleanUp();
	}
}
