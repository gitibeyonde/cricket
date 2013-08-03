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
import com.cricket.mmog.cric.util.MoveParams;
import com.criconline.actions.LastMoveAction;
import com.criconline.exceptions.WicketCollisionException;
import com.criconline.exceptions.FielderCollisionException;
import com.cricket.common.message.CommandPlayerShift;
import com.criconline.CricketController;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import com.cricket.mmog.cric.util.KeyMap;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.util.ResourceBundle;
import com.criconline.resources.Bundle;
import com.cricket.mmog.cric.util.MoveUtils;
import com.criconline.anim.AnimationEvent.AEvent;

public class BallView implements Animator, AnimationConstants, KeyListener {
  private static Logger _cat = Logger.getLogger(BallView.class.getName());

  public BallAnimation _ball = null;
  AnimationEvent _anim_event = null;
  AnimationManager _am;
  public Animation _pitch_point = null;
  int _steps = 0;

  /** the component which proced painint */
  protected CricketController _owner = null;
  public int _bowl_state = NEUTRAL;
  public static KeyMap _km;

  //--------------------------
  protected ResourceBundle _bundle;

  public BallView(CricketController owner, PitchSkin ps, AnimationManager am) {
    _owner = owner;
    _am = am;
    _ball = new BallAnimation(am, new Point(420, 800), BallAnimation.BOWLING, ps);
    _pitch_point = ps.getPitchPoint();
    _km = new KeyMap(BALL);
    _bundle = Bundle.getBundle();
  }

  public int getZOrder() {
    return _ball.getZOrder();
  }

  public Animation getAnimation() {
    return _ball;
  }

  public void collision(Animation a) {
    //collision detected. stop the ball
    _cat.warning("Ball collided with " + a);
  }

  public synchronized void animate(AnimationEvent ev, int frame) {
    if (ev._type != BALL) {
      return;
    }
    _cat.finest("BALL VIEW---------" + ev._action);
    if (ev._action.equals("enable_pitch")) {
      _anim_event = ev;
      _bowl_state = SELECT_PITCH_POINT;
      _km.reset();
      _pitch_point.reset();
      _pitch_point._dx=0;
      _pitch_point._dy=0;
      _pitch_point.setPos(new Point(_pitch_point._startPos));
      _pitch_point.setCurrentFrame(3);
      _pitch_point.setValid();
      _owner.repaint(_pitch_point.x(), _pitch_point.y(),
                     _pitch_point.getIconWidth(), _pitch_point.getIconWidth());
      if (_owner.getThisPlayer().isBowl()) {
        _owner.addKeyListener(this);
        _owner.setFocusable(true);
        _owner.requestFocusInWindow();
      }
    }
    else if (ev._action.equals("disable_pitch")) {
      _anim_event = ev;
      _owner.requestFocus();
      _bowl_state = SELECT_SPIN;
      _km.setPosition(_pitch_point.getPos());
      _cat.finest(_km.toString());
    }
    else if (ev._action.equals("remove_pitch")) {
      _anim_event = ev;
      _pitch_point.setInvalid();
      _pitch_point.reset();
      _owner.repaint(_pitch_point.getBounds());
      _bowl_state = NEUTRAL;
      //_owner.setFocusable(false);
      //_owner.removeKeyListener(this);
      _cat.finest(_km.toString());
    }
    else if (ev._action.equals("ball_throw")) {
      _bowl_state = NEUTRAL;
      _anim_event = ev;
      LastMoveAction lma = (LastMoveAction) _anim_event.getAttach();
      MoveParams mp = lma.getMoveParams();
      if (mp.isWideBall()){
        _am.update(null, Gse.WIDE_BALL);
      }
      _cat.finest("LAST move ball params=" + mp);
      _pitch_point._currentFrame = 3 - mp._spin;
      _pitch_point.setPos(new Point(mp.getX(), mp.getY()));
      _cat.finest(mp.toString());
      _owner.repaint(_pitch_point.getBounds());
      _ball.setType(BallAnimation.BOWLING);
      _ball.setPos(new Point(550, 360));
      _ball.startAnimation(mp, _pitch_point._pos.x + _pitch_point._width/2, _pitch_point._pos.y + _pitch_point._height/2, 520,
                           -140);
      _owner.addCommentary(mp.getBallCommentary());
    }
    else if (ev._action.equals("ball_stop")) {
      _bowl_state = NEUTRAL;
      _anim_event = ev;
      _ball.setType(BallAnimation.NONE);
      _ball.setInvalid();
    }
    else if (ev._action.equals("ball_flight")) { // after batsman hit
      _bowl_state = NEUTRAL;
      //_ball.setPos(new Point(500, 130));
      _anim_event = ev;
      if (ev._eq.checkEvent(Gse.FIELDING_LOOKUP) ){
        _ball.setType(BallAnimation.OTHER_THROW);
      }
      else {
        _ball.setType(BallAnimation.THROW);
      }
      LastMoveAction lma = (LastMoveAction) _anim_event.getAttach();
      MoveParams mp = lma.getMoveParams();
      _ball.setFielder(MoveUtils.whichFielderStr(_owner.getGameType(), mp));
      _ball.startAnimation(mp);
      _owner.addCommentary(mp.getShotCommentary());
    }
    else if (ev._action.equals("ball_return")) {
      _bowl_state = NEUTRAL;
      _anim_event = ev;
      LastMoveAction lma = (LastMoveAction) _anim_event.getAttach();
      _cat.finest("LMA=" + lma);
      _anim_event.setNextEvent(lma, Gse.BALL_FLIGHT_OVER, 9999);
      _ball.setPos(new Point(lma.getMoveParams().getX(), lma.getMoveParams().getY()));
      _ball.setType(BallAnimation.TO_WK);
      _ball.startAnimation(475, 55, 475, 55);
      _cat.finest(_ball.toString());
    }
    _steps = 0;
  }

