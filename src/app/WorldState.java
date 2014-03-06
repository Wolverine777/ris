package app;

import static app.nodes.NodeFactory.nodeFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import app.eventsystem.CameraCreation;
import app.eventsystem.Events;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeModification;
import app.eventsystem.SimulateCreation;
import app.eventsystem.StartNodeModification;
import app.eventsystem.Target;
import app.eventsystem.Types;
import app.eventsystem.WorldEvents;
import app.messages.KeyEvent;
import app.messages.Message;
import app.messages.Mode;
import app.messages.PhysicInitialization;
import app.messages.RendererInitialization;
import app.messages.RendererInitialized;
import app.messages.SimulateType;
import app.nodes.GroupNode;
import app.nodes.Node;
import app.nodes.camera.Camera;
import app.nodes.shapes.Cube;
import app.nodes.shapes.Pipe;
import app.nodes.shapes.Plane;
import app.nodes.shapes.Sphere;
import app.shader.Shader;
import app.toolkit.StopWatch;
import app.vecmath.Matrix;
import app.vecmath.Vector;

/**
 * Technical base
 * 
 * @author Constantin
 * 
 */
public class WorldState extends UntypedActor{
	public static ActorSystem system;
	
	private Map<String, Node> nodes = new HashMap<String, Node>();

	private StopWatch time = new StopWatch();
	private Map<ActorRef, Boolean> unitState = new HashMap<ActorRef, Boolean>();
	private SetMultimap<Events, ActorRef> observers = HashMultimap.create();
	private ActorRef renderer;
	private ActorRef simulator;
	private ActorRef input;
	private ActorRef physic;

	protected Node startNode;
	protected Camera camera;
	protected Shader shader;

	private void loop() {

		System.out.println("\nStarting new loop");

		simulator.tell(Message.LOOP, self());
		input.tell(Message.LOOP, self());
		renderer.tell(Message.DISPLAY, self());
		physic.tell(Message.LOOP, self());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.DONE) {
			unitState.put(getSender(), true);

			if (!unitState.containsValue(false)) {
				for (Map.Entry<ActorRef, Boolean> entry : unitState.entrySet()) {
					entry.setValue(false);
				}
				System.out.printf("Took %.2fms at %.1ffps.%n", time.elapsed()*1000, time.fps);
				loop();
			}
		} else if (message == Message.INITIALIZED) {

			System.out.println("Initialized " + getSender());

			unitState.put(getSender(), true);

			if (!unitState.containsValue(false)) {
				for (Map.Entry<ActorRef, Boolean> entry : unitState.entrySet()) {
					entry.setValue(false);
				}

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
			
			observers.put(Events.NODE_CREATION, renderer);
			observers.put(Events.NODE_CREATION, simulator);
			observers.put(Events.NODE_MODIFICATION, renderer);
			observers.put(Events.NODE_MODIFICATION, simulator);
			observers.put(Events.NODE_MODIFICATION, physic);
			
			System.out.println("Initializing Entities");

			renderer.tell(new RendererInitialization(0), self());
			simulator.tell(Message.INIT, self());
			physic.tell(new PhysicInitialization(simulator), self());
		} else if (message instanceof RendererInitialized) {
			shader = ((RendererInitialized) message).shader;
			
			System.out.println("Initializing App");

			initialize();

			System.out.println("App initialized");
			
			
			input.tell(Message.INIT, self());
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
		if (event instanceof NodeCreation || event instanceof CameraCreation) {
			for (ActorRef observer : observers.get(Events.NODE_CREATION)) {
				observer.tell(event, self());
			}
		} else if (event instanceof NodeModification || event instanceof StartNodeModification) {
			for (ActorRef observer : observers.get(Events.NODE_MODIFICATION)) {
				observer.tell(event, self());
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
		nm.localMod = m;
		announce(nm);
	}
	
	protected void append(Node n, Node m) {
		n.appendTo(m);
		
		NodeModification nm = new NodeModification();
		nm.id = n.id;
		nm.appendTo = m.id;
		
		System.out.println("__ Appending " + n.id + " to " + m.id);
		
		
		announce(nm);
	}
	
	protected GroupNode createGroup(String id) {
		GroupNode group = nodeFactory.groupNode(id);
		nodes.put(id, group);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = Types.GROUP;
        n.shader = null;
        announce(n);
        
        return group;
	}
	
	protected Cube createCube(String id, Shader shader) {
		return createCube(id, shader, 1, 1, 1);
	}
	
	protected Cube createCube(String id, Shader shader, float w, float h, float d) {
		Cube cube = nodeFactory.cube(id, shader, w, h, d);
		nodes.put(id, cube);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = Types.CUBE;
        n.shader = shader;
        
        n.d = d;
        n.w = w;
        n.h = h;
        
        announce(n);
        
        return cube;
	}
	
	protected Pipe createPipe(String id, Shader shader, float r, int lats, int longs) {
		Pipe pipe = nodeFactory.pipe(id, shader, r, lats, longs);
		nodes.put(id, pipe);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = Types.PIPE;
        n.shader = shader;
        
        n.r = r;
        n.lats = lats;
        n.longs = longs;
        
        announce(n);
        
        return pipe;
	}
	
	protected Sphere createSphere(String id, Shader shader) {
		Sphere sphere = nodeFactory.sphere(id, shader);
		nodes.put(id, sphere);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = Types.SPHERE;
        n.shader = shader;
        
        announce(n);
        
        return sphere;
	}
	
	protected Plane createPlane(String id, Shader shader, float width, float depth) {
		Plane plane = nodeFactory.plane(id, shader, width, depth);
		nodes.put(id, plane);
		
		NodeCreation n = new NodeCreation();
        n.id = id;
        n.type = Types.PLANE;
        n.shader = shader;
        
        n.w = width;
        n.d = depth;
        
        announce(n);
        
        return plane;
	}

	protected void addPhysic(Cube cube){
		
		NodeCreation n = new NodeCreation();
		n.id = cube.id;
		n.type = Types.CUBE;
		n.shader = cube.getShader();
		
		physic.tell(n, self());
			
	}
	
	protected void addPhysic(Cube cube, Vector velocity){
		
		NodeCreation n = new NodeCreation();
		n.id = cube.id;
		n.type = Types.CUBE;
		n.shader = cube.getShader();
		n.velocity = velocity;
		
		physic.tell(n, self());
			
	}
	
	protected void simulateOnKey(Node object, Set<Integer> keys, SimulateType simulation, Mode mode){
		simulator.tell(new SimulateCreation(object, keys, simulation, mode), getSelf());
	}
}
