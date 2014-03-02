package app.eventsystem;

import vecmath.Matrix;

public class NodeModification {
	public String id;
	public String appendTo;
	public Matrix localMod;
	
	public NodeModification(){
		
	}
	
	public NodeModification(String id, Matrix localMod){
		this.id=id;
		this.localMod=localMod;
	}
}
