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
	
	public Matrix4f getViewMatrix() {
		Matrix4f viewMat = new Matrix4f();
		viewMat = viewMat.translate(-position.x, -position.y, 0.0f);
		// viewMat = viewMat.rotate(r, 0.0f, 0.0f, 1.0f);
		return viewMat;
	}
}
