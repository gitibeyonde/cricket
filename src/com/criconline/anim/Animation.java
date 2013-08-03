package com.criconline.anim;

import java.awt.Point;
import javax.swing.ImageIcon;
import java.awt.Rectangle;
import com.criconline.PlayerView;
import java.util.logging.Logger;
import java.awt.Component;
import java.awt.Graphics;
import com.criconline.exceptions.AnimationOverException;
import java.util.Enumeration;
import java.util.Vector;
import com.cricket.mmog.cric.util.Coordinate;
import com.criconline.exceptions.CollisionException;
import com.criconline.pitch.ImageStrip;
import com.criconline.pitch.PitchSkin;
import com.criconline.ClientConfig;

public class Animation extends Anime {
  private static Logger _cat = Logger.getLogger(Animation.class.getName());

  /** Start and end */
  protected Point _pos = null;
  protected Point _orig_pos = null;
  protected Point _startPos = null;
  protected Point _endPos = null;
  protected int _deltaX, _deltaY;

  public int _hook_count;
  public boolean _invalid = false;
  public int _delay;
  public int _init_delay;
  public int _dx, _dy;

  public Animation() {}

  public Animation(Animation anim) {
    super(anim._strip);
    _pos = new Point(anim._pos);
    _orig_pos = new Point(anim._pos);
    _startPos = new Point(anim._startPos);
    _endPos = new Point(anim._endPos);
    _deltaX = anim._deltaX;
    _deltaY = anim._deltaY;
    _hook_count = anim._hook_count;
    _invalid = anim._invalid;
    _delay = anim._delay;
    _init_delay = anim._init_delay;
    _zOrder = anim._zOrder;
  }

  public Animation(ImageStrip is) {
    super(is._strip, is._width, is._height, is._frames);
    _pos = new Point(is._start);
    _orig_pos = new Point(is._start);
    _startPos = new Point(is._start);
    _endPos = new Point(is._end);
    _deltaX = is._deltaX;
    _deltaY = is._deltaY;

    setHookCount(is._hook);
    _type = is._type;
    _delay = is._delay;
    _init_delay = is._delay;
    _zOrder = is._zorder;
  }


  public Animation(ImageIcon img, Point spos) {
    super(img);
    _pos = new Point(spos);
    _orig_pos = new Point(spos);
    _startPos = new Point(spos);
    _endPos = new Point(spos);
  }

  public Animation(String img, Point spos, int wd, int ht, int cnt) {
    super(img, wd, ht, cnt);
    _pos = new Point(spos);
    _orig_pos = new Point(spos);
    _startPos = new Point(spos);
    _endPos = new Point(spos);
  }

  public Animation(ImageIcon img, Point spos, int wd, int ht, int cnt, int type) {
    super(img, wd, ht, cnt);
    _pos = new Point(spos);
    _orig_pos = new Point(spos);
    _startPos = new Point(spos);
    _endPos = new Point(spos);
    _type = type;
  }

  public Animation(ImageIcon img, Point spos, int wd, int ht, int cnt) {
    super(img, wd, ht, cnt);
    _pos = new Point(spos);
    _orig_pos = new Point(spos);
    _startPos = new Point(spos);
    _endPos = new Point(spos);
  }

  public Animation(String img, Point spos) {
    super(img);
    _pos = new Point(spos);
    _orig_pos = new Point(spos);
    _startPos = new Point(spos);
    _endPos = new Point(spos);
  }

  public void setHookCount(int i) {
    _hook_count = i;
  }

  public void setInvalid() {
    _invalid = true;
  }

  public void setValid() {
    _invalid = false;
  }

  public int getHookCount() {
    return _hook_count;
  }

  public int x() {
    return _pos.x;
  }

  public int y() {
    return _pos.y;
  }

  public int deltaX() {
    return _deltaX;
  }

  public int deltaY() {
    return _deltaY;
  }

  public Point getPos() {
    return _pos;
  }

  public boolean move(int dx, int dy, int limit) {
    if (_dx > limit || _dx < -limit) {
      dx=0;
      return false;
    }
    if (_dy > limit || _dy < -limit) {
      dy=0;
      return false;
    }
    _dx += dx;
    _dy += dy;
    _pos.x += dx;
    _pos.y += dy;
    _startPos.x += dx;
    _startPos.y += dy;
    _endPos.x += dx;
    _endPos.y += dy;
    return true;
  }

  public void setOrigPos(Point pos) {
    _orig_pos = new Point(pos);
    _pos = new Point(pos);
    _startPos = new Point(pos);
    _endPos = new Point(pos);
  }

  public void setPos(Point pos) {
    _pos = new Point(pos);
    _startPos = new Point(pos);
    _endPos = new Point(pos);
  }

  public void setStartPos(Point pos) {
    _startPos = new Point(pos);
  }

  public void setEndPos(Point pos) {
    _endPos = new Point(pos);
  }


  public void startAnimation(Point end) {
    setValid();
    _endPos = new Point(end);
  }

  public void startAnimation() {
    setValid();
  }

