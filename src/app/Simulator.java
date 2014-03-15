package app;

import static app.nodes.NodeFactory.nodeFactory;
import static vecmath.vecmathimp.FactoryDefault.vecmath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vecmath.Matrix;
import vecmath.Vector;
import vecmath.vecmathimp.MatrixImp;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import app.Types.KeyMode;
import app.Types.ObjectTypes;
import app.Types.SimulateType;
import app.datatype.KeyDef;
import app.edges.Edge;
import app.eventsystem.CameraCreation;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeDeletion;
import app.eventsystem.NodeModification;
import app.eventsystem.PhysicModification;
import app.eventsystem.SimulateCreation;
import app.messages.KeyState;
import app.messages.Message;
import app.messages.SingelSimulation;
import app.nodes.Node;
import app.nodes.shapes.Car;
import app.toolkit.StopWatch;

public class Simulator extends UntypedActor {

	private Map<String, Node> nodes = new HashMap<String, Node>();
	private SetMultimap<Node, KeyDef> simulations = HashMultimap.create();
	private Set<Integer> pressedKeys = new HashSet<Integer>();
	private Set<Integer> toggeled = new HashSet<Integer>();
	private float angle = 0;
	private StopWatch sw = new StopWatch();
	private float elapsed = 0;
	// TODO:was besseres überlegen, also simulator die App zu geben?
	private ActorRef woldState;

	private void initialize() {
		getSender().tell(Message.INITIALIZED, self());
	}

	private void simulate() throws Exception {
		elapsed = sw.elapsed();
		for (Map.Entry<Node, KeyDef> entry : simulations.entries()) {
			Set<Integer> keys = entry.getValue().getKeys();
			// if(entry.getValue().getType()!=SimulateType.PHYSIC){
			if (keys == null || keys.isEmpty()) {
				if(entry.getValue().getVector()!=null){
					doSimulation(entry.getKey(), entry.getValue().getType(), entry.getValue().getVector());
				}else{
					if(entry.getKey() instanceof Car&&entry.getValue().getType()==SimulateType.DRIVE){
						Car car=(Car) entry.getKey();
						if(car.getWayToTarget()!=null){
//							if(car.getPosition().equals(car.getNextWaypoint().getPOS())){
//								System.out.println("Waypoint reached");
//								car.waypointReached();
//							}
//							if(car.getWayToTarget()!=null){
//								Vector vec=car.getVecToNextTarget();
//								System.out.println("move direction car: "+vec);
//								doSimulation(car, SimulateType.DRIVE, vec);
//							}
							Vector vec=car.getVecToNextTarget(elapsed);
							if(vec!=null){
								System.out.println("move direction car: "+vec);
								doSimulation(car, SimulateType.DRIVE, vec);
							}
							System.out.println("waytoTarget:"+car.getWayToTarget());
						}else{
							if(car.getTarget()!=null){
								System.out.println("Target in sim:"+car.getTarget().getId());
								List<String> list=new LinkedList<String>();
								list.add(car.getTarget().getId());
								woldState.tell(new NodeDeletion(list), getSelf());
								System.out.println("node del");
							}
						}
					}
				}
			} else {
				if (entry.getValue().getMode() == KeyMode.DOWN) {
					boolean contains = false;
					for (Integer i : keys)
						if (pressedKeys.contains(i))
							contains = true;
					if (contains)
						doSimulation(entry.getKey(), entry.getValue().getType(), entry.getValue().getVector());
				} else if (entry.getValue().getMode() == KeyMode.TOGGLE) {
					boolean contains = false;
					for (Integer i : keys)
						if (toggeled.contains(i))
							contains = true;
					if (contains)
						doSimulation(entry.getKey(), entry.getValue().getType(), entry.getValue().getVector());
				} else {
					throw new Exception("Add Key Mode!");
				}
			}
			// }
		}
		// möglich: hier alle modifizierten schicken, nicht mehr nötig da modify
		// matrix getellt
		getSender().tell(Message.DONE, self());
	}

