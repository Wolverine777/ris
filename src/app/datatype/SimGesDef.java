package app.datatype;

import vecmath.Vector;
import app.Types.GestureType;
import app.Types.SimulateType;

/**
 * @author Benjamin Reemts
 *
 */
public class SimGesDef {
	private SimulateType type;
	private GestureType gesture;
	private Vector vector;
	
	public SimGesDef(SimulateType type, GestureType gesture, Vector vector) {
		this.type = type;
		this.gesture = gesture;
		this.vector = vector;
	}

	public SimulateType getType() {
		return type;
	}

	public void setType(SimulateType type) {
		this.type = type;
	}

	public GestureType getGesture() {
		return gesture;
	}

	public void setGesture(GestureType gesture) {
		this.gesture = gesture;
	}

	public Vector getVector() {
		return vector;
	}

	public void setVector(Vector vector) {
		this.vector = vector;
	}

	
	
}
