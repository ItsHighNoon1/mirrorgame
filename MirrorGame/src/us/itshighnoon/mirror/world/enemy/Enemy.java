package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Physics;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Entity;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Player;

public abstract class Enemy extends Entity {
	protected int hp;
	
	public Enemy(TexturedModel model, Vector2f position, float rotation, float scale, int hp) {
		super(model, position, rotation, scale);
		this.hp = hp;
	}
	
	public int getHp() {
		return hp;
	}
	
	public boolean shoot(Level world) {
		hp--;
		return true;
	}
	
	public abstract void tick(Player player, Level world, float dt);
	
	public abstract String getName();
	
	public boolean isVisible(Player player, Level world) {
		Vector2f currentPosition = new Vector2f(getPosition().x, getPosition().y);
		Vector2f toPlayer = new Vector2f(player.getPosition().x - getPosition().x, player.getPosition().y - getPosition().y);
		float dx = toPlayer.x;
		float dy = toPlayer.y;
		toPlayer.normalize(0.1f);
		while (dx * dx + dy * dy > 0.25f) {
			currentPosition.add(toPlayer);
			dx = player.getPosition().x - currentPosition.x;
			dy = player.getPosition().y - currentPosition.y;
			if (Physics.nearestLine(currentPosition, world.getColliders()) < 0.2f) {
				return false;
			}
		}
		return true;
	}
}
