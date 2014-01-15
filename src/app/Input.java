package app;

import java.util.HashSet;
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
 * @author Constantin, Benjamin Reemts
 *
 */
public class Input extends UntypedActor {
	
	private SetMultimap<Integer, ActorRef> keyObservers = HashMultimap.create();
	
    private void initialize() {
        getSender().tell(Message.INITIALIZED, self());
    }

    private void run() {
    	Set<Integer> pressedKeys = new HashSet<Integer>();
    	Set<Integer> releasedKeys = new HashSet<Integer>();
		while (Keyboard.next()) {
			int k = Keyboard.getEventKey();
			System.out.println("haaaaaaaaaaaaaaaaaaaaallllloooo ");
			if (Keyboard.getEventKeyState()) {
				pressedKeys.add(k);
				System.out.println("p: "+k);
			} 
			else {
				releasedKeys.add(k);
				System.out.println("r:"+k);
			}
		}
		System.out.println("pressed..................");
		for(Integer i:pressedKeys)System.out.print(i+" ");
		System.out.println("released.................."+releasedKeys.toString());
		
		SetMultimap<ActorRef, Integer> pushPr = HashMultimap.create();
		SetMultimap<ActorRef, Integer> pushRe = HashMultimap.create();
		for(Integer i:pressedKeys){
			if(keyObservers.containsKey(i)){
				for(ActorRef ar:keyObservers.get(i)){
					pushPr.put(ar, i);
				}
			}
		}
		for(Integer i:releasedKeys){
			if(keyObservers.containsKey(i)){
				for(ActorRef ar:keyObservers.get(i)){
					pushRe.put(ar, i);
				}
			}
		}
		for(ActorRef send:pushPr.keySet()){
			KeyState ks=new KeyState(pushPr.get(send), pushRe.get(send));
			send.tell(ks, self());
			pushRe.remove(send, pushRe.get(send));
		}
		for(ActorRef send:pushRe.keySet()){
			KeyState ks=new KeyState(pushPr.get(send), pushRe.get(send));
			send.tell(ks, self());
		}
		
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