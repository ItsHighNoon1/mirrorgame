package us.itshighnoon.mirror;

import org.joml.Vector2f;

public class Wall {
	private Vector2f p1;
	private Vector2f p2;
	
	public Wall(Vector2f p1, Vector2f p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Vector2f getP1() {
		return p1;
	}
	
	public Vector2f getP2() {
		return p2;
	}
}
