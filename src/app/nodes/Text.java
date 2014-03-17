package app.nodes;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.FloatBuffer;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import app.Renderer;
import app.datatype.FontInfo;
import app.nodes.camera.Camera;
import vecmath.Matrix;
import vecmath.Vector;
import vecmath.vecmathimp.MatrixImp;

/**
 * @author Benjamin Reemts
 * 
 */

public class Text extends Node {
	private float posX, posY;
	private UnicodeFont font;
	private static FloatBuffer perspectiveProjectionMatix = BufferUtils.createFloatBuffer(16);
	private static FloatBuffer orthgraphicProjectionMatix = BufferUtils.createFloatBuffer(16);
	private String text="";
	private FontInfo info;

	public Text(String id) {
		super(id);
		posX = getWorldTransform().getPosition().x();
		posY = getWorldTransform().getPosition().y();
	}

	public Text(String id, Matrix modelMatrix) {
		super(id, modelMatrix);
		posX = getWorldTransform().getPosition().x();
		posY = getWorldTransform().getPosition().y();
	}
	public Text(String id, Matrix modelMatrix, String text, FontInfo font){
		super(id, modelMatrix);
		posX = getWorldTransform().getPosition().x();
		posY = getWorldTransform().getPosition().y();
		this.text=text;
		this.info=font;
	}

	public Text(String id, Matrix modelMatrix, Node n) {
		super(id, modelMatrix, n);
		posX = getWorldTransform().getPosition().x();
		posY = getWorldTransform().getPosition().y();
	}

	public Text(String id, Matrix modelMatrix, Node n, Map<String, String> data) {
		super(id, modelMatrix, n, data);
		posX = getWorldTransform().getPosition().x();
		posY = getWorldTransform().getPosition().y();
//		setUpCamera();
	}

	public static void setUpCamera() {
//		float aspect = (float) Renderer.width / (float) Renderer.height;
//		Matrix projectionMatrix = vecmath.perspectiveMatrix(60f, aspect, 0.1f, 100f);
//		Shader.setProjectionMatrix(projectionMatrix);
		glGetFloat(GL_PROJECTION_MATRIX, perspectiveProjectionMatix);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glGetFloat(GL_PROJECTION_MATRIX, orthgraphicProjectionMatix);
		glLoadMatrix(perspectiveProjectionMatix);
		glMatrixMode(GL_MODELVIEW);
	}

	@Override
	public void updateWorldTransform(Matrix previousTrafo) {
		if (MatrixImp.isTranslationMatrix(previousTrafo)) {
			Vector pos = previousTrafo.getPosition();
			if (Camera.getCamZ() == null)
				previousTrafo = MatrixImp.translate(pos.x(), pos.y(), pos.z());
			else
				previousTrafo = MatrixImp.translate(pos.x(), pos.y(), Camera.getCamZ());
			super.updateWorldTransform(previousTrafo);
			posX = getWorldTransform().getPosition().x();
			posY = getWorldTransform().getPosition().y();
		} else if (MatrixImp.isScaleMatrix(previousTrafo)) {
			// TODO: Text scale
//			float scale = height / 8f;
//            glTranslatef(x, y, 0);
//            glScalef(scale,scale,1f);
		}
	}

	@Override
	public void updateWorldTransform() {
		super.updateWorldTransform();
		if (MatrixImp.isTranslationMatrix(getLocalTransform())) {
			posX = getWorldTransform().getPosition().x();
			posY = getWorldTransform().getPosition().y();
		}
	}

	@Override
	public void display(Matrix m) {
		if(font==null)font=setUpFonts();
		glUseProgram(0);
		glMatrixMode(GL_PROJECTION);
		// TODO: Besser nur zum testen
		glLoadMatrix(Renderer.getOrthgraphicProjectionMatix());
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glDisable(GL_LIGHTING);
		System.out.println("text font:"+font+" t:"+text);
		font.drawString(1, 1, text);
		glEnable(GL_LIGHTING);
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(Renderer.getPerspectiveProjectionMatix());
		glMatrixMode(GL_MODELVIEW);
	}

	private UnicodeFont setUpFonts() {
		java.awt.Font awtFont=null;
		if(info!=null) awtFont = new java.awt.Font(info.getName(), info.getStyle(), info.getSize());
		else awtFont = new java.awt.Font("Arial Bold", java.awt.Font.BOLD, 18);
		UnicodeFont font = new UnicodeFont(awtFont);
		font.getEffects().add(new ColorEffect(java.awt.Color.white));
		font.addAsciiGlyphs();
		try {
			font.loadGlyphs();
		} catch (SlickException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return font;
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

}
