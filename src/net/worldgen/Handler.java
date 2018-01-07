package net.worldgen;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.worldgen.object.Camera;
import net.worldgen.object.DirectionLight;
import net.worldgen.object.Entity;
import net.worldgen.object.Sun;
import net.worldgen.object.planet.Planet;
import net.worldgen.object.planet.PlanetData;
import net.worldgen.sql.Database;
import net.worldgen.util.vector.Vector3f;

public class Handler {

	private ExecutorService executor;
	private Camera camera;
	private Sun sun;
	private List<DirectionLight> dirLights;
	private List<Entity> entities;
	private List<Planet> planets;

	private DataInput input;
	private Database database;
	
	// Models
	// private Model stall;

	public Handler() {
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		camera = new Camera(new Vector3f(0, 160, 230), 35, 0, 0);
		dirLights = new ArrayList<DirectionLight>();
		entities = new ArrayList<Entity>();
		planets = new ArrayList<Planet>();

		loadModels();

		sun = new Sun(null, new Vector3f(1, 1, 1), 1f);
		dirLights.add(sun);
		
		try {
			database = new Database();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		input = new DataInput(database);
		
		int max = 1;
		for (int i = 1; i <= max; i++)
			planets.add(new Planet(executor, camera, 100, new PlanetData(true, 25, i, 12, 1, 0.02f, 0.1f, 100),
					new Vector3f(i * 250 - (max + 1) / 2f * 250, 0, 0), new Vector3f(0, 0, 0)));
		
		// entities.add(new Entity(stall, new Vector3f(20,0,0), new Vector3f(0,180,0),
		// 1));
	}

	private void loadModels() {
		// stall = Loader.loadObjFile("/assets/test/stall.obj",
		// "/assets/test/stallTexture.png");
	}

	// tick
	public void update() {
		input.applyData(planets.get(0).getData());
		sun.update();
		for (Planet planet : planets) {
			// planet.rotate(new Vector3f(10 * Game.TT, 10 * Game.TT,10 * Game.TT));
			planet.update();
		}
	}

	// frame
	public void update(float dt) {
		camera.update(dt);
	}

	public void shutdown() {
		executor.shutdown();
		input.dispose();
		try {
			database.disconnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS))
				executor.shutdownNow();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

	public Sun getSun() {
		return sun;
	}

}
