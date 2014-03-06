/*******************************************************************************
 * Copyright (c) 2013 Henrik Tramberend, Marc Latoschik.
 * All rights reserved.
 *******************************************************************************/
package app.vecmathimp;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import app.vecmath.Vector;

/**
 * Simple 3 component vector class. Vectors are non mutable and can be passed
 * around by value.
 * 
 * @author henrik, marc
 */

public final class VectorImp implements Comparable<Vector>, Vector {
  
  public final float x, y, z;
  
    
  /**
   * Construct a new vector and initialize the components.
   * 
   * @param x
   *          The X component.
   * @param y
   *          The Y component.
   * @param z
   *          The Z component.
   */
  public VectorImp(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Simple getters and setters
   * @author marc
   *
   */
  public float x(){return x;}
  public float y(){return y;}
  public float z(){return z;}
 
  /**
   * Component-wise addition of two vectors.
   * 
   * @param v
   *          The second vector.
   * @return The sum.
   */
  @Override
  public Vector add(Vector v) {
    return new VectorImp(x + v.x(), y + v.y(), z + v.z());
  }

  /**
   * Subtract a vector from this vector.
   * 
   * @param v
   *          The second vector.
   * @return The difference.
   */
  @Override
  public Vector sub(Vector v) {
    return new VectorImp(x - v.x(), y - v.y(), z - v.z());
  }

  /**
   * Multiply this vector by a scalar.
   * 
   * @param s
   *          The scalar.
   * @return The scaled vector.
   */
  @Override
  public Vector mult(float s) {
    return new VectorImp(x * s, y * s, z * s);
  }

  /**
   * Componentwise multiplication of two vectors. This is not the dot product!
   * 
   * @param v
   *          The second vector.
   * @return The product.
   */
  @Override
  public Vector mult(Vector v) {
    return new VectorImp(x * v.x(), y * v.y(), z * v.z());
  }

  /* (non-Javadoc)
   * @see cg2.vecmath.Vector#length()
   */
  @Override
  public float length() {
    return (float) Math.sqrt(x * x + y * y + z * z);
  }

  /**
   * Normalize this vector.
   * 
   * @return The normalized vector.
   */
  @Override
  public Vector normalize() {
    return mult(1.0f / length());
  }

  /**
   * Calculate the dot product of two vectors.
   * 
   * @param v
   *          The second vector.
   * @return The dot product.
   */
  @Override
  public float dot(Vector v) {
    return x * v.x() + y * v.y() + z * v.z();
  }

  /**
   * Calculate the cross product of two vectors.
   * 
   * @param v
   *          The second vector.
   * @return The cross product.
   */
  @Override
  public Vector cross(Vector v) {
    return new VectorImp(y * v.z() - z * v.y(), -x * v.z() + z * v.x(), x * v.y() - y * v.x());
  }

  /* (non-Javadoc)
   * @see cg2.vecmath.Vector#asArray()
   */
  @Override
  public float[] asArray() {
    float[] v = { x, y, z };
    return v;
  }

  /* (non-Javadoc)
   * @see cg2.vecmath.Vector#asBuffer()
   */
  @Override
  public FloatBuffer asBuffer() {
    FloatBuffer b = BufferUtils.createFloatBuffer(size());
    b.put(x);
    b.put(y);
    b.put(z);
    b.rewind();
    return b;
  }
  
  /* (non-Javadoc)
   * @see cg2.vecmath.Vector#fillBuffer(java.nio.FloatBuffer)
   */
  @Override
  public void fillBuffer(FloatBuffer buf) {
    buf.put(x);
    buf.put(y);
    buf.put(z);
  }


  /*
   * @see java.lang.Object#toString()
   */
  /* (non-Javadoc)
   * @see cg2.vecmath.Vector#toString()
   */
  @Override
  public String toString() {
    return "(" + x + ", " + y + ", " + z + ")";
  }

  /* (non-Javadoc)
   * @see cg2.vecmath.Vector#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Vector))
      return false;
    final Vector v = (Vector) o;
    return x == v.x() && y == v.y() && z == v.z();
  }

  @Override
  public int compareTo(Vector o) {
    if (x != o.x())
      return (x < o.x() ? -1 : 1);
    if (y != o.y())
      return (y < o.y() ? -1 : 1);
    if (z != o.z())
      return (z < o.z() ? -1 : 1);
    return 0;
  }

  @Override
  public int size(){
    return getSize();
  }
  /* (non-Javadoc)
   * @see cg2.vecmath.Vector#size()
   */
  //@Override
  public static int getSize() {
    return 3;
  }
}