package us.itshighnoon.mirror;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Mirror {
	private Vector2f a;
	private Vector2f b;
	
	public Mirror(Vector2f a, Vector2f b) {
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
		return new Vector4f(a.x, a.y, vec.x, vec.y);
	}
}
