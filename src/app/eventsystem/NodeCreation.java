package app.eventsystem;

import java.io.File;

import vecmath.Matrix;
import vecmath.Vector;
import app.Types.ObjectTypes;
import app.Types.PhysicType;
import app.shader.Shader;

public class NodeCreation {
	public String id;
	public Shader shader;
	public ObjectTypes type;
	
	public Matrix modelmatrix;
	
	public Vector impulse;
	
	public float w, h, d, r, hight;
	
	public int lats, longs;
	
	public File sourceFile, sourceTex;
	
	public Vector center;
	
	public float radius;
	
	public float mass;
	
	public float speed;
	
	public PhysicType physicType;

	public Matrix getModelmatrix() {
		return modelmatrix;
	}

	public String getId() {
		return id;
	}
	
}