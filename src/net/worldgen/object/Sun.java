package net.worldgen.object;

import net.worldgen.Game;
import net.worldgen.util.vector.Vector3f;

public class Sun extends DirectionLight {

	public static final float ROTATION_SPEED = 100;
	private float rotation;

	public Sun(Vector3f dir, Vector3f color, float intensity) {
		super(dir, color, intensity);
	}

	public void update() {
		rotation += ROTATION_SPEED * Game.TT;
		dir = new Vector3f((float) -Math.cos(Math.toRadians(rotation)), -0.1f, (float) Math.sin(Math.toRadians(rotation)));
		dir.normalise();
	}

}
