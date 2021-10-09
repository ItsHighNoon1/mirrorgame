package us.itshighnoon.mirror;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.object.TexturedModel;

public class Entity {
	private TexturedModel model;
	private Vector2f position;
	private float rotation;
	private float scale;
	
	public Entity(TexturedModel model, Vector2f position, float rotation, float scale) {
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public Entity(TexturedModel model) {
		this(model, new Vector2f(0.0f, 0.0f), 0.0f, 1.0f);
	}
	
	public TexturedModel getModel() {
		return model;
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}
	
	public void increasePosition(float dx, float dy) {
		position.x += dx;
		position.y += dy;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	public void setRotation(float r) {
		rotation = r;
	}
	
	public void increaseRotation(float dr) {
		rotation += dr;
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float s) {
		scale = s;
	}
	
	public void increaseScale(float ds) {
		scale += ds;
	}
}
