package app.datatype;

import java.util.List;

/**
 * @author Benjamin Reemts
 *
 */

public class AStarNodes {
	private double length; 
	private double resistance;
	private List<LevelNode> path;
	public AStarNodes(double length, double resistance, List<LevelNode> path){
		this.resistance=resistance;
		this.length=length;
		this.path=path;
	}
	public double getLength() {
		return length;
	}
	public List<LevelNode> getPath() {
		return path;
	}
	public double getResistance() {
		return resistance;
	}

}
