package app;


import static app.nodes.NodeFactory.nodeFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import akka.actor.UntypedActor;
import app.eventsystem.CameraCreation;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeModification;
import app.eventsystem.SimulateCreation;
import app.eventsystem.StartNodeModification;
import app.eventsystem.Types;
import app.messages.KeyDef;
import app.messages.KeyState;
import app.messages.Message;
import app.messages.Mode;
import app.messages.SimulateType;
import app.nodes.GroupNode;
import app.nodes.Node;
import app.nodes.camera.Camera;

public class Simulator extends UntypedActor {
    
    private Map<String, Node> nodes = new HashMap<String, Node>();
    private Map<Node, KeyDef> simulations=new HashMap<Node, KeyDef>();
    private Set<Integer> pressedKeys = new HashSet<Integer>();
    
    private void initialize() {
        getSender().tell(Message.INITIALIZED, self());
    }

    private void simulate() {
    	for(Map.Entry<Node, KeyDef> entry:simulations.entrySet()){
    		SimulateType type=entry.getValue().getType();
    		if(entry.getValue().getMode()==Mode.DOWN){
    			
    		}
    		if(type==SimulateType.ROTATE){
    			
    		}else if(type==SimulateType.TRANSLATE){
    			
    		}
    	}
                
        getSender().tell(Message.DONE, self());
    }
    
    private void rotate(){
    	//st end nodemodification
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
        if (message == Message.LOOP) {
            simulate();
        } else if (message == Message.INIT) {
            initialize();
        } else if(message instanceof KeyState){
        	pressedKeys.clear();
        	pressedKeys.addAll(((KeyState)message).getPressedKeys());
        }
        
//        else if (message instanceof NodeCreation) {
//        	System.out.println("NodeCreation");
//        	
//        	if (((NodeCreation) message).type == Types.GROUP) {
//        		Node newNode = nodeFactory.groupNode(((NodeCreation) message).id);
//        		nodes.put(newNode.id, newNode);
//        	} else if (((NodeCreation) message).type == Types.CUBE) {
//        		
//        		System.out.println("Shadering cube with " + ((NodeCreation) message).shader);
//        		
//        		
//        		Node newNode = nodeFactory.cube(((NodeCreation) message).id, ((NodeCreation) message).shader);
//        		nodes.put(newNode.id, newNode);
//        	}
//        	
//        	
//        } else if (message instanceof CameraCreation) {
//        	System.out.println("CameraCreation");
//        	
//        	Camera camera = nodeFactory.camera(((CameraCreation) message).id);
//        	nodes.put(((CameraCreation) message).id, camera);
//        	
//        } 
        	else if (message instanceof NodeModification) {
//        	System.out.println("NodeModification");
        	
        	System.out.println("Nodes " + nodes);
        	System.out.println("Accesing " + ((NodeModification) message).id);
        	
        	if(nodes.containsKey(((NodeModification) message).id)){
        		Node modify = nodes.get(((NodeModification) message).id);
        		if (((NodeModification) message).localMod != null) {
        			modify.updateWorldTransform(((NodeModification) message).localMod);
//        		modify.setLocalTransform(modify.getWorldTransform());
        		}
        		if (((NodeModification) message).appendTo != null) {
        			
        			System.out.println("Appending " + ((NodeModification) message).id + " to " + ((NodeModification) message).appendTo);
        			
        			
        			modify.appendTo(nodes.get(((NodeModification) message).appendTo));
        		}
        	}
        		
        } 
//        else if (message instanceof StartNodeModification) {
//        	System.out.println("StartNodeModification");
//        	
//        	Node start = nodes.get(((StartNodeModification) message).id);
//        }
        else if(message instanceof SimulateCreation){
        	SimulateCreation sc=(SimulateCreation)message;
        	Node newNode=null;
        	if (((NodeCreation) message).type == Types.GROUP) {
        		newNode = nodeFactory.groupNode(((NodeCreation) message).id);
        		nodes.put(newNode.id, newNode);
        	} else if (((NodeCreation) message).type == Types.CUBE) {
        		
        		System.out.println("Shadering cube with " + ((NodeCreation) message).shader);
        		
        		
        		newNode = nodeFactory.cube(((NodeCreation) message).id, ((NodeCreation) message).shader);
        		nodes.put(newNode.id, newNode);
        	}else if(((NodeCreation) message).type == Types.CAMERA){
        		newNode = nodeFactory.camera(((CameraCreation) message).id);
        		nodes.put(((CameraCreation) message).id, newNode);
        	}
        	else{
        		throw new Exception("Please implement Type");
        	}
        	if(sc.getSimulation()!=SimulateType.NONE){
        		simulations.put(newNode, new KeyDef(sc.getSimulation(), sc.getKeys(), sc.getMode()));
        	}else{
        		simulations.remove(newNode);
        	}
        }
        
    }
}