  public void reset() {
    _currentFrame = 0;
    _pos = new Point(_orig_pos);
    _startPos = new Point(_orig_pos);
    _endPos = new Point(_orig_pos);
  }

  public void update() throws AnimationOverException, CollisionException {
    //try {
    incrCurrentFrame();
    _pos.move(_startPos.x +
              (_endPos.x - _startPos.x) * getCurrentFrame() / (_count),
              _startPos.y +
              (_endPos.y - _startPos.y) * getCurrentFrame() / (_count));
    // _cat.finest("currentFrame=" + _currentFrame + ", cnt=" + _count + ", cur x=" + _pos.x + ", cur y=" +
    //         _pos.y + ", plr=" + _strip);
    /**}catch (Exception e){
      _cat.warning("animation ends"+  e);
         }**/
  }

  public void forward() throws AnimationOverException, CollisionException {
    //try {
    incrCurrentFrame();
    _pos.move(_startPos.x +
              (_endPos.x - _startPos.x) * getCurrentFrame() / (_count),
              _startPos.y +
              (_endPos.y - _startPos.y) * getCurrentFrame() / (_count));
    // _cat.finest("currentFrame=" + _currentFrame + ", cnt=" + _count + ", cur x=" + _pos.x + ", cur y=" +
    //          _pos.y + ", plr=" + _strip);
    /**}catch (Exception e){
      _cat.warning("animation ends"+  e);
           }**/
  }

  public void reverse() throws AnimationOverException, CollisionException {
    //try {
    decrCurrentFrame();
    _pos.move(_startPos.x +
              (_endPos.x - _startPos.x) * getCurrentFrame() / (_count),
              _startPos.y +
              (_endPos.y - _startPos.y) * getCurrentFrame() / (_count));
    //_cat.finest("currentFrame=" + _currentFrame + ", cnt=" + _count + ", cur x=" + _pos.x + ", cur y=" +
    //       _pos.y + ", plr=" + _strip);
    /**}catch (Exception e){
      _cat.warning("animation ends"+  e);
             }**/
  }


  public void paintIcon(Component c, Graphics g) {
    if (_invalid) {
      return;
    }
    if (_strip != null) {
      if (!g.getClipBounds().intersects(_pos.x, _pos.y, _width, _height)) {
        return;
      }
      Graphics gcopy = g.create(_pos.x, _pos.y, _width, _height);
      _strip.paintIcon(c, gcopy, -_width * _currentFrame, 0);
      gcopy.dispose();
    }
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    if (_invalid) {
      return;
    }
    if (_strip != null) {
      if (g.getClipBounds().intersects(_pos.x + x, _pos.y + y, _width, _height)) {
        Graphics gcopy = g.create(_pos.x + x, _pos.y + y, _width, _height);
        _strip.paintIcon(c, gcopy, -_width * _currentFrame, 0);
        gcopy.dispose();
      }
    }
  }

  public Rectangle getBounds() {
    return new Rectangle(_pos.x, _pos.y, _width, _height);
  }

  public Rectangle getFieldBounds() {
    return new Rectangle(_pos.x, _pos.y + _height/2, _width, _height);
  }

  public Rectangle getRealCoords() {
    return new Rectangle(_pos.x, _pos.y, _width, _height);
  }

  public int distance(Animation a) {
    Coordinate c1 = new Coordinate(a.x(), a.y(), 0);
    int dist = (int) c1.distance(new Coordinate(x(), y(), 0));
    return dist;
  }

  public void collisionWithBall() {
    _cat.finest("Ball collided " + this);
  }

  public Rectangle getNameBounds() {
    return new Rectangle(_pos.x + _width, _pos.y + _height - NAME_HEIGHT,
                         NAME_WIDTH, NAME_HEIGHT);
  }

  public Rectangle getWorthBounds() {
    return new Rectangle(750, 20, 130, 30);
  }


  public String toString() {
    return "Inv=" + _invalid + ",Delay=" + _delay + ", Cnt=" + _count +
        ",Pos=[" + _pos.x + "," + _pos.y + "]," + super.toString();
  }

  public static void main(String args[]) throws Exception {

    PitchSkin ps = new PitchSkin();
    Animation an = ps.getPitchPoint();

    _cat.finest(an.toString());

    an.reverse();
    _cat.finest(an.getCurrentFrame() + "");
    an.reverse();
    _cat.finest(an.getCurrentFrame() + "");
    an.reverse();
    _cat.finest(an.getCurrentFrame() + "");
    an.reverse();
    _cat.finest(an.getCurrentFrame() + "");
    an.reverse();
    _cat.finest(an.getCurrentFrame() + "");
    an.reverse();
    _cat.finest(an.getCurrentFrame() + "");
    an.reverse();
    _cat.finest(an.getCurrentFrame() + "");
    an.reverse();
    _cat.finest(an.getCurrentFrame() + "");
    an.reverse();
    _cat.finest(an.getCurrentFrame() + "");


    _cat.finest("-----------");


    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");
    an.forward();
    _cat.finest(an.getCurrentFrame() + "");


  }

}
