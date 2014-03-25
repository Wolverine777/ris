package app;

import static app.nodes.NodeFactory.nodeFactory;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static vecmath.vecmathimp.FactoryDefault.vecmath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import app.Types.GestureType;
import app.Types.KeyMode;
import app.Types.ObjectTypes;
import app.Types.PhysicType;
import app.Types.SimulateType;
import app.datatype.FontInfo;
import app.edges.Edge;
import app.eventsystem.CameraCreation;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeDeletion;
import app.eventsystem.NodeModification;
import app.eventsystem.SimulateCreation;
import app.eventsystem.SimulateGestureCreation;
import app.eventsystem.StartNodeModification;
import app.messages.AiInitialization;
import app.messages.KeyState;
import app.messages.Message;
import app.messages.RegisterGesture;
import app.messages.RegisterKeys;
import app.messages.PhysicInitialization;
import app.messages.RendererInitialization;
import app.messages.RendererInitialized;
import app.messages.TapDetected;
import app.nodes.Camera;
import app.nodes.GroupNode;
import app.nodes.Node;
import app.nodes.Sun;
import app.nodes.Text;
import app.nodes.shapes.*;
import app.shader.Shader;
import app.toolkit.StopWatch;

/**
 * Technical base
 * 
 * @author Benjamin Reemts, Fabian Unruh
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
	protected Shader texShader;
	protected Plane floor=new Plane("Floor", shader, 20, 20, -2.0f, 1.0f);
//	protected Canon canon;
	private Set<Integer> pressedKeys = new HashSet<Integer>();
    private Set<Integer> toggeled=new HashSet<Integer>();
    private float canonballnumber = 0;
    private float amountOfSpheres=0;
    private boolean tapped=false;
    public static final boolean LEAP=false;
//    protected double leap=0.5; //1Tastatur 0.5Leap
    private boolean done;
    private final boolean LOOGING=false;
    

	private void loop() {
		System.out.println("\nStarting new loop");
		
		if(LEAP){
			if(tapped &&amountOfSpheres>15){
				generateCanonBall();
				tapped=false;
				amountOfSpheres=0;
			}
		}
		
		if(pressedKeys.contains(Keyboard.KEY_SPACE)){
			
			if(amountOfSpheres>8){
//				alSourcePlay(Renderer.source2);
				generateCanonBall();
				amountOfSpheres=0;
			}
			
		}
		amountOfSpheres++;
		physic.tell(Message.LOOP, self());
		ai.tell(Message.LOOP, self());
		input.tell(Message.LOOP, self());
		simulator.tell(Message.LOOP, self());
		renderer.tell(Message.DISPLAY, self());
		done=true;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.DONE) {
			unitState.put(getSender(), true);
			System.out.println("Done: "+getSender());
			if (!unitState.containsValue(false)&&done) {
				done=false;
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
				
				alSourcePlay(Renderer.source2);
				alSourcePlay(Renderer.source1);
				loop();
			}
		} else if (message == Message.INIT) {
			
			if(LOOGING){
				//For logging
				try {
					String fName="log1.txt";
					Pattern p = Pattern.compile("(.*?)(\\d+)?(\\..*)?");
					do{
						Matcher m = p.matcher(fName);
						if(m.matches()){//group 1 is the prefix, group 2 is the number, group 3 is the suffix
							fName = m.group(1) + (m.group(2)==null?1:(Integer.parseInt(m.group(2)) + 1)) + (m.group(3)==null?"":m.group(3));
						}
					}while(new File("log/"+fName).exists());//repeat until a new filename is generated
					
					PrintStream out=new PrintStream(new FileOutputStream("log/"+fName, true), true);
					System.setOut(out);
					System.setErr(out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Leap: "+LEAP);
			
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
			observers.put(Events.NODE_CREATION, physic);
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
			texShader = ((RendererInitialized) message).texShader;
			
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
        	
		} else if(message instanceof TapDetected){
			tapped=true;
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
		
		CameraCreation cc = new CameraCreation(cam.getId(), cam.getShaderList());
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
		
		NodeModification nm = new NodeModification(n.getId(), m);
		announce(nm);
	}
	
	protected void append(Node nodeAppend, Node toNode) {
		nodeAppend.appendTo(toNode);
		
		NodeModification nm = new NodeModification(nodeAppend.getId(), toNode.getId());
		System.out.println("__ Appending " + nodeAppend.getId() + " to " + toNode.getId());
		
		
		announce(nm);
	}
	
	protected GroupNode createGroup(String id) {
		GroupNode group = nodeFactory.groupNode(id, vecmath.identityMatrix());
		nodes.put(id, group);
		
		NodeCreation n = new NodeCreation(id, vecmath.identityMatrix(), ObjectTypes.GROUP);
        n.type = ObjectTypes.GROUP;
        n.shader = null;
        announce(n);
        
        return group;
	}
	
	protected GroupNode createGroup(String id, Matrix modelMatrix) {
		GroupNode group = nodeFactory.groupNode(id, modelMatrix);
		nodes.put(id, group);
		
		NodeCreation n = new NodeCreation(id, modelMatrix, ObjectTypes.GROUP);
        n.type = ObjectTypes.GROUP;
        n.shader = null;
        announce(n);
        
        return group;
	}
	
	protected Cube createCube(String id, Shader shader, float mass) {
		return createCube(id, shader, 1, 1, 1, mass, null, null);
	}
	
	/**
	 * @param id
	 * @param shader
	 * @param w
	 * @param h
	 * @param d
	 * @param mass
	 * @param impulse can be null, for no Physic
	 * @param physicType can be null, for no Physic
	 * @return
	 */
	protected Cube createCube(String id, Shader shader, float w, float h, float d, float mass, Vector impulse, PhysicType physicType) {
		Cube cube = nodeFactory.cube(id, shader, w, h, d, mass);
		nodes.put(id, cube);
		NodeCreation n = new NodeCreation(id, shader, w, d, h, mass, ObjectTypes.CUBE);
        n.addPhysic(impulse, physicType);
        announce(n);
        
        return cube;
	}
	
	protected Pipe createPipe(String id, Shader shader, float r, int lats, int longs, float mass) {
		Pipe pipe = nodeFactory.pipe(id, shader, r, lats, longs, mass);
		nodes.put(id, pipe);
		
		NodeCreation n = new NodeCreation(id, shader, r, lats, longs, mass);
        
        announce(n);
        
        return pipe;
	}

	/**
	 * @param id
	 * @param shader
	 * @param mass
	 * @param impulse can be null, for no Physic
	 * @param physicType can be null, for no Physic
	 * @return
	 */
	protected Sphere createSphere(String id, Shader shader, Matrix modelMatrix, float mass, Vector impulse, PhysicType physicType) {
		Sphere sphere = nodeFactory.sphere(id, shader, mass, modelMatrix);
		nodes.put(id, sphere);
		NodeCreation n = new NodeCreation(id, shader, mass, modelMatrix);
        n.addPhysic(impulse, physicType);
        announce(n);
        
        return sphere;
	}
	
	protected Canon createCanon(String id, Shader shader, File sourceFile, Matrix modelMatrix, float mass){
		if(modelMatrix==null)modelMatrix=vecmath.identityMatrix();
		Canon canon = nodeFactory.canon(id, shader, sourceFile, null, modelMatrix, mass);
		nodes.put(id, canon);
		
		NodeCreation n = new NodeCreation(id, shader, sourceFile, null, modelMatrix, mass, ObjectTypes.CANON);

	    announce(n);
	    return canon;
	}
	
	protected Canon createCanon(String id, Shader shader, File sourceFile,File sourceTex, Matrix modelMatrix, float mass){
		if(modelMatrix==null)modelMatrix=vecmath.identityMatrix();
		Canon canon = nodeFactory.canon(id, shader, sourceFile, null, modelMatrix, mass);
		nodes.put(id, canon);
		
		NodeCreation n = new NodeCreation(id, shader, sourceFile, sourceTex, modelMatrix, mass, ObjectTypes.CANON);

	    announce(n);
	    return canon;
	}
	
	protected Plane createPlane(String id, Shader shader, float width, float depth, float hight, float mass) {
		Plane plane = nodeFactory.plane(id, shader, width, depth, hight, mass);
		nodes.put(id, plane);
		
		NodeCreation n = new NodeCreation(id, shader, width, depth, hight, mass, ObjectTypes.PLANE);
        
        announce(n);
        return plane;
	}
	
	protected void announceFloor(Plane floor) {
		nodes.put(floor.getId(), floor);
		
		NodeCreation n = new NodeCreation(floor.getId(), shader, floor.getW(), floor.getD(), floor.getHight(), floor.getMass(), ObjectTypes.PLANE);
        
        announce(n);
	}
	
	/**
	 * @param id
	 * @param shader
	 * @param sourceFile
	 * @param sourceTex
	 * @param mass
	 * @param impulse can be null, for no Physic
	 * @param physicType can be null, for no Physic
	 * @return
	 */
	protected ObjLoader createObject(String id, Shader shader, File sourceFile, File sourceTex, Matrix modelMatrix, float mass, Vector impulse, PhysicType physicType) {
		if(modelMatrix==null)modelMatrix=vecmath.identityMatrix();
		ObjLoader obj = nodeFactory.obj(id, shader, sourceFile, null, modelMatrix, mass);
		nodes.put(id, obj);
		
		NodeCreation n = new NodeCreation(id, shader, sourceFile, sourceTex, modelMatrix, mass, ObjectTypes.OBJECT);
        n.addPhysic(impulse, physicType);
        announce(n);
        
        return obj;
	}
	
	/**
	 * @param id
	 * @param shader
	 * @param sourceFile
	 * @param speed
	 * @param mass
	 * @param impulse can be null, for no Physic
	 * @param physicType can be null, for no Physic
	 * @return
	 */
	protected Car createCar(String id, Shader shader, File sourceFile, File sourceTex, double speed, Matrix modelMatrix, float mass, Vector impulse, PhysicType physicType){
		if(modelMatrix==null)modelMatrix=vecmath.identityMatrix();
		Car car = nodeFactory.car(id, shader, sourceFile,null, speed, modelMatrix, mass);
		nodes.put(id, car);
		SimulateCreation n = new SimulateCreation(id, shader, sourceFile, sourceTex, speed, modelMatrix, mass, null, null);
        n.mass = mass;
        n.addPhysic(impulse, physicType);
        
        announce(n);
        return car;
	}
	
	/**
	 * @param id
	 * @param shader
	 * @param sourceFile
	 * @param mass
	 * @param impulse can be null, for no Physic
	 * @param physicType can be null, for no Physic
	 * @return
	 */
	protected Coin createCoin(String id, Shader shader, File sourceFile, File sourceTex, Matrix modelMatrix, float mass, Vector impulse, PhysicType physicType){
		if(modelMatrix==null)modelMatrix=vecmath.identityMatrix();
		Coin coin = nodeFactory.coin(id, shader, sourceFile, null, modelMatrix, mass);
		nodes.put(id, coin);
		SimulateCreation sc=new SimulateCreation(id, shader, sourceFile, sourceTex, modelMatrix, mass, ObjectTypes.COIN);
//		NodeCreation n=new NodeCreation(id, shader, sourceFile, mass, ObjectTypes.COIN);
		sc.addPhysic(impulse, physicType);
        
        announce(sc);
        return coin;
	}
	
	protected void simulateOnKey(Node object, Set<Integer> keys, SimulateType simulation, KeyMode mode, Vector vec){
		SimulateCreation sc=new SimulateCreation(object.getId(), object.getWorldTransform(), keys, simulation, mode, vec);
		if(object instanceof Shape){
			Shape s=(Shape)object;
			sc.shader=s.getShader();
		}
		if (object instanceof Cube){
			sc.w =((Cube)object).getW2();
			sc.h = ((Cube)object).getH2();
			sc.d =((Cube)object).getD2();
			sc.type=ObjectTypes.CUBE;
		} else if(object instanceof Pipe){
			Pipe p=(Pipe)object;
			sc.r = p.r;
		    sc.lats = p.lats;
		    sc.longs = p.longs;
		    sc.type=ObjectTypes.PIPE;
		}else if(object instanceof Plane){
			Plane p=(Plane)object;
			sc.w = p.getW();
	        sc.d = p.getD();
	        sc.type=ObjectTypes.PLANE;
		}else if(object instanceof Sphere){
			sc.type=ObjectTypes.SPHERE;
		}
		else if(object instanceof ObjLoader){
			ObjLoader obj=(ObjLoader)object;
			sc.sourceFile=obj.getSourceFile();
	        sc.sourceTex=obj.getSourceTex();
	        sc.type=ObjectTypes.OBJECT;
		}
		simulator.tell(sc, getSelf());
		if(!(keys==null||keys.isEmpty())){
			if(simulation!=SimulateType.NONE) input.tell(new RegisterKeys(keys, true), simulator);
			else input.tell(new RegisterKeys(keys, false), simulator);
		}
	}
	
	//TODO: integrate in Simulation Creation
	protected void simulateOnGesture(Node object, GestureType gesture, SimulateType simulation, Vector vec){
		SimulateGestureCreation sgc= new SimulateGestureCreation(object.getId(),object.getWorldTransform(), gesture, simulation, vec);
		if(object instanceof Shape){
			Shape s=(Shape)object;
			sgc.shader=s.getShader();
		}
		if (object instanceof Cube){
			sgc.w =((Cube)object).getW2();
			sgc.h = ((Cube)object).getH2();
			sgc.d =((Cube)object).getD2();
			sgc.type=ObjectTypes.CUBE;
		} else if(object instanceof Pipe){
			Pipe p=(Pipe)object;
			sgc.r = p.r;
		    sgc.lats = p.lats;
		    sgc.longs = p.longs;
		    sgc.type=ObjectTypes.PIPE;
		}else if(object instanceof Plane){
			Plane p=(Plane)object;
			sgc.w = p.getW();
	        sgc.d = p.getD();
	        sgc.type=ObjectTypes.PLANE;
		}else if(object instanceof Sphere){
			sgc.type=ObjectTypes.SPHERE;
		}
		else if(object instanceof ObjLoader){
			ObjLoader obj=(ObjLoader)object;
			sgc.sourceFile=obj.getSourceFile();
	        sgc.sourceTex=obj.getSourceTex();
	        sgc.type=ObjectTypes.OBJECT;
		}
		
		simulator.tell(sgc, getSelf());
		if(simulation!=SimulateType.NONE){
			input.tell(new RegisterGesture(gesture, true), simulator);			
		} else input.tell(new RegisterGesture(gesture, false), simulator);
		
		
	}
	
	protected void doCanonBalls(){
		input.tell(new RegisterKeys(new HashSet<Integer>(Arrays.asList(Keyboard.KEY_SPACE)), true), self());
		input.tell(new RegisterGesture(GestureType.KEY_TAP, true),self());
	}
	
	protected void generateCanonBall(){
		float scaleFactor=0.25f;
		if(LEAP) scaleFactor=scaleFactor/0.5f; 
		Canon canon = (Canon) nodes.get("Canon");
		Matrix modelMatrix =vecmath.translationMatrix(canon.getSpawn()).mult(vecmath.scaleMatrix(scaleFactor, scaleFactor, scaleFactor)); 
		Sphere cs = createSphere("CanonBall" + canonballnumber, shader, modelMatrix, 0.72f, canon.getDirection().mult(0.07f+(0.001f*floor.getD())), PhysicType.Physic_complete);
//		cs.setRadius(cs.getRadius()* scaleFactor);
		append(cs, startNode);
//		System.out.println("sphere speed: " + canon.getDirection().mult(0.03f));
//		System.out.println("Sphere Id: " + cs.getId() + "Radius SPhere: " + cs.getRadius());
		canonballnumber++;
		
	}
	
	protected Text createText(String id, String text, FontInfo font){
		Text t=nodeFactory.text(id, vecmath.identityMatrix(), text, font);
		NodeCreation n=new NodeCreation(id, vecmath.identityMatrix(), text, font);
		
		announce(n);
		return t;
	}
	
	protected Sun createSun(String id, Matrix matrix, Shader shader){
		Sun s=nodeFactory.sun(id, matrix, shader);
		NodeCreation n=new NodeCreation(id, shader, matrix);
		announce(n);
		return s; 
	}
	
}
