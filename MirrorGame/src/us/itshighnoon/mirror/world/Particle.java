package us.itshighnoon.mirror.world;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.object.TexturedModel;

public class Particle extends Entity {
	private Vector2f velocity;
	private float angVelocity;
	private float dampening;
	private float angDampening;
	private float timeLeft;
	private boolean immortal;
	
	public Particle(TexturedModel model, Vector2f position, float rotation, float scale, float lt) {
		super(model, position, rotation, scale);
		immortal = lt < 0.0f;
		timeLeft = lt;
		velocity = new Vector2f(0.0f, 0.0f);
	}
	
	public Vector2f getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Vector2f velocity, float dampening) {
		this.velocity = velocity;
		this.dampening = dampening;
	}
	
	public void setAngularVelocity(float velocity, float dampening) {
		this.angVelocity = velocity;
		this.angDampening = dampening;
	}

	public boolean tick(float dt) {
		increasePosition(velocity.x * dt, velocity.y * dt);
		increaseRotation(angVelocity * dt);
		velocity.x *= 1.0f - dampening * dt;
		velocity.y *= 1.0f - dampening * dt;
		angVelocity *= 1.0f - angDampening * dt;
		timeLeft -= dt;
		return immortal || timeLeft > 0.0f;
	}
}
