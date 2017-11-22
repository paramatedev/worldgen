package net.worldgen.object;

import net.worldgen.util.vector.Vector3f;

public class DirectionLight {

	protected Vector3f dir;
	protected Vector3f color;
	protected float intensity;

	public DirectionLight(Vector3f dir, Vector3f color, float intensity) {
		this.dir = dir;
		this.color = color;
		this.intensity = intensity;
	}

	public Vector3f getDirection() {
		return dir;
	}

	public Vector3f getColor() {
		return color;
	}

	public float getIntensity() {
		return intensity;
	}

}
