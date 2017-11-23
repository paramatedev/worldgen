package net.worldgen.object.planet;

import java.util.concurrent.ExecutorService;

import net.worldgen.object.raw.RawModel;
import net.worldgen.util.Loader;
import net.worldgen.util.vector.Matrix4f;

public class Chunk {

	private int lod;
	private float width;
	private float x, y;
	private RawChunk raw;
	private Chunk parent;
	private Matrix4f rot;
	private ExecutorService executor;
	private PlanetData data;
	private Chunk[] children;
	
	private long timer;

	public Chunk(int lod, float x, float y, Chunk parent, Matrix4f rot, ExecutorService executor, PlanetData data) {
		this.lod = lod;
		this.x = x;
		this.y = y;
		this.parent = parent;
		this.rot = rot;
		this.executor = executor;
		this.data = data;
		width = 2 / (float) Math.pow(2, lod);
		raw = new RawChunk();
		executor.submit(new GenChunk(x, y, width, raw, rot, data));
	}

	public void createChildren() {
		float wdiv2 = width / 2;
		children = new Chunk[4];
		children[0] = new Chunk(lod + 1, x, y, this, rot, executor, data);
		children[1] = new Chunk(lod + 1, x + wdiv2, y, this, rot, executor, data);
		children[2] = new Chunk(lod + 1, x, y + wdiv2, this, rot, executor, data);
		children[3] = new Chunk(lod + 1, x + wdiv2, y + wdiv2, this, rot, executor, data);
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
		if(!raw.isProcessed())
			raw.interrupt();
		Loader.deleteVao(raw.getVao());
	}

	public int getLod() {
		return lod;
	}

	public float getWidth() {
		return width;
	}

	public RawModel getRawChunk() {
		return raw;
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

	public void setTimer() {
		timer = System.currentTimeMillis();
	}

	public boolean isOutdated(int millis) {
		return (timer + millis) < System.currentTimeMillis();
	}
}
