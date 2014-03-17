package app;

import static app.nodes.NodeFactory.nodeFactory;
import static org.lwjgl.opengl.GL11.*;
import static vecmath.vecmathimp.FactoryDefault.vecmath;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import vecmath.Matrix;
import akka.actor.UntypedActor;
import app.Types.ObjectTypes;
import app.edges.Edge;
import app.eventsystem.CameraCreation;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeDeletion;
import app.eventsystem.NodeModification;
import app.eventsystem.StartNodeModification;
import app.messages.Message;
import app.messages.RendererInitialization;
import app.messages.RendererInitialized;
import app.nodes.Node;
import app.nodes.Text;
import app.nodes.camera.Camera;
import app.shader.Shader;

public class Renderer extends UntypedActor {
	public static final int width = 640;
	public static final int height = 480;

	private static UnicodeFont font;
	
	private static FloatBuffer perspectiveProjectionMatix = BufferUtils.createFloatBuffer(16);
	private static FloatBuffer orthgraphicProjectionMatix = BufferUtils.createFloatBuffer(16);
	
	private static boolean multisampling = false;

	private Map<String, Node> nodes = new HashMap<String, Node>();

	private Shader shader;
	private Node start;
	private Camera camera;

	// private Float medTime=0f;

	private void initialize() {
		setUpDisplay();
		font=setUpFonts();
		System.out.println("font"+font);
		setUpCamera();
		shader = new Shader();
		setUpLighting();
		
		// Set background color to black.
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		// Enable depth testing.
		glEnable(GL11.GL_DEPTH_TEST);
		
//		shader = new Shader();
		// shader = new Shader(new
		// File("src/app/shadercode/backgroundVertShader"), new
		// File("src/app/shadercode/backgroundFragShader"));

		getSender().tell(new RendererInitialized(shader), self());
		getSender().tell(Message.INITIALIZED, self());
	}

