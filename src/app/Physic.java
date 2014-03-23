package app;

import static app.nodes.NodeFactory.nodeFactory;
import static org.lwjgl.openal.AL10.alSourcePlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import vecmath.Matrix;
import vecmath.Vector;
import vecmath.vecmathimp.MatrixImp;
import vecmath.vecmathimp.VectorImp;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import app.Types.ObjectTypes;
import app.Types.PhysicType;
import app.Types.SimulateType;
import app.edges.Edge;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeDeletion;
import app.eventsystem.NodeModification;
import app.eventsystem.PhysicModification;
import app.eventsystem.SimulateCreation;
import app.messages.Message;
import app.messages.PhysicInitialization;
import app.messages.SingelSimulation;
import app.nodes.Node;
import app.nodes.shapes.Car;
import app.nodes.shapes.Coin;
import app.nodes.shapes.Cube;
import app.nodes.shapes.ObjLoader;
import app.nodes.shapes.Shape;
import app.nodes.shapes.Sphere;
import app.toolkit.StopWatch;

public class Physic extends UntypedActor {

	private Map<String, Node> nodes = new HashMap<String, Node>();
	private Map<String, Node> nodesCollisionOnly = new HashMap<String, Node>();
	private Map<String, Vector> impacts = new HashMap<String, Vector>();
	ActorRef simulator;
	ActorRef ai;
	private StopWatch zeit = new StopWatch();
	private Vector ground = new VectorImp(0f, -0.0981f, 0f);
	private float elapsedaverage = 0;
	private float elapsed = 0;
	private float elapsedCounter = 1;
	private Vector floor;

	private void initialize() {
		getSender().tell(Message.INITIALIZED, self());
		System.out.println("Physic initialised");
		elapsedaverage = zeit.elapsed();
	}

