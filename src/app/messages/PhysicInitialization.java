package app.messages;

import akka.actor.ActorRef;

/**
 * @author Fabian Unruh
 *
 */
public class PhysicInitialization {

	public ActorRef simulator;
	public ActorRef ai;

	public PhysicInitialization(ActorRef simulator, ActorRef ai) {
		this.simulator = simulator;
		this.ai = ai;
	}

}
