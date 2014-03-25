package app.datatype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Benjamin Reemts
 *
 */
public class Route {
	private double totalway = 0;
	private List<LevelNode> waypoints = new ArrayList<LevelNode>();

	public Route(double totalway, List<LevelNode> waypoints) {
		this.totalway = totalway;
		Collections.reverse(waypoints);
		this.waypoints = waypoints;
	}

	public double getTotalway() {
		return totalway;
	}

	public List<LevelNode> getWaypoints() {
		return waypoints;
	}
	
	public String toString(){
		String out="Len:("+totalway+") Way: ";
		for(LevelNode node:waypoints)out+="-->("+node.getPOS().x()+"/"+node.getPOS().z()+")";
		return out;
	}

	public void setTotalway(double totalway) {
		this.totalway = totalway;
	}
	
	public void removeFirstPoint(){
		waypoints.remove(0);
	}
	
	public LevelNode getLastWaypoint(){
		int size=waypoints.size();
		if(size>0)return waypoints.get(size-1);
		return null;
	}
	
	public LevelNode getFirstWaypoint(){
		if(waypoints!=null&&!waypoints.isEmpty())return waypoints.get(0);
		return null;
	}
	
	@Override
	public Route clone(){
		List<LevelNode> ln=new LinkedList<LevelNode>();
//		ln.addAll(waypoints);
		for(LevelNode l:waypoints){
			LevelNode node=new LevelNode(l.getPOS());
			ln.add(node);
		}
		for(int x=0;x<ln.size();x++){
			for(LevelNode edge:waypoints.get(x).getEdges()){
				for(LevelNode newNode:ln){
					if(newNode.getPOS().equals(edge.getPOS())){
						ln.get(x).addEdge(newNode, edge.getValOfEdge(waypoints.get(x)));
					}
				}
			}
		}
		Collections.reverse(ln);
		double lenWay=totalway;
		return new Route(lenWay, ln); 
	}
}