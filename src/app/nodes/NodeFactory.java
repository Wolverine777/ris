package app.nodes;

import java.io.File;
import java.util.Map;

import vecmath.Matrix;
import app.datatype.FontInfo;
import app.nodes.camera.Camera;
import app.nodes.shapes.Car;
import app.nodes.shapes.Coin;
import app.nodes.shapes.Canon;
import app.nodes.shapes.Cube;
import app.nodes.shapes.ObjLoader;
import app.nodes.shapes.Pipe;
import app.nodes.shapes.Plane;
import app.nodes.shapes.Sphere;
import app.shader.Shader;

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
	
//	public Cube cube(String id, Shader shader, float mass) {
//		Cube n = new Cube(id, shader, mass);
//		return n;
//	}

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
	
	public Canon canon(String id, Shader shader, File sourceFile, File sourceTex, float mass) {
		Canon c = new Canon (id, shader, sourceFile, sourceTex, mass);
		return c;
		
	}
	
	public Plane plane(String id, Shader shader, float width, float depth, float hight, float mass) {
		Plane p = new Plane(id, shader, width, depth, hight, mass);
		return p;
	}
	
	public ObjLoader obj(String id, Shader shader, File sourceFile, File sourceTex, float mass){
		return new ObjLoader(id, shader, sourceFile, sourceTex, mass);
	}
	
	public Coin coin(String id, Shader shader, File sourceFile, float mass){
		return new Coin(id, shader, sourceFile, mass);
	}
	
	public Car car(String id, Shader shader, File sourceFile, double speed, float mass){
		return new Car(id, shader, sourceFile, speed, mass);
	}
	
	public Text text(String id, Matrix matrix, String text, FontInfo font){
		return new Text(id,matrix,text,font);
	}
}
