package app.messages;

import java.util.HashSet;
import java.util.Set;

/**
 * State passed from target to observer containing all relevant released and
 * pressed keys.
 * 
 * @author Constantin, Benjamin Reemts
 * 
 */
public class KeyState implements State {
	private Set<Integer> pressedKeys;
	private Set<Integer> releasedKeys;

	public KeyState() {
		pressedKeys = new HashSet<Integer>();
		releasedKeys = new HashSet<Integer>();
	}

	public KeyState(Set<Integer> pressedKeys, Set<Integer> releasedKeys) {
		this.pressedKeys = pressedKeys;
		this.releasedKeys = releasedKeys;
	}

	public Set<Integer> getPressedKeys() {
		return pressedKeys;
	}

	public Set<Integer> getReleasedKeys() {
		return releasedKeys;
	}
	
	public void addPressedKeys(Set<Integer> pressedKeys){
		this.pressedKeys.addAll(pressedKeys);
	}
	
	public void addReleasedKeys(Set<Integer> releasedKeys){
		this.releasedKeys.addAll(releasedKeys);
	}
	
	public void addPressedKey(Integer pressedKey){
		this.pressedKeys.add(pressedKey);
	}
	
	public void addReleasedKey(Integer releasedKey){
		this.releasedKeys.add(releasedKey);
	}
}