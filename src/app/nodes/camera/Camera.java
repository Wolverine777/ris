package app.nodes.camera;

import vecmath.Matrix;
import vecmath.vecmathimp.FactoryDefault;
import app.nodes.Node;
import app.shader.Shader;

public class Camera extends Node {
	private static Float camZ=null;

	public static Float getCamZ() {
		return camZ;
	}
	
	public Camera(String id){
		super(id, FactoryDefault.vecmath.identityMatrix());
		camZ= getWorldTransform().getPosition().z();
	}
	
	@Override
	public void display(Matrix m) {
	}
	
	public void activate(){
		Matrix inv=getWorldTransform().invertFull();
		camZ= inv.getPosition().z();
		Shader.setViewMatrix(inv);
	}

	@Override
	public void updateWorldTransform(Matrix previousTrafo) {
		super.updateWorldTransform(previousTrafo);
		camZ= getWorldTransform().getPosition().z();
	}

	@Override
	public void updateWorldTransform() {
		super.updateWorldTransform();
		camZ= getWorldTransform().getPosition().z();
	}
	
}
