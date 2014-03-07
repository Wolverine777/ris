package app.nodes.shapes;

import java.io.File;

import vecmath.Vector;
import app.datatype.LevelNode;
import app.datatype.Route;
import app.shader.Shader;

/**
 * @author Benjamin Reemts
 *
 */

public class Car extends ObjLoader {
	private Route wayToTarget;
	private double speed;
	private Vector directionToNextTarget;
	//for later implementation of changing routes because of ball is moving closer to floor.
	private int updateFrequenz=0;

	public Car(String id, Shader shader, double speed, float mass) {
		super(id, shader, mass);
		this.speed=speed;
		// TODO Auto-generated constructor stub
	}

	public Car(String id, Shader shader, File sourcePath, float speed, float mass) {
		super(id, shader, sourcePath, mass);
		this.speed=speed;
		// TODO Auto-generated constructor stub
	}

	public Route getWayToTarget() {
		return wayToTarget;
	}

	public void setWayToTarget(Route wayToTarget) {
		this.wayToTarget = wayToTarget;
		System.out.println("direction calc: "+getWorldTransform().getPosition()+"-->"+getNextWaypoint().getPOS()+"="+getNextWaypoint().getPOS().sub(getWorldTransform().getPosition()));
		directionToNextTarget=getNextWaypoint().getPOS().sub(getWorldTransform().getPosition());
	}
	
	public void waypointReached(){
		System.out.print("point reached, set to next waypoint: "+wayToTarget.toString());
		wayToTarget.setTotalway(wayToTarget.getTotalway()-getNextWaypoint().getValOfEdge(wayToTarget.getWaypoints().get(1)));
		this.wayToTarget.removeFirstPoint();
		if(this.wayToTarget.getWaypoints().isEmpty()){
			this.wayToTarget=null;
		}else{
			directionToNextTarget=getNextWaypoint().getPOS().sub(getWorldTransform().getPosition());
			System.out.println(" now: "+wayToTarget.toString());
		}
	}
	
	public LevelNode getNextWaypoint(){
		return wayToTarget.getWaypoints().get(0);
	}

	public int getUpdateFrequenz() {
		return updateFrequenz;
	}

	public void setUpdateFrequenz(int updateFrequenz) {
		this.updateFrequenz = updateFrequenz;
	}

	public double getSpeed() {
		return speed;
	}
	
	public Vector getVecToNextTarget() {
		updateFrequenz--;
		System.out.println("pos way:"+getNextWaypoint().getPOS()+" poss car:"+getWorldTransform().getPosition());
		return directionToNextTarget.mult((float) speed);
	}	
	
	public Vector getPosition(){
		return getWorldTransform().getPosition();
	}
	
	public LevelNode getFinalTarget(){
		if(wayToTarget!=null)return wayToTarget.getLastWaypoint();
		return null;
	}
}
