package app;

import static app.nodes.NodeFactory.nodeFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import akka.actor.UntypedActor;
import app.datatype.Route;
import app.eventsystem.Level;
import app.eventsystem.LevelCreation;
import app.eventsystem.LevelNode;
import app.eventsystem.NodeCreation;
import app.eventsystem.SimulateCreation;
import app.eventsystem.Types;
import app.messages.Message;
import app.nodes.Node;
import app.nodes.shapes.Cube;
import app.nodes.shapes.Shape;
import app.vecmathimp.VectorImp;



public class Ai extends UntypedActor {

	Level level;
	private Map<String, Node> nodes = new HashMap<String, Node>();
	private Route perfectway;

	private void initialize() {

		getSender().tell(Message.INITIALIZED, self());

	}

	private Route aStar(LevelNode target) {
		return null;

	}

	private VectorImp findClosestCoin(Node n) {
		float distance = 1000000;
		float tempdistance = 0;
		Node nearest = null;
		for (Node node : nodes.values()) {
			if(node instanceof Cube){
				tempdistance = node.getWorldTransform().getPosition().sub(n.getWorldTransform().getPosition()).length();
				if(tempdistance < distance){
				distance = tempdistance;
				nearest = node;
				}
			}
		}
		VectorImp closestlevelnode= new VectorImp(level.getNearestinLevel(nearest.getWorldTransform().getPosition()).x(), level.getNearestinLevel(nearest.getWorldTransform().getPosition()).y(), level.getNearestinLevel(nearest.getWorldTransform().getPosition()).z());
		return closestlevelnode;
	}

	private void aiLoop() {

		System.out.println("Level size: " + level.getLevelPoints().size());
		System.out.println(level.toString());
		for (Node n : nodes.values()) {
			System.out.println("NodeAI: " + n.id);
			VectorImp closest = new VectorImp(level.getNearestinLevel(n.getWorldTransform().getPosition()).x(),level.getNearestinLevel(n.getWorldTransform().getPosition()).y(),level.getNearestinLevel(n.getWorldTransform().getPosition()).z());
			System.out.println("Nearest is: " + closest.toString());
		}
		getSender().tell(Message.DONE, self());

	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.LOOP) {
			System.out.println("ai loop");
			aiLoop();
		} else if (message == Message.INIT) {
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
			}
		} else if (message instanceof LevelCreation) {
			LevelCreation lc = (LevelCreation) message;
			level = new Level(lc.position, lc.width, lc.height, lc.depth);
		}

	}

}
