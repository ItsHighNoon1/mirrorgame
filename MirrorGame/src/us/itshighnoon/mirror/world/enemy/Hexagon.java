package us.itshighnoon.mirror.world.enemy;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Player;

public class Hexagon extends Enemy {
	public Hexagon(TexturedModel model, Vector2f position, float rotation, float scale) {
		super(model, position, rotation, scale, 3);
	}

	@Override
	public void tick(Player player, Level world, float dt) {
		
	}

	@Override
	public String getName() {
		return "hexagon";
	}
}
