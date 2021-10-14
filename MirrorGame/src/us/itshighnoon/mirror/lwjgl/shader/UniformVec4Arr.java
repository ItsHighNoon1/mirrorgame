package us.itshighnoon.mirror.lwjgl.shader;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

public class UniformVec4Arr extends Uniform {
	public UniformVec4Arr(String name) {
		super(name);
	}
	
	public void loadVec4s(Vector4f[] vectors) {
		if (vectors.length > 64) System.out.println("Warning: current max is 64 mirrors, using " + vectors.length);
		float[] data = new float[vectors.length * 4];
		for (int i = 0; i < vectors.length; i++) {
			data[4 * i] = vectors[i].x;
			data[4 * i + 1] = vectors[i].y;
			data[4 * i + 2] = vectors[i].z;
			data[4 * i + 3] = vectors[i].w;
		}
		GL20.glUniform4fv(getLocation(), data);
	}
}
