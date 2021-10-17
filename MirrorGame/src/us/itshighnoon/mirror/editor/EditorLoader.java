package us.itshighnoon.mirror.editor;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.object.Texture;
import us.itshighnoon.mirror.lwjgl.object.VAO;

public class EditorLoader extends Loader {
	private Map<String, Texture> alreadyLoaded;
	private Map<Integer, Image> imageData;
	private int newTexPointer = 0;
	
	public EditorLoader() {
		alreadyLoaded = new HashMap<String, Texture>();
		imageData = new HashMap<Integer, Image>();
	}
	
	@Override
	public VAO loadQuad() {
		return null;
	}
	
	@Override
	public Texture loadTexture(String path) {
		if (alreadyLoaded.containsKey(path)) {
			return alreadyLoaded.get(path);
		}
		try {
			Image image = ImageIO.read(new File(path));
			imageData.put(newTexPointer, image);
			Texture tex = new Texture(newTexPointer);
			alreadyLoaded.put(path, tex);
			newTexPointer++;
			return tex;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	public Image getImage(int id) {
		return imageData.get(id);
	}
}
