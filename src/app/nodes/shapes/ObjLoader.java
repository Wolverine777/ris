package app.nodes.shapes;

import static vecmath.vecmathimp.FactoryDefault.vecmath;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;

import vecmath.Color;
import vecmath.Matrix;
import vecmath.Vector;
import vecmath.vecmathimp.MatrixImp;
import vecmath.vecmathimp.VectorImp;
import app.toolkit.BasicFunctions;
import app.toolkit.Texture;
import app.nodes.shapes.Vertex;
import app.shader.Shader;

/**
 * @author Benjamin Reemts
 * 
 */

public class ObjLoader extends Shape {

	// The positions of the cube vertices.
	private Vector[] p = {};
	private Vector[] n = {};
	private Vector[] t = {};
	private File sourceFile, sourceTex;

//	 private final Color[] defaultCol ={ Vertex.col((float) Math.random(), (float) Math.random(), (float) Math.random()),Vertex.col((float) Math.random(), (float) Math.random(), (float) Math.random())};
//	private final Color[] defaultCol = { Vertex.col(0.4f, 0.7f, 0.8f),
//			Vertex.col(1, 0, 0), Vertex.col(1, 1, 0), Vertex.col(0, 1, 0),
//			Vertex.col(1, 0, 1), Vertex.col(0, 0, 1), Vertex.col(0, 1, 1),
//			Vertex.col(1, 1, 1) };
	private final Color[] defaultCol = {Vertex.col(0.45f, 0.45f, 0.45f)};

//	private Texture tex;

	public ObjLoader(String id, Shader shader, Matrix modelMatrix, float mass) {
		this(id, shader, new File("obj/cube.obj"), modelMatrix, mass);
	}

	public ObjLoader(String id, Shader shader, File sourceFile, Matrix modelMatrix, float mass) {
		this(id, shader, sourceFile, null, modelMatrix, mass);
	}

	public ObjLoader(String id, Shader shader, File sourceFile, File sourceTex, Matrix modelMatrix, float mass) {
		super(id, shader, mass, modelMatrix);
		if (sourceTex != null){
			super.tex = new Texture(sourceTex);
		}

		if (sourceFile == null)
			sourceFile = new File("obj/cube.obj");
		List<String> linesInObj = Arrays.asList(BasicFunctions
				.readFile(sourceFile));
		p = getVecs(linesInObj, p);
		n = getNormals(linesInObj, n);
		t = getTex(linesInObj, t);
		fillVert(linesInObj);
		// Prepare the vertex data arrays.
		// Compile vertex data into a Java Buffer data structures that can be
		// passed to the OpenGL API efficently.
		positionData3 = BufferUtils.createFloatBuffer(vertices3.length
				* vecmath.vectorSize());
		colorData3 = BufferUtils.createFloatBuffer(vertices3.length
				* vecmath.colorSize());
		normalData3 = BufferUtils.createFloatBuffer(vertices3.length
				* vecmath.vectorSize());
		if (tex != null)
			textureData3 = BufferUtils.createFloatBuffer(vertices3.length
					* vecmath.vectorSize());

		for (Vertex v : vertices3) {
			positionData3.put(v.position.asArray());
			colorData3.put(v.color.asArray());
			normalData3.put(v.normal.asArray());
			if (tex != null)
				textureData3.put(v.texcoord.asArray());
		}
		positionData3.rewind();
		colorData3.rewind();
		normalData3.rewind();
		if (tex != null)
			textureData3.rewind();

		positionData = BufferUtils.createFloatBuffer(vertices.length
				* vecmath.vectorSize());
		colorData = BufferUtils.createFloatBuffer(vertices.length
				* vecmath.colorSize());
		normalData = BufferUtils.createFloatBuffer(vertices.length
				* vecmath.vectorSize());
		if (tex != null)
			textureData = BufferUtils.createFloatBuffer(vertices.length
					* vecmath.vectorSize());

		for (Vertex v : vertices) {
			positionData.put(v.position.asArray());
			colorData.put(v.color.asArray());
			normalData.put(v.normal.asArray());
			if (tex != null)
				textureData.put(v.texcoord.asArray());
		}
		positionData.rewind();
		colorData.rewind();
		normalData.rewind();
		if (tex != null)
			textureData.rewind();
		findCenter();
		if(MatrixImp.isTranslationMatrix(modelMatrix))setCenter(modelMatrix.mult(vecmath.translationMatrix(getCenter())).getPosition());
		if(modelMatrix.get(0, 0) == modelMatrix.get(1, 1) && modelMatrix.get(1, 1) == modelMatrix.get(2, 2))radius = modelMatrix.get(0, 0)*radius;
		//Only for Level design, change in setblocked in Ai 
		radius=radius*0.85f;
	}

