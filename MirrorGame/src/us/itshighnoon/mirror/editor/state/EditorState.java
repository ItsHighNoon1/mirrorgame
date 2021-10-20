package us.itshighnoon.mirror.editor.state;

import org.joml.Vector2f;

import us.itshighnoon.mirror.world.Level;

public interface EditorState {
	void pressLeft(Level level, Vector2f pos);
	
	void releaseLeft(Level level, Vector2f pos);
	
	void pressRight(Level level, Vector2f pos);
	
	void releaseRight(Level level, Vector2f pos);
	
	void drag(Level level, Vector2f pos);
}
