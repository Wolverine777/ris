package app.eventsystem;

import vecmath.Vector;

/**
 * @author Fabian Unruh
 *
 */
public class PhysicModification {
	private final String id;
	private final Vector force;
	
	public PhysicModification(String id, Vector force) {
		super();
		this.id = id;
		this.force = force;
	}

	public String getId() {
		return id;
	}

	public Vector getForce() {
		return force;
	}
}
