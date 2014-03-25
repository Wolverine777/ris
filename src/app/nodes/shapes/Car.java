package app.nodes.shapes;

import java.io.File;

import vecmath.Matrix;
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

	public Car(String id, Shader shader, double speed, Matrix modelMatrix, float mass) {
		super(id, shader, modelMatrix, mass);
		this.speed=speed;
		// TODO Auto-generated constructor stub
	}

	public Car(String id, Shader shader, File sourcePath, double speed, Matrix modelMatrix, float mass) {
		super(id, shader, sourcePath, modelMatrix, mass);
		this.speed=speed;
		// TODO Auto-generated constructor stub
	}
	
	public Car(String id, Shader shader, File sourceFile, File sourceTex, double speed, Matrix modelMatrix, float mass) {
		super(id, shader, sourceFile, sourceTex, modelMatrix, mass);
		this.speed=speed;
		// TODO Auto-generated constructor stub
	}

	public Route getWayToTarget() {
		return wayToTarget;
	}

	public void setWayToTarget(Route wayToTarget) {
		this.wayToTarget = wayToTarget;
		if(wayToTarget!=null){
//			Vector dir=getWorldTransform().getPosition();
//			Vector toNext=getNextWaypoint().getPOS();
//			System.out.println("direction calc: "+dir+"-->"+toNext+"="+getNextWaypoint().getPOS().sub(getPosition()));
			calcDirection();
		}else{
			directionToNextTarget=new VectorImp(0, 0, 0);
			this.timesToMove=-1;
		}
	}
	
	/**
	 * Call this when the Car has Reached a Waypoint to get to the next Waypoint
	 */
	private void waypointReached(){
//		System.out.println("point reached, set to next waypoint: "+wayToTarget.toString());
		if(wayToTarget.getWaypoints().size()<=1){
			wayToTarget.setTotalway(0);
		}else{
//			System.out.println("way to next:"+getNextWaypoint()+" sec next: "+wayToTarget.getWaypoints().get(1)+" totalway:"+wayToTarget.getTotalway());
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
		Vector v=getNextWaypoint().getPOS().sub(getPosition());
		this.timesToMove=1/this.speed*100;
		this.elapsed=1;
		int pos=1000000;
		directionToNextTarget=new VectorImp(((float)Math.round((v.x()*pos)))/pos, ((float)Math.round((v.y()*pos)))/pos, ((float)Math.round((v.z()*pos)))/pos);
	}
	
	private LevelNode getNextWaypoint(){
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
	
	/**
	 * 
	 * @param elapsed
	 * @return the Vector to the next WayPoint mult with Speed, or null if there is no next Point.
	 */
	public Vector getVecToNextTarget(float elapsed) {
		updateFrequenz--;
//		System.out.println("pos way:"+getNextWaypoint().getPOS()+" poss car:"+getWorldTransform().getPosition());
		if(timesToMove==0){
			waypointReached();
		}
		if(directionToNextTarget.equals(new VectorImp(0, 0, 0)))return null;
		Vector vec=directionToNextTarget.mult((float) ((speed/100*moveTime())/this.elapsed));
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
	
	private double moveTime(){
		if(this.timesToMove>0){
			if(this.timesToMove>1){
				this.timesToMove-=1;
				return 1;
			}else{
				double tmp=timesToMove;
				this.timesToMove-=timesToMove;
				return tmp;
			}
		}
		return 0;
	}	
}
