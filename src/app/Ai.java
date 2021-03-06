package app;

import static app.nodes.NodeFactory.nodeFactory;
import static org.lwjgl.openal.AL10.alSourcePlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import vecmath.Vector;
import vecmath.vecmathimp.FactoryDefault;
import vecmath.vecmathimp.MatrixImp;
import vecmath.vecmathimp.VectorImp;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import app.Types.ObjectTypes;
import app.datatype.AStarNodes;
import app.datatype.Level;
import app.datatype.LevelNode;
import app.datatype.Route;
import app.edges.Edge;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeDeletion;
import app.eventsystem.NodeModification;
import app.eventsystem.PhysicModification;
import app.eventsystem.SimulateCreation;
import app.messages.AiInitialization;
import app.messages.Message;
import app.nodes.Node;
import app.nodes.Text;
import app.nodes.shapes.Car;
import app.nodes.shapes.Coin;
import app.nodes.shapes.ObjLoader;
import app.nodes.shapes.Shape;
import app.nodes.shapes.Sphere;


/**
 * @author Benjamin Reemts
 *
 */
public class Ai extends UntypedActor {

	Level level;
	private Map<String, Node> nonAiNodes = new HashMap<String, Node>();
	private Map<String, Car> cars=new HashMap<String, Car>();
	private Map<String, Coin> coins=new HashMap<String, Coin>();
	private Map<String, Shape> impacts=new HashMap<String, Shape>();
	ActorRef simulator;
	private int gameover = 0;
	private int gameover2 = 0;
	private float radCar=0.0f;
	
	private void initialize(Vector levelPosition, float width, float depth) {
		System.out.println("Init Ai");
		level=new Level(levelPosition, width, depth);
		getSender().tell(Message.INITIALIZED, self());
	}
	
	private List<LevelNode> findWayOut(LevelNode startNode){
		List<LevelNode> toCheck=new LinkedList<LevelNode>();
		toCheck.add(startNode);
		List<LevelNode> visited=new LinkedList<LevelNode>();
		TreeMap<LevelNode, AStarNodes> lookAt=new TreeMap<LevelNode, AStarNodes>();
		List<LevelNode> firstPath=new LinkedList<LevelNode>();
		firstPath.add(startNode);
		lookAt.put(startNode, new AStarNodes(0, 0, firstPath));
		AStarNodes bestFree=null;
//		System.out.println("find out");
		while(bestFree==null){
			visited.addAll(toCheck);
//			System.out.println("find way visited:"+visited);
			List<LevelNode> rem=new LinkedList<LevelNode>();
			List<LevelNode> add=new LinkedList<LevelNode>();
			for(LevelNode child:toCheck){
				for(LevelNode edge:child.getEdges()){
					if(!visited.contains(edge)){
						add.add(edge);
//						toCheck.add(edge);
						List<LevelNode> path=new LinkedList<LevelNode>();
//						try{
							path.addAll(lookAt.get(child).getPath());
//						}catch(Exception e){
//							System.out.println("Fail at way out look at "+e.getMessage());
//							System.out.println("lookat:"+lookAt);
//							System.out.print("lookat to child:"+child+" is:"+lookAt.get(child));
//							System.out.println(" path:"+lookAt.get(child).getPath());
//						}
						path.add(edge);
						if(lookAt.containsKey(edge)){
							if(child.getValOfEdge(edge)>0){
								if(lookAt.get(edge).getResistance()>0){
									if(child.lengthtoNode(edge)<lookAt.get(edge).getLength()){
										lookAt.put(edge, new AStarNodes(child.lengthtoNode(edge), child.getValOfEdge(edge), path));
									}
								}else{
									lookAt.put(edge, new AStarNodes(child.lengthtoNode(edge), child.getValOfEdge(edge), path));
								}
							}
						}else{
							lookAt.put(edge, new AStarNodes(child.lengthtoNode(edge), child.getValOfEdge(edge), path));
						}
					}
				}
				rem.add(child);
			}
			for(LevelNode r:rem){
				lookAt.remove(r);
				toCheck.remove(r);
			}
			for(LevelNode a:add)toCheck.add(a);
			//break no more unblocked points in Level
			if(toCheck.isEmpty())return null;
			for(AStarNodes as:lookAt.values()){
//			for(LevelNode v:lookAt.keySet()){
				if(as.getResistance()>0){
//				if(v.getVal()>0){
					if(bestFree!=null){
						if(bestFree.getLength()>as.getLength())bestFree=as;
					}else bestFree=as;
				}
			}
			
		}
		//if nullpointer here check why return in while is not reached
//		System.out.println("WayOut: "+bestFree.getPath());
		List<LevelNode> out=bestFree.getPath();
		out.remove(out.size()-1);
		return out;
	}
	
