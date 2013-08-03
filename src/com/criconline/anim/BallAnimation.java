package com.criconline.anim;

import com.cricket.mmog.cric.util.Trajectory;
import com.cricket.mmog.cric.util.Coordinate;
import java.awt.Point;
import javax.swing.JComponent;
import java.awt.Rectangle;
import java.awt.Graphics;
import com.criconline.Utils;
import javax.swing.ImageIcon;
import java.util.logging.Logger;
import com.criconline.exceptions.AnimationOverException;
import com.cricket.mmog.cric.util.MoveParams;
import java.util.Vector;
import com.criconline.exceptions.CollisionException;
import java.util.Enumeration;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import com.cricket.mmog.cric.util.MoveUtils;
import com.criconline.pitch.PitchSkin;
import com.criconline.exceptions.FielderCollisionException;
import com.criconline.exceptions.WicketCollisionException;
import com.criconline.FielderPlayerView;
import com.criconline.BowlerPlayerView;
import com.criconline.PlayerView;

public class BallAnimation extends Animation {
  private static Logger _cat = Logger.getLogger(BallAnimation.class.getName());

  protected int _type = 2;
  protected String _fielder;
  protected int _ht;
  public boolean _sequential;
  Coordinate[][] _path = null;
  AnimationManager _am;
  Animation _shadow;

  public static final int BOWLING = 1;
  public static final int OTHER_THROW = 2;
  public static final int THROW = 3;
  public static final int TO_WK = 4;
  public static final int NONE = 99;

  public static final int CYCLE_COUNT = 100;
  public static final int BALL_WIDTH = 10;
  public static final int BALL_HEIGHT = 10;

  public static final int PP_DISP_X = 15;
  public static final int PP_DISP_Y = 40;

  public BallAnimation(AnimationManager am, Point spos, int type, PitchSkin ps) {
    super("strips/ball.png", spos, (int) BALL_WIDTH, (int) BALL_HEIGHT, 1);
    _type = type;
    _delay = 2;
    _am = am;
    _shadow = ps.getBallShadow();
  }

  public void startAnimation(MoveParams mv, int pitch_x, int pitch_y, int end_x,
                             int end_y) {
    if (Math.abs(_pos.x) > 1000 || Math.abs(_pos.y) > 1000) {
      throw new IllegalStateException("The coordinates are invalid " + _pos);
    }
    _pos.y += 100;
    _startPos.move(_pos.x, _pos.y);
    _endPos.move(end_x, end_y);
    _shadow.setPos(new Point(_pos.x, _pos.y));
    Trajectory t = new Trajectory(new Coordinate(_pos.x, _pos.y, 0),
                                  new
                                  Coordinate(pitch_x - PP_DISP_X, pitch_y - PP_DISP_Y,
                                             0));
    t.setBoundary(new Coordinate( -RANGE_X / 2, -RANGE_Y / 2, 0),
                  new
                  Coordinate(SCREEN_WIDTH + RANGE_X / 2,
                             SCREEN_HEIGHT + RANGE_Y / 2, 0));
    _cat.finest("SPIN=" + mv._spin);

    t.setBowl(mv);
    t.setCameraAngle(45);
    _path = t.path();
    _cat.finest("BALL: startMove  frames=" + _path[0].length);
    _currentFrame = _path[0].length - 25;
    _invalid = false;
  }

  public void startAnimation(int pitch_x, int pitch_y, int end_x, int end_y) {
    if (Math.abs(_pos.x) > 1000 || Math.abs(_pos.y) > 1000) {
      throw new IllegalStateException("The coordinates are invalid " + _pos);
    }
    _cat.finest("BALL: startMove  startpos=" + _pos);
    _startPos.move(_pos.x, _pos.y);
    _endPos.move(end_x, end_y);
    _shadow.setPos(new Point(_pos.x, _pos.y));
    Trajectory t = new Trajectory(new Coordinate(_pos.x, _pos.y, 0),
                                  new
                                  Coordinate(pitch_x - PP_DISP_X, pitch_y - PP_DISP_Y,
                                             0));
    t.setBoundary(new Coordinate( -RANGE_X / 2, -RANGE_Y / 2, 0),
                  new
                  Coordinate(SCREEN_WIDTH + RANGE_X / 2,
                             SCREEN_HEIGHT + RANGE_Y / 2, 0));

    //t.setHit(mv);
    t.setCameraAngle(45);
    _cat.finest("BALL: startMove  path start=");
    _path = t.path();
    _cat.finest("BALL: startMove  frames=" + _path[0].length);
    _currentFrame = _path[0].length;
    _invalid = false;
  }

  public void startAnimation(MoveParams mv) {
    _startPos.move(_pos.x, _pos.y);
    _shadow.setPos(new Point(_pos.x, _pos.y));
    int pitch_distance = mv.getBallHitDistance();
    _cat.info("BALL:  dir=" + mv._direction + " sin=" + Math.sin(mv._direction) +
              " cos=" + Math.cos(mv._direction) + ", pitch dist=" +
              pitch_distance);
    int pitch_x = (int) (Math.sin(mv._direction) * pitch_distance) +
                  _startPos.x;
    int pitch_y = (int) (Math.cos(mv._direction) * pitch_distance) +
                  _startPos.y;
    _cat.info("BALL:  x=" + pitch_x + ", y=" + pitch_y + ", " + _startPos);
    Trajectory t = new Trajectory(new Coordinate(_startPos.x, _startPos.y, 0),
                                  new
                                  Coordinate(pitch_x - PP_DISP_X, pitch_y - PP_DISP_Y,
                                             0));
    t.setBoundary(new Coordinate( -RANGE_X / 2, -RANGE_Y / 2, 0),
                  new
                  Coordinate(SCREEN_WIDTH + RANGE_X / 2,
                             SCREEN_HEIGHT + RANGE_Y / 2, 0));

    t.setCameraAngle(45);
    _path = t.path();
    _endPos.move((int) _path[0][_path[0].length - 1]._x,
                 (int) _path[0][_path[0].length - 1]._y);
    _currentFrame = _path[0].length;
    _cat.finest("BALL: startMove  frames=" + _path[0].length);
    _invalid = false;
  }

