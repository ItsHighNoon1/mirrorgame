package us.itshighnoon.mirror.editor.state;

import java.util.Iterator;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.enemy.Enemy;
import us.itshighnoon.mirror.world.enemy.Hexagon;
import us.itshighnoon.mirror.world.enemy.Octagon;
import us.itshighnoon.mirror.world.enemy.Pentagon;
import us.itshighnoon.mirror.world.enemy.Square;
import us.itshighnoon.mirror.world.enemy.Triangle;

public class EnemyState implements EditorState {
	private TexturedModel currentTexture;
	private String currentType;
	private boolean deleting;
	
	public void setEnemyType(TexturedModel texture, String type) {
		currentTexture = texture;
		currentType = type;
	}
	
	@Override
	public void pressLeft(Level level, Vector2f pos) {
		pos.x = pos.x - pos.x % 0.5f;
		pos.y = pos.y - pos.y % 0.5f;
		
		switch (currentType) {
		case "triangle":
			level.addEnemy(new Triangle(currentTexture, pos, 0.0f, 0.5f));
			break;
		case "square":
			level.addEnemy(new Square(currentTexture, pos, 0.0f, 0.5f));
			break;
		case "pentagon":
			level.addEnemy(new Pentagon(currentTexture, pos, 0.0f, 0.5f));
			break;
		case "hexagon":
			level.addEnemy(new Hexagon(currentTexture, pos, 0.0f, 0.5f));
			break;
		case "octagon":
			level.addEnemy(new Octagon(currentTexture, pos, 0.0f, 0.5f));
			break;
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
			Iterator<Enemy> it = level.getEnemies().iterator();
			while (it.hasNext()) {
				Enemy enemy = it.next();
				float dx = enemy.getPosition().x - pos.x;
				float dy = enemy.getPosition().y - pos.y;
				if (dx * dx + dy * dy < 0.25f) {
					it.remove();
				}
			}
		}
	}
}
