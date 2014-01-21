package app.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.edges.DefaultEdge;
import app.edges.Edge;
import app.vecmath.Matrix;
import app.vecmath.Vector;
import app.vecmathimp.FactoryDefault;
import app.vecmathimp.VectorImp;

public abstract class Node {
    private Matrix modelMatrix, worldTrafo;
    protected List<Edge> edges = new ArrayList<Edge>();
    public String id;
    public Vector velocity;
    public Vector acceleration;
    public Vector force;
	public Vector impulse;
//	protected Vector center = new VectorImp(0,0,0);

    public Matrix getWorldTransform() {
        return worldTrafo;
    }

    protected Node(String id) {
    	this.id = id;
    	setLocalTransform(FactoryDefault.vecmath.identityMatrix());
    	updateWorldTransform();
    	   }

    protected Node(String id, Matrix modelMatrix) {
    	this.id = id;
        setLocalTransform(modelMatrix);
        updateWorldTransform();
//        updateWorldTransform(FactoryDefault.vecmath.identityMatrix());
    }

    protected Node(String id, Matrix modelMatrix, Node n) {
    	this(id, modelMatrix);
        new DefaultEdge(n, this);
    }

    protected Node(String id, Matrix modelMatrix, Node n, Map<String, String> data) {
    	this(id, modelMatrix);
        new DefaultEdge(n, this, data);
    }

    public Matrix getLocalTransform() {
        return modelMatrix;
    }

    public void setLocalTransform(Matrix modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public void updateWorldTransform(Matrix previousTrafo) {
        worldTrafo = previousTrafo.mult(getWorldTransform());
//    	System.out.println("world tranform: " + getWorldTransform());
//        worldTrafo = getWorldTransform().mult(previousTrafo);
    }

    public void updateWorldTransform() {
        worldTrafo = getLocalTransform();
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public void removeEdge(Edge e) {
        edges.remove(e);
    }

    public Edge appendTo(Node n) {
        return new DefaultEdge(n, this);
    }

    public Edge appendTo(Node n, Map<String, String> data) {
        return new DefaultEdge(n, this, data);
    }

    public Edge append(Node n) {
        return new DefaultEdge(this, n);
    }

    public Edge append(Node n, Map<String, String> data) {
        return new DefaultEdge(this, n, data);
    }

    public Map<String, String> getData(Node n) {
        for (Edge e : edges) {
            if (e.getOtherNode(this) == n) return e.getData();
        }

        return new HashMap<String, String>();
    }

    
    public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	public Vector getForce() {
		return force;
	}

	public void setForce(Vector force) {
		this.force = force;
	}

	public abstract void display(Matrix m);
}