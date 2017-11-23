package net.worldgen;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.worldgen.gui.Element;
import net.worldgen.object.Camera;
import net.worldgen.object.DirectionLight;
import net.worldgen.object.Entity;
import net.worldgen.object.Sun;
import net.worldgen.object.planet.Planet;
import net.worldgen.object.planet.PlanetData;
import net.worldgen.util.vector.Vector3f;

public class Handler {

	// DATA
	public static int QUEUELENGTH;

	private ExecutorService executor;

	private Camera camera;

	// Lights
	private Sun sun;
	private List<DirectionLight> dirLights;
	private List<Entity> entities;
	private List<Element> elements;
	private List<Planet> planets;

	// Models
	// private Model stall;

	public Handler() {
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		// camera = new Camera(new Vector3f(-11.165606f, 47.04815f,
		// 19.088432f),-62.999985f, 29.700012f, 0.0f);
		camera = new Camera(new Vector3f(30, 5, 0), 9, -90, 0);
		dirLights = new ArrayList<DirectionLight>();
		entities = new ArrayList<Entity>();
		elements = new ArrayList<Element>();
		planets = new ArrayList<Planet>();

		loadModels();

		sun = new Sun(null, new Vector3f(1, 1, 1), 1f);
		dirLights.add(sun);

		int max = 1;
		for (int i = 1; i < max + 1; i++)
			planets.add(new Planet(executor, camera, new PlanetData(10, 0.5f, i + 8, 8, 1), new Vector3f(0, 0, 0),
					new Vector3f(0, 0, 0)));

		// entities.add(new Entity(stall, new Vector3f(20,0,0), new Vector3f(0,180,0),
		// 1));
	}

	private void loadModels() {
		// stall = Loader.loadObjFile("/assets/test/stall.obj",
		// "/assets/test/stallTexture.png");
	}

	// tick
	public void update() {
		sun.update();
		for (Planet planet : planets)
			planet.update();
	}

	// frame
	public void update(float dt) {
		camera.update(dt);
	}

	public void shutdown() {
		executor.shutdown();
	}

	public static InputStream getResource(String path) {
		return Handler.class.getResourceAsStream(path);
	}

	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	public void removeEntity(Entity entity) {
		entities.remove(entity);
	}

	public Camera getCamera() {
		return camera;
	}

	public List<Planet> getPlanets() {
		return planets;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public List<Element> getElements() {
		return elements;
	}

	public Sun getSun() {
		return sun;
	}

}
