package us.itshighnoon.mirror.world;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.enemy.Enemy;
import us.itshighnoon.mirror.world.enemy.Triangle;

public class Level {
	private List<Wall> walls;
	private List<Wall> mirrors;
	private List<Entity> floors;
	private List<Enemy> enemies;
	private List<Particle> particles;
	private Vector2f playerSpawn;
	
	public Level(String levelFile, Loader loader) {
		this();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(levelFile)));
			Map<String, TexturedModel> textures = new HashMap<String, TexturedModel>();
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] lineData = line.split(" ");
				switch (lineData[0]) {
				case "s":
					// Spawn point
					playerSpawn.x = Float.parseFloat(lineData[1]);
					playerSpawn.y = Float.parseFloat(lineData[2]);
					break;
				case "w":
					// Wall (blocks visibility)
					Vector2f wa = new Vector2f(Float.parseFloat(lineData[1]), Float.parseFloat(lineData[2]));
					Vector2f wb = new Vector2f(Float.parseFloat(lineData[3]), Float.parseFloat(lineData[4]));
					Wall w = new Wall(wa, wb);
					walls.add(w);
					break;
				case "m":
					// Mirror (reflects visibility)
					Vector2f ma = new Vector2f(Float.parseFloat(lineData[1]), Float.parseFloat(lineData[2]));
					Vector2f mb = new Vector2f(Float.parseFloat(lineData[3]), Float.parseFloat(lineData[4]));
					Wall m = new Wall(ma, mb);
					mirrors.add(m);
					break;
				case "ft":
					// Floor type
					TexturedModel model = new TexturedModel(loader.loadQuad(), loader.loadTexture(lineData[2]));
					textures.put(lineData[1], model);
					break;
				case "f":
					// Floor
					Vector2f floorPos = new Vector2f(Float.parseFloat(lineData[2]), Float.parseFloat(lineData[3]));
					float rot = 0.0f;
					float scale = 1.0f;
					if (lineData.length > 4) {
						rot = Float.parseFloat(lineData[4]);
						scale = Float.parseFloat(lineData[5]);
					}
					Entity floorTile = new Entity(textures.get(lineData[1]), floorPos, rot, scale);
					floors.add(floorTile);
					break;
				case "e":
					// Enemy
					Vector2f enemyPos = new Vector2f(Float.parseFloat(lineData[2]), Float.parseFloat(lineData[3]));
					Enemy enemy;
					switch (lineData[1]) {
					default:
						enemy = new Triangle(new TexturedModel(loader.loadQuad(), loader.loadTexture("res/texture/triangle.png")), enemyPos, 0.0f, Float.parseFloat(lineData[4]));
					}
					enemies.add(enemy);
					break;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not read file: " + levelFile);
			System.exit(1);
		}
	}
	
	public Level() {
		walls = new ArrayList<Wall>();
		mirrors = new ArrayList<Wall>();
		floors = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		particles = new ArrayList<Particle>();
		playerSpawn = new Vector2f(0.0f, 0.0f);
	}
	
	public Wall[] getWalls() {
		Wall[] wallsArr = new Wall[walls.size()];
		int i = 0;
		for (Wall w : walls) {
			wallsArr[i++] = w;
		}
		return wallsArr;
	}
	
	public void addWall(Wall w) {
		walls.add(w);
	}
	
	public Wall[] getMirrors() {
		Wall[] mirrorsArr = new Wall[mirrors.size()];
		int i = 0;
		for (Wall w : mirrors) {
			mirrorsArr[i++] = w;
		}
		return mirrorsArr;
	}
	
	public void addMirror(Wall m) {
		mirrors.add(m);
	}
	
	public Wall[] getColliders() {
		Wall[] colliders = new Wall[walls.size() + mirrors.size()];
		int i = 0;
		for (Wall w : walls) {
			colliders[i++] = w;
		}
		for (Wall w : mirrors) {
			colliders[i++] = w;
		}
		return colliders;
	}
	
	public List<Entity> getFloors() {
		return floors;
	}
	
	public void addFloor(Entity f) {
		floors.add(f);
	}
	
	public List<Enemy> getEnemies() {
		return enemies;
	}
	
	public void addEnemy(Enemy e) {
		enemies.add(e);
	}
	
	public List<Particle> getParticles() {
		return particles;
	}
	
	public void addParticle(Particle p) {
		particles.add(p);
	}
	
	public Vector2f getSpawn() {
		return playerSpawn;
	}
}
