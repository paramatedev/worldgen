package net.worldgen.object;

import net.worldgen.object.raw.Model;
import net.worldgen.util.vector.Vector3f;

public class Entity {
	protected Model model;
	protected Vector3f position;
	protected Vector3f rotation;
	protected float scale;
//	protected int textureIndex = 0;
	
	protected boolean render;

	protected boolean isWater;
	
	public Entity(Model model, Vector3f position, Vector3f rotation, float scale) {
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.render = true;
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotation.x += dx;
		this.rotation.y += dy;
		this.rotation.z += dz;
	}

	// public float getTextureXOffset() {
	// int column = textureIndex % model.getTexture().getNumberOfRows();
	// return (float) column / (float) model.getTexture().getNumberOfRows();
	// }
	//
	// public float getTextureYOffset() {
	// int row = textureIndex / model.getTexture().getNumberOfRows();
	// return (float) row / (float) model.getTexture().getNumberOfRows();
	// }

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
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
	
	public void setWater() {
		isWater = true;
	}
	
	public boolean isWater() {
		return isWater;
	}
}
