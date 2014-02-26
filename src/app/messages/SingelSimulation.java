package app.messages;

import app.nodes.Node;
import app.vecmath.Vector;

/**
 * @author Benjamin Reemts
 *
 */

public class SingelSimulation {
	private Node node;
	private SimulateType type; 
	private Vector vec;
	public SingelSimulation(Node node, SimulateType type, Vector vec){
		if(node==null||type==null||vec==null){
			try {
				throw new Exception("Nicht zulässig");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}
		this.node=node;
		this.type=type;
		this.vec=vec;
	}
	
	public Node getNode() {
		return node;
	}
	public SimulateType getType() {
		return type;
	}
	public Vector getVec() {
		return vec;
	}
}
