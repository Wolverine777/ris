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
	public float scale=0.05f;
	private double counter=1/scale;
	

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
		this.times=times*2;
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

	public boolean timesDown() {
		if(counter>1)this.counter--;
		else counter=0;
		if(counter==0){
			this.times--;
			counter=Math.abs(1/scale);
			return true;
		}
		return false;
	}

	public String getReferenzId() {
		return referenzId;
	}

	public double getCounter() {
		return counter;
	}
	
}
