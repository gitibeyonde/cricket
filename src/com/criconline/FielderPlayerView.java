package com.criconline;

import java.awt.*;
import javax.swing.*;
import java.util.logging.Logger;
import com.criconline.models.*;
import com.cricket.mmog.PlayerStatus;
import com.criconline.pitch.PitchSkin;
import com.criconline.anim.Anime;
import com.criconline.anim.AnimationInterface;
import com.criconline.anim.Animation;
import com.criconline.anim.Animate;
import com.criconline.anim.Animator;
import com.criconline.anim.AnimationEvent;
import com.criconline.anim.AnimationManager;
import com.criconline.exceptions.AnimationOverException;
import com.criconline.anim.Gse;
import com.criconline.anim.AnimationConstants;
import com.criconline.exceptions.CollisionException;
import com.criconline.anim.AnimationEvent.AEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import com.cricket.common.message.CommandPlayerShift;
import java.io.IOException;
import java.awt.event.ActionEvent;
import com.criconline.anim.BallView;
import java.util.TimerTask;
import java.util.Timer;

public class FielderPlayerView extends PlayerView implements Animator,
    KeyListener {
  static Logger _cat = Logger.getLogger(FielderPlayerView.class.getName());

  protected Animation _curr_player = null;
  public AnimationEvent _anim_event = null;
  private int _steps = 0;
  static int _count;

  boolean _focus;
  int _move_state = DT;

  public FielderPlayerView(PitchSkin skin, ClientPlayerModel model,
                           CricketController owner, AnimationManager am, int ts,
                           int num) {
    super(skin, model, owner, am, ts, num);
    _am.addAnimator(this, model.getPlayerStatus());
    _anim_event = getDefaultAnimation();
    _count++;
  }

  public int getZOrder() {
    return _curr_player == null ? -1 : _curr_player.getZOrder();
  }

  public Animation getAnimation() {
    return _player;
  }

  public void collision(Animation a) {
    //no impl
  }

  public synchronized void animate(AnimationEvent ev, int frame) {
    PlayerStatus ps = _playerModel._player_status;
    if (ps.isWaiting()) {
      return;
    }
    if (ev._type == FIELD) {
      if (ev._action.equals("enable_fielder_move")) {
        _anim_event = ev;
        if (_owner.getThisPlayer().equals(_playerModel) &&
            _playerModel.isFielder()) {
          _cat.finest("enable_fielder_move");
          _owner.addKeyListener(this);
          _owner.setFocusable(true);
          _owner.requestFocusInWindow();
          _focus = true;
        }
        _move_state = FD;
      }
      else if (ev._action.equals("disable_fielder_move")) {
        _anim_event = ev;
        if (_focus) {
          _cat.finest("disable_fielder_move");
          _owner.setFocusable(false);
          _owner.removeKeyListener(this);
        }
        _move_state = DT;
      }
      else if (ev._action.equals("default")) {
        _anim_event = ev;
        Rectangle r = _player.getRealCoords();
        Point pos = new Point(_player.getPos());
        _player = _skin.getPlayersSkin(ps, "default", (int) ps.getPlayerType());
        _player.setPos(pos);
        _owner.repaint(r);
        _owner.repaint(_player.getRealCoords());
        _move_state = DT;
        _cat.finest("default " + _player);
      }
      else if (ev._action.equals("reset")) {
        _anim_event = ev;
        Rectangle r = _player.getRealCoords();
        _player = _skin.getPlayersSkin(ps, "default", (int) ps.getPlayerType());
        _move_state = DT;
        _cat.finest("reset " + _player);
        _owner.repaint(r);
        _owner.repaint(_player.getRealCoords());
      }
    }
    else if (ev._type >= 11 && ev._type <= 21) {
      _cat.finest("ANIMATE " + ev);
      if (ev._type == getAnimConstantValue(ps.getPlayerType())) {
        refresh();
        _anim_event = ev;

        Rectangle r = _player.getRealCoords();
        Point pos = new Point(_player.getPos());
        _player = _skin.getPlayersSkin(ps, ev._action, ev._type);
        _player.setPos(pos);
        _player.startAnimation();
        _owner.repaint(r);
        _owner.repaint(_player.getRealCoords());

        _cat.finest("ANIMATE " + ev + _player + _playerModel);
      }
      _cat.finest("Animated " + _player);
      _steps = 0;
    }
    if (!_am.isObserver() && _am.isThisMyPlayer(_playerModel)) {
      // if this player move the camera to me
      _am.moveCamera(_player.x(), _player.y());
    }
  }

  public int delay() {
    return (int) (_player._delay); // * _am._speed_factor);
  }

  public void run() {
    if (_anim_event == null) {
      return;
    }
    _anim_event._start_delay--;
    if (_anim_event._start_delay > 0) {
      return;
    }

    Rectangle r1 = null, r2 = null;
    try {
      r1 = _player.getRealCoords();
      r2 = _player.getNameBounds();
      _player.update();
      //_cat.finest(_player);
      _owner.repaint(r1);
      _owner.repaint(r2);
      AEvent event = _anim_event.getEvent(_steps);
      if (event != null) {
        _am.update(event._attach, event._nextEvent);
      }
      _steps++;
    }
    catch (AnimationOverException e) {
      _anim_event.invokeRemainingEvents(_am);
      _owner.repaint(r1);
      _owner.repaint(r2);
      _cat.info("Animation Complete " + _anim_event);
      animate(getDefaultAnimation(), _am.frameCount());
    }
    catch (CollisionException e) {
      _cat.info("Collision detected " + _anim_event);
    }
    catch (Exception e) {
      _cat.warning("Animation Complete");
      animate(getDefaultAnimation(), _am.frameCount());
    }
  }

  public AnimationEvent getDefaultAnimation() {
    AnimationEvent ev = null;
    PlayerStatus ps = _playerModel._player_status;
    ev = new AnimationEvent("default", getAnimConstantValue(ps.getPlayerType()),
                            0);
    ev.setStart(new Point(_player.x(), _player.y()));
    return ev;
  }

  public String getType() {
    return _playerModel._player_status.getPlayerTypeString();
  }

  public void shift(int x, int y, int z) {
    _cat.info("Shifting player x=" + x + ", y=" + y);
    PlayerStatus ps = _playerModel.getPlayerStatus();

    Rectangle r = _player.getRealCoords();
    if (x > 0 && (_move_state & RT) == 0) {
      _move_state = RT;
      Point pos = new Point(_player.getPos());
      _player = _skin.getPlayersSkin(ps, "right", (int) ps.getPlayerType());
      _player.setPos(pos);
    }
    if (x < 0 && (_move_state & LT) == 0) {
      _move_state = LT;
      Point pos = new Point(_player.getPos());
      _player = _skin.getPlayersSkin(ps, "left", (int) ps.getPlayerType());
      _player.setPos(pos);
    }
    if (y < 0 && (_move_state & UP) == 0) {
      _move_state = UP;
      Point pos = new Point(_player.getPos());
      _player = _skin.getPlayersSkin(ps, "up", (int) ps.getPlayerType());
      _player.setPos(pos);
    }
    if (y > 0 && (_move_state & DN) == 0) {
      _move_state = DN;
      Point pos = new Point(_player.getPos());
      _player = _skin.getPlayersSkin(ps, "down", (int) ps.getPlayerType());
      _player.setPos(pos);
    }

    _player.move(x, y, 300);
    _cat.finest(_player.toString());
    _owner.repaint(r);
    _owner.repaint(_player.getRealCoords());

  }

  public void process(int ch, boolean for_move) {
    _cat.finest("keyTyped = " + ch + ", moveState=" + _move_state);
    PlayerStatus ps = _playerModel.getPlayerStatus();
    boolean move = false;
    int dx = 0, dy = 0;

    if (ch == 38) {
      // up arrow
      Rectangle r = _player.getRealCoords();
      if ((_move_state & UP) == 0) {
        Point pos = new Point(_player.getPos());
        _player = _skin.getPlayersSkin(ps, "up", (int) ps.getPlayerType());
        _player.setPos(pos);
        _cat.finest(_player.toString());
      }
      move = _player.move(0, dy = -5, 200);
      _move_state = UP + FD;
      _owner.repaint(r);
      _owner.repaint(_player.getRealCoords());
    }
    else if (ch == 40) {
      //down arrow
      Rectangle r = _player.getRealCoords();
      if ((_move_state & DN) == 0) {
        Point pos = new Point(_player.getPos());
        _player = _skin.getPlayersSkin(ps, "down", (int) ps.getPlayerType());
        _player.setPos(pos);
        _cat.finest(_player.toString());
      }
      move = _player.move(0, dy = 5, 200);
      _move_state = DN + FD;
      _owner.repaint(r);
      _owner.repaint(_player.getRealCoords());
    }
    else if (ch == 37) {
      //left arrow
      Rectangle r = _player.getRealCoords();
      if ((_move_state & LT) == 0) {
        Point pos = new Point(_player.getPos());
        _player = _skin.getPlayersSkin(ps, "left", (int) ps.getPlayerType());
        _player.setPos(pos);
        _cat.finest(_player.toString());
      }
      move = _player.move(dx = -5, 0, 200);
      _move_state = LT + FD;
      _owner.repaint(r);
      _owner.repaint(_player.getRealCoords());
    }
    else if (ch == 39) {
      // right arrow
      Rectangle r = _player.getRealCoords();
      if ((_move_state & RT) == 0) {
        Point pos = new Point(_player.getPos());
        _player = _skin.getPlayersSkin(ps, "right", (int) ps.getPlayerType());
        _player.setPos(pos);
        _cat.finest(_player.toString());
      }
      move = _player.move(dx = 5, 0, 200);
      _move_state = RT + FD;
      _owner.repaint(r);
      _owner.repaint(_player.getRealCoords());
    }

    else if (ch == KeyEvent.VK_BACK_SLASH && _move_state != DT) { // player is in default state when he is not fielding
      _cat.finest("STOPPING..." + this);
      Rectangle r = _player.getRealCoords();
      Point pos = new Point(_player.getPos());
      _player = _skin.getPlayersSkin(ps, "default", (int) ps.getPlayerType());
      _player.setPos(pos);
      _owner.repaint(r);
      _owner.repaint(_player.getRealCoords());
    }

    else if ((_move_state & FD) > 0 && ch == KeyEvent.VK_S) {
      Animator ban = _am.getBall();
      if (ban instanceof BallView) {
        BallView bav = (BallView) ban;
        Rectangle ball_bounds = bav._ball.getBounds();
        _cat.finest("Got ball animation " + bav._ball + "\n FB=" +
                   _player.getBounds() + ", BB=" + ball_bounds);
        if (_player.getBounds().intersects(ball_bounds)) {
          _cat.finest("Fielded ..");
          _move_state = DT;
          _owner.addCommentary(" the fielder " + this._playerModel.getName() +
                               " has fielded and stopped the ball well done " +
                               this._playerModel.getName());

          JButton jb = new JButton();
          jb.setName("field");
          ActionEvent ae = new ActionEvent(jb, 1, "S");
          _owner._bottomPanel.actionPerformed(ae);
        }
      }
    }

    else if ((_move_state & FD) > 0 && ch == KeyEvent.VK_C) {
      Animator ban = _am.getBall();
      _cat.finest("BV=" + ban.toString());
      if (ban instanceof BallView) {
        BallView bav = (BallView) ban;
        Rectangle ball_bounds = bav._ball.getBounds();
        _cat.finest("Got ball animation " + bav._ball + "\n FB=" +
                   _player.getBounds() + ", BB=" + ball_bounds);
        if (_player.getBounds().intersects(ball_bounds)) {
          _cat.finest("Caught ..");
          _move_state = DT;
          _owner.addCommentary(" the fielder " + this._playerModel.getName() +
                               " has caught the ball in the air....catch out " +
                               this._playerModel.getName());

          JButton jb = new JButton();
          jb.setName("field");
          ActionEvent ae = new ActionEvent(jb, 1, "C");
          _owner._bottomPanel.actionPerformed(ae);
        }
      }
    }

    _steps = 0;
    //_anim_event.setNextEvent(null, Gse.FIELDER_DEFAULT, 20);
    if (move && _owner instanceof CricketController && (_move_state & FD) > 0 &&
        for_move && _playerModel.isFielder()) {
      try {
        CricketController cc = (CricketController) _owner;
        cc._eventGen.sendToServer(new CommandPlayerShift(null,
            cc._cricketModel.getGameId(), getType(), dx, dy, 0));
      }
      catch (IOException e) {
        // DISCONNECTED FRM SERVER
        JOptionPane.showMessageDialog(_owner, _bundle.getString("server.error"),
                                      _bundle.getString("error"),
                                      JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void keyReleased(KeyEvent ke) {
  }

  public void keyPressed(KeyEvent ke) {
    //_cat.finest(_move_state + " This player = " + _playerModel + " Owner " +
    //         _owner.getThisPlayer());
    if (!(_owner.getThisPlayer().equals(_playerModel) || _playerModel.isFielder()) ||
        (_move_state & FD) == 0) {
      return;
    }
    process(ke.getKeyCode(), true);
  }

  public void keyTyped(KeyEvent ke) {
  }

  public static int getAnimConstantValue(long fielder_status_value) {
    if (fielder_status_value == PlayerStatus.LF1) {
      return LF1;
    }
    else if (fielder_status_value == PlayerStatus.RF1) {
      return RF1;
    }
    else if (fielder_status_value == PlayerStatus.LF2) {
      return LF2;
    }
    else if (fielder_status_value == PlayerStatus.RF2) {
      return RF2;
    }
    else if (fielder_status_value == PlayerStatus.LF3) {
      return LF3;
    }
    else if (fielder_status_value == PlayerStatus.RF3) {
      return RF3;
    }
    else if (fielder_status_value == PlayerStatus.LF4) {
      return LF4;
    }
    else if (fielder_status_value == PlayerStatus.RF4) {
      return RF4;
    }
    else if (fielder_status_value == PlayerStatus.LF5) {
      return LF5;
    }
    else if (fielder_status_value == PlayerStatus.RF5) {
      return RF5;
    }
    else if (fielder_status_value == PlayerStatus.WICKET_KEEPER) {
      return WICKET_KEEPER;
    }
    else if (fielder_status_value == PlayerStatus.BOWLER) {
      return BOWL;
    }
    else {
      _cat.warning("Wrong player status ------------------!!!" +
                fielder_status_value);
      return NONE;
    }
  }

}
