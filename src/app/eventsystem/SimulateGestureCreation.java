package app.eventsystem;

import vecmath.Matrix;
import vecmath.Vector;
import app.Types.GestureType;
import app.Types.SimulateType;

public class SimulateGestureCreation extends NodeCreation {
	private GestureType gesture;
	private SimulateType simulation;
	private Vector vector;
	
	public SimulateGestureCreation(String id, Matrix modelmatrix, GestureType gesture, SimulateType simulation, Vector vector) {
		super(id);
		this.modelmatrix = modelmatrix;
		this.gesture = gesture;
		this.simulation = simulation;
		this.vector = vector;
	}

	public GestureType getGesture() {
		return gesture;
	}

	public void setGesture(GestureType gesture) {
		this.gesture = gesture;
	}

	public SimulateType getSimulation() {
		return simulation;
	}

	public void setSimulation(SimulateType simulation) {
		this.simulation = simulation;
	}

	public Vector getVector() {
		return vector;
	}

	public void setVector(Vector vector) {
		this.vector = vector;
	}
	
	
	
	
	

}
