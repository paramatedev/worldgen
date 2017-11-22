package net.worldgen.object.raw;

public class RawModel {
	
	private int vao;
	private int vertexCount;

	public RawModel(int vao, int vertexCount) {
		this.vao = vao;
		this.vertexCount = vertexCount;
	}

	public int getVao() {
		return this.vao;
	}

	public int getVertexCount() {
		return this.vertexCount;
	}
}
