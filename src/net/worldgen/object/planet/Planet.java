package net.worldgen.object.planet;

import net.worldgen.object.Entity;
import net.worldgen.util.vector.Vector3f;

public class Planet {

	private Entity surface;
	private Entity ocean;

	public Planet(float radius, long seed, int octaves, float freq, float amplitude, int n, int on, String surfacePath, String oceanPath, Vector3f position, Vector3f rotation, float scale) {
		GenPlanet gp = new GenPlanet(radius, seed, octaves, freq, amplitude, n, on, surfacePath, oceanPath);
		surface = new Entity(gp.getSurfaceModel(), position, rotation, scale);
		ocean = new Entity(gp.getOceanModel(), position, rotation, scale);
		ocean.setWater();
	}

	public Entity getSurface() {
		return surface;
	}
	
	public Entity getOcean() {
		return ocean;
	}

}
