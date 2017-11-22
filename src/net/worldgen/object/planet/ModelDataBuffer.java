package net.worldgen.object.planet;

import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector4f;

public class ModelDataBuffer {

	private int[] indices;
	private float[] vertices;
	private float[] normals;
	private float[] texCoords;

	public ModelDataBuffer(int[] indices, float[] vertices, float[] normals, float[] texCoords) {
		this.indices = indices;
		this.vertices = vertices;
		this.normals = normals;
		this.texCoords = texCoords;
	}

	public void append(ModelDataBuffer buffer) {
		// create new arrays
		int[] indices = new int[this.indices.length + buffer.indices.length];
		float[] vertices = new float[this.vertices.length + buffer.vertices.length];
		float[] normals = new float[this.normals.length + buffer.normals.length];
		float[] texCoords = new float[this.texCoords.length + buffer.texCoords.length];
		// indices
		for (int i = 0; i < this.indices.length; i++)
			indices[i] = this.indices[i];
		for (int i = 0; i < buffer.indices.length; i++)
			indices[i + this.indices.length] = buffer.indices[i] + this.vertices.length / 3;
		// vertices
		for (int i = 0; i < this.vertices.length; i++)
			vertices[i] = this.vertices[i];
		for (int i = 0; i < buffer.vertices.length; i++)
			vertices[i + this.vertices.length] = buffer.vertices[i];
		// normals
		for (int i = 0; i < this.normals.length; i++)
			normals[i] = this.normals[i];
		for (int i = 0; i < buffer.normals.length; i++)
			normals[i + this.normals.length] = buffer.normals[i];
		// texCoords
		for (int i = 0; i < this.texCoords.length; i++)
			texCoords[i] = this.texCoords[i];
		for (int i = 0; i < buffer.texCoords.length; i++)
			texCoords[i + this.texCoords.length] = buffer.texCoords[i];
		// apply
		this.indices = indices;
		this.vertices = vertices;
		this.normals = normals;
		this.texCoords = texCoords;
	}

	public ModelDataBuffer rotate(Matrix4f m) {
		rotateVertices(m);
		rotateNormals(m);
		return this;
	}

	public ModelDataBuffer rotateVertices(Matrix4f m) {
		for (int i = 0; i < vertices.length / 3; i++) {
			Vector4f vertex = Matrix4f.transform(m,
					new Vector4f(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2], 1), null);
			vertices[i * 3] = vertex.x;
			vertices[i * 3 + 1] = vertex.y;
			vertices[i * 3 + 2] = vertex.z;
		}
		return this;
	}

	public ModelDataBuffer rotateNormals(Matrix4f m) {
		for (int i = 0; i < normals.length / 3; i++) {
			Vector4f normal = Matrix4f.transform(m,
					new Vector4f(normals[i * 3], normals[i * 3 + 1], normals[i * 3 + 2], 1), null);
			normals[i * 3] = normal.x;
			normals[i * 3 + 1] = normal.y;
			normals[i * 3 + 2] = normal.z;
		}
		return this;
	}

	public void calcSphericNormals() {
		for (int i = 0; i < vertices.length / 3; i++) {
			float length = (float) Math.sqrt(vertices[i * 3] * vertices[i * 3]
					+ vertices[i * 3 + 1] * vertices[i * 3 + 1] + vertices[i * 3 + 2] * vertices[i * 3 + 2]);
			normals[i * 3] = vertices[i * 3] / length;
			normals[i * 3 + 1] = vertices[i * 3 + 1] / length;
			normals[i * 3 + 2] = vertices[i * 3 + 2] / length;
		}
	}

	public ModelDataBuffer clone() {
		return new ModelDataBuffer(indices, vertices, normals, texCoords);
	}

	public int[] getIndices() {
		return indices;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getNormals() {
		return normals;
	}

	public float[] getTexCoords() {
		return texCoords;
	}

}
