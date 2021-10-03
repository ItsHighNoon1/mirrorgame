package us.itshighnoon.mirror.lwjgl;

public class VAO {
	private int vaoId;
	private int vertexCount;
	
	public VAO(int vaoId, int vertexCount) {
		this.vaoId = vaoId;
		this.vertexCount = vertexCount;
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}
}
