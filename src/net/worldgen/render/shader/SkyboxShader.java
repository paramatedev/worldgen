package net.worldgen.render.shader;

import net.worldgen.object.Camera;
import net.worldgen.object.Sun;
import net.worldgen.util.Maths;
import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector3f;

public class SkyboxShader extends ShaderProgram {

	private static final String VERTEX_PATH = "/shader/skybox.vsh.txt";
	private static final String FRAGMENT_PATH = "/shader/skybox.fsh.txt";
	
	private float rotation = 0;

	private int location_projectionMatrix;
	private int location_viewMatrix;

	public SkyboxShader() {
		super(VERTEX_PATH, FRAGMENT_PATH);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	@Override
	protected void getUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera, float dt) {
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		rotation += Sun.ROTATION_SPEED * dt;
		rotation %= 360;
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}

}
