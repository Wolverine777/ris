package app.eventsystem;

import vecmath.Matrix;

public class NodeModification {
	public final String id;
	public String appendTo;
	public Matrix localMod;
	
	public NodeModification(String id, String appendTo) {
		this.id = id;
		this.appendTo=appendTo;
	}

	public NodeModification(String id, Matrix localMod){
		this.id=id;
		this.localMod=localMod;
	}
}