	public void physic() {
		System.out.println("physic loop");
		if(elapsedCounter == 1){
			elapsedaverage = 0;
			elapsedCounter++;
		}
	
		float tmpelapsed =zeit.elapsed();
		elapsed = tmpelapsed;
		elapsedaverage = (elapsedaverage + tmpelapsed)/elapsedCounter;
		NodeDeletion delete = new NodeDeletion();
		for (Node n : nodes.values()) {
			if (collisionGround(n) == 0 && collisionObjects(n).isEmpty()) {
			
				n.setForce((n.getVelocity().add(new VectorImp(0, ground.y()* n.getMass()* elapsed, 0))));
				
				n.setVelocity(n.getForce());
				
				SimulateCreation sc=new SimulateCreation(n.getId(),n.getWorldTransform(), SimulateType.PHYSIC, n.getForce());	
//				PhysicModification p = new PhysicModification(n.getId(), n.getForce());
				
				simulator.tell(sc, self());

			} else if (collisionGround(n) != 0
					&& collisionObjects(n).isEmpty()) {
				
				if(((Shape)n).getLifetimeCounter() > 0) {
					
					System.out.println("lifetimecounter: " + ((Shape)n).getLifetimeCounter());
					((Shape)n).setLifetimeCounter(((Shape)n).getLifetimeCounter()-1);
					VectorImp opposite = oppositeDirectionGround(n);
					n.setVelocity(opposite);
					VectorImp reduce = reduceVelocityGround(n, 0.8f);
					n.setVelocity(reduce);
					// TODO Erdanziehungskraft m*g?
	//				n.setForce((n.getVelocity().add(new VectorImp(0, ground.y()* elapsed, 0))));
					// TODO Masse einabauen, dann impuls setzen und dann velocity
					n.setForce(n.getVelocity());
					n.setVelocity(n.getForce());
	//				
					/*TODO: nicht mehr verschieben, ansonsten SingelSimulation
					 *  Oder Map mit Nodes,Matrix und nur intern verschieben, damit collision nix mehr erkennt und den richtingen verschiebungsvektor setzt, 
					 *  danach bei nModifikation recive aus der map die matrix holen, damit interne raprä. mit anderen actors gleich bleibt
					 *  vorteil, interne verschiebung nicht sichtbar. also sieht man nur die kugel in das andere object rein gehen, aber danach auch wieder raus
					 */
					
					float differenceinfloor = collisionGround(n);
	//				float differenceinfloor = (float) Math.sqrt((float) Math.pow((n.getWorldTransform().getPosition().y() - floor.y()),2));
					VectorImp vec = new VectorImp(0, differenceinfloor + 0.2f, 0); // 1 ist der Radius der Kugeln + 0.01 damit immer knapp über dem boden
					SingelSimulation ss = new SingelSimulation(n.getId(), SimulateType.FIXVALUE, vec, n.getWorldTransform());
	//				Matrix modify=MatrixImp.translate(vec);
	//	    		n.updateWorldTransform(modify);
	//	    		getSender().tell(new NodeModification(n.id,modify), self());
					
					simulator.tell(ss, self());
				
					SimulateCreation sc=new SimulateCreation(n.getId(),n.getWorldTransform(), SimulateType.PHYSIC, n.getForce());	
//					PhysicModification p1 = new PhysicModification(n.getId(), n.getForce());
					
					simulator.tell(sc, self());	
				}
				else{
					System.out.println("lifetimecounter unten: " + ((Shape)n).getLifetimeCounter());
					delete.ids.add(n.getId());
				}
				

			} else if (collisionGround(n) == 0
					&& !collisionObjects(n).isEmpty()) {

				delete.ids.add(n.getId());
				ArrayList<Node> collision = new ArrayList<>(collisionObjects(n));
				
				if(n instanceof Car ||  n instanceof Cube || n instanceof ObjLoader){
					
					for(Node colwith : collision){
						if(colwith instanceof Coin && collision.size()==1){
							delete.ids.remove(n.getId());
						}
					}
				}
				if(n instanceof Sphere && collision.size() ==1){
					for (Node colwith : collision){
						if(colwith instanceof Coin){
							delete.ids.remove(n.getId());
							System.out.println("klappt das? ");				
							n.setForce((n.getVelocity().add(new VectorImp(0, ground.y()* n.getMass()* elapsed, 0))));
					
							n.setVelocity(n.getForce());
					
							SimulateCreation sc=new SimulateCreation(n.getId(),n.getWorldTransform(), SimulateType.PHYSIC, n.getForce());
						
							simulator.tell(sc, self());	
							}
					
						}
				}
				if(n instanceof Sphere){
					for (Node colwith : collision){
						if(colwith instanceof Car){
							alSourcePlay(Renderer.source3);
							
						}
					}
				}
				if(n instanceof Coin){
//					simulator.tell(msg, sender);
				}

							
//				delete.ids.add(n.getId());
				

			} else if(collisionGround(n) !=0 && !collisionObjects(n).isEmpty()){
				
				delete.ids.add(n.getId());
				ArrayList<Node> collision = new ArrayList<>(collisionObjects(n));
				
					if(n instanceof Car ||  n instanceof Cube || n instanceof ObjLoader){
					
						for(Node colwith : collision){
							if(colwith instanceof Coin && collision.size()==1){
							delete.ids.remove(n.getId());
							}
						}
					}
					if(n instanceof Sphere && collision.size() ==1){
						for (Node colwith : collision){
							if(colwith instanceof Coin){
								delete.ids.remove(n.getId());
								System.out.println("klappt das? ");				
								n.setForce((n.getVelocity().add(new VectorImp(0, ground.y()* n.getMass()* elapsed, 0))));
						
								n.setVelocity(n.getForce());
						
								SimulateCreation sc=new SimulateCreation(n.getId(),n.getWorldTransform(), SimulateType.PHYSIC, n.getForce());
							
								simulator.tell(sc, self());	
								}
						
							}
					}
					if(n instanceof Sphere){
						for (Node colwith : collision){
							if(colwith instanceof Car){
								alSourcePlay(Renderer.source3);
								
							}
						}
					}
					if(n instanceof Coin){
						
						delete.ids.remove(n.getId());
					}
				}
//			Vector impact = collisionGroundPosition(n);
//			System.out.println("IMpact oben: " + impact.toString());
		}
		List<Node> remCoin=new LinkedList<Node>();
		for (Node n : nodesCollisionOnly.values()) {
			ArrayList<Node> collision = new ArrayList<Node>(collisionObjects(n));
			if(!collision.isEmpty() ){
				delete.ids.add(n.getId());
					
				if(n instanceof Car ||  n instanceof Cube || n instanceof ObjLoader){
						
					for(Node colwith : collision){
						if(colwith instanceof Coin && collision.size()==1){
							delete.ids.remove(n.getId());
						}
					}
				}
			
				if(n instanceof Coin){
					System.out.println("colli with coin");
					for(Node col:collision){
						if(col instanceof Car){
							delete.ids.remove(n.getId());
							remCoin.add(n);
							Vector carhight=new VectorImp(((Car) col).getPosition().x(), ((Car) col).getPosition().y()+((Car) col).getRadius(), ((Car) col).getPosition().z());
							System.out.println("tell pickup simulation");
							simulator.tell(new SimulateCreation(n.getId(), col.getId(), col.getWorldTransform(), 1, carhight), getSelf());
						} else{
							delete.ids.remove(n.getId());
						}
					}
				}
				
			}
			
		}
		for(Node n:remCoin)nodesCollisionOnly.remove(n.getId());
		
		if(delete.ids.isEmpty() != true){
			for(String id: delete.ids){
				nodes.remove(id);
			}
			for(String id: delete.ids){
				nodesCollisionOnly.remove(id);
			}
			getSender().tell(delete, self());
			
		}
		
		getSender().tell(Message.DONE, self());
//		System.out.println("Impacts: " + impacts.toString());
	}

