package net.worldgen.object.raw;

import net.worldgen.util.Loader;

public class Material {
	
	private int texID;
//	private float reflectivity;
//	private boolean culling;
	
	public Material(String path) {
		this.texID = Loader.createTexture(path);
	}
	
	public int getID() {
		return texID;
	}
	
}
