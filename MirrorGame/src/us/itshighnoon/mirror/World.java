package us.itshighnoon.mirror;

import java.util.ArrayList;
import java.util.List;

public class World {
	private Camera camera;
	private List<Wall> walls;
	private List<Wall> mirrors;
	
	public World() {
		walls = new ArrayList<Wall>();
		mirrors = new ArrayList<Wall>();
	}
	
	public Camera getCamera() {
		return camera;
	}
}
