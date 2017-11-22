package net.worldgen.object.planet;

import java.util.Random;

import net.worldgen.object.raw.Material;
import net.worldgen.object.raw.Model;
import net.worldgen.util.Loader;
import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector3f;

public class GenPlanetOld {

	private float radius;
	private long seed;

	private float deviation;
	private float amplitude;

	private int n;
	private int on;
	private int width;
	private int widthM1;

	private String surfacePath;
	private String oceanPath;
	private Model surfaceModel;
	private Model oceanModel;

	public GenPlanetOld(float radius, long seed, float amplitude, int n, int on, String surfacePath, String oceanPath) {
		this.radius = radius;
		this.seed = seed;
		this.amplitude = amplitude;
		this.n = n;
		this.on = on;
		this.surfacePath = surfacePath;
		this.oceanPath = oceanPath;
		gen();
	}

	public void gen() {

		width = (int) Math.pow(2, n) + 1;
		widthM1 = width - 1;

		// create faces
		float[][] left = new float[width][width];
		float[][] right = new float[width][width];
		float[][] bottom = new float[width][width];
		float[][] top = new float[width][width];
		float[][] front = new float[width][width];
		float[][] back = new float[width][width];

		//// gen random values
		if (seed != 0) {
			// set seed
			Random rdm = new Random();
			rdm.setSeed(seed);
			// assign start params
			float v1 = (rdm.nextFloat() * 2 - 1) * amplitude;
			float v2 = (rdm.nextFloat() * 2 - 1) * amplitude;
			float v3 = (rdm.nextFloat() * 2 - 1) * amplitude;
			float v4 = (rdm.nextFloat() * 2 - 1) * amplitude;
			float v5 = (rdm.nextFloat() * 2 - 1) * amplitude;
			float v6 = (rdm.nextFloat() * 2 - 1) * amplitude;
			float v7 = (rdm.nextFloat() * 2 - 1) * amplitude;
			float v8 = (rdm.nextFloat() * 2 - 1) * amplitude;

			left[0][0] = v6;
			left[widthM1][0] = v5;
			left[0][widthM1] = v2;
			left[widthM1][widthM1] = v1;

			right[0][0] = v8;
			right[widthM1][0] = v7;
			right[0][widthM1] = v4;
			right[widthM1][widthM1] = v3;

			bottom[0][0] = v1;
			bottom[widthM1][0] = v4;
			bottom[0][widthM1] = v2;
			bottom[widthM1][widthM1] = v3;

			top[0][0] = v6;
			top[widthM1][0] = v7;
			top[0][widthM1] = v5;
			top[widthM1][widthM1] = v8;

			front[0][0] = v5;
			front[widthM1][0] = v8;
			front[0][widthM1] = v1;
			front[widthM1][widthM1] = v4;

			back[0][0] = v7;
			back[widthM1][0] = v6;
			back[0][widthM1] = v3;
			back[widthM1][widthM1] = v2;

			// generate
			for (int i = 0; i < n; i++) {
				// calc deviation for this level of detail
				deviation = sigmoid(i, 4, 0.8f);

				// calc constants
				int p = (int) Math.pow(2, i);
				int p1 = (int) Math.pow(2, n - i);
				int p2 = p1 / 2;

				// diamond
				left = diamond(left, p, p1, p2, rdm);
				right = diamond(right, p, p1, p2, rdm);
				bottom = diamond(bottom, p, p1, p2, rdm);
				top = diamond(top, p, p1, p2, rdm);
				front = diamond(front, p, p1, p2, rdm);
				back = diamond(back, p, p1, p2, rdm);

				// square faces
				left = squareFace(left, p, p1, p2, rdm);
				right = squareFace(right, p, p1, p2, rdm);
				bottom = squareFace(bottom, p, p1, p2, rdm);
				top = squareFace(top, p, p1, p2, rdm);
				front = squareFace(front, p, p1, p2, rdm);
				back = squareFace(back, p, p1, p2, rdm);

				// square edges
				float[] e1 = squareEdge(getRow(front, p2), getRow(front, 0), getRow(top, widthM1 - p2), p, p1, p2, rdm);
				front = setRow(front, e1, 0);
				top = setRow(top, e1, widthM1);

				float[] e2 = squareEdge(getColumn(front, widthM1 - p2), getColumn(right, 0), getColumn(right, p2), p,
						p1, p2, rdm);
				front = setColumn(front, e2, widthM1);
				right = setColumn(right, e2, 0);

				float[] e3 = squareEdge(getRow(bottom, p2), getRow(bottom, 0), getRow(front, widthM1 - p2), p, p1, p2,
						rdm);
				bottom = setRow(bottom, e3, 0);
				front = setRow(front, e3, widthM1);

				float[] e4 = squareEdge(getColumn(left, widthM1 - p2), getColumn(front, 0), getColumn(front, p2), p, p1,
						p2, rdm);
				left = setColumn(left, e4, widthM1);
				front = setColumn(front, e4, 0);

				float[] e5 = squareEdge(getRow(right, p2), getRow(right, 0), flipEdge(getColumn(top, widthM1 - p2)), p,
						p1, p2, rdm);
				right = setRow(right, e5, 0);
				top = setColumn(top, flipEdge(e5), widthM1);

				float[] e6 = squareEdge(getColumn(bottom, widthM1 - p2), getColumn(bottom, widthM1),
						getRow(right, widthM1 - p2), p, p1, p2, rdm);
				bottom = setColumn(bottom, e6, widthM1);
				right = setRow(right, e6, widthM1);

				float[] e7 = squareEdge(flipEdge(getColumn(bottom, p2)), getRow(left, widthM1),
						getRow(left, widthM1 - p2), p, p1, p2, rdm);
				bottom = setColumn(bottom, flipEdge(e7), 0);
				left = setRow(left, e7, widthM1);

				float[] e8 = squareEdge(getRow(left, p2), getColumn(top, 0), getColumn(top, p2), p, p1, p2, rdm);
				left = setRow(left, e8, 0);
				top = setColumn(top, e8, 0);

				float[] e9 = squareEdge(getRow(back, p2), getRow(back, 0), flipEdge(getRow(top, p2)), p, p1, p2, rdm);
				back = setRow(back, e9, 0);
				top = setRow(top, flipEdge(e9), 0);

				float[] e10 = squareEdge(getColumn(right, widthM1 - p2), getColumn(back, 0), getColumn(back, p2), p, p1,
						p2, rdm);
				right = setColumn(right, e10, widthM1);
				back = setColumn(back, e10, 0);

				float[] e11 = squareEdge(flipEdge(getRow(bottom, widthM1 - p2)), getRow(back, widthM1),
						getRow(back, widthM1 - p2), p, p1, p2, rdm);
				bottom = setRow(bottom, flipEdge(e11), widthM1);
				back = setRow(back, e11, widthM1);

				float[] e12 = squareEdge(getColumn(back, widthM1 - p2), getColumn(left, 0), getColumn(left, p2), p, p1,
						p2, rdm);
				back = setColumn(back, e12, widthM1);
				left = setColumn(left, e12, 0);
			}
		}

		//// gen mesh
		// create matrices
		Matrix4f m1 = new Matrix4f();
		Matrix4f m2 = new Matrix4f();
		Matrix4f m3 = new Matrix4f();
		Matrix4f m4 = new Matrix4f();
		Matrix4f m6 = new Matrix4f();

		// rotate matrices
		m1.rotate((float) -Math.PI / 2f, new Vector3f(0, 1, 0));
		m2.rotate((float) Math.PI / 2f, new Vector3f(0, 1, 0));
		m3.rotate((float) Math.PI / 2f, new Vector3f(1, 0, 0));
		m4.rotate((float) -Math.PI / 2f, new Vector3f(1, 0, 0));
		m6.rotate((float) -Math.PI, new Vector3f(0, 1, 0));

		// permutate vertices / create mesh
		ModelDataBuffer buffer = genMeshFN(left).rotate(m1);
		buffer.append(genMeshFN(right).rotate(m2));
		buffer.append(genMeshFN(bottom).rotate(m3));
		buffer.append(genMeshFN(top).rotate(m4));
		buffer.append(genMeshFN(front));
		buffer.append(genMeshFN(back).rotate(m6));
		surfaceModel = new Model(
				Loader.loadModel(buffer.getIndices(), buffer.getVertices(), buffer.getNormals(), buffer.getTexCoords()),
				new Material(surfacePath));

		int oceanWidth = (int) Math.pow(2, on) + 1;
		buffer = genOceanMesh(oceanWidth, radius).rotateVertices(m1);
		buffer.append(genOceanMesh(oceanWidth, radius).rotateVertices(m2));
		buffer.append(genOceanMesh(oceanWidth, radius).rotateVertices(m3));
		buffer.append(genOceanMesh(oceanWidth, radius).rotateVertices(m4));
		buffer.append(genOceanMesh(oceanWidth, radius));
		buffer.append(genOceanMesh(oceanWidth, radius).rotateVertices(m6));
		buffer.calcSphericNormals();
		oceanModel = new Model(
				Loader.loadModel(buffer.getIndices(), buffer.getVertices(), buffer.getNormals(), buffer.getTexCoords()),
				new Material(oceanPath));
	}

