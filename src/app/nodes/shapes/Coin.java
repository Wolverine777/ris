package app.nodes.shapes;

import java.io.File;

import app.shader.Shader;

/**
 * @author Benjamin Reemts
 *
 */

public class Coin extends ObjLoader {

	public Coin(String id, Shader shader, float mass) {
		super(id, shader, mass);
		// TODO Auto-generated constructor stub
	}

	public Coin(String id, Shader shader, File sourcePath, float mass) {
		super(id, shader, sourcePath, mass);
		// TODO Auto-generated constructor stub
	}

}
