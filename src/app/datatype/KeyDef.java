package app.datatype;

import java.util.Set;

import vecmath.Vector;
import app.Types.KeyMode;
import app.Types.SimulateType;

/**
 * @author Benjamin Reemts
 *
 */

public class KeyDef {
	private SimulateType type;
	private Set<Integer> keys;
	private KeyMode mode;
	private Vector vector;
	private Route way;
	

	public KeyDef(SimulateType type, Set<Integer> keys, KeyMode mode){
		this.type=type;
		this.keys=keys;
		this.mode=mode;
	}
	
	public KeyDef(SimulateType type, Set<Integer> keys, KeyMode mode, Vector vec){
		this.type=type;
		this.keys=keys;
		this.mode=mode;
		this.vector=vec;
	}
	
	public KeyDef(SimulateType type, Route way){
		this(type, null, null);
		this.way=way;
	}


	public SimulateType getType() {
		return type;
	}


	public Set<Integer> getKeys() {
		return keys;
	}


	public KeyMode getMode() {
		return mode;
	}

	public Vector getVector() {
		return vector;
	}

	public void setVector(Vector vec) {
		this.vector = vec;
	}

	public Route getWay() {
		return way;
	}
	
}
