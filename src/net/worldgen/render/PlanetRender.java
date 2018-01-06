package net.worldgen.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.worldgen.Handler;
import net.worldgen.object.planet.Chunk;
import net.worldgen.object.planet.Planet;
import net.worldgen.object.raw.RawModel;
import net.worldgen.render.shader.SurfaceShader;
import net.worldgen.render.shader.WaterShader;
import net.worldgen.util.Maths;
import net.worldgen.util.vector.Matrix4f;

public class PlanetRender {

	private SurfaceShader surfaceShader;
	private WaterShader waterShader;

	public PlanetRender(SurfaceShader surfaceShader, WaterShader waterShader) {
		this.surfaceShader = surfaceShader;
		this.waterShader = waterShader;

	}

	public void renderSurface(Handler handler, List<Planet> planets, Matrix4f projectionMatrix) {
		surfaceShader.loadProjectionMatrix(projectionMatrix);
		surfaceShader.loadDirLight(handler.getSun());
		for (Planet planet : planets) {
			surfaceShader.loadRadius(planet.getRadius());
			surfaceShader.loadPlanetData(planet.getData());
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(planet.getPos(), planet.getRot(), 1);
			surfaceShader.loadTransformationMatrix(transformationMatrix);
			for (Chunk face : planet.getFaces()) {
				renderSurfaceChunk(face);
			}
		}
	}

	public void renderWater(Handler handler, List<Planet> planets, Matrix4f projectionMatrix) {
		waterShader.loadProjectionMatrix(projectionMatrix);
		waterShader.loadDirLight(handler.getSun());
		for (Planet planet : planets) {
			if(!planet.getData().hasWater())
				continue;
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(planet.getPos(), planet.getRot(), 1);
			waterShader.loadTransformationMatrix(transformationMatrix);
			waterShader.loadRadius(planet.getRadius());
			waterShader.loadPlanetData(planet.getData());
			for (Chunk face : planet.getFaces()) {
				renderWaterChunk(face);
			}
		}
	}

	public void renderSurfaceChunk(Chunk chunk) {
		if (chunk.shouldRender()) {
			if (!chunk.isProcessed())
				return;
			surfaceShader.loadLod(chunk.getLod());
			bindModel(chunk.getRawSurface());
			GL11.glDrawElements(GL11.GL_TRIANGLES, chunk.getRawSurface().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindModel();
		}
		if (chunk.hasChildren())
			for (Chunk child : chunk.getChildren())
				renderSurfaceChunk(child);
	}

	public void renderWaterChunk(Chunk chunk) {
		if (chunk.shouldRender()) {
			if (!chunk.isProcessed())
				return;
			bindModel(chunk.getRawWater());
			GL11.glDrawElements(GL11.GL_TRIANGLES, chunk.getRawWater().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindModel();
		}
		if (chunk.hasChildren())
			for (Chunk child : chunk.getChildren())
				renderWaterChunk(child);
	}

	public void bindModel(RawModel raw) {
		GL30.glBindVertexArray(raw.getVao());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
	}

	public void unbindModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	// private void prepareModel(Entity entity) {
	// RawModel raw = entity.getModel().getRaw();
	// GL30.glBindVertexArray(raw.getVao());
	// GL20.glEnableVertexAttribArray(0);
	// GL20.glEnableVertexAttribArray(1);
	// GL20.glEnableVertexAttribArray(2);
	//
	// GL13.glActiveTexture(GL13.GL_TEXTURE0);
	// GL11.glBindTexture(GL11.GL_TEXTURE_2D,
	// entity.getModel().getMaterial().getID());
	//
	// Matrix4f transformationMatrix =
	// Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(),
	// entity.getScale());
	// surfaceShader.loadTransformationMatrix(transformationMatrix);
	// }
	//
	// private void unbindModel() {
	// GL20.glDisableVertexAttribArray(0);
	// GL20.glDisableVertexAttribArray(1);
	// GL20.glDisableVertexAttribArray(2);
	// GL30.glBindVertexArray(0);
	// }
}