package app.nodes.shapes;

import java.io.File;

import vecmath.Matrix;
import vecmath.Vector;
import vecmath.vecmathimp.MatrixImp;
import vecmath.vecmathimp.VectorImp;
import app.shader.Shader;

public class Canon extends ObjLoader {
	
	private Vector direction = new VectorImp(0, 1, 0);
	private Vector spawn = new VectorImp(0, 2.1f, 0);

	public Canon(String id, Shader shader, File sourcePath, float mass) {
		super(id, shader, sourcePath, null, mass);
	}
	
	public Canon(String id, Shader shader, File sourceFile, File sourceTex,
			float mass) {
		super(id, shader, sourceFile, sourceTex, mass);
		
	}
	
	@Override
	public void updateWorldTransform(Matrix previousTrafo){
		super.updateWorldTransform(previousTrafo);
		System.out.println("update world transform canon: " + spawn.toString());
		direction = previousTrafo.getRotation().mult(MatrixImp.translate(direction)).getPosition();
//		direction = previousTrafo.mult(MatrixImp.translate(direction)).getPosition();
		spawn = previousTrafo.mult(MatrixImp.translate(spawn)).getPosition();
		System.out.println("update world transform canon danach: " + spawn.toString());
	}

	public Vector getDirection() {
		return direction;
	}

	public Vector getSpawn() {
		return spawn;
	}
	
	

}
