package app;

import static app.vecmathimp.FactoryDefault.vecmath;
import static app.nodes.NodeFactory.nodeFactory;

import java.util.Arrays;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import app.eventsystem.Types;
import app.messages.Message;
import app.messages.Mode;
import app.messages.SimulateType;
import app.nodes.GroupNode;
import app.nodes.shapes.Cube;
import app.nodes.shapes.Pipe;
import app.nodes.shapes.Plane;
import app.nodes.shapes.Sphere;
import app.vecmathimp.FactoryDefault;
import app.vecmathimp.VectorImp;

/**
 * Put your stuff here
 * 
 * @author Constantin
 * 
 */
public class App extends WorldState {

	/*-
	 * 0. Pick shader of choice // TODO 
	 * 1. Create a camera 
	 * 2. Create nodes 
	 * 3. Assign a starting node 
	 * 4. ??? 
	 * 5. Profit!
	 */
	@Override
	protected void initialize() {

		/**
		 * Note: After Creation add keys and physic before transform.
		 */
		setCamera(nodeFactory.camera("Cam"));
		transform(camera, FactoryDefault.vecmath.translationMatrix(0, 0, 3));

		GroupNode head = createGroup("head");
		setStart(head);

		System.out.println("Using shader " + shader);

		Cube c1 = createCube("Cube1", shader, 0.3f, 0.3f, 0.3f);
		append(c1, head);
		transform(c1, vecmath.translationMatrix(-1.5f, -1, 0));
//		addPhysic(c1, new VectorImp(6,6,6));


		Pipe c3 = createPipe("Pipe!", shader, 0, 1, 30);
//		transform(c3, vecmath.translationMatrix(-1.5f, -1, 0));
		append(c3, head);
		
		Sphere c4 = createSphere("Shpere!", shader);
		transform(c4, vecmath.translationMatrix(-1f, 1f, 0));
		
		append(c4, head);
		
		GroupNode g1 = createGroup("Group");
		append(g1, head);
		transform(g1, vecmath.translationMatrix(0, 1, 0));
		
		Cube c2 = createCube("Cube2", shader, 1.5f, 1.5f, 1.5f);
//		transform(c2, vecmath.translationMatrix(0.1f, 0, 0));
		transform(c2, vecmath.translationMatrix(1, 2, -5));
		simulateOnKey(c2, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_R)), SimulateType.ROTATE, Mode.TOGGLE, new VectorImp(1, 0, 0) ,Types.CUBE);
		append(c2, g1);
		
		Plane floor = createPlane("Floor", shader, 20, 20);
		transform(floor, vecmath.translationMatrix(0, -2f, 0));
		append(floor, g1);

	}

	public static void main(String[] args) {
		system = ActorSystem.create();
		system.actorOf(Props.create(App.class), "App").tell(Message.INIT,
				ActorRef.noSender());
	}
}