	private void calcRoute(Car car){
		//TODO: einbauen, dass Coins die schon Target sind geblockt werden und ein Anderes auto bekommt( fincloesestcoin eine liste �bergeben)
		Coin nextCoin=findClosestCoin(car);
		boolean routeStillGood=false;
		if(car.getWayToTarget()!=null){
			double currentWayVal=0;
			LevelNode next=car.getWayToTarget().getFirstWaypoint();
			List<LevelNode> ln=car.getWayToTarget().getWaypoints();
			for(int x=0;x<ln.size()-1;x++){
				
				double val=ln.get(x+1).getValOfEdge(next);
				if(val<0){
					currentWayVal=-1;
					break;
				}
				currentWayVal+=val;
				next=ln.get(x+1);
			}
//			System.out.println("calced"+currentWayVal+" way:"+car.getWayToTarget().getTotalway());
			if(currentWayVal==car.getWayToTarget().getTotalway()){
				routeStillGood=true;
			}
		}
		if(nextCoin!=null&&!routeStillGood){
			LevelNode startNode=level.getNearestinLevel(car.getCenter(), true), target=getNearestNodeinLevel(nextCoin);
			List<LevelNode>path =new LinkedList<LevelNode>();
			if(startNode!=null){
				if(startNode.isBlocked()){
//					System.out.println("StartNode: "+startNode.toString());
					List<LevelNode> wayOut=findWayOut(startNode);
					if(wayOut!=null){
						path.addAll(wayOut);
						//aStar starts over at first in path, sould be startNode oder last node in way out
						Collections.reverse(path);
					}
				}else{
					path.add(startNode);
				}
			}
			if(target!=null&&!path.isEmpty()){
				if(!startNode.getPOS().equals(target.getPOS())){
					TreeMap<LevelNode, AStarNodes> lookAt= new TreeMap<LevelNode, AStarNodes>();
					List<LevelNode> pathWithoutStart=new LinkedList<LevelNode>();
					pathWithoutStart.addAll(path);
					pathWithoutStart.remove(startNode);
					lookAt.put(path.get(0), new AStarNodes(path.get(0).lengthtoNode(target), 0, pathWithoutStart));
//					System.out.println("look at bevor astar:"+lookAt);
					
//					System.out.println("target:"+nextCoin.getId()+" pos:"+nextCoin.getWorldTransform().getPosition()+" inLevel: "+target.getPOS());
//					path.add(startNode);
					
					List<LevelNode> visit=new LinkedList<LevelNode>();
					
					Route way=aStarLoop(path, lookAt, visit, target);
//					Route way=aStar(path, lookAt, visit, target);
					car.setTarget(nextCoin);
//					System.out.println("Level: "+level.toString());
//					System.out.println("ai setway: "+way);
					car.setWayToTarget(way);
					simulator.tell(new SimulateCreation(car.getId(), car.getShader(), car.getSourceFile(), car.getSourceTex(), car.getSpeed(), car.getWorldTransform(), car.getMass(), way, nextCoin.getId()), getSelf());
					lookAt.clear();
					path.clear();
//					return way;
				}
			}
		}else if(nextCoin==null){
			car.setTarget(nextCoin);
			car.setWayToTarget(null);
			simulator.tell(new SimulateCreation(car.getId(), car.getShader(), car.getSourceFile(), car.getSourceTex(), car.getSpeed(), car.getWorldTransform(), car.getMass(), null, null), getSelf());
		}
//		return null;
	}
	
