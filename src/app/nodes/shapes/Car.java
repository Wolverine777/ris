package app.nodes.shapes;

import java.io.File;

import vecmath.Vector;
import vecmath.vecmathimp.VectorImp;
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
	private double timesToMove=-1;
	//for later implementation of changing routes because of ball is moving closer to floor.
	private int updateFrequenz=0;
	private Coin target=null;
	private float elapsed=1;

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
		if(wayToTarget!=null){
			Vector dir=getWorldTransform().getPosition();
			Vector toNext=getNextWaypoint().getPOS();
			System.out.println("direction calc: "+dir+"-->"+toNext+"="+getNextWaypoint().getPOS().sub(getWorldTransform().getPosition()));
			calcDirection();
		}else{
			directionToNextTarget=new VectorImp(0, 0, 0);
			this.timesToMove=-1;
		}
	}
	
	/**
	 * Call this when the Car has Reached a Waypoint to get to the next Waypoint
	 */
	public void waypointReached(){
		System.out.print("point reached, set to next waypoint: "+wayToTarget.toString());
		if(wayToTarget.getWaypoints().size()<=1){
			wayToTarget.setTotalway(0);
		}else{
			wayToTarget.setTotalway(wayToTarget.getTotalway()-getNextWaypoint().getValOfEdge(wayToTarget.getWaypoints().get(1)));
		}
		this.wayToTarget.removeFirstPoint();
		if(this.wayToTarget.getWaypoints().isEmpty()){
			this.wayToTarget=null;
			this.timesToMove=-1;
			directionToNextTarget=new VectorImp(0, 0, 0);
		}else{
			calcDirection();
		}
	}
	
	private void calcDirection(){
		Vector v=getNextWaypoint().getPOS().sub(getWorldTransform().getPosition());
		this.timesToMove=1/this.speed;
		this.elapsed=1;
		int pos=1000000;
		directionToNextTarget=new VectorImp(((float)Math.round((v.x()*pos)))/pos, ((float)Math.round((v.y()*pos)))/pos, ((float)Math.round((v.z()*pos)))/pos);
	}
	
	public LevelNode getNextWaypoint(){
		if(wayToTarget!=null)return wayToTarget.getFirstWaypoint();
		return null;
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
	
	public Vector getVecToNextTarget(float elapsed) {
		updateFrequenz--;
//		System.out.println("pos way:"+getNextWaypoint().getPOS()+" poss car:"+getWorldTransform().getPosition());
		if(timesToMove==0){
			System.out.println("reached");
			waypointReached();
		}
		if(directionToNextTarget.equals(new VectorImp(0, 0, 0)))return null;
		Vector vec=directionToNextTarget.mult((float) ((speed*moveTime())/elapsed));
		this.elapsed=elapsed;
		return vec; 
	}	
	
	public Vector getPosition(){
		return getWorldTransform().getPosition();
	}
	
	public Coin getTarget() {
		return target;
	}

	public void setTarget(Coin target) {
		this.target = target;
	}
	
	public double moveTime(){
		System.out.println("movetime: "+timesToMove);
		if(this.timesToMove>0){
			if(this.timesToMove>1){
				this.timesToMove-=1;
				return 1;
			}else{
				System.out.println("is null");
				double tmp=timesToMove;
				this.timesToMove-=timesToMove;
				return tmp;
			}
		}
		return 0;
	}	
}
