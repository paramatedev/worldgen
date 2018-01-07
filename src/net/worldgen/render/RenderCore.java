package net.worldgen.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.worldgen.Handler;
import net.worldgen.object.Entity;
import net.worldgen.object.raw.Model;
import net.worldgen.render.shader.EntityShader;
import net.worldgen.render.shader.MeshShader;
import net.worldgen.render.shader.SkyboxShader;
import net.worldgen.render.shader.SurfaceShader;
import net.worldgen.render.shader.WaterShader;
import net.worldgen.util.Maths;
import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector3f;

public class RenderCore {

	private Handler handler;

	private EntityShader entityShader;
	private EntityRender entityRender;

	private WaterShader waterShader;
	private SurfaceShader surfaceShader;
	private PlanetRender planetRender;

	private MeshShader meshShader;
	private MeshRender meshRender;

	private SkyboxShader skyboxShader;
	private SkyboxRender skyboxRender;

	private Matrix4f projectionMatrix;
	private Map<Model, List<Entity>> entities = new HashMap<Model, List<Entity>>();
	private boolean renderModel;
	private boolean renderMesh;

	private final float NEAR_PLANE = 0.1f;
	private final float FAR_PLANE = 10000f;

	private Vector3f clearColor;

	public RenderCore(Handler handler, float fov, int width, int height, boolean renderModel, boolean renderMesh) {
		setup();
		this.handler = handler;
		updateProjectionMatrix(fov, width, height);
		this.renderModel = renderModel;
		this.renderMesh = renderMesh;
		entityShader = new EntityShader();
		entityRender = new EntityRender(entityShader);
		waterShader = new WaterShader();
		surfaceShader = new SurfaceShader();
		planetRender = new PlanetRender(surfaceShader, waterShader);
		meshShader = new MeshShader();
		meshRender = new MeshRender(meshShader);
		skyboxShader = new SkyboxShader();
		skyboxRender = new SkyboxRender(skyboxShader, FAR_PLANE);
	}

	public void render(float dt) {
		GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		for (Entity entity : handler.getEntities()) {
			processEntity(entity);
		}

		if (renderModel) {
			entityShader.start();
			entityShader.loadViewMatrix(handler.getCamera());
			entityRender.render(handler, this.entities, projectionMatrix);
			entityShader.stop();

			surfaceShader.start();
			surfaceShader.loadViewMatrix(handler.getCamera());
			planetRender.renderSurface(handler, handler.getPlanets(), projectionMatrix);
			surfaceShader.stop();
			waterShader.start();
			waterShader.updateTime(dt);
			waterShader.loadViewMatrix(handler.getCamera());
			planetRender.renderWater(handler, handler.getPlanets(), projectionMatrix);
			waterShader.stop();
		}
		if (renderMesh) {
			meshShader.start();
			meshShader.loadViewMatrix(handler.getCamera());
			meshRender.render(this.entities, handler.getPlanets(), projectionMatrix);
			meshShader.stop();
		}

		skyboxShader.start();
		skyboxShader.loadViewMatrix(handler.getCamera(), dt);
		skyboxRender.render(projectionMatrix);
		skyboxShader.stop();

		this.entities.clear();
	}

	private void processEntity(Entity entity) {
		Model entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}

	private void setup() {
		clearColor = new Vector3f(0, 0, 0);
		GL11.glEnable(GL_MULTISAMPLE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, 1f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		enableCulling();
	}

	public void updateProjectionMatrix(float fov, int width, int height) {
		projectionMatrix = Maths.createProjectionMatrix(fov, NEAR_PLANE, FAR_PLANE, width, height);
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void clearMemory() {
		entityShader.clearMemory();
		meshShader.clearMemory();
	}
}
