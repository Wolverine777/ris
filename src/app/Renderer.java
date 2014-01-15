package app;

import static app.nodes.NodeFactory.nodeFactory;
import static app.vecmathimp.FactoryDefault.vecmath;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import akka.actor.UntypedActor;
import app.eventsystem.CameraCreation;
import app.eventsystem.NodeCreation;
import app.eventsystem.NodeModification;
import app.eventsystem.StartNodeModification;
import app.eventsystem.Types;
import app.messages.Message;
import app.messages.RendererInitialization;
import app.messages.RendererInitialized;
import app.nodes.Node;
import app.nodes.camera.Camera;
import app.shader.Shader;
import app.vecmath.Matrix;

public class Renderer extends UntypedActor {
	private static final int width = 640;
	private static final int height = 480;

	private boolean multisampling = false;

	private Map<String, Node> nodes = new HashMap<String, Node>();

	private Shader shader;
	private Node start;
	private Camera camera;

	private void initialize() {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));

			if (multisampling)
				Display.create(new PixelFormat().withSamples(8));
			else
				Display.create();

			// Limit to 60 FPS
			Display.setSwapInterval(1);
			Display.setVSyncEnabled(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		// Set background color to black.
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		// Enable depth testing.
		glEnable(GL11.GL_DEPTH_TEST);

		shader = new Shader();
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

		shader.activate();

		// Assemble the transformation matrix that will be applied to all
		// vertices in the vertex shader.
		float aspect = (float) width / (float) height;

		// The perspective projection. Camera space to NDC.
		Matrix projectionMatrix = vecmath.perspectiveMatrix(60f, aspect, 0.1f,
				100f);
		Shader.setProjectionMatrix(projectionMatrix);

		camera.activate();
		System.out.println("hier!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!2222222: " + start.toString());
		start.display(start.getWorldTransform());

		Display.setTitle("App");
		Display.update();

		getSender().tell(Message.DONE, self());

		if (Display.isCloseRequested()) {
			Display.destroy();
			context().system().shutdown();
		}

	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message == Message.DISPLAY) {
			display();
		} else if (message instanceof RendererInitialization) {
			initialize();
		} else if (message instanceof NodeCreation) {
			System.out.println("NodeCreation");

			if (((NodeCreation) message).type == Types.GROUP) {
				Node newNode = nodeFactory
						.groupNode(((NodeCreation) message).id);
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == Types.CUBE) {

				System.out.println("Shadering cube with "
						+ ((NodeCreation) message).shader);

				Node newNode = nodeFactory.cube(((NodeCreation) message).id,
						((NodeCreation) message).shader,
						((NodeCreation) message).w, ((NodeCreation) message).h,
						((NodeCreation) message).d);
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == Types.PIPE) {
				Node newNode = nodeFactory.pipe(((NodeCreation) message).id,
						((NodeCreation) message).shader,
						((NodeCreation) message).r,
						((NodeCreation) message).lats,
						((NodeCreation) message).longs);
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == Types.SPHERE) {
				Node newNode = nodeFactory.sphere(((NodeCreation) message).id,
						((NodeCreation) message).shader);
				nodes.put(newNode.id, newNode);
			} else if (((NodeCreation) message).type == Types.PLANE) {
				Node newNode = nodeFactory.plane(((NodeCreation) message).id,
						((NodeCreation) message).shader,
						((NodeCreation) message).w, ((NodeCreation) message).d);
				nodes.put(newNode.id, newNode);
			}

		} else if (message instanceof CameraCreation) {
			System.out.println("CameraCreation");

			camera = nodeFactory.camera(((CameraCreation) message).id);
			nodes.put(((CameraCreation) message).id, camera);

		} else if (message instanceof NodeModification) {
			System.out.println("NodeModification");

			System.out.println("Nodes " + nodes);
			System.out.println("Accesing " + ((NodeModification) message).id);

			Node modify = nodes.get(((NodeModification) message).id);

			if (((NodeModification) message).localMod != null) {
				 modify.setLocalTransform(((NodeModification) message).localMod);
				 modify.updateWorldTransform();
				
				// modify.setLocalTransform(modify.getWorldTransform());
			}
			if (((NodeModification) message).appendTo != null) {

				System.out.println("Appending "
						+ ((NodeModification) message).id + " to "
						+ ((NodeModification) message).appendTo);

				modify.appendTo(nodes
						.get(((NodeModification) message).appendTo));
			}

		} else if (message instanceof StartNodeModification) {
			System.out.println("StartNodeModification");

			start = nodes.get(((StartNodeModification) message).id);

		}
	}
}