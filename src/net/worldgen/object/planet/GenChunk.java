package net.worldgen.object.planet;

import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector3f;
import net.worldgen.util.vector.Vector4f;

public class GenChunk implements Runnable {

	private static final int SIZE = (int) Math.pow(2, 5) + 1;
	private static final int SIZEM1 = SIZE - 1;
	private static final int SIZEP1 = SIZE + 1;
	private static final int SIZEP2 = SIZE + 2;
	private static final int WSIZE = (int) Math.pow(2, 4) + 1;
	private static final int WSIZEM1 = WSIZE - 1;

	private float x, y, width;
	private Chunk chunk;
	private Matrix4f m;

	private float radius;
	private boolean hasWater;

	public GenChunk(float x, float y, float width, Chunk chunk, Matrix4f m, float radius, boolean hasWater) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.chunk = chunk;
		this.m = m;
		this.radius = radius;
		this.hasWater = hasWater;
	}

	@Override
	public void run() {
		// create mesh
		int[] indices = new int[SIZEP1 * SIZEP1 * 6];
		int vertexCount = SIZEP2 * SIZEP2;
		float[] vertices = new float[vertexCount * 3];

		int i = 0;
		int i3 = 0;
		for (int y = 0; y < SIZEP2; y++) {
			for (int x = 1; x <= SIZEP2; x++) {
				i = (y * SIZEP2 + x) * 3;

				float[] v = new float[3];
				if (x == 1) {
					v[0] = (this.x + width / SIZEM1 * (x - 1)) - 1;
					v[1] = 1 - (this.y + width / SIZEM1 * (y - 1));
					v[2] = 1;
					v = normalize(v);
					v = scale(v, 0.9f);
				} else if (x == SIZEP2) {
					v[0] = (this.x + width / SIZEM1 * (x - 3)) - 1;
					v[1] = 1 - (this.y + width / SIZEM1 * (y - 1));
					v[2] = 1;
					v = normalize(v);
					v = scale(v, 0.9f);
				} else if (y == 0) {
					v[0] = (this.x + width / SIZEM1 * (x - 2)) - 1;
					v[1] = 1 - (this.y + width / SIZEM1 * (y));
					v[2] = 1;
					v = normalize(v);
					v = scale(v, 0.9f);
				} else if (y == SIZEP1) {
					v[0] = (this.x + width / SIZEM1 * (x - 2)) - 1;
					v[1] = 1 - (this.y + width / SIZEM1 * (y - 2));
					v[2] = 1;
					v = normalize(v);
					v = scale(v, 0.9f);
				} else {
					v[0] = (this.x + width / SIZEM1 * (x - 2)) - 1;
					v[1] = 1 - (this.y + width / SIZEM1 * (y - 1));
					v[2] = 1;
					v = normalize(v);
				}
				vertices[i - 3] = v[0];
				vertices[i - 2] = v[1];
				vertices[i - 1] = v[2];

				if (x < SIZEP2 && y < SIZEP1) {
					i3 = (y * SIZEP1 + x) * 6;

					int v1 = y * SIZEP2 + x - 1;
					int v2 = y * SIZEP2 + x;
					int v3 = (y + 1) * SIZEP2 + x - 1;
					int v4 = (y + 1) * SIZEP2 + x;

					if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) { // |\|
						indices[i3 - 6] = v1;
						indices[i3 - 5] = v3;
						indices[i3 - 4] = v4;
						indices[i3 - 3] = v4;
						indices[i3 - 2] = v2;
						indices[i3 - 1] = v1;
					} else { // |/|
						indices[i3 - 6] = v1;
						indices[i3 - 5] = v3;
						indices[i3 - 4] = v2;
						indices[i3 - 3] = v4;
						indices[i3 - 2] = v2;
						indices[i3 - 1] = v3;
					}
				}
			}
		}
		float[] pos = new float[3];
		pos[0] = (this.x + width / SIZEM1 * (SIZE / 2f)) - 1;
		pos[1] = 1 - (this.y + width / SIZEM1 * (SIZE / 2f));
		pos[2] = 1;
		pos = rotate(m, scale(normalize(pos), radius));
		chunk.setPos(new Vector3f(pos[0], pos[1], pos[2]));
		chunk.setSurfaceBuffer(new ModelDataBuffer(indices, vertices, null, null).rotate(m));
		if (hasWater)
			chunk.setWaterBuffer(generateWater());
		chunk.setGenerated();
	}

	/** rotates a vector **/
	private float[] rotate(Matrix4f m, float[] v) {
		float[] vec = new float[3];
		Vector4f vertex = Matrix4f.transform(m, new Vector4f(v[0], v[1], v[2], 1), null);
		vec[0] = vertex.x;
		vec[1] = vertex.y;
		vec[2] = vertex.z;
		return vec;
	}

	/** calculate length of a vector **/
	private float getLength(float[] vec) {
		return (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
	}

	/** normalizes a vector **/
	private float[] normalize(float[] normal) {
		return scale(normal, 1 / getLength(normal));
	}

	/** scales a vector **/
	public float[] scale(float[] vec, float s) {
		float[] v = new float[3];
		v[0] = vec[0] * s;
		v[1] = vec[1] * s;
		v[2] = vec[2] * s;
		return v;
	}

	/** generates chunk of water **/
	private ModelDataBuffer generateWater() {
		int[] indices = new int[WSIZEM1 * WSIZEM1 * 6];
		int vertexCount = WSIZE * WSIZE;
		float[] vertices = new float[vertexCount * 3];
		float[] normals = new float[vertexCount * 3];
		float[] texCoords = new float[vertexCount * 2];

		int i = 0;
		int i2 = 0;
		int i3 = 0;

		for (int y = 0; y <= WSIZEM1; y++) {
			for (int x = 1; x <= WSIZE; x++) {
				i = (y * WSIZE + x) * 3;
				i2 = (y * WSIZE + x) * 2;

				float[] v = new float[3];
				v[0] = (this.x + width / WSIZEM1 * (x - 1)) - 1;
				v[1] = 1 - (this.y + width / WSIZEM1 * y);
				v[2] = 1;
				v = normalize(v);
				vertices[i - 3] = v[0];
				vertices[i - 2] = v[1];
				vertices[i - 1] = v[2];

				texCoords[i2 - 2] = 1f / WSIZEM1 * (x - 1);
				texCoords[i2 - 1] = 1f / WSIZEM1 * y;

				if (x < WSIZE && y < WSIZEM1) {
					i3 = (y * WSIZEM1 + x) * 6;

					int v1 = y * WSIZE + x - 1;
					int v2 = y * WSIZE + x;
					int v3 = (y + 1) * WSIZE + x - 1;
					int v4 = (y + 1) * WSIZE + x;

					if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) { // |/|
						indices[i3 - 6] = v1;
						indices[i3 - 5] = v3;
						indices[i3 - 4] = v2;
						indices[i3 - 3] = v4;
						indices[i3 - 2] = v2;
						indices[i3 - 1] = v3;
					} else { // |\|
						indices[i3 - 6] = v1;
						indices[i3 - 5] = v3;
						indices[i3 - 4] = v4;
						indices[i3 - 3] = v4;
						indices[i3 - 2] = v2;
						indices[i3 - 1] = v1;
					}
				}
			}
		}
		ModelDataBuffer buffer = new ModelDataBuffer(indices, vertices, normals, texCoords).rotateVertices(m);
		buffer.calcSphericNormals();
		return buffer;
	}
}
