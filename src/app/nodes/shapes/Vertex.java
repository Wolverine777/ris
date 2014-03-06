package app.nodes.shapes;

import static app.vecmathimp.FactoryDefault.vecmath;
import app.vecmath.Color;
import app.vecmath.Vector;

public class Vertex {
	Vector position;
	Color color;
	public Vector normal;
	public Vector texcoord;

	Vertex(Vector p, Color c) {
		position = p;
		color = c;
	}

	public Vertex(Vector p, Color c, Vector n) {
		position = p;
		color = c;
		normal = n;
	}

	public Vertex(Vector p, Color c, Vector n, Vector tex) {
		position = p;
		color = c;
		normal = n;
		texcoord = tex;
	}

	public void newPos(Vector newPosition) {
		position = newPosition;
	}

	public void setNormal(Vector normal) {
		this.normal = normal;
	}

	// Make construction of vertices easy on the eyes.
	public static Vertex v(Vector p, Color c) {
		return new Vertex(p, c);
	}

	// Make construction of vertices easy on the eyes.
	public static Vertex v(Vector p, Color c, Vector n) {
		return new Vertex(p, c, n);
	}

	// Make construction of vectors easy on the eyes.
	public static Vector vec(float x, float y, float z) {
		return vecmath.vector(x, y, z);
	}

	// Make construction of colors easy on the eyes.
	public static Color col(float r, float g, float b) {
		return vecmath.color(r, g, b);
	}

	// Make construction of normals easy on the eyes.
	public static Vector norm(float nx, float ny, float nz) {
		return vecmath.vector(nx, ny, nz);
	}
}
