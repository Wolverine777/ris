package app.nodes;

import static org.lwjgl.opengl.GL11.glEnable;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import app.shader.Shader;
import vecmath.Matrix;
import vecmath.vecmathimp.VectorImp;

/**
 * @author Benjamin Reemts
 *
 */
public class Sun extends Node {

	// Sinnvolle SWerte; position könnte 0, 0, 1, 0 sein
	private FloatBuffer ambient = BufferUtils.createFloatBuffer(4).put(new float[] { 0, 0, 0, 1 });
	private FloatBuffer diffuse = BufferUtils.createFloatBuffer(4).put(new float[] { 1, 1, 1, 1 });
	private FloatBuffer specular = BufferUtils.createFloatBuffer(4).put(new float[] { 1, 1, 1, 1 });
	private FloatBuffer position = BufferUtils.createFloatBuffer(4).put(new float[] { 0, 0, 1, 0 });
	private static boolean state = true;
	private Shader shader;

	// private static int statetest=2;

	public Sun(String id, Matrix m, FloatBuffer ambient, FloatBuffer diffuse, FloatBuffer specular, Shader shader) {
		super(id,m);
		this.shader=shader;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		setPosition(getWorldTransform());
	}

	public Sun(String id, Matrix m, Shader shader) {
		super(id,m);
		this.shader=shader;
		setPosition(getWorldTransform());
	}

	@Override
	public void display(Matrix m) {
		shader.activate();
		Matrix displayMat = m.mult(getWorldTransform());
		setPosition(displayMat);
		if (state)
			turnOnLightsBasic();
		else
			turnOffLightsBasic();

	}

	private void turnOffLightsBasic() {
		int light = GL11.GL_LIGHT0;
		ambient = BufferUtils.createFloatBuffer(4).put(new float[] { 0, 0, 0, 0 });
		diffuse = BufferUtils.createFloatBuffer(4).put(new float[] { 0, 0, 0, 0 });
		specular = BufferUtils.createFloatBuffer(4).put(new float[] { 0, 0, 0, 0 });
		position = BufferUtils.createFloatBuffer(4).put(new float[] { 0, 0, 1, 0 });
		ambient.rewind();
		diffuse.rewind();
		specular.rewind();
		position.rewind();
		GL11.glLight(light, GL11.GL_POSITION, position);
		GL11.glLight(light, GL11.GL_AMBIENT, ambient);
		GL11.glLight(light, GL11.GL_DIFFUSE, diffuse);
		GL11.glLight(light, GL11.GL_SPECULAR, specular);

		// GL11.glDisable(GL11.GL_LIGHTING);
		// GL11.glDisable(GL11.GL_LIGHT0);
		// GL11.glDisableClientState(GL11.GL_LIGHTING);
		// setState(true);

	}

	private void turnOnLightsBasic() {
		// Enable light
		glEnable(GL11.GL_LIGHTING);
		int light = GL11.GL_LIGHT0;
		GL11.glEnable(light);
		ambient = BufferUtils.createFloatBuffer(4).put(new float[] { 0, 0, 0, 1 });
		diffuse = BufferUtils.createFloatBuffer(4).put(new float[] { 1, 1, 1, 1 });
		specular = BufferUtils.createFloatBuffer(4).put(new float[] { 1, 1, 1, 1 });
		ambient.rewind();
		diffuse.rewind();
		specular.rewind();
		position.rewind();
		GL11.glLight(light, GL11.GL_POSITION, position);
		GL11.glLight(light, GL11.GL_AMBIENT, ambient);
		GL11.glLight(light, GL11.GL_DIFFUSE, diffuse);
		GL11.glLight(light, GL11.GL_SPECULAR, specular);
		// setState(false);

	}

	public void setPosition(Matrix mat) {
		VectorImp v = new VectorImp(mat.getPosition().x(), mat.getPosition().y(), mat.getPosition().z());
		position = null;
		position = BufferUtils.createFloatBuffer(4).put(new float[] { v.x, v.y, v.z, 1 });

	}

	public void setPosition(Matrix mat, float w) {
		VectorImp v = new VectorImp(mat.getPosition().x(), mat.getPosition().y(), mat.getPosition().z());
		position = BufferUtils.createFloatBuffer(4).put(new float[] { v.x, v.y, v.z, w });
	}

	public FloatBuffer getAmbient() {
		return ambient;
	}

	public FloatBuffer getDiffuse() {
		return diffuse;
	}

	public FloatBuffer getSpecular() {
		return specular;
	}

	public FloatBuffer getPosition() {
		return position;
	}

	public void setState(boolean state) {
		Sun.state = state;
	}

	// public void setState(int state) {
	// Sun.statetest = state;
	// System.out.println(" "+statetest);
	// }

	public boolean isState() {
		return state;
	}

}
