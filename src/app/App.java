package app;

import static app.nodes.NodeFactory.nodeFactory;
import static vecmath.vecmathimp.FactoryDefault.vecmath;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import vecmath.Matrix;
import vecmath.vecmathimp.FactoryDefault;
import vecmath.vecmathimp.VectorImp;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import app.Types.GestureType;
import app.Types.KeyMode;
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
 * @author Benjamin, Fabian
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
		Matrix m=vecmath.scaleMatrix(0.007f, 0.007f, 0.007f);
//		Matrix tankApc=vecmath.scaleMatrix(0.3f, 0.3f, 0.3f).mult(vecmath.rotationMatrix(0, 1, 0, -60).mult((vecmath.rotationMatrix(1f, 0.0f, 0.0f, -90))));
//		ObjLoader obj=createObject("tank", shader, new File("obj/apc.obj"), null/*new File("obj/3.jpg")*/, m.mult(tankApc), 1f, null, null);
		ObjLoader obj=createObject("fuelTruck", shader, new File("obj/airport_fuel_truck.obj"), null, vecmath.translationMatrix(-3.0f, floor.getGround(), 0.0f).mult(m), 1f, null, null);
//		ObjLoader obj=createObject("hamvee", texShader, new File("obj/apc.obj"), new File("obj/2.jpg"), 1f, null, null);
//		ObjLoader obj=createObject("hamvee", shader, new File("obj/ATV.obj"), null, 1f, null, null);
		simulateOnKey(obj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_RIGHT)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 1f, 0f));
		simulateOnKey(obj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_LEFT)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, -1f, 0f));
		simulateOnKey(obj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_UP)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0f, 0f));
		simulateOnKey(obj, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_DOWN)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(-1f, 0f, 0f));
//		transform(obj, vecmath.scaleMatrix(0.0006f, 0.0006f, 0.0006f));
//		transform(obj, vecmath.translationMatrix(0, -2, 0));
//		append(obj, head);
		
		Matrix scaleVan=vecmath.scaleMatrix(0.008f, 0.008f, 0.008f).mult(vecmath.rotationMatrix(0, 1.0f, 0, 135));
		ObjLoader obj1=createObject("van", shader, new File("obj/Van.obj"), null, vecmath.translationMatrix(0.0f, floor.getGround(), 0.0f).mult(scaleVan), 1f, null, null);
		simulateOnKey(obj1, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_D)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 1f, 0f));
		simulateOnKey(obj1, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_A)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, -1f, 0f));
		simulateOnKey(obj1, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_W)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0f, 0f));
		simulateOnKey(obj1, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_S)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(-1f, 0f, 0f));
//		append(obj1, head);
		
		Matrix scale=vecmath.scaleMatrix(8.0f, 8.0f, 8.0f);
		ObjLoader cycle=createObject("cycle", shader, new File("obj/cb750f.obj"), null, vecmath.translationMatrix(0.0f, floor.getGround()+0.2f, 0.0f).mult(scale), 1f, null, null);
		simulateOnKey(cycle, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_D)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 1f, 0f));
		simulateOnKey(cycle, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_A)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, -1f, 0f));
		simulateOnKey(cycle, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_W)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(1f, 0f, 0f));
		simulateOnKey(cycle, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_S)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(-1f, 0f, 0f));
		append(cycle, head);
		
		Cube c=createCube("ref", shader, 1);
		append(c, head);
		
		float tree = 0.0009f;
		Matrix scaleHouse=vecmath.scaleMatrix(tree, tree, tree);
		ObjLoader house=createObject("House", texShader, new File("obj/1.obj"),  null, vecmath.translationMatrix(0, 0, 0).mult(scaleHouse), 1f , null, PhysicType.Collision_only);
		System.out.println("objSphere3" + house.getCenter());
		append(house, head);
		
