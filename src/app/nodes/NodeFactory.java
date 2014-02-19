package app.nodes;

import java.io.File;
import java.util.Map;

import app.nodes.camera.Camera;
import app.nodes.shapes.Cube;
import app.nodes.shapes.ObjLoader;
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
	
	public Cube cube(String id, Shader shader, float mass) {
		Cube n = new Cube(id, shader, mass);
		return n;
	}

	public Cube cube(String id, Shader shader, float w, float h, float d, float mass) {
		Cube n = new Cube(id, shader, w, h, d, mass);
		return n;
	}
	
	public Pipe pipe(String id, Shader shader, float r, int lats, int longs, float mass) {
		Pipe p = new Pipe(id, shader, r, lats, longs, mass);
		return p;
	}
	
	public Sphere sphere(String id, Shader shader, float mass) {
		Sphere s = new Sphere(id, shader, mass);
		return s;
	}
	
	public Plane plane(String id, Shader shader, float width, float depth, float mass) {
		Plane p = new Plane(id, shader, width, depth, mass);
		return p;
	}
	
	public ObjLoader obj(String id, Shader shader, File sourceFile, File sourceTex, float mass){
		return new ObjLoader(id, shader, sourceFile, sourceTex, mass);
	}
}
