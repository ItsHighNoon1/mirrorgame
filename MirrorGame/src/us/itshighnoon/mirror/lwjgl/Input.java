package us.itshighnoon.mirror.lwjgl;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class Input {
	public int keyForward;
	public int keyBackward;
	public int keyLeft;
	public int keyRight;
	public int buttonShoot;
	
	public float forward;
	public float backward;
	public float left;
	public float right;
	
	public Vector2f aimLocation;
	public boolean holdShoot;
	public boolean shoot;
	
	public Input() {
		keyForward = GLFW.GLFW_KEY_W;
		keyBackward = GLFW.GLFW_KEY_S;
		keyLeft = GLFW.GLFW_KEY_A;
		keyRight = GLFW.GLFW_KEY_D;
		
		aimLocation = new Vector2f();
	}
	
	public Input(String file) {
		// TODO read controls file
	}
	
	public void save(String file) {
		// TODO save controls file
	}
}
