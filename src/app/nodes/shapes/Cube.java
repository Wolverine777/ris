package app.nodes.shapes;

import java.io.File;

import app.nodes.shapes.Texture;

import org.lwjgl.BufferUtils;

import app.shader.Shader;
import app.vecmath.Color;
import app.vecmath.Vector;
import app.vecmathimp.VectorImp;
import static app.nodes.shapes.Vertex.*;
import static app.vecmathimp.FactoryDefault.vecmath;

public class Cube extends Shape {

	/*-
	 * 
	 *   6 ------- 7
	 *  /|        /|
	 * 3 ------- 2 |
	 * | |       | |
	 * | 5 ------|-4
	 * |/        |/
	 * 0 ------- 1
	 * 
	 */

	// Width, depth and height of the cube divided by 2.
	private float w2;
	private float h2;
	private float d2;
	

	public Cube(String id, Shader shader) {
		this(id, shader, 1f, 1f, 1f);
	}

	public Cube(String id, Shader shader, float w, float h, float d) {
		this(id, shader, w, h, d, null);
	}

	public Cube(String id, Shader shader, float w, float h, float d,
			String sourceTex) {
		super(id, shader);
		w2 = w / 2;
		h2 = h / 2;
		d2 = d / 2;
		
		
		
//		// TODO noch eine sinnvolle Zahl drauf addieren um rechtzeitig kollision zu erkennen
//		if(center.x() >= center.y() && center.x() >=center.z()){
//			radius = center.x();			
//		}
//		else if (center.y() >= center.x() && center.y() >=center.z()){
//			radius = center.y();			
//		}
//		else if (center.z() >= center.x() && center.z() >=center.y()){
//			radius = center.z();			
//		}
//		System.out.println("Hat das jetzt nen mittelpunkt und nen radius?????? " + id + center.toString() + radius);

		if (sourceTex != null) {
			tex = new Texture(new File(sourceTex));
		} else {
			tex = null;
		}
		setup();
		findCenter();
	}

	private void setup() {
		
		Color[] c = { col(.4f, .7f, .8f), col(1, 0, 0), col(1, 1, 0),
				col(0, 1, 0), col(1, 0, 1), col(0, 0, 1), col(0, 1, 1),
				col(1, 1, 1) };
		
		Vector[] p = { vec(-w2, -h2, d2), vec(w2, -h2, d2), vec(w2, h2, d2),
		vec(-w2, h2, d2), vec(w2, -h2, -d2), vec(-w2, -h2, -d2),
		vec(-w2, h2, -d2), vec(w2, h2, -d2) };
		
		Vector[] t = { vec(0.0f, 0.0f, 0.0f), vec(1.0f, 0.0f, 0.0f),
				vec(1.0f, 1.0f, 0.0f), vec(0.0f, 1.0f, 0.0f) };

		Vector[] n = {
				((p[2].sub(p[1]).cross(p[0].sub(p[1]))).add(p[3].sub(p[0])
						.cross(p[5].sub(p[0]))).add(p[1].sub(p[4]).cross(
						p[5].sub(p[4])))),
				(p[2].sub(p[1]).cross(p[0].sub(p[1]))).add(
						p[7].sub(p[4]).cross(p[1].sub(p[4]))).add(
						p[1].sub(p[4]).cross(p[5].sub(p[4]))),
				(p[2].sub(p[1]).cross(p[0].sub(p[1]))).add(
						p[7].sub(p[4]).cross(p[1].sub(p[4]))).add(
						p[7].sub(p[2]).cross(p[3].sub(p[2]))),
				(p[2].sub(p[1]).cross(p[0].sub(p[1]))).add(
						p[7].sub(p[2]).cross(p[3].sub(p[2]))).add(
						p[3].sub(p[0]).cross(p[5].sub(p[0]))),
				(p[7].sub(p[4]).cross(p[1].sub(p[4]))).add(
						p[1].sub(p[4]).cross(p[5].sub(p[4]))).add(
						p[6].sub(p[5]).cross(p[4].sub(p[5]))),
				(p[1].sub(p[4]).cross(p[5].sub(p[4]))).add(
						p[3].sub(p[0]).cross(p[5].sub(p[0]))).add(
						p[6].sub(p[5]).cross(p[4].sub(p[5]))),
				(p[3].sub(p[0]).cross(p[5].sub(p[0]))).add(
						p[6].sub(p[5]).cross(p[4].sub(p[5]))).add(
						p[7].sub(p[2]).cross(p[3].sub(p[2]))),
				(p[7].sub(p[2]).cross(p[3].sub(p[2]))).add(
						p[6].sub(p[5]).cross(p[4].sub(p[5]))).add(
						p[7].sub(p[4]).cross(p[1].sub(p[4]))), };

		Vertex[] vert = {
				// front
				new Vertex(p[0], c[0], n[0], t[3]),
				new Vertex(p[1], c[1], n[1], t[2]),
				new Vertex(p[2], c[2], n[2], t[1]),
				new Vertex(p[3], c[3], n[3], t[0]),
				// back
				new Vertex(p[4], c[4], n[4], t[2]),
				new Vertex(p[5], c[5], n[5], t[3]),
				new Vertex(p[6], c[6], n[6], t[0]),
				new Vertex(p[7], c[7], n[7], t[1]),
				// right
				new Vertex(p[1], c[1], n[1], t[3]),
				new Vertex(p[4], c[4], n[4], t[2]),
				new Vertex(p[7], c[7], n[7], t[1]),
				new Vertex(p[2], c[2], n[2], t[0]),
				// top
				new Vertex(p[3], c[3], n[3], t[3]),
				new Vertex(p[2], c[2], n[3], t[2]),
				new Vertex(p[7], c[7], n[7], t[1]),
				new Vertex(p[6], c[6], n[6], t[0]),
				// left
				new Vertex(p[5], c[5], n[5], t[3]),
				new Vertex(p[0], c[0], n[0], t[2]),
				new Vertex(p[3], c[3], n[3], t[1]),
				new Vertex(p[6], c[6], n[6], t[0]),
				// bottom
				new Vertex(p[5], c[5], n[5], t[0]),
				new Vertex(p[4], c[4], n[4], t[1]),
				new Vertex(p[1], c[1], n[1], t[3]),
				new Vertex(p[0], c[0], n[0], t[2]) };

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

	public float getH2() {
		return h2*2;
	}

	public float getD2() {
		return d2*2;
	}
	
	
}