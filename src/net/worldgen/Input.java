package net.worldgen;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

public class Input {

	private static boolean[] keyDown;
	private static boolean[] keyPressed;
	private static boolean[] buttonDown;
	private static boolean[] buttonPressed;
	private static double mouseX, mouseY;
	private static double mouseDX, mouseDY;
	private static double wheel;

	private static GLFWKeyCallback keyCallback;
	private static GLFWMouseButtonCallback mouseButtonCallback;
	private static GLFWCursorPosCallback cursorPosCallback;
	private static GLFWScrollCallback scrollCallback;

	public static void init(long window) {

		keyDown = new boolean[349];
		keyPressed = new boolean[349];
		buttonDown = new boolean[8];
		buttonPressed = new boolean[8];

		keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				Input.keyDown[key] = action != GLFW.GLFW_RELEASE;
				if (action != GLFW.GLFW_RELEASE)
					Input.keyPressed[key] = true;
			}
		};

		mouseButtonCallback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				Input.buttonDown[button] = action != GLFW.GLFW_RELEASE;
				if (action != GLFW.GLFW_RELEASE)
					Input.buttonPressed[button] = true;
			}
		};

		cursorPosCallback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				if (mouseX != 0 && mouseY != 0) {
					Input.mouseDX += xpos - mouseX;
					Input.mouseDY += ypos - mouseY;
				}
				Input.mouseX = xpos;
				Input.mouseY = ypos;
			}
		};

		scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				Input.wheel = yoffset;
			}
		};

		GLFW.glfwSetKeyCallback(window, keyCallback);
		GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback);
		GLFW.glfwSetCursorPosCallback(window, cursorPosCallback);
		GLFW.glfwSetScrollCallback(window, scrollCallback);

	}

	public static boolean isKeyDown(int key) {
		return keyDown[key];
	}

	public static boolean keyPressed(int key) {
		boolean kp = keyPressed[key];
		keyPressed[key] = false;
		return kp;
	}

	public static boolean isButtonDown(int button) {
		return buttonDown[button];
	}

	public static boolean buttonPressed(int button) {
		boolean bp = buttonPressed[button];
		buttonPressed[button] = false;
		return bp;
	}

	public static double getX() {
		return mouseX;
	}

	public static double getY() {
		return mouseY;
	}

	public static double getDX() {
		double dx = mouseDX;
		mouseDX = 0;
		return dx;
	}

	public static double getDY() {
		double dy = mouseDY;
		mouseDY = 0;
		return dy;
	}

	public static double getDWheel() {
		double w = wheel;
		wheel = 0;
		return w;
	}

}
