package app.shader;

import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.File;
import org.lwjgl.opengl.GL20;

import app.toolkit.BasicFunctions;
import app.toolkit.MatrixUniform;
import app.toolkit.Util;
import app.vecmath.Matrix;
import app.vecmathimp.FactoryDefault;

public class Shader {
	private static Matrix viewMatrix = FactoryDefault.vecmath.identityMatrix();
	private static Matrix projectionMatrix = FactoryDefault.vecmath.identityMatrix();
	
	// The vertex program source code.
	private String[] vsSource /*= {
			"uniform mat4 modelMatrix;",
			"uniform mat4 viewMatrix;",
			"uniform mat4 projectionMatrix;",

			"attribute vec3 vertex;",
			"attribute vec3 color;",
			"varying vec3 fcolor;",

			"void main() {",
			"  fcolor = color;",
			"  gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(vertex, 1);",
	"}" }*/;

	// The fragment program source code.
	private String[] fsSource /*= { 
			"varying vec3 fcolor;",
			"void main() {", 
			"  gl_FragColor = vec4(fcolor, 1.0);", 
	"}" }*/;

	// The shader program.
	private int program;

	// The location of the "mvpMatrix" uniform variable.
	private MatrixUniform modelMatrixUniform;
	private MatrixUniform viewMatrixUniform;
	private MatrixUniform projectionMatrixUniform;

	public void setModelMatrix(Matrix modelMatrix) {
		modelMatrixUniform.set(modelMatrix);
	}


	protected void setViewMatrixUniform(Matrix viewMatrix) {
		viewMatrixUniform.set(viewMatrix);
	}
	
	public static void setProjectionMatrix(Matrix m) {
		projectionMatrix = m;
	}


	protected void setProjectionMatrixUniform(Matrix projectionMatrix) {
		projectionMatrixUniform.set(projectionMatrix);
	}

	public void activate(){
		// Activate the shader program and set the transformation matricies to the
		// uniform variables.
		setProjectionMatrixUniform(projectionMatrix);
		setViewMatrixUniform(viewMatrix);		
		glUseProgram(program);
	}
	
	public static void setViewMatrix(Matrix m){
		viewMatrix = m;
	}


	// The attribute indices for the vertex data.
	public static int vertexAttribIdx = 0;
	public static int colorAttribIdx = 1;
	public static int normalAttribIdx = 2;
	
	public Shader(){
		vsSource =BasicFunctions.readFile(new File("src/app/shadercode/basicVertex.txt"));
		fsSource = BasicFunctions.readFile(new File("src/app/shadercode/basicFragment.txt"));
		init();
	}
	
	public Shader(File vertexShader, File fragmentShader){
		vsSource =BasicFunctions.readFile(vertexShader);
		fsSource = BasicFunctions.readFile(fragmentShader);
		init();
	}
	
	private void init(){
		int vs = glCreateShader(GL20.GL_VERTEX_SHADER);
		glShaderSource(vs, vsSource);
		glCompileShader(vs);
		Util.checkCompilation(vs);

		// Create and compile the fragment shader.
		int fs = glCreateShader(GL20.GL_FRAGMENT_SHADER);
		glShaderSource(fs, fsSource);
		glCompileShader(fs);
		Util.checkCompilation(fs);

		// Create the shader program and link vertex and fragment shader
		// together.
		program = glCreateProgram();
		glAttachShader(program, vs);
		glAttachShader(program, fs);

		// Bind the vertex attribute data locations for this shader program. The
		// shader expects to get vertex and color data from the mesh. This needs to
		// be done *before* linking the program.
		glBindAttribLocation(program, vertexAttribIdx, "vertex");
		glBindAttribLocation(program, colorAttribIdx, "color");
		glBindAttribLocation(program, normalAttribIdx, "normal");

		// Link the shader program.
		glLinkProgram(program);
		Util.checkLinkage(program);

		// Bind the matrix uniforms to locations on this shader program. This needs
		// to be done *after* linking the program.
		modelMatrixUniform = new MatrixUniform(program, "modelMatrix");
		viewMatrixUniform = new MatrixUniform(program, "viewMatrix");
		projectionMatrixUniform = new MatrixUniform(program, "projectionMatrix");
	}
	
}
