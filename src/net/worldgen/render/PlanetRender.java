package net.worldgen.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.worldgen.Handler;
import net.worldgen.object.Entity;
import net.worldgen.object.planet.Planet;
import net.worldgen.object.raw.RawModel;
import net.worldgen.render.shader.PlanetShader;
import net.worldgen.util.Maths;
import net.worldgen.util.vector.Matrix4f;

public class PlanetRender {

	private PlanetShader shader;

	public PlanetRender(PlanetShader shader) {
		this.shader = shader;

	}

	public void render(Handler handler, List<Planet> planets, Matrix4f projectionMatrix) {
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadDirLight(handler.getSun());
		for (Planet planet : planets) {
			Entity entity = planet.getSurface();
			if (!entity.shouldRender())
				continue;
			prepareModel(entity);
			GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getRaw().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindModel();
		}
	}

	private void prepareModel(Entity entity) {
		RawModel raw = entity.getModel().getRaw();
		GL30.glBindVertexArray(raw.getVao());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getMaterial().getID());

		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(),
				entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

	private void unbindModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
}