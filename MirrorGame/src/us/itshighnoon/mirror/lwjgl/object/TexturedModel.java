package us.itshighnoon.mirror.lwjgl.object;

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
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TexturedModel)) return false;
		TexturedModel model = (TexturedModel)o;
		if (!texture.equals(model.getTexture())) return false;
		return vao.equals(model.getVao());
	}
}
