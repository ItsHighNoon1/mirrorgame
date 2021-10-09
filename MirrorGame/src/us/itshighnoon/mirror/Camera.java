package us.itshighnoon.mirror;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Camera {
	private Vector2f position;
	
	public Camera(float x, float y) {
		position = new Vector2f(x, y);
	}
	
	public Camera() {
		this(0.0f, 0.0f);
	}
	
	public Vector2f getPosition() {
		return position;
	}
}