	private Coin findClosestCoin(Car car) {
		float distance = -1;
		Coin nearest = null;
		for (Coin node : coins.values()) {
//			System.out.println("find: "+node.getId());
			boolean isTarget=false;
			for(Car c:cars.values()){
				if(c.getTarget()!=null)if(c.getTarget().getId().equals(node.getId())&&!c.getId().equals(car.getId()))isTarget=true;
			}
			//TODO: filter nicht durch collision erreichbare coins
			if(!level.nearestIsBlocked(node.getCenter())&&!isTarget){
//				System.out.println("is in");
				//TODO: ignore y-Axis
				float tempdistance = node.getWorldTransform().getPosition().sub(car.getWorldTransform().getPosition()).length();
				if (tempdistance < distance || distance<0) {
					distance = tempdistance;
//					System.out.println("distance coin: " + distance);
					nearest = node;
				}
			}
		}
		return nearest;
	}
	
	private Route aStar(List<LevelNode> path, TreeMap<LevelNode, AStarNodes> lookAt, List<LevelNode> visited, LevelNode target) {
		if(starParamsValid(path,lookAt, visited, target)){
			for(LevelNode child:path.get(0).getChilds()){
				if(child.getValOfEdge(path.get(0))<0)visited.add(child);
				if(!visited.contains(child)){
					double resistance = lookAt.get(path.get(0)).getResistance()+child.getValOfEdge(path.get(0)); //resistance till parent + resistance child to parent
					double distance=child.lengthtoNode(target)+resistance; //pytagoras lenght + resistance
//					System.out.print(" distance: "+distance);
					List<LevelNode> pathClone=new LinkedList<LevelNode>();
					pathClone.addAll(path);
					if(lookAt.containsKey(child))if(lookAt.get(child).getLength()<=distance)continue; //keep only shortest way to child
					lookAt.put(child, new AStarNodes(distance, resistance, pathClone));
				}
			}
//			if(path.contains(target))return new Route((int) lookAt.get(target).getLength(), path);
			lookAt.remove(path.get(0));
			visited.add(path.get(0));
			
//			System.out.println(lookAt.toString());
			if(lookAt.isEmpty())return null;
			LevelNode min=Collections.min(lookAt.entrySet(), new Comparator<Map.Entry<LevelNode, AStarNodes>>() {
				@Override
				public int compare(Entry<LevelNode, AStarNodes> o1,
						Entry<LevelNode, AStarNodes> o2) {
					return Double.compare(o1.getValue().getLength(), o2.getValue().getLength());
				}}).getKey();
			
			List<LevelNode> pathMin=new LinkedList<LevelNode>();
			pathMin.add(min);
			pathMin.addAll(lookAt.get(min).getPath());
			if(min.equals(target)){
				//start to move to the next, not to the nearestBase
				double len=lookAt.get(min).getLength()-pathMin.get(pathMin.size()-1).getValOfEdge(pathMin.get(pathMin.size()-2));
				
				pathMin.remove(pathMin.size()-1);
				return new Route(len, pathMin);
			}else{
				return aStar(pathMin, lookAt, visited, target);
			}
		}
		System.out.println("Falsche Parameter �bergeben");
		return null;
	}
	
	private boolean isTarget(LevelNode min, LevelNode target){
		if(min==null)return false;
		if(min.equals(target)){
			return true;
		}
		return false;
	}
	
