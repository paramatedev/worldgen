package net.worldgen.object.planet;

import java.util.Random;

public class PlanetData {

	private float amplitude;
	private float offset;
	private int octaves;
	private float freq;

	public PlanetData() {}
	
	public PlanetData(float amplitude, int seed, int octaves, float freq) {
		this.amplitude = amplitude;
		this.offset = new Random(seed).nextFloat() * Short.MAX_VALUE;
		this.octaves = octaves;
		this.freq = freq;
	}

	public void setAmplitude(float amplitude) {
		this.amplitude = amplitude;
	}

	public void setOffset(float offset) {
		this.offset = offset;
	}

	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}

	public void setFreq(float freq) {
		this.freq = freq;
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
