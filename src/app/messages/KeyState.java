package app.messages;

import java.util.HashSet;
import java.util.Set;

/**
 * State passed from target to observer containing all relevant released and pressed keys.
 * 
 * @author Constantin
 * 
 */
public class KeyState implements State {
	public Set<Integer> pressedKeys;
	public Set<Integer> releasedKeys;
	
	public KeyState() {
		pressedKeys = new HashSet<Integer>();
		releasedKeys = new HashSet<Integer>();
	}
}