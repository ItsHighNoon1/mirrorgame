package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Main;
import us.itshighnoon.mirror.Physics;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Particle;
import us.itshighnoon.mirror.world.Player;

public class Triangle extends Enemy {
	private Particle projectile;
	private float shootTimer = 1.0f;
	
	public Triangle(TexturedModel model, Vector2f position, float rotation, float scale) {
		super(model, position, rotation, scale, 3);
	}
	
	@Override
	public boolean shoot(Level world) {
		if (projectile != null) {
			projectile.tick(999.9f);
		}
		return super.shoot(world);
	}

	@Override
	public void tick(Player player, Level world, float dt) {
		Vector2f toPlayer = new Vector2f(player.getPosition().x - getPosition().x, player.getPosition().y - getPosition().y);
		toPlayer.normalize();
		if (isVisible(player, world)) {
			setRotation((float)Math.atan2(toPlayer.y, toPlayer.x));
			increasePosition(toPlayer.x * dt, toPlayer.y * dt);
			Physics.collideCircle(getPosition(), getScale(), world.getColliders());
			
			shootTimer -= dt;
			if (shootTimer < 0.0f) {
				shootTimer = 5.0f;
				projectile = new Particle(Main.triangle, new Vector2f(getPosition().x, getPosition().y), getRotation(), 0.1f, 3.0f);
				projectile.setVelocity(toPlayer.mul(2.0f), 0.0f);
				world.addParticle(projectile);
			}
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
			}
		}
	}

	@Override
	public String getName() {
		return "triangle";
	}
}