	private Vector[] getVecs(List<String> source, Vector[] points) {
		LinkedList<Vector> targetP = new LinkedList<Vector>(
				Arrays.asList(points));
		for (String s : source) {
			if (s.startsWith("v ")) {
				String[] num = s.split("\\s+");
				targetP.add(Vertex.vec(Float.parseFloat(num[1]),
						Float.parseFloat(num[2]), Float.parseFloat(num[3])));
			}
		}
		return targetP.toArray(new Vector[0]);
	}

	private Vector[] getNormals(List<String> source, Vector[] normals) {
		LinkedList<Vector> targetP = new LinkedList<Vector>(
				Arrays.asList(normals));
		for (String s : source) {
			if (s.startsWith("vn ")) {
				String[] num = s.split("\\s+");
				targetP.add(Vertex.vec(Float.parseFloat(num[1]),
						Float.parseFloat(num[2]), Float.parseFloat(num[3])));
			}
		}
		return targetP.toArray(new Vector[0]);
	}

	private Vector[] getTex(List<String> source, Vector[] text) {
		LinkedList<Vector> targetP = new LinkedList<Vector>(Arrays.asList(text));
		for (String s : source) {
			//TODO: Make Regex for mult spaces bevor nummber
			if (s.startsWith("vt ")) {
				String[] num = s.split("\\s+");
				// -1* f¸r rechtsh‰ndiges koordinatensystem
				if (num.length == 3)
					targetP.add(Vertex.vec(-1 * Float.parseFloat(num[1]),
							Float.parseFloat(num[2]), 0));
				else if (num.length == 4)
					targetP.add(Vertex.vec(-1 * Float.parseFloat(num[1]),
							Float.parseFloat(num[2]), Float.parseFloat(num[3])));
			}
		}
		// for(Vector d:targetP)System.out.println(d.x()+", "+d.y()+", "+d.z());
		return targetP.toArray(new Vector[0]);
	}

	private void fillVert(List<String> source) {
		LinkedList<Vertex> targetV3 = new LinkedList<Vertex>(
				Arrays.asList(vertices3));
		LinkedList<Vertex> targetV4 = new LinkedList<Vertex>(
				Arrays.asList(vertices));
		for (String s : source) {
			if (s.startsWith("f ")) {
				String[] num = s.split("\\s+");
				if (num.length == 4) {
					doATri(targetV3, num);
				} else if (num.length == 5) {
					doAQuad(targetV4, num);
				} else if (num.length == 6) {
					doAQuad(targetV4, splittBy(num, 0, 5));
					doATri(targetV3, splittBy(num, 2, 6));
					// System.out.println("5");
				} /*
				 * else if(num.length ==7){
				 * 
				 * }
				 */else
					/* doATri(targetV3, num); */System.out
							.println("Not Supported");

			}
		}
		vertices3 = targetV3.toArray(new Vertex[0]);
		vertices = targetV4.toArray(new Vertex[0]);
	}

	private String[] splittBy(String[] source, int start, int end) {
		String[] out = new String[end - start];
		for (int i = start; i < end; i++) {
			out[i - start] = source[start + (i - start)];
		}
		return out;
	}

