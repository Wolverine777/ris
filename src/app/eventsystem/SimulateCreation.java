package app.eventsystem;

import java.util.Set;

<<<<<<< HEAD
import app.messages.Mode;
import app.messages.SimulateType;
import app.nodes.Node;

public class SimulateCreation {
	private Node object;
	private Set<Integer> keys; 
	private SimulateType simulation;
	private Mode mode;
	
	public SimulateCreation(Node object, Set<Integer> keys, SimulateType simulation, Mode mode){
		this.object=object;
		this.keys=keys;
		this.simulation=simulation;
		this.mode=mode;
	}

	public Node getObject() {
		return object;
=======
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
>>>>>>> refs/remotes/origin/test
	}

	public Set<Integer> getKeys() {
		return keys;
	}

	public SimulateType getSimulation() {
		return simulation;
	}

<<<<<<< HEAD
	public Mode getMode() {
		return mode;
	}
	
=======
	public KeyMode getMode() {
		return mode;
	}

	public Vector getVector() {
		return vector;
	}
	

>>>>>>> refs/remotes/origin/test
}
