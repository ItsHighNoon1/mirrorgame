package us.itshighnoon.mirror.lwjgl.object;

public class Framebuffer extends Texture {
	private int fboId;
	
	public Framebuffer(int fboId, int textureId) {
		super(textureId);
		this.fboId = fboId;
	}
	
	public int getFramebufferId() {
		return fboId;
	}
}
