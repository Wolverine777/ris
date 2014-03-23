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
	
	}
	public Vector getHandPosition() {
		return handPosition;
	}
	public void setHandPosition(Vector handPosition) {
		this.handPosition = handPosition;
	}
	public int getFingerAmount() {
		return fingerAmount;
	}
	public void setFingerAmount(int fingerAmount) {
		this.fingerAmount = fingerAmount;
	}
	
	
	
	

}
