package app.messages;

import akka.actor.ActorRef;
import app.vecmath.Vector;

/**
 * @author Benjamin Reemts
 *
 */

public class AiInitialization {

	private ActorRef simulator;
	private Vector centerPosition; 
	private float width, depth;
	public AiInitialization(ActorRef simulator, Vector centerPosition,
			float width, float depth) {
		this.simulator = simulator;
		this.centerPosition = centerPosition;
		this.width = width;
		this.depth = depth;
	}
	public ActorRef getSimulator() {
		return simulator;
	}
	public Vector getCenterPosition() {
		return centerPosition;
	}
	public float getWidth() {
		return width;
	}
	public float getDepth() {
		return depth;
	}
	
}
