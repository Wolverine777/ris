package app.nodes.shapes;

import static app.vecmathimp.FactoryDefault.vecmath;
import app.vecmath.Color;
import app.vecmath.Vector;

public class Vertex {
	Vector position;
	Color color;

	Vertex(Vector p, Color c) {
		position = p;
		color = c;
	}
	
	// Make construction of vertices easy on the eyes.
	public static Vertex v(Vector p, Color c) {
		return new Vertex(p, c);
	}

	// Make construction of vectors easy on the eyes.
	public static Vector vec(float x, float y, float z) {
		return vecmath.vector(x, y, z);
	}

	// Make construction of colors easy on the eyes.
	public static Color col(float r, float g, float b) {
		return vecmath.color(r, g, b);
	}
}