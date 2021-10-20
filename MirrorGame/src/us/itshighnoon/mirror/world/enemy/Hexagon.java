package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Main;
import us.itshighnoon.mirror.Physics;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Particle;
import us.itshighnoon.mirror.world.Player;

public class Hexagon extends Enemy {
	private float shootTimer;
	private float autofire;
	
	public Hexagon(TexturedModel model, Vector2f position, float rotation, float scale) {
		super(model, position, rotation, scale, 3);
		shootTimer = 2.0f;
		autofire = 0.0f;
	}

	@Override
	public void tick(Player player, Level world, float dt) {
		Vector2f toPlayer = new Vector2f(player.getPosition().x - getPosition().x, player.getPosition().y - getPosition().y);
		float dist = toPlayer.length();
		if (dist < 5.0f) {
			shootTimer -= dt;
			if (shootTimer < 0.0f) {
				increaseRotation(10.0f * dt);
				autofire -= dt;
				if (autofire < 0.0f) {
					autofire = 0.05f;
					Main.audio.playSound(Main.shootSound);
					Particle flash = new Particle(Main.muzzleFlash, new Vector2f(getPosition().x + (float)Math.cos(getRotation()) * 0.5f, getPosition().y + (float)Math.sin(getRotation()) * 0.5f), getRotation(), 0.5f, 0.05f);
					Vector2f shotDirection = new Vector2f((float)Math.cos(getRotation()) * 0.1f, (float)Math.sin(getRotation()) * 0.1f);
					Vector2f currentLocation = new Vector2f(getPosition().x + (float)Math.cos(getRotation()) * 0.5f, getPosition().y + (float)Math.sin(getRotation()) * 0.5f);
					for (int i = 0; i < 100; i++) {
						// Draw vapor trail
						Particle trail = new Particle(Main.vaporTrail, new Vector2f(currentLocation.x, currentLocation.y), getRotation(), 0.1f, 0.2f);
						world.addParticle(trail);
						currentLocation.add(shotDirection);
						
						// Check if we shot the player
						Vector2f projToPlayer = new Vector2f(player.getPosition().x - currentLocation.x, player.getPosition().y - currentLocation.y);
						float projDist = projToPlayer.length();
						if (projDist < 0.25f) {
							player.increaseHp(-1);
							break;
						}
						
						// Check if we shot a wall
						float distToWall = Physics.nearestLine(currentLocation, world.getColliders());
						if (distToWall < 0.2f) break;
					}
					world.addParticle(flash);
				}
				if (shootTimer < -2.0f) {
					shootTimer = 3.0f;
				}
			} else {
				setRotation((float)Math.atan2(toPlayer.y, toPlayer.x));
			}
		} else {
			shootTimer = 2.0f;
		}
	}

	@Override
	public String getName() {
		return "hexagon";
	}
}
