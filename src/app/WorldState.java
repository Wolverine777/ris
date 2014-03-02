package app;

import static app.nodes.NodeFactory.nodeFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import vecmath.Matrix;
import vecmath.Vector;
import vecmath.vecmathimp.VectorImp;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import app.Types.Events;
import app.Types.KeyMode;
import app.Types.ObjectTypes;
import app.Types.SimulateType;
import app.datatype.Level;
import app.eventsystem.CameraCreation;
import app.eventsystem.FloorCreation;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeModification;
import app.eventsystem.SimulateCreation;
import app.eventsystem.StartNodeModification;
import app.messages.AiInitialization;
import app.messages.Message;
import app.messages.RegisterKeys;
import app.messages.PhysicInitialization;
import app.messages.RendererInitialization;
import app.messages.RendererInitialized;
import app.nodes.GroupNode;
import app.nodes.Node;
import app.nodes.camera.Camera;
import app.nodes.shapes.Cube;
import app.nodes.shapes.ObjLoader;
import app.nodes.shapes.Pipe;
import app.nodes.shapes.Plane;
import app.nodes.shapes.Shape;
import app.nodes.shapes.Sphere;
import app.nodes.shapes.Torus;
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
	protected Plane floor=new Plane("Floor", shader, 2, 2, 1.0f);

	private void loop() {

		System.out.println("\nStarting new loop");

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

			simulator = getContext().actorOf(Props.create(Simulator.class),
					"Simulator");
			unitState.put(simulator, false);

			input = getContext().actorOf(Props.create(Input.class), "Input");
			unitState.put(input, false);
			
			physic = getContext().actorOf(Props.create(Physic.class), "Physic");
			unitState.put(physic, false);
			
			ai = getContext().actorOf(Props.create(Ai.class), "Ai");
			unitState.put(ai, false);
			
			observers.put(Events.NODE_CREATION, renderer);
			observers.put(Events.NODE_MODIFICATION, renderer);
			observers.put(Events.NODE_MODIFICATION, simulator);
			observers.put(Events.NODE_MODIFICATION, physic);
			observers.put(Events.NODE_MODIFICATION, ai);
			
			System.out.println("Initializing Entities");

			renderer.tell(new RendererInitialization(0), self());
			simulator.tell(Message.INIT, self());
			physic.tell(new PhysicInitialization(simulator), self());
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
		} 	
	}

	protected void setCamera(Camera cam) {
		camera = cam;
		nodes.put(cam.id, cam);
		
		CameraCreation cc = new CameraCreation();
		cc.id = cam.id;
		announce(cc);
	}
	
	protected void setStart(GroupNode n) {
		startNode = n;
		nodes.put(n.id, n);
		
		StartNodeModification snm = new StartNodeModification();
		snm.id = n.id;
		announce(snm);
	}
	
	protected void transform(Node n, Matrix m) {
		n.updateWorldTransform(m);
		
		NodeModification nm = new NodeModification();
		nm.id = n.id;
//		nm.localMod = n.getWorldTransform();
		nm.localMod = m;
		announce(nm);
	}
	
	protected void append(Node nodeAppend, Node toNode) {
		nodeAppend.appendTo(toNode);
		
		NodeModification nm = new NodeModification();
		nm.id = nodeAppend.id;
		nm.appendTo = toNode.id;
		
		System.out.println("__ Appending " + nodeAppend.id + " to " + toNode.id);
		
		
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
        
        announce(n);
        
        return sphere;
	}
	
	protected Plane createPlane(String id, Shader shader, float width, float depth, float mass) {
		Plane plane = nodeFactory.plane(id, shader, width, depth, mass);
		nodes.put(id, plane);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = ObjectTypes.PLANE;
        n.shader = shader;
        n.mass = mass;
        
        n.w = width;
        n.d = depth;
        
        announce(n);
        return plane;
	}
	
	protected void announceFloor(Plane floor) {
		nodes.put(floor.id, floor);
		
		NodeCreation n = new NodeCreation();
        n.id = floor.id;
        n.type = ObjectTypes.PLANE;
        n.shader = shader;
        n.mass = floor.getMass();
        
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

	protected void addPhysic(Cube cube){
		
		NodeCreation n = new NodeCreation();
		n.id = cube.id;
		n.type = ObjectTypes.CUBE;
		n.shader = cube.getShader();
		n.d = cube.getD2();
		n.w = cube.getW2();
	    n.h = cube.getH2();
	    n.center = cube.getCenter();
		n.radius = cube.getRadius();
	    
		
		physic.tell(n, self());
			
	}
	
	protected void addPhysic(Cube cube, Vector impulse){
		
				
		NodeCreation n = new NodeCreation();
		n.modelmatrix = (nodes.get(cube.id).getWorldTransform());
		n.id = cube.id;
		n.type = ObjectTypes.CUBE;
		n.shader = cube.getShader();
		n.impulse = impulse;
		n.d = cube.getD2();
		n.w = cube.getW2();
	    n.h = cube.getH2();
		n.center = cube.getCenter();
		n.radius = cube.getRadius();
		
		//TODO: sinnvolle kapselung announcePhysic
		physic.tell(n, self());
//		SimulateCreation sc=(SimulateCreation)n; TODO: wieso geht das nicht?
//		sc.setSimulation(SimulateType.PHYSIC);
		SimulateCreation sc = new SimulateCreation(cube.id, null, SimulateType.PHYSIC, null, null);
		sc.modelmatrix = n.getModelmatrix();
		sc.type = ObjectTypes.CUBE;
		simulator.tell(sc,self());
			
	}
	
	protected void addPhysic(Sphere sphere, Vector impulse){
		
		
		NodeCreation n = new NodeCreation();
		n.modelmatrix = (nodes.get(sphere.id).getWorldTransform());
		n.id = sphere.id;
		n.type = ObjectTypes.SPHERE;
		n.shader = sphere.getShader();
		n.impulse = impulse;
		n.center = sphere.getCenter();
		n.radius = sphere.getRadius();
		
		
		physic.tell(n, self());
//		SimulateCreation sc=(SimulateCreation)n; TODO: wieso geht das nicht?
//		sc.setSimulation(SimulateType.PHYSIC);
		SimulateCreation sc = new SimulateCreation(sphere.id, null, SimulateType.PHYSIC, null, null);
		sc.modelmatrix = n.getModelmatrix();
		sc.type = ObjectTypes.CUBE;
		simulator.tell(sc,self());
			
	}
	
	protected void addPhysicFloor(Plane plane){
		
		Vector pos = plane.getWorldTransform().getPosition();
		
		FloorCreation f = new FloorCreation();
		f.position = pos;
		
		physic.tell(f, self());
		
	}
	
	protected void simulateOnKey(Node object, Set<Integer> keys, SimulateType simulation, KeyMode mode, Vector vec, ObjectTypes type){ //TODO:better solution for type
		SimulateCreation sc=new SimulateCreation(object.id, keys, simulation, mode, vec);
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
	
	protected void addToAi(Cube cube){
		
		
		NodeCreation n = new NodeCreation();
		n.modelmatrix = (nodes.get(cube.id).getWorldTransform());
		n.id = cube.id;
		n.type = ObjectTypes.CUBE;
		n.shader = cube.getShader();
		n.d = cube.getD2();
		n.w = cube.getW2();
	    n.h = cube.getH2();
		n.center = cube.getCenter();
		n.radius = cube.getRadius();
		
		ai.tell(n, self());
	}
	
	protected void addToAi(Sphere sphere){
		
		
		NodeCreation n = new NodeCreation();
		n.modelmatrix = (nodes.get(sphere.id).getWorldTransform());
		n.id = sphere.id;
		n.type = ObjectTypes.SPHERE;
		n.shader = sphere.getShader();
		n.center = sphere.getCenter();
		n.radius = sphere.getRadius();
		
		
		ai.tell(n, self());
	}
}
