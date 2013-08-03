package com.criconline.anim;

import javax.swing.ImageIcon;
import java.awt.Rectangle;
import java.awt.Point;
import javax.swing.JComponent;
import java.awt.Graphics;
import com.cricket.mmog.cric.util.Trajectory;
import com.cricket.mmog.cric.util.Coordinate;
import java.util.logging.Logger;
import com.criconline.Painter;
import com.criconline.Utils;
import com.criconline.exceptions.AnimationOverException;

public class Animate extends Animation implements Runnable {
  static Logger _cat = Logger.getLogger(Animate.class.getName());
  public int _seq;

  public Animate(Animation anim) {
    super(anim);
  }


  public void run() {
    boolean flag = false;
    /** Update moving pot chips */
    try {
      while (true) {
        //Rectangle r1 = getRealCoords();
        update();
        //_owner.repaint(r1);
        //refresh();
        //_cat.finest("PLAYER MOVE: " + r1);
        Thread.currentThread().sleep(50);
      } // while moving chips
    }
    catch (AnimationOverException e) {
      _cat.finest("Animation Complete");
    }
    catch (Exception e) {
      _cat.warning("Animation Complete"+  e);
    }
  }


  public Rectangle getRealCoords() {
    return new Rectangle(_pos.x, _pos.y, _width, _height);
  }


}