//		float apple = 0.009f;
//		Matrix scaleapple=vecmath.scaleMatrix(apple, apple, apple);
//		ObjLoader objsphere4=createObject("objSphere4", texShader, new File("obj/apple.obj"),  new File("obj/skin.jpg"), vecmath.translationMatrix(5,0,5).mult(scaleapple), 1f , null, PhysicType.Collision_only);
//		System.out.println("objSphere4" + objsphere4.getWorldTransform().getPosition());
//		append(objsphere4, head);
	}
	
	private void finalLevel(GroupNode head){
//		Matrix m=vecmath.translationMatrix(-1, -2, 0);
		Matrix carScale=vecmath.scaleMatrix(0.0006f, 0.0006f, 0.0006f);
		Matrix scale=vecmath.scaleMatrix(8.0f, 8.0f, 8.0f);
		double leap=1;
		if(LEAP)leap=0.4f;
		Car car1=createCar("Car1", texShader, new File("obj/ATV.obj"), new File("obj/3.jpg"), 3.9*leap, vecmath.translationMatrix(4.0f, floor.getGround(), 1.5f).mult(carScale), 1f, null, PhysicType.Collision_only);
		append(car1, head);
		
//		Car car2=createCar("Car2", shader, new File("obj/cb750f.obj"), null, 1.1*leap, /*vecmath.translationMatrix(5.0f, floor.getGround(), 2.0f).mult(scaleVan)*/vecmath.identityMatrix(), 1f, null, PhysicType.Collision_only);
//		transform(car2, scale);
//		transform(car2, vecmath.translationMatrix(5.0f, floor.getGround()+0.2f, 2.0f));
//		append(car2, head);
		
		Car car3=createCar("Car3", texShader, new File("obj/ATV.obj"), new File("obj/3.jpg"), 2.7*leap, vecmath.translationMatrix(-3.0f, floor.getGround(), -6.0f).mult(carScale), 1f, null, PhysicType.Collision_only);
		append(car3, head);
		
		Canon canon = createCanon("Canon", shader, new File("obj/Cannon2.obj"), null, vecmath.translationMatrix(0.0f, floor.getHight(), ((floor.getD()/2)*0.90f)).mult(vecmath.rotationMatrix(-1.0f, 0f, 0f, 45f)), 1.0f);
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_D)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, -0.55f, 0f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_A)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0f, 0.55f, 0f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_W)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(-0.55f, 0f, 0f));
		simulateOnKey(canon, new HashSet<Integer>(Arrays.asList(Keyboard.KEY_S)), SimulateType.ROTATE, KeyMode.DOWN, new VectorImp(0.55f, 0f, 0f));		
		simulateOnGesture(canon, GestureType.HAND_POSITION, SimulateType.ROTATE, new VectorImp(1, 0, 0));
		append(canon, head);
		doCanonBalls();
		
		float apple = 0.0075f;
		Matrix scaleapple=vecmath.scaleMatrix(apple, apple, apple);
		
		ObjLoader appleObj1=createCoin("apple1", texShader, new File("obj/apple.obj"),  new File("obj/skin.jpg"), vecmath.translationMatrix(6.0f,floor.getGround(),-8.5f).mult(scaleapple), 1f , null, PhysicType.Collision_only);
		append(appleObj1, head);
		
		ObjLoader appleObj2=createCoin("apple2", texShader, new File("obj/apple.obj"),  new File("obj/skin.jpg"), vecmath.translationMatrix(9.0f,floor.getGround(),6.5f).mult(scaleapple), 1f , null, PhysicType.Collision_only);
		append(appleObj2, head);
		
		ObjLoader appleObj3=createCoin("apple3", texShader, new File("obj/apple.obj"),  new File("obj/skin.jpg"), vecmath.translationMatrix(-8.5f,floor.getGround(),2.0f).mult(scaleapple), 1f , null, PhysicType.Collision_only);
		append(appleObj3, head);
		
		ObjLoader appleObj4=createCoin("apple4", texShader, new File("obj/apple.obj"),  new File("obj/skin.jpg"), vecmath.translationMatrix(-9.5f,floor.getGround(),-7.0f).mult(scaleapple), 1f , null, PhysicType.Collision_only);
		append(appleObj4, head);
		
		ObjLoader appleObj5=createCoin("apple5", texShader, new File("obj/apple.obj"),  new File("obj/skin.jpg"), vecmath.translationMatrix(1.0f,floor.getGround(),0.5f).mult(scaleapple), 1f , null, PhysicType.Collision_only);
		append(appleObj5, head);
		
		
		
		float block = 0.0016f;
		Matrix scaleHouse=vecmath.scaleMatrix(block, block, block);
		ObjLoader house=createObject("House", shader, new File("obj/house1.obj"),  null, vecmath.translationMatrix(-3.0f, floor.getGround(), -2.5f).mult(scaleHouse), 1f , null, PhysicType.Collision_only);
		append(house, head);
		ObjLoader appleObj0=createCoin("apple0", texShader, new File("obj/apple.obj"),  new File("obj/skin.jpg"), vecmath.translationMatrix(-3.5f,floor.getGround(),-3.5f).mult(scaleapple), 1f , null, PhysicType.Collision_only);
		append(appleObj0, head);
		
		ObjLoader house1=createObject("House1", shader, new File("obj/house1.obj"),  null, vecmath.translationMatrix(3.0f, floor.getGround(), -6.5f).mult(scaleHouse), 1f , null, PhysicType.Collision_only);
		append(house1, head);
		
		ObjLoader house2=createObject("House2", shader, new File("obj/house1.obj"),  null, vecmath.translationMatrix(-4.5f, floor.getGround(), 5.5f).mult(scaleHouse), 1f , null, PhysicType.Collision_only);
		append(house2, head);
		
		ObjLoader house3=createObject("House3", shader, new File("obj/house1.obj"),  null, vecmath.translationMatrix(6.5f, floor.getGround(), 7.5f).mult(scaleHouse), 1f , null, PhysicType.Collision_only);
		append(house3, head);
		
		
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
		Coin coin=createCoin("Coin1", shader, new File("obj/cube.obj"), null, vecmath.translationMatrix(1.0f, floor.getGround(), 1.0f).mult(scaleCoin), 1f, null, PhysicType.Collision_only);
//		transform(coin,  vecmath.translationMatrix(1.0f, floor.getGround(), 1.0f));
		append(coin, head);
		Coin coin2=createCoin("Coin2", shader, new File("obj/cube.obj"), null, vecmath.translationMatrix(0.5f, floor.getGround(), 0.0f).mult(scaleCoin), 1f, null, PhysicType.Collision_only);
//		transform(coin2,  vecmath.translationMatrix(0.5f, floor.getGround(), 0.0f));
		append(coin2, head);
		
		Coin coin3=createCoin("Coin3", shader, new File("obj/cube.obj"), null, vecmath.translationMatrix(0.0f, floor.getGround(), 0.0f).mult(scaleCoin), 1f, null, PhysicType.Collision_only);
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