	private void doSimulation(Node node, SimulateType type, Vector vec) {
		if (type == SimulateType.ROTATE) {
			angle += elapsed * (vec.length() * 90);
			Vector v = node.getWorldTransform().getPosition();

			Matrix modify = MatrixImp.translate(v.x(), v.y(), v.z()).mult(
					vecmath.rotationMatrix(vec.x(), vec.y(), vec.z(), angle).mult(MatrixImp.translate(-v.x(), -v.y(), -v.z())));
			node.updateWorldTransform(modify);
			angle = 0;
			woldState.tell(new NodeModification(node.getId(), modify), self());
		} else if (type == SimulateType.TRANSLATE) {
			Matrix modify = MatrixImp.translate(vec.mult(elapsed));
			node.updateWorldTransform(modify);
			woldState.tell(new NodeModification(node.getId(),modify), self());
		} else if (type == SimulateType.DRIVE) {
			Matrix modify = MatrixImp.translate(vec.mult(elapsed));
			node.updateWorldTransform(modify);
			woldState.tell(new NodeModification(node.getId(), modify), self());
		} else if (type == SimulateType.PHYSIC) {
			if (node.force != null) {
				Matrix modify = MatrixImp.translate(node.force.mult((elapsed * 60)));
				node.updateWorldTransform(modify);
				woldState.tell(new NodeModification(node.getId(), modify), self());
				node.force = null;
			}
		} 
		// st end nodemodification
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.LOOP) {
			System.out.println("simulation loop");
			simulate();
		} else if (message == Message.INIT) {
			woldState = getSender();
			initialize();
		} else if (message instanceof KeyState) {
			pressedKeys.clear();
			toggeled.clear();
			pressedKeys.addAll(((KeyState) message).getPressedKeys());
			toggeled.addAll(((KeyState) message).getToggled());
		} else if (message instanceof NodeModification) {
//			System.out.println("Nodes " + nodes);
//			System.out.println("Accesing " + ((NodeModification) message).id);
			if (nodes.containsKey(((NodeModification) message).id)) {
				Node modify = nodes.get(((NodeModification) message).id);
				if (((NodeModification) message).localMod != null) {
					modify.updateWorldTransform(((NodeModification) message).localMod);
				}
			}
		} else if (message instanceof SimulateCreation) {
			SimulateCreation sc = (SimulateCreation) message;
			Node newNode = null;
			if (!nodes.containsKey(sc.id)) {
				// TODO: ein Type reicht nur ein Shape, von den objekten wird
				// nur id und woldtrafo benoetigt.
				// TODO: Generics?
				NodeCreation nc = (NodeCreation) message;
				if (nc.type == ObjectTypes.GROUP) {
					nodes.put(nc.getId(), nodeFactory.groupNode(nc.id));
				} else if (nc.type == ObjectTypes.CUBE) {
					nodes.put(nc.getId(), nodeFactory.cube(nc.id, nc.shader, nc.w, nc.h, nc.d, nc.mass));
				} else if (nc.type == ObjectTypes.SPHERE) {
					nodes.put(nc.getId(), nodeFactory.sphere(nc.id, nc.shader, nc.mass));
				} else if (nc.type == ObjectTypes.CAMERA) {
					nodes.put(((CameraCreation) message).id, nodeFactory.camera(((CameraCreation) message).id));
				} else if (((NodeCreation) message).type == ObjectTypes.OBJECT) {
					nodes.put(nc.getId(), nodeFactory.obj(nc.id, nc.shader, nc.sourceFile, nc.sourceTex, nc.mass));
				} else if (((NodeCreation) message).type == ObjectTypes.CANON) {
					nodes.put(nc.getId(), nodeFactory.canon(nc.id, nc.shader, nc.sourceFile, nc.sourceTex, nc.mass));
				} else if (nc.type == ObjectTypes.CAR) {
					Car car = nodeFactory.car(nc.id, nc.shader, nc.sourceFile, nc.speed, nc.mass);
					nodes.put(nc.id, car);
					System.out.println("got car "+nc.getId());
				} else if (nc.type == ObjectTypes.COIN) {
					nodes.put(nc.id, nodeFactory.coin(nc.id, nc.shader, nc.sourceFile, nc.mass));
				}else {
					throw new Exception("Please implement Type"+sc.getId()+" "+sc.getSimulation());
				}
			}
			newNode = nodes.get(sc.id);
			if (sc.getSimulation() == SimulateType.NONE) {
				//TODO: only remove one keyDef
				for (KeyDef kd : simulations.get(newNode)) {
					if (kd.getKeys().containsAll(sc.getKeys()) && kd.getMode() == sc.getMode()) {
						simulations.remove(newNode, kd);
					}
				}

			} else if (sc.getSimulation() == SimulateType.DRIVE) {
				if (newNode instanceof Car) {
					simulations.removeAll(newNode);
					simulations.put(newNode, new KeyDef(sc.getSimulation(), sc.getWay()));
//					System.out.println("simulator setway:"+sc.getWay());
					((Car)newNode).setWayToTarget(sc.getWay());
					((Car)newNode).setTarget(nodeFactory.coin(sc.getTargetId(), sc.shader, null, 1));
				}
			} else {
				simulations.put(newNode, new KeyDef(sc.getSimulation(), sc.getKeys(), sc.getMode(), sc.getVector()));
				newNode.setLocalTransform(sc.modelmatrix);
				newNode.updateWorldTransform();
			}
		} else if (message instanceof PhysicModification) {
			if (nodes.containsKey(((PhysicModification) message).id)) {
				Node modify = nodes.get(((PhysicModification) message).id);
				modify.setForce((((PhysicModification) message)).force);
				for (KeyDef k : simulations.get(modify)) {
					if (k.getType().equals(SimulateType.PHYSIC)) {
						k.setVector(modify.getForce());
						// doSimulation(modify, k.getType(), k.getVector());
					}
				}
			}
		} else if (message instanceof SingelSimulation) {
			SingelSimulation simulation = (SingelSimulation) message;
			if (nodes.containsKey(simulation.getNodeId()))
				doSimulation(nodes.get(simulation.getNodeId()), simulation.getType(), simulation.getVec());
			else {
				doSimulation(nodeFactory.groupNode(simulation.getNodeId(), simulation.getModelMatrix()), simulation.getType(),
						simulation.getVec());
				// nodes.put(simulation.getNodeId(),
				// nodeFactory.groupNode(simulation.getNodeId(),
				// simulation.getModelMatrix()));
				// doSimulation(nodes.get(simulation.getNodeId()),
				// simulation.getType(), simulation.getVec());
				// nodes.remove(simulation.getNodeId());
			}
		} else if (message instanceof NodeDeletion) {
			NodeDeletion delete = (NodeDeletion) message;
			for (String id : delete.ids) {
				Node modify = nodes.get(id);
				ArrayList<Edge> removeEdges = new ArrayList<>();
				if (modify != null) {
					for (Edge e : modify.getEdges()) {
						removeEdges.add(e);
						// nodes.get(e.getOtherNode(modify).id).removeEdge(e);
					}
					for (Edge e : removeEdges) {
						modify.removeEdge(e);
					}
					nodes.remove(modify);
				}
			}
		}
	}
}