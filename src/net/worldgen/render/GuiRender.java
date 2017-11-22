package net.worldgen.render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.worldgen.object.Entity;
import net.worldgen.object.raw.Model;
import net.worldgen.object.raw.RawModel;
import net.worldgen.render.shader.GuiShader;
import net.worldgen.util.Maths;
import net.worldgen.util.vector.Matrix4f;

public class GuiRender {
	
	private GuiShader shader;
	
	public GuiRender(GuiShader shader) {
		this.shader = shader;
	}
	
	public void render(Map<Model, List<Entity>> entities, Matrix4f projectionMatrix) {
		shader.loadProjectionMatrix(projectionMatrix);
		for(Model model: entities.keySet()) {
			prepareModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity: batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRaw().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindModel();
		}
	}
	
	private void prepareModel(Model model) {
		RawModel raw = model.getRaw();
		GL30.glBindVertexArray(raw.getVao());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getMaterial().getID());
	}
	
	private void unbindModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
}
