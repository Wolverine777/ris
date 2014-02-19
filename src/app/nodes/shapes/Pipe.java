package app.nodes.shapes;

import static app.nodes.shapes.Vertex.*;
import static app.vecmathimp.FactoryDefault.vecmath;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import app.shader.Shader;
import app.vecmath.Vector;

/**
 * Some Pipe
 * 
 * @author Constantin
 * 
 */
public class Pipe extends Shape {

	public float r;
	public int lats;
	public int longs;

	private List<Vertex> vL = new ArrayList<Vertex>();

	public Pipe(String id, Shader shader, float mass) {
		this(id, shader, 1.5f, 10, 10, mass);
	}

	public Pipe(String id, Shader shader, float r, int lats, int longs, float mass) {
		super(id, shader, mass);
		this.r = r;
		this.lats = lats;
		this.longs = longs;

		mode = GL11.GL_QUAD_STRIP;

		setup();

	}

	private void setup() {

		Vector vec;
		float lat0 = (float) (Math.PI * -1);
		float z0 = (float) Math.sin(lat0);

		float lat1 = (float) (Math.PI * -0.5);
		float z1 = (float) Math.sin(lat1);

		for (int j = 0; j <= longs; j++) {
			float lng = (float) (2 * Math.PI * (j - 1) / longs);
			float x = (float) Math.cos(lng);
			float y = (float) Math.sin(lng);

			vec = vec(x, y, z0);
			vec.normalize();
			vL.add(new Vertex(vec, col((float) Math.random(),
					(float) Math.random(), (float) Math.random())));

			vec = vec(x, y, z1);
			vec.normalize();
			vL.add(v(vec, col(.4f, .8f, .3f)));
		}

		vertices = vL.toArray(new Vertex[vL.size()]);

		positionData = BufferUtils.createFloatBuffer(vertices.length
				* vecmath.vectorSize());
		colorData = BufferUtils.createFloatBuffer(vertices.length
				* vecmath.vectorSize());

		for (Vertex v : vertices) {
			positionData.put(v.position.asArray());
			colorData.put(v.color.asArray());
		}

		positionData.rewind();
		colorData.rewind();
	}

}