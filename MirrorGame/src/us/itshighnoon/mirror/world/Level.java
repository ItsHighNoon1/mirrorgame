package us.itshighnoon.mirror.world;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2f;

import us.itshighnoon.mirror.Main;
import us.itshighnoon.mirror.lwjgl.Loader;
import us.itshighnoon.mirror.lwjgl.object.Texture;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.enemy.Enemy;
import us.itshighnoon.mirror.world.enemy.Hexagon;
import us.itshighnoon.mirror.world.enemy.Octagon;
import us.itshighnoon.mirror.world.enemy.Pentagon;
import us.itshighnoon.mirror.world.enemy.Square;
import us.itshighnoon.mirror.world.enemy.Triangle;

public class Level {
	private List<Wall> walls;
	private List<Wall> mirrors;
	private List<Entity> floors;
	private List<Enemy> enemies;
	private List<Particle> particles;
	private Vector2f playerSpawn;
	private Vector2f exit;
	private String nextLevel;
	
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
					Enemy enemy = null;
					switch (lineData[1]) {
					case "triangle":
						enemy = new Triangle(Main.triangle, enemyPos, 0.0f, Float.parseFloat(lineData[4]));
						break;
					case "square":
						enemy = new Square(Main.square, enemyPos, 0.0f, Float.parseFloat(lineData[4]));
						break;
					case "pentagon":
						enemy = new Pentagon(Main.pentagon, enemyPos, 0.0f, Float.parseFloat(lineData[4]));
						break;
					case "hexagon":
						enemy = new Hexagon(Main.hexagon, enemyPos, 0.0f, Float.parseFloat(lineData[4]));
						break;
					case "octagon":
						enemy = new Octagon(Main.octagon, enemyPos, 0.0f, Float.parseFloat(lineData[4]));
						break;
					}
					if (enemy != null) {
						enemies.add(enemy);
					}
					break;
				case "ex":
					// Exit
					exit.x = Float.parseFloat(lineData[1]);
					exit.y = Float.parseFloat(lineData[2]);
					if (lineData.length > 3) {
						nextLevel = lineData[3];
					} else {
						nextLevel = null;
					}
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
		exit = new Vector2f(0.0f, 0.0f);
	}
	
	public void save(String file, Map<Texture, String> textureFiles) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			PrintWriter out = new PrintWriter(fos);
			
			out.println("s " + playerSpawn.x + " " + playerSpawn.y);
			
			List<Texture> textures = new ArrayList<Texture>();
			for (int i = 0; i < floors.size(); i++) {
				if (!textures.contains(floors.get(i).getModel().getTexture())) {
					textures.add(floors.get(i).getModel().getTexture());
				}
			}
			for (int i = 0; i < textures.size(); i++) {
				out.println(String.format("ft %d %s", i, textureFiles.get(textures.get(i))));
			}
			for (int i = 0; i < floors.size(); i++) {
				out.println(String.format("f %d %f %f", textures.indexOf(floors.get(i).getModel().getTexture()), floors.get(i).getPosition().x, floors.get(i).getPosition().y));
			}
			for (int i = 0; i < walls.size(); i++) {
				out.println(String.format("w %f %f %f %f", walls.get(i).getA().x, walls.get(i).getA().y, walls.get(i).getB().x, walls.get(i).getB().y));
			}
			for (int i = 0; i < mirrors.size(); i++) {
				out.println(String.format("m %f %f %f %f", mirrors.get(i).getA().x, mirrors.get(i).getA().y, mirrors.get(i).getB().x, mirrors.get(i).getB().y));
			}
			for (int i = 0; i < enemies.size(); i++) {
				out.println(String.format("e %s %f %f %f", enemies.get(i).getName(), enemies.get(i).getPosition().x, enemies.get(i).getPosition().y, enemies.get(i).getScale()));
			}
			
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not write file " + file);
			e.printStackTrace();
		}
	}
	
	public List<Wall> getWalls() {
		return walls;
	}
	
	public void addWall(Wall w) {
		walls.add(w);
	}
	
	public void removeWall(Wall w) {
		walls.remove(w);
	}
	
	public List<Wall> getMirrors() {
		return mirrors;
	}
	
	public void addMirror(Wall m) {
		mirrors.add(m);
	}
	
	public void removeMirror(Wall m) {
		mirrors.remove(m);
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
	
	public void removeFloor(Entity f) {
		floors.remove(f);
	}
	
	public List<Enemy> getEnemies() {
		return enemies;
	}
	
	public void addEnemy(Enemy e) {
		enemies.add(e);
	}
	
	public void removeEnemy(Enemy e) {
		enemies.remove(e);
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
	
	public void setSpawn(Vector2f spawn) {
		playerSpawn = spawn;
	}
	
	public Vector2f getExit() {
		return exit;
	}
	
	public void setExit(Vector2f exit) {
		this.exit = exit;
	}
	
	public String getNextLevel() {
		return nextLevel;
	}
	
	public void setNextLevel(String next) {
		nextLevel = next;
	}
}
