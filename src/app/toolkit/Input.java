/*******************************************************************************
 * Copyright (c) 2013 Henrik Tramberend, Marc Latoschik.
 * All rights reserved.
 *******************************************************************************/
package app.toolkit;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import vecmath.Vector;
// Select the factory we want to use.
// It is a bit ugly to do it at several points...though 
import static vecmath.vecmathimp.FactoryDefault.vecmath;


/**
 * Represent the input state for the application. Can be queried for key and
 * button status and mouse position.
 */
public final class Input {
  private Set<Integer> keys = new HashSet<Integer>();
  private Set<Integer> buttons = new HashSet<Integer>();
  private Set<Integer> toggled = new HashSet<Integer>();
  private Vector mpos = vecmath.vector(0, 0, 0);
  private int width, height;

  /**
   * Determine if the specified key is currently pressed.
   * 
   * @param k
   *          The code of the key as defined in class <code>KeyEvent<code>.
   * @return Returns true if the key is currently pressed.
   */
  public boolean isKeyDown(int k) {
    return keys.contains(k);
  }

  /**
   * Determine if the specified mouse button is currently pressed.
   * 
   * @param b
   *          The button code of as defined in class <code>MouseEvent<code>.
   * @return Returns true if the key is currently pressed.
   */
  public boolean isButtonDown(int b) {
    return buttons.contains(b);
  }

  /**
   * Determine the toggle state of the specified key.
   * 
   * @param k
   *          The code of the key as defined in class <code>KeyEvent<code>.
   * @return Returns true if the key is currently pressed.
   */
  public boolean isKeyToggled(int k) {
    return toggled.contains(k);
  }

  /**
   * Get the current position of the mouse in pixels. Position (0,0) is at the
   * upper left corner of the window.
   * 
   * @return The current mouse position.
   */
  public Vector getMousePosition() {
    return mpos;
  }

  /**
   * Calculate the normalized mouse position within the window. Position (0, 0)
   * is at the center of the window. Coordinates are from the the interval [-1,
   * 1].
   * 
   * @return The normalized mouse position. Coordinate Z is always zero.
   */
  public Vector getNormalizedMousePosition() {
    float f = Math.max(width, height) / 2.0f;
    float cx = mpos.x() - width / 2.0f;
    float cy = height - mpos.y() - 1 - height / 2.0f;

    return vecmath.vector(cx / f, cy / f, 0);
  }

  void setWindowSize(int w, int h) {
    width = w;
    height = h;
  }

  public void update() {
    if (Keyboard.next()) {
      int k = Keyboard.getEventKey();
      if (Keyboard.getEventKeyState()) {
        // Key pressed.
        keys.add(k);
        if (toggled.contains(k))
          toggled.remove(k);
        else
          toggled.add(k);
      } else {
        // Key released.
        keys.remove(k);
      }
    }
    if (Mouse.next()) {
      int b = Mouse.getEventButton();
      if (Mouse.getEventButtonState()) {
        // Button pressed.
        buttons.add(b);
      } else {
        // Button released.
        buttons.remove(b);
      }
    }
    mpos = vecmath.vector(Mouse.getX(), Mouse.getY(), 0.0f);
  }

}