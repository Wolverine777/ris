package app.eventsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabian Unruh
 *
 */
public class NodeDeletion {
	
	public NodeDeletion(List<String> ids) {
		this.ids = ids;
	}
	public NodeDeletion() {
	}

	public List<String> ids = new ArrayList<String>();

}
