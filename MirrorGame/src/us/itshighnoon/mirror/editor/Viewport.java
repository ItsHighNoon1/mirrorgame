package us.itshighnoon.mirror.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import org.joml.Vector2f;

import us.itshighnoon.mirror.world.Entity;
import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Wall;

public class Viewport extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final float CAM_WIDTH = 16.0f;
	
	private Level level;
	private Vector2f camera;
	private EditorLoader loader;
	
	public Viewport(EditorLoader loader) {
		this.loader = loader;
		this.camera = new Vector2f(0.0f, 0.0f);
		addKeyListener(new KeyHandler());
	}
	
	public void setLevel(Level level) {
		this.level = level;
		setCamera(level.getSpawn().x, level.getSpawn().y);
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.MAGENTA);
		g.fillRect(0, 0, 480, 360);
		if (level != null) {
			for (Entity e : level.getFloors()) {
				int ex = (int)((e.getPosition().x - camera.x) * CAM_WIDTH - 8 * e.getScale());
				int ey = (int)((e.getPosition().y - camera.y) * CAM_WIDTH + 8 * e.getScale());
				int scale = (int)(16 * e.getScale());
				g.drawImage(loader.getImage(e.getModel().getTexture().getTextureId()), ex, -ey, scale, scale, null);
			}
			g.setColor(Color.BLACK);
			for (Wall w : level.getWalls()) {
				int wx1 = (int)((w.getA().x - camera.x) * CAM_WIDTH);
				int wy1 = (int)((w.getA().y - camera.y) * CAM_WIDTH);
				int wx2 = (int)((w.getB().x - camera.x) * CAM_WIDTH);
				int wy2 = (int)((w.getB().y - camera.y) * CAM_WIDTH);
				g.drawLine(wx1, -wy1, wx2, -wy2);
			}
			g.setColor(Color.CYAN);
			for (Wall w : level.getMirrors()) {
				int wx1 = (int)((w.getA().x - camera.x) * CAM_WIDTH);
				int wy1 = (int)((w.getA().y - camera.y) * CAM_WIDTH);
				int wx2 = (int)((w.getB().x - camera.x) * CAM_WIDTH);
				int wy2 = (int)((w.getB().y - camera.y) * CAM_WIDTH);
				g.drawLine(wx1, -wy1, wx2, -wy2);
			}
		}
	}
	
	public void setCamera(float x, float y) {
		camera.x = x;
		camera.y = y;
		repaint();
	}
	
	public void moveCamera(float dx, float dy) {
		camera.x += dx;
		camera.y += dy;
		repaint();
	}
	
	public Vector2f getLocation(int mouseX, int mouseY) {
		float x = mouseX / CAM_WIDTH + camera.x;
		float y = -mouseY / CAM_WIDTH + camera.y;
		return new Vector2f(x, y);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(480, 360);
	}
	
	private class KeyHandler implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case 'W':
				moveCamera(0.0f, 10.0f);
				break;
			case 'S':
				moveCamera(0.0f, -10.0f);
				break;
			case 'A':
				moveCamera(-10.0f, 0.0f);
				break;
			case 'D':
				moveCamera(10.0f, 0.0f);
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}
	}
}
