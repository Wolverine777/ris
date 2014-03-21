package app.eventsystem;

import java.io.File;

import vecmath.Matrix;
import vecmath.Vector;
import app.Types.ObjectTypes;
import app.Types.PhysicType;
import app.datatype.FontInfo;
import app.shader.Shader;

public class NodeCreation {
	public FontInfo font;
	public String id, text;
	public Shader shader;
	public ObjectTypes type;
	
	public Matrix modelmatrix;
	
	public float w, h, d, r;
	
	public int lats, longs;
	
	public File sourceFile, sourceTex;
	public double speed;
	
	//TODO: in physcreate
	public Vector impulse;
//	public Vector center;	
//	public float radius;
	public float mass;
	public PhysicType physicType;
	
	/**
	 * For SimulationCreation
	 * @param id
	 */
	public NodeCreation(String id) {
		//TODO
		this.id = id;
	}
	
	public NodeCreation(String id, ObjectTypes type) {
		this.id = id;
		type=type;
	}
	
	/**
	 * For Cube, Plane
	 * @param id
	 * @param shader
	 * @param width
	 * @param hight
	 * @param depth
	 * @param mass
	 */
	public NodeCreation(String id, Shader shader, float width, float depth, float hight, float mass, ObjectTypes type) {
		this.id = id;
		this.shader=shader;
		this.mass=mass;
		this.w=width;
		this.h=hight;
		this.d=depth;
		this.type=type;
	}
	
	/**
	 * For Pipe
	 * @param id
	 * @param shader
	 * @param r
	 * @param lats
	 * @param longs
	 * @param mass
	 */
	public NodeCreation(String id, Shader shader, float r, int lats, int longs, float mass){
		this.id = id;
		this.shader=shader;
		this.mass=mass;
		this.r=r;
		this.lats=lats;
		this.longs=longs;
		this.type=ObjectTypes.PIPE;
	}
	
	/**
	 * For Sphere
	 * @param id
	 * @param shader
	 * @param mass
	 */
	public NodeCreation(String id, Shader shader, float mass) {
		this.id = id;
		this.shader=shader;
		this.mass=mass;
		this.type=ObjectTypes.SPHERE;
	}
	
	/**
	 * For Canon, ObjLoader
	 * @param id
	 * @param shader
	 * @param sourceFile
	 * @param sourceTex
	 * @param mass
	 * @param type
	 */
	public NodeCreation(String id, Shader shader, File sourceFile, File sourceTex, float mass, ObjectTypes type) {
		this.id = id;
		this.shader=shader;
		this.mass=mass;
		this.sourceFile=sourceFile;
		this.sourceTex=sourceTex;
		this.type=type;
	}
	
	/**
	 * For Car, Coin
	 * @param id
	 * @param shader
	 * @param sourceFile
	 * @param mass
	 * @param type
	 */
	public NodeCreation(String id, Shader shader, File sourceFile, float mass, ObjectTypes type){
		this.id = id;
		this.shader=shader;
		this.mass=mass;
		this.sourceFile=sourceFile;
		this.type=type;
	}
	
	/**
	 * For Text
	 * @param id
	 * @param matrix
	 * @param text
	 * @param font
	 */
	public NodeCreation(String id, Matrix matrix, String text, FontInfo font) {
		this.id = id;
		this.modelmatrix=matrix;
		this.text=text;
		this.font=font;
		this.type=ObjectTypes.TEXT;
	}

	public Matrix getModelmatrix() {
		return modelmatrix;
	}

	public String getId() {
		return id;
	}
	
	public void addPhysic(Vector impulse, PhysicType physicType){
		this.impulse=impulse;
		this.physicType=physicType;
	}

	public FontInfo getFont() {
		return font;
	}

	public String getText() {
		return text;
	}

	public Shader getShader() {
		return shader;
	}

	public ObjectTypes getType() {
		return type;
	}

	public float getW() {
		return w;
	}

	public float getH() {
		return h;
	}

	public float getD() {
		return d;
	}

	public float getR() {
		return r;
	}

	public int getLats() {
		return lats;
	}

	public int getLongs() {
		return longs;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public File getSourceTex() {
		return sourceTex;
	}

	public double getSpeed() {
		return speed;
	}

	public Vector getImpulse() {
		return impulse;
	}

	public float getMass() {
		return mass;
	}

	public PhysicType getPhysicType() {
		return physicType;
	}
	
}