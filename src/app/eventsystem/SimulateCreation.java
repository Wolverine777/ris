package app.eventsystem;

import java.util.Set;

import vecmath.Vector;
import app.Types.KeyMode;
import app.Types.ObjectTypes;
import app.Types.SimulateType;
import app.datatype.Route;

public class SimulateCreation extends NodeCreation {
	private Set<Integer> keys;
	private SimulateType simulation;
	private KeyMode mode;
	private Vector vector;
	private Route way;

	// TODO: add modelmatrix as params, can be null

	public SimulateCreation(String objectId, Set<Integer> keys, SimulateType simulation, KeyMode mode, Vector vec) {
		id = objectId;
		this.keys = keys;
		this.simulation = simulation;
		this.mode = mode;
		this.vector=vec;
	}
	
	public SimulateCreation(String objectId, Route way){
		this(objectId,null, SimulateType.DRIVE, null, null);
		if(way!=null)this.way=way.clone();
		type=ObjectTypes.CAR;
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
	
}
