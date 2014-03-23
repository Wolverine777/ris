package app.nodes.shapes;

import java.io.File;

import vecmath.Matrix;
import app.shader.Shader;

/**
 * @author Benjamin Reemts
 *
 */

public class Coin extends ObjLoader {

	public Coin(String id, Shader shader, Matrix modelMatrix, float mass) {
		super(id, shader, modelMatrix, mass);
		// TODO Auto-generated constructor stub
		
	}

	public Coin(String id, Shader shader, File sourcePath, Matrix modelMatrix, float mass) {
		super(id, shader, sourcePath, modelMatrix, mass);
		// TODO Auto-generated constructor stub
	}

}
