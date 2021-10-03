package us.itshighnoon.mirror.lwjgl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Window {
	private static final int OPENGL_MAJOR = 4;
	private static final int OPENGL_MINOR = 6;
	
	private long window;
	
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
		
		// set up the context
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		GL11.glViewport(0, 0, width, height); // TODO resize callback
		
		// resize callback
		GLFW.glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallbackI() {
			@Override
			public void invoke(long ptr, int w, int h) {
				GL11.glViewport(0, 0, w, h);
			}
		});
	}
	
	public Window() {
		this("Window", 1280, 720);
	}
	
	public void cleanUp() {
		GLFW.glfwTerminate();
	}
	
	public void poll() {
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
	}
	
	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(window);
	}
}
