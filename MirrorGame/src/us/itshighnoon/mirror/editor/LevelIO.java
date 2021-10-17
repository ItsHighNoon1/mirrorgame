package us.itshighnoon.mirror.editor;

import java.io.File;

import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.world.Level;

public class LevelIO {
	public static Level loadLevel(File file, Loader loader) {
		return new Level(file.getAbsolutePath(), loader);
	}
	
	public static void saveLevel(File file) {
		
	}
}
