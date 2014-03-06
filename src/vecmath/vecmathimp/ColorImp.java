package vecmath.vecmathimp;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import vecmath.Color;
import vecmath.Vector;


/**
 * A simple three component color vector. Color vectors are non-mutable and can
 * be passed around by value.
 * 
 */
/**
 * @author henrik
 *
 */
public final class ColorImp implements Color, Comparable<Color> {

  private final float r;
  private final float g;
  private final float b;

  /**
   * @return the r
   */
  public float getR() {
    return r;
  }
  /**
   * @return the g
   */
  public float getG() {
    return g;
  }/**
   * @return the b
   */
  public float getB() {
    return b;
  }
  
  /**
   * Construct a new color vector and initialize the components.
   * 
   * @param r
   *          The red component.
   * @param g
   *          The green component.
   * @param b
   *          The blue component.
   */
  public ColorImp(float r, float g, float b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  /**
   * Construct a new color vector and initialize the components.
   * 
   * @param v
   *          A three commponent vector.
   */
  public ColorImp(Vector v) {
    r = v.x();
    g = v.y();
    b = v.z();
  }

  /**
   * Test this color for blackness.
   * 
   * @return True if black, else false.
   */
  public boolean isBlack() {
    return getR() == 0.0 && g == 0.0 && b == 0.0;
  }

  /**
   * Calculate the sum of two colors.
   * 
   * @param c
   *          The second color.
   * @return The sum.
   */
  public Color add(ColorImp c) {
    return new ColorImp(getR() + c.getR(), g + c.g, b + c.b);
  }

  /**
   * Calculate the product of this color an a scalar.
   * 
   * @param s
   *          The scalar.
   * @return The product.
   */
  public Color modulate(float s) {
    return new ColorImp(getR() * s, g * s, b * s);
  }

  /**
   * Perform the component wise multiplication of two colors. This is not a dot
   * product!
   * 
   * @param c
   *          The second color.
   * @return The result of the multiplication.
   */
  public Color modulate(ColorImp c) {
    return new ColorImp(getR() * c.getR(), g * c.g, b * c.b);
  }

  /**
   * Clip the color components to the interval [0.0, 1.0].
   * 
   * @return The clipped color.
   */
  public ColorImp clip() {
    ColorImp c = new ColorImp(Math.min(getR(), 1), Math.min(g, 1), Math.min(b, 1));
    return new ColorImp(Math.max(c.getR(), 0), Math.max(c.g, 0), Math.max(c.b, 0));
  }

  /**
   * Convert to float array.
   * 
   * @return
   */
  public float[] asArray() {
    float[] v = { r, g, b };
    return v;
  }

  /* (non-Javadoc)
   * @see cg2.vecmath.Color#asBuffer()
   */
  @Override
  public FloatBuffer asBuffer() {
    FloatBuffer buf = BufferUtils.createFloatBuffer(size());
    buf.put(getR());
    buf.put(g);
    buf.put(b);
    buf.rewind();
    return buf;
  }

  /* (non-Javadoc)
   * @see cg2.vecmath.Color#fillBuffer(java.nio.FloatBuffer)
   */
  @Override
  public void fillBuffer(FloatBuffer buf) {
    buf.put(getR());
    buf.put(g);
    buf.put(b);
  }
  
  /* (non-Javadoc)
   * @see cg2.vecmath.Color#toAwtColor()
   */
  @Override
  public int toAwtColor() {
    ColorImp c = clip();
    return (toInt(1.0f) << 24) | (toInt(c.getR()) << 16) | (toInt(c.g) << 8)
        | toInt(c.b);
  }

  /**
   * Convert a floating point component from the interval [0.0, 1.0] to an
   * integral component in the interval [0, 255]
   * 
   * @param c
   *          The float component.
   * @return The integer component.
   */
  private static int toInt(float c) {
    return Math.round(c * 255.0f);
  }

  /*
   * @see java.lang.Object#toString()
   */
  /* (non-Javadoc)
   * @see cg2.vecmath.Color#toString()
   */
  @Override
  public String toString() {
    return "(" + getR() + ", " + g + ", " + b + ")";
  }

  /* (non-Javadoc)
   * @see cg2.vecmath.Color#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof ColorImp))
      return false;
    final ColorImp c = (ColorImp) o;
    return getR() == c.getR() && g == c.g && b == c.b;
  }

  /* (non-Javadoc)
   * @see cg2.vecmath.Color#compareTo(cg2.vecmath.Color)
   */
  @Override
  public int compareTo(Color o) {
    if (getR() != o.getR())
      return (getR() < o.getR() ? -1 : 1);
    if (g != o.getG())
      return (g < o.getG() ? -1 : 1);
    if (b != o.getB())
      return (b < o.getB() ? -1 : 1);
    return 0;
  }

  /* (non-Javadoc)
   * @see cg2.vecmath.Color#size()
   */
  @Override
  public int size() {
    return getSize();
  }
//@Override
  public static int getSize() {
    return 3;
  }
}
