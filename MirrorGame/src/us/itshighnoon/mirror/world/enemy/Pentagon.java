package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Main;
import us.itshighnoon.mirror.Physics;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Particle;
import us.itshighnoon.mirror.world.Player;

public class Pentagon extends Enemy {
	private int dodges;
	private boolean shouldDodge;
	
	public Pentagon(TexturedModel model, Vector2f position, float rotation, float scale) {
		super(model, position, rotation, scale, 1);
		dodges = 2;
	}
	
	@Override
	public boolean shoot() {
		if (dodges > 0) {
			shouldDodge = true;
			return false;
		} else {
			return super.shoot();
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
