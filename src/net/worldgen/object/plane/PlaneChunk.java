package net.worldgen.object.plane;

import java.util.List;

import net.worldgen.Handler;
import net.worldgen.object.Entity;
import net.worldgen.object.raw.Material;
import net.worldgen.object.raw.Model;
import net.worldgen.util.vector.Vector3f;

public class PlaneChunk extends Entity {
	
	private int lod;
	private float width;
	private Handler handler;
	private List<Model> models;
	
	private PlaneChunk parent;
	private PlaneChunk[] children;
	
	private long timer;
	
	public PlaneChunk(PlaneChunk parent, int lod, float width, Handler handler, List<Model> models, Model model, Vector3f position, Vector3f rotation, float scale) {
		super(model, position, rotation, scale);
		this.lod = lod;
		this.width = width;
		this.handler = handler;
		this.models = models;
	}
	
	public void createChildren() {
		children = new PlaneChunk[4];
		int newLod = lod+1;
		float newWidth = width/2;
		
		if(!(models.size() > newLod))
			models.add(new Model(GenPlaneChunk.genRawChunk(newWidth), new Material("res/assets/ground.png")));
		
		children[0] = new PlaneChunk(this, newLod, newWidth, handler, models, models.get(newLod), new Vector3f(position.x,0,position.z), new Vector3f(0,0,0), 1);
		children[1] = new PlaneChunk(this, newLod, newWidth, handler, models, models.get(newLod), new Vector3f(position.x+newWidth,0,position.z), new Vector3f(0,0,0), 1);
		children[2] = new PlaneChunk(this, newLod, newWidth, handler, models, models.get(newLod), new Vector3f(position.x,0,position.z+newWidth), new Vector3f(0,0,0), 1);
		children[3] = new PlaneChunk(this, newLod, newWidth, handler, models, models.get(newLod), new Vector3f(position.x+newWidth,0,position.z+newWidth), new Vector3f(0,0,0), 1);
		
		handler.addEntity(children[0]);
		handler.addEntity(children[1]);
		handler.addEntity(children[2]);
		handler.addEntity(children[3]);
	}
	
	public void deleteChildren() {
		if(!this.hasChildren())
			return;
		handler.getEntities().remove(children[0]);
		handler.getEntities().remove(children[1]);
		handler.getEntities().remove(children[2]);
		handler.getEntities().remove(children[3]);
		children = null;
	}
	
	public int getLod() {
		return lod;
	}
	
	public float getWidth() {
		return width;
	}
	
	public PlaneChunk getParent() {
		return parent;
	}
	
	public boolean hasChildren() {
		return children != null;
	}
	
	public PlaneChunk[] getChildren() {
		return children;
	}

	public void setTimer() {
		timer = System.currentTimeMillis();
	}
	
	public boolean isOutdated(int millis) {
		return (timer + millis) < System.currentTimeMillis();
	}
	
}
