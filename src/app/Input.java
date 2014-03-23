package app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import vecmath.vecmathimp.VectorImp;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.KeyTapGesture;
import com.leapmotion.leap.ScreenTapGesture;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;
import com.leapmotion.leap.Gesture.State;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import app.Types.GestureType;
import app.messages.HandPosition;
import app.messages.KeyState;
import app.messages.Message;
import app.messages.RegisterGesture;
import app.messages.RegisterKeys;

/**
 * @author Benjamin Reemts
 *
 */
public class Input extends UntypedActor {
	
	private SetMultimap<Integer, ActorRef> keyObservers = HashMultimap.create();
	private SetMultimap<GestureType, ActorRef> gestureObservers = HashMultimap.create();
	private Controller controller;
	
    private void initialize() {
//    	try {
//    		Keyboard.create();
//		} catch (LWJGLException e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
		controller = new Controller();
		
    	onConnect(controller);
        getSender().tell(Message.INITIALIZED, self());
    }

    Set<Integer> pressedKeys = new HashSet<Integer>();
    Set<Integer> toggled = new HashSet<Integer>();
    Set<Integer> pressedKeystmp = new HashSet<Integer>();
    
    private void run() {
    	pressedKeystmp.clear();
    	pressedKeystmp.addAll(pressedKeys);
		while(Keyboard.next()) {
			int k = Keyboard.getEventKey();
			System.out.println("Key: " + k + "Pressed");
			if (Keyboard.getEventKeyState()) {
				pressedKeys.add(k);
				pressedKeystmp.add(k);
				if(toggled.contains(k))toggled.remove(k);
				else toggled.add(k);
			}else {
				System.out.println("Ich möchte diesen key releasen: " + k);
				pressedKeys.remove(k);
			}
		}
//		System.out.println("pressed keys: " + pressedKeys + "tmp keys: " + pressedKeystmp);
		
		Map<ActorRef, KeyState> outcome = new HashMap<ActorRef, KeyState>();
		for(Integer obsKey:keyObservers.keySet()){
			if(pressedKeystmp.contains(obsKey)){
				for(ActorRef actor:keyObservers.get(obsKey)){
					KeyState ks=new KeyState();
					if(outcome.containsKey(actor))ks=outcome.get(actor);
					if(pressedKeys.contains(obsKey))ks.addPressedKey(obsKey);
					if(toggled.contains(obsKey))ks.addToggl(obsKey);
					outcome.put(actor, ks);
				}
			}
		}
		
	
		for(Entry<ActorRef, KeyState> out:outcome.entrySet())out.getKey().tell(out.getValue(), self());

		
		onFrame(controller);
		
		
        getSender().tell(Message.DONE, self());
    }
    
	public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        System.out.println("Frame id: " + frame.id()
                         + ", timestamp: " + frame.timestamp()
                         + ", hands: " + frame.hands().count()
                         + ", fingers: " + frame.fingers().count()
                         + ", tools: " + frame.tools().count()
                         + ", gestures " + frame.gestures().count());

        if (!frame.hands().isEmpty()) {
            // Get the first hand
            Hand hand = frame.hands().get(0);

            // Check if the hand has any fingers
            FingerList fingers = hand.fingers();
            if (!fingers.isEmpty()) {
                // Calculate the hand's average finger tip position
                Vector avgPos = Vector.zero();
                for (Finger finger : fingers) {
                    avgPos = avgPos.plus(finger.tipPosition());
                }
                if(fingers.count()== 1){
                	System.out.println("ein Finger mit der posi" + fingers.get(0).direction());
                }
                avgPos = avgPos.divide(fingers.count());
                System.out.println("Hand has " + fingers.count()
                                 + " fingers, average finger tip position: " + avgPos);
            }

            // Get the hand's sphere radius and palm position
            System.out.println("Hand sphere radius: " + hand.sphereRadius()
                             + " mm, palm position: " + hand.palmPosition());

            // Get the hand's normal vector and direction
            Vector normal = hand.palmNormal();
            Vector direction = hand.direction();
            
            Map<ActorRef, HandPosition> sendHP = new HashMap<ActorRef, HandPosition>();            	
            	for(ActorRef actor:gestureObservers.get(GestureType.HAND_POSITION)){
            		HandPosition hp = new HandPosition();
           			hp.handPosition = new VectorImp(normal.getX(), normal.getY(), normal.getZ());
           			hp.fingerAmount = fingers.count();
           			            			
           			sendHP.put(actor, hp);
           		}
            	for(Entry<ActorRef, HandPosition> send:sendHP.entrySet()){
            		send.getKey().tell(send.getValue(), self());
            	}
            	
            

            // Calculate the hand's pitch, roll, and yaw angles
//            System.out.println("Hand pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
//                             + "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
//                             + "yaw: " + Math.toDegrees(direction.yaw()) + " degrees");
        }

