package app.nodes.shapes;

import java.io.File;
import app.nodes.shapes.Texture;
import org.lwjgl.BufferUtils;
import app.shader.Shader;
import app.vecmath.Color;
import app.vecmath.Vector;
import static app.nodes.shapes.Vertex.*;
import static app.vecmathimp.FactoryDefault.vecmath;

public class Plane extends Shape {

	// Width and depth of the plane divided by 2.
	public float w2;
	public float d2;

	public Plane(String id, Shader shader) {
		this(id, shader, 10f, 10f);
	}

	public Plane(String id, Shader shader, float w, float d) {
		this(id, shader, w, d, null);
	}

	public Plane(String id, Shader shader, float w, float d, String sourceTex) {
		super(id, shader);
		w2 = w / 2;
		d2 = d / 2;

		if (sourceTex != null) {
			tex = new Texture(new File(sourceTex));
		} else {
			tex = null;
		}

		// TODO normals make no sense at all!
		setup();
	}

	private void setup() {

		Color[] c = { col(.4f, .7f, .8f), col(1, 0, 0), col(1, 1, 0),
				col(0, 1, 0) };

		Vector[] p = { vec(-w2, 0, -d2), vec(w2, 0, -d2), vec(w2, 0, d2),
				vec(-w2, 0, d2) };

		Vector[] n = {vec(0.0f, 1.0f, 0.0f), vec(1.0f, 0.0f, 0.0f),
				vec(1.0f, 1.0f, 0.0f), vec(0.0f, 1.0f, 0.0f) };

		Vertex[] vert = {
				// front
				new Vertex(p[0], c[0], n[0]),
				new Vertex(p[1], c[1], n[1]),
				new Vertex(p[2], c[2], n[2]),
				new Vertex(p[3], c[3], n[3]) };

		vertices = vert;

		// Prepare the vertex data arrays.
		// Compile vertex data into a Java Buffer data structures that can be
		// passed to the OpenGL API efficently.
		positionData = BufferUtils.createFloatBuffer(vertices.length
				* vecmath.vectorSize());
		colorData = BufferUtils.createFloatBuffer(vertices.length
				* vecmath.colorSize());
		normalData = BufferUtils.createFloatBuffer(vertices.length
				* vecmath.vectorSize());

		for (Vertex v : vertices) {
			positionData.put(v.position.asArray());
			colorData.put(v.color.asArray());
			normalData.put(v.normal.asArray());
		}
		positionData.rewind();
		colorData.rewind();
		normalData.rewind();
	}
	public float getW2() {
		return w2*2;
	}

	public float getD2() {
		return d2*2;
	}
}