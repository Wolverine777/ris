package app;

import static app.nodes.NodeFactory.nodeFactory;
import static vecmath.vecmathimp.FactoryDefault.vecmath;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Keyboard;



import vecmath.Matrix;
import vecmath.Vector;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import app.Types.Events;
import app.Types.KeyMode;
import app.Types.ObjectTypes;
import app.Types.PhysicType;
import app.Types.SimulateType;
import app.edges.Edge;
import app.eventsystem.CameraCreation;
import app.eventsystem.FloorCreation;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeDeletion;
import app.eventsystem.NodeModification;
import app.eventsystem.SimulateCreation;
import app.eventsystem.StartNodeModification;
import app.messages.AiInitialization;
import app.messages.KeyState;
import app.messages.Message;
import app.messages.RegisterKeys;
import app.messages.PhysicInitialization;
import app.messages.RendererInitialization;
import app.messages.RendererInitialized;
import app.nodes.GroupNode;
import app.nodes.Node;
import app.nodes.camera.Camera;
import app.nodes.shapes.*;
import app.shader.Shader;
import app.toolkit.StopWatch;

/**
 * Technical base
 * 
 * @author Constantin, Benjamin, Fabian
 * 
 */
public abstract class WorldState extends UntypedActor{
	public static ActorSystem system;
	
	private Map<String, Node> nodes = new HashMap<String, Node>();

	private StopWatch time = new StopWatch();
	private Map<ActorRef, Boolean> unitState = new HashMap<ActorRef, Boolean>();
	private SetMultimap<Events, ActorRef> observers = HashMultimap.create();
	private ActorRef renderer;
	private ActorRef simulator;
	private ActorRef input;
	private ActorRef physic;
	private ActorRef ai;
	
	
	protected Node startNode;
	protected Camera camera;
	protected Shader shader;
	protected Plane floor=new Plane("Floor", shader, 2, 2, -2.0f, 1.0f);
//	protected Canon canon;
	private Set<Integer> pressedKeys = new HashSet<Integer>();
    private Set<Integer> toggeled=new HashSet<Integer>();
    private float canonballnumber = 0;
    private float amountOfSpheres=0;

	private void loop() {

		System.out.println("\nStarting new loop");
		

		if(pressedKeys.contains(Keyboard.KEY_SPACE)){
			
			if(amountOfSpheres%10==0){
				System.out.println("HUHHHUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
				generateCanonBall();
				
			}
			amountOfSpheres++;
			
		}
		physic.tell(Message.LOOP, self());
		ai.tell(Message.LOOP, self());
		input.tell(Message.LOOP, self());
		simulator.tell(Message.LOOP, self());
		renderer.tell(Message.DISPLAY, self());
		
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.DONE) {
			unitState.put(getSender(), true);
//			System.out.println("Done: "+getSender());
			if (!unitState.containsValue(false)) {
				for (Map.Entry<ActorRef, Boolean> entry : unitState.entrySet()) {
					entry.setValue(false);
				}
//				System.out.printf("Took %.2fms at %.1ffps.%n", time.elapsed()*1000, time.fps);
				float elapsed=time.elapsed();
				System.out.printf("Took %.2fms(possible %.3ffps) at %.1ffps.%n", elapsed*1000, time.fps, (1/elapsed));
//				renderer.tell(time.elapsed()*1000, self());  //TODO: nullpointer
				loop();
			}
		} else if (message == Message.INITIALIZED) {

			System.out.println("Initialized " + getSender());

			unitState.put(getSender(), true);

			if (!unitState.containsValue(false)) {
				for (Map.Entry<ActorRef, Boolean> entry : unitState.entrySet()) {
					entry.setValue(false);
				}

				System.out.println("Initializing App");

				initialize();

				System.out.println("App initialized");
				System.out.printf("Initialization finished in %.3fs",
						time.elapsed());
				
				loop();
			}
		} else if (message == Message.INIT) {
			System.out.println("Starting initialization");

			System.out.println("Creating Entities");

			renderer = getContext().actorOf(
					Props.create(Renderer.class).withDispatcher(
							"akka.actor.fixed-thread-dispatcher"), "Renderer");
			unitState.put(renderer, false);

			ai = getContext().actorOf(Props.create(Ai.class), "Ai");
			unitState.put(ai, false);
			
			simulator = getContext().actorOf(Props.create(Simulator.class),
					"Simulator");
			unitState.put(simulator, false);

			input = getContext().actorOf(Props.create(Input.class), "Input");
			unitState.put(input, false);
			
			physic = getContext().actorOf(Props.create(Physic.class), "Physic");
			unitState.put(physic, false);
			
			
			observers.put(Events.NODE_CREATION, renderer);
			observers.put(Events.NODE_CREATION, ai);
			observers.put(Events.NODE_CREATION, simulator);
			observers.put(Events.NODE_MODIFICATION, renderer);
			observers.put(Events.NODE_MODIFICATION, simulator);
			observers.put(Events.NODE_MODIFICATION, physic);
			observers.put(Events.NODE_MODIFICATION, ai);
			observers.put(Events.NODE_DELETION, renderer);
			observers.put(Events.NODE_DELETION, simulator);
			observers.put(Events.NODE_DELETION, physic);
			observers.put(Events.NODE_DELETION, ai);
			
			
			System.out.println("Initializing Entities");

			renderer.tell(new RendererInitialization(0), self());
			simulator.tell(Message.INIT, self());
			physic.tell(new PhysicInitialization(simulator,ai), self());
			input.tell(Message.INIT, self());
			ai.tell(new AiInitialization(simulator, floor.getWorldTransform().getPosition(), floor.w2*2, floor.d2*2), self());
			
		} else if (message instanceof RendererInitialized) {
			shader = ((RendererInitialized) message).shader;
			
			//reinpacken nach alle INIZILIZED gesendet haben
//			System.out.println("Initializing App");
//
//			initialize();
//
//			System.out.println("App initialized");
			
			//rein nach allen INITs
//			input.tell(Message.INIT, self());
		} else if (message instanceof NodeCreation) {
			
			announce(message);
		} else if (message instanceof NodeModification) {
			
			announce(message);
		} else if (message instanceof CameraCreation) {
			
			announce(message);
		} else if (message instanceof StartNodeModification) {
			
			announce(message);
		} else if(message instanceof NodeDeletion){
			
			announce(message);
		} if(message instanceof KeyState){
        	pressedKeys.clear();
        	toggeled.clear();
        	pressedKeys.addAll(((KeyState)message).getPressedKeys());
        	toggeled.addAll(((KeyState)message).getToggled());
		}
		
	}

