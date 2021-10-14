package us.itshighnoon.mirror.world;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.object.TexturedModel;

public abstract class Enemy extends Entity {
	public Enemy(TexturedModel model, Vector2f position, float rotation, float scale) {
		super(model, position, rotation, scale);
	}
	
	public abstract void tick(Entity player, Level world);
}
