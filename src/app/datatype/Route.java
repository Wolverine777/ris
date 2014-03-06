package app.datatype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {
	
	private int totalway = 0;
	private List<LevelNode> waypoints = new ArrayList<LevelNode>();

	public Route(int totalway, List<LevelNode> waypoints) {
		this.totalway = totalway;
		Collections.reverse(waypoints);
		this.waypoints = waypoints;
	}

	public int getTotalway() {
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
	
}
