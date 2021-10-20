package us.itshighnoon.mirror.editor.state;

import org.joml.Vector2f;

import us.itshighnoon.mirror.world.Level;

public class SpawnState implements EditorState {
	@Override
	public void pressLeft(Level level, Vector2f pos) {
		pos.x = pos.x - pos.x % 0.5f;
		pos.y = pos.y - pos.y % 0.5f;
		level.setSpawn(pos);
	}

	@Override
	public void releaseLeft(Level level, Vector2f pos) {}

	@Override
	public void pressRight(Level level, Vector2f pos) {}

	@Override
	public void releaseRight(Level level, Vector2f pos) {}

	@Override
	public void drag(Level level, Vector2f pos) {}
}