	private Route aStarLoop(List<LevelNode> path, TreeMap<LevelNode, AStarNodes> lookAt, List<LevelNode> visited, LevelNode target) {
		if(starParamsValid(path,lookAt, visited, target)){
			LevelNode min=null;
			List<LevelNode> pathMin=new LinkedList<LevelNode>();
			while(!isTarget(min,target)){
				for(LevelNode child:path.get(0).getChilds()){
					if(child.getValOfEdge(path.get(0))<0)visited.add(child);
					if(!visited.contains(child)){
//						System.out.println("Val of Child to Edge: "+child+" -->"+path.get(0)+" :"+child.getValOfEdge(path.get(0)));
						double resistance=1;
//						try{
							resistance = lookAt.get(path.get(0)).getResistance()+child.getValOfEdge(path.get(0)); //resistance till parent + resistance child to parent
//						}catch(Exception e){
//							System.out.println("Resistance fail: "+e.getMessage());
//							System.out.println("lookat: "+lookAt);
//							System.out.println("child: "+child+" -->"+path.get(0)+" val:"+child.getValOfEdge(path.get(0)));
//							System.out.println("lookat path0:"+lookAt.get(path.get(0)));
//							System.out.println("lookat res:"+lookAt.get(path.get(0)).getResistance());
//						}
						double distance=child.lengthtoNode(target)+resistance; //pytagoras lenght + resistance
//					System.out.print(" distance: "+distance);
						//keep only shortest way to child
						List<LevelNode> pathClone=new LinkedList<LevelNode>();
						pathClone.addAll(path);
						if(lookAt.containsKey(child)){
							if(lookAt.get(child).getLength()>distance){
								lookAt.put(child, new AStarNodes(distance, resistance, pathClone));
							}
						}else{
							lookAt.put(child, new AStarNodes(distance, resistance, pathClone));
						}
					}else{
//						System.out.println("visited: "+child+" -->"+path.get(0)+" :"+child.getValOfEdge(path.get(0)));
					}
				}
//			if(path.contains(target))return new Route((int) lookAt.get(target).getLength(), path);
				lookAt.remove(path.get(0));
				if(lookAt.isEmpty()){
//					System.out.println("lookatempty");
					return null;
				}
				visited.add(path.get(0));
//			System.out.println(lookAt.toString());
				min=Collections.min(lookAt.entrySet(), new Comparator<Map.Entry<LevelNode, AStarNodes>>() {
					@Override
					public int compare(Entry<LevelNode, AStarNodes> o1,
							Entry<LevelNode, AStarNodes> o2) {
						return Double.compare(o1.getValue().getLength(), o2.getValue().getLength());
					}}).getKey();
//				System.out.println("Pathmin size "+pathMin.size()+" "+pathMin);
				pathMin.clear();
				pathMin.add(min);
				pathMin.addAll(lookAt.get(min).getPath());
				path.clear();
				path.addAll(pathMin);
			}
			if(min!=null){
//				System.out.println("Pathmin size:"+pathMin.size()+" min:"+min+" path:"+pathMin);
				double len=lookAt.get(min).getLength()-pathMin.get(pathMin.size()-1).getValOfEdge(pathMin.get(pathMin.size()-2));
				pathMin.remove(pathMin.size()-1);
				return new Route(len, pathMin);
			}
		}
		return null;
	}
	
	private boolean starParamsValid(List<LevelNode> path, Map<LevelNode, AStarNodes> lookAt, List<LevelNode> visited, LevelNode target){
		if(path!=null&&lookAt!=null&&!lookAt.isEmpty()&&target!=null&&visited!=null) return true;
		return false;
	}

	private void setBlocked(Shape object, boolean setBlock){
		int inLevel=inLevel(object.getCenter(), object.getRadius());
		if(inLevel>=0){
			//mult fix values only for level design
			Vector max=object.getCenter().add(new VectorImp(object.getRadius()*1.15f+this.radCar*1.05f, 0, object.getRadius()*1.15f+this.radCar*1.05f));
//			System.out.println(object.getId()+" block center:"+object.getCenter()+" rad:"+object.getRadius());
			Vector min=object.getCenter().sub(new VectorImp(object.getRadius()+this.radCar, 0, object.getRadius()+this.radCar));
			if(inLevel==0){
//				System.out.println("part start "+object.getId()+" min:"+min.toString()+" max:"+max.toString());
				//partially
				if(inLevel(max,0)<0){
					Vector maxBorder=level.getMaxBorder();
					if(max.z()>maxBorder.z()){
						max=max.sub(new VectorImp(0, 0, (max.z()-maxBorder.z())));
					}
					if(max.x()>maxBorder.x()){
						max=max.sub(new VectorImp((max.x()-maxBorder.x()), 0, 0));
					}
				}
				if(inLevel(min,0)<0){
					Vector minBorder=level.getMinBorder();
					if(min.z()<minBorder.z()){
						min=min.sub(new VectorImp(0, 0, (min.z()-minBorder.z())));
					}
					if(min.x()<minBorder.x()){
						min=min.sub(new VectorImp((min.x()-minBorder.x()), 0, 0));
					}
				}
//				System.out.println("part end "+object.getId()+" min:"+min.toString()+" max:"+max.toString());
			}
			
//			System.out.println("min:"+min.toString()+" max:"+max.toString()+" ID:"+object.getId());
			if(setBlock)level.setBlocked(level.getBiggerPosInLevel(min,false), level.getBiggerPosInLevel(max,true));
			else level.setUnblocked(level.getBiggerPosInLevel(min,false), level.getBiggerPosInLevel(max,true));
			calcNewRouts();
		}
	}
	
