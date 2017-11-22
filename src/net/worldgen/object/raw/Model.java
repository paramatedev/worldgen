package net.worldgen.object.raw;

public class Model {
	
	private int id;
	private RawModel model;
	private Material mat;

	public Model(RawModel model, Material mat) {
		this.model = model;
		this.mat = mat;
	}
	
	public void assign(int id) {
		this.id  = id;
	}

	public int getID() {
		return id;
	}
	
	public RawModel getRaw() {
		return model;
	}
	
	public Material getMaterial() {
		return mat;
	}
}
