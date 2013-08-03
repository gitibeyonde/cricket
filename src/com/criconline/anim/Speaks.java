package com.criconline.anim;

import javax.swing.ImageIcon;
import com.criconline.Utils;
import javax.swing.JComponent;
import com.criconline.Painter;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

import java.util.logging.Logger;
import java.awt.Rectangle;
import com.criconline.pitch.PitchSkin;

public class Speaks implements Painter, Animator, AnimationConstants {
  private static Logger _cat = Logger.getLogger(Speaks.class.getName());

  protected JComponent _owner = null;
  protected Animation bubbles = null;
  /** Bubles orientation constants */
  public final int BUBLE_LEFT = 0;
  public final int BUBLE_RIGHT = 1;
  private int bubleOrientation = -1;
  AnimationManager _am;
  String message;

  public Speaks(JComponent owner, PitchSkin ps, AnimationManager am) {
    bubbles = ps.getSpeakBubbles();
    _owner = owner;
    _am = am;
  }

  public int getZOrder(){
    return bubbles.getZOrder();
  }

  public int delay(){
    return (1000);
  }

  public void animate(AnimationEvent ev, int frame) {
    if (ev._type != SPEAKS) {
      return;
    }
    message = ev._action;
    bubbles.setValid();
    bubbles.startAnimation();

    _cat.finest("SPEAKS=" + message);
  }


  public void run() {
    try {
      Rectangle r1 = bubbles.getRealCoords();
      try {
        bubbles.update();
      }catch (Exception ao){
      }
      _owner.repaint(r1);
      refresh();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    message = null;
  }

  public void refresh() {
    _owner.repaint(bubbles.x(), bubbles.y(), bubbles.getIconWidth(),
                   bubbles.getIconWidth());
  }

  public void setMessage(String mesg) {
    message = mesg;
  }

  public void unsetMessage() {
    message = null;
  }

  public ImageIcon getBubblesIcon() {
    return (bubbles._strip);
  }

  public void paint(JComponent c, Graphics g) {
    if (message != null) {
      bubbles.paintIcon(c, g);
      Graphics gcopy = g.create(bubbles.x(), bubbles.y(), NAME_WIDTH, NAME_HEIGHT);
      gcopy.setColor(Color.BLUE);
      gcopy.setFont(Utils.bubbleFont);
      gcopy.drawString(message, 10, 23);
      gcopy.dispose();
    }
  }

  public String getType() {
    return "speaks";
  }

  public void shift(int x, int y, int z){
  }

  public void collision(Animation a) {
    ;
  }

  public Animation getAnimation() {
    return bubbles;
  }


  public AnimationEvent getDefaultAnimation() {
    return null;
  }

}
