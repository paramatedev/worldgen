package net.worldgen.render.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.worldgen.Handler;
import net.worldgen.util.vector.Matrix4f;
import net.worldgen.util.vector.Vector2f;
import net.worldgen.util.vector.Vector3f;
import net.worldgen.util.vector.Vector4f;

public abstract class ShaderProgram {

	private int program;
	private int vertexShader;
	private int fragmentShader;

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	///// SHADER /////

	public ShaderProgram(String vertexPath, String fragmentPath) {
		vertexShader = loadShader(vertexPath, GL20.GL_VERTEX_SHADER);
		fragmentShader = loadShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);
		program = GL20.glCreateProgram();
		GL20.glAttachShader(program, vertexShader);
		GL20.glAttachShader(program, fragmentShader);
		bindAttributes();
		GL20.glLinkProgram(program);
		GL20.glValidateProgram(program);
		getUniformLocations();
	}

	public void start() {
		GL20.glUseProgram(program);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	private int loadShader(String path, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(Handler.getResource(path)));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read shader!");
			e.printStackTrace();
			System.exit(-1);
		}
		int shader = GL20.glCreateShader(type);
		GL20.glShaderSource(shader, shaderSource);
		GL20.glCompileShader(shader);
		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shader, 500));
			System.err.println("Could not compile shader!");
			System.exit(-1);
		}
		return shader;
	}

	protected abstract void bindAttributes();

	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(program, attribute, variableName);
	}

	protected abstract void getUniformLocations();

	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(program, uniformName);
	}

	///// LOAD /////

	protected void loadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}

	protected void loadVector(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.x, vector.y);
	}

	protected void loadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}

	protected void loadVector(int location, Vector4f vector) {
		GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}

	///// MEMORY /////

	public void clearMemory() {
		stop();
		GL20.glDetachShader(program, vertexShader);
		GL20.glDetachShader(program, fragmentShader);
		GL20.glDeleteShader(vertexShader);
		GL20.glDeleteShader(fragmentShader);
		GL20.glDeleteProgram(program);
	}
}
