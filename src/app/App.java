package app;

import static app.nodes.NodeFactory.nodeFactory;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static vecmath.vecmathimp.FactoryDefault.vecmath;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import com.leapmotion.leap.Controller;
import vecmath.Matrix;
import vecmath.vecmathimp.FactoryDefault;
import vecmath.vecmathimp.VectorImp;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import app.Types.GestureType;
import app.Types.KeyMode;
import app.Types.ObjectTypes;
import app.Types.PhysicType;
import app.Types.SimulateType;
import app.datatype.FontInfo;
import app.messages.Message;
import app.nodes.GroupNode;
import app.nodes.Sun;
import app.nodes.Text;
import app.nodes.shapes.*;
import app.shader.Shader;

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
		 * For Ai at first init Cars
		 */
		setCamera(nodeFactory.camera("Cam", new LinkedList<Shader>(Arrays.asList(new Shader[]{shader,texShader}))));
		transform(camera, FactoryDefault.vecmath.translationMatrix(0, floor.getHight()+floor.getD()*0.2f, (floor.getD()*0.86f)));

		GroupNode start = createGroup("start");
		setStart(start);
		Sun sun=createSun("Sun", camera.getWorldTransform(), texShader);
		GroupNode head = createGroup("head");
		append(head, start);
		append(sun, start);
		
		
		announceFloor(floor);
		append(floor, head);
		
//		test(head);
		finalLevel(head);
//		obj(head);

	}
	
	private void obj(GroupNode head){
		Matrix m=vecmath.translationMatrix(0, -2, 0);
		m=m.mult(vecmath.scaleMatrix(0.0006f, 0.0006f, 0.0006f));
//		ObjLoader obj=createObject("hamvee", texShader, new File("obj/HQ_Movie cycle.obj"), new File("obj/2.jpg"), 1f, null, null);
//		ObjLoader obj=createObject("hamvee", texShader, new File("obj/apc.obj"), new File("obj/2.jpg"), 1f, null, null);
		ObjLoader obj=createObject("hamvee", texShader, new File("obj/ATV.obj"), new File("obj/3.jpg"), m, 1f, null, null);
//		ObjLoader obj=createObject("hamvee", shader, new File("obj/ATV.obj"), null, 1f, null, null);
		simulateOnKey(obj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_RIGHT)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 1f, 0f));
		simulateOnKey(obj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_LEFT)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, -1f, 0f));
		simulateOnKey(obj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_UP)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0f, 0f));
		simulateOnKey(obj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_DOWN)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(-1f, 0f, 0f));
