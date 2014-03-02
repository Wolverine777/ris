package app.eventsystem;

import java.util.Set;

import vecmath.Vector;
import app.Types.KeyMode;
import app.Types.SimulateType;

public class SimulateCreation extends NodeCreation {
	private Set<Integer> keys;
	private SimulateType simulation;
	private KeyMode mode;
	private Vector vector;

	// TODO: add modelmatrix as params, can be null

	public SimulateCreation(String objectId, Set<Integer> keys, SimulateType simulation, KeyMode mode, Vector vec) {
		id = objectId;
		this.keys = keys;
		this.simulation = simulation;
		this.mode = mode;
		this.vector=vec;
	}

	public void setSimulation(SimulateType simulation) {
		this.simulation = simulation;
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
	

}
