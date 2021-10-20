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
import us.itshighnoon.mirror.world.Player;
import us.itshighnoon.mirror.world.enemy.Enemy;

public class Main {
	// These will be used a lot and we can save memory by allocating them once instead of per entity
	public static TexturedModel triangle;
	public static TexturedModel square;
	public static TexturedModel pentagon;
	public static TexturedModel hexagon;
	public static TexturedModel octagon;
	public static TexturedModel vaporTrail;
	public static TexturedModel muzzleFlash;
	public static TexturedModel blood;
	public static TexturedModel bulletCasing;
	public static TexturedModel smoke;
	public static TexturedModel heart;
	public static TexturedModel bullet;
	public static TexturedModel exitSign;
	public static Sound shootSound;
	public static Random rand;
	public static AudioEngine audio;
	
	public static void main(String[] args) {
		audio = new AudioEngine();
		if (args.length > 0 && "e".equals(args[0])) {
			// The thinking is that I can run the editor on my Mac which doesn't have OpenGL
			new Editor();
			return;
		}
		
		Window window = new Window(); // window initializes the gl context so it has to go first
		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		rand = new Random();
		
		VAO vao = loader.loadQuad();
		Framebuffer displayBuffer = window.getFramebuffer();
		Framebuffer preRender = loader.createFbo(2048, 2048);
		
		triangle = new TexturedModel(vao, loader.loadTexture("res/texture/triangle.png"));
		square = new TexturedModel(vao, loader.loadTexture("res/texture/square.png"));
		pentagon = new TexturedModel(vao, loader.loadTexture("res/texture/pentagon.png"));
		hexagon = new TexturedModel(vao, loader.loadTexture("res/texture/hexagon.png"));
		octagon = new TexturedModel(vao, loader.loadTexture("res/texture/octagon.png"));
		vaporTrail = new TexturedModel(vao, loader.loadTexture("res/texture/vapor_trail.png"));
		muzzleFlash = new TexturedModel(vao, loader.loadTexture("res/texture/muzzle_flash.png"));
		blood = new TexturedModel(vao, loader.loadTexture("res/texture/blood.png"));
		bulletCasing = new TexturedModel(vao, loader.loadTexture("res/texture/bullet_case.png"));
		smoke = new TexturedModel(vao, loader.loadTexture("res/texture/smoke.png"));
		heart = new TexturedModel(vao, loader.loadTexture("res/texture/heart.png"));
		bullet = new TexturedModel(vao, loader.loadTexture("res/texture/bullet.png"));
		exitSign = new TexturedModel(vao, loader.loadTexture("res/texture/exit.png"));
		
		String levelFile = "res/level/industry.txt";
		Level level = new Level(levelFile, loader);
		renderer.submitWalls(level.getWalls());
		renderer.submitReflectors(level.getMirrors());
		
		shootSound = audio.loadSound("res/sound/shoot.wav", -20.0f, 100);
		if (level.getMusic() != null) {
			audio.playMusic(level.getMusic());
		}
		
		Entity reflectedView = new Entity(new TexturedModel(vao, preRender), new Vector2f(0.0f, 0.0f), 0.0f, 2.0f);
		Entity cam = new Entity(null, new Vector2f(-1.0f, 1.0f), 0.0f, 5.0f);
		Player player = new Player(new TexturedModel(vao, loader.loadTexture("res/texture/circle.png")), new Vector2f(level.getSpawn().x, level.getSpawn().y), 0.0f, 0.5f);
		Entity gun = new Entity(new TexturedModel(vao, loader.loadTexture("res/texture/block_td.png")), new Vector2f(0.0f, 0.0f), 0.0f, 0.5f);
		Entity[] hearts = new Entity[player.getHp()];
		for (int i = 0; i < hearts.length; i++) {
			hearts[i] = new Entity(heart, new Vector2f(-0.5f + 0.5f * i, 4.5f), 0.0f, 0.45f);
		}
		Entity[] bullets = new Entity[player.getAmmo()];
		for (int i = 0; i < bullets.length; i++) {
			bullets[i] = new Entity(bulletCasing, new Vector2f(-3.0f + 0.15f * i, -4.5f), -1.571f, 0.15f);
		}
		
		Input input = new Input();
		
		while (!window.shouldClose()) {
			// Tick the player
			Vector2f velocity = new Vector2f(0.0f);
			velocity.y += 2.3f * input.forward;
			velocity.y -= 2.3f * input.backward;
			velocity.x -= 2.3f * input.left;
			velocity.x += 2.3f * input.right;
			player.increasePosition(velocity.x * window.getFrameTime(), velocity.y * window.getFrameTime());
			player.tick(window.getFrameTime());
			if (player.getHp() <= 0) {
				level = new Level(levelFile, loader);
				player.setHp(3);
				player.setAmmo(40);
				player.setPosition(level.getSpawn().x, level.getSpawn().y);
			}
			Physics.collideCircle(player.getPosition(), player.getScale(), level.getColliders());
			
			// If the player is in the exit area, load the next level
			float exitDistanceX = player.getPosition().x - level.getExit().x;
			float exitDistanceY = player.getPosition().y - level.getExit().y;
			if (exitDistanceX * exitDistanceX + exitDistanceY * exitDistanceY < 0.25f && level.getNextLevel() != null) {
				levelFile = level.getNextLevel();
				level = new Level(levelFile, loader);
				renderer.submitWalls(level.getWalls());
				renderer.submitReflectors(level.getMirrors());
				if (level.getMusic() != null) {
					audio.playMusic(level.getMusic());
				}
				player.setHp(3);
				player.setAmmo(40);
				player.setPosition(level.getSpawn().x, level.getSpawn().y);
			}
			
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
			if (input.shoot && player.getAmmo() > 0) {
				audio.playSound(shootSound);
				player.increaseAmmo(-1);
				Particle bulletCase = new Particle(bulletCasing, new Vector2f(player.getPosition().x + aimDirection.y * 0.25f, player.getPosition().y - aimDirection.x * 0.25f), rand.nextFloat() * 6.28f, 0.1f, 1.0f);
				bulletCase.setVelocity(new Vector2f(aimDirection.y * (rand.nextFloat() + 1.0f) * 5.0f, -aimDirection.x * (rand.nextFloat() + 1.0f) * 5.0f), 10.0f);
				bulletCase.setAngularVelocity((rand.nextFloat() - 0.5f) * 10.0f, 10.0f);
				level.addParticle(bulletCase);
				
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
							if (e.shoot(level)) {
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
					float distToWall = Physics.nearestLine(currentLocation, level.getColliders());
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
			
			// Draw gui
			cam.setPosition(0.0f, 0.0f);
			for (int i = 0; i < hearts.length && i < player.getHp(); i++) {
				renderer.submitBase(hearts[i]);
			}
			for (int i = 0; i < bullets.length && i < player.getAmmo(); i++) {
				renderer.submitBase(bullets[i]);
			}
			renderer.drawOver(cam, displayBuffer);
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
