package us.itshighnoon.mirror.lwjgl.shader;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class UniformMat4 extends Uniform {
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public UniformMat4(String name) {
		super(name);
	}
	
	public void loadMatrix(Matrix4f matrix) {
		matrix.get(matrixBuffer);
		//matrixBuffer.flip();
		GL20.glUniformMatrix4fv(super.getLocation(), false, matrixBuffer);
	}
}
