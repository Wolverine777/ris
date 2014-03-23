package app.eventsystem;

import java.io.File;
import java.util.Set;

import vecmath.Matrix;
import vecmath.Vector;
import app.Types.KeyMode;
import app.Types.ObjectTypes;
import app.Types.SimulateType;
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
	private int times;

	/**
	 * Standard for simulation on Keys
	 * @param objectId
	 * @param modelMatrix
	 * @param keys
	 * @param simulation
	 * @param mode
	 * @param vec
	 */
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
	public SimulateCreation(String id, Shader shader, File sourceFile, File sourceTex, double speed, Matrix modelMatrix, float mass, Route way, String targetId){
		super(id, shader, sourceFile, sourceTex, speed, modelMatrix, mass);
		this.simulation=SimulateType.DRIVE;
		if(way!=null)this.way=way.clone();
		this.targetId=targetId;
	}

	/**
	 * Only for Pickup animation
	 * @param id
	 * @param targetId
	 */
	public SimulateCreation(String id, String targetId, Matrix targetMatrix, int times, Vector maxHight) {
		super(id);
		this.targetId = targetId;
		modelmatrix=targetMatrix;
		simulation=SimulateType.PICKUP;
		this.times=times;
		//TODO possible error when simulator dont have the coin, quicksolve change to group
		type=ObjectTypes.COIN;
		this.vector=maxHight;
	}
	
	/**
	 * For Objects and childs
	 * @param id
	 * @param shader
	 * @param sourceFile
	 * @param sourceTex
	 * @param mass
	 * @param type
	 */
	public SimulateCreation(String id, Shader shader, File sourceFile, File sourceTex, Matrix modelMatrix, float mass, ObjectTypes type) {
		super(id, shader, sourceFile, sourceTex, modelMatrix, mass, type);
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

	public int getTimes() {
		return times;
	}
	
}
