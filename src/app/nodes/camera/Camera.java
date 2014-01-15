package app.nodes.camera;

import app.nodes.Node;
import app.shader.Shader;
import app.vecmath.Matrix;
import app.vecmathimp.FactoryDefault;

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
