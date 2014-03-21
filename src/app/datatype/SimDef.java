package app.datatype;

import java.util.Set;

import vecmath.Vector;
import app.Types.KeyMode;
import app.Types.SimulateType;

/**
 * @author Benjamin Reemts
 *
 */

public class SimDef {
	private SimulateType type;
	private Set<Integer> keys;
	private KeyMode mode;
	private Vector vector;
	private Route way;
	private int times=0;
	private String referenzId;
	// has to be <0
	public float scale=0.2f;
	

	public SimDef(SimulateType type, Set<Integer> keys, KeyMode mode){
		this.type=type;
		this.keys=keys;
		this.mode=mode;
	}
	
	public SimDef(SimulateType type, Set<Integer> keys, KeyMode mode, Vector vec){
		this.type=type;
		this.keys=keys;
		this.mode=mode;
		this.vector=vec;
	}

	/**
	 * Only for Pickup
	 * @param vector
	 * @param referenzId
	 */
	public SimDef(String referenzId, int times) {
		this.referenzId = referenzId;
		this.times=times;
		this.type=SimulateType.PICKUP;
	}

	public SimDef(SimulateType type, Route way){
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

	public int getTimes() {
		return times;
	}

	public void timesDown() {
		this.times--;
	}

	public String getReferenzId() {
		return referenzId;
	}
	
}
