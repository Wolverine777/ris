package app.nodes.shapes;

import static app.nodes.shapes.Vertex.*;
import static app.vecmathimp.FactoryDefault.vecmath;

import java.util.ArrayList;
import java.util.List;

import app.shader.Shader;
import app.vecmath.Vector;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * Torus (the donut-ish thing)
 * 
 * @author Constantin
 * 
 */
public class Torus extends Shape {

	// Diameters
	float rCenter;
	float rTube;

	// Smoothness
	float uStep;
	float vStep;

	private Vertex[] vertices;

	private List<Vector> pL = new ArrayList<Vector>();
	private List<Vertex> vL = new ArrayList<Vertex>();
	private Vector[] nL;

	public Torus(String id, Shader shader) {
		this(id, shader, 2f, 1f, 12, 12, true);
	}

	public Torus(String id, Shader shader, float rCenter, float rTube) {
		this(id, shader, rCenter, rTube, 12, 12, true);
	}

	public Torus(String id, Shader shader, float rCenter, float rTube,
			float uS, float vS, boolean useNormals) {
		super(id, shader);
		
		mode = GL11.GL_QUAD_STRIP;
		
		this.rCenter = rCenter;
		this.rTube = rTube;

		uStep = (float) Math.PI / uS;
		vStep = (float) Math.PI / vS;

		boolean swap = false;
		float x, y, z;
		for (float u = 0; u <= 2 * Math.PI; u += uStep) {
			for (float v = 0; v <= 2 * Math.PI; v += vStep) {
				x = (float) ((rCenter + rTube * Math.cos(v)) * Math.cos(u));
				y = (float) ((rCenter + rTube * Math.cos(v)) * Math.sin(u));
				z = (float) (rTube * Math.sin(v));
				pL.add(vec(x, y, z));
				if (swap) {
					u += uStep;
				} else {
					u -= uStep;
				}
				swap = !swap;
			}
		}
		nL = setNull();
		if (useNormals)
			calculateSurfaceNormal();
		for (int i = 0; i < pL.size(); i++) {
			vL.add(v(pL.get(i), col((float) (Math.random() * 0.3), 0.2f, 0.2f),
					nL[i]));
		}
		vertices = vL.toArray(new Vertex[vL.size()]);

		positionData = BufferUtils.createFloatBuffer(vertices.length
				* vecmath.vectorSize());
		colorData = BufferUtils.createFloatBuffer(vertices.length
				* vecmath.vectorSize());
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

	private Vector[] setNull() {
		Vector[] ntemp = new Vector[pL.size()];
		for (int i = 0; i < pL.size(); i++) {
			ntemp[i] = norm(0, 0, 0);
		}
		return ntemp;
	}

	public void calculateSurfaceNormal() {
		ArrayList<Vector> temp = new ArrayList<Vector>();
		ArrayList<Integer> index = new ArrayList<Integer>();
		Vector finalNormal;
		for (int i = 0; i < pL.size(); i++) {
			temp.clear();
			index.clear();
			finalNormal = norm(0, 0, 0);
			index.add(i);
			temp.add(pL.get(i));
			for (int j = i + 1; j < pL.size(); j++) {
				if (pL.get(i).equals(pL.get(j))) {
					temp.add(pL.get(j));
					index.add(j);
				}
			}
			for (int k = 0; k < temp.size(); k++) {
				finalNormal = finalNormal.add(temp.get(k));
			}
			finalNormal = finalNormal.normalize();
			for (int in : index) {
				nL[in] = finalNormal;
			}
		}
	}
}