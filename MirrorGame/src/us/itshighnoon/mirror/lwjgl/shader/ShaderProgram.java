package us.itshighnoon.mirror.lwjgl.shader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;

public class ShaderProgram {
	private int programId;
	
	public ShaderProgram(String vertexFile, String fragmentFile) {
		int vShader = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		int fShader = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vShader);
		GL20.glAttachShader(programId, fShader);
		GL20.glLinkProgram(programId);
		GL20.glDetachShader(programId, vShader);
		GL20.glDetachShader(programId, fShader);
		GL20.glDeleteShader(vShader);
		GL20.glDeleteShader(fShader);
	}
	
	public ShaderProgram(String computeFile) {
		int cShader = loadShader(computeFile, GL43.GL_COMPUTE_SHADER);
		programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, cShader);
		GL20.glLinkProgram(programId);
		GL20.glDetachShader(programId, cShader);
		GL20.glDeleteShader(cShader);
	}
	
	public void storeAllUniformLocations(Uniform... uniforms) {
		for (Uniform uniform : uniforms) {
			uniform.storeUniformLocation(programId);
		}
		GL20.glValidateProgram(programId);
	}
	
	public void start() {
		GL20.glUseProgram(programId);
	}
	
	public void stop() {
		GL20.glUseProgram(0);
	}
	
	public void cleanUp() {
		stop();
		GL20.glDeleteProgram(programId);
	}
	
	private int loadShader(String path, int type) {
		StringBuilder source = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line;
			while ((line = reader.readLine()) != null) {
				source.append(line).append("//\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		int shader = GL20.glCreateShader(type);
		GL20.glShaderSource(shader, source);
		GL20.glCompileShader(shader);
		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shader, 500));
			System.err.println("Could not compile shader " + path);
			System.exit(-1);
		}
		return shader;
	}
}
