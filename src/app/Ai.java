package app;

import static app.nodes.NodeFactory.nodeFactory;

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
import app.Types.SimulateType;
import app.datatype.AStarNodes;
import app.datatype.Level;
import app.datatype.LevelNode;
import app.datatype.Route;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeModification;
import app.messages.AiInitialization;
import app.messages.Message;
import app.messages.SingelSimulation;
import app.nodes.Node;
import app.nodes.shapes.Cube;
import app.nodes.shapes.Shape;

public class Ai extends UntypedActor {

	Level level;
	private Map<String, Node> nodes = new HashMap<String, Node>();
	
	//TODO: Car das eine route hat und eine aktualisierungsfrequenz und pick up auslösen kann
	private List<Node> bots=new LinkedList<Node>(){
		private static final long serialVersionUID = 4808793150108855621L;
		@Override
		public boolean contains(Object o){
			for(Node x:this)if(((Node)o).id.equals(x.id))return true;
	        return false;
		}
	};
	List<Node> flags=new LinkedList<Node>(){
		private static final long serialVersionUID = 7233857901815694877L;
		@Override
		public boolean contains(Object o) {
			for(Node x:this)if(((Node)o).id.equals(x.id))return true;
	        return false;
	    }
	};
	private Map<Node, Route> perfectway= new HashMap<Node, Route>();
	ActorRef simulator;

	private void initialize(Vector levelPosition, float width, float depth) {
		System.out.println("initLevel"+levelPosition +"w: "+ width +"d: "+ depth);
		level=new Level(levelPosition, width, depth);
		System.out.println(level.toString());
		getSender().tell(Message.INITIALIZED, self());
	}
	
	private void findRoute(Node bot){
		LevelNode startNode= level.getLevelNode(getNearestNodeinLevel(bot));
		LevelNode target = level.getLevelNode(getNearestNodeinLevel(findClosestCoin(bot))); 
		
		LinkedHashMap<LevelNode, AStarNodes> lookAt= new LinkedHashMap<LevelNode, AStarNodes>();
		lookAt.put(startNode, new AStarNodes(startNode.lengthtoNode(target), 0, new LinkedList<LevelNode>()));
		
		List<LevelNode>path =new LinkedList<LevelNode>();
		path.add(startNode);
		
		LinkedList<LevelNode> visit=new LinkedList<LevelNode>();
		
		perfectway.put(bot, aStar(path, lookAt, visit, target));
		
		lookAt.clear();
		path.clear();
	}
	
