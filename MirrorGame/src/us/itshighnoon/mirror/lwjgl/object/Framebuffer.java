package us.itshighnoon.mirror.lwjgl.object;

import org.joml.Vector2i;

public class Framebuffer extends Texture {
	private Vector2i dims;
	private int fboId;
	
	public Framebuffer(int fboId, int textureId, int w, int h) {
		super(textureId);
		this.fboId = fboId;
		dims = new Vector2i(w, h);
	}
	
	public int getFramebufferId() {
		return fboId;
	}
	
	public Vector2i getSize() {
		return dims;
	}
	
	public void setSize(Vector2i size) {
		if (fboId != 0) throw new IllegalStateException("Cannot resize non-display buffer");
		dims = size;
	}
}