	/**
	 * Shows whether an object is in or partially in the Level
	 * @param center Center Vector of the Object
	 * @param rad radius of the Object
	 * @return -1 if the Object is complete out or bigger than the level, 0 if partially in, 1 if the object is completely in.
	 */
	private int inLevel(Vector center, float rad){
		float maxHight=level.getHight();
		for(Car car:cars.values()){
			float tmpHight=car.getCenter().y()+car.getRadius();
			if(tmpHight>maxHight)maxHight=tmpHight;
		}
		if(center.y()-rad<=maxHight){
			return level.inLevel(center, rad);
		}
		return -1;
	}

	private void calcNewRouts(){
		if(!coins.isEmpty()){
			for(Car car:cars.values()){
				calcRoute(car);
//				car.setWayToTarget(r);
//				simulator.tell(new SimulateCreation(car.getId(), r), self());
			}
		}
	}
	
	private void changeText(){
		Node carsT =  nonAiNodes.get("Cars");
		Node coinsT = nonAiNodes.get("Coins");
		Node ballsT=  nonAiNodes.get("Balls");
		
		int carsAmount = 0;
		int coinsAmount = 0;
		int ballsAmount = 0;
		
		
		for(Node n : nonAiNodes.values()){
			if(n instanceof Sphere){
				ballsAmount++;
			}
		}
		for(Node n : cars.values()){
			if(n instanceof Car){
				carsAmount++;
			}
		}	
		for(Node n : coins.values()){
			if(n instanceof Coin){
				coinsAmount++;
			}
		}
		
		if(coinsT instanceof Text){
			((Text) coinsT).setText("Coins: " + coinsAmount);
			NodeModification nm = new NodeModification(coinsT.getId(), FactoryDefault.vecmath.identityMatrix());
			nm.text = ((Text) coinsT).getText();
			sender().tell(nm, self());
			
			if(coinsAmount == 0 && gameover == 0){
				alSourcePlay(Renderer.source4);
				gameover++;
				
			}
		}
		
		if(carsT instanceof Text){
			((Text) carsT).setText("Cars: " + carsAmount);
			NodeModification nm = new NodeModification(carsT.getId(), FactoryDefault.vecmath.identityMatrix());
			nm.text = ((Text) carsT).getText();
			sender().tell(nm, self());
			
			if(carsAmount == 0 && gameover2 ==0 && coinsAmount >0){
				alSourcePlay(Renderer.source6);
				gameover2++;
			}
		}
		
		if(ballsT instanceof Text){
			((Text) ballsT).setText("Balls: " + ballsAmount);
			NodeModification nm = new NodeModification(ballsT.getId(), FactoryDefault.vecmath.identityMatrix());
			nm.text = ((Text) ballsT).getText();
			sender().tell(nm, self());
		}
		
		
		
		
//		((Text) carsT).setText("Cars: " + carsAmount);
//		((Text) ballsT).setText("Balls: " + ballsAmount);
	}
	
