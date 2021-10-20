package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

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
}