//		transform(obj, vecmath.scaleMatrix(0.0006f, 0.0006f, 0.0006f));
//		transform(obj, vecmath.translationMatrix(0, -2, 0));
		append(obj, head);
		
		float tree = 0.0009f;
		Matrix scaletree=vecmath.scaleMatrix(tree, tree, tree);
		
		ObjLoader objsphere3=createObject("objSphere3", texShader, new File("obj/1.obj"),  null, vecmath.translationMatrix(0, 0, 0).mult(scaletree), 1f , null, PhysicType.Collision_only);
		System.out.println("objSphere3" + objsphere3.getCenter());
		append(objsphere3, head);
		
		float apple = 0.009f;
		Matrix scaleapple=vecmath.scaleMatrix(apple, apple, apple);
		ObjLoader objsphere4=createObject("objSphere4", texShader, new File("obj/apple.obj"),  new File("obj/skin.jpg"), vecmath.translationMatrix(5,0,5).mult(scaleapple), 1f , null, PhysicType.Collision_only);
		System.out.println("objSphere4" + objsphere4.getWorldTransform().getPosition());
		append(objsphere4, head);
	}
	
	private void finalLevel(GroupNode head){
//		Matrix m=vecmath.translationMatrix(-1, -2, 0);
		Matrix carScale=vecmath.scaleMatrix(0.0006f, 0.0006f, 0.0006f);
		Car car1=createCar("Car1", texShader, new File("obj/ATV.obj"), new File("obj/3.jpg"), 3.4, vecmath.translationMatrix(-2, -2, 8).mult(carScale), 1f, null, PhysicType.Collision_only);
//		transform(car, vecmath.scaleMatrix(0.0006f, 0.0006f, 0.0006f));
//		transform(car,  vecmath.translationMatrix(-1.0f, -2.0f, 0.0f));
		append(car1, head);
		
		Car car2=createCar("Car2", texShader, new File("obj/ATV.obj"), new File("obj/3.jpg"), 3.4, vecmath.translationMatrix(-5, -2, -3).mult(carScale), 1f, null, PhysicType.Collision_only);
//		transform(car, vecmath.scaleMatrix(0.0006f, 0.0006f, 0.0006f));
//		transform(car,  vecmath.translationMatrix(-1.0f, -2.0f, 0.0f));
		append(car2, head);
		
		Canon canon = createCanon("Canon", shader, new File("obj/Cannon2.obj"), /*new File("obj/2.jpg")*/null, vecmath.translationMatrix(0.0f, floor.getHight(), ((floor.getD()/2)*0.90f)).mult(vecmath.rotationMatrix(-1.0f, 0f, 0f, 45f)), 1.0f);
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_D)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, -0.7f, 0f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_A)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 0.7f, 0f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_W)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(-0.7f, 0f, 0f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_S)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0.7f, 0f, 0f));		
//		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_X)), SimulateType.ROTATE, KeyMode.TOGGLE, new VectorImp(0f, 1f, 0f));
		simulateOnGesture(canon, GestureType.HAND_POSITION, SimulateType.ROTATE, new VectorImp(1, 0, 0));
//		transform(canon, vecmath.translationMatrix(2.5f, 0.0f, 0.0f));
		append(canon, head);
		doCanonBalls();
		
		Matrix scaleCoin=vecmath.scaleMatrix(0.15f, 0.15f, 0.15f);
		Coin coin=createCoin("Coin1", shader, new File("obj/cube.obj"), vecmath.translationMatrix(8.0f, floor.getGround(), 8.0f).mult(scaleCoin), 1f, null, PhysicType.Collision_only);
//		transform(coin,  vecmath.translationMatrix(1.0f, floor.getGround(), 1.0f));
		append(coin, head);
		Coin coin2=createCoin("Coin2", shader, new File("obj/cube.obj"), vecmath.translationMatrix(3.5f, floor.getGround(), 0.0f).mult(scaleCoin), 1f, null, PhysicType.Collision_only);
//		transform(coin2,  vecmath.translationMatrix(0.5f, floor.getGround(), 0.0f));
		append(coin2, head);
		
		Coin coin3=createCoin("Coin3", shader, new File("obj/cube.obj"), vecmath.translationMatrix(0.0f, floor.getGround(), 0.0f).mult(scaleCoin), 1f, null, PhysicType.Collision_only);
//		transform(coin3,  vecmath.translationMatrix(0.0f, 1, 0));
		append(coin3, head);
		
		
		Cube block=createCube("tree", shader, 2.2f,0.8f, 2.2f, 1f, null, PhysicType.Collision_only);
		transform(block,  vecmath.translationMatrix(-1.0f, -2.0f, -1.0f));
		append(block, head);
		
		
		
		
		Text t=createText("Coins", "hi" , new FontInfo("Arial Bold", java.awt.Font.BOLD, 35));
		transform(t, vecmath.translationMatrix(200f, 1.0f, 0.0f));
		append(t, head);
		Text t2=createText("Cars", "hi" , new FontInfo("Arial Bold", java.awt.Font.BOLD, 35));
		append(t2, head);
		Text t3=createText("Balls", "hi" , new FontInfo("Arial Bold", java.awt.Font.BOLD, 35));
		transform(t3, vecmath.translationMatrix(400f, 1.0f, 0.0f));
		append(t3, head);
	}
	private void test(GroupNode head){
//		alSourcePlay(Renderer.source2);
		Canon canon = createCanon("Canon", shader, new File("obj/Cannon2.obj"), null, vecmath.translationMatrix(2.5f, 0.0f, 1.0f), 1.0f);
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_T)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 0f, 1f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_Z)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0f, 0f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_H)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(-1f, 0f, 0f));		
//		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_X)), SimulateType.ROTATE, KeyMode.TOGGLE, new VectorImp(0f, 1f, 0f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_U)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 0f, -1f));
		simulateOnGesture(canon, GestureType.HAND_POSITION, SimulateType.ROTATE, new VectorImp(0, 0, 0));
