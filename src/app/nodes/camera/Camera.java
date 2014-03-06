package app.nodes.camera;

import vecmath.Matrix;
import vecmath.vecmathimp.FactoryDefault;
import app.nodes.Node;
import app.shader.Shader;

public class Camera extends Node {
	public Camera(String id){
		super(id, FactoryDefault.vecmath.identityMatrix());
	}
	
	@Override
	public void display(Matrix m) {
	}
	
	public void activate(){
		Shader.setViewMatrix(getWorldTransform().invertFull());
	}
	
}
