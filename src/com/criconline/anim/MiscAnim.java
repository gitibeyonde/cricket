package com.criconline.anim;

import com.criconline.Painter;
import java.awt.Point;
import com.criconline.pitch.PitchSkin;
import java.awt.Rectangle;
import com.criconline.PlayerView;
import java.util.logging.Logger;
import javax.swing.JComponent;
import java.awt.Graphics;
import com.criconline.Utils;
import javax.swing.ImageIcon;
import com.criconline.exceptions.AnimationOverException;
import com.criconline.exceptions.CollisionException;
import java.util.Vector;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Color;
import com.criconline.anim.AnimationEvent.AEvent;

public class MiscAnim implements Animator, AnimationConstants {
  private static Logger _cat = Logger.getLogger(MiscAnim.class.getName());

  AnimationEvent _anim_event = null;
  AnimationManager _am;
  PitchSkin _skin;
  Animation _anim;
  int _skip;

  /** the component which proced painint */
  protected JComponent _owner = null;

  public MiscAnim(JComponent owner, PitchSkin ps, AnimationManager am) {
    _owner = owner;
    _am = am;
    _skin = ps;
  }

  public int getZOrder(){
    return _anim == null ? -1 : _anim.getZOrder();
  }

  public Animation getAnimation() {
    return _anim;
  }

  public void collision(Animation a) {
    //collision detected. stop the ball
    throw new IllegalStateException("Misc animation has no collision");
  }

  public synchronized void animate(AnimationEvent ev, int frame) {
    if (ev._type != MISC) {
      return;
    }
    if (ev._action.equals("toss_coin")) {
      _anim_event = ev;
      _anim = _skin.getTossCoin();
      _anim.setValid();
      _anim.startAnimation(new Point(200, 200));
    }
    else if (ev._action.equals("stumps_fall")){
      _anim_event = ev;
      _anim = _skin.getStumpsFall();
      _anim.setValid();
      _anim.startAnimation();
    }
    _skip = 0;
  }

  public int delay() {
    return (int)(_anim._delay * _am._speed_factor);
  }

  public void run() {
    if (_anim_event == null) {
      return;
    }
    /** Update moving pot chips */
    Rectangle r1 = null;
    try {
      r1 = _anim.getRealCoords();
      try {
        //_cat.finest(_anim);
        _anim.update();
        _owner.repaint(r1);
        refresh();
      }
      catch (CollisionException e) {
        _owner.repaint(r1);
        refresh();
        _cat.warning("Collision Exception");
        //_anim_event.invokeNextEvent();
      }
      //_cat.finest(" PLAYER MOVE: " + _anim_event);
      //Thread.currentThread().sleep((int) (_anim._delay * _am._speed_factor));
      AEvent event = _anim_event.getEvent(_skip);
      if (event != null) {
        _am.update(event._attach, event._nextEvent);
      }
      _skip++;
    }
    catch (AnimationOverException e) {
      _owner.repaint(r1);
      refresh();
      _anim_event.invokeRemainingEvents(_am);
      _cat.info("Animation Complete " + _anim_event);
      _anim.setInvalid();
    }
    catch (Exception e) {
      _cat.warning("Animation Error"+  e);
      _anim.setInvalid();
    }
  }

  public void paint(JComponent c, Graphics g) {
    //_cat.finest("MiscAnim: paint start");
    if (_anim == null) {
      return;
    }
    _anim.paintIcon(c, g, _am.dispX(), _am.dispY());
    //_cat.finest("MiscAnim: paint end");
  }

  public void refresh() {
    _owner.repaint(_anim.x(), _anim.y(), _anim.getIconWidth(),
                   _anim.getIconWidth());
  }

  public AnimationEvent getDefaultAnimation() {
    return null;
  }

  public String getType() {
    return "misc";
  }

  public void shift(int x, int y, int z){

  }

}
