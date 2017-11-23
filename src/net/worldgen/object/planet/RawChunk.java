package net.worldgen.object.planet;

import net.worldgen.object.raw.RawModel;
import net.worldgen.util.vector.Vector3f;

public class RawChunk extends RawModel {

	private Vector3f pos;
	private boolean processed;
	private boolean interrupted;

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void processed() {
		processed = true;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void interrupt() {
		interrupted = true;
	}
	
	public boolean isInterrupted() {
		return interrupted;
	}
	
}
