package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Physics;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Entity;
import us.itshighnoon.mirror.world.Level;

public class Triangle extends Enemy {
	public Triangle(TexturedModel model, Vector2f position, float rotation, float scale) {
		super(model, position, rotation, scale, 3);
	}

	@Override
	public void tick(Entity player, Level world, float dt) {
		Vector2f toPlayer = new Vector2f(player.getPosition().x - getPosition().x, player.getPosition().y - getPosition().y);
		float dist = toPlayer.length();
		if (dist < 5.0f) {
			toPlayer.div(dist);
			setRotation((float)Math.atan2(toPlayer.y, toPlayer.x));
			increasePosition(toPlayer.x * dt, toPlayer.y * dt);
			Physics.collideCircle(getPosition(), getScale(), world.getColliders());
		}
	}

	@Override
	public String getName() {
		return "triangle";
	}
}