	private Route aStar(List<LevelNode> path, LinkedHashMap<LevelNode, AStarNodes> lookAt, List<LevelNode> visited, LevelNode target) {
		if(starParamsValid(path,lookAt, visited, target)){
			for(LevelNode child:path.get(0).getChilds()){
				if(child.getValOfEdge(path.get(0))>0&&!visited.contains(child)){
					int resistance = lookAt.get(path.get(0)).getResistance()+child.getValOfEdge(path.get(0)); //resistance till parent + resistance child to parent
					double distance=child.lengthtoNode(target)+resistance; //pytagoras lenght + resistance
					System.out.print(" distance: "+distance);
					if(lookAt.containsKey(child))if(lookAt.get(child).getLength()<=distance)continue; //keep only shortest way to child
					lookAt.put(child, new AStarNodes(distance, resistance, path));
				}
			}
			if(path.contains(target))return new Route((int) lookAt.get(target).getLength(), path);
			lookAt.remove(path.get(0));
			visited.add(path.get(0));
			
			System.out.println(lookAt.toString());
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
				return new Route((int)lookAt.get(min).getLength(), pathMin);
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

	private Node findClosestCoin(Node car) { //VectorImp
		float distance = -1;
		Node nearest = null;
		for (Node node : flags) { //nodes.values()
//			if (node instanceof Cube) {
//				System.out.println("Node(Cube) findclosest: " + node.id);
				float tempdistance = node.getWorldTransform().getPosition().sub(car.getWorldTransform().getPosition()).length();
				if (tempdistance < distance || distance<0) {
					distance = tempdistance;
//					System.out.println("distance coin: " + distance);
					nearest = node;
				}
//			}
		}
//		VectorImp closestlevelnode = (VectorImp) getNearestNodeinLevel(nearest); //return changed to Node
//		System.out.println("closestlevelNode: " + closestlevelnode);
//		return closestlevelnode;
		return nearest;
	}

	private void aiLoop() {

//		System.out.println("Level size: " + level.getLevelPoints().size());
		System.out.println(level.toString());
		for (Node n : nodes.values()) {
			System.out.println("NodeAI: " + n.id);
//			findClosestCoin(n);
			VectorImp closest = (VectorImp) getNearestNodeinLevel(n);
			System.out.println("Nearest is: " + closest.toString());
		}
		
		LinkedHashMap<LevelNode, AStarNodes> lookAt= new LinkedHashMap<LevelNode, AStarNodes>();
		LevelNode target = level.toArray()[0]; 
		LevelNode startNode=level.toArray()[12]; 
		lookAt.put(startNode, new AStarNodes(startNode.lengthtoNode(target), 0, new LinkedList<LevelNode>()));
		System.out.println("StartNode: "+startNode.getPOS());
		System.out.println("EndNode: "+target.getPOS());
		List<LevelNode>path =new LinkedList<LevelNode>();
		path.add(startNode);
		LinkedList<LevelNode> visit=new LinkedList<LevelNode>();
		System.out.println("lookat size vor dem call: " + lookAt.size());
		Route r=aStar(path, lookAt, visit, target);
		System.out.println(r.toString());
//		System.out.println(aStar(path, lookAt, new LinkedList<LevelNode>(), target).toString()); //warum?
		lookAt.clear();
		System.out.println("gecleart??????? " + lookAt.size());
		path.clear();
		getSender().tell(Message.DONE, self());

	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.LOOP) {
			System.out.println("ai loop");
			getSender().tell(Message.DONE, self());
			aiLoop();
		} else if (message instanceof AiInitialization) {
			AiInitialization init= (AiInitialization) message;
			this.simulator = (init.getSimulator());
			initialize(init.getCenterPosition(), init.getWidth(), init.getDepth());
		} else if (message instanceof NodeCreation) {

			if (((NodeCreation) message).type == ObjectTypes.GROUP) {
				Node newNode = nodeFactory
						.groupNode(((NodeCreation) message).id);
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == ObjectTypes.CUBE) {

				Node newNode = nodeFactory.cube(((NodeCreation) message).id,
						((NodeCreation) message).shader,
						((NodeCreation) message).w, ((NodeCreation) message).h,
						((NodeCreation) message).d,
						((NodeCreation) message).mass);
				if ((((NodeCreation) message).modelmatrix != null)) {
					newNode.updateWorldTransform(((NodeCreation) message).modelmatrix);
				}
				if ((((NodeCreation) message).center != null)) {
					((Shape) newNode)
							.setCenter(((NodeCreation) message).center);
				}
				if ((((NodeCreation) message).radius != 0)) {
					((Shape) newNode)
							.setRadius(((NodeCreation) message).radius);
				}
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == ObjectTypes.SPHERE) {

				Node newNode = nodeFactory.sphere(((NodeCreation) message).id,
						((NodeCreation) message).shader,
						((NodeCreation) message).mass);
				if ((((NodeCreation) message).modelmatrix != null)) {
					newNode.updateWorldTransform(((NodeCreation) message).modelmatrix);
				}
				if ((((NodeCreation) message).center != null)) {
					((Shape) newNode)
							.setCenter(((NodeCreation) message).center);
				}
				if ((((NodeCreation) message).radius != 0)) {
					((Shape) newNode)
							.setRadius(((NodeCreation) message).radius);
				}
				nodes.put(newNode.id, newNode);
			}
		} else if(message instanceof NodeModification){
			if(nodes.containsKey(((NodeModification) message).id)){
        		Node modify = nodes.get(((NodeModification) message).id);
        		if (((NodeModification) message).localMod != null){
        			modify.updateWorldTransform(((NodeModification) message).localMod);
        		}
        	}
		}

	}
	
	private Vector getNearestNodeinLevel(Node object){
		Vector nearestVec = level.getNearestinLevel(object.getWorldTransform().getPosition());
//		Vector translate=(nearestVec.sub(object.getWorldTransform().getPosition()));
//		if(!translate.equals(new VectorImp(0, 0, 0)))simulator.tell(new SingelSimulation(object.id, SimulateType.TRANSLATE, translate,object.getWorldTransform()), self());
		return nearestVec;
	}

}
