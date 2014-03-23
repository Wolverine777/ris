package app.nodes;

import java.util.LinkedList;
import java.util.List;

import vecmath.Matrix;
import vecmath.vecmathimp.FactoryDefault;
import app.shader.Shader;

public class Camera extends Node {
	private static Float camZ=null;
	private List<Shader> shaderList=new LinkedList<Shader>();

	public static Float getCamZ() {
		return camZ;
	}
	
	public Camera(String id, List<Shader> shaderList){
		super(id, FactoryDefault.vecmath.identityMatrix());
		camZ= getWorldTransform().getPosition().z();
		this.shaderList=shaderList;
	}
	
	@Override
	public void display(Matrix m) {
	}
	
	public void activate(){
		for(Shader s:shaderList)s.activate();
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

	public List<Shader> getShaderList() {
		return shaderList;
	}
	
}