	private float sigmoid(int i, float x, float m) {
		return (float) (1 - (1 / (1 + Math.pow(Math.E, -m * (i - x)))));
	}

	private float[] getColumn(float[][] face, int index) {
		return face[index];
	}

	private float[][] setColumn(float[][] face, float[] column, int index) {
		face[index] = column;
		return face;
	}

	private float[] getRow(float[][] face, int index) {
		float[] row = new float[width];
		for (int i = 0; i < width; i++) {
			row[i] = face[i][index];
		}
		return row;
	}

	private float[][] setRow(float[][] face, float[] row, int index) {
		for (int i = 0; i < width; i++) {
			face[i][index] = row[i];
		}
		return face;
	}

	private float[] flipEdge(float[] edge) {
		float[] flipped = new float[width];
		for (int i = 0; i < width; i++) {
			flipped[i] = edge[widthM1 - i];
		}
		return flipped;
	}

	private float[][] diamond(float[][] values, int p, int p1, int p2, Random rdm) {
		for (int y = 0; y < p; y++) {
			for (int x = 0; x < p; x++) {
				values[x * p1 + p2][y * p1 + p2] = avg4(values[x * p1][y * p1], values[x * p1 + p1][y * p1],
						values[x * p1][y * p1 + p1], values[x * p1 + p1][y * p1 + p1])
						+ (rdm.nextFloat() - 0.5f) * deviation;
			}
		}
		return values;
	}

