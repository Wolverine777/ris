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
import app.nodes.shapes.Shape;
import app.toolkit.StopWatch;
import app.vecmath.Vector;
import app.vecmathimp.VectorImp;

public class Physic extends UntypedActor {

	private Map<String, Node> nodes = new HashMap<String, Node>();
	ActorRef simulator;
	private StopWatch zeit = new StopWatch();
	private Vector ground = new VectorImp(0f, -0.001f, 0f);
	private float elapsed = 0;

	private void initialize() {
		getSender().tell(Message.INITIALIZED, self());
		System.out.println("Physic initialised");
	}

	public void physic() {
		elapsed = zeit.elapsed();
//		System.out.println("hierho:" + nodes.isEmpty());
		for (Node n : nodes.values()) {
			System.out.println("Radius n oben: " + ((Shape) n).getRadius());
			if (collisionGround(n) == false && collisionObjects(n) == false) {
				// for(Node n: nodes.values()){
				// System.out.println("Matrix NodePhysic: " +
				// n.getWorldTransform().toString());
				// System.out.println("funkt das???" + n.id);
//				System.out.println("alte velo:" + n.id + n.getVelocity());
				// TODO Erdanziehungskraft m*g?
				n.setForce((n.getVelocity().add(new VectorImp(0, ground.y()
						* elapsed, 0))));
				// TODO Masse einabauen, dann impuls setzen und dann velocity
				n.setVelocity(n.getForce());
//				System.out.println("neue velo:" + n.id + n.getVelocity());

				PhysicModification p = new PhysicModification();
				p.id = n.id;
				p.force = n.getForce();
				simulator.tell(p, self());

			} else if (collisionGround(n) == true
					&& collisionObjects(n) == false) {

			} else if (collisionGround(n) == false
					&& collisionObjects(n) == true) {
				// System.out.println("richtige schleife!!!!!!!!!!!");

			}
		}
		getSender().tell(Message.DONE, self());
		System.out.println("physic loop");
	}

	private boolean collisionObjects(Node n) {
		// float distance = 0;
		// float radiuses = 0;
		// // System.out.println("geht das hier überhaupt rein??????????");
		// for (Node node : nodes.values()) {
		// if(!node.equals(n)){
		// //
		// System.out.println("HUUUUUUUUUUUUUUUHHHHHHHHHUUUUUUUUUUUUUUUUUUU");
		// System.out.println("Center n: " + ((Shape) n).getCenter());
		// distance = ((Shape) n).getCenter().sub(((Shape)
		// node).getCenter()).length();
		// radiuses = (((Shape) n).getRadius() + ((Shape) node).getRadius());
		// System.out.println("Radius n: " + ((Shape) n).getRadius() +
		// "Radius node: " + ((Shape) node).getRadius() + "distance1: " +
		// distance + "radiuses1: " + radiuses);
		// if(distance < radiuses){
		// // System.out.println("distance2: " + distance + "radiuses2: " +
		// radiuses);
		// return true;
		// }
		// }
		// }
		return false;
	}

	private boolean collisionGround(Node n) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onReceive(Object message) throws Exception {
		 System.out.println("STH STILL WORKING?" + nodes);
		if (message == Message.LOOP) {
			System.out.println("gibt es hier Nodes? " + nodes);
			physic();
			System.out.println("gibt es hier auch noch Nodes? " + nodes);
		} else if (message instanceof PhysicInitialization) {
			this.simulator = (((PhysicInitialization) message).simulator);
			initialize();
		} else if (message instanceof NodeCreation) {
			// System.out.println("PHHHHHYYYYYYYYYYYYYYYYYYYSSSSSSSSIIIIIICCC");

			if (((NodeCreation) message).type == Types.GROUP) {
				Node newNode = nodeFactory
						.groupNode(((NodeCreation) message).id);
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == Types.CUBE) {

				// System.out.println("Shadering cube with "
				// + ((NodeCreation) message).shader);

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
				if ((((NodeCreation) message).center != null)) {
					((Shape) newNode)
							.setCenter(((NodeCreation) message).center);
				}
				if ((((NodeCreation) message).radius != 0)) {
					((Shape) newNode)
							.setRadius(((NodeCreation) message).radius);
				}
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == Types.SPHERE) {

				Node newNode = nodeFactory.sphere(((NodeCreation) message).id,
						((NodeCreation) message).shader);

				if ((((NodeCreation) message).impulse != null)) {
					// TODO Masse einbauen
					newNode.setVelocity(((NodeCreation) message).impulse);
				}
				if ((((NodeCreation) message).modelmatrix != null)) {
					newNode.updateWorldTransform(((NodeCreation) message).modelmatrix);
				}
				if ((((NodeCreation) message).center != null)) {
					((Shape) newNode)
							.setCenter(((NodeCreation) message).center);
				}
				if ((((NodeCreation) message).radius != 0)) {
					((Shape) newNode)
							.setRadius(((NodeCreation) message).radius);
				}
				System.out.println("kommen wir noch bis hierher");
				nodes.put(newNode.id, newNode);
				System.out.println("Nodes leer? " + nodes.size());
			}
		} 
		else if (message instanceof NodeModification) {
			// System.out.println("NODEMODIFICATION!!!!!");
			if (nodes.containsKey(((NodeModification) message).id)) {
				// System.out.println("NodeModification");

				// System.out.println("Nodes " + nodes);
				// System.out.println("Accesing "
				// + ((NodeModification) message).id);

				Node modify = nodes.get(((NodeModification) message).id);

				System.out.println("get node" + modify.id);
				if (((NodeModification) message).localMod != null) {
					// modify.setLocalTransform(((NodeModification)
					// message).localMod);
					// modify.updateWorldTransform();
					modify.updateWorldTransform(((NodeModification) message).localMod);
				}

			}
		}
	}
}
