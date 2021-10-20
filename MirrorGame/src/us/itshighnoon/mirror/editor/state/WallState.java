package us.itshighnoon.mirror.editor.state;

import java.util.Iterator;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Physics;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Wall;

public class WallState implements EditorState {
	private Vector2f start;
	private boolean deleting;
	
	@Override
	public void pressLeft(Level level, Vector2f pos) {
		pos.x = pos.x - pos.x % 0.5f;
		pos.y = pos.y - pos.y % 0.5f;
		start = pos;
	}

	@Override
	public void releaseLeft(Level level, Vector2f pos) {
		pos.x = pos.x - pos.x % 0.5f;
		pos.y = pos.y - pos.y % 0.5f;
		level.addWall(new Wall(start, pos));
	}

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
			Iterator<Wall> it = level.getWalls().iterator();
			while (it.hasNext()) {
				if (Physics.distToLine(pos, it.next()) < 0.5f) {
					it.remove();
				}
			}
		}
	}
}
