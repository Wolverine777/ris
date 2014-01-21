package app;

import static app.nodes.NodeFactory.nodeFactory;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeModification;
import app.eventsystem.PhysicModification;
import app.eventsystem.Types;
import app.messages.Message;
import app.messages.PhysicInitialization;
import app.messages.RendererInitialization;
import app.nodes.Node;
import app.toolkit.StopWatch;
import app.vecmath.Vector;
import app.vecmathimp.VectorImp;

public class Physic extends UntypedActor {

	private Map<String, Node> nodes = new HashMap<String, Node>();
	ActorRef simulator;
	private StopWatch zeit = new StopWatch();
	private Vector ground = new VectorImp(0f, -0.001f, 0f);

	private void initialize() {
		getSender().tell(Message.INITIALIZED, self());
		System.out.println("Physic initialised");
	}

	public void physic() {
		for (Node n : nodes.values()) {
			if (collisionGround(n) == false || collisionObjects(n) == false) {
				// for(Node n: nodes.values()){
//				System.out.println("Matrix NodePhysic: " + n.getWorldTransform().toString());
//				System.out.println("funkt das???" + n.id);
//				System.out.println("alte velo:" + n.getVelocity());
				n.setForce((n.getVelocity().add(new VectorImp(0, ground.y()*zeit.elapsed(), 0))));
//				TODO Masse einabauen, dann impuls setzen und dann velocity
				n.setVelocity(n.getForce());
//				System.out.println("neue velo:" + n.getVelocity());
				
				PhysicModification p = new PhysicModification();
				p.id = n.id;
				p.force = n.getForce();
				simulator.tell(p, self());
				

			} else if (collisionGround(n) == true
					|| collisionObjects(n) == false) {

			} else if (collisionGround(n) == false
					|| collisionObjects(n) == true) {

			}
		}
		getSender().tell(Message.DONE, self());
		System.out.println("physic loop");
	}

	private boolean collisionObjects(Node n) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean collisionGround(Node n) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onReceive(Object message) throws Exception {
		if (message == Message.LOOP) {
			physic();
		} else if (message instanceof PhysicInitialization) {
			this.simulator = (((PhysicInitialization) message).simulator);
			initialize();
		} else if (message instanceof NodeCreation) {
//			System.out.println("PHHHHHYYYYYYYYYYYYYYYYYYYSSSSSSSSIIIIIICCC");

			if (((NodeCreation) message).type == Types.GROUP) {
				Node newNode = nodeFactory
						.groupNode(((NodeCreation) message).id);
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == Types.CUBE) {

//				System.out.println("Shadering cube with "
//						+ ((NodeCreation) message).shader);

				Node newNode = nodeFactory.cube(((NodeCreation) message).id,
						((NodeCreation) message).shader,
						((NodeCreation) message).w, ((NodeCreation) message).h,
						((NodeCreation) message).d);
				if ((((NodeCreation) message).impulse != null)) {
					newNode.setVelocity(((NodeCreation) message).impulse);
				}
				if ((((NodeCreation) message).modelmatrix != null)) {
					newNode.updateWorldTransform(((NodeCreation) message).modelmatrix);
				}
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == Types.SPHERE) {

				Node newNode = nodeFactory.sphere(((NodeCreation) message).id,
						((NodeCreation) message).shader);
				
						
				if ((((NodeCreation) message).impulse != null)) {
//					TODO Masse einbauen
					newNode.setVelocity(((NodeCreation) message).impulse);
				}
				if ((((NodeCreation) message).modelmatrix != null)) {
					newNode.updateWorldTransform(((NodeCreation) message).modelmatrix);
				}
				nodes.put(newNode.id, newNode);
			}else if (message instanceof NodeModification) {
				if (nodes.containsKey(((NodeModification) message).id)) {
//					System.out.println("NodeModification");

//					System.out.println("Nodes " + nodes);
//					System.out.println("Accesing "
//							+ ((NodeModification) message).id);

					Node modify = nodes.get(((NodeModification) message).id);

					if (((NodeModification) message).localMod != null) {
						 modify.setLocalTransform(((NodeModification) message).localMod);
						 modify.updateWorldTransform();
					}
					if (((NodeModification) message).appendTo != null) {

//						System.out.println("Appending "
//								+ ((NodeModification) message).id + " to "
//								+ ((NodeModification) message).appendTo);

						modify.appendTo(nodes
								.get(((NodeModification) message).appendTo));
					}
				}
			}
		}
	}
}