	protected void initialize() {
		
	}

	public <T> void announce(T event) {
//		System.out.println("self............................"+self());
		if (event instanceof NodeCreation || event instanceof CameraCreation) {
			for (ActorRef observer : observers.get(Events.NODE_CREATION)) {
				observer.tell(event, self());
			}
		} else if (event instanceof NodeModification || event instanceof StartNodeModification) {
			for (ActorRef observer : observers.get(Events.NODE_MODIFICATION)) { 
//				System.out.println("announce:"+getSender()+"message"+event.toString());
				if(!observer.equals(getSender())){
					observer.tell(event, self()); 
				}
				else if(observer.equals(getSender()) && getSender().equals(renderer)){
					observer.tell(event, self()); 
				}
			}
			if(event instanceof NodeModification && (getSender().equals(simulator))){
				if(((NodeModification) event).id.equals("Canon")){
					Node modify = nodes.get(((NodeModification) event).id);
					if (((NodeModification) event).localMod != null) {
//						System.out.println("Spawn im worldstate vor transformation: " + ((Canon)modify).getSpawn());
//						System.out.println("Direction im worldstate vor transformation: " + ((Canon)modify).getDirection());
						modify.updateWorldTransform(((NodeModification) event).localMod);
//						System.out.println("Matrix die updateworld im worldstate übergeben wird: " +  ((NodeModification) event).localMod);
//						System.out.println("Spawn im worldstate nach transformation: " + ((Canon)modify).getSpawn());
//						System.out.println("Direction im worldstate nach transformation: " + ((Canon)modify).getDirection());
					}
				}
			}
		} else if (event instanceof NodeDeletion){
			NodeDeletion delete = (NodeDeletion)event;
			for(String id: delete.ids){
				Node modify = nodes.get(id);
				ArrayList<Edge> removeEdges = new ArrayList<>(); 
				if(modify!=null){
				for(Edge e: modify.getEdges()){
					removeEdges.add(e);
//					nodes.get(e.getOtherNode(modify).id).removeEdge(e);
					
				}
				for(Edge e : removeEdges){
					modify.removeEdge(e);
				}
			
				nodes.remove(modify);
				}
			}
			for (ActorRef observer : observers.get(Events.NODE_DELETION)){
				if(!observer.equals(getSender())){
					observer.tell(event, self());
				}
			}
		}
	}

	protected void setCamera(Camera cam) {
		camera = cam;
		nodes.put(cam.getId(), cam);
		
		CameraCreation cc = new CameraCreation();
		cc.id = cam.getId();
		announce(cc);
	}
	
