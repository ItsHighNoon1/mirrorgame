package us.itshighnoon.mirror.lwjgl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import us.itshighnoon.mirror.lwjgl.object.Framebuffer;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.lwjgl.shader.Shader;
import us.itshighnoon.mirror.lwjgl.shader.UniformFloat;
import us.itshighnoon.mirror.lwjgl.shader.UniformInt;
import us.itshighnoon.mirror.lwjgl.shader.UniformMat4;
import us.itshighnoon.mirror.lwjgl.shader.UniformVec2;
import us.itshighnoon.mirror.lwjgl.shader.UniformVec4Arr;
import us.itshighnoon.mirror.world.Entity;
import us.itshighnoon.mirror.world.Wall;

public class Renderer {
	private Shader base;
	private UniformMat4 base_mvpMatrix = new UniformMat4("u_mvpMatrix");
	
	private Shader reflection;
	private UniformVec4Arr reflection_mirrors = new UniformVec4Arr("u_mirrors");
	private UniformVec4Arr reflection_walls = new UniformVec4Arr("u_walls");
	private UniformVec2 reflection_absPosition = new UniformVec2("u_absPosition");
	private UniformFloat reflection_aspect = new UniformFloat("u_aspect");
	private UniformFloat reflection_camSize = new UniformFloat("u_camSize");
	private UniformInt reflection_nMirrors = new UniformInt("u_nMirrors");
	private UniformInt reflection_nWalls = new UniformInt("u_nWalls");
	
	private Map<TexturedModel, List<Entity>> entities;
	
	public Renderer() {
		base = new Shader("res/shader/v_base.glsl", "res/shader/f_base.glsl");
		base.storeAllUniformLocations(base_mvpMatrix);
		reflection = new Shader("res/shader/v_reflect.glsl", "res/shader/f_reflect.glsl");
		reflection.storeAllUniformLocations(reflection_mirrors, reflection_walls, reflection_absPosition, reflection_aspect, reflection_camSize, reflection_nMirrors, reflection_nWalls);
		entities = new LinkedHashMap<TexturedModel, List<Entity>>();
		GL11.glClearColor(0.0f, 0.05f, 0.05f, 1.0f);
	}
	
	public void submitBase(Entity e) {
		TexturedModel model = e.getModel();
		if (entities.get(model) == null) {
			entities.put(model, new LinkedList<Entity>()); // we will be iterating
		}
		entities.get(model).add(0, e); // i love dsa !
	}
	
	public void submitWalls(Wall... walls) {
		Vector4f[] packedWalls = new Vector4f[walls.length];
		for (int i = 0; i < walls.length; i++) {
			packedWalls[i] = walls[i].getPacked();
		}
		reflection.start();
		reflection_nWalls.loadInt(walls.length);
		reflection_walls.loadVec4s(packedWalls);
		reflection.stop();
	}
	
	public void submitReflectors(Wall... mirrors) {
		Vector4f[] packedMirrors = new Vector4f[mirrors.length];
		for (int i = 0; i < mirrors.length; i++) {
			packedMirrors[i] = mirrors[i].getPacked();
		}
		reflection.start();
		reflection_nMirrors.loadInt(mirrors.length);
		reflection_mirrors.loadVec4s(packedMirrors);
		reflection.stop();
	}
	
	public void drawBase(Entity camera, Framebuffer target) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, target.getFramebufferId());
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glViewport(0, 0, target.getSize().x, target.getSize().y);
		
		// vp matrix is not going to change so lets premultiply
		Matrix4f vpMatrix = new Matrix4f();
		Matrix4f mvpMatrix = new Matrix4f();
		float aspect = (float)target.getSize().x / (float)target.getSize().y;
		vpMatrix.setOrtho(-aspect, aspect, -1.0f, 1.0f, -1.0f, 1.0f);
		vpMatrix.scale(1.0f / camera.getScale());
		vpMatrix.rotate(-camera.getRotation(), 0.0f, 0.0f, 1.0f);
		vpMatrix.translate(-camera.getPosition().x, -camera.getPosition().y, 0.0f);
		
		base.start();
		for (TexturedModel model : entities.keySet()) {
			GL30.glBindVertexArray(model.getVao().getVaoId());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
			List<Entity> modelEntities = entities.get(model);
			for (Entity e : modelEntities) {
				vpMatrix.translate(e.getPosition().x, e.getPosition().y, 0.0f, mvpMatrix); // without dest, vp gets modified
				mvpMatrix.rotate(e.getRotation(), 0.0f, 0.0f, 1.0f);
				mvpMatrix.scale(e.getScale());
				base_mvpMatrix.loadMatrix(mvpMatrix);
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, model.getVao().getVertexCount());
			}
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
		}
		entities.clear();
		base.stop();
	}
	
	public void drawReflected(Entity view, Entity camera, Framebuffer target) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, target.getFramebufferId());
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glViewport(0, 0, target.getSize().x, target.getSize().y);
		
		float aspect = (float)target.getSize().x / (float)target.getSize().y;
		
		reflection.start();
		GL30.glBindVertexArray(view.getModel().getVao().getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, view.getModel().getTexture().getTextureId());
		reflection_absPosition.loadVec2(camera.getPosition().x, camera.getPosition().y);
		reflection_aspect.loadFloat(aspect);
		reflection_camSize.loadFloat(camera.getScale());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, view.getModel().getVao().getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		reflection.stop();
	}
	
	public void cleanUp() {
		base.cleanUp();
		reflection.cleanUp();
	}
}
