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

public class WKPlayerView extends PlayerView implements Animator, KeyListener {
  static Logger _cat = Logger.getLogger(WKPlayerView.class.getName());

  protected Animation _curr_player = null;
  public AnimationEvent _anim_event = null;
  private int _steps = 0;
  static int _count;
  boolean _focus;

  public WKPlayerView(PitchSkin skin, ClientPlayerModel model,
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
    //collision detected. stop the ball
  }

  public synchronized void animate(AnimationEvent ev, int frame) {
    PlayerStatus ps = _playerModel._player_status;

    if (ps.isWaiting() || !ps.isWicketKeeper()) {
      return;
    }
    if (ev._type == WICKET_KEEPER) {
      _anim_event = ev;
      _cat.finest("ANIMATE " + ev + ps);
      Rectangle r = _player.getRealCoords();
      _player = _skin.getPlayersSkin(_playerModel.getPlayerStatus(), ev._action,
                                     ev._type);
      _owner.repaint(r);
      _owner.repaint(_player.getRealCoords());
      _player.startAnimation();
      _cat.finest("Animated " + _player);
      _steps = 0;
    }
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
      refresh();
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
    return new AnimationEvent("default", WICKET_KEEPER, 0);
  }

  public String getType() {
    return _playerModel._player_status.getPlayerTypeString();
  }

  public void shift(int x, int y, int z) {
  }


  public void keyPressed(KeyEvent ke) {
    process(ke);
  }

  public void process(KeyEvent ke) {

  }

  public void keyReleased(KeyEvent ke) {
    process(ke);
  }

  public void keyTyped(KeyEvent ke) {
  }

}
