package app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import app.messages.KeyState;
import app.messages.Message;
import app.messages.RegisterKeys;

/**
 * @author Benjamin Reemts
 *
 */
public class Input extends UntypedActor {
	
	private SetMultimap<Integer, ActorRef> keyObservers = HashMultimap.create();
	
    private void initialize() {
//    	try {
//    		Keyboard.create();
//		} catch (LWJGLException e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
        getSender().tell(Message.INITIALIZED, self());
    }

    Set<Integer> pressedKeys = new HashSet<Integer>();
    Set<Integer> toggled = new HashSet<Integer>();
    private void run() {
		while(Keyboard.next()) {
			int k = Keyboard.getEventKey();
			if (Keyboard.getEventKeyState()) {
				pressedKeys.add(k);
				if(toggled.contains(k))toggled.remove(k);
				else toggled.add(k);
			}else {
				pressedKeys.remove(k);
			}
		}
		
		Map<ActorRef, KeyState> outcome = new HashMap<ActorRef, KeyState>();
		for(Integer obsKey:keyObservers.keySet()){
			if(pressedKeys.contains(obsKey)||toggled.contains(obsKey)){
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
		
        getSender().tell(Message.DONE, self());
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
        	if(rk.isAdd())for(Integer i:rk.getKeys())keyObservers.put(i, getSender());
        	else for(Integer i:rk.getKeys())keyObservers.remove(i, getSender());
        }
    }

}