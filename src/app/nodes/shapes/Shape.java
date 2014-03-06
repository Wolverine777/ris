package app.nodes.shapes;

import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import app.nodes.Node;
import app.nodes.shapes.Vertex;
import app.shader.Shader;
import app.vecmathimp.FactoryDefault;

public abstract class Shape extends Node {
	protected Vertex[] vertices;
	protected FloatBuffer positionData;
	protected FloatBuffer colorData;
	protected FloatBuffer normalData;
	protected FloatBuffer textureData;
	protected Texture tex;
	protected Shader shader;
	protected int mode = GL11.GL_QUADS;

	public Shape(String id, Shader shader) {
		super(id, FactoryDefault.vecmath.identityMatrix());
		this.shader = shader;
	}

	public void display() {

		System.out.println("Displaying " + id);

		shader.activate();
		if (tex != null) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			tex.display();
		}
		shader.setModelMatrix(getWorldTransform());
		// Enable the vertex data arrays (with indices 0 and 1). We use a vertex
		// position and a vertex color.
		glVertexAttribPointer(Shader.vertexAttribIdx, 3, false, 0, positionData);
		glEnableVertexAttribArray(Shader.vertexAttribIdx);
		glVertexAttribPointer(Shader.colorAttribIdx, 3, false, 0, colorData);
		glEnableVertexAttribArray(Shader.colorAttribIdx);
		if (normalData != null) {
			glVertexAttribPointer(Shader.normalAttribIdx, 3, false, 0,
					normalData);
			glEnableVertexAttribArray(Shader.normalAttribIdx);
		}
		if (tex != null) {
			GL11.glTexCoordPointer(3, 0, textureData);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}

		// Draw the triangles that form the cube from the vertex data arrays.
		glDrawArrays(mode, 0, vertices.length);
	}

	public Shader getShader() {
		return shader;
	}
	
}
