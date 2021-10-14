package us.itshighnoon.mirror.world;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Wall {
	private Vector2f a;
	private Vector2f b;
	
	public Wall(Vector2f a, Vector2f b) {
		this.a = a;
		this.b = b;
	}
	
	public Vector2f getA() {
		return a;
	}
	
	public Vector2f getB() {
		return b;
	}
	
	public Vector2f getVec() {
		return new Vector2f(b.x - a.x, b.y - a.y);
	}
	
	public Vector4f getPacked() {
		Vector2f vec = getVec();
		return new Vector4f(getA().x, getA().y, vec.x, vec.y);
	}
}