	private float[][] squareFace(float[][] values, int p, int p1, int p2, Random rdm) {
		for (int y = 0; y < p; y++) {
			for (int x = 0; x < p; x++) {
				// left
				if (x != 0)
					values[x * p1][y * p1 + p2] = avg4(values[x * p1][y * p1], values[x * p1][y * p1 + p1],
							values[x * p1 - p2][y * p1 + p2], values[x * p1 + p2][y * p1 + p2])
							+ (rdm.nextFloat() - 0.5f) * deviation;

				// top
				if (y != 0)
					values[x * p1 + p2][y * p1] = avg4(values[x * p1][y * p1], values[x * p1 + p1][y * p1],
							values[x * p1 + p2][y * p1 - p2], values[x * p1 + p2][y * p1 + p2])
							+ (rdm.nextFloat() - 0.5f) * deviation;
			}
		}
		return values;
	}

	private float[] squareEdge(float[] left, float[] mid, float[] right, int p, int p1, int p2, Random rdm) {
		for (int y = 0; y < p; y++) {
			mid[y * p1 + p2] = avg4(left[y * p1 + p2], mid[y * p1], mid[y * p1 + p1], right[y * p1 + p2])
					+ (rdm.nextFloat() - 0.5f) * deviation;
		}
		return mid;
	}

	private float avg4(float v1, float v2, float v3, float v4) {
		return (v1 + v2 + v3 + v4) / 4f;
	}

