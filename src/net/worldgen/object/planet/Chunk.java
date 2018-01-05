package net.worldgen.object.planet;

import java.util.concurrent.ExecutorService;

import net.worldgen.object.raw.RawModel;
import net.worldgen.util.Loader;
import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector3f;

public class Chunk {

	private int lod;
	private float width;
	private float x, y;
	private Chunk parent;
	private Matrix4f rot;
	private ExecutorService executor;
	private float radius;
	private Chunk[] children;

	private RawModel rawSurface;
	private ModelDataBuffer surfaceBuffer;
	private RawModel rawWater;
	private ModelDataBuffer waterBuffer;
	private Vector3f pos;

	private boolean generated;
	private boolean processed;
	private boolean interrupted;
	private boolean render;
	private long timer;

	public Chunk(int lod, float x, float y, Chunk parent, Matrix4f rot, ExecutorService executor, float radius) {
		this.lod = lod;
		this.x = x;
		this.y = y;
		this.parent = parent;
		this.rot = rot;
		this.executor = executor;
		this.radius = radius;
		render = false;
		width = 2 / (float) Math.pow(2, lod);
		executor.execute(new GenChunk(x, y, width, this, rot, radius));
		setTimer();
	}

	public void createChildren() {
		float wdiv2 = width / 2;
		children = new Chunk[4];
		children[0] = new Chunk(lod + 1, x, y, this, rot, executor, radius);
		children[1] = new Chunk(lod + 1, x + wdiv2, y, this, rot, executor, radius);
		children[2] = new Chunk(lod + 1, x, y + wdiv2, this, rot, executor, radius);
		children[3] = new Chunk(lod + 1, x + wdiv2, y + wdiv2, this, rot, executor, radius);
	}

	public void deleteChildren() {
		if (!this.hasChildren())
			return;
		children[0].delete();
		children[1].delete();
		children[2].delete();
		children[3].delete();
		children = null;
	}

	public void delete() {
		if (processed) {
			Loader.deleteVao(rawSurface.getVao());
			Loader.deleteVao(rawWater.getVao());
		} else
			interrupted = true;
	}

	public void checkProgress() {
		if (generated && !processed && !interrupted) {
			rawSurface = Loader.loadModel(surfaceBuffer.getIndices(), surfaceBuffer.getVertices());
			surfaceBuffer = null;
			rawWater = Loader.loadModel(waterBuffer.getIndices(), waterBuffer.getVertices(), waterBuffer.getNormals(),
					waterBuffer.getTexCoords());
			waterBuffer = null;
			processed = true;
		}
	}

	public int getLod() {
		return lod;
	}

	public float getWidth() {
		return width * radius;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setSurfaceBuffer(ModelDataBuffer buffer) {
		this.surfaceBuffer = buffer;
	}

	public void setWaterBuffer(ModelDataBuffer buffer) {
		this.waterBuffer = buffer;
	}

	public RawModel getRawSurface() {
		return rawSurface;
	}

	public RawModel getRawWater() {
		return rawWater;
	}

	public Chunk getParent() {
		return parent;
	}

	public boolean hasChildren() {
		return children != null;
	}

	public Chunk[] getChildren() {
		return children;
	}

	public void setGenerated() {
		generated = true;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void enableRender() {
		render = true;
	}

	public void disableRender() {
		render = false;
	}

	public boolean shouldRender() {
		return render;
	}

	public void setTimer() {
		timer = System.currentTimeMillis();
	}

	public boolean isOutdated(int millis) {
		return (timer + millis) < System.currentTimeMillis();
	}
}
