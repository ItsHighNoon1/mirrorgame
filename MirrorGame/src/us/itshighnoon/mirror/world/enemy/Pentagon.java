package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Entity;
import us.itshighnoon.mirror.world.Level;

public class Pentagon extends Enemy {
	public Pentagon(TexturedModel model, Vector2f position, float rotation, float scale) {
		super(model, position, rotation, scale, 3);
	}

	@Override
	public void tick(Entity player, Level world, float dt) {
		
	}

	@Override
	public String getName() {
		return "pentagon";
	}
}
