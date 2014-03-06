package app.nodes.shapes;

import java.io.File;

import vecmath.Matrix;
import vecmath.Vector;
import vecmath.vecmathimp.MatrixImp;
import vecmath.vecmathimp.VectorImp;
import app.shader.Shader;

public class Canon extends ObjLoader {
	
	public Vector direction = new VectorImp(0, 1, 0);

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
		direction = previousTrafo.mult(MatrixImp.translate(direction)).getPosition();
	}
	

}
