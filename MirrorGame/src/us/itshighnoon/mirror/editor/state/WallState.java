package us.itshighnoon.mirror.editor.state;

import java.util.Iterator;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Physics;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Wall;

public class WallState implements EditorState {
	private Wall current;
	private boolean deleting;
	
	@Override
	public void pressLeft(Level level, Vector2f pos) {
		pos.x = pos.x - pos.x % 0.5f;
		pos.y = pos.y - pos.y % 0.5f;
		current = new Wall(pos, pos);
		level.addWall(current);
	}

	@Override
	public void releaseLeft(Level level, Vector2f pos) {
		pos.x = pos.x - pos.x % 0.5f;
		pos.y = pos.y - pos.y % 0.5f;
		current.setB(pos);
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
		} else if (current != null) {
			pos.x = pos.x - pos.x % 0.5f;
			pos.y = pos.y - pos.y % 0.5f;
			current.setB(pos);
		}
	}
}
