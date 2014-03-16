package app;

import static app.nodes.NodeFactory.nodeFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import vecmath.Vector;
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
import app.nodes.shapes.Car;
import app.nodes.shapes.Coin;
import app.nodes.shapes.Shape;

public class Ai extends UntypedActor {

	Level level;
	private Map<String, Node> nonAiNodes = new HashMap<String, Node>();
	private Map<String, Car> cars=new HashMap<String, Car>();
	private Map<String, Coin> coins=new HashMap<String, Coin>();
	private Map<String, Shape> impacts=new HashMap<String, Shape>();
	ActorRef simulator;

	private void initialize(Vector levelPosition, float width, float depth) {
		System.out.println("Init Ai");
		level=new Level(levelPosition, width, depth);
		getSender().tell(Message.INITIALIZED, self());
	}
	
	private void calcRoute(Car car){
		//TODO: einbauen, dass Coins die schon Target sind geblockt werden und ein Anderes auto bekommt( fincloesestcoin eine liste übergeben)
		Coin nextCoin=findClosestCoin(car);
		boolean routeStillGood=false;
		if(car.getWayToTarget()!=null){
			double currentWayVal=0;
			LevelNode last=null;
			for(LevelNode ln:car.getWayToTarget().getWaypoints()){
				if(last!=null){
					currentWayVal+=ln.getValOfEdge(last);
				}
				last=ln;
			}
			if(currentWayVal==car.getWayToTarget().getTotalway())routeStillGood=true;
		}
		if(nextCoin!=null&&!routeStillGood){
			System.out.println("Level: "+level.toString());
			LevelNode startNode=getNearestNodeinLevel(car), target=getNearestNodeinLevel(nextCoin);
			if(startNode!=null&&target!=null){
				if(!startNode.getPOS().equals(target.getPOS())){
					LinkedHashMap<LevelNode, AStarNodes> lookAt= new LinkedHashMap<LevelNode, AStarNodes>();
					lookAt.put(startNode, new AStarNodes(startNode.lengthtoNode(target), 0, new LinkedList<LevelNode>()));
					System.out.println("target:"+nextCoin.getId()+" pos:"+nextCoin.getWorldTransform().getPosition()+" inLevel: "+target.getPOS());
					
					List<LevelNode>path =new LinkedList<LevelNode>();
					path.add(startNode);
					
					LinkedList<LevelNode> visit=new LinkedList<LevelNode>();
					Route way=aStar(path, lookAt, visit, target);
					car.setTarget(nextCoin);
					System.out.println("Level: "+level.toString());
					System.out.println("ai setway: "+way);
					car.setWayToTarget(way);
					simulator.tell(new SimulateCreation(car.getId(), way, nextCoin.getId()), self());
					lookAt.clear();
					path.clear();
//					return way;
				}
			}
		}else if(nextCoin==null){
			car.setTarget(nextCoin);
			car.setWayToTarget(null);
			simulator.tell(new SimulateCreation(car.getId(), null, null), self());
		}
//		return null;
	}
	
