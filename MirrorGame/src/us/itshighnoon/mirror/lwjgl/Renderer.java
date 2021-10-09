package us.itshighnoon.mirror.lwjgl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import us.itshighnoon.mirror.Camera;
import us.itshighnoon.mirror.Entity;
import us.itshighnoon.mirror.lwjgl.shader.Shader;
import us.itshighnoon.mirror.lwjgl.shader.UniformMat4;

public class Renderer {
	private Map<TexturedModel, List<Entity>> entities;
	
	public Renderer() {
		entities = new LinkedHashMap<TexturedModel, List<Entity>>(); // dont need to use a sorted map because models are unsorted anyways
		GL11.glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
	}
	
	public void prepare() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public void submit(Entity e) {
		TexturedModel model = e.getModel();
		if (entities.get(model) == null) {
			entities.put(model, new LinkedList<Entity>()); // we will be iterating
		}
		entities.get(model).add(0, e); // i love dsa !
	}
	
	public void drawBase(Shader shader, UniformMat4 matrixUniform, Camera camera, Vector2i screenDims) {
		// vp matrix is not going to change so lets premultiply
		Matrix4f vpMatrix = new Matrix4f();
		Matrix4f mvpMatrix = new Matrix4f();
		float aspect = (float)screenDims.x / (float)screenDims.y;
		vpMatrix.setOrtho(-aspect, aspect, -1.0f, 1.0f, -1.0f, 1.0f);
		vpMatrix.translate(-camera.getPosition().x, -camera.getPosition().y, 0.0f);
		
		shader.start();
		for (TexturedModel model : entities.keySet()) {
			GL30.glBindVertexArray(model.getVao().getVaoId());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
			List<Entity> modelEntities = entities.get(model);
			for (Entity e : modelEntities) {
				vpMatrix.translate(e.getPosition().x, e.getPosition().y, 0.0f, mvpMatrix); // without dest, vp gets modified
				mvpMatrix.rotate(e.getRotation(), 0.0f, 0.0f, 1.0f);
				matrixUniform.loadMatrix(mvpMatrix);
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, model.getVao().getVertexCount());
			}
			modelEntities.clear();
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
		}
		shader.stop();
	}
}
