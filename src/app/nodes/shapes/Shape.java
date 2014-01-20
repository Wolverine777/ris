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
import app.vecmath.Matrix;
import app.vecmath.Vector;
import app.vecmathimp.FactoryDefault;
import app.vecmathimp.MatrixImp;
import app.vecmathimp.VectorImp;

public abstract class Shape extends Node {
	protected Vertex[] vertices={};
	protected Vertex[] vertices3={};
	protected FloatBuffer positionData;
	protected FloatBuffer colorData;
	protected FloatBuffer normalData;
	protected FloatBuffer textureData;
	protected FloatBuffer positionData3;
	protected FloatBuffer colorData3;
	protected FloatBuffer normalData3;
	protected FloatBuffer textureData3;
	protected Texture tex;
	protected Shader shader;
	protected int mode = GL11.GL_QUADS;
	private Vector center = new VectorImp(0,0,0);
	private float radius;

	public Shape(String id, Shader shader) {
		super(id, FactoryDefault.vecmath.identityMatrix());
		this.shader = shader;
		findCenter();
		
		
	}

	public void display(Matrix m) {

		shader.activate();
		if (tex != null) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			tex.display();
		}
		shader.setModelMatrix(m.mult(getWorldTransform()));
		// Enable the vertex data arrays (with indices 0 and 1). We use a vertex
		// position and a vertex color.
		if(vertices3!=null&&vertices3.length!=0){
			glVertexAttribPointer(Shader.vertexAttribIdx, 3, false, 0, positionData3);
			glEnableVertexAttribArray(Shader.vertexAttribIdx);
			glVertexAttribPointer(Shader.colorAttribIdx, 3, false, 0, colorData3);
			glEnableVertexAttribArray(Shader.colorAttribIdx);
			if (normalData != null) {
				glVertexAttribPointer(Shader.normalAttribIdx, 3, false, 0,
						normalData3);
				glEnableVertexAttribArray(Shader.normalAttribIdx);
			}
			if (tex != null) {
				GL11.glTexCoordPointer(3, 0, textureData3);
				GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			}
			
			glDrawArrays(GL11.GL_TRIANGLES, 0, vertices3.length);
		}
		
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
	
	protected void findCenter(){
		float xKlein = 0;
		float xGroﬂ = 0;
		float yKlein = 0;
		float yGroﬂ = 0;
		float zKlein = 0;
		float zGroﬂ = 0;
		
		for(Vertex v: vertices){
			if(v.position.x() < xKlein){
				xKlein = v.position.x();
			}
			if(v.position.x() > xGroﬂ){
				xGroﬂ = v.position.x();
			}
			if(v.position.y() < yKlein){
				yKlein = v.position.y();
			}
			if(v.position.y() > yGroﬂ){
				yGroﬂ = v.position.y();
			}
			if(v.position.z() < zKlein){
				zKlein = v.position.z();
			}
			if(v.position.z() > zGroﬂ){
				zGroﬂ = v.position.z();
			}
		}
		center = new VectorImp((xGroﬂ + xKlein)/2, (yGroﬂ + yKlein)/2, (zGroﬂ + zKlein)/2);
		
		if(center.x() >= center.y() && center.x() >=center.z()){
			radius = center.x();			
		}
		else if (center.y() >= center.x() && center.y() >=center.z()){
			radius = center.y();			
		}
		else if (center.z() >= center.x() && center.z() >=center.y()){
			radius = center.z();			
		}
		System.out.println("Neues center f¸r Cubezuerst: " + super.id + center.toString());
	}
	 
	@Override
	public void updateWorldTransform(Matrix previousTrafo){
		super.updateWorldTransform(previousTrafo);
	    center = getWorldTransform().mult(MatrixImp.translate(center)).getPosition();
	    System.out.println("Neues center f¸r Cube: " + super.id + center.toString());
	}
	
	@Override
	public void updateWorldTransform() {
		super.updateWorldTransform();
//		center = getWorldTransform().mult(MatrixImp.translate(center)).getPosition();
//		System.out.println("Neues center f¸r Cube: " + super.id + center.toString());
	}
    
}
