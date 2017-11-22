package net.worldgen.object.planet;

import java.util.Random;

import net.worldgen.object.raw.Material;
import net.worldgen.object.raw.Model;
import net.worldgen.util.ImprovedNoise;
import net.worldgen.util.Loader;
import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector3f;
import net.worldgen.util.vector.Vector4f;

public class GenPlanet {

	private float radius;
	private int octaves;
	private float freq;
	private float offset;
	private float amplitude;
	private float p;

	private int n;
	private int on;
	private int width;
	private int widthM1;

	private String surfacePath;
	private String oceanPath;
	private Model surfaceModel;
	private Model oceanModel;

	public GenPlanet(float radius, long seed, int octaves, float freq, float amplitude, int n, int on,
			String surfacePath, String oceanPath) {
		this.radius = radius;
		this.octaves = octaves;
		this.freq = freq;
		this.amplitude = amplitude;
		this.n = n;
		this.on = on;
		this.surfacePath = surfacePath;
		this.oceanPath = oceanPath;
		Random rdm = new Random();
		rdm.setSeed(seed);
		offset = rdm.nextFloat() * Short.MAX_VALUE;
		gen();
	}

	/** generate planet **/
	public void gen() {

		width = (int) Math.pow(2, n) + 1;
		widthM1 = width - 1;

		float diameter = radius * 2;
		p = radius - diameter / widthM1;
		System.out.println(p);
		System.out.println(radius);

		//// gen mesh
		// create matrices
		Matrix4f m1 = new Matrix4f();
		Matrix4f m2 = new Matrix4f();
		Matrix4f m3 = new Matrix4f();
		Matrix4f m4 = new Matrix4f();
		Matrix4f m5 = new Matrix4f();
		Matrix4f m6 = new Matrix4f();

		// rotate matrices
		m1.rotate((float) -Math.PI / 2f, new Vector3f(0, 1, 0));
		m2.rotate((float) Math.PI / 2f, new Vector3f(0, 1, 0));
		m3.rotate((float) Math.PI / 2f, new Vector3f(1, 0, 0));
		m4.rotate((float) -Math.PI / 2f, new Vector3f(1, 0, 0));
		m6.rotate((float) -Math.PI, new Vector3f(0, 1, 0));

		// permutate vertices / create mesh
		ModelDataBuffer buffer = genMesh(m1);
		buffer.append(genMesh(m2));
		buffer.append(genMesh(m3));
		buffer.append(genMesh(m4));
		buffer.append(genMesh(m5));
		buffer.append(genMesh(m6));
		surfaceModel = new Model(
				Loader.loadModel(buffer.getIndices(), buffer.getVertices(), buffer.getNormals(), buffer.getTexCoords()),
				new Material(surfacePath));

		int oceanWidth = (int) Math.pow(2, on) + 1;
		buffer = genOceanMesh(m1, oceanWidth);
		buffer.append(genOceanMesh(m2, oceanWidth));
		buffer.append(genOceanMesh(m3, oceanWidth));
		buffer.append(genOceanMesh(m4, oceanWidth));
		buffer.append(genOceanMesh(m5, oceanWidth));
		buffer.append(genOceanMesh(m6, oceanWidth));
		buffer.calcSphericNormals();
		oceanModel = new Model(
				Loader.loadModel(buffer.getIndices(), buffer.getVertices(), buffer.getNormals(), buffer.getTexCoords()),
				new Material(oceanPath));
	}