	// TODO: Im Moment gibt er die Node aus der liste nodes zuerst aus + Problem wenn Collision mit mehreren Objekten vorhanden ist
	// 		 Mögliche Lösung wäre eine Liste von nodes anzulegen und diese zurückzugeben.
	private ArrayList<Node> collisionObjects(Node n) {
		float distance = 0;
		float radiuses = 0;
		ArrayList<Node> colObjects = new ArrayList<Node>();
		for (Node node : nodes.values()) {
			if (!node.equals(n)) {
//				System.out.println("Center n: " + ((Shape) n).getCenter());
				distance = ((Shape) n).getCenter().sub(((Shape) node).getCenter()).length();
				radiuses = (((Shape) n).getRadius() + ((Shape) node).getRadius());
//				System.out.println("Radius n: " + ((Shape) n).getRadius()
//						+ "Radius node: " + ((Shape) node).getRadius()
//						+ "distance1: " + distance + "radiuses1: " + radiuses);
				if (distance < radiuses) {
//					System.out.println("distance2: " + distance + "radiuses2: "
//							+ radiuses);
					colObjects.add(node);
				}
			}
		}
		for (Node node : nodesCollisionOnly.values()) {
			if (!node.equals(n)) {
//				System.out.println("Center n: " + ((Shape) n).getCenter());
				distance = ((Shape) n).getCenter().sub(((Shape) node).getCenter()).length();
				radiuses = (((Shape) n).getRadius() + ((Shape) node).getRadius());
//				System.out.println(n.getId()+" Radius n: " + ((Shape) n).getRadius()
//						+ "Radius "+node.getId()+": " + ((Shape) node).getRadius()
//						+ "distance1: " + distance + "radiuses1: " + radiuses);
				if (distance < radiuses) {
//					System.out.println("distance2: " + distance + "radiuses2: "
//							+ radiuses);
					colObjects.add(node);
				}
			}
		}
		return colObjects;
	}
	
	private void collisionGroundPosition(String id, Node n){
		
		float durchlauf = 0;
//		System.out.println("collision ground posi: " + n.getId());
		
		
				
		while(collisionGround(n)==0){
			
//			System.out.println("Durchlauf Nr: " + durchlauf);
			
			n.setForce((n.getVelocity().add(new VectorImp(0, ground.y()* n.getMass()* elapsedaverage, 0))));
			
			n.setVelocity(n.getForce());
			
			Matrix modify=MatrixImp.translate(n.getForce());
    		n.updateWorldTransform(modify);
			
    		durchlauf++;
		}
		VectorImp impact = new VectorImp(n.getWorldTransform().getPosition().x(), floor.y(), n.getWorldTransform().getPosition().z());
		
		System.out.println("Aufprallort: " + n.getId() + impact + "Elapsed: " + elapsedaverage);
		impacts.put(id, impact);
		
		PhysicModification tellAi = new PhysicModification(id, impact);
		//TODO: activate when test done
		ai.tell(tellAi, self());
		
		
	}
	

	private float collisionGround(Node n) {
		float distance = 0;
//		float radiuses = 0;
		distance=(((Shape) n).getCenter().y()- ((Shape)n).getRadius()) - floor.y();
		if(distance<0)return Math.abs(distance);
		return 0;
//		
////		System.out.println("flooooooor: " + floor.y());
//		distance = (float) Math.sqrt((float) Math.pow(((Shape) n).getCenter().y() - floor.y(),2));
//		radiuses = ((Shape) n).getRadius();
////		System.out.println("distance ground: " + distance + " radiuses ground: " + radiuses);
//			
//		if(distance < radiuses){
//			float inground = radiuses -distance;
//					return inground;
//				
//		
//		}
//		return 0;
	}
	
	private VectorImp oppositeDirection(Node n){
		float x = n.getVelocity().x();
		float y = n.getVelocity().y();
		float z = n.getVelocity().z();
		
		x = -1* x;
		
		VectorImp newVelo = new VectorImp(x, y, z);
		
		return newVelo;
		
		
	}
	
	private VectorImp oppositeDirectionGround(Node n){
		float x = n.getVelocity().x();
		float y = n.getVelocity().y();
		float z = n.getVelocity().z();
		
		y = -1* y;
		
		VectorImp newVelo = new VectorImp(x, y, z);
		
		return newVelo;
		
	}
	
	private VectorImp reduceVelocityGround(Node n, Float reduceBy){
		
		float x = n.getVelocity().x();
		float y = n.getVelocity().y();
		float z = n.getVelocity().z();
		
		y = reduceBy *y;
		
		VectorImp newVelo = new VectorImp(x, y, z);
		
		return newVelo;
	}

