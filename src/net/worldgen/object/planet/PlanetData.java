package net.worldgen.object.planet;

import java.util.Random;

public class PlanetData {

	private float radius;
	private float amplitude;

	// noise
	private float offset;
	private int octaves;
	private float freq;

	public PlanetData(float radius, float amplitude, int seed, int octaves, float freq) {
		super();
		this.radius = radius;
		this.amplitude = amplitude;
		this.offset = new Random(seed).nextFloat() * Short.MAX_VALUE;
		this.octaves = octaves;
		this.freq = freq;
	}

	public float getRadius() {
		return radius;
	}

	public float getAmplitude() {
		return amplitude;
	}

	public float getOffset() {
		return offset;
	}

	public int getOctaves() {
		return octaves;
	}

	public float getFreq() {
		return freq;
	}

}
