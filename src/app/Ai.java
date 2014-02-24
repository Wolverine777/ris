package app;

import java.util.HashMap;
import java.util.Map;

import akka.actor.UntypedActor;
import app.datatype.Route;
import app.eventsystem.Level;
import app.eventsystem.LevelCreation;
import app.eventsystem.LevelNode;
import app.messages.Message;
import app.nodes.Node;

public class Ai extends UntypedActor {
	
	Level level;
	private Map<String, Node> nodes = new HashMap<String, Node>();

	private void initialize() {
		 getSender().tell(Message.INITIALIZED, self());

	}

	private Route aStar(){
		return null;
		
	}
	
	private void aiLoop() {
			
		getSender().tell(Message.DONE, self());

	}
	
	

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.LOOP) {
			System.out.println("ai loop");
			aiLoop();
		} else if (message == Message.INIT) {
			initialize();
		} else if (message instanceof LevelCreation){
			this.level = ((LevelCreation) message).level;			
		}

	}

}
