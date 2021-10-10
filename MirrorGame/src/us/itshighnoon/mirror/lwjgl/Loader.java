package us.itshighnoon.mirror.lwjgl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import us.itshighnoon.mirror.lwjgl.object.Framebuffer;
import us.itshighnoon.mirror.lwjgl.object.Texture;
import us.itshighnoon.mirror.lwjgl.object.VAO;

public class Loader {
	private static final float[] positions = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };
	private static final float[] texCoords = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
	
	private List<Integer> vaos;
	private List<Integer> vbos;
	private List<Integer> textures;
	private List<Integer> fbos;
	
	public Loader() {
		vaos = new ArrayList<Integer>();
		vbos = new ArrayList<Integer>();
		textures = new ArrayList<Integer>();
		fbos = new ArrayList<Integer>();
	}
	
	public VAO loadQuad() {
		int vao = createVao();
		createVbo(0, 2, positions);
		createVbo(1, 2, texCoords);
		GL30.glBindVertexArray(0); // prevent someone else from modifying our vao
		return new VAO(vao, positions.length / 2);
	}
	
	public Texture loadTexture(String path) {
		STBImage.stbi_set_flip_vertically_on_load(true);
		
		// i love pointers!
		int[] w = new int[1];
		int[] h = new int[1];
		int[] comp = new int[1];
		
		// load using stb, glTexImage2D, free using stb
		ByteBuffer imageData = STBImage.stbi_load(path, w, h, comp, STBImage.STBI_rgb_alpha);
		int texture = createTexture(w[0], h[0], imageData);
		STBImage.stbi_image_free(imageData);
		
		return new Texture(texture);
	}
	
	public Framebuffer createFbo(int width, int height) {
		int fbo = GL30.glGenFramebuffers();
		fbos.add(fbo);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		int colorBuffer = createTexture(width, height, null);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorBuffer, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		return new Framebuffer(fbo, colorBuffer, width, height);
	}
	
	public void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
		for (int fbo : fbos) {
			GL30.glDeleteFramebuffers(fbo);
		}
	}
	
	private int createVao() {
		int vao = GL30.glGenVertexArrays();
		vaos.add(vao);
		GL30.glBindVertexArray(vao);
		return vao;
	}
	
	private void createVbo(int attribute, int vecSize, float[] data) {
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attribute, vecSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // prevent someone else from modifying our buffer
	}
	
	private int createTexture(int width, int height, ByteBuffer data) {
		int texture = GL11.glGenTextures();
		textures.add(texture);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		
		// i like these params because 99% of the time they produce the most expected results
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		
		return texture;
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		// lwjgl hates arrays so we have to do this
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
