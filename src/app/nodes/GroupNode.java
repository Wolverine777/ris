package app.nodes;

import java.util.Map;

import app.edges.Edge;
import app.vecmath.Matrix;

public class GroupNode extends Node {

	public GroupNode(String id, Matrix modelMatrix, Node n,
			Map<String, String> data) {
		super(id, modelMatrix, n, data);
	}

	public GroupNode(String id, Matrix modelMatrix, Node n) {
		super(id, modelMatrix, n);
	}

	public GroupNode(String id, Matrix modelMatrix) {
		super(id, modelMatrix);
	}

	public GroupNode(String id) {
		super(id);
	}

	@Override
	public void display(Matrix m) {
		for (Edge e : edges) {
			if (e.isStart(this)) {
				e.getOtherNode(this).display(m.mult(getWorldTransform()));
			}
		}
	}
}
