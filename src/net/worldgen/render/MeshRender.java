package net.worldgen.render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.worldgen.object.Entity;
import net.worldgen.object.planet.Chunk;
import net.worldgen.object.planet.Planet;
import net.worldgen.object.raw.Model;
import net.worldgen.object.raw.RawModel;
import net.worldgen.render.shader.MeshShader;
import net.worldgen.util.Maths;
import net.worldgen.util.vector.Matrix4f;

public class MeshRender {

	private MeshShader shader;

	public MeshRender(MeshShader shader) {
		this.shader = shader;

	}

	public void render(Map<Model, List<Entity>> entities, List<Planet> planets, Matrix4f projectionMatrix) {
		shader.loadProjectionMatrix(projectionMatrix);
		// entity
		for (Model model : entities.keySet()) {
			prepareModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				if (!entity.shouldRender()) // TODO
					continue;
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRaw().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindModel();
		}
		// planet
		for (Planet planet : planets) {
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(planet.getPos(), planet.getRot(), 1);
			shader.loadTransformationMatrix(transformationMatrix);
			for (Chunk chunk : planet.getFaces())
				renderChunk(chunk);
		}
	}

	private void renderChunk(Chunk chunk) {
		// System.out.println(chunk.getLod());
		if (chunk.shouldRender()) {
			if (!chunk.isProcessed())
				return;
			renderChunkModel(chunk.getRawSurface());
			renderChunkModel(chunk.getRawWater());
			return;
		}
		if (chunk.hasChildren())
			for (Chunk child : chunk.getChildren())
				renderChunk(child);
	}

	private void renderChunkModel(RawModel raw) {
		GL30.glBindVertexArray(raw.getVao());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawElements(GL11.GL_TRIANGLES,raw.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareModel(Model model) {
		RawModel raw = model.getRaw();
		GL30.glBindVertexArray(raw.getVao());
		GL20.glEnableVertexAttribArray(0);
	}

	private void unbindModel() {
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(),
				entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
}