	// private ModelDataBuffer genMesh(float[][] face) {
	// int[] indices = new int[widthM1 * widthM1 * 6];
	// int vertexCount = width * width;
	// float[] vertices = new float[vertexCount * 3];
	// float[] normals = new float[vertexCount * 3];
	// float[] texCoords = new float[vertexCount * 2];
	//
	// int i = 0;
	// int i2 = 0;
	// int i3 = 0;
	// for (int y = 0; y <= widthM1; y++) {
	// for (int x = 1; x <= width; x++) {
	// i = (y * width + x) * 3;
	// i2 = (y * width + x) * 2;
	//
	// float diameter = radius * 2;
	// vertices[i - 3] = diameter / widthM1 * (x - 1) - radius;
	// vertices[i - 2] = radius - diameter / widthM1 * y;
	// vertices[i - 1] = radius;
	//
	// float length = (float) Math.sqrt(vertices[i - 3] * vertices[i - 3] +
	// vertices[i - 2] * vertices[i - 2]
	// + vertices[i - 1] * vertices[i - 1]);
	// vertices[i - 3] /= length;
	// vertices[i - 2] /= length;
	// vertices[i - 1] /= length;
	// vertices[i - 3] *= radius + face[x - 1][y] * amplitude;
	// vertices[i - 2] *= radius + face[x - 1][y] * amplitude;
	// vertices[i - 1] *= radius + face[x - 1][y] * amplitude;
	//
	// texCoords[i2 - 2] = 1f / widthM1 * (x - 1);
	// texCoords[i2 - 1] = 1f / widthM1 * y;
	//
	// if (x < width && y < widthM1) {
	// i3 = (y * widthM1 + x) * 6;
	//
	// int v1 = y * width + x - 1;
	// int v2 = y * width + x;
	// int v3 = (y + 1) * width + x - 1;
	// int v4 = (y + 1) * width + x;
	//
	// if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
	// indices[i3 - 6] = v1;
	// indices[i3 - 5] = v3;
	// indices[i3 - 4] = v2;
	// indices[i3 - 3] = v4;
	// indices[i3 - 2] = v2;
	// indices[i3 - 1] = v3;
	// } else {
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
	// return new ModelDataBuffer(indices, vertices, normals, texCoords);
	// }

