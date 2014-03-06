package app.nodes.camera;

import app.nodes.Node;
import app.shader.Shader;
import app.vecmathimp.FactoryDefault;

public class Camera extends Node {
	public Camera(String id){
		super(id, FactoryDefault.vecmath.identityMatrix());
	}
	
	@Override
	public void display() {
	}
	
	public void activate(){
		Shader.setViewMatrix(getWorldTransform().invertFull());
	}
	
}
