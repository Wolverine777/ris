package app;

import static app.nodes.NodeFactory.nodeFactory;
import static org.lwjgl.openal.AL10.alSourcePlay;
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
import app.Types.PhysicType;
import app.Types.SimulateType;
import app.datatype.FontInfo;
import app.messages.Message;
import app.nodes.GroupNode;
import app.nodes.Text;
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
		
		announceFloor(floor);
		addPhysicFloor(floor);
		append(floor, head);
		test(head);

	}
	
	private void finalLevel(GroupNode head){
		
	}
	private void test(GroupNode head){
		alSourcePlay(Renderer.source2);
		Canon canon = createCanon("Canon", shader, new File("obj/Cannon2.obj"), null, 1.0f);
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_T)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 0f, 1f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_Z)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0f, 0f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_H)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(-1f, 0f, 0f));		
//		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_X)), SimulateType.ROTATE, KeyMode.TOGGLE, new VectorImp(0f, 1f, 0f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_U)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 0f, -1f));
		transform(canon, vecmath.translationMatrix(2.5f, 0.0f, 0.0f));
		append(canon, head);
		
		Text t=createText("Coins", "hi" , new FontInfo("Arial Bold", java.awt.Font.BOLD, 12));
		transform(t, vecmath.translationMatrix(200f, 1.0f, 0.0f));
		append(t, head);
		
		Text t2=createText("Cars", "hi" , new FontInfo("Arial Bold", java.awt.Font.BOLD, 12));
		append(t2, head);
		
		Text t3=createText("Balls", "hi" , new FontInfo("Arial Bold", java.awt.Font.BOLD, 12));
		transform(t3, vecmath.translationMatrix(400f, 1.0f, 0.0f));
		append(t3, head);
		
//		Sphere c4 = createSphere("Shpere!", shader, 1f);
//		transform(c4, vecmath.translationMatrix(-5f, 1f, 0));
//		addPhysic(c4, new VectorImp(0.01f,0.01f,0));
//		append(c4, head);
		
		Sphere c8 = createSphere("Shpere3", shader, 3f,new VectorImp(0.0f,0.1f,0), PhysicType.Physic_complete);
		transform(c8, vecmath.translationMatrix(-5f, 0.5f, 0));
//		addPhysic(c8, new VectorImp(0.0f,0.01f,0));
		append(c8, head);
		
		
//		Sphere c5 = createSphere("Shpere2", shader, 1f);
//		transform(c5, vecmath.translationMatrix(5f, 3f, 0));
//		addPhysic(c5, new VectorImp(0.0f,0.00f,0));
//		append(c5, head);
		
//		ObjLoader sphere=createObject("objSphere", shader, new File("obj/Sphere.obj"), null, 1f);
//		transform(sphere, vecmath.translationMatrix(4f, 0f, 0f));
//		simulateOnKey(sphere, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_DOWN)), SimulateType.TRANSLATE, KeyMode.DOWN, new VectorImp(0.0f, 0.0f, 0.1f) ,ObjectTypes.CUBE);
//		simulateOnKey(sphere, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_P)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0, 0) ,ObjectTypes.CUBE);
//		append(sphere, head);
		
		Car car=createCar("Car1", shader, new File("obj/cube.obj"), 1.4, 1f, null, PhysicType.Collision_only);
		transform(car, vecmath.scaleMatrix(0.35f, 0.35f, 0.35f));
		transform(car,  vecmath.translationMatrix(-1.0f, -1.65f, 0.0f));
//		addPhysic(car, new VectorImp(0, 0, 0), PhysicType.Collision_only);
		append(car, head);
		
		Coin coin=createCoin("Coin1", shader, new File("obj/cube.obj"), 1f, null, PhysicType.Collision_only);
		transform(coin, vecmath.scaleMatrix(0.15f, 0.15f, 0.15f));
		transform(coin,  vecmath.translationMatrix(1.0f, floor.getGround(), 1.0f));
//		addPhysic(coin, new VectorImp(0, 0, 0), PhysicType.Collision_only);
		append(coin, head);
		Coin coin2=createCoin("Coin2", shader, new File("obj/cube.obj"), 1f, null, PhysicType.Collision_only);
		transform(coin2, vecmath.scaleMatrix(0.15f, 0.15f, 0.15f));
		transform(coin2,  vecmath.translationMatrix(0.5f, floor.getGround(), 0.0f));
//		addPhysic(coin2, new VectorImp(0, 0, 0), PhysicType.Collision_only);
		append(coin2, head);
		
//		Coin coin3=createCoin("Coin3", shader, new File("obj/cube.obj"), 1f, null, PhysicType.Collision_only);
//		transform(coin3, vecmath.scaleMatrix(0.15f, 0.15f, 0.15f));
//		transform(coin3,  vecmath.translationMatrix(0.0f, 1, 0));
////		addPhysic(coin3, new VectorImp(0, 0, 0), PhysicType.Collision_only);
//		append(coin3, head);
		
//		Cube block=createCube("tree", shader, 0.2f,0.8f, 0.2f, 1f, null, null);
//		transform(block,  vecmath.translationMatrix(-1.0f, -2.0f, -1.0f));
//		append(block, head);

		ObjLoader objsphere=createObject("objSphere2", shader, new File("obj/Sphere.obj"), null, 1f, null, PhysicType.Collision_only);
		transform(objsphere, vecmath.translationMatrix(5f, 0f, 0f));
//		addPhysic(objsphere, new VectorImp(0,0,0), PhysicType.Collision_only);
		append(objsphere, head);
		
		doCanonBalls();
	}

	public static void main(String[] args) {
		system = ActorSystem.create();
		system.actorOf(Props.create(App.class), "App").tell(Message.INIT,
				ActorRef.noSender());
	}
}