package app.eventsystem;

import java.io.File;

import app.shader.Shader;
import app.vecmath.Matrix;
import app.vecmath.Vector;

public class NodeCreation {
	public String id;
	public Shader shader;
	public Types type;
	
	public Matrix modelmatrix;
	
	public Vector impulse;
	
	public float w, h, d, r;
	
	public int lats, longs;
	
	public File sourceFile, sourceTex;
	
	public Vector center;
	
	public float radius;
	
	public float mass;

	public Matrix getModelmatrix() {
		return modelmatrix;
	}
	
	
}