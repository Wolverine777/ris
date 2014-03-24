package app.datatype;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
	
	public Set<LevelNode> getEdges(){
		return edges.keySet();
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
		//TODO: addEdge toNode(this)
		edges.put(toNode, BASEVAL);
	}
	
	public void addEdge(LevelNode toNode, double value) {
		edges.put(toNode, value);
		calcOwnVal();
	}

	public void multEdgesVal(double val) {
		for(LevelNode node:edges.keySet()){
			if(val<0)System.out.println("mult val:"+this+"-->"+node+" val:"+edges.get(node).doubleValue()*val);
			edges.put(node, edges.get(node).doubleValue()*val);
		}
		calcOwnVal();
	}
	
	private void calcOwnVal(){
		Double tmpVal=null;
		for(LevelNode l:edges.keySet()){
			if(tmpVal==null)tmpVal=getValOfEdge(l);
			else{
				if(tmpVal>0)tmpVal=tmpVal*getValOfEdge(l);
				else tmpVal=tmpVal*Math.abs(getValOfEdge(l));
			}
		}
		if(tmpVal==null)tmpVal=BASEVAL;
		this.val=tmpVal;
	}
	
	public Set<LevelNode> getChilds(){
//		System.out.println("parentNode: "+POS);
//		System.out.println("childlist:"+new LinkedList<LevelNode>(edges.keySet()).size());
//		System.out.println("childlist set:"+edges.keySet().size());
		return new TreeSet<LevelNode>(edges.keySet());
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
		double ret=BASEVAL;
		try{
			ret=edges.get(toNode).doubleValue();
		}catch(Exception e){
			System.out.println("Fail to get Way of Edge: "+e.getMessage());
			System.out.println(" this: "+toString()+ " toNode: "+toNode+" edges: "+edges+" get "+edges.get(toNode));
			ret=edges.get(toNode).doubleValue();
		}
		return ret;
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
