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
import vecmath.vecmathimp.VectorImp;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import app.Types.KeyMode;
import app.Types.ObjectTypes;
import app.Types.SimulateType;
import app.datatype.SimDef;
import app.edges.Edge;
import app.eventsystem.CameraCreation;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeDeletion;
import app.eventsystem.NodeModification;
import app.eventsystem.SimulateCreation;
import app.messages.KeyState;
import app.messages.Message;
import app.messages.SingelSimulation;
import app.nodes.Node;
import app.nodes.shapes.Car;
import app.nodes.shapes.Coin;
import app.nodes.shapes.Shape;
import app.toolkit.StopWatch;

public class Simulator extends UntypedActor {

	private Map<String, Node> nodes = new HashMap<String, Node>();
	private SetMultimap<Node, SimDef> simulations = HashMultimap.create();
	private Set<Integer> pressedKeys = new HashSet<Integer>();
	private Set<Integer> toggeled = new HashSet<Integer>();
	private StopWatch sw = new StopWatch();
	private float elapsed = 0;
	// TODO:was besseres �berlegen, also simulator die App zu geben?
	private ActorRef worldState;

	private void initialize() {
		getSender().tell(Message.INITIALIZED, self());
	}

	private void simulate() throws Exception {
		elapsed = sw.elapsed();
		Map<Node, SimDef> remove=new HashMap<Node, SimDef>();
		for (Map.Entry<Node, SimDef> entry : simulations.entries()) {
			Set<Integer> keys = entry.getValue().getKeys();
			// if(entry.getValue().getType()!=SimulateType.PHYSIC){
			if (keys == null || keys.isEmpty()) {
				if(entry.getValue().getVector()!=null){
					doSimulation(entry.getKey(), entry.getValue().getType(), entry.getValue().getVector());
				}else{
					if(entry.getKey() instanceof Car&&entry.getValue().getType()==SimulateType.DRIVE){
						Car car=(Car) entry.getKey();
						if(car.getWayToTarget()!=null){
							Vector vec=car.getVecToNextTarget(elapsed);
							if(vec!=null){
								System.out.println("move direction car: "+vec);
								doSimulation(car, SimulateType.DRIVE, vec);
							}
							System.out.println("simu waytoTarget:"+car.getWayToTarget());
						}
					}
					if(entry.getValue().getType()==SimulateType.PICKUP&&entry.getKey() instanceof Shape){
						if(nodes.containsKey(entry.getValue().getReferenzId())&&entry.getValue().getTimes()>0){
							Node ref=nodes.get(entry.getValue().getReferenzId());
							Shape s=(Shape)entry.getKey();
							Vector up=new VectorImp(0, s.getRadius()*8, 0);
							Vector posCoin=s.getWorldTransform().getPosition();
							Vector posRef=ref.getWorldTransform().getPosition();
							//If coinY == (car+up)y --> down
							System.out.println("Time ai "+entry.getValue().getTimes());
							System.out.println("pos coin: "+posCoin+ " pos car: "+posRef);
							//If coinY == carY && isdown --> up
							if(entry.getValue().timesDown()){
								entry.getValue().multScale(-1);
								System.out.println("Time down "+entry.getValue().getTimes());
							}
							posCoin=new VectorImp(posCoin.x(), 0, posCoin.z());
							posRef=new VectorImp(posRef.x(), 0, posRef.z());
							//verschiebungsvector= zielpunkt- startpunkt
							//up only a part(scale) + trans over actual car pos 
//							Vector trans=up.mult(entry.getValue().scale).add((posCoin.sub(posRef)));
							System.out.println("up: "+up + " mult: "+up.mult(entry.getValue().getScale()));
							System.out.println("zu ueber car: "+posRef.sub(posCoin));
							
							Vector trans=posRef.sub(posCoin).add(up.mult(entry.getValue().getScale()));
							doSimulation(entry.getKey(), entry.getValue().getType(), trans);
							if(entry.getValue().getTimes()==0){
								System.out.println("rem coin"+s.getId());
								List<String> ids=new LinkedList<String>();
								ids.add(entry.getKey().getId());
								worldState.tell(new NodeDeletion(ids), getSelf());
								remove.put(entry.getKey(), entry.getValue());
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
		// m�glich: hier alle modifizierten schicken, nicht mehr n�tig da modify
		// matrix getellt
		for(Map.Entry<Node, SimDef> rem:remove.entrySet()){
			simulations.remove(rem.getKey(), rem.getValue());
		}
		getSender().tell(Message.DONE, self());
	}

	private void doSimulation(Node node, SimulateType type, Vector vec) {
		if (type == SimulateType.ROTATE) {
			float angle = elapsed * (vec.length() * 90);
			Vector v = node.getWorldTransform().getPosition();

			Matrix modify = MatrixImp.translate(v.x(), v.y(), v.z()).mult(
					vecmath.rotationMatrix(vec.x(), vec.y(), vec.z(), angle).mult(MatrixImp.translate(-v.x(), -v.y(), -v.z())));
			node.updateWorldTransform(modify);
			worldState.tell(new NodeModification(node.getId(), modify), self());
		} else if (type == SimulateType.TRANSLATE) {
			Matrix modify = MatrixImp.translate(vec.mult(elapsed));
			node.updateWorldTransform(modify);
			worldState.tell(new NodeModification(node.getId(),modify), self());
		} else if (type == SimulateType.DRIVE) {
			Matrix modify = MatrixImp.translate(vec.mult(elapsed));
			node.updateWorldTransform(modify);
			worldState.tell(new NodeModification(node.getId(), modify), self());
		} else if (type == SimulateType.PHYSIC) {
			if (node.getForce() != null) {
				Matrix modify = MatrixImp.translate(node.getForce().mult((elapsed * 60)));
				node.updateWorldTransform(modify);
				worldState.tell(new NodeModification(node.getId(), modify), self());
				node.setForce(null);
			}
		} else if (type == SimulateType.FIXVALUE) {
			Matrix modify = MatrixImp.translate(vec);
			node.updateWorldTransform(modify);
			worldState.tell(new NodeModification(node.getId(),modify), self());
		}else if (type ==SimulateType.PICKUP){
			float angle = elapsed *  90 * vec.length()*100;
			System.out.println("vec len"+vec.length());
			Vector v = node.getWorldTransform().getPosition();
			Matrix rot=vecmath.rotationMatrix(0, 1, 0, angle);
			Matrix modify =vecmath.identityMatrix();
			modify = MatrixImp.translate(v.x(), v.y(), v.z()).mult(rot.mult(MatrixImp.translate(-v.x(), -v.y(), -v.z())));
			modify = vecmath.translationMatrix(vec).mult(modify);
			node.updateWorldTransform(modify);
			worldState.tell(new NodeModification(node.getId(), modify), self());
		}
		// st end nodemodification
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.LOOP) {
			System.out.println("simulation loop");
			simulate();
		} else if (message == Message.INIT) {
			worldState = getSender();
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
				System.out.println("not in "+sc.id);
				// TODO: ein Type reicht nur ein Shape, von den objekten wird
				// nur id und woldtrafo benoetigt.
				// TODO: Generics?
				NodeCreation nc = (NodeCreation) message;
				if (nc.type == ObjectTypes.GROUP) {
					if(nc.getModelmatrix()!=null)nodes.put(nc.getId(), nodeFactory.groupNode(nc.id, nc.getModelmatrix()));
					else nodes.put(nc.getId(), nodeFactory.groupNode(nc.id));
				} else if (nc.type == ObjectTypes.CUBE) {
					nodes.put(nc.getId(), nodeFactory.cube(nc.id, nc.shader, nc.w, nc.h, nc.d, nc.mass));
				} else if (nc.type == ObjectTypes.SPHERE) {
					nodes.put(nc.getId(), nodeFactory.sphere(nc.id, nc.shader, nc.mass));
				} else if (nc.type == ObjectTypes.CAMERA) {
					nodes.put(((CameraCreation) message).id, nodeFactory.camera(((CameraCreation) message).id));
				} else if (nc.type == ObjectTypes.OBJECT) {
					nodes.put(nc.getId(), nodeFactory.obj(nc.id, nc.shader, nc.sourceFile, nc.sourceTex, nc.mass));
				} else if (nc.type == ObjectTypes.CANON) {
					nodes.put(nc.getId(), nodeFactory.canon(nc.id, nc.shader, nc.sourceFile, nc.sourceTex, nc.mass));
				} else if (nc.type == ObjectTypes.CAR) {
					Car car = nodeFactory.car(nc.id, nc.shader, nc.sourceFile, nc.speed, nc.mass);
					nodes.put(nc.id, car);
					System.out.println("got car "+nc.getId());
				} else if (nc.type == ObjectTypes.COIN) {
					//TODO: because of pickup animation
					nodes.put(nc.id, nodeFactory.coin(nc.id, nc.shader, nc.sourceFile, nc.mass));
				} else if(nc.type == ObjectTypes.TEXT){
					nodes.put(nc.getId(),nodeFactory.text(nc.id, nc.getModelmatrix(), nc.getText(), nc.getFont()));
				} else {
					throw new Exception("Please implement Type"+sc.getId()+" "+sc.getSimulation());
				}
			}
			newNode = nodes.get(sc.id);
			if (sc.getSimulation() == SimulateType.NONE) {
				//TODO: only remove one keyDef
				for (SimDef kd : simulations.get(newNode)) {
					if (kd.getKeys().containsAll(sc.getKeys()) && kd.getMode() == sc.getMode()) {
						simulations.remove(newNode, kd);
					}
				}

			} else if (sc.getSimulation() == SimulateType.DRIVE) {
				if (newNode instanceof Car) {
					simulations.removeAll(newNode);
					simulations.put(newNode, new SimDef(sc.getSimulation(), sc.getWay()));
//					System.out.println("simulator setway:"+sc.getWay());
					((Car)newNode).setWayToTarget(sc.getWay());
					((Car)newNode).setTarget(nodeFactory.coin(sc.getTargetId(), sc.shader, null, 1));
				}
			} else if(sc.getSimulation() ==SimulateType.PHYSIC){
				newNode.setForce(sc.getVector());
				if(!simulations.containsKey(newNode)){
					simulations.put(newNode, new SimDef(sc.getSimulation(), sc.getKeys(), sc.getMode(), sc.getVector()));
				}
				for (SimDef k : simulations.get(newNode)){
					if (k.getType().equals(SimulateType.PHYSIC)){
						k.setVector(newNode.getForce());
						// doSimulation(modify, k.getType(), k.getVector());
					}
				}
			} else if(sc.getSimulation() ==SimulateType.PICKUP){
				//TODO: kick from ai
				Node ref=nodes.get(sc.getTargetId());
				if(ref==null){
					Node n=nodeFactory.groupNode(sc.getTargetId(), sc.getModelmatrix());
					nodes.put(sc.getTargetId(), n);
					ref=n;
				}
				SimDef sd =new SimDef(sc.getTargetId(), sc.getTimes());
				if(ref instanceof Car)sd.multScale((float) ((((Car) ref).getSpeed())));
				doSimulation(newNode, SimulateType.FIXVALUE, sc.getVector().sub(newNode.getWorldTransform().getPosition()));
				simulations.put(newNode, sd);
			}
			else if(sc.getSimulation()!=null) {
				simulations.put(newNode, new SimDef(sc.getSimulation(), sc.getKeys(), sc.getMode(), sc.getVector()));
				newNode.setLocalTransform(sc.modelmatrix);
				newNode.updateWorldTransform();
			}
		} /*else if (message instanceof PhysicModification) {
			PhysicModification pm=(PhysicModification)message;
			if (nodes.containsKey(pm.getId())) {
				Node modify = nodes.get(pm.getId());
				modify.setForce(pm.getForce());
				for (KeyDef k : simulations.get(modify)) {
					if (k.getType().equals(SimulateType.PHYSIC)) {
						k.setVector(modify.getForce());
						// doSimulation(modify, k.getType(), k.getVector());
					}
				}
			}
		}*/ else if (message instanceof SingelSimulation) {
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
					for (Map.Entry<Node, SimDef> entry : simulations.entries()) {
						if(entry.getValue().getReferenzId()!=null){
							if(entry.getValue().getReferenzId().equals(id)){
								simulations.remove(entry.getKey(), entry.getValue());
							}
						}
					}
					if(!(modify instanceof Coin))nodes.remove(modify);
				}
			}
		}
	}
}