	protected void setStart(GroupNode n) {
		startNode = n;
		nodes.put(n.getId(), n);
		
		StartNodeModification snm = new StartNodeModification();
		snm.id = n.getId();
		announce(snm);
	}
	
	protected void transform(Node n, Matrix m) {
		n.updateWorldTransform(m);
		
		NodeModification nm = new NodeModification();
		nm.id = n.getId();
//		nm.localMod = n.getWorldTransform();
		nm.localMod = m;
		announce(nm);
	}
	
	protected void append(Node nodeAppend, Node toNode) {
		nodeAppend.appendTo(toNode);
		
		NodeModification nm = new NodeModification();
		nm.id = nodeAppend.getId();
		nm.appendTo = toNode.getId();
		
		System.out.println("__ Appending " + nodeAppend.getId() + " to " + toNode.getId());
		
		
		announce(nm);
	}
	
	protected GroupNode createGroup(String id) {
		GroupNode group = nodeFactory.groupNode(id);
		nodes.put(id, group);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = ObjectTypes.GROUP;
        n.shader = null;
        announce(n);
        
        return group;
	}
	
	protected Cube createCube(String id, Shader shader, float mass) {
		return createCube(id, shader, 1, 1, 1, mass);
	}
	
	protected Cube createCube(String id, Shader shader, float w, float h, float d, float mass) {
		Cube cube = nodeFactory.cube(id, shader, w, h, d, mass);
		nodes.put(id, cube);
		
		NodeCreation n = new NodeCreation();
		n.id = id;
        n.type = ObjectTypes.CUBE;
        n.shader = shader;
        n.mass = mass;
        
        n.d = d;
        n.w = w;
        n.h = h;
        
        announce(n);
        
        return cube;
	}
	
	protected Pipe createPipe(String id, Shader shader, float r, int lats, int longs, float mass) {
		Pipe pipe = nodeFactory.pipe(id, shader, r, lats, longs, mass);
		nodes.put(id, pipe);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = ObjectTypes.PIPE;
        n.shader = shader;
        n.mass = mass;
        
        n.r = r;
        n.lats = lats;
        n.longs = longs;
        
        announce(n);
        
        return pipe;
	}
	
	protected Sphere createSphere(String id, Shader shader, float mass) {
		Sphere sphere = nodeFactory.sphere(id, shader, mass);
		nodes.put(id, sphere);
		
		NodeCreation n = new NodeCreation();
		n.id = id;
        n.type = ObjectTypes.SPHERE;
        n.shader = shader;
        n.mass = mass;
        
        System.out.println("Was geht hier? " + n.mass);
        
        announce(n);
        
        return sphere;
	}
	
	protected Canon createCanon(String id, Shader shader, File sourceFile, float mass){
		Canon canon = nodeFactory.canon(id, shader, sourceFile, mass);
		nodes.put(id, canon);
		
		NodeCreation n = new NodeCreation();
		n.id = id;
	    n.type = ObjectTypes.CANON;
	    n.shader = shader;
	    n.sourceFile = sourceFile;
	    n.mass = mass;
	    

	    announce(n);
	    return canon;
	}
	
	protected Canon createCanon(String id, Shader shader, File sourceFile,File sourceTex, float mass){
		Canon canon = nodeFactory.canon(id, shader, sourceFile, sourceTex, mass);
		nodes.put(id, canon);
		
		NodeCreation n = new NodeCreation();
		n.id = id;
	    n.type = ObjectTypes.CANON;
	    n.shader = shader;
	    n.sourceFile = sourceFile;
	    n.mass = mass;
	    

	    announce(n);
	    return canon;
	}
	
	protected Plane createPlane(String id, Shader shader, float width, float depth, float hight, float mass) {
		Plane plane = nodeFactory.plane(id, shader, width, depth, hight, mass);
		nodes.put(id, plane);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = ObjectTypes.PLANE;
        n.shader = shader;
        n.mass = mass;
        n.hight= hight;
        
        n.w = width;
        n.d = depth;
        
        announce(n);
        return plane;
	}
	
	protected void announceFloor(Plane floor) {
		nodes.put(floor.getId(), floor);
		
		NodeCreation n = new NodeCreation();
        n.id = floor.getId();
        n.type = ObjectTypes.PLANE;
        n.shader = shader;
        n.mass = floor.getMass();
        n.hight = floor.getHight();
        
        n.w = floor.getW();
        n.d = floor.getD();
        
        announce(n);
	}
	