  public int delay() {
    return (int) (_ball._delay * _am._ball_speed_factor);
  }

  public void run() {
    /** Update moving pot chips */
    if (_anim_event == null) {
      return;
    }
    _anim_event._start_delay--;
    if (_anim_event._start_delay > 0) {
      return;
    }

    try {
      Rectangle r1 = _ball.getRealCoords();
      Rectangle r2 = _ball.getShadowCoords();
      try {
        //_cat.finest("Ball View ======" + _steps);
        _ball.update();
        //_cat.finest(_ball);
        _owner.repaint(r1);
        _owner.repaint(r2);
        refresh();
      }
      catch (WicketCollisionException e) {
        _owner.repaint(r1);
        refresh();
        _am.update(null, Gse.BATSMAN_OUT);
        _cat.warning("Wicket gone ");
      }
      catch (FielderCollisionException e) {
        _am.update(null, Gse.FIELDING_START);
        _owner.repaint(r1);
        refresh();
        _cat.info("Fielder Collision Exception ");
      }
      AEvent event = _anim_event.getEvent(_steps);
      //System.out.print("_" + _steps + "_");
      if (event != null) {
        _am.update(event._attach, event._nextEvent);
      }
      _steps++;
    }
    catch (AnimationOverException e) {
      _cat.warning("Animation Over ");
      _ball.setInvalid();
      _anim_event.invokeRemainingEvents(_am);
    }
    catch (Exception e) {
      _cat.warning("Animation Error"+  e);
    }
  }

  public void paint(JComponent c, Graphics g) {
    //_cat.finest("ClientPlayerView: paint start");
    _ball.paintIcon(c, g);
    _pitch_point.paintIcon(c, g, _am.dispX(), _am.dispY());
    //_cat.finest("ClientPlayerView: paint end");
  }

