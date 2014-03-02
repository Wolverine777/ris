/*******************************************************************************
 * Copyright (c) 2013 Henrik Tramberend, Marc Latoschik.
 * All rights reserved.
 *******************************************************************************/
package vecmath.vecmathimp;

import vecmath.Color;
import vecmath.Factory;
import vecmath.Matrix;
import vecmath.Vector;

//Our implementation of the Factory interface. For deployment this source 
//file is exluded from the package and the compiled class file is 
//shipped in a JAR. 
public class FactoryDefault implements Factory {

  //Bind an instance to a class variable for easy 
  //access via static import.
  public static final Factory vecmath = new FactoryDefault();

  @Override
  public Vector vector(float nx, float ny, float nz) {
    return new VectorImp(nx,ny,nz);
  }
  @Override 
  public Vector xAxis() {
    return new VectorImp(1, 0, 0);
  }
  @Override
  public Vector yAxis() {
    return new VectorImp(0, 1, 0);
  }
  @Override
  public Vector zAxis() {
    return new VectorImp(0, 0, 1);
  }
  @Override
  public int vectorSize() {
    return VectorImp.getSize();
  }
  
  @Override
  public Matrix identityMatrix() {
    return MatrixImp.identity;
  }

  @Override
  public Matrix translationMatrix(Vector t) {
    return MatrixImp.translate(t);
  }
  
  @Override
  public Matrix matrix(float m00, float m01, float m02, float m03, float m10,
      float m11, float m12, float m13, float m20, float m21, float m22,
      float m23, float m30, float m31, float m32, float m33){
    return new MatrixImp(m00, m01, m02, m03, m10,
      m11, m12, m13, m20, m21, m22,
      m23, m30, m31, m32, m33);
  }
 
  
  @Override
  public Matrix matrix(float[] elements) {
    return new MatrixImp(elements);
  }
  @Override
  public Matrix matrix(Vector b0, Vector b1, Vector b2) {
   return new MatrixImp(b0, b1, b2);
  }
  
  @Override
  public Matrix translationMatrix(float x, float y, float z) {
    return MatrixImp.translate(x,y,z);
  }
  @Override
  public Matrix rotationMatrix(Vector axis, float angle) {
    return MatrixImp.rotate(axis,angle);
  }
  @Override
  public Matrix rotationMatrix(float ax, float ay, float az, float angle) {
    return MatrixImp.rotate(ax,ay,az,angle);
  }
  @Override
  public Matrix scaleMatrix(Vector s) {
    return MatrixImp.scale(s);
}
  @Override
  public Matrix scaleMatrix(float x, float y, float z) {
    return MatrixImp.scale(x,y,z);
  }
  @Override
  public Matrix lookatMatrix(Vector eye, Vector center, Vector up) {
    return MatrixImp.lookat(eye, center, up);
  }
  @Override
  public Matrix frustumMatrix(float left, float right, float bottom, float top,
      float zNear, float zFar) {
    return MatrixImp.frustum(left, right, bottom, top, zNear, zFar);
  }
  @Override
  public Matrix perspectiveMatrix(float fovy, float aspect, float zNear, float zFar) {
    return MatrixImp.perspective(fovy, aspect, zNear, zFar);
    }
  @Override
  public Color color(float r, float g, float b){
    return new ColorImp(r, g, b);
  }
  @Override
  public int colorSize() {
    return ColorImp.getSize();
  }
}

