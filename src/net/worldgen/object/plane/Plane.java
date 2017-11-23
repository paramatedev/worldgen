package net.worldgen.object.plane;

import java.util.ArrayList;
import java.util.List;

import net.worldgen.Handler;
import net.worldgen.object.Camera;
import net.worldgen.object.raw.Material;
import net.worldgen.object.raw.Model;
import net.worldgen.util.vector.Vector3f;

public class Plane {

	private final int outTime = 5000; // ms

	private Camera camera;
	private PlaneChunk chunkTree;
	private List<Model> models;

	public Plane(Handler handler, Camera camera, float width) {
		this.camera = camera;
		models = new ArrayList<Model>();
		models.add(new Model(GenPlaneChunk.genRawChunk(width), new Material("res/assets/ground.png")));
		chunkTree = new PlaneChunk(null, 0, width, handler, models, models.get(0), new Vector3f(-width / 2, 0, -width / 2),
				new Vector3f(0, 0, 0), 1);
		handler.addEntity(chunkTree);
	}

	public void update() {
		disableAll(chunkTree);
		checkChunk(chunkTree);
	}

	public void disableAll(PlaneChunk chunk) {
		chunk.disableRender();
		if (chunk.hasChildren()) {
			PlaneChunk[] children = chunk.getChildren();
			if (children[0].isOutdated(outTime) && children[1].isOutdated(outTime) && children[2].isOutdated(outTime)
					&& children[3].isOutdated(outTime))
				chunk.deleteChildren();
			disableAll(children[0]);
			disableAll(children[1]);
			disableAll(children[2]);
			disableAll(children[3]);
		}
	}

	public void checkChunk(PlaneChunk chunk) {

		float dx = chunk.getPosition().x - camera.getPosition().x + chunk.getWidth() / 2;
		float dy = chunk.getPosition().y - camera.getPosition().y;
		float dz = chunk.getPosition().z - camera.getPosition().z + chunk.getWidth() / 2;

		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

		if (distance / chunk.getWidth() < 1.5) {
			if (!chunk.hasChildren())
				chunk.createChildren();
			PlaneChunk[] children = chunk.getChildren();
			checkChunk(children[0]);
			checkChunk(children[1]);
			checkChunk(children[2]);
			checkChunk(children[3]);
		} else {
			chunk.enableRender();
		}
		chunk.setTimer();
	}

}
