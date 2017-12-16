package net.worldgen.object.planet;

import net.worldgen.util.ImprovedNoise;
import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector3f;
import net.worldgen.util.vector.Vector4f;

public class GenChunk implements Runnable {

	private static final int SIZE = (int) Math.pow(2, 4) + 1;
	private static final int SIZEM1 = SIZE - 1;
	private static final int WSIZE = (int) Math.pow(2, 3) + 1;
	private static final int WSIZEM1 = WSIZE - 1;

	private float radius;
	private float amplitude;
	private float x, y, width;
	private Chunk chunk;
	private Matrix4f m;

	// noise
	private float offset;
	private int octaves;
	private float freq;

	public GenChunk(float x, float y, float width, Chunk chunk, Matrix4f m, PlanetData data) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.chunk = chunk;
		this.m = m;
		radius = data.getRadius();
		amplitude = data.getAmplitude();
		offset = data.getOffset();
		octaves = data.getOctaves();
		freq = data.getFreq();
	}

	@Override
	public void run() {
		float[][][] v = new float[SIZE + 2][SIZE + 2][3];
		float[][][] n = new float[SIZE][SIZE][3];

		boolean hasWater = false;

		// calc vertices
		for (int y = 0; y < SIZE + 2; y++) {
			for (int x = 0; x < SIZE + 2; x++) {
				v[x][y][0] = (this.x + width / SIZEM1 * (x - 1)) - 1;
				v[x][y][1] = 1 - (this.y + width / SIZEM1 * (y - 1));
				v[x][y][2] = 1;
				v[x][y] = normalize(v[x][y]);
				float amp = getPointHeight(scale(rotate(m, v[x][y]), 1));
				v[x][y] = scale(v[x][y], radius + amp * amplitude);
				if (amp <= 0)
					hasWater = true;
			}
		}

		// calc normals
		for (int y = 1; y <= SIZE; y++) {
			for (int x = 1; x <= SIZE; x++) {
				float[] normal = new float[3];
				if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) { // |\|
					normal = addVector(normal, calcFaceNormal(v[x - 1][y - 1], v[x - 1][y], v[x][y]));
					normal = addVector(normal, calcFaceNormal(v[x - 1][y - 1], v[x][y], v[x][y - 1]));
					normal = addVector(normal, calcFaceNormal(v[x - 1][y], v[x - 1][y + 1], v[x][y]));
					normal = addVector(normal, calcFaceNormal(v[x - 1][y + 1], v[x][y + 1], v[x][y]));
					normal = addVector(normal, calcFaceNormal(v[x][y - 1], v[x][y], v[x + 1][y - 1]));
					normal = addVector(normal, calcFaceNormal(v[x][y], v[x + 1][y], v[x + 1][y - 1]));
					normal = addVector(normal, calcFaceNormal(v[x][y], v[x][y + 1], v[x + 1][y + 1]));
					normal = addVector(normal, calcFaceNormal(v[x][y], v[x + 1][y + 1], v[x + 1][y]));
				} else { // |/|
					normal = addVector(normal, calcFaceNormal(v[x - 1][y - 1], v[x - 1][y], v[x][y - 1]));
					normal = addVector(normal, calcFaceNormal(v[x - 1][y], v[x][y], v[x][y - 1]));
					normal = addVector(normal, calcFaceNormal(v[x][y - 1], v[x][y], v[x + 1][y]));
					normal = addVector(normal, calcFaceNormal(v[x][y - 1], v[x + 1][y], v[x + 1][y - 1]));
					normal = addVector(normal, calcFaceNormal(v[x - 1][y], v[x - 1][y + 1], v[x][y + 1]));
					normal = addVector(normal, calcFaceNormal(v[x - 1][y], v[x][y + 1], v[x][y]));
					normal = addVector(normal, calcFaceNormal(v[x][y], v[x][y + 1], v[x + 1][y]));
					normal = addVector(normal, calcFaceNormal(v[x][y + 1], v[x + 1][y + 1], v[x + 1][y]));
				}
				n[x - 1][y - 1] = normalize(normal);
			}
		}

		// create mesh
		int[] indices = new int[(SIZEM1 * SIZEM1 + SIZEM1 * 4) * 6];
		int vertexCount = SIZE * SIZE + SIZE * 4;
		float[] vertices = new float[vertexCount * 3];
		float[] normals = new float[vertexCount * 3];

		int i = 0;
		int i2 = 0;
		int i3 = 0;
		for (int y = 0; y < SIZE; y++) {
			for (int x = 1; x <= SIZE; x++) {
				i = (y * SIZE + x) * 3;
				i2 = (y * SIZE + x) * 2;

				vertices[i - 3] = v[x][y + 1][0];
				vertices[i - 2] = v[x][y + 1][1];
				vertices[i - 1] = v[x][y + 1][2];

				normals[i - 3] = n[x - 1][y][0];
				normals[i - 2] = n[x - 1][y][1];
				normals[i - 1] = n[x - 1][y][2];

				if (x < SIZE && y < SIZEM1) {
					i3 = (y * SIZEM1 + x) * 6;

					int v1 = y * SIZE + x - 1;
					int v2 = y * SIZE + x;
					int v3 = (y + 1) * SIZE + x - 1;
					int v4 = (y + 1) * SIZE + x;

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

		// fix chunk error
		int io = SIZEM1 * SIZEM1 * 6;
		int vo = SIZE * SIZE * 3;

		for (int e = 0; e < 4; e++) {
			for (int x = 0; x < SIZEM1; x++) {
				
				int v1 = 0;
				int v2 = 0;
				int v3 = 0;
				int v4 = 0;
				
				switch(e) {
				case 0:
					
					normals[vo + x] = 0;
					v1 = vo + x - 1;
					v2 = vo + x;
					v3 = vo + SIZE + x - 1;
					v4 = vo + SIZE + x;
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				}
				
				vertices[vo + SIZE * e + x * 3] = 0;
				vertices[vo + SIZE * e + x * 3 + 1] = 0;
				vertices[vo + SIZE * e + x * 3 + 2] = 0;

				if (x % 2 == 0 || x % 2 == 1) { // |/|
					indices[io] = v1;
					indices[io + 1] = v3;
					indices[io + 2] = v2;
					indices[io + 3] = v4;
					indices[io + 4] = v2;
					indices[io + 5] = v3;
				} else { // |\|
					indices[io] = v1;
					indices[io + 1] = v3;
					indices[io + 2] = v4;
					indices[io + 3] = v4;
					indices[io + 4] = v2;
					indices[io + 5] = v1;
				}
			}
		}

		// boolean leftWrap = false;
		// boolean rightWrap = false;
		// boolean topWrap = false;
		// boolean bottomWrap = false;
		//
		// float diameter = radius * 2;
		// float p = radius - diameter / SIZEM1;
		//
		// float[][][] v = new float[SIZE + 2][SIZE + 2][3];
		// float[][][] n = new float[SIZE][SIZE][3];
		//
		// // calc vertices
		// for (int y = 1; y <= SIZE; y++) {
		// for (int x = 1; x <= SIZE; x++) {
		// if (x == 1) {
		// v[x - 1][y][0] = -radius;
		// v[x - 1][y][1] = radius - diameter / SIZEM1 * (y - 1);
		// v[x - 1][y][2] = p;
		// v[x - 1][y] = normalize(v[x - 1][y]);
		// float amp = getPointHeight(rotate(m, v[x - 1][y]));
		// v[x - 1][y] = scale(v[x - 1][y], radius + amp * amplitude);
		// }
		// if (y == 1) {
		// v[x][y - 1][0] = diameter / SIZEM1 * (x - 1) - radius;
		// v[x][y - 1][1] = radius;
		// v[x][y - 1][2] = p;
		// v[x][y - 1] = normalize(v[x][y - 1]);
		// float amp = getPointHeight(rotate(m, v[x][y - 1]));
		// v[x][y - 1] = scale(v[x][y - 1], radius + amp * amplitude);
		// }
		// if (x == SIZE) {
		// v[x + 1][y][0] = radius;
		// v[x + 1][y][1] = radius - diameter / SIZEM1 * (y - 1);
		// v[x + 1][y][2] = p;
		// v[x + 1][y] = normalize(v[x + 1][y]);
		// float amp = getPointHeight(rotate(m, v[x + 1][y]));
		// v[x + 1][y] = scale(v[x + 1][y], radius + amp * amplitude);
		// }
		// if (y == SIZE) {
		// v[x][y + 1][0] = diameter / SIZEM1 * (x - 1) - radius;
		// v[x][y + 1][1] = -radius;
		// v[x][y + 1][2] = p;
		// v[x][y + 1] = normalize(v[x][y + 1]);
		// float amp = getPointHeight(rotate(m, v[x][y + 1]));
		// v[x][y + 1] = scale(v[x][y + 1], radius + amp * amplitude);
		// }
		//
		// v[x][y][0] = diameter / SIZEM1 * (x - 1) - radius;
		// v[x][y][1] = radius - diameter / SIZEM1 * (y - 1);
		// v[x][y][2] = radius;
		// v[x][y] = normalize(v[x][y]);
		// float amp = getPointHeight(rotate(m, v[x][y]));
		// v[x][y] = scale(v[x][y], radius + amp * amplitude);
		// }
		// }
		//
		// // fixing missing vertices
		// if (leftWrap && topWrap)
		// v[1][0] = v[0][1];
		// if (rightWrap && topWrap)
		// v[SIZE + 1][1] = v[SIZE][0];
		// if (leftWrap && bottomWrap)
		// v[1][SIZE + 1] = v[0][SIZE];
		// if (rightWrap && bottomWrap)
		// v[SIZE][SIZE + 1] = v[SIZE + 1][SIZE];
		//
		// // calc normals
		// for (int y = 1; y <= SIZE; y++) {
		// for (int x = 1; x <= SIZE; x++) {
		// float[] normal = new float[3];
		// if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) { // |\|
		// if (!(x == 1 && y == 1 && leftWrap && topWrap)) { // 1/2
		// normal = addVector(normal, calcFaceNormal(v[x - 1][y - 1], v[x - 1][y],
		// v[x][y]));
		// normal = addVector(normal, calcFaceNormal(v[x - 1][y - 1], v[x][y], v[x][y -
		// 1]));
		// }
		// if (!(x == 1 && y == SIZE && leftWrap && bottomWrap)) { // 5/6
		// normal = addVector(normal, calcFaceNormal(v[x - 1][y], v[x - 1][y + 1],
		// v[x][y]));
		// normal = addVector(normal, calcFaceNormal(v[x - 1][y + 1], v[x][y + 1],
		// v[x][y]));
		// }
		// if (!(x == SIZE && y == 1 && rightWrap && topWrap)) { // 3/4
		// normal = addVector(normal, calcFaceNormal(v[x][y - 1], v[x][y], v[x + 1][y -
		// 1]));
		// normal = addVector(normal, calcFaceNormal(v[x][y], v[x + 1][y], v[x + 1][y -
		// 1]));
		// }
		// if (!(x == SIZE && y == SIZE && rightWrap && bottomWrap)) { // 7/8
		// normal = addVector(normal, calcFaceNormal(v[x][y], v[x][y + 1], v[x + 1][y +
		// 1]));
		// normal = addVector(normal, calcFaceNormal(v[x][y], v[x + 1][y + 1], v[x +
		// 1][y]));
		// }
		// } else { // |/|
		// normal = addVector(normal, calcFaceNormal(v[x - 1][y - 1], v[x - 1][y],
		// v[x][y - 1]));
		// normal = addVector(normal, calcFaceNormal(v[x - 1][y], v[x][y], v[x][y -
		// 1]));
		// normal = addVector(normal, calcFaceNormal(v[x][y - 1], v[x][y], v[x +
		// 1][y]));
		// normal = addVector(normal, calcFaceNormal(v[x][y - 1], v[x + 1][y], v[x +
		// 1][y - 1]));
		// normal = addVector(normal, calcFaceNormal(v[x - 1][y], v[x - 1][y + 1],
		// v[x][y + 1]));
		// normal = addVector(normal, calcFaceNormal(v[x - 1][y], v[x][y + 1],
		// v[x][y]));
		// normal = addVector(normal, calcFaceNormal(v[x][y], v[x][y + 1], v[x +
		// 1][y]));
		// normal = addVector(normal, calcFaceNormal(v[x][y + 1], v[x + 1][y + 1], v[x +
		// 1][y]));
		// }
		// n[x - 1][y - 1] = normalize(normal);
		// }
		// }
		//
		// // create mesh
		// int[] indices = new int[SIZEM1 * SIZEM1 * 6];
		// int vertexCount = SIZE * SIZE;
		// float[] vertices = new float[vertexCount * 3];
		// float[] normals = new float[vertexCount * 3];
		// float[] texCoords = new float[vertexCount * 2];
		//
		// int i = 0;
		// int i2 = 0;
		// int i3 = 0;
		// for (int y = 0; y <= SIZEM1; y++) {
		// for (int x = 1; x <= SIZE; x++) {
		// i = (y * SIZE + x) * 3;
		// i2 = (y * SIZE + x) * 2;
		//
		// vertices[i - 3] = v[x][y + 1][0];
		// vertices[i - 2] = v[x][y + 1][1];
		// vertices[i - 1] = v[x][y + 1][2];
		//
		// normals[i - 3] = n[x - 1][y][0];
		// normals[i - 2] = n[x - 1][y][1];
		// normals[i - 1] = n[x - 1][y][2];
		//
		// texCoords[i2 - 2] = 1f / SIZEM1 * (x - 1);
		// texCoords[i2 - 1] = 1f / SIZEM1 * y;
		//
		// if (x < SIZE && y < SIZEM1) {
		// i3 = (y * SIZEM1 + x) * 6;
		//
		// int v1 = y * SIZE + x - 1;
		// int v2 = y * SIZE + x;
		// int v3 = (y + 1) * SIZE + x - 1;
		// int v4 = (y + 1) * SIZE + x;
		//
		// if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) { // |/|
		// indices[i3 - 6] = v1;
		// indices[i3 - 5] = v3;
		// indices[i3 - 4] = v2;
		// indices[i3 - 3] = v4;
		// indices[i3 - 2] = v2;
		// indices[i3 - 1] = v3;
		// } else { // |\|
		// indices[i3 - 6] = v1;
		// indices[i3 - 5] = v3;
		// indices[i3 - 4] = v4;
		// indices[i3 - 3] = v4;
		// indices[i3 - 2] = v2;
		// indices[i3 - 1] = v1;
		// }
		// }
		// }
		// }
		// v = null;
		// n = null;

		float[] pos = v[SIZEM1 / 2][SIZEM1 / 2];
		pos = addVector(v[0][0], pos);
		pos = addVector(v[SIZEM1][0], pos);
		pos = addVector(v[0][SIZEM1], pos);
		pos = addVector(v[SIZEM1][SIZEM1], pos);
		pos = rotate(m, scale(pos, 0.2f));
		chunk.setPos(new Vector3f(pos[0], pos[1], pos[2]));
		v = null;
		n = null;
		chunk.setSurfaceBuffer(new ModelDataBuffer(indices, vertices, normals).rotate(m));
		chunk.setHasWater(hasWater);
		if (hasWater) {
			chunk.setWaterBuffer(generateWater());
		}
		chunk.setGenerated();
	}

	/** prints out a vector **/
	public void pVec(float[] vec) {
		System.out.println(vec[0] + ", " + vec[1] + ", " + vec[2]);
	}

	/** adds up two vectors **/
	private float[] addVector(float[] v1, float[] v2) {
		float[] v = new float[3];
		v[0] = v1[0] + v2[0];
		v[1] = v1[1] + v2[1];
		v[2] = v1[2] + v2[2];
		return v;
	}

	/** get height of point based on perlin noise **/
	private float getPointHeight(float[] p) {
		double n = 0;
		for (int i = 1; i <= octaves; i++) {
			int oct = (int) Math.pow(2, i);
			int amp = (int) Math.pow(2, 1 + octaves - i);
			n += ImprovedNoise.noise(oct * freq * p[0] + offset, oct * freq * p[1], oct * freq * p[2]) * amp;
		}
		return (float) sigmoid((float) (n / Math.pow(2, octaves))); // TODO fix noise output
	}

	/** modify height value by sigmoid function **/
	private double sigmoid(double height) {
		double min = 0.3;
		if (height <= 0)
			return height * min;
		height *= min + 1 / (1 + Math.exp(-8 * (height - 0.7)));
		return height;
	}

	/** calculate length of a vector **/
	private float getLength(float[] vec) {
		return (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
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

	/** calculate normal vector of a face **/
	private float[] calcFaceNormal(float[] p1, float[] p2, float[] p3) {
		float[] n = new float[3];
		float v1x = p2[0] - p1[0];
		float v1y = p2[1] - p1[1];
		float v1z = p2[2] - p1[2];
		float v2x = p3[0] - p1[0];
		float v2y = p3[1] - p1[1];
		float v2z = p3[2] - p1[2];
		n[0] = v1y * v2z - v1z * v2y;
		n[1] = v1z * v2x - v1x * v2z;
		n[2] = v1x * v2y - v1y * v2x;
		return normalize(n);
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
				v = scale(normalize(v), radius);
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
		ModelDataBuffer buffer = new ModelDataBuffer(indices, vertices, normals).rotateVertices(m);
		buffer.calcSphericNormals();
		return buffer;
	}
}
