package app.datatype;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import vecmath.Vector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Coordinate.DimensionalComparator;

/**
 * @author Benjamin Reemts
 * 
 */

public class LevelNode implements Comparable<LevelNode> {
	private static final double BASEVAL = 0.01;
	private final Vector POS;
	private final Coordinate COORD;
	private double val=BASEVAL;
	// private int ident=0;
	private Map<LevelNode, Double> edges = new HashMap<LevelNode, Double>();

	public LevelNode(Vector vector) {
		this.POS = vector;
		this.COORD=new Coordinate(vector.x(), vector.z());
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

	public void multEdgesVal(double val) {
		for(LevelNode node:edges.keySet())edges.put(node, edges.get(node)*val);
		this.val=this.val*val;
	}
	
	public List<LevelNode> getChilds(){
//		System.out.println("parentNode: "+POS);
//		System.out.println("childlist:"+new LinkedList<LevelNode>(edges.keySet()).size());
//		System.out.println("childlist set:"+edges.keySet().size());
		return new LinkedList<LevelNode>(edges.keySet());
	}

	public float getDepth() {

		return 0;
	}

	public double getVal() {
		return val;
	}

	public Vector getPOS() {
		return POS;
	}
	
	public double getValOfEdge(LevelNode toNode){
		return edges.get(toNode);
	}
	
	public double lengthtoNode(LevelNode target){
//		System.out.println("länge von "+ POS+" bis "+ target.getPOS()+" :"+ Math.sqrt((Math.pow((target.getPOS().x()-POS.x()), 2)+Math.pow((target.getPOS().z()-POS.z()), 2))));
		return Math.sqrt((Math.pow((target.getPOS().x()-POS.x()), 2)+Math.pow((target.getPOS().z()-POS.z()), 2)));
	}

	@Override
	public int hashCode() {
		return getPOS().hashCode()*457;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LevelNode) {
			LevelNode lNode = (LevelNode) obj;
			if(lNode.POS.equals(getPOS())){
				return true;
			}
		}
		return false;
	}
	
	public Coordinate getCoordinate(){
		return COORD;
	}

	@Override
	public int compareTo(LevelNode o) {
		return new DimensionalComparator(2).compare(getCoordinate(), o.getCoordinate());
	}
	
	@Override
	public String toString() {
		return "("+POS.x()+"/"+POS.z()+")["+val+"]";
	}
}
