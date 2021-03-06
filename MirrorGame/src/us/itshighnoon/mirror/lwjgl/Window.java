package us.itshighnoon.mirror.lwjgl;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import us.itshighnoon.mirror.lwjgl.object.Framebuffer;

public class Window {
	private static final int OPENGL_MAJOR = 4;
	private static final int OPENGL_MINOR = 6;
	private static final float MAX_FRAME_TIME = 0.1f;
	
	private long window;
	private double lastTime;
	private float frameTime;
	
	public Window(String name, int width, int height) {
		// initialize glfw
		GLFW.glfwInit();
		
		// i know that we will be using compute shaders, so we will use opengl 4.6
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, OPENGL_MAJOR);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, OPENGL_MINOR);
		
		// other window stuff
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		window = GLFW.glfwCreateWindow(width, height, name, 0, 0);
		
		// dont miss a key/click event
		GLFW.glfwSetInputMode(window, GLFW.GLFW_STICKY_KEYS, GLFW.GLFW_TRUE);
		GLFW.glfwSetInputMode(window, GLFW.GLFW_STICKY_MOUSE_BUTTONS, GLFW.GLFW_TRUE);
		
		// set up the context
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		frameTime = 0.0f;
		lastTime = GLFW.glfwGetTime();
	}
	
	public Window() {
		this("Window", 1280, 720);
	}
	
	public void cleanUp() {
		GLFW.glfwTerminate();
	}
	
	public void poll(Input i) {
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
		i.forward = GLFW.glfwGetKey(window, i.keyForward) == GLFW.GLFW_PRESS ? 1.0f : 0.0f;
		i.backward = GLFW.glfwGetKey(window, i.keyBackward) == GLFW.GLFW_PRESS ? 1.0f : 0.0f;
		i.left = GLFW.glfwGetKey(window, i.keyLeft) == GLFW.GLFW_PRESS ? 1.0f : 0.0f;
		i.right = GLFW.glfwGetKey(window, i.keyRight) == GLFW.GLFW_PRESS ? 1.0f : 0.0f;
		double[] mouseX = new double[1];
		double[] mouseY = new double[1];
		GLFW.glfwGetCursorPos(window, mouseX, mouseY);
		Vector2i dims = getWindowDims();
		i.aimLocation.x = ((float)mouseX[0] / dims.x - 0.5f) * 2.0f * dims.x / dims.y;
		i.aimLocation.y = (0.5f - (float)mouseY[0] / dims.y) * 2.0f;
		i.shoot = GLFW.glfwGetMouseButton(window, i.buttonShoot) == GLFW.GLFW_PRESS && !i.holdShoot;
		i.holdShoot = GLFW.glfwGetMouseButton(window, i.buttonShoot) == GLFW.GLFW_PRESS;
		
		double currentTime = GLFW.glfwGetTime();
		frameTime = (float)(currentTime - lastTime);
		lastTime = currentTime;
	}
	
	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(window);
	}
	
	public float getFrameTime() {
		return frameTime > MAX_FRAME_TIME ? MAX_FRAME_TIME : frameTime;
	}
	
	public Vector2i getWindowDims() {
		Vector2i dims = new Vector2i(-1, -1);
		int[] w = new int[1];
		int[] h = new int[1];
		GLFW.glfwGetWindowSize(window, w, h);
		dims.x = w[0];
		dims.y = h[0];
		return dims;
	}

	public Framebuffer getFramebuffer() {
		return new Framebuffer(0, 0, getWindowDims().x, getWindowDims().y);
	}
}
