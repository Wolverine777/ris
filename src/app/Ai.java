package app;

import static app.nodes.NodeFactory.nodeFactory;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import app.datatype.Route;
import app.eventsystem.Level;
import app.eventsystem.LevelCreation;
import app.eventsystem.LevelNode;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeModification;
import app.eventsystem.SimulateCreation;
import app.eventsystem.Types;
import app.messages.Message;
import app.messages.PhysicInitialization;
import app.messages.SimulateType;
import app.messages.SingelSimulation;
import app.nodes.Node;
import app.nodes.shapes.Cube;
import app.nodes.shapes.Shape;
import app.vecmath.Vector;
import app.vecmathimp.VectorImp;

public class Ai extends UntypedActor {

	Level level;
	private Map<String, Node> nodes = new HashMap<String, Node>();
	private Route perfectway;
	ActorRef simulator;

	private void initialize() {

		getSender().tell(Message.INITIALIZED, self());

	}

	private Route aStar(LevelNode target) {
		return null;

	}

	private VectorImp findClosestCoin(Node car) {
		float distance = -1;
		Node nearest = null;
		for (Node node : nodes.values()) {
			if (node instanceof Cube) {
				System.out.println("Node(Cube) findclosest: " + node.id);
				float tempdistance = node.getWorldTransform().getPosition().sub(car.getWorldTransform().getPosition()).length();
				if (tempdistance < distance || distance<0) {
					distance = tempdistance;
					System.out.println("distance coin: " + distance);
					nearest = node;
				}
			}
		}
		VectorImp closestlevelnode = (VectorImp) getNearestNodeinLevel(nearest);
		System.out.println("closestlevelNode: " + closestlevelnode);
		return closestlevelnode;
	}

	private void aiLoop() {

//		System.out.println("Level size: " + level.getLevelPoints().size());
		System.out.println(level.toString());
		for (Node n : nodes.values()) {
			System.out.println("NodeAI: " + n.id);
//			findClosestCoin(n);
			VectorImp closest = (VectorImp) getNearestNodeinLevel(n);
			System.out.println("Nearest is: " + closest.toString());
		}
		getSender().tell(Message.DONE, self());

	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.LOOP) {
			System.out.println("ai loop");
			getSender().tell(Message.DONE, self());
			aiLoop();
		} else if (message instanceof PhysicInitialization) {
			this.simulator = (((PhysicInitialization) message).simulator);
			initialize();
		} else if (message instanceof NodeCreation) {

			if (((NodeCreation) message).type == Types.GROUP) {
				Node newNode = nodeFactory
						.groupNode(((NodeCreation) message).id);
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == Types.CUBE) {

				Node newNode = nodeFactory.cube(((NodeCreation) message).id,
						((NodeCreation) message).shader,
						((NodeCreation) message).w, ((NodeCreation) message).h,
						((NodeCreation) message).d,
						((NodeCreation) message).mass);
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
						((NodeCreation) message).shader,
						((NodeCreation) message).mass);
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
			}
		} else if (message instanceof LevelCreation) {
			LevelCreation lc = (LevelCreation) message;
			level = new Level(lc.position, lc.width, lc.height, lc.depth);
		} else if(message instanceof NodeModification){
			if(nodes.containsKey(((NodeModification) message).id)){
        		Node modify = nodes.get(((NodeModification) message).id);
        		if (((NodeModification) message).localMod != null){
        			modify.updateWorldTransform(((NodeModification) message).localMod);
        		}
        	}
		}

	}
	
	private Vector getNearestNodeinLevel(Node object){
		Vector nearestVec = level.getNearestinLevel(object.getWorldTransform().getPosition());
		Vector translate=(nearestVec.sub(object.getWorldTransform().getPosition()));
		if(!translate.equals(new VectorImp(0, 0, 0)))simulator.tell(new SingelSimulation(object.id, SimulateType.TRANSLATE, translate,object.getWorldTransform()), self());
		return nearestVec;
	}

}
