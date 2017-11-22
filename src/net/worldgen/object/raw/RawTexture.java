package net.worldgen.object.raw;

import java.nio.ByteBuffer;

public class RawTexture {

	private int width;
	private int height;
	private ByteBuffer buffer;

	public RawTexture(ByteBuffer buffer, int width, int height) {
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
