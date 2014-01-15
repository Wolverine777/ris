package app.eventsystem;

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

	public Matrix getModelmatrix() {
		return modelmatrix;
	}
	
	
}