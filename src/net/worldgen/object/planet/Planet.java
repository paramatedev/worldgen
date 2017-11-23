package net.worldgen.object.planet;

import java.util.concurrent.ExecutorService;

import net.worldgen.object.Camera;
import net.worldgen.util.Maths;
import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector3f;
import net.worldgen.util.vector.Vector4f;

public class Planet {

	private Vector3f pos;
	private Vector3f rot;
	private Camera camera;
	private Chunk[] faces;
	private Matrix4f transformation;
	
	public Planet(ExecutorService executor, Camera camera, PlanetData data, Vector3f pos, Vector3f rot) {
		this.camera = camera;
		this.pos = pos;
		this.rot = rot;
		
		transformation = Maths.createTransformationMatrix(pos, rot, 1);
		
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
		faces[0] = new Chunk(0, 0, 0, null, m1, executor, data);
		faces[1] = new Chunk(0, 0, 0, null, m2, executor, data);
		faces[2] = new Chunk(0, 0, 0, null, m3, executor, data);
		faces[3] = new Chunk(0, 0, 0, null, m4, executor, data);
		faces[4] = new Chunk(0, 0, 0, null, m5, executor, data);
		faces[5] = new Chunk(0, 0, 0, null, m6, executor, data);
	}
	
	public void update() {
		Vector4f temp = new Vector4f(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z, 1);
		Matrix4f.transform(transformation, temp, temp);
		Vector3f camPos = new Vector3f(temp.x, temp.y, temp.z);
		for(Chunk face : faces)
			checkChunk(face, camPos);
	}

	public void checkChunk(Chunk parent, Vector3f camPos) {
		
	}
	
}
