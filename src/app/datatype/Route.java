package app.datatype;

import java.util.ArrayList;
import java.util.List;

import app.eventsystem.LevelNode;

public class Route {
	
	private int totalway = 0;
	private List<LevelNode> waypoints = new ArrayList<LevelNode>();

	public Route(){
		
	}

	
	public int getTotalway() {
		return totalway;
	}

	public void setTotalway(int totalway) {
		this.totalway = totalway;
	}

	public List<LevelNode> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<LevelNode> waypoints) {
		this.waypoints = waypoints;
	}
	
	
}
