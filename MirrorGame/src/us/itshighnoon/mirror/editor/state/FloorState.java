package us.itshighnoon.mirror.editor.state;

import java.util.Iterator;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Entity;
import us.itshighnoon.mirror.world.Level;

public class FloorState implements EditorState {
	private TexturedModel floorTexture;
	private boolean deleting;
	
	public void setFloorTexture(TexturedModel texture) {
		floorTexture = texture;
	}
	
	@Override
	public void pressLeft(Level level, Vector2f pos) {
		if (floorTexture != null) {
			pos.x = pos.x - pos.x % 0.5f;
			pos.y = pos.y - pos.y % 0.5f;
			level.addFloor(new Entity(floorTexture, pos, 0.0f, 1.0f));
		}
	}

	@Override
	public void releaseLeft(Level level, Vector2f pos) {}

	@Override
	public void pressRight(Level level, Vector2f pos) {
		deleting = true;
	}

	@Override
	public void releaseRight(Level level, Vector2f pos) {
		deleting = false;
	}

	@Override
	public void drag(Level level, Vector2f pos) {
		if (deleting) {
			Iterator<Entity> it = level.getFloors().iterator();
			while (it.hasNext()) {
				Entity floor = it.next();
				float dx = floor.getPosition().x - pos.x;
				float dy = floor.getPosition().y - pos.y;
				if (dx * dx + dy * dy < 0.25f) {
					it.remove();
				}
			}
		}
	}
}
