package app.eventsystem;

import java.io.File;
import java.util.Set;

import vecmath.Matrix;
import vecmath.Vector;
import app.Types.KeyMode;
import app.Types.ObjectTypes;
import app.Types.SimulateType;
import app.datatype.FontInfo;
import app.datatype.Route;
import app.shader.Shader;

/**
 * @author Benjamin Reemts
 */

public class SimulateCreation extends NodeCreation {
	private Set<Integer> keys;
	private SimulateType simulation;
	private KeyMode mode;
	private Vector vector;
	private Route way;
	private String targetId;

	public SimulateCreation(String objectId, Matrix modelMatrix, Set<Integer> keys, SimulateType simulation, KeyMode mode, Vector vec) {
		super(objectId);
		this.modelmatrix=modelMatrix;
		this.keys = keys;
		this.simulation = simulation;
		this.mode = mode;
		this.vector=vec;
	}
	
	/**
	 * For Physic
	 * @param objectId
	 * @param modelMatrix
	 * @param simulation
	 * @param vec
	 */
	public SimulateCreation(String objectId, Matrix modelMatrix, SimulateType simulation, Vector vec) {
		super(objectId);
		this.type=ObjectTypes.GROUP;
		this.modelmatrix=modelMatrix;
		this.simulation = simulation;
		this.vector=vec;
	}
	
	/**
	 * Only for Car
	 * @param id
	 * @param shader
	 * @param sourceFile
	 * @param mass
	 * @param way
	 * @param targetId
	 */
	public SimulateCreation(String id, Shader shader, File sourceFile, float mass, Route way, String targetId){
		super(id, shader, sourceFile, mass, ObjectTypes.CAR);
		this.simulation=SimulateType.DRIVE;
		if(way!=null)this.way=way.clone();
		this.targetId=targetId;
	}

	public String getObjectId() {
		return id;
	}

	public Set<Integer> getKeys() {
		return keys;
	}

	public SimulateType getSimulation() {
		return simulation;
	}

	public KeyMode getMode() {
		return mode;
	}

	public Vector getVector() {
		return vector;
	}

	public Route getWay() {
		return way;
	}

	public String getTargetId() {
		return targetId;
	}
}
