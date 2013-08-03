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
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import com.criconline.anim.AnimationEvent.AEvent;
import com.cricket.common.message.CommandPlayerShift;
import java.awt.event.ActionEvent;
import com.criconline.actions.LastMoveAction;
import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.mmog.cric.util.KeyMap;
import com.cricket.mmog.cric.util.MoveUtils;
import java.io.IOException;

public class BatsmanPlayerView extends PlayerView implements Animator,
    KeyListener {
  static Logger _cat = Logger.getLogger(BatsmanPlayerView.class.getName());
  static int _type = BATS;

  public AnimationEvent _anim_event = null;
  int _steps = 0;
  static int _count;

  //protected Animation _arrow_left, _arrow_right, _arrow_all;
  public int _bats_state = NEUTRAL;
  public KeyMap _km;

  public BatsmanPlayerView(PitchSkin skin, ClientPlayerModel model,
                           CricketController owner, AnimationManager am, int ts,
                           int num) {
    super(skin, model, owner, am, ts, num);
    _am.addAnimator(this, model.getPlayerStatus());

    _anim_event = getDefaultAnimation();
    /** _arrow_left = skin.getArrow("left");
     _arrow_left.setPos(new Point(_player.x() - _arrow_left.getIconWidth() - 185,
                                  _player.y() + _player.getIconHeight()));
     _arrow_left.setEndPos(new Point(_player.x() - _arrow_left.getIconWidth() -
     185, _player.y() + _player.getIconHeight()));
     _arrow_left.setStartPos(new Point(_player.x() - _arrow_left.getIconWidth() -
     185, _player.y() + _player.getIconHeight()));

     _arrow_right = skin.getArrow("right");
     _arrow_right.setPos(new Point(_player.x() + 160,
                                   _player.y() + _player.getIconHeight()));
     _arrow_right.setEndPos(new Point(_player.x() + 160,
                                      _player.y() + _player.getIconHeight()));
     _arrow_right.setStartPos(new Point(_player.x() + 160,
     _player.y() + _player.getIconHeight()));

     _arrow_all = skin.getArrow("all");

     _arrow_left.setInvalid();
     _arrow_right.setInvalid();
     _arrow_all.setInvalid(); **/_km = new KeyMap(BATS);
    refresh();
    _count++;
  }

  public int getZOrder() {
    return _player.getZOrder();
  }

  public Animation getAnimation() {
    return _player;
  }

  public void collision(Animation a) {
    //collision detected. stop the ball
    throw new IllegalStateException(" No collision with batasman ");
  }

  public int spin() {
    if (_player._dx < -33) {
      return -3;
    }
    else if (_player._dx < -20) {
      return -2;
    }
    else if (_player._dx < -10) {
      return -1;
    }
    else if (_player._dx == 0) {
      return 0;
    }
    else if (_player._dx < 10) {
      return 1;
    }
    else if (_player._dx < 20) {
      return 2;
    }
    else if (_player._dx < 33) {
      return 3;
    }
    return 3;
  }

  public synchronized void animate(AnimationEvent ev, int frame) {
    if (ev._type != BATS || !_playerModel._player_status.isBatsman()) {
      return;
    }
    if (ev._action.equals("enable_bats_move")) {
      _player.reset();

      if (_owner.getThisPlayer().equals(_playerModel) && _playerModel.isBats()) {
        _owner.addKeyListener(this);
        _owner.setFocusable(true);
        _owner.requestFocus();
        _cat.finest("Setting keyboard focus-----------");
        _owner.requestFocusInWindow();
      }
      _bats_state = TAKE_POSITION;
      /**_arrow_left.setValid();
             _arrow_right.setValid();
             _owner.repaint(_arrow_left.getBounds());
             _owner.repaint(_arrow_right.getBounds());**/_km.reset();
    }
    else if (ev._action.equals("enable_bats_shots")) {
      _bats_state = SELECT_SHOT;
      /**_arrow_left.setInvalid();
             _arrow_right.setInvalid();
             _arrow_all.setValid();
             _owner.repaint(_arrow_left.getBounds());
             _owner.repaint(_arrow_right.getBounds());
             _owner.repaint(_arrow_all.getBounds());**/

    }
    else if (ev._action.equals("disable_bats_move")) {
      //_owner.setFocusable(false);
      //_cat.finest("Removing keyboard focus-----------");
      //_owner.removeKeyListener(this);
      _bats_state = NEUTRAL;
      //_arrow_all.setInvalid();
      //_owner.repaint(_arrow_all.getBounds());
    }
    else if (ev._action.equals("ready")) {
      _anim_event = ev;
      _km.reset();
      Rectangle r = _player.getRealCoords();
      _player = _skin.getPlayersSkin(_playerModel.getPlayerStatus(), "ready", ev._type);
      _player._dx=0;
      _player._dy=0;

      _owner.repaint(r);
      _owner.repaint(_player.getRealCoords());
      _cat.finest("ready " + _player);
    }
    else {
      refresh();
      _anim_event = ev;
      int dx = _player._dx;
      int dy = _player._dy;
      _cat.finest("Dx=" + dx + ", Dy=" + dy);
      _player = _skin.getPlayersSkin(_playerModel.getPlayerStatus(), ev._action,
                                     ev._type);
      _player.move(dx, dy, 50);
      LastMoveAction lma = (LastMoveAction) ev.getAttach();
      if (lma != null) {
        MoveParams mp = lma.getMoveParams();
        //mp.setKeyMap(_km);
        _cat.finest("MoveParams=" + mp);
        // This will move the player ahead if the hit is bit early
        _player.setEndPos(new Point(_player.x(), _player.y() + (mp._timing * 5)));
        _owner.addCommentary(mp.getBatCommentary());
      }
      _cat.finest("ANIMATE " + ev + _player);
      _cat.finest("ANIMATE " + _playerModel);
      _steps = 0;
      _player.startAnimation();
    }
    _cat.finest(_player.toString());
  }

  public int delay() {
    return (int) (_player._delay * _am._speed_factor);
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
      //_cat.info("Animation Complete " + _anim_event);
      //animate(getDefaultAnimation());
//        _thread.setPriority(Thread.MIN_PRIORITY);
    }
    catch (CollisionException e) {
      _cat.info("Collision detected " + _anim_event);
    }
    catch (Exception e) {
      _cat.warning("Animation Complete or Inturrepted " + e);
      //animate(getDefaultAnimation());
    }
  }

  public void paint(JComponent c, Graphics g) {
    super.paint(c, g);
    //_cat.finest("BatsmanPlayerView: paint start" + _focus + ", " + _player.x() + ", " + _player.x());
    /**if (_bats_state == TAKE_POSITION) {
      _arrow_left.paintIcon(c, g);
      _arrow_right.paintIcon(c, g);
         }
         else if (_bats_state == SELECT_SHOT) {
      _arrow_all.paintIcon(c, g);
         }**/
  }

  public AnimationEvent getDefaultAnimation() {
    return new AnimationEvent("default", BATS, 0);
  }

  public String getType() {
    return _playerModel._player_status.getPlayerTypeString();
  }


  public void keyPressed(KeyEvent ke) {
    if (_owner.getThisPlayer().equals(_playerModel) && _playerModel.isBats()) {
  //    _cat.finest("This player = " + _playerModel);
//      _cat.finest("KEY PRESSED");
      processKey(ke, KEY_PRESSED);
    }
  }

  public void keyReleased(KeyEvent ke) {
    if (_owner.getThisPlayer().equals(_playerModel) && _playerModel.isBats()) {
      //_cat.finest("This player = " + _playerModel);
      //_cat.finest("KEY RELEASED");
      processKey(ke, KEY_RELEASED);
    }
  }

  public void keyTyped(KeyEvent ke) {
    if (_owner.getThisPlayer().equals(_playerModel) && _playerModel.isBats()) {
      //_cat.finest("This player = " + _playerModel);
      //_cat.finest("KEY TYPED");
      processKey(ke, KEY_TYPED);
    }
  }


  public void shift(int x, int y, int z) {
    Rectangle r = _player.getRealCoords();
    _player._dx += x - _player.x();
    _player._dy += y - _player.y();
    Point pos = new Point(x, y);
    _player.setPos(pos);
    _owner.repaint(r);
    _owner.repaint(_player.getRealCoords());
  }

  public void processKey(KeyEvent ke, int key_event) {
    int ch = ke.getKeyCode();
    boolean move = false;
    //_cat.finest("keyTyped = " + ch + ", kcode=" + ke.getKeyCode());
    if (ch == KeyEvent.VK_UP) {
      // up arrow
      if (!_owner._cricketModel.getGameType().isLowerHalfPitch()) {
        if (_bats_state == SELECT_SHOT) {
          _cat.finest("UP");
          _km.addKey(KeyEvent.VK_DOWN);  // alwasy hit down
        }
      }
    }
    else if (ch == KeyEvent.VK_DOWN) {
      //down arrow
      if (_bats_state == SELECT_SHOT) {
        _km.addKey(KeyEvent.VK_DOWN);
        _cat.finest("DOWN");
      }
    }
    else if (ch == KeyEvent.VK_LEFT) {
      //left arrow
      if (_bats_state == TAKE_POSITION && key_event == KEY_PRESSED) {
        Rectangle r = _player.getRealCoords();
        move = _player.move( -2, 0, 50);
        _owner.repaint(_player.getRealCoords());
        _owner.repaint(r);
      }
      else if (_bats_state == SELECT_SHOT) {
        _km.addKey(KeyEvent.VK_LEFT);
        _cat.finest("LEFT");
      }
    }
    else if (ch == KeyEvent.VK_RIGHT) {
      // right arrow
      if (_bats_state == TAKE_POSITION && key_event == KEY_PRESSED) {
        Rectangle r = _player.getRealCoords();
        move = _player.move(2, 0, 50);
        _owner.repaint(_player.getRealCoords());
        _owner.repaint(r);
      }
      else if (_bats_state == SELECT_SHOT) {
        _km.addKey(KeyEvent.VK_RIGHT);
        _cat.finest("RIGHT");
      }
    }
    else if (_bats_state == SELECT_SHOT && ch == KeyEvent.VK_S) {
      _bats_state = NEUTRAL;
      JButton jb = new JButton();
      jb.setName("bat");
      ActionEvent ae = new ActionEvent(jb, 1, "");
      _owner._bottomPanel.actionPerformed(ae);
    }
    if (move && _owner instanceof CricketController && _playerModel.isBats()) {
      try {
        CricketController cc = (CricketController) _owner;
        cc._eventGen.sendToServer(new CommandPlayerShift(null,
            cc._cricketModel.getGameId(), getType(), _player.x(), _player.y(), 0));
      }
      catch (IOException e) {
        // DISCONNECTED FRM SERVER
        JOptionPane.showMessageDialog(_owner, _bundle.getString("server.error"),
                                      _bundle.getString("error"),
                                      JOptionPane.ERROR_MESSAGE);
      }

    }
    //_cat.finest("_player dx=" + _player._dx + ", _player dy=" + _player._dy);
  }

}
