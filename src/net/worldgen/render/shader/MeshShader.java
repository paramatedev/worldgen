package net.worldgen.render.shader;

import net.worldgen.object.Camera;
import net.worldgen.util.Maths;
import net.worldgen.util.vector.Matrix4f;

public class MeshShader extends GeoShaderProgram {

	private static final String VERTEX_PATH = "/shader/mesh.vsh.txt";
	private static final String GEOMETRY_PATH = "/shader/mesh.gsh.txt";
	private static final String FRAGMENT_PATH = "/shader/mesh.fsh.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	public MeshShader() {
		super(VERTEX_PATH, GEOMETRY_PATH, FRAGMENT_PATH);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "vertex");
	}
	
	@Override
	protected void getUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
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

}