	/** generate mesh **/
	private ModelDataBuffer genMesh(Matrix4f m) {
		float[][][] v = new float[width + 2][width + 2][3];
		float[][][] n = new float[width][width][3];

		// calc vertices
		float diameter = radius * 2;
		for (int y = 1; y <= width; y++) {
			for (int x = 1; x <= width; x++) {
				if (x == 1) {
					v[x - 1][y][0] = -radius;
					v[x - 1][y][1] = radius - diameter / widthM1 * (y - 1);
					v[x - 1][y][2] = p;
					v[x - 1][y] = normalize(v[x - 1][y]);
					 float amp = getPointHeight(rotate(m, v[x - 1][y]));
					v[x - 1][y] = scale(v[x - 1][y], radius + amp * amplitude);
				} else if (y == 1) {
					v[x][y - 1][0] = diameter / widthM1 * (x - 1) - radius;
					v[x][y - 1][1] = radius;
					v[x][y - 1][2] = p;
					v[x][y - 1] = normalize(v[x][y - 1]);
					 float amp = getPointHeight(rotate(m, v[x][y - 1]));
					v[x][y - 1] = scale(v[x][y - 1], radius + amp * amplitude);
				} else if (x == width) {
					v[x + 1][y][0] = radius;
					v[x + 1][y][1] = radius - diameter / widthM1 * (y - 1);
					v[x + 1][y][2] = p;
					v[x + 1][y] = normalize(v[x + 1][y]);
					 float amp = getPointHeight(rotate(m, v[x + 1][y]));
					v[x + 1][y] = scale(v[x + 1][y], radius + amp * amplitude);
				} else if (y == width) {
					v[x][y + 1][0] = diameter / widthM1 * (x - 1) - radius;
					v[x][y + 1][1] = -radius;
					v[x][y + 1][2] = p;
					v[x][y + 1] = normalize(v[x][y + 1]);
					 float amp = getPointHeight(rotate(m, v[x][y + 1]));
					v[x][y + 1] = scale(v[x][y + 1], radius + amp * amplitude);
				}

				v[x][y][0] = diameter / widthM1 * (x - 1) - radius;
				v[x][y][1] = radius - diameter / widthM1 * (y - 1);
				v[x][y][2] = radius;
				v[x][y] = normalize(v[x][y]);
				 float amp = getPointHeight(rotate(m, v[x][y]));
				v[x][y] = scale(v[x][y], radius + amp * amplitude);
			}
		}

		// fixing missing vertices
		v[1][0] = v[0][1];
		v[width+1][1] = v[width][0];
		v[1][width+1] = v[0][width];
		v[width][width+1] = v[width+1][width];
		
		// calc normals
		for (int y = 1; y <= width; y++) {
			for (int x = 1; x <= width; x++) {
				float[] normal = new float[3];
				if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) { // |\|
					if (!(x == 1 && y == 1)) { // 1/2
						normal = addVector(normal, calcFaceNormal(v[x - 1][y - 1], v[x - 1][y], v[x][y]));
						normal = addVector(normal, calcFaceNormal(v[x - 1][y - 1], v[x][y], v[x][y - 1]));
					}
					if (!(x == 1 && y == width)) { // 5/6
						normal = addVector(normal, calcFaceNormal(v[x - 1][y], v[x - 1][y + 1], v[x][y]));
						normal = addVector(normal, calcFaceNormal(v[x - 1][y + 1], v[x][y + 1], v[x][y]));
					}
					if (!(x == width && y == 1)) { // 3/4
						normal = addVector(normal, calcFaceNormal(v[x][y - 1], v[x][y], v[x + 1][y - 1]));
						normal = addVector(normal, calcFaceNormal(v[x][y], v[x + 1][y], v[x + 1][y - 1]));
					}
					if (!(x == width && y == width)) { // 7/8
						normal = addVector(normal, calcFaceNormal(v[x][y], v[x][y + 1], v[x + 1][y + 1]));
						normal = addVector(normal, calcFaceNormal(v[x][y], v[x + 1][y + 1], v[x + 1][y]));
					}
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

				vertices[i - 3] = v[x][y + 1][0];
				vertices[i - 2] = v[x][y + 1][1];
				vertices[i - 1] = v[x][y + 1][2];

				normals[i - 3] = n[x - 1][y][0];
				normals[i - 2] = n[x - 1][y][1];
				normals[i - 1] = n[x - 1][y][2];

				texCoords[i2 - 2] = 1f / widthM1 * (x - 1);
				texCoords[i2 - 1] = 1f / widthM1 * y;

				if (x < width && y < widthM1) {
					i3 = (y * widthM1 + x) * 6;

					int v1 = y * width + x - 1;
					int v2 = y * width + x;
					int v3 = (y + 1) * width + x - 1;
					int v4 = (y + 1) * width + x;

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
		v = null;
		n = null;
		return new ModelDataBuffer(indices, vertices, normals, texCoords).rotate(m);
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
		float n = 0;
		for (int i = 1; i <= octaves; i++) {
			int oct = (int) Math.pow(2, i);
			int amp = (int) Math.pow(2, 1 + octaves - i);
			n += ImprovedNoise.noise(oct * freq * p[0] + offset, oct * freq * p[1], oct * freq * p[2]) * amp;
		}
		return sigmoid((float) (n / Math.pow(2, octaves))); // TODO fix noise
															// output
	}

	/** modify height value by sigmoid function **/
	private float sigmoid(float height) {
		float min = 0.3f;
		if (height <= 0)
			return height * min;
		height *= min + (float) (1 / (1 + Math.exp(-8 * (height - 0.7))));
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

	/** generate ocean mesh **/
	private ModelDataBuffer genOceanMesh(Matrix4f m, int width) {
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
		return new ModelDataBuffer(indices, vertices, normals, texCoords).rotateVertices(m);
	}

	// Getter

	public Model getSurfaceModel() {
		return surfaceModel;
	}

	public Model getOceanModel() {
		return oceanModel;
	}

}
