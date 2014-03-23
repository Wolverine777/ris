package app.messages;

import vecmath.Vector;
import vecmath.vecmathimp.VectorImp;

public class HandPosition {
	
	public Vector handPosition;
	public int fingerAmount;
	
	public HandPosition(Vector handPosition, int fingerAmount) {
		this.handPosition = handPosition;
		this.fingerAmount = fingerAmount;
	}
	
	public HandPosition() {
		this.handPosition = new VectorImp(0,0,0);
		this.fingerAmount = 0;
	}
	

}
