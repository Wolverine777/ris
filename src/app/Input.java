package app;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import akka.actor.UntypedActor;
import app.messages.Message;

/**
 * TODO
 * @author Constantin
 *
 */
public class Input extends UntypedActor {
	private Set<Integer> pressedKeys = new HashSet<Integer>();
	
    private void initialize() {
        getSender().tell(Message.INITIALIZED, self());
    }

    private void run() {
    	pressedKeys.clear();

		while (Keyboard.next()) {
			int k = Keyboard.getEventKey();
			if (Keyboard.getEventKeyState()) {
				pressedKeys.add(k);
			} 
		}

        getSender().tell(Message.DONE, self());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message == Message.LOOP) {
            run();
        } else if (message == Message.INIT) {
            initialize();
        }
    }

}