	protected ObjLoader createObject(String id, Shader shader, File sourceFile, File sourceTex, float mass) {
		ObjLoader obj = nodeFactory.obj(id, shader, sourceFile, sourceTex, mass);
		nodes.put(id, obj);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = ObjectTypes.OBJECT;
        n.shader = shader;
        n.sourceFile=sourceFile;
        n.sourceTex=sourceTex;
        n.mass = mass;
        
        announce(n);
        
        return obj;
	}
	
	protected Car createCar(String id, Shader shader, File sourceFile, float speed, float mass){
		Car car = nodeFactory.car(id, shader, sourceFile, speed, mass);
		nodes.put(id, car);
		
		SimulateCreation n = new SimulateCreation(id,null, null);
        n.type = ObjectTypes.CAR;
        n.shader = shader;
        n.sourceFile=sourceFile;
        n.speed=speed;
        n.mass = mass;
        
        announce(n);
        
        return car;
	}
	
	protected Coin createCoin(String id, Shader shader, File sourceFile, float mass){
		Coin coin = nodeFactory.coin(id, shader, sourceFile, mass);
		nodes.put(id, coin);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = ObjectTypes.COIN;
        n.shader = shader;
        n.sourceFile=sourceFile;
        n.mass = mass;
        
        announce(n);
        
        return coin;
	}

	protected void addPhysic(Cube cube, PhysicType physicType){
		
		NodeCreation n = new NodeCreation();
		n.id = cube.getId();
		n.type = ObjectTypes.CUBE;
		n.shader = cube.getShader();
		n.d = cube.getD2();
		n.w = cube.getW2();
	    n.h = cube.getH2();
	    n.center = cube.getCenter();
		n.radius = cube.getRadius();
		n.mass = cube.getMass();
		n.physicType = physicType;
	    
		
		physic.tell(n, self());
			
	}
	
	//TODO: add Psysic und ai in create.. integrieren
	protected void addPhysic(Cube cube, Vector impulse, PhysicType physicType){
		
				
		NodeCreation n = new NodeCreation();
		n.modelmatrix = (nodes.get(cube.getId()).getWorldTransform());
		n.id = cube.getId();
		n.type = ObjectTypes.CUBE;
		n.shader = cube.getShader();
		n.impulse = impulse;
		n.d = cube.getD2();
		n.w = cube.getW2();
	    n.h = cube.getH2();
		n.center = cube.getCenter();
		n.radius = cube.getRadius();
		n.mass = cube.getMass();
		n.physicType = physicType;
		
		//TODO: sinnvolle kapselung announcePhysic
		physic.tell(n, self());
//		SimulateCreation sc=(SimulateCreation)n; TODO: wieso geht das nicht?
//		sc.setSimulation(SimulateType.PHYSIC);
		SimulateCreation sc = new SimulateCreation(cube.getId(), null, SimulateType.PHYSIC, null, null);
		sc.modelmatrix = n.getModelmatrix();
		sc.type = ObjectTypes.CUBE;
		sc.shader = cube.getShader();
		sc.impulse = impulse;
		sc.d = cube.getD2();
		sc.w = cube.getW2();
	    sc.h = cube.getH2();
		sc.center = cube.getCenter();
		sc.radius = cube.getRadius();
		sc.mass = cube.getMass();
		
		
		
		simulator.tell(sc,self());
			
	}
	
	protected void addPhysic(Sphere sphere, Vector impulse, PhysicType physicType){
		
		
		NodeCreation n = new NodeCreation();
		n.modelmatrix = (nodes.get(sphere.getId()).getWorldTransform());
		n.id = sphere.getId();
		n.type = ObjectTypes.SPHERE;
		n.shader = sphere.getShader();
		n.impulse = impulse;
		n.center = sphere.getCenter();
		n.radius = sphere.getRadius();
		n.mass = sphere.getMass();
		n.physicType = physicType;
		
		
		physic.tell(n, self());
//		SimulateCreation sc=(SimulateCreation)n; TODO: wieso geht das nicht?
//		sc.setSimulation(SimulateType.PHYSIC);
		SimulateCreation sc = new SimulateCreation(sphere.getId(), null, SimulateType.PHYSIC, null, null);
		sc.modelmatrix = n.getModelmatrix();
		sc.type = ObjectTypes.SPHERE;
		sc.shader = sphere.getShader();
		sc.impulse = impulse;
		sc.center = sphere.getCenter();
		sc.radius = sphere.getRadius();
		sc.mass = sphere.getMass();
		simulator.tell(sc,self());
			
	}
	
