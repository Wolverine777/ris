package app.nodes;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

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
	private FloatBuffer perspectiveProjectionMatix = BufferUtils.createFloatBuffer(16);
	private FloatBuffer orthgraphicProjectionMatix = BufferUtils.createFloatBuffer(16);
	private String text="";
	private FontInfo info;

	public Text(String id, Matrix modelMatrix, String text, FontInfo font){
		super(id, modelMatrix);
		posX = getWorldTransform().getPosition().x();
		posY = getWorldTransform().getPosition().y();
		this.text=text;
		this.info=font;
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
			System.out.println("Posx: " + posX);
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
		if(font==null)font=setUpFonts(info);
		System.out.println("Posx anfang display: " + posX);
//		setUpCamera();
		glUseProgram(0);
		glMatrixMode(GL_PROJECTION);
		// TODO: Besser nur zum testen
		glLoadMatrix(orthgraphicProjectionMatix);
//		glLoadMatrix(Renderer.getOrthgraphicProjectionMatix());
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glDisable(GL_LIGHTING);
		//TODO: syso kommt 2 mal
//		System.out.println("text font:"+font+" t:"+text);
		font.drawString(posX, posY, text);
		glEnable(GL_LIGHTING);
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(perspectiveProjectionMatix);
//		glLoadMatrix(Renderer.getPerspectiveProjectionMatix());
		glMatrixMode(GL_MODELVIEW);
		System.out.println("Posx ende display: " + posX);
	}

	private UnicodeFont setUpFonts(FontInfo info) {
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
//			System.exit(0);
		}
		return font;
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPerspectiveProjectionMatix(FloatBuffer perspectiveProjectionMatix) {
		this.perspectiveProjectionMatix = perspectiveProjectionMatix;
	}

	public void setOrthgraphicProjectionMatix(FloatBuffer orthgraphicProjectionMatix) {
		this.orthgraphicProjectionMatix = orthgraphicProjectionMatix;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