  public void update() throws AnimationOverException, CollisionException {
    if (_invalid) {
      return;
    }
    if (_currentFrame == 0) {
      throw new AnimationOverException("pos=" + _pos);
    }
    if (_currentFrame > 0) {
      _pos.move((int) _path[0][_path[0].length - _currentFrame]._x,
                (int) _path[0][_path[0].length - _currentFrame]._y);

      _shadow.setPos(new Point((int) _path[1][_path[1].length -
                               _currentFrame]._x,
                               (int) _path[1][_path[1].length -
                               _currentFrame]._y));

      // _cat.finest("Ball pos=" + _pos + ", Shadoe pos=" + _shadow.getPos());

      _ht = (int) (_path[0][_path[0].length - _currentFrame]._z / 20.00);
      _ht = _ht > 0 ? _ht : 0;
      _ht = _ht > 34 ? 34 : _ht;
      //_cat.finest(_invalid + ", ht=" + _ht + ", " + _pos.x + ", " + _pos.y + ", " + _type + ", " + _endPos);
      if (--_currentFrame <= 0) {
        _pos.move(_endPos.x, _endPos.y);
        _invalid = true;
        throw new AnimationOverException("pos=" + _pos);
      }
    }
    if (_type == BOWLING) {
      if (_pos.y < -DEFAULT_POS_Y) {
        _currentFrame = 0;
        _invalid = true;
        throw new AnimationOverException("pos=" + _pos);
      }
    }
    else if (_type == OTHER_THROW) {
      if (detectCollision()) {
        throw new FielderCollisionException();
      }
      _am.moveCamera(_pos.x, _pos.y);
    }

    else if (_type == THROW) {
      _am.moveCamera(_pos.x, _pos.y);
    }
    else if (_type == TO_WK) {
      _am.moveCamera(_pos.x, _pos.y);
      if (_pos.y < _endPos.y) {
        throw new AnimationOverException("pos=" + _pos);
      }
    }
    /**else {
      if (hitWicket()) {
        _invalid = true;
        throw new WicketCollisionException();
      }
         }**/
  }

  public boolean detectCollision() throws CollisionException {
    if (!_am._detect_collision) {
      return false;
    }
    Animator[] fielders = _am.getFielders();
    for (int i = 0; i < fielders.length; i++) {
      if (fielders[i] instanceof FielderPlayerView ||
          fielders[i] instanceof BowlerPlayerView) {
        if (fielders[i] == null || !fielders[i].getType().equals(_fielder)) {
          continue;
        }
        //_cat.finest(fielders[i].getType() + "=" + _fielder);
        Rectangle r = ((PlayerView) fielders[i]).getBounds();
        if (r.contains(_shadow.getBounds())) {
          _cat.finest("Collision detected=" + fielders[i] + ", this=" + this);
          fielders[i].collision(this);
          _am._detect_collision = false;
          return true;
        }
      }
    }
    return false;
  }

  public boolean hitWicket() throws CollisionException {
    PitchSkin ps = new PitchSkin();
    Animation wicket = ps.getStumps(0);
    Rectangle r = wicket.getRealCoords();
    return r.contains(getRealCoords());
  }

  public Rectangle getRealCoords() {
    return new Rectangle(_pos.x + _am.dispX(), _pos.y + _am.dispY(),
                         (int) BALL_WIDTH, (int) BALL_HEIGHT);
  }

  public Rectangle getShadowCoords() {
    return new Rectangle(_shadow.x() + _am.dispX(), _shadow.y() + _am.dispY(),
                         (int) BALL_WIDTH, (int) BALL_HEIGHT);
  }

  public void setType(int type) {
    _type = type;
  }

  public void setFielder(String fld) {
    _fielder = new String(fld);
  }

  public void paintIcon(JComponent c, Graphics g) {
    if (_invalid) {
      return;
    }
    _shadow.paintIcon(c, g, _am.dispX(), _am.dispY());
    //_cat.finest("Shadow " + _shadow);

    if (_strip != null) {
      //_cat.finest("Pos " + _pos);
      if (g.getClipBounds().intersects(_pos.x + _am.dispX(), _pos.y + _am.dispY(),
                                       BALL_WIDTH, BALL_HEIGHT)) {
        Graphics gcopy = g.create(_pos.x + _am.dispX(), _pos.y + _am.dispY(),
                                  (int) BALL_WIDTH, (int) BALL_HEIGHT);
        _strip.paintIcon(c, gcopy, (_ht - 34) * BALL_WIDTH, 0);
        gcopy.dispose();
      }
      //_cat.finest("Ball " + _strip + "," + _pos);
    }
  }

}
