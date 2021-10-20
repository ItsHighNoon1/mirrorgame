package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Physics;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Player;

public class Square extends Enemy {
	private static final float FLEE_TIME = 1.0f;
	
	private float runningTime = 0.0f;
	
	public Square(TexturedModel model, Vector2f position, float rotation, float scale) {
		super(model, position, rotation, scale, 3);
	}

	@Override
	public void tick(Player player, Level world, float dt) {
		Vector2f toPlayer = new Vector2f(player.getPosition().x - getPosition().x, player.getPosition().y - getPosition().y);
		float dist = toPlayer.length();
		toPlayer.div(dist);
		setRotation((float)Math.atan2(toPlayer.y, toPlayer.x));
		if (dist < 0.5f) {
			player.increaseHp(-1);
			runningTime = FLEE_TIME;
		}
		runningTime -= dt;
		if (runningTime < 0.0f && dist < 5.0) {
			increasePosition(toPlayer.x * dt * 2.0f, toPlayer.y * dt * 2.0f);
		} else if (runningTime > 0.0f) {
			increasePosition(-toPlayer.x * dt * 2.0f, -toPlayer.y * dt * 2.0f);
		}
		Physics.collideCircle(getPosition(), getScale(), world.getColliders());
	}

	@Override
	public String getName() {
		return "square";
	}
}