        GestureList gestures = frame.gestures();
        for (int i = 0; i < gestures.count(); i++) {
            Gesture gesture = gestures.get(i);

            switch (gesture.type()) {
                case TYPE_CIRCLE:
                    CircleGesture circle = new CircleGesture(gesture);

                    // Calculate clock direction using the angle between circle normal and pointable
                    String clockwiseness;
                    if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {
                        // Clockwise if angle is less than 90 degrees
                        clockwiseness = "clockwise";
                    } else {
                        clockwiseness = "counterclockwise";
                    }

                    // Calculate angle swept since last frame
                    double sweptAngle = 0;
                    if (circle.state() != State.STATE_START) {
                        CircleGesture previousUpdate = new CircleGesture(controller.frame(1).gesture(circle.id()));
                        sweptAngle = (circle.progress() - previousUpdate.progress()) * 2 * Math.PI;
                    }

                    System.out.println("Circle id: " + circle.id()
                               + ", " + circle.state()
                               + ", progress: " + circle.progress()
                               + ", radius: " + circle.radius()
                               + ", angle: " + Math.toDegrees(sweptAngle)
                               + ", " + clockwiseness);
                    break;
                case TYPE_SWIPE:
                    SwipeGesture swipe = new SwipeGesture(gesture);
                    System.out.println("Swipe id: " + swipe.id()
                               + ", " + swipe.state()
                               + ", position: " + swipe.position()
                               + ", direction: " + swipe.direction()
                               + ", speed: " + swipe.speed());
                    break;
                case TYPE_SCREEN_TAP:
                    ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
                    System.out.println("Screen Tap id: " + screenTap.id()
                               + ", " + screenTap.state()
                               + ", position: " + screenTap.position()
                               + ", direction: " + screenTap.direction());
                    break;
                case TYPE_KEY_TAP:
                    KeyTapGesture keyTap = new KeyTapGesture(gesture);
                    System.out.println("Key Tap id: " + keyTap.id()
                               + ", " + keyTap.state()
                               + ", position: " + keyTap.position()
                               + ", direction: " + keyTap.direction());
                    break;
                default:
                    System.out.println("Unknown gesture type.");
                    break;
            }
        }

        if (!frame.hands().isEmpty() || !gestures.isEmpty()) {
            System.out.println();
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message == Message.LOOP) {
        	System.out.println("input loop");
            run();
        } else if (message == Message.INIT) {
            initialize();
        } else if (message instanceof RegisterKeys){
        	RegisterKeys rk=(RegisterKeys)message;
        	System.out.println("input receive: " + rk.getKeys().toString() + getSender());
        	if(rk.isAdd())for(Integer i:rk.getKeys())keyObservers.put(i, getSender());
        	else for(Integer i:rk.getKeys())keyObservers.remove(i, getSender());
        } else if (message instanceof RegisterGesture){
        	RegisterGesture rg=(RegisterGesture)message;
        	if(rg.isAdd())gestureObservers.put(rg.getGestureType(), getSender());
        	else gestureObservers.remove(rg.getGestureType(), getSender());
        }        
    }

}