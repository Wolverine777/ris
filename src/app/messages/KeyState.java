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
	private Set<Integer> toggled;

	public KeyState() {
		pressedKeys = new HashSet<Integer>();
		toggled = new HashSet<Integer>();
	}

	public KeyState(Set<Integer> pressedKeys, Set<Integer> toggled) {
		this.pressedKeys = pressedKeys;
		this.toggled = toggled;
	}

	public Set<Integer> getPressedKeys() {
		return pressedKeys;
	}

	public Set<Integer> getToggled() {
		return toggled;
	}
	
	public void addPressedKeys(Set<Integer> pressedKeys){
		this.pressedKeys.addAll(pressedKeys);
	}
	
	public void addToggled(Set<Integer> toggled){
		this.toggled.addAll(toggled);
	}
	
	public void addPressedKey(Integer pressedKey){
		this.pressedKeys.add(pressedKey);
	}
	
	public void addToggl(Integer toggl){
		this.toggled.add(toggl);
	}
}