	public void onReceive(Object message) throws Exception {
		if (message == Message.LOOP) {
			physic();
		} else if (message instanceof PhysicInitialization) {
			this.simulator = (((PhysicInitialization) message).simulator);
			this.ai = (((PhysicInitialization) message).ai);
			initialize();
		} else if (message instanceof NodeCreation) {
			NodeCreation nc=(NodeCreation)message;
			if(nc.getPhysicType()!=null){
				Node newNode=null;
				if (nc.type == ObjectTypes.GROUP) {
					newNode = nodeFactory.groupNode(nc.id, nc.getModelmatrix());
				} else if (nc.type == ObjectTypes.CUBE) {
					newNode = nodeFactory.cube(nc.id,nc.shader,nc.w, nc.h,	nc.d, nc.mass);
					if ((nc.impulse != null)) {
						Vector impulse = (nc.impulse);
						float newx = impulse.x()/newNode.getMass();
						float newy = impulse.y()/newNode.getMass();
						float newz = impulse.z()/newNode.getMass();
						VectorImp newimpulse = new VectorImp(newx, newy, newz);
						newNode.setVelocity(newimpulse);
					}
				} else if (nc.type == ObjectTypes.SPHERE) {
					newNode = nodeFactory.sphere(nc.id,nc.shader, nc.mass, nc.getModelmatrix());
					//TODO: wtf ist denn hier los?
					Node newNode2 = nodeFactory.sphere("hallo",nc.shader, nc.mass, nc.getModelmatrix());
					
					if ((nc.impulse != null)) {
						Vector impulse = (nc.impulse);
						
						float newx = impulse.x()/newNode.getMass();
						float newy = impulse.y()/newNode.getMass();
						float newz = impulse.z()/newNode.getMass();
						
						VectorImp newimpulse = new VectorImp(newx, newy, newz);
						newNode.setVelocity(newimpulse);
						newNode2.setVelocity(newimpulse);
						
					}
					collisionGroundPosition(newNode.getId(), newNode2);
				} else if(nc.type == ObjectTypes.OBJECT){
					newNode = nodeFactory.obj(nc.id, nc.shader, nc.sourceFile, null, nc.getModelmatrix(), nc.mass);
					if ((nc.impulse != null)) {
						Vector impulse = (nc.impulse);
						float newx = impulse.x()/newNode.getMass();
						float newy = impulse.y()/newNode.getMass();
						float newz = impulse.z()/newNode.getMass();
						
						VectorImp newimpulse = new VectorImp(newx, newy, newz);
						newNode.setVelocity(newimpulse);
					}
				} else if(nc.type == ObjectTypes.CAR){
					newNode = nodeFactory.car(nc.id, nc.shader, nc.sourceFile, null, nc.speed, nc.getModelmatrix(), nc.mass);
					if ((nc.impulse != null)) {
						Vector impulse = (nc.impulse);
						float newx = impulse.x()/newNode.getMass();
						float newy = impulse.y()/newNode.getMass();
						float newz = impulse.z()/newNode.getMass();
						
						VectorImp newimpulse = new VectorImp(newx, newy, newz);
						newNode.setVelocity(newimpulse);
					}
				} else if(nc.type == ObjectTypes.COIN){
					newNode = nodeFactory.coin(nc.id, nc.shader, nc.sourceFile, nc.getModelmatrix(), nc.mass);
					if ((nc.impulse != null)) {
						Vector impulse = (nc.impulse);
						float newx = impulse.x()/newNode.getMass();
						float newy = impulse.y()/newNode.getMass();
						float newz = impulse.z()/newNode.getMass();
						
						VectorImp newimpulse = new VectorImp(newx, newy, newz);
						newNode.setVelocity(newimpulse);
					}
				} 
					
								
				if(nc.physicType == PhysicType.Physic_complete){
					nodes.put(newNode.getId(), newNode);
				}
				if(nc.physicType == PhysicType.Collision_only){
					nodesCollisionOnly.put(newNode.getId(), newNode);
				}
			}
			if(nc.type == ObjectTypes.PLANE){
				floor = nodeFactory.plane(nc.getId(), nc.getShader(), nc.getW(), nc.getD(), nc.getH(), nc.getMass()).getWorldTransform().getPosition();
			}
		} else if (message instanceof NodeModification) {
			NodeModification nm=(NodeModification)message;
			// System.out.println("NODEMODIFICATION!!!!!");
			if (nodes.containsKey(nm.id)) {
				// System.out.println("NodeModification");

				// System.out.println("Nodes " + nodes);
				// System.out.println("Accesing "
				// + nm.id);

				Node modify = nodes.get(nm.id);

				if (nm.localMod != null) {
					// modify.setLocalTransform(((NodeModification)
					// message).localMod);
					// modify.updateWorldTransform();
					modify.updateWorldTransform(nm.localMod);
				}

			}
			if (nodesCollisionOnly.containsKey(nm.id)) {
				Node modify = nodesCollisionOnly.get(nm.id);
				if (nm.localMod != null) {
					modify.updateWorldTransform(nm.localMod);
				}
			}
		}
		else if (message instanceof NodeDeletion){
			NodeDeletion delete = (NodeDeletion)message;
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
			for(String id: delete.ids){
				Node modify1 = nodesCollisionOnly.get(id);
				ArrayList<Edge> removeEdges = new ArrayList<>(); 
				if(modify1!=null){
				for(Edge e: modify1.getEdges()){
					removeEdges.add(e);
//					nodes.get(e.getOtherNode(modify).id).removeEdge(e);
					
				}
				for(Edge e : removeEdges){
					modify1.removeEdge(e);
				}
			
				nodesCollisionOnly.remove(modify1);
				}
			}
		}
		
	}
}