	private void display() {

		
		// Adjust the the viewport to the actual window size. This makes the
		// rendered image fill the entire window.
		glViewport(0, 0, width, height);

		// Clear all buffers.
		glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
//        glLoadIdentity();
        
     // Assemble the transformation matrix that will be applied to all
     // vertices in the vertex shader.
        float aspect = (float) width / (float) height;
        
        // The perspective projection. Camera space to NDC.
        Matrix projectionMatrix = vecmath.perspectiveMatrix(60f, aspect, 0.1f,
        		100f);
        Shader.setProjectionMatrix(projectionMatrix);
        
        camera.activate();
//    	shader.activate(); ist bereits in jedem Shape drin
    	start.display(start.getWorldTransform());
    	glUseProgram(0);
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(orthgraphicProjectionMatix);
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();
        glDisable(GL_LIGHTING);
        font.drawString(100, 100, "Benni ich kann Text TEXT ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        glEnable(GL_LIGHTING);
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(perspectiveProjectionMatix);
        glMatrixMode(GL_MODELVIEW);
		
		
		start.display(start.getWorldTransform());
		
		Display.update();

		getSender().tell(Message.DONE, self());

		if (Display.isCloseRequested()) {
			// System.out.println("Average ms took:"+medTime); //TODO:
			// nullpointer
			Display.destroy();
			context().system().stop(getSender());
			context().system().shutdown();
		}

	}
	
	private static UnicodeFont setUpFonts(){
		java.awt.Font awtFont = new java.awt.Font("Arial Bold", java.awt.Font.BOLD, 18);
		UnicodeFont font = new UnicodeFont(awtFont);
		System.out.println("make new font"+font);
		font.getEffects().add(new ColorEffect(java.awt.Color.white));
		font.addAsciiGlyphs();
		try {
			font.loadGlyphs();
		} catch (SlickException e){
			e.printStackTrace();
			cleanUp();
		}
		return font;
	}
	
	 private static void cleanUp() {
        Display.destroy();
        System.exit(0);
	 }
	 
	 private static void setUpLighting() {
		 glShadeModel(GL_SMOOTH);
	     glEnable(GL_DEPTH_TEST);
	     glEnable(GL_LIGHTING);
	     glEnable(GL_TEXTURE_2D);
	     glEnable(GL_BLEND);
	     glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//	     glEnable(GL_LIGHT0);
//	     FloatBuffer lm =BufferUtils.createFloatBuffer(4);
//	     lm.put(new float[]{0.05f, 0.05f, 0.05f, 1f});
//	     lm.rewind();
//	     glLightModel(GL_LIGHT_MODEL_AMBIENT, lm);
//	     FloatBuffer l =BufferUtils.createFloatBuffer(4);
//	     l.put(new float[]{0, 0, 0, 1});
//	     l.rewind();
//	     glLight(GL_LIGHT0, GL_POSITION, l);
//	     glEnable(GL_CULL_FACE);
//	     glCullFace(GL_BACK);
//	     glEnable(GL_COLOR_MATERIAL);
//	     glColorMaterial(GL_FRONT, GL_DIFFUSE);
	  }
	 
	 //TODO: warum static?try
	 private static void setUpDisplay(){
		 try {
				Display.setDisplayMode(new DisplayMode(width, height));
				Display.setSwapInterval(1);
				Display.setVSyncEnabled(true);
				Display.setTitle("App");
				if (multisampling)
					Display.create(new PixelFormat().withSamples(8));
				else
					Display.create();

				// Limit to 60 FPS
//				Display.setSwapInterval(1);
//				Display.setVSyncEnabled(true);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		 
	 }
	 
	 public static void setUpCamera(){
		float aspect = (float) width / (float) height;
		Matrix projectionMatrix = vecmath.perspectiveMatrix(60f, aspect, 0.1f,
					100f);
		Shader.setProjectionMatrix(projectionMatrix);
	    glGetFloat(GL_PROJECTION_MATRIX, perspectiveProjectionMatix);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
	    glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
	    glGetFloat(GL_PROJECTION_MATRIX, orthgraphicProjectionMatix);
	    glLoadMatrix(perspectiveProjectionMatix);
	    glMatrixMode(GL_MODELVIEW);
	 }
	   
	   

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.DISPLAY) {
			display();
		} else if (message instanceof RendererInitialization) {
			initialize();
		} else if (message instanceof NodeCreation) {
			NodeCreation nc = (NodeCreation) message;
			if (nc.type == ObjectTypes.GROUP) {
				nodes.put(nc.getId(), nodeFactory.groupNode(nc.id));
			} else if (nc.type == ObjectTypes.CUBE) {
				nodes.put(nc.getId(), nodeFactory.cube(nc.id, nc.shader, nc.w, nc.h,nc.d, nc.mass));
			} else if (nc.type == ObjectTypes.PIPE) {
				nodes.put(nc.getId(), nodeFactory.pipe(nc.id, nc.shader, nc.r,nc.lats, nc.longs, nc.mass));
			} else if (nc.type == ObjectTypes.SPHERE) {
				nodes.put(nc.getId(), nodeFactory.sphere(nc.id, nc.shader, nc.mass));
			} else if (nc.type == ObjectTypes.PLANE) {
				nodes.put(nc.getId(), nodeFactory.plane(nc.id, nc.shader, nc.w, nc.d, nc.h, nc.mass));
			} else if (nc.type == ObjectTypes.OBJECT) {
				nodes.put(nc.getId(), nodeFactory.obj(nc.id, nc.shader, nc.sourceFile, nc.sourceTex, nc.mass));
			}else if (nc.type == ObjectTypes.CAR){
				nodes.put(nc.getId(), nodeFactory.car(nc.getId(), nc.shader, nc.sourceFile, nc.speed, nc.mass));
			}else if (nc.type == ObjectTypes.COIN){
				nodes.put(nc.getId(), nodeFactory.coin(nc.getId(), nc.shader, nc.sourceFile, nc.mass));
			}else if(((NodeCreation) message).type == ObjectTypes.CANON){
				Node newNode = nodeFactory.canon(nc.id, nc.shader, nc.sourceFile, nc.sourceTex, nc.mass);
				nodes.put(newNode.getId(), newNode);
			}else if(nc.type==ObjectTypes.TEXT){
				Text t=nodeFactory.text(nc.getId(), nc.modelmatrix, nc.text, nc.font);
				nodes.put(nc.getId(), t);
				t.setOrthgraphicProjectionMatix(orthgraphicProjectionMatix);
				t.setPerspectiveProjectionMatix(perspectiveProjectionMatix);
			}

		} else if (message instanceof CameraCreation) {
			camera = nodeFactory.camera(((CameraCreation) message).id);
			nodes.put(((CameraCreation) message).id, camera);
		} else if (message instanceof NodeModification) {
			Node modify = nodes.get(((NodeModification) message).id);

			if (((NodeModification) message).localMod != null) {
				// modify.setLocalTransform(((NodeModification)
				// message).localMod);
				// modify.updateWorldTransform();
				modify.updateWorldTransform(((NodeModification) message).localMod);

				// modify.setLocalTransform(modify.getWorldTransform());
			}
			if (((NodeModification) message).appendTo != null) {
				modify.appendTo(nodes
						.get(((NodeModification) message).appendTo));
			}
		} else if (message instanceof StartNodeModification) {
			start = nodes.get(((StartNodeModification) message).id);

		} else if (message instanceof NodeDeletion) {
//			start.append(nodes.get("text"));
			NodeDeletion delete = (NodeDeletion) message;
			for (String id : delete.ids) {
				Node modify = nodes.get(id);
				ArrayList<Edge> removeEdges = new ArrayList<>();
				if (modify != null) {
					for (Edge e : modify.getEdges()) {
						removeEdges.add(e);
						// nodes.get(e.getOtherNode(modify).id).removeEdge(e);

					}
					for (Edge e : removeEdges) {
						modify.removeEdge(e);
					}

					nodes.remove(modify);
				}
			}
		}
		// else if(message instanceof Float){
		// if(medTime==0)medTime=(Float)message;
		// else medTime=(medTime+(Float)message)/2;
		// }
	}
	
}