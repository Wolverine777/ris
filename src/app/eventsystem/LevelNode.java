package app.eventsystem;

import app.vecmath.Vector;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin Reemts
 * 
 */

public class LevelNode {
	private static final int BASEVAL = 1;
	private final Vector POS;
	private int val=1;
	// private int ident=0;
	private Map<LevelNode, Integer> edges = new HashMap<LevelNode, Integer>();

	public LevelNode(Vector vector) {
		this.POS = vector;
	}

	public void addNode(LevelNode appendNode) {
		edges.put(appendNode, BASEVAL);
	}

	public void removeNode() {
		for (LevelNode node : edges.keySet()) {
			node.removeEdge(this);
		}
	}

	public void removeEdge(LevelNode toNode) {
		edges.remove(toNode);
	}

	public void addEdge(LevelNode toNode) {
		edges.put(toNode, BASEVAL);
	}

	public void multEdgesVal(int val) {
		for(LevelNode node:edges.keySet())edges.put(node, edges.get(node)*val);
		this.val=this.val*val;
	}
	
	public List<LevelNode> getChilds(){
		System.out.println("parentNode: "+POS);
		System.out.println("childlist:"+new LinkedList<LevelNode>(edges.keySet()).size());
		System.out.println("childlist set:"+edges.keySet().size());
		return new LinkedList<LevelNode>(edges.keySet());
	}

	public float getDepth() {

		return 0;
	}

	public int getVal() {
		return val;
	}

	public Vector getPOS() {
		return POS;
	}
	
	public int getValOfEdge(LevelNode toNode){
		return edges.get(toNode);
	}
	
	public double lengthtoNode(LevelNode target){
		System.out.println("länge von "+ POS+" bis "+ target.getPOS()+" :"+ Math.sqrt((Math.pow((target.getPOS().x()-POS.x()), 2)+Math.pow((target.getPOS().z()-POS.z()), 2))));
		return Math.sqrt((Math.pow((target.getPOS().x()-POS.x()), 2)+Math.pow((target.getPOS().z()-POS.z()), 2)));
	}
	
}
