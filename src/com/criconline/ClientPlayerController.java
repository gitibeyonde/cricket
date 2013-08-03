package com.criconline;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.util.logging.Logger;
import com.criconline.models.*;
import com.cricket.mmog.PlayerStatus;
import com.criconline.actions.*;
import com.criconline.pitch.PitchSkin;
import com.criconline.anim.AnimationManager;
import com.criconline.util.StatsPopup;

//import com.agneya.util.Utils;

public class ClientPlayerController implements Painter {
  static Logger _cat = Logger.getLogger(ClientPlayerController.class.getName());
  /** signalizate then NULL-player */
  protected boolean _show = false;
  /** the component which proced painint */
  protected CricketController _owner = null;
  /** model & view of the player */
  protected ClientPlayerModel _playerModel = null;
  private PlayerStatus _past_status;
  protected PlayerView _playerView = null;

  /** Player sex */
  protected char _playerSex = ' ';

  protected PitchSkin _skin = null;
  protected static ImageIcon placeInaccessible = null;
  public boolean _isMe=false;
  boolean _isPopup;
  StatsPopup _popup;

  /** Create client player */
  public ClientPlayerController(PlayerModel model, PitchSkin skin,
                                CricketController owner, AnimationManager am, int ts, int num) {
    assert owner != null:"Owner is null ";
    //new Exception().printStackTrace();
    _owner = owner;
    _skin = skin;
    _playerModel = new ClientPlayerModel(model);
    _past_status = _playerModel.getPlayerStatus();
    _playerView = PlayerViewFactory.newPlayerView(_skin, _playerModel, _owner, am, ts, num);
    _playerView._isMe = _isMe;
    _cat.finest("CPC PLAYER = " + model._name + ", state=" +
               _playerModel.getPlayerStatus());
  }


  public void updateController(PlayerModel pm, AnimationManager am, int ts, int num) {
    _playerModel.updateModel(pm);
    if (!_past_status.equals(pm._player_status)) {
      //_cat.finest("Update Player View= " + pm + ", Past status = " +_past_status + ", ts=" + ts + ", num=" + num);
      _playerView.refresh();
      _playerView.destroy();
      _playerView = PlayerViewFactory.newPlayerView(_skin, _playerModel, _owner, am, ts, num);
      _playerView._isMe = _isMe;
      _past_status = _playerModel.getPlayerStatus();
      _playerView.refresh();
    }
  }

  public void setSelected(boolean selected) {
    if (_playerModel != null) {
      if (selected != _playerModel.isSelected()) {
        _playerModel.setSelected(selected);
        _playerView.refresh();
      }
    }
  }

  public PlayerStatus getState() {
    return _playerModel._player_status;
  }

  public void setState(long ps) {
    _playerModel._player_status.setStatus(ps);
  }

  public void setState(PlayerStatus ps) {
    _playerModel._player_status = ps;
  }

  public void clear() {
    Rectangle r = null;
    _owner.repaint(r);
  }


  public void setShow(boolean show) {
    _show = show;
    _playerView.refresh();
  }

  public boolean getShow() {
    //System.out.println("getShow() = " + show);
    return (_show);
  }

  public boolean mouseOver(int mouseX, int mouseY) {
    //_cat.finest( _owner.getLocation() + " Mouse Over " + _playerModel);
    if (_playerView != null && _playerView.getBounds().contains(mouseX, mouseY) && _popup == null) {
      if (!_playerModel.getPlayerStatus().isPreJoin()){
        //_popup = new StatsPopup(_playerModel, _owner.getLocation());
      }
    }
    else if (_popup!= null){
      //_popup.update(_playerModel);
      //_popup.setVisible(true);
    }
    return true;
  }

  public String getPlayerName() {
    if (_playerModel != null) {
      return _playerModel.getName();
    }
    else {
      return "";
    }
  }

  public void setPlayerName(String name) {
    if (_playerModel != null) {
      _playerModel.setName(name);
    }
  }

  public void paint(JComponent c, Graphics g) {
    Graphics scratchGraphics = g.create();
    try {
      _playerView.paint(c, scratchGraphics);
    }
    finally {
      scratchGraphics.dispose();
    }
  }

  public Point getPos() {
    return _playerView.getPos();
  }

  public char getSex() {
    return _playerSex;
  }

  public boolean isMute() {
    if (_playerModel != null) {
      return _playerModel.isMute();
    }
    return true;
  }

  public void stop(){
    _playerView.stop();
  }

  public String toString(){
    return _playerModel.toString() + _playerView.toString();
  }
}
