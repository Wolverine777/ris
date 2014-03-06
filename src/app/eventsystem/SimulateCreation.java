package app.eventsystem;

import java.util.Set;

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
	}

	public Set<Integer> getKeys() {
		return keys;
	}

	public SimulateType getSimulation() {
		return simulation;
	}

	public Mode getMode() {
		return mode;
	}
	
}
