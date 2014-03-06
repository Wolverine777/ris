package app.input;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import app.eventsystem.Target;
import app.messages.Actions;
import app.messages.KeyEvent;
import app.messages.KeyState;
import app.messages.RegisterKeyObserver;
import app.messages.RemoveKeyObserver;
import app.messages.SendSignal;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class Key extends UntypedActor implements Target {
	private Set<Integer> pressedKeys = new HashSet<Integer>();
	private Set<Integer> releasedKeys = new HashSet<Integer>();
	private SetMultimap<Integer, ActorRef> observers = HashMultimap.create();
	private Map<Integer, Boolean> keyStates = new HashMap<Integer, Boolean>();

	public void update() {
		boolean updated = false;
		pressedKeys.clear();
		releasedKeys.clear();

		while (Keyboard.next()) {
			updated = true;
			int k = Keyboard.getEventKey();

			if (Keyboard.getEventKeyState()) {
				pressedKeys.add(k);
				keyStates.put(k, true);
			} else {
				releasedKeys.add(k);
				keyStates.put(k, false);
			}
		}

		if (updated) {
			KeyState state = new KeyState();

			state.pressedKeys.addAll(pressedKeys);
			state.releasedKeys.addAll(releasedKeys);

			notifyAllObservers(state);
		}
	}

	@Override
	public <T> void registerObserver(ActorRef observer, T keyEvents) {
		if (keyEvents instanceof KeyEvent) {
			for (Integer key : ((KeyEvent) keyEvents).keys) {
				observers.put(key, observer);
			}
		}
	}

	@Override
	public void removeObserver(ActorRef observer) {
		Set<Integer> keys = new HashSet<Integer>();

		for (Integer key : observers.keySet()) {
			if (observers.get(key).contains(observer)) {
				keys.add(key);
			}
		}
		for (Integer key : keys) {
			observers.remove(key, observer);
		}
	}

	@Override
	public <T> void removeObserver(ActorRef observer, T keyEvents) {
		if (keyEvents instanceof KeyEvent) {
			for (Integer key : ((KeyEvent) keyEvents).keys) {
				observers.remove(key, observer);
			}
		}
	}

	@Override
	public <T> void notifyAllObservers(T keyState) {
		if (keyState instanceof KeyState) {
			Set<Integer> keys = new HashSet<Integer>();

			keys.addAll(((KeyState) keyState).pressedKeys);
			keys.addAll(((KeyState) keyState).releasedKeys);

			for (Integer key : keys) {
				for (ActorRef observer : observers.get(key)) {
					observer.tell(keyState, self());
				}
			}
		}
	}

	@Override
	public void sendSignal(ActorRef recipient) {
		recipient.tell(new SendSignal(keyStates), self());
	}

	@Override
	public void onReceive(Object arg0) throws Exception {
		if (arg0 instanceof Actions && arg0 == Actions.UPDATE) {
			update();
		} else if (arg0 instanceof RegisterKeyObserver) {
			RegisterKeyObserver ref = (RegisterKeyObserver) arg0;
			registerObserver(ref.observer, ref.keys);
		} else if (arg0 instanceof Actions && arg0 == Actions.SIGNAL) {
			sendSignal(getSender());
		} else if (arg0 instanceof RemoveKeyObserver) {
			RemoveKeyObserver ref = (RemoveKeyObserver) arg0;

			if (ref.keys.keys == null) {
				removeObserver(ref.observer);
			} else {
				removeObserver(ref.observer, ref.keys);
			}
		}
	}

}