	private Coin findClosestCoin(Car car) {
		float distance = -1;
		Coin nearest = null;
		for (Coin node : coins.values()) {
//			System.out.println("find: "+node.getId());
			//TODO: filter nicht durch collision erreichbare coins
			if(!level.nearestIsBlocked(node.getCenter())){
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
	
	private Route aStar(List<LevelNode> path, LinkedHashMap<LevelNode, AStarNodes> lookAt, List<LevelNode> visited, LevelNode target) {
		if(starParamsValid(path,lookAt, visited, target)){
			for(LevelNode child:path.get(0).getChilds()){
				if(child.getValOfEdge(path.get(0))<0)visited.add(child);
				if(!visited.contains(child)){
					double resistance = lookAt.get(path.get(0)).getResistance()+child.getValOfEdge(path.get(0)); //resistance till parent + resistance child to parent
					double distance=child.lengthtoNode(target)+resistance; //pytagoras lenght + resistance
//					System.out.print(" distance: "+distance);
					if(lookAt.containsKey(child))if(lookAt.get(child).getLength()<=distance)continue; //keep only shortest way to child
					lookAt.put(child, new AStarNodes(distance, resistance, path));
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
				pathMin.remove(pathMin.size()-1);
				return new Route(lookAt.get(min).getLength(), pathMin);
			}else{
				return aStar(pathMin, lookAt, visited, target);
			}
		}
		System.out.println("Falsche Parameter Übergeben");
		return null;
	}
	
	private boolean starParamsValid(List<LevelNode> path, Map<LevelNode, AStarNodes> lookAt, List<LevelNode> visited, LevelNode target){
		if(path!=null&&lookAt!=null&&!lookAt.isEmpty()&&target!=null&&visited!=null) return true;
		return false;
	}

	private void setBlocked(Shape object, boolean setBlock){
		int inLevel=inLevel(object.getCenter(), object.getRadius());
		if(inLevel>=0){
			Vector max=object.getCenter().add(new VectorImp(object.getRadius(), 0, object.getRadius()));
//			System.out.println(object.getId()+" block center:"+object.getCenter()+" rad:"+object.getRadius());
			Vector min=object.getCenter().sub(new VectorImp(object.getRadius(), 0, object.getRadius()));
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
//			System.out.println("min:"+min.toString()+" max:"+max.toString());
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
	
	private void aiLoop() {
		System.out.println("Ai Loop");
		if(!coins.isEmpty()){
			for(Car car:cars.values()){
				if(car.getTarget()==null){
					System.out.println("route to new coin");
					//TODO:trigger pickup animation
					if(car.getTarget()!=null)coins.remove(car.getTarget().getId());
					for(Node n:coins.values())System.out.println("coin:"+n.getId());
					calcRoute(car);
				}
//				car.getVecToNextTarget();
			}
		}
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
				nonAiNodes.put(nc.id, nodeFactory.groupNode(nc.id));
			} else if (nc.type == ObjectTypes.CUBE) {
				nonAiNodes.put(nc.id, nodeFactory.cube(nc.id, nc.shader, nc.w, nc.h, nc.d, nc.mass));
				setBlocked((Shape)nonAiNodes.get(nc.getId()), true);
			} else if (nc.type == ObjectTypes.PIPE) {
				nonAiNodes.put(nc.id, nodeFactory.pipe(nc.id, nc.shader, nc.r, nc.lats, nc.longs,nc.mass));
				setBlocked((Shape)nonAiNodes.get(nc.getId()), true);
			} else if (nc.type == ObjectTypes.SPHERE) {
				nonAiNodes.put(nc.id, nodeFactory.sphere(nc.id, nc.shader, nc.mass));
				setBlocked((Shape)nonAiNodes.get(nc.getId()), true);
			}
//			else if (nc.type == ObjectTypes.PLANE) {
//				nonAiNodes.put(nc.id, nodeFactory.plane(nc.id, nc.shader, nc.w, nc.d, nc.hight, nc.mass));
//			}
			else if(nc.type == ObjectTypes.OBJECT){
				nonAiNodes.put(nc.id, nodeFactory.obj(nc.id, nc.shader, nc.sourceFile, nc.sourceTex, nc.mass));
				setBlocked((Shape)nonAiNodes.get(nc.getId()), true);
			}else if(nc.type == ObjectTypes.CAR){
				Car car=nodeFactory.car(nc.id, nc.shader, nc.sourceFile, nc.speed, nc.mass);
				cars.put(nc.id, car);
			}else if(nc.type == ObjectTypes.COIN){
				coins.put(nc.id, nodeFactory.coin(nc.id, nc.shader, nc.sourceFile, nc.mass));
			}else if(((NodeCreation) message).type == ObjectTypes.CANON){
				Node newNode = nodeFactory.canon(nc.id, nc.shader, nc.sourceFile, nc.sourceTex, nc.mass);
				nonAiNodes.put(newNode.getId(), newNode);
				setBlocked((Shape)nonAiNodes.get(nc.getId()), true);
			}
		} else if(message instanceof NodeModification){
			NodeModification nm=(NodeModification) message;
			if(cars.get(nm.id)!=null){
//				System.out.println("Nodemodification ai:\nmatrix alt car: \n"+cars.get(nm.id).getWorldTransform()+ "transformationsmatrix: \n"+nm.localMod);
				setNewMatrix(cars.get(nm.id), nm);
//				System.out.println("matrix neu car: \n"+cars.get(nm.id).getWorldTransform());
//				System.out.println("pso car"+nm.id+" "+cars.get(nm.id).getWorldTransform().getPosition().toString());
			}else{
				if(nonAiNodes.get(nm.id)instanceof Shape){
					if(nm.localMod!=null){
						setBlocked((Shape)nonAiNodes.get(nm.id), false);
						setNewMatrix(nonAiNodes.get(nm.id),nm);
						setBlocked((Shape)nonAiNodes.get(nm.id), true);
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
				if(deleteNode(cars.get(id), delete))cars.remove(id);
				if(deleteNode(coins.get(id), delete)){
					coins.remove(id);
					for(Car car:cars.values()){
						if(car.getTarget().getId().equals(id)){
							car.setTarget(null);
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
			if(nonAiNodes.get(pm.id) instanceof Shape&&nonAiNodes.get(pm.id) != null){
				Shape s=((Shape) nonAiNodes.get(pm.id)).clone();
				s.setCenter(pm.force);
				setBlocked(s, true);
				impacts.put(pm.id, s);
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
			nearestVec = level.getNearestinLevel(((Shape)object).getCenter());
		}else{
			nearestVec = level.getNearestinLevel(object.getWorldTransform().getPosition());
		}
		//Tagetposition.sub(startPosition)
//		Vector translate=(nearestVec.sub(object.getWorldTransform().getPosition()));
//		if(!translate.equals(new VectorImp(0, 0, 0)))simulator.tell(new SingelSimulation(object.id, SimulateType.TRANSLATE, translate,object.getWorldTransform()), self());
		return nearestVec;
	}

}
