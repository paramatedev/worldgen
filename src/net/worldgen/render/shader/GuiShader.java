package net.worldgen.render.shader;

import net.worldgen.util.vector.Matrix4f;

public class GuiShader extends ShaderProgram {

	private static final String VERTEX_PATH = "/shader/gui.vsh.txt";
	private static final String FRAGMENT_PATH = "/shader/gui.fsh.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	
	public GuiShader() {
		super(VERTEX_PATH, FRAGMENT_PATH);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "vertex");
		super.bindAttribute(1, "texCoord");
	}
	
	@Override
	protected void getUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}

}
