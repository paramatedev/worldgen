package net.worldgen.object.raw;

public class RawModel {

	private int vao;
	private int vertexCount;

	public RawModel() {
	};

	public RawModel(int vao, int vertexCount) {
		this.vao = vao;
		this.vertexCount = vertexCount;
	}

	public void setVao(int vao) {
		this.vao = vao;
	}

	public int getVao() {
		return this.vao;
	}

	public void setVertexCount(int vertexCount) {
		this.vertexCount = vertexCount;
	}

	public int getVertexCount() {
		return this.vertexCount;
	}
}
