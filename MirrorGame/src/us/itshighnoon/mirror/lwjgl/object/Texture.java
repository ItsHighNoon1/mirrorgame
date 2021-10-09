package us.itshighnoon.mirror.lwjgl.object;

public class Texture {
	private int textureId;
	
	public Texture(int textureId) {
		this.textureId = textureId;
	}
	
	public int getTextureId() {
		return textureId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Texture)) return false;
		Texture tex = (Texture)o;
		return textureId == tex.getTextureId();
	}
}