	private ModelDataBuffer genOceanMesh(int width, float radius) {
		int widthM1 = width - 1;
		int[] indices = new int[widthM1 * widthM1 * 6];
		int vertexCount = width * width;
		float[] vertices = new float[vertexCount * 3];
		float[] normals = new float[vertexCount * 3];
		float[] texCoords = new float[vertexCount * 2];

		int i = 0;
		int i2 = 0;
		int i3 = 0;
		for (int y = 0; y <= widthM1; y++) {
			for (int x = 1; x <= width; x++) {
				i = (y * width + x) * 3;
				i2 = (y * width + x) * 2;

				float diameter = radius * 2;
				vertices[i - 3] = diameter / widthM1 * (x - 1) - radius;
				vertices[i - 2] = radius - diameter / widthM1 * y;
				vertices[i - 1] = radius;

				float length = (float) Math.sqrt(vertices[i - 3] * vertices[i - 3] + vertices[i - 2] * vertices[i - 2]
						+ vertices[i - 1] * vertices[i - 1]);
				vertices[i - 3] /= length;
				vertices[i - 2] /= length;
				vertices[i - 1] /= length;
				vertices[i - 3] *= radius;
				vertices[i - 2] *= radius;
				vertices[i - 1] *= radius;

				texCoords[i2 - 2] = 1f / widthM1 * (x - 1);
				texCoords[i2 - 1] = 1f / widthM1 * y;

				if (x < width && y < widthM1) {
					i3 = (y * widthM1 + x) * 6;

					int v1 = y * width + x - 1;
					int v2 = y * width + x;
					int v3 = (y + 1) * width + x - 1;
					int v4 = (y + 1) * width + x;

					if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
						indices[i3 - 6] = v1;
						indices[i3 - 5] = v3;
						indices[i3 - 4] = v2;
						indices[i3 - 3] = v4;
						indices[i3 - 2] = v2;
						indices[i3 - 1] = v3;
					} else {
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
		return new ModelDataBuffer(indices, vertices, normals, texCoords);
	}

	private ModelDataBuffer genMeshFN(float[][] face) {
		int vertexCount = widthM1 * widthM1 * 6;
		int[] indices = new int[vertexCount];
		float[] vertices = new float[vertexCount * 3];
		float[] normals = new float[vertexCount * 3];
		float[] texCoords = new float[vertexCount * 2];

		float[][][] tv = new float[width][width][3];

		for (int y = 0; y < width; y++) {
			for (int x = 0; x < width; x++) {
				float diameter = radius * 2;
				tv[x][y][0] = diameter / widthM1 * x - radius;
				tv[x][y][1] = radius - diameter / widthM1 * y;
				tv[x][y][2] = radius;
				float length = (float) Math
						.sqrt(tv[x][y][0] * tv[x][y][0] + tv[x][y][1] * tv[x][y][1] + tv[x][y][2] * tv[x][y][2]);
				tv[x][y][0] /= length;
				tv[x][y][1] /= length;
				tv[x][y][2] /= length;
				tv[x][y][0] *= radius + face[x][y] * amplitude;
				tv[x][y][1] *= radius + face[x][y] * amplitude;
				tv[x][y][2] *= radius + face[x][y] * amplitude;
			}
		}

		int offset = 0;
		for (int y = 0; y < widthM1; y++) {
			for (int x = 0; x < widthM1; x++) {
				if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
					vertices[offset * 3] = tv[x + 1][y][0];
					vertices[offset * 3 + 1] = tv[x + 1][y][1];
					vertices[offset * 3 + 2] = tv[x + 1][y][2];
					vertices[offset * 3 + 3] = tv[x][y][0];
					vertices[offset * 3 + 4] = tv[x][y][1];
					vertices[offset * 3 + 5] = tv[x][y][2];
					vertices[offset * 3 + 6] = tv[x + 1][y + 1][0];
					vertices[offset * 3 + 7] = tv[x + 1][y + 1][1];
					vertices[offset * 3 + 8] = tv[x + 1][y + 1][2];
					vertices[offset * 3 + 9] = tv[x + 1][y + 1][0];
					vertices[offset * 3 + 10] = tv[x + 1][y + 1][1];
					vertices[offset * 3 + 11] = tv[x + 1][y + 1][2];
					vertices[offset * 3 + 12] = tv[x][y][0];
					vertices[offset * 3 + 13] = tv[x][y][1];
					vertices[offset * 3 + 14] = tv[x][y][2];
					vertices[offset * 3 + 15] = tv[x][y + 1][0];
					vertices[offset * 3 + 16] = tv[x][y + 1][1];
					vertices[offset * 3 + 17] = tv[x][y + 1][2];
					texCoords[offset * 2] = 1f / widthM1 * (x + 1);
					texCoords[offset * 2 + 1] = 1f / widthM1 * y;
					texCoords[offset * 2 + 2] = 1f / widthM1 * x;
					texCoords[offset * 2 + 3] = 1f / widthM1 * y;
					texCoords[offset * 2 + 4] = 1f / widthM1 * (x + 1);
					texCoords[offset * 2 + 5] = 1f / widthM1 * (y + 1);
					texCoords[offset * 2 + 6] = 1f / widthM1 * (x + 1);
					texCoords[offset * 2 + 7] = 1f / widthM1 * (y + 1);
					texCoords[offset * 2 + 8] = 1f / widthM1 * x;
					texCoords[offset * 2 + 9] = 1f / widthM1 * y;
					texCoords[offset * 2 + 10] = 1f / widthM1 * x;
					texCoords[offset * 2 + 11] = 1f / widthM1 * (y + 1);
				} else {
					vertices[offset * 3] = tv[x][y][0];
					vertices[offset * 3 + 1] = tv[x][y][1];
					vertices[offset * 3 + 2] = tv[x][y][2];
					vertices[offset * 3 + 3] = tv[x][y + 1][0];
					vertices[offset * 3 + 4] = tv[x][y + 1][1];
					vertices[offset * 3 + 5] = tv[x][y + 1][2];
					vertices[offset * 3 + 6] = tv[x + 1][y][0];
					vertices[offset * 3 + 7] = tv[x + 1][y][1];
					vertices[offset * 3 + 8] = tv[x + 1][y][2];
					vertices[offset * 3 + 9] = tv[x + 1][y][0];
					vertices[offset * 3 + 10] = tv[x + 1][y][1];
					vertices[offset * 3 + 11] = tv[x + 1][y][2];
					vertices[offset * 3 + 12] = tv[x][y + 1][0];
					vertices[offset * 3 + 13] = tv[x][y + 1][1];
					vertices[offset * 3 + 14] = tv[x][y + 1][2];
					vertices[offset * 3 + 15] = tv[x + 1][y + 1][0];
					vertices[offset * 3 + 16] = tv[x + 1][y + 1][1];
					vertices[offset * 3 + 17] = tv[x + 1][y + 1][2];
					texCoords[offset * 2] = 1f / widthM1 * x;
					texCoords[offset * 2 + 1] = 1f / widthM1 * y;
					texCoords[offset * 2 + 2] = 1f / widthM1 * x;
					texCoords[offset * 2 + 3] = 1f / widthM1 * (y + 1);
					texCoords[offset * 2 + 4] = 1f / widthM1 * (x + 1);
					texCoords[offset * 2 + 5] = 1f / widthM1 * y;
					texCoords[offset * 2 + 6] = 1f / widthM1 * (x + 1);
					texCoords[offset * 2 + 7] = 1f / widthM1 * y;
					texCoords[offset * 2 + 8] = 1f / widthM1 * x;
					texCoords[offset * 2 + 9] = 1f / widthM1 * (y + 1);
					texCoords[offset * 2 + 10] = 1f / widthM1 * (x + 1);
					texCoords[offset * 2 + 11] = 1f / widthM1 * (y + 1);
				}

				float[] n1 = getFaceNormal(getArray(vertices, offset * 3, 9));
				float[] n2 = getFaceNormal(getArray(vertices, offset * 3 + 9, 9));

				normals[offset * 3] = n1[0];
				normals[offset * 3 + 1] = n1[1];
				normals[offset * 3 + 2] = n1[2];
				normals[offset * 3 + 3] = n1[0];
				normals[offset * 3 + 4] = n1[1];
				normals[offset * 3 + 5] = n1[2];
				normals[offset * 3 + 6] = n1[0];
				normals[offset * 3 + 7] = n1[1];
				normals[offset * 3 + 8] = n1[2];
				normals[offset * 3 + 9] = n2[0];
				normals[offset * 3 + 10] = n2[1];
				normals[offset * 3 + 11] = n2[2];
				normals[offset * 3 + 12] = n2[0];
				normals[offset * 3 + 13] = n2[1];
				normals[offset * 3 + 14] = n2[2];
				normals[offset * 3 + 15] = n2[0];
				normals[offset * 3 + 16] = n2[1];
				normals[offset * 3 + 17] = n2[2];

				indices[offset] = offset;
				indices[offset + 1] = offset + 1;
				indices[offset + 2] = offset + 2;
				indices[offset + 3] = offset + 3;
				indices[offset + 4] = offset + 4;
				indices[offset + 5] = offset + 5;

				offset += 6;
			}
		}
		return new ModelDataBuffer(indices, vertices, normals, texCoords);
	}

	private float[] getArray(float[] array, int index, int length) {
		float[] out = new float[length];
		for (int i = index; i < index + length; i++) {
			out[i - index] = array[i];
		}
		return out;
	}

	private float[] getFaceNormal(float[] tri) {
		float[] n = new float[3];
		float v1x = tri[3] - tri[0];
		float v1y = tri[4] - tri[1];
		float v1z = tri[5] - tri[2];
		float v2x = tri[6] - tri[0];
		float v2y = tri[7] - tri[1];
		float v2z = tri[8] - tri[2];
		n[0] = v1y * v2z - v1z * v2y;
		n[1] = v1z * v2x - v1x * v2z;
		n[2] = v1x * v2y - v1y * v2x;
		float length = (float) Math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2]);
		n[0] /= length;
		n[1] /= length;
		n[2] /= length;
		return n;
	}

	public Model getSurfaceModel() {
		return surfaceModel;
	}

	public Model getOceanModel() {
		return oceanModel;
	}

}
