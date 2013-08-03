package com.criconline.anim;

import java.util.TimerTask;
import com.criconline.actions.LastMoveAction;
import java.util.logging.Logger;
import com.criconline.exceptions.NetworkDelayException;
import javax.swing.JComponent;
import java.awt.Graphics;

public class MovePoller implements Animator, AnimationConstants {
  static Logger _cat = Logger.getLogger(MovePoller.class.getName());
  AnimationManager _am;
  MoveBuffer _mb;
  int _count;
  int _move_type;
  int _poll_count;
  int _hook_count;
  boolean _found = false;
  LastMoveAction _lma = null;
  Gse _hook;
  Animation _poll;
  int _slow_factor=10;

  public MovePoller(AnimationManager am, MoveBuffer mb, int type, Gse hook,
                    int max_poll, int hc) {
    _am = am;
    _mb = mb;
    _hook = hook;
    _move_type = type;
    _poll_count = max_poll;
    assert (hc <= _poll_count):hc +
        " hook count should be less than poll count " + _poll_count;
    _hook_count = hc;
    _poll = new Animation();
  }

  public void animate(AnimationEvent ev, int frame) {
    // stub
  }

  public int getZOrder() {
    return 0;
  }

  public int delay() {
    return (int) (1 * _am._speed_factor);
  }

  public void setSlowFactor(int sf){
    _slow_factor = sf;
  }

  public void run() {
    if (_poll == null) {
      return;
    }
    //_cat.finest("Running poller ------------" + this);
    if (!_found) {
      _lma = _mb.getLastMove(_move_type);
      if (_lma != null) {
        _cat.finest("Last move ---------------------------------" + _lma);
        _found = true;
        _poll = null;
      }
      if ((_count % _slow_factor) == 0) {
        _cat.finest("Slow down----------------------------------" + this);
        _am.slowDown();
        _am.slowDownBall();
      }
      if (_count == _poll_count) {
        _cat.finest("Cancelled----------------------------------" + this);
        _am.update(null, Gse.MOVE_NOT_FOUND);
        _poll = null;
      }
    }
    else if (_hook_count <= _count) {
      _cat.finest("Invoking hook " + _hook);
      _am.update(_lma, _hook);
      _poll = null;
    }
    _count++;
  }

  public void cancel(){
    _poll= null;
  }

  public void paint(JComponent c, Graphics g) {
  }

  public void collision(Animation a) {
    _cat.warning("Ball collided with " + a);
  }

  public Animation getAnimation() {
    return _poll;
  }


  public AnimationEvent getDefaultAnimation() {
    return null;
  }

  public String getType() {
    return "movepoller";
  }

  public void shift(int x, int y, int z) {

  }


  public String toString() {
    return "count=" + _count + ", type=" + _move_type + ", pc=" + _poll_count +
        ", hc=" + _hook_count + ", hook=" + _hook;
  }

}