	private void aiLoop() {
		System.out.println("Ai Loop");
		if(!coins.isEmpty()){
			for(Car car:cars.values()){
				if(car.getTarget()==null){
					System.out.println("route to new coin");
					//TODO:trigger pickup animation
					if(car.getTarget()!=null)coins.remove(car.getTarget().getId());
//					for(Node n:coins.values())System.out.println("coin:"+n.getId());
					calcRoute(car);
				}
//				car.getVecToNextTarget();
			}
		}
		changeText();
		getSender().tell(Message.DONE, self());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.LOOP) {
			aiLoop();
		} else if (message instanceof AiInitialization) {
			AiInitialization init= (AiInitialization) message;
			this.simulator = (init.getSimulator());
			initialize(init.getCenterPosition(), init.getWidth(), init.getDepth());
		} else if (message instanceof NodeCreation) {
			NodeCreation nc=(NodeCreation) message;
			//TODO: delete Groupnode?
			if (nc.type == ObjectTypes.GROUP) {
				nonAiNodes.put(nc.id, nodeFactory.groupNode(nc.id, nc.getModelmatrix()));
			} else if (nc.type == ObjectTypes.CUBE) {
				nonAiNodes.put(nc.id, nodeFactory.cube(nc.id, nc.shader, nc.w, nc.h, nc.d, nc.mass));
				setBlocked((Shape)nonAiNodes.get(nc.getId()), true);
			} else if (nc.type == ObjectTypes.PIPE) {
				nonAiNodes.put(nc.id, nodeFactory.pipe(nc.id, nc.shader, nc.r, nc.lats, nc.longs,nc.mass));
				setBlocked((Shape)nonAiNodes.get(nc.getId()), true);
			} else if (nc.type == ObjectTypes.SPHERE) {
				nonAiNodes.put(nc.id, nodeFactory.sphere(nc.id, nc.shader, nc.mass, nc.getModelmatrix()));
//				setBlocked((Shape)nonAiNodes.get(nc.getId()), true);
			} else if(nc.type == ObjectTypes.TEXT){
				nonAiNodes.put(nc.getId(),nodeFactory.text(nc.id, nc.getModelmatrix(), nc.getText(), nc.getFont()));
			}	
//			else if (nc.type == ObjectTypes.PLANE) {
//				nonAiNodes.put(nc.id, nodeFactory.plane(nc.id, nc.shader, nc.w, nc.d, nc.hight, nc.mass));
//			}
			else if(nc.type == ObjectTypes.OBJECT){
				ObjLoader obj=nodeFactory.obj(nc.id, nc.shader, nc.sourceFile, null, nc.getModelmatrix(), nc.mass);
				nonAiNodes.put(nc.id, obj);
				setBlocked((Shape)nonAiNodes.get(nc.getId()), true);
			}else if(nc.type == ObjectTypes.CAR){
				Car car=nodeFactory.car(nc.id, nc.shader, nc.sourceFile, null, nc.speed, nc.getModelmatrix(), nc.mass);
				cars.put(nc.id, car);
				if(car.getRadius()>this.radCar)this.radCar=car.getRadius();
			}else if(nc.type == ObjectTypes.COIN){
				coins.put(nc.id, nodeFactory.coin(nc.id, nc.shader, nc.sourceFile, null, nc.getModelmatrix(), nc.mass));
			}else if(((NodeCreation) message).type == ObjectTypes.CANON){
				Node newNode = nodeFactory.canon(nc.id, nc.shader, nc.sourceFile, null, nc.getModelmatrix(), nc.mass);
				nonAiNodes.put(newNode.getId(), newNode);
				setBlocked((Shape)nonAiNodes.get(nc.getId()), true);
			}
		} else if(message instanceof NodeModification){
			NodeModification nm=(NodeModification) message;
			if(nonAiNodes.get(nm.id) instanceof Text){
				((Text) nonAiNodes.get(nm.id)).setText(((NodeModification) message).text);
				
			}
			if(cars.get(nm.id)!=null){
//				System.out.println("Nodemodification ai:\nmatrix alt car: \n"+cars.get(nm.id).getWorldTransform()+ "transformationsmatrix: \n"+nm.localMod);
				setNewMatrix(cars.get(nm.id), nm);
				if(cars.get(nm.id).getRadius()>this.radCar){
					for(Node n:nonAiNodes.values()){
						if(n instanceof Shape){
							Shape s=(Shape)n;
							setBlocked(s, false);
						}
					}
					this.radCar=cars.get(nm.id).getRadius();
					for(Node n:nonAiNodes.values()){
						if(n instanceof Shape){
							Shape s=(Shape)n;
							setBlocked(s, true);
						}
					}
				}
//				System.out.println("matrix neu car: \n"+cars.get(nm.id).getWorldTransform());
//				System.out.println("pso car"+nm.id+" "+cars.get(nm.id).getWorldTransform().getPosition().toString());
			}else{
				if(nonAiNodes.get(nm.id)instanceof Shape){
					if(nm.localMod!=null){
						boolean isRotation=MatrixImp.isRotationMatrix(nm.localMod);
						if(!isRotation)	setBlocked((Shape)nonAiNodes.get(nm.id), false);
						setNewMatrix(nonAiNodes.get(nm.id),nm);
						if(!isRotation) setBlocked((Shape)nonAiNodes.get(nm.id), true);
					}
				}else{
					setNewMatrix(nonAiNodes.get(nm.id),nm);
				}
			}
			setNewMatrix(coins.get(nm.id), nm);
		} else if (message instanceof NodeDeletion){
			NodeDeletion delete = (NodeDeletion)message;
			for(String id: delete.ids){
				if(deleteNode(nonAiNodes.get(id), delete)){
					if(nonAiNodes.get(id) instanceof Shape){
						setBlocked((Shape)nonAiNodes.get(id), false);
					}
					nonAiNodes.remove(id);
				}
				if(deleteNode(cars.get(id), delete)){
					cars.remove(id);
				}
				if(deleteNode(coins.get(id), delete)){
					coins.remove(id);
					for(Car car:cars.values()){
						if(car.getTarget()!=null){
							if(car.getTarget().getId().equals(id)){
								car.setWayToTarget(null);
								car.setTarget(null);
//							simulator.tell(new SimulateCreation(id, car.getId(), car.getWorldTransform(), 2), getSelf());
							}
						}
					}
				}
				if(impacts.containsKey(id)){
					setBlocked(impacts.get(id), false);
					impacts.remove(id);
				}
			}
		}else if(message instanceof PhysicModification){
			PhysicModification pm=(PhysicModification)message;
			if(nonAiNodes.get(pm.getId()) instanceof Shape&&nonAiNodes.get(pm.getId()) != null){
				Shape s=((Shape) nonAiNodes.get(pm.getId())).clone();
				s.setCenter(pm.getForce());
				setBlocked(s, true);
				impacts.put(pm.getId(), s);
			}
		}
			
	}
	
	private void setNewMatrix(Node modify, NodeModification nm){
		if(modify!=null && nm.localMod != null){
			modify.updateWorldTransform(nm.localMod);
		}
	}
	
	private boolean deleteNode(Node modify, NodeDeletion nd){
		boolean deleted=false;
		if(modify!=null &&nd != null){
			ArrayList<Edge> removeEdges = new ArrayList<>();
			//TODO: opt
			for(Edge e: modify.getEdges()){
				removeEdges.add(e);
			}
			for(Edge e : removeEdges){
				modify.removeEdge(e);
			}
			deleted=true;
		}
		return deleted;
	}
	
	/**
	 * @param object
	 * @return the position of the nearest LevelNode, null if there is no unblocked LevelNode
	 */
	private LevelNode getNearestNodeinLevel(Node object){
		LevelNode nearestVec = null;
		if(object instanceof Shape){
			nearestVec = level.getNearestinLevel(((Shape)object).getCenter(), false);
		}else{
			nearestVec = level.getNearestinLevel(object.getWorldTransform().getPosition(), false);
		}
		//Tagetposition.sub(startPosition)
//		Vector translate=(nearestVec.sub(object.getWorldTransform().getPosition()));
//		if(!translate.equals(new VectorImp(0, 0, 0)))simulator.tell(new SingelSimulation(object.id, SimulateType.TRANSLATE, translate,object.getWorldTransform()), self());
		return nearestVec;
	}

}
