package app.messages;

import app.Types.GestureType;

/**
 * @author Fabian Unruh
 *
 */
public class RegisterGesture {
	
	private GestureType gestureType;
	private boolean add;
	
	public RegisterGesture(GestureType gestureType, boolean add) {
		this.gestureType = gestureType;
		this.add = add;
	}

	public GestureType getGestureType() {
		return gestureType;
	}

	public boolean isAdd() {
		return add;
	}
	
	
	
	

}