//		transform(canon, vecmath.translationMatrix(2.5f, 0.0f, 0.0f));
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
//		append(c4, head);
		
		Sphere c8 = createSphere("Shpere3", shader, vecmath.translationMatrix(-5.0f, 0.5f, 0.0f), 3f,new VectorImp(0.0f,0.1f,0), PhysicType.Physic_complete);
		append(c8, head);
		
		
//		Sphere c5 = createSphere("Shpere2", shader, 1f);
//		transform(c5, vecmath.translationMatrix(5f, 3f, 0));
//		append(c5, head);
		
//		ObjLoader sphere=createObject("objSphere", shader, new File("obj/Sphere.obj"), null, 1f);
//		transform(sphere, vecmath.translationMatrix(4f, 0f, 0f));
//		simulateOnKey(sphere, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_DOWN)), SimulateType.TRANSLATE, KeyMode.DOWN, new VectorImp(0.0f, 0.0f, 0.1f) ,ObjectTypes.CUBE);
//		simulateOnKey(sphere, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_P)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0, 0) ,ObjectTypes.CUBE);
//		append(sphere, head);
		
		Matrix m=vecmath.translationMatrix(-1, -2, 0);
		m=m.mult(vecmath.scaleMatrix(0.0006f, 0.0006f, 0.0006f));
//		m=m.mult(vecmath.scaleMatrix(0.35f, 0.35f, 0.35f));
//		Car car=createCar("Car1", shader, new File("obj/cube.obj"), null, 1.4, m, 1f, null, PhysicType.Collision_only);
		Car car=createCar("Car1", texShader, new File("obj/ATV.obj"), new File("obj/3.jpg"), 1.4, m, 1f, null, PhysicType.Collision_only);
//		transform(car, vecmath.scaleMatrix(0.35f, 0.35f, 0.35f));
//		transform(car,  vecmath.translationMatrix(-1.0f, -1.65f, 0.0f));
		append(car, head);
		
		Matrix scaleCoin=vecmath.scaleMatrix(0.15f, 0.15f, 0.15f);
		Coin coin=createCoin("Coin1", shader, new File("obj/cube.obj"), vecmath.translationMatrix(1.0f, floor.getGround(), 1.0f).mult(scaleCoin), 1f, null, PhysicType.Collision_only);
//		transform(coin,  vecmath.translationMatrix(1.0f, floor.getGround(), 1.0f));
		append(coin, head);
		Coin coin2=createCoin("Coin2", shader, new File("obj/cube.obj"), vecmath.translationMatrix(0.5f, floor.getGround(), 0.0f).mult(scaleCoin), 1f, null, PhysicType.Collision_only);
//		transform(coin2,  vecmath.translationMatrix(0.5f, floor.getGround(), 0.0f));
		append(coin2, head);
		
		Coin coin3=createCoin("Coin3", shader, new File("obj/cube.obj"), vecmath.translationMatrix(0.0f, floor.getGround(), 0.0f).mult(scaleCoin), 1f, null, PhysicType.Collision_only);
//		transform(coin3,  vecmath.translationMatrix(0.0f, 1, 0));
		append(coin3, head);
		
//		Cube block=createCube("tree", shader, 0.2f,0.8f, 0.2f, 1f, null, null);
//		transform(block,  vecmath.translationMatrix(-1.0f, -2.0f, -1.0f));
//		append(block, head);
		

		
		doCanonBalls();
		
	}

	public static void main(String[] args) {
		system = ActorSystem.create();
		system.actorOf(Props.create(App.class), "App").tell(Message.INIT,
				ActorRef.noSender());
	}
}