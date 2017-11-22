package net.worldgen.render.shader;

import net.worldgen.object.Camera;
import net.worldgen.object.DirectionLight;
import net.worldgen.util.Maths;
import net.worldgen.util.vector.Matrix4f;

public class PlanetShader extends ShaderProgram {

	private static final String VERTEX_PATH = "/shader/planet.vsh.txt";
	private static final String FRAGMENT_PATH = "/shader/planet.fsh.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_dirLightDir;
	private int location_dirLightColor;
	private int location_dirLightIntensity;
	
	public PlanetShader() {
		super(VERTEX_PATH, FRAGMENT_PATH);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "vertex");
		super.bindAttribute(1, "normal");
		super.bindAttribute(2, "texCoord");
	}
	
	@Override
	protected void getUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_dirLightDir = super.getUniformLocation("dirLightDir");
		location_dirLightColor = super.getUniformLocation("dirLightColor");
		location_dirLightIntensity = super.getUniformLocation("dirLightIntensity");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}

	public void loadDirLight(DirectionLight light) {
		super.loadVector(location_dirLightDir, light.getDirection());
		super.loadVector(location_dirLightColor, light.getColor());
		super.loadFloat(location_dirLightIntensity, light.getIntensity());
	}
	
}