	private void doATri(LinkedList<Vertex> targetV3, String[] lineSplitted) {
		Vertex v1 = makeVert(1, lineSplitted);
		Vertex v2 = makeVert(2, lineSplitted);
		Vertex v3 = makeVert(3, lineSplitted);
		if (v1.normal == null) {
//			System.out.println("tri n");
			// Constis normalize: (v1.sub(v0).cross(v2.sub(v0))).normalize(); Vector v0 = p[i]; Vector v1 = p[i + 1]; Vector v2 = p[i + 2];
//			v1.setNormal(v2.position.sub(v1.position).cross(v3.position.sub(v1.position)).normalize());
//			v2.setNormal(v2.position.sub(v1.position).cross(v3.position.sub(v1.position)).normalize());
//			v3.setNormal(v2.position.sub(v1.position).cross(v3.position.sub(v1.position)).normalize());
			v1.setNormal(v3.position.sub(v2.position).cross(
					v1.position.sub(v2.position)));
			// v1.setNormal(v1.position.cross(v2.position).cross(v3.position));
		}
		if (v2.normal == null)
			v2.setNormal(v3.position.sub(v2.position).cross(
					v1.position.sub(v2.position)));
		if (v3.normal == null)
			v3.setNormal(v3.position.sub(v2.position).cross(
					v1.position.sub(v2.position)));
		targetV3.add(v1);
		targetV3.add(v2);
		targetV3.add(v3);
	}

	private void doAQuad(LinkedList<Vertex> targetV4, String[] lineSplitted) {
		Vertex v1 = makeVert(1, lineSplitted);
		Vertex v2 = makeVert(2, lineSplitted);
		Vertex v3 = makeVert(3, lineSplitted);
		Vertex v4 = makeVert(4, lineSplitted);
		if (v1.normal == null) {
//			System.out.println("quad n");
			v1.setNormal(v3.position.sub(v2.position).cross(
					v1.position.sub(v2.position)));
		}
		if (v2.normal == null)
			v2.setNormal(v3.position.sub(v2.position).cross(
					v1.position.sub(v2.position)));
		if (v3.normal == null)
			v3.setNormal(v3.position.sub(v2.position).cross(
					v1.position.sub(v2.position)));
		if (v4.normal == null)
			v4.setNormal(v3.position.sub(v2.position).cross(
					v1.position.sub(v2.position)));
		targetV4.add(v1);
		targetV4.add(v2);
		targetV4.add(v3);
		targetV4.add(v4);
	}

	private Vertex makeVert(int pos, String[] lineSplitted) {
		// for(String w:lineSplitted)System.out.print(w+", ");
		// System.out.println();
		// for(String q:lineSplitted[pos].split("\\D"))System.out.print(q+", ");
		// System.out.println();
		int lastNum = lineSplitted[pos].split("\\D").length;
		String[] nums = lineSplitted[pos].split("\\D");
		if (lastNum == 1) {
			return new Vertex(p[Integer.parseInt(nums[0]) - 1],
					defaultCol[(int) (Math.random() * (defaultCol.length))]);
		} else if (lastNum == 2) {
			return new Vertex(
					p[Integer.parseInt(nums[0]) - 1],
					defaultCol[(int) (Math.random() * (defaultCol.length))],
					null, t[Integer.parseInt(nums[1]) - 1]);
		} else if (lastNum == 3) {
			if (lineSplitted[pos].split("\\D")[1].equalsIgnoreCase("")
					|| t.length == 0) {
				return new Vertex(
						p[Integer.parseInt(nums[0]) - 1],
						defaultCol[(int) (Math.random() * (defaultCol.length))],
						n[Integer.parseInt(nums[lastNum - 1]) - 1]);
			} else {
				return new Vertex(
						p[Integer.parseInt(nums[0]) - 1],
						defaultCol[(int) (Math.random() * (defaultCol.length))],
						n[Integer.parseInt(nums[lastNum - 1]) - 1], t[Integer
								.parseInt(nums[1]) - 1]);
			}
		} else {
			System.out.println("Face definition longer than 3");
			return null;
		}
	}
	
