package net.worldgen.object.planet;

import java.util.Random;

import net.worldgen.util.vector.Vector4f;

public class PlanetData {

	private boolean hasWater;
	private float amplitude;
	private float offset;
	private int octaves;
	private float freq;
	private float normalDetail;

	private float waterAmplitude;
	private float waterFreq;

	private Vector4f color1;
	private Vector4f color2;
	private Vector4f color3;
	private Vector4f color4;
	private Vector4f colorWater;
	
	public PlanetData() {
	}

	public PlanetData(boolean hasWater, float amplitude, int seed, int octaves, float freq, float normalDetail,
			float waterAmplitude, float waterFreq) {
		this.hasWater = hasWater;
		this.amplitude = amplitude;
		this.offset = new Random(seed).nextFloat() * Short.MAX_VALUE;
		this.octaves = octaves;
		this.freq = freq;
		this.normalDetail = normalDetail;
		this.waterAmplitude = waterAmplitude;
		this.waterFreq = waterFreq;
		color1 = new Vector4f(225 / 255f, 184 / 255f, 133 / 255f, 1);
		color2 = new Vector4f(151 / 255f, 210 / 255f, 32 / 255f, 1);
		color3 = new Vector4f(155 / 255f, 156 / 255f, 158 / 255f, 1);
		color4 = new Vector4f(219 / 255f, 220 / 255f, 225 / 255f, 1);
		colorWater = new Vector4f(12 / 255f, 148 / 255f, 180 / 255f, 0.5f);
	}

	public boolean hasWater() {
		return hasWater;
	}

	public void setHasWater(boolean hasWater) {
		this.hasWater = hasWater;
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

	public void setNormalDetail(float normalDetail) {
		this.normalDetail = normalDetail;
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

	public float getNormalDetail() {
		return normalDetail;
	}

	public float getWaterAmplitude() {
		return waterAmplitude;
	}

	public void setWaterAmplitude(float waterAmplitude) {
		this.waterAmplitude = waterAmplitude;
	}

	public float getWaterFreq() {
		return waterFreq;
	}

	public void setWaterFreq(float waterFreq) {
		this.waterFreq = waterFreq;
	}

	public Vector4f getColor1() {
		return color1;
	}

	public void setColor1(Vector4f color1) {
		this.color1 = color1;
	}

	public Vector4f getColor2() {
		return color2;
	}

	public void setColor2(Vector4f color2) {
		this.color2 = color2;
	}

	public Vector4f getColor3() {
		return color3;
	}

	public void setColor3(Vector4f color3) {
		this.color3 = color3;
	}

	public Vector4f getColor4() {
		return color4;
	}

	public void setColor4(Vector4f color4) {
		this.color4 = color4;
	}

	public Vector4f getColorWater() {
		return colorWater;
	}

	public void setColorWater(Vector4f colorWater) {
		this.colorWater = colorWater;
	}

}
