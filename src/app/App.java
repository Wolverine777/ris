package app;

import static app.nodes.NodeFactory.nodeFactory;
import static vecmath.vecmathimp.FactoryDefault.vecmath;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;

import vecmath.vecmathimp.FactoryDefault;
import vecmath.vecmathimp.VectorImp;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import app.Types.KeyMode;
import app.Types.ObjectTypes;
import app.Types.SimulateType;
import app.messages.Message;
import app.nodes.GroupNode;
import app.nodes.shapes.*;

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
//		Cube c1 = createCube("Cube1", shader, 1f);
////		Cube c1 = createCube("Cube1", shader, 0.5f, 0.3f, 0.3f);
//		append(c1, head);
////		transform(c1, vecmath.scaleMatrix(2, 2, 2));
////		transform(c1, vecmath.translationMatrix(-1, 0.5f, 0));
////		transform(c1, vecmath.translationMatrix(-1, 0.5f, 0));
////		transform(c1, vecmath.scaleMatrix(2, 2, 2));
////		transform(c1, vecmath.translationMatrix(1.5f, -1, 0));
//		simulateOnKey(c1, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_W)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0, 0) ,ObjectTypes.CUBE);
//		simulateOnKey(c1, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_E)), SimulateType.TRANSLATE, KeyMode.DOWN, new VectorImp(-0.01f, 0, 0) ,ObjectTypes.CUBE);
////		addPhysic(c1, new VectorImp(0.00001f,0,0));

		
		GroupNode g1 = createGroup("group");
		append(g1,head);
//		transform(g1, vecmath.translationMatrix(0, -1, 0));

		announceFloor(floor);
		addPhysicFloor(floor);
		append(floor, g1);
		
		Canon canon = createCanon("Canon", shader, new File("obj/Cannon2.obj"), null, 1.0f);
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_T)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 0f, 1f) ,ObjectTypes.OBJECT);
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_Z)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 1f, 0f) ,ObjectTypes.OBJECT);
		append(canon, head);
		
//		Cube c2 = createCube("Cube2", shader, 1.5f, 1.5f, 1.5f, 1f);
////		transform(c2, vecmath.translationMatrix(-1.5f, 2, 0));
////		transform(c2, vecmath.scaleMatrix(2f, 2f, 0));
////		transform(c2, vecmath.translationMatrix(-2f, 2, 0));
//		simulateOnKey(c2, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_R)), SimulateType.TRANSLATE, KeyMode.TOGGLE, new VectorImp(0.01f, 0, 0) ,ObjectTypes.CUBE);
//		simulateOnKey(c2, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_E)), SimulateType.TRANSLATE, KeyMode.DOWN, new VectorImp(-0.01f, 0, 0) ,ObjectTypes.CUBE);
//		simulateOnKey(c2, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_D)), SimulateType.TRANSLATE, KeyMode.DOWN, new VectorImp(0.0f, 0.01f, 0) ,ObjectTypes.CUBE);
//		simulateOnKey(c2, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_W)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0, 0) ,ObjectTypes.CUBE);
////		transform(c2, vecmath.translationMatrix(1, 0, 0));
//		append(c2, g1);
//
//		Pipe c3 = createPipe("Pipe!", shader, 0, 1, 30, 1f);
////		transform(c3, vecmath.translationMatrix(-1.5f, -1, 0));
//		append(c3, head);
		
//		Sphere c4 = createSphere("Shpere!", shader, 1f);
//		transform(c4, vecmath.translationMatrix(-5f, 1f, 0));
//		addPhysic(c4, new VectorImp(0.01f,0.01f,0));
//		append(c4, head);
		
//		Sphere c8 = createSphere("Shpere3", shader, 3f);
//		transform(c8, vecmath.translationMatrix(-5f, 0.5f, 0));
//		addPhysic(c8, new VectorImp(0.0f,0.01f,0));
//		append(c8, head);
		
		
//		Sphere c5 = createSphere("Shpere2", shader, 1f);
//		transform(c5, vecmath.translationMatrix(5f, 3f, 0));
//		addPhysic(c5, new VectorImp(0.0f,0.00f,0));
//		append(c5, head);
//
//		
//		Cube c6 = createCube("coinbla", shader, 1f);
//		transform(c6, vecmath.translationMatrix(0.6f,0,0.6f));
//		addToAi(c6);
//		append(c6, head);
//		
//		Sphere c7 = createSphere("car", shader, 1f);
//		transform(c7, vecmath.translationMatrix(0.2f, 0, 1f));
//		addToAi(c7);
//		append(c7, head);
		
		
		
//		ObjLoader testObj=createObject("ObjCube", shader, new File("obj/Cannon2.obj"), null, 1f);
//		transform(testObj, vecmath.translationMatrix(6f, 0f, 0f));
//		transform(testObj, vecmath.scaleMatrix(0.01f, 0.01f, 0.01f));
//		transform(testObj, vecmath.translationMatrix(-3f, -2f, 5f));
//		transform(testObj, vecmath.translationMatrix(-8f, 0f, 0f));
//		simulateOnKey(testObj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_T)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 0f, 1f) ,ObjectTypes.OBJECT);
//		simulateOnKey(testObj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_Z)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 1f, 0f) ,ObjectTypes.OBJECT);
//		append(testObj, head);
		
//		ObjLoader sphere=createObject("objSphere", shader, new File("obj/Sphere.obj"), null, 1f);
//		transform(sphere, vecmath.translationMatrix(4f, 0f, 0f));
//		simulateOnKey(sphere, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_DOWN)), SimulateType.TRANSLATE, KeyMode.DOWN, new VectorImp(0.0f, 0.0f, 0.1f) ,ObjectTypes.CUBE);
//		simulateOnKey(sphere, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_P)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0, 0) ,ObjectTypes.CUBE);
//		append(sphere, head);
		
		Car car=createCar("Car1", shader, new File("obj/cube.obj"), 0.2f, 1f);
		transform(car, vecmath.scaleMatrix(0.5f, 0.5f, 0.5f));
		transform(car,  vecmath.translationMatrix(-0.5f, floor.getGround(), -0.5f));
		append(car, head);
		
		Coin coin=createCoin("Coin1", shader, new File("obj/cube.obj"), 1f);
		transform(coin, vecmath.scaleMatrix(0.5f, 0.2f, 0.5f));
		transform(coin,  vecmath.translationMatrix(0.5f, floor.getGround(), 0));
		append(coin, head);

		ObjLoader objsphere=createObject("objSphere2", shader, new File("obj/Sphere.obj"), null, 1f);
		transform(objsphere, vecmath.translationMatrix(4f, 0f, 0f));
		addPhysic(objsphere, new VectorImp(0.0f,0.00f,0));
		append(objsphere, head);
		
		doCanonBalls();
	}

	public static void main(String[] args) {
		system = ActorSystem.create();
		system.actorOf(Props.create(App.class), "App").tell(Message.INIT,
				ActorRef.noSender());
	}
}