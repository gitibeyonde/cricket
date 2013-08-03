package com.criconline.anim;

import java.util.Vector;
import java.awt.Component;
import java.awt.Graphics;
import com.criconline.exceptions.CollisionException;
import com.criconline.exceptions.AnimationOverException;
import java.util.logging.Logger;

public class AnimationSequence {
  private static Logger _cat = Logger.getLogger(AnimationSequence.class.getName());
  Vector _animations;
  String _name;
  int _current=-1;

  public AnimationSequence() {
    _animations = new Vector();
  }

  public void addAnimation(Animation a) {
    _animations.add(a);
  }

  public void startAnimation() {
    _current = 0;
  }

  public void update() throws AnimationOverException, CollisionException {
    try {
      Animation curr = (Animation) _animations.get(_current);
      curr.incrCurrentFrame();
      curr._pos.move(curr._startPos.x +
                     (curr._endPos.x - curr._startPos.x) * curr.getCurrentFrame() /
                     (curr._count),
                     curr._startPos.y +
                     (curr._endPos.y - curr._startPos.y) * curr.getCurrentFrame() /
                     (curr._count));
      _cat.finest("currentFrame=" + curr._currentFrame + ", cnt=" + curr._count + ", cur x=" + curr._pos.x + ", cur y=" +
                 curr._pos.y + ", plr=" + curr._strip);
    }
    catch (AnimationOverException e) {
      if (_current == _animations.size()) {
        _cat.warning("animation ends"+  e);
        throw new AnimationOverException();
      }
      else {
        _current++;
      }
    }
  }

  public void forward() throws AnimationOverException, CollisionException {
    try {
      Animation curr = (Animation) _animations.get(_current);
      curr.incrCurrentFrame();
      curr._pos.move(curr._startPos.x +
                     (curr._endPos.x - curr._startPos.x) * curr.getCurrentFrame() /
                     (curr._count),
                     curr._startPos.y +
                     (curr._endPos.y - curr._startPos.y) * curr.getCurrentFrame() /
                     (curr._count));
      // _cat.finest("currentFrame=" + _currentFrame + ", cnt=" + _count + ", cur x=" + _pos.x + ", cur y=" +
      //          _pos.y + ", plr=" + _strip);
    }
    catch (AnimationOverException e) {
      if (_current == _animations.size()) {
        _cat.warning("animation ends"+  e);
        throw new AnimationOverException();
      }
      else {
        _current++;
      }

    }
  }

  public int getCurrentFrame() {
    Animation curr = (Animation) _animations.get(_current);
    return curr._currentFrame;
  }

  public void paintIcon(Component c, Graphics g) {
    if (_animations.size()==0)return;
    Animation curr = (Animation) _animations.get(_current);
    curr.paintIcon(c, g);
  }


  public int x() {
    Animation curr = (Animation) _animations.get(_current);
    return curr._pos.x;
  }

  public int y() {
    Animation curr = (Animation) _animations.get(_current);
    return curr._pos.y;
  }

  public int getIconWidth() {
    Animation curr = (Animation) _animations.get(_current);
    return curr._height;
  }

  public int getIconHeight() {
    Animation curr = (Animation) _animations.get(_current);
    return curr._width;
  }

  public int delay(){
    Animation curr = (Animation) _animations.get(_current);
    return curr._delay;
  }

  public void reset() {
    _current=0;
    for (int i=0;i<_animations.size();i++){
      ((Animation)_animations.get(i)).reset();
    }
  }

  public String toString(){
    Animation curr = (Animation) _animations.get(_current);
    return curr.toString();
  }
}
