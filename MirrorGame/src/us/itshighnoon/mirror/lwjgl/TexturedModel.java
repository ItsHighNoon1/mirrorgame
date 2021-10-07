package us.itshighnoon.mirror.lwjgl;

public class TexturedModel {
	private VAO vao;
	private Texture texture;
	
	public TexturedModel(VAO vao, Texture texture) {
		this.vao = vao;
		this.texture = texture;
	}
	
	public VAO getVao() {
		return vao;
	}
	
	public Texture getTexture() {
		return texture;
	}
}