	@Override
	protected void findCenter(){
		Float xKlein = null;
		Float xGroﬂ = null;
		Float yKlein = null;
		Float yGroﬂ = null;
		Float zKlein = null;
		Float zGroﬂ = null;
		Float xKlein2 = null;
		Float xGroﬂ2 = null;
		Float yKlein2 = null;
		Float yGroﬂ2 = null;
		Float zKlein2 = null;
		Float zGroﬂ2 = null;
		float radiustmp = 0;
		float radiustmp2 = 0;
		
		for(Vertex v: vertices){
			if(xKlein==null)xKlein = v.position.x();
			if(xGroﬂ==null)xGroﬂ = v.position.x();
			if(yKlein==null)yKlein = v.position.y();
			if(yGroﬂ==null)yGroﬂ = v.position.y();
			if(zKlein==null)zKlein = v.position.z();
			if(zGroﬂ==null)zGroﬂ = v.position.z();
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
		
		for(Vertex v: vertices3){
			if(xKlein2==null)xKlein2 = v.position.x();
			if(xGroﬂ2==null)xGroﬂ2 = v.position.x();
			if(yKlein2==null)yKlein2 = v.position.y();
			if(yGroﬂ2==null)yGroﬂ2 = v.position.y();
			if(zKlein2==null)zKlein2 = v.position.z();
			if(zGroﬂ2==null)zGroﬂ2 = v.position.z();
			if(v.position.x() < xKlein2){
				xKlein2 = v.position.x();
			}
			if(v.position.x() > xGroﬂ2){
				xGroﬂ2 = v.position.x();
			}
			if(v.position.y() < yKlein2){
				yKlein2 = v.position.y();
			}
			if(v.position.y() > yGroﬂ2){
				yGroﬂ2 = v.position.y();
			}
			if(v.position.z() < zKlein2){
				zKlein2 = v.position.z();
			}
			if(v.position.z() > zGroﬂ2){
				zGroﬂ2 = v.position.z();
			}			
		}
		xKlein = min(xKlein, xKlein2);
		xGroﬂ = max(xGroﬂ, xGroﬂ2);
		yKlein = min(yKlein, yKlein2);
		yGroﬂ = max(yGroﬂ, yGroﬂ2);
		zKlein = min(zKlein, zKlein2);
		zGroﬂ = max(zGroﬂ, zGroﬂ2);
//		System.out.println(getId()+" xkl:"+xKlein+" xGr:"+xGroﬂ+" yKl:"+yKlein+" yGr:"+yGroﬂ+" zKl:"+zKlein+" zGr:"+zGroﬂ);
		
		setCenter(new VectorImp((xGroﬂ + xKlein)/2, (yGroﬂ + yKlein)/2, (zGroﬂ + zKlein)/2));
		
		for(Vertex v: vertices){
			radiustmp = Math.max(v.position.sub(getCenter()).length(),radiustmp);
		}
		for(Vertex v: vertices3){
			radiustmp2 = Math.max(v.position.sub(getCenter()).length(),radiustmp2);
		}
		
		radiustmp = Math.max(radiustmp, radiustmp2);		
		
		setRadius(radiustmp);
		
//		setRadius(Math.max(Math.max((Math.abs(xGroﬂ - xKlein)),Math.abs(yGroﬂ - yKlein)), Math.abs(zGroﬂ - zKlein)));
//		 System.out.println("Radius f¸r ObjLoader: " + super.getId() + " " + getRadius());
		
//		System.out.println("Neues center f¸r Cubezuerst: " + super.id + center.toString());
	}
	private Float min(Float one, Float two){
		if(one==null&&two==null)return 0.0f;
		if(one==null)return two;
		if(two==null)return one;
		return Math.min(one, two);
	}
	
	private Float max(Float one, Float two){
		if(one==null&&two==null)return 0.0f;
		if(one==null)return two;
		if(two==null)return one;
		return Math.max(one, two);
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public File getSourceTex() {
		return sourceTex;
	}

	@Override
	public Shape clone() {
		return new ObjLoader(new String(getId()), shader, sourceFile, sourceTex, getWorldTransform(), mass);
	}
}
