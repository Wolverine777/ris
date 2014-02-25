package app;

import static app.vecmathimp.FactoryDefault.vecmath;
import static app.nodes.NodeFactory.nodeFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import app.eventsystem.Level;
import app.eventsystem.Types;
import app.messages.Message;
import app.messages.Mode;
import app.messages.SimulateType;
import app.nodes.GroupNode;
import app.nodes.shapes.Cube;
import app.nodes.shapes.ObjLoader;
import app.nodes.shapes.Pipe;
import app.nodes.shapes.Plane;
import app.nodes.shapes.Sphere;
import app.vecmathimp.FactoryDefault;
import app.vecmathimp.VectorImp;

/**
 * Put your stuff here
 * 
 * @author Constantin, Benjamin, Fabian
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
		transform(camera, FactoryDefault.vecmath.translationMatrix(0, 0, 10));

		GroupNode head = createGroup("head");
		setStart(head);
		Cube c1 = createCube("Cube1", shader, 1f);
//		Cube c1 = createCube("Cube1", shader, 0.5f, 0.3f, 0.3f);
		append(c1, head);
//		transform(c1, vecmath.scaleMatrix(2, 2, 2));
//		transform(c1, vecmath.translationMatrix(-1, 0.5f, 0));
//		transform(c1, vecmath.translationMatrix(-1, 0.5f, 0));
//		transform(c1, vecmath.scaleMatrix(2, 2, 2));
//		transform(c1, vecmath.translationMatrix(1.5f, -1, 0));
		simulateOnKey(c1, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_W)), SimulateType.ROTATE, Mode.DOWN, new VectorImp(1f, 0, 0) ,Types.CUBE);
		simulateOnKey(c1, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_E)), SimulateType.TRANSLATE, Mode.DOWN, new VectorImp(-0.01f, 0, 0) ,Types.CUBE);
//		addPhysic(c1, new VectorImp(0.00001f,0,0));

		GroupNode g1 = createGroup("group");
		append(g1,head);
//		transform(g1, vecmath.translationMatrix(0, -1, 0));
		
		Cube c2 = createCube("Cube2", shader, 1.5f, 1.5f, 1.5f, 1f);
//		transform(c2, vecmath.translationMatrix(-1.5f, 2, 0));
//		transform(c2, vecmath.scaleMatrix(2f, 2f, 0));
//		transform(c2, vecmath.translationMatrix(-2f, 2, 0));
		simulateOnKey(c2, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_R)), SimulateType.TRANSLATE, Mode.TOGGLE, new VectorImp(0.01f, 0, 0) ,Types.CUBE);
		simulateOnKey(c2, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_E)), SimulateType.TRANSLATE, Mode.DOWN, new VectorImp(-0.01f, 0, 0) ,Types.CUBE);
		simulateOnKey(c2, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_D)), SimulateType.TRANSLATE, Mode.DOWN, new VectorImp(0.0f, 0.01f, 0) ,Types.CUBE);
		simulateOnKey(c2, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_W)), SimulateType.ROTATE, Mode.DOWN, new VectorImp(1f, 0, 0) ,Types.CUBE);
//		transform(c2, vecmath.translationMatrix(1, 0, 0));
		append(c2, g1);

		Pipe c3 = createPipe("Pipe!", shader, 0, 1, 30, 1f);
//		transform(c3, vecmath.translationMatrix(-1.5f, -1, 0));
		append(c3, head);
		
//		Sphere c4 = createSphere("Shpere!", shader, 1f);
//		transform(c4, vecmath.translationMatrix(-5f, 1f, 0));
//		addPhysic(c4, new VectorImp(0.01f,0.01f,0));
//		append(c4, head);
		
//		Sphere c6 = createSphere("Shpere3", shader, 1f);
//		transform(c6, vecmath.translationMatrix(5f, 1f, 0));
//		addPhysic(c6, new VectorImp(-0.01f,0.01f,0));
//		append(c6, head);
		
		
		Sphere c5 = createSphere("Shpere2", shader, 1f);
		transform(c5, vecmath.translationMatrix(5f, 4f, 0));
		addPhysic(c5, new VectorImp(0,-0.03f,0));
		append(c5, head);
		
		Plane floor = createPlane("Floor", shader, 2, 2, 1f);
//		transform(floor, vecmath.translationMatrix(0, -2f, 0));
		addPhysicFloor(floor);
		append(floor, g1);
//		Level level=new Level(floor.getWorldTransform().getPosition(), floor.w2*2, 0, floor.d2*2);
		sendLevelAi(floor);
//		System.out.println(level.toString());
		
		Cube c6 = createCube("Cube6", shader, 1f);
		transform(c6, vecmath.translationMatrix(0.51f,0,0.51f));
		addAi(c6);
		
		ObjLoader testObj=createObject("ObjCube", shader, new File("obj/Cannon2.obj"), null, 1f);
//		transform(testObj, vecmath.translationMatrix(6f, 0f, 0f));
//		transform(testObj, vecmath.scaleMatrix(0.01f, 0.01f, 0.01f));
		transform(testObj, vecmath.translationMatrix(-3f, -2f, 5f));
//		transform(testObj, vecmath.translationMatrix(-8f, 0f, 0f));
		simulateOnKey(testObj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_T)), SimulateType.ROTATE, Mode.DOWN, new VectorImp(0f, 0f, 1f) ,Types.OBJECT);
		simulateOnKey(testObj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_Z)), SimulateType.ROTATE, Mode.DOWN, new VectorImp(0f, 1f, 0f) ,Types.OBJECT);
		append(testObj, head);
		
		ObjLoader sphere=createObject("objSphere", shader, new File("obj/Sphere.obj"), null, 1f);
		transform(sphere, vecmath.translationMatrix(4f, 0f, 0f));
		simulateOnKey(sphere, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_DOWN)), SimulateType.TRANSLATE, Mode.DOWN, new VectorImp(0.0f, 0.0f, 0.1f) ,Types.CUBE);
		simulateOnKey(sphere, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_P)), SimulateType.ROTATE, Mode.DOWN, new VectorImp(1f, 0, 0) ,Types.CUBE);
		append(sphere, head);
	}

	public static void main(String[] args) {
		system = ActorSystem.create();
		system.actorOf(Props.create(App.class), "App").tell(Message.INIT,
				ActorRef.noSender());
	}
}