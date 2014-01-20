package app.nodes;

import java.util.HashMap;
import java.util.Map;

import app.nodes.camera.Camera;
import app.nodes.shapes.Cube;
import app.nodes.shapes.Pipe;
import app.nodes.shapes.Plane;
import app.nodes.shapes.Sphere;
import app.shader.Shader;
import app.vecmath.Matrix;

/**
 * Creates and keeps track of created nodes for easy modification
 * 
 * @author Constantin
 *
 */
public class NodeFactory {
	
	// Easy access
	public static final NodeFactory nodeFactory = new NodeFactory();
	
	public GroupNode groupNode(String id) {
		return new GroupNode(id);
	}
	
	public GroupNode groupNode(String id, Matrix modelMatrix) {
		return new GroupNode(id, modelMatrix);
	}
	
	public GroupNode groupNode(String id, Matrix modelMatrix, Node node) {
		GroupNode n = new GroupNode(id, modelMatrix, node);
		return n;
	}
	
	public GroupNode groupNode(String id, Matrix modelMatrix, Node node,
			Map<String, String> data) {
		GroupNode n = new GroupNode(id, modelMatrix, node, data);
		return n;
	}
	
	public Camera camera(String id) {
		Camera n = new Camera(id);
		return n;
	}
	
	public Cube cube(String id, Shader shader) {
		Cube n = new Cube(id, shader);
		System.out.println("cubes: "+ n.id + n.getCenter() + n.getRadius());;
		return n;
	}

	public Cube cube(String id, Shader shader, float w, float h, float d) {
		Cube n = new Cube(id, shader, w, h, d);
		System.out.println("cubes: "+ n.id + n.getCenter() + n.getRadius());;
		return n;
	}
	
	public Pipe pipe(String id, Shader shader, float r, int lats, int longs) {
		Pipe p = new Pipe(id, shader, r, lats, longs);
		return p;
	}
	
	public Sphere sphere(String id, Shader shader) {
		Sphere s = new Sphere(id, shader);
		return s;
	}
	
	public Plane plane(String id, Shader shader, float width, float depth) {
		Plane p = new Plane(id, shader, width, depth);
		return p;
	}
}
