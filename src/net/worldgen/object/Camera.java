package net.worldgen.object;

import org.lwjgl.glfw.GLFW;

import net.worldgen.Input;
import net.worldgen.util.vector.Vector3f;

public class Camera {

	private final float MAX_SPEED = 100;
//	private final float distance = 12;

	private Vector3f position;
	private float pitch;
	private float yaw;
	private float roll;
	
	private float speed;

	public Camera(Vector3f position, float pitch, float yaw, float roll) {
		this.position = position;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	public void update(float dt) {
//		if (Input.isKeyDown(GLFW.GLFW_KEY_W) || Input.isKeyDown(GLFW.GLFW_KEY_UP)) {
//			pitch += moveSpeed * dt;
//		}
//		if (Input.isKeyDown(GLFW.GLFW_KEY_S) || Input.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
//			pitch -= moveSpeed * dt;
//		}
//		if (Input.isKeyDown(GLFW.GLFW_KEY_A) || Input.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
//			yaw += moveSpeed * dt;
//		}
//		if (Input.isKeyDown(GLFW.GLFW_KEY_D) || Input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
//			yaw -= moveSpeed * dt;
//		}
//
//		position.x = (float) Math.sin(Math.toRadians(-yaw)) * distance;
//		position.y = 0;
//		position.z = (float) Math.cos(Math.toRadians(-yaw)) * distance;
//
//		position.normalise();
//		float cosh = (float) Math.cos(Math.toRadians(pitch)) * distance;
//
//		position.x *= cosh;
//		position.y = (float) Math.sin(Math.toRadians(pitch)) * distance;
//		position.z *= cosh;
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_X))
			speed = MAX_SPEED / 10;
		else
			speed = MAX_SPEED;
		
		if (Input.isKeyDown(GLFW.GLFW_KEY_W) || Input.isKeyDown(GLFW.GLFW_KEY_UP)) {
			position.x -= (float) Math.sin(Math.toRadians(-yaw)) * speed * dt;
			position.z -= (float) Math.cos(Math.toRadians(-yaw)) * speed * dt;
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_S) || Input.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
			position.x += (float) Math.sin(Math.toRadians(-yaw)) * speed * dt;
			position.z += (float) Math.cos(Math.toRadians(-yaw)) * speed * dt;
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_A) || Input.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
			position.x -= (float) Math.cos(Math.toRadians(-yaw)) * speed * dt;
			position.z += (float) Math.sin(Math.toRadians(-yaw)) * speed * dt;
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_D) || Input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
			position.x += (float) Math.cos(Math.toRadians(-yaw)) * speed * dt;
			position.z -= (float) Math.sin(Math.toRadians(-yaw)) * speed * dt;
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			position.y += speed * dt;
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			position.y -= speed * dt;
		}
		
		pitch += Input.getDY() * 0.2f;
		yaw += Input.getDX() * 0.2f;
		
		
		if(pitch > 90)
			pitch = 90;
		if(pitch < -90)
			pitch = -90;
	}

	public String toString() {
		String string = "Camera - ";
		string += "pos: " + position.toString();
		string += ", rot: " + new Vector3f(pitch, yaw, roll).toString();
		return string;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
}