	protected void addPhysic(ObjLoader obj, Vector impulse, PhysicType physicType){
		
		NodeCreation n = new NodeCreation();
		n.modelmatrix = (nodes.get(obj.getId()).getWorldTransform());
        n.id = obj.getId();
        n.type = ObjectTypes.OBJECT;
        n.shader = shader;
        n.sourceFile= obj.getSourceFile();
        n.sourceTex= obj.getSourceTex();
    	n.impulse = impulse;
		n.center = obj.getCenter();
		n.radius = obj.getRadius();
		n.mass = obj.mass;
		n.physicType = physicType;
		
		physic.tell(n, self());

		SimulateCreation sc = new SimulateCreation(obj.getId(), null, SimulateType.PHYSIC, null, null);
		sc.modelmatrix = n.getModelmatrix();
		sc.type = ObjectTypes.OBJECT;
		sc.shader = shader;
	    sc.sourceFile= obj.getSourceFile();
	    sc.sourceTex= obj.getSourceTex();
	    sc.mass = obj.mass;
		simulator.tell(sc,self());
		
	}
	
	protected void addPhysicFloor(Plane plane){
		
		Vector pos = plane.getWorldTransform().getPosition();
		
		FloorCreation f = new FloorCreation();
		f.position = pos;
		
		physic.tell(f, self());
		
	}
	
	protected void simulateOnKey(Node object, Set<Integer> keys, SimulateType simulation, KeyMode mode, Vector vec, ObjectTypes type){ //TODO:better solution for type
		SimulateCreation sc=new SimulateCreation(object.getId(), keys, simulation, mode, vec);
		sc.type=type;
		sc.modelmatrix=object.getWorldTransform();
		if(object instanceof Shape){
			Shape s=(Shape)object;
			sc.shader=s.getShader();
		}
		if (object instanceof Cube){
			sc.w =((Cube)object).getW2();
			sc.h = ((Cube)object).getH2();
			sc.d =((Cube)object).getD2();
		} else if(object instanceof Pipe){
			Pipe p=(Pipe)object;
			sc.r = p.r;
		    sc.lats = p.lats;
		    sc.longs = p.longs;
		}else if(object instanceof Plane){
			Plane p=(Plane)object;
			sc.w = p.getW();
	        sc.d = p.getD();
		}/*else if(object instanceof Torus){
			Torus t=(Torus) object;
			
		}*/
		else if(object instanceof ObjLoader){
			ObjLoader obj=(ObjLoader)object;
			sc.sourceFile=obj.getSourceFile();
	        sc.sourceTex=obj.getSourceTex();
		}
		simulator.tell(sc, getSelf());
		if(!(keys==null||keys.isEmpty())){
			if(simulation!=SimulateType.NONE) input.tell(new RegisterKeys(keys, true), simulator);
			else input.tell(new RegisterKeys(keys, false), simulator);
		}
	}
	
//	protected void addToAi(Cube cube){
//		
//		
//		NodeCreation n = new NodeCreation();
//		n.modelmatrix = (nodes.get(cube.getId()).getWorldTransform());
//		n.id = cube.getId();
//		n.type = ObjectTypes.CUBE;
//		n.shader = cube.getShader();
//		n.d = cube.getD2();
//		n.w = cube.getW2();
//	    n.h = cube.getH2();
//		n.center = cube.getCenter();
//		n.radius = cube.getRadius();
//		
//		ai.tell(n, self());
//	}
//	
//	protected void addToAi(Sphere sphere){
//		
//		
//		NodeCreation n = new NodeCreation();
//		n.modelmatrix = (nodes.get(sphere.getId()).getWorldTransform());
//		n.id = sphere.getId();
//		n.type = ObjectTypes.SPHERE;
//		n.shader = sphere.getShader();
//		n.center = sphere.getCenter();
//		n.radius = sphere.getRadius();
//		
//		
//		ai.tell(n, self());
//	}
	
	protected void doCanonBalls(){
		input.tell(new RegisterKeys(new HashSet<Integer>(Arrays.asList(Keyboard.KEY_SPACE)), true), self());
	}
	
	protected void generateCanonBall(){
		float scaleFactor=0.5f;
		Node canon = nodes.get("Canon");
		Sphere cs = createSphere("CanonBall" + canonballnumber, shader, 1f);
		transform(cs, vecmath.scaleMatrix(scaleFactor, scaleFactor, scaleFactor));
		cs.setRadius(cs.getRadius()* scaleFactor);
		transform(cs, vecmath.translationMatrix(((Canon) canon).getSpawn()));
		addPhysic(cs, ((Canon)canon).getDirection().mult(0.03f), PhysicType.Physic_complete);
		append(cs, startNode);
		System.out.println("sphere speed: " + ((Canon)canon).getDirection().mult(0.03f));
//		System.out.println("Sphere Id: " + cs.getId() + "Radius SPhere: " + cs.getRadius());
		canonballnumber++;
		
	}
}
