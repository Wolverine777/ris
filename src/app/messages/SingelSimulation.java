package app.messages;

import app.vecmath.Matrix;
import app.vecmath.Vector;

/**
 * @author Benjamin Reemts
 *
 */

public class SingelSimulation {
	private String nodeId;
	private SimulateType type; 
	private Vector vec;
	private Matrix modelMatrix;
	public SingelSimulation(String nodeId, SimulateType type, Vector vec, Matrix modelMatix){
		if(nodeId==null||type==null||vec==null||modelMatix==null){
			try {
				throw new Exception("Nicht zulässig");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}
		this.nodeId=nodeId;
		this.type=type;
		this.vec=vec;
		this.modelMatrix=modelMatix;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	public SimulateType getType() {
		return type;
	}
	public Vector getVec() {
		return vec;
	}

	public Matrix getModelMatrix() {
		return modelMatrix;
	}
	
}
