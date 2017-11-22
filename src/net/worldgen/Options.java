package net.worldgen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Options {

	private File file;
	private PrintWriter writer;
	private BufferedReader reader;

	private final boolean DEF_DEBUG = false;
	private boolean debug;
	private final int DEF_WIDTH = 1920;
	private int width;
	private final int DEF_HEIGHT = 1080;
	private int height;
	private final int DEF_CMONITOR = 0;
	private int cmonitor;
	private final boolean DEF_FULLSCREEN = true;
	private boolean fullscreen;
	private final boolean DEF_VSYNC = false;
	private boolean vsync;
	private final float DEF_FOV = 75;
	private float fov;
	private final int DEF_TPS = 60;
	private int tps;
	private final int DEF_FPS = 60;
	private int fps;
	private final boolean DEF_UNLFPS = true;
	private boolean unlfps;
	private final boolean DEF_RENDERMODEL = true;
	private boolean rendermodel;
	private final boolean DEF_RENDERMESH = false;
	private boolean rendermesh;
	
	public Options(String path) {
		file = new File(path);
		read();
	}

	public void write() {
		try {
			writer = new PrintWriter(file, "UTF-8");
			writer.println("debug: " + debug);
			writer.println("width: " + width);
			writer.println("height: " + height);
			writer.println("current_monitor: " + cmonitor);
			writer.println("fullscreen: " + fullscreen);
			writer.println("vsync: " + vsync);
			writer.println("fov: " + fov);
			writer.println("tps: " + tps);
			writer.println("fps: " + fps);
			writer.println("unlimited_fps: " + unlfps);
			writer.println("render_model: " + rendermodel);
			writer.println("render_mesh: " + rendermesh);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	public void read() {
		if (!file.exists()) {
			restoreDefaults();
			return;
		}
		try {
			reader = new BufferedReader(new FileReader(file));
			if (reader.ready()) {
				debug = Boolean.parseBoolean(reader.readLine().split(": ")[1]);
				width = Integer.parseInt(reader.readLine().split(": ")[1]);
				height = Integer.parseInt(reader.readLine().split(": ")[1]);
				cmonitor = Integer.parseInt(reader.readLine().split(": ")[1]);
				fullscreen = Boolean.parseBoolean(reader.readLine().split(": ")[1]);
				vsync = Boolean.parseBoolean(reader.readLine().split(": ")[1]);
				fov = Float.parseFloat(reader.readLine().split(": ")[1]);
				tps = Integer.parseInt(reader.readLine().split(": ")[1]);
				fps = Integer.parseInt(reader.readLine().split(": ")[1]);
				unlfps = Boolean.parseBoolean(reader.readLine().split(": ")[1]);
				rendermodel = Boolean.parseBoolean(reader.readLine().split(": ")[1]);
				rendermesh = Boolean.parseBoolean(reader.readLine().split(": ")[1]);
			}
		} catch (Exception e) {
			restoreDefaults();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void restoreDefaults() {
		debug = DEF_DEBUG;
		width = DEF_WIDTH;
		height = DEF_HEIGHT;
		cmonitor = DEF_CMONITOR;
		fullscreen = DEF_FULLSCREEN;
		vsync = DEF_VSYNC;
		fov = DEF_FOV;
		tps = DEF_TPS;
		fps = DEF_FPS;
		unlfps = DEF_UNLFPS;
		rendermodel = DEF_RENDERMODEL;
		rendermesh = DEF_RENDERMESH;
		write();
	}

	public File getFile() {
		return file;
	}

	public boolean isDebug() {
		return debug;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getCMonitor() {
		return cmonitor;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public boolean isVsync() {
		return vsync;
	}
	
	public float getFov() {
		return fov;
	}

	public int getTps() {
		return tps;
	}

	public int getFps() {
		return fps;
	}

	public boolean isFpsUnlimited() {
		return unlfps;
	}
	
	public boolean isRenderModel() {
		return rendermodel;
	}
	
	public boolean isRenderMesh() {
		return rendermesh;
	}
}
