package net.worldgen;

import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import net.worldgen.render.RenderCore;
import net.worldgen.util.Loader;

public class Game implements Runnable {

	public static float TT; // Ticktime
	
	// core
	private final String VERSION = "0.1.0";
	private Thread core;
	private boolean running;
	private Options options;
	private boolean debug;

	// window
	private long window;
	private GLFWVidMode vidmode;
	private int width, height;
	private int defMonitor;
	private long monitor;
	private boolean fullscreen;
	private boolean vsync;
	private int tps;
	private int fps;
	private boolean fps_unlimited;
	
	private Handler handler;
	private RenderCore renderer;

	public static void main(String[] args) {
		new Game().start();
	}

	public synchronized void start() {
		running = true;
		core = new Thread(this, "Core");
		core.start();
	}

	public synchronized void stop() {
		running = false;
	}

	@Override
	public void run() {
//		try {
			init();
			loop();
//		} finally {
			shutdown();
//		}
	}

	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		// settings
		options = new Options("options.txt");
		debug = options.isDebug();
		width = options.getWidth();
		height = options.getHeight();
		defMonitor = options.getCMonitor();
		fullscreen = options.isFullscreen();
		vsync = options.isVsync();
		tps = options.getTps();
		fps = options.getFps();
		fps_unlimited = options.isFpsUnlimited();
		TT = 1f / tps;

		createWindow();
		Loader.init();
		
		handler = new Handler();
		renderer = new RenderCore(handler, options.getFov(), width, height, options.isRenderModel(), options.isRenderMesh());
	}

	private void createWindow() {

		// init glfw
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// config window
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW_SAMPLES, 16);

		if (fullscreen) {
			PointerBuffer monitors = GLFW.glfwGetMonitors();
			monitor = monitors.get(defMonitor);
		} else
			monitor = NULL;

		// create window
		window = GLFW.glfwCreateWindow(width, height, VERSION, monitor, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// center window
		vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

		// make context current
		GLFW.glfwMakeContextCurrent(window);

		// v-sync
		if (vsync)
			GLFW.glfwSwapInterval(1);
		else
			GLFW.glfwSwapInterval(0);

		// show window
		GLFW.glfwShowWindow(window);

		// init input
		Input.init(window);
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_DISABLED);

		// setup opengl
		GL.createCapabilities();
	}

	private void loop() {

		long past = System.nanoTime();
		int frames = 0;
		int ticks = 0;
		float dtf = 0;
		long timer = System.currentTimeMillis();
		final double timeU = 1000000000 / tps;
		final double timeF = 1000000000 / fps;
		double deltaT = 1;
		double deltaF = 0;

		while (!glfwWindowShouldClose(window) && running) {

			long now = System.nanoTime();
			long timePassed = now - past;
			deltaT += timePassed / timeU;
			if (!fps_unlimited)
				deltaF += timePassed / timeF;
			float dt = timePassed * 0.000000001f;
			dtf += dt;
			past = now;

			while (deltaT >= 1) {
				update();
				ticks++;
				deltaT--;
			}

			if (deltaF >= 1 || fps_unlimited) {
				GLFW.glfwPollEvents();
				render(dtf);
				GLFW.glfwSwapBuffers(window);
				dtf = 0;
				frames++;
				if (!fps_unlimited)
					deltaF--;
			}

			if (System.currentTimeMillis() - timer > 1000) {
				if (debug) {
					System.out.println("TPS: " + ticks + ", FPS: " + frames + ", Queued: " + Handler.QUEUELENGTH);
				}
				frames = 0;
				ticks = 0;
				timer += 1000;
			}
		}
	}

	private void update() {
		if (Input.keyPressed(GLFW_KEY_ESCAPE))
			stop();
		if (Input.keyPressed(GLFW.GLFW_KEY_C))
			System.out.println(handler.getCamera().toString());
		handler.update();
	}

	private void render(float dt) {
		handler.update(dt);
		renderer.render(dt);
	}

	private void shutdown() {
		handler.shutdown();
		renderer.clearMemory();
		Loader.clearMemory();

		Callbacks.glfwFreeCallbacks(window);
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

	public String getVersion() {
		return VERSION;
	}

}