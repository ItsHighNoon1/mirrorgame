package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Main;
import us.itshighnoon.mirror.Physics;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Particle;
import us.itshighnoon.mirror.world.Player;

public class Pentagon extends Enemy {
	private Particle projectile;
	private float shootTimer = 1.0f;
	private int dodges;
	private boolean shouldDodge;
	
	public Pentagon(TexturedModel model, Vector2f position, float rotation, float scale) {
		super(model, position, rotation, scale, 1);
		dodges = 2;
	}
	
	@Override
	public boolean shoot(Level world) {
		if (dodges > 0) {
			shouldDodge = true;
			return false;
		} else {
			if (projectile != null) {
				projectile.tick(999.9f);
			}
			return super.shoot(world);
		}
	}

	@Override
	public void tick(Player player, Level world, float dt) {
		if (shouldDodge) {
			shouldDodge = false;
			dodges--;
			Particle smokeScreen = new Particle(Main.smoke, new Vector2f(getPosition().x, getPosition().y), 0.0f, 1.0f, 2.0f);
			smokeScreen.setAngularVelocity(5.0f, 0.0f);
			world.addParticle(smokeScreen);
			float dx = getPosition().x - player.getPosition().x;
			float dy = getPosition().y - player.getPosition().y;
			float r = (float)Math.sqrt(dx * dx + dy * dy);
			float ang = Main.rand.nextFloat() * 6.28f;
			dx = (float)Math.cos(ang) * r;
			dy = (float)Math.sin(ang) * r;
			setPosition(player.getPosition().x + dx, player.getPosition().y + dy);
		} else {
			Vector2f toPlayer = new Vector2f(player.getPosition().x - getPosition().x, player.getPosition().y - getPosition().y);
			float dist = toPlayer.length();
			if (dist < 5.0f) {
				shootTimer -= dt;
				if (shootTimer < 0.0f) {
					shootTimer = 5.0f;
					projectile = new Particle(Main.pentagon, new Vector2f(getPosition().x, getPosition().y), getRotation(), 0.1f, 3.0f);
					projectile.setAngularVelocity(10.0f, 0.0f);
					world.addParticle(projectile);
				}
				if (projectile != null && projectile.tick(0.0f)) {
					Vector2f projToPlayer = new Vector2f(player.getPosition().x - projectile.getPosition().x, player.getPosition().y - projectile.getPosition().y);
					float projDist = projToPlayer.length();
					if (projDist < 0.25f) {
						player.increaseHp(-1);
						projectile.tick(999.9f);
						projectile = null;
					} else if (Physics.nearestLine(projectile.getPosition(), world.getColliders()) < 0.2f) {
						projectile.tick(999.9f);
						projectile = null;
					} else {
						projToPlayer.div(projDist);
						projToPlayer.add(projectile.getVelocity());
						projectile.setVelocity(projToPlayer.mul(0.7f), 0.0f);
					}
				}
				
				toPlayer.div(dist);
				setRotation((float)Math.atan2(toPlayer.y, toPlayer.x));
				if (dist > 3.0f) {
					increasePosition(toPlayer.x * dt, toPlayer.y * dt);
				} else if (dist < 2.0f) {
					increasePosition(-toPlayer.x * dt, -toPlayer.y * dt);
				}
				Physics.collideCircle(getPosition(), getScale(), world.getColliders());
			}
		}
	}

	@Override
	public String getName() {
		return "pentagon";
	}
}
