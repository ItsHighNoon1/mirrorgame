package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Physics;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Player;

public class Octagon extends Enemy {
	private boolean invulnerable;
	private int dupes;
	
	public Octagon(TexturedModel model, Vector2f position, float rotation, float scale) {
		this(model, position, rotation, scale, 2);
	}
	
	public Octagon(TexturedModel model, Vector2f position, float rotation, float scale, int dupes) {
		super(model, position, rotation, scale, 1);
		this.dupes = dupes;
	}
	
	@Override
	public boolean shoot(Level world) {
		if (invulnerable) {
			return false;
		} else {
			if (dupes > 0) {
				Octagon o1 = new Octagon(getModel(), new Vector2f(getPosition().x + 0.3f, getPosition().y), 0.0f, getScale() / 1.5f, dupes - 1);
				Octagon o2 = new Octagon(getModel(), new Vector2f(getPosition().x - 0.3f, getPosition().y), 0.0f, getScale() / 1.5f, dupes - 1);
				o1.invulnerable = true;
				o2.invulnerable = true;
				world.addEnemy(o1);
				world.addEnemy(o2);
			}
			return super.shoot(world);
		}
	}

	@Override
	public void tick(Player player, Level world, float dt) {
		invulnerable = false;
		Vector2f toPlayer = new Vector2f(player.getPosition().x - getPosition().x, player.getPosition().y - getPosition().y);
		float dist = toPlayer.length();
		toPlayer.div(dist);
		setRotation((float)Math.atan2(toPlayer.y, toPlayer.x));
		if (dist < 0.5f) {
			player.increaseHp(-1);
			hp = 0;
		}
		if (isVisible(player, world)) {
			increasePosition(toPlayer.x * dt / getScale(), toPlayer.y * dt / getScale());
		}
		Physics.collideCircle(getPosition(), getScale(), world.getColliders());
	}

	@Override
	public String getName() {
		return "octagon";
	}
}
