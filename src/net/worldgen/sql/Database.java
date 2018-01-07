package net.worldgen.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.worldgen.object.planet.PlanetData;
import net.worldgen.util.vector.Vector4f;

public class Database {

	private Connection con;
	private Statement query;

	public Database() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		boolean create = false;
		File file = new File("planets.db");
		if (!file.exists())
			create = true;
		con = DriverManager.getConnection("jdbc:sqlite:planets.db");
		query = con.createStatement();
		if (create)
			create();
	}

	public void disconnect() throws SQLException {
		con.close();
	}

	public void create() throws SQLException {
		query.execute("CREATE TABLE planets(id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
				+ "name TEXT NOT NULL,"
				+ "hasWater INTEGER NOT NULL,"
				+ "amplitude REAL NOT NULL,"
				+ "offset REAL NOT NULL,"
				+ "octaves INTEGER NOT NULL,"
				+ "freq REAL NOT NULL,"
				+ "normalDetail REAL NOT NULL,"
				+ "waterAmplitude REAL NOT NULL,"
				+ "waterFreq REAL NOT NULL,"
				+ "color1 TEXT NOT NULL,"
				+ "color2 TEXT NOT NULL,"
				+ "color3 TEXT NOT NULL,"
				+ "color4 TEXT NOT NULL,"
				+ "colorWater TEXT NOT NULL"
				+ ");");
	}

	public boolean addPlanet(String name, PlanetData data) throws SQLException {
		query.execute("SELECT id FROM planets WHERE name = '" + name + "';");
		ResultSet rs = query.getResultSet();
		if(rs.isBeforeFirst())
			return false;
		query.execute("INSERT INTO planets(name, hasWater, amplitude, offset, octaves, freq, normalDetail,"
				+ "waterAmplitude, waterFreq, color1, color2, color3, color4, colorWater) VALUES ('"
				+ name + "',"
				+ bool(data.hasWater()) + ","
				+ data.getAmplitude() + ","
				+ data.getOffset() + ","
				+ data.getOctaves() + ","
				+ data.getFreq() + ","
				+ data.getNormalDetail() + ","
				+ data.getWaterAmplitude() + ","
				+ data.getWaterFreq() + ",'"
				+ color(data.getColor1()) + "','"
				+ color(data.getColor2()) + "','"
				+ color(data.getColor3()) + "','"
				+ color(data.getColor4()) + "','"
				+ color(data.getColorWater())
				+ "');");
		return true;
	}
	
	public void deletePlanet(String name) throws SQLException {
		query.execute("DELETE FROM planets WHERE name = '" + name + "';");
	}

	public PlanetData getPlanet(String name) throws SQLException {
		query.execute("SELECT * FROM planets WHERE name = '" + name + "';");
		ResultSet rs = query.getResultSet();
		PlanetData data = new PlanetData(bool(rs.getInt(3)),
				rs.getFloat(4),
				rs.getFloat(5),
				rs.getInt(6),
				rs.getFloat(7),
				rs.getFloat(8),
				rs.getFloat(9),
				rs.getFloat(10));
		data.setColor1(color(rs.getString(11)));
		data.setColor2(color(rs.getString(12)));
		data.setColor3(color(rs.getString(13)));
		data.setColor4(color(rs.getString(14)));
		data.setColorWater(color(rs.getString(15)));
		return data;
	}
	
	public String[] getAllPlanetNames() throws SQLException {
		query.execute("SELECT COUNT(name) FROM planets;");
		ResultSet rs = query.getResultSet();
		int count = rs.getInt(1);
		String[] names = new String[count];
		query.execute("SELECT name FROM planets ORDER BY name ASC;");
		rs = query.getResultSet();
		for(int i = 0; i < count; i++) {
			rs.next();
			names[i] = rs.getString(1);
		}
		return names;
	}

	public int bool(boolean value) {
		return (value) ? 1 : 0;
	}

	public boolean bool(int value) {
		return (value == 1) ? true : false;
	}
	
	public String color(Vector4f color) {
		return color.x + "/" + color.y + "/" + color.z + "/" + color.w;
	}
	
	public Vector4f color(String color) {
		String[] rgba = color.split("/");
		return new Vector4f(Float.parseFloat(rgba[0]),
				Float.parseFloat(rgba[1]),
				Float.parseFloat(rgba[2]),
				Float.parseFloat(rgba[3]));
	}

}