  public void refresh() {
    _owner.repaint(_ball.x(), _ball.y(), _ball.BALL_WIDTH,
                   _ball.BALL_HEIGHT);
    _owner.repaint(_ball._shadow.x(), _ball._shadow.y(), _ball.BALL_WIDTH,
                   _ball.BALL_HEIGHT);

  }

  public AnimationEvent getDefaultAnimation() {
    return null;
  }

  public String getType() {
    return "ball";
  }

  public void shift(int x, int y, int z) {
    if (_am._pm!= null && !_am._pm.getPlayerStatus().getPlayerTypeString().equals("bowler")){
      _cat.finest("Shifting pitch point to " + x + ", " + y);
      _pitch_point.setPos(new Point( x, y));
      _pitch_point._currentFrame = z;
      _owner.repaint(_pitch_point.getBounds());
    }
  }


  public void keyPressed(KeyEvent ke) {
    if (_owner.getThisPlayer().isBowl() && (_bowl_state == SELECT_PITCH_POINT || _bowl_state== SELECT_SPIN))  {
      processKey(ke);
    }
  }

  public void keyReleased(KeyEvent ke) {
  }

  public void keyTyped(KeyEvent ke) {
  }

  public void processKey(KeyEvent ke) {
    int ch = ke.getKeyCode();
    boolean move = false;
    _cat.finest("keyTyped = " + ch + ", kcode=" + ke.getKeyCode() + " bowl_state=" + _bowl_state);
    if (ch == 38) {
      if (_bowl_state == SELECT_PITCH_POINT) {
        // up arrow
        move = _pitch_point.move(0, -2, PITCH_POINT_Y_SPREAD);
      }
      else if (_bowl_state == SELECT_SPIN) {
        _km.addKey(KeyMap.U);
      }
    }
    else if (ch == 40) {
      if (_bowl_state == SELECT_PITCH_POINT) {
        //down arrow
        move = _pitch_point.move(0, 2, PITCH_POINT_Y_SPREAD);
      }
      else if (_bowl_state == SELECT_SPIN) {
        _km.addKey(KeyMap.D);
      }
    }
    else if (ch == 37) {
      if (_bowl_state == SELECT_PITCH_POINT) {
        //left arrow
        move = _pitch_point.move( -2, 0, PITCH_POINT_X_SPREAD);
      }
      else if (_bowl_state == SELECT_SPIN) {
        _km.addKey(KeyMap.L);
        _pitch_point._currentFrame = 3 - _km.getSpin();
        move=true;
      }
    }
    else if (ch == 39) {
      if (_bowl_state == SELECT_PITCH_POINT) {
        // right arrow
        move = _pitch_point.move(2, 0, PITCH_POINT_X_SPREAD);
      }
      else if (_bowl_state == SELECT_SPIN) {
        _km.addKey(KeyMap.R);
        _pitch_point._currentFrame = 3 - _km.getSpin();
        move=true;
      }
    }
    else if (_bowl_state == SELECT_SPIN && ch == 83) {
      _bowl_state = NEUTRAL;
      JButton jb = new JButton();
      jb.setName("bowl");
      ActionEvent ae = new ActionEvent(jb, 1, "");
      _owner._bottomPanel.actionPerformed(ae);
    }
    _owner.repaint(_pitch_point.getBounds());
    if (move && _owner instanceof CricketController &&
        _owner.getThisPlayer().isBowl()) {
      try {
        CricketController cc = (CricketController) _owner;
        cc._eventGen.sendToServer(new CommandPlayerShift(null, cc.getGid(),
            getType(), _pitch_point.x(), _pitch_point.y(), _pitch_point._currentFrame));
      }
      catch (IOException e) {
        // DISCONNECTED FRM SERVER
        JOptionPane.showMessageDialog(_owner, _bundle.getString("server.error"),
                                      _bundle.getString("error"),
                                      JOptionPane.ERROR_MESSAGE);
      }

    }

  }

}
