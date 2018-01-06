package net.worldgen.object.planet;

import java.util.concurrent.ExecutorService;

import net.worldgen.object.Camera;
import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector3f;
import net.worldgen.util.vector.Vector4f;

public class Planet {

	private final int OUT_TIME = 2000; // ms

	private Vector3f pos;
	private Vector3f rot;
	private Camera camera;
	private float radius;
	private PlanetData data;
	private Chunk[] faces;
	private Matrix4f transformation;
	private int index;

	public Planet(ExecutorService executor, Camera camera, float radius, PlanetData data, Vector3f pos, Vector3f rot) {
		this.camera = camera;
		this.radius = radius;
		this.data = data;
		this.pos = pos;
		this.rot = rot;

		updateTransformation();

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
		
		faces = new Chunk[6];
		faces[0] = new Chunk(0, 0, 0, null, m1, executor, radius, data.hasWater());
		faces[1] = new Chunk(0, 0, 0, null, m2, executor, radius, data.hasWater());
		faces[2] = new Chunk(0, 0, 0, null, m3, executor, radius, data.hasWater());
		faces[3] = new Chunk(0, 0, 0, null, m4, executor, radius, data.hasWater());
		faces[4] = new Chunk(0, 0, 0, null, m5, executor, radius, data.hasWater());
		faces[5] = new Chunk(0, 0, 0, null, m6, executor, radius, data.hasWater());
	}

	public void update() {
		Vector4f temp = new Vector4f(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z, 1);
		Matrix4f.transform(transformation, temp, temp);
		faces[index].checkProgress();
		disableAll(faces[index]);
		checkChunk(faces[index], new Vector3f(temp.x, temp.y, temp.z));
		index++;
		index %= 6;
	}

	public void disableAll(Chunk chunk) {
		chunk.disableRender();
		chunk.checkProgress();
		if (chunk.hasChildren()) {
			Chunk[] children = chunk.getChildren();
			if (children[0].isOutdated(OUT_TIME) && children[1].isOutdated(OUT_TIME) && children[2].isOutdated(OUT_TIME)
					&& children[3].isOutdated(OUT_TIME)) {
				chunk.deleteChildren();
			} else {
				disableAll(children[0]);
				disableAll(children[1]);
				disableAll(children[2]);
				disableAll(children[3]);
			}
		}
	}

	public boolean checkChunk(Chunk chunk, Vector3f camPos) {
		if (!chunk.isProcessed())
			return true;

		Vector3f cpos = chunk.getPos();
		float dx = cpos.x - camPos.x;
		float dy = cpos.y - camPos.y;
		float dz = cpos.z - camPos.z;

		float distanceSquared = dx * dx + dy * dy + dz * dz;

		if (distanceSquared / (chunk.getWidth() * chunk.getWidth()) < 5f) {
			if (!chunk.hasChildren()) {
				chunk.createChildren();
			}
			Chunk[] children = chunk.getChildren();
			if (checkChunk(children[0], camPos) || checkChunk(children[1], camPos) || checkChunk(children[2], camPos)
					|| checkChunk(children[3], camPos))
				chunk.enableRender();
		} else {
			chunk.enableRender();
		}
		chunk.setTimer();
		return false;
	}

	/* temp */
	public void rotate(Vector3f drot) {
		Vector3f.add(rot, drot, rot);
		updateTransformation();
	}

	public void updateTransformation() {
		transformation = new Matrix4f();
		Matrix4f.rotate((float) Math.toRadians(rot.z), new Vector3f(0, 0, 1), transformation, transformation);
		Matrix4f.rotate((float) Math.toRadians(rot.y), new Vector3f(0, 1, 0), transformation, transformation);
		Matrix4f.rotate((float) Math.toRadians(rot.x), new Vector3f(1, 0, 0), transformation, transformation);
		Matrix4f.translate(pos.negate(null), transformation, transformation);
	}

	public Vector3f getPos() {
		return pos;
	}

	public Vector3f getRot() {
		return rot;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public PlanetData getData() {
		return data;
	}
	
	public Chunk[] getFaces() {
		return faces;
	}

}
