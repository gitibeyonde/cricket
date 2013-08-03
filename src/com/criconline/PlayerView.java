package com.criconline;

import java.awt.*;
import javax.swing.*;
import java.util.logging.Logger;
import com.cricket.mmog.PlayerStatus;
import com.criconline.pitch.PitchSkin;
import com.criconline.anim.Animation;
import com.criconline.anim.AnimationConstants;
import com.criconline.anim.AnimationManager;
import java.util.ResourceBundle;
import com.criconline.resources.Bundle;

public class PlayerView implements AnimationConstants {

  static Logger _cat = Logger.getLogger(PlayerView.class.getName());
  /** the component which proced painint */
  protected CricketController _owner = null;

  protected boolean _b = true;
  protected int _visibleMessageTact = 0;
  protected Animation _player = null;
  protected int _player_number;
  protected int _team_size;
  protected AnimationManager _am;

  protected ClientPlayerModel _playerModel = null;
  protected PitchSkin _skin;
  public boolean _isMe = false;

  //--------------------------
  protected ResourceBundle _bundle;

  public PlayerView(PitchSkin skin, ClientPlayerModel model,
                    CricketController owner, AnimationManager am, int ts,
                    int num) {
    assert owner != null:"Owner is null"; _team_size = ts;
    _player_number = num;
    _player = skin.getPlayersSkin(model.getPlayerStatus(), ts, num);
    // _cat.finest("VIEW PS=" + model + ", PI=" + _player + ", num=" + _player_number);
    _playerModel = model;
    _owner = owner;
    _skin = skin;
    _am = am;
    //refresh();
    _bundle = Bundle.getBundle();
  }

  public Rectangle getBounds() {
    return _player.getBounds();
  }

  public Rectangle getFieldBounds() {
    return _player.getFieldBounds();
  }

  public Point getPos() {
    return _player.getPos();
  }

  public void paint(JComponent c, Graphics g) {
    //_cat.finest("ClientPlayerView: paint start");
    //_cat.finest("PAINTING " + _player);
    //_cat.finest("PM " + _playerModel);
    PlayerStatus ps = _playerModel.getPlayerStatus();
    if (_player != null) {
      Graphics2D g2d = (Graphics2D) g.create();
      if (ps.isWaiting() || ps.isPreJoin()) {
        float alpha = 0.4F;
        if (_playerModel.getPlayerStatus().isNew()) {
          alpha = 0.2F;
        }
        int type = AlphaComposite.SRC_OVER;
        AlphaComposite rule = AlphaComposite.getInstance(type, alpha);
        g2d.setComposite(rule);
        _player.paintIcon(c, g2d);
      }
      else {
        _player.paintIcon(c, g2d, _am.dispX(), _am.dispY());
      }
      g2d.dispose();
    }
    /** Paint players name and money **/
    Rectangle r = _player.getNameBounds();
    if (r != null) {
      Graphics gcopy = null;
      if (ps.isWaiting() || ps.isNew()){
        gcopy = g.create(r.x - 42, r.y-10, r.width,
                         r.height);
      }
      else if (ps.isBatsman() ||
          ps.isBowler() || ps.isWicketKeeper()) {
        gcopy = g.create(r.x + _am.dispX(), r.y - 80 + _am.dispY(), r.width,
                         r.height);
      }
      else {
        gcopy = g.create(r.x - 30 + _am.dispX(), r.y - 40 + _am.dispY(),
                         r.width, r.height);
      }

      if (_isMe) {
        gcopy.setColor(Color.RED);
        gcopy.setFont(Utils.myNameFont);
      }
      else {
        gcopy.setColor(Color.YELLOW);
        gcopy.setFont(Utils.nameFont);
      }

      if (_playerModel.getName().length() > 8) {
        gcopy.drawString(_playerModel.getName().substring(0, 8), 0, 10);
      }
      else {
        gcopy.drawString(_playerModel.getName(), 0, 10);
      }

      if  (!(ps.isPreJoin() || ps.isWaiting())){
        gcopy.setFont(Utils.chipsFont);
        gcopy.setColor(Color.CYAN);
        gcopy.drawString(_playerModel._runs + "", 0, 20);
      }

      gcopy.dispose();
    }
    //_cat.finest("ClientPlayerView: paint end");
  }

  public void refresh() {
    _owner.repaint(_player.x(), _player.y(), _player.getIconWidth(),
                   _player.getIconWidth());
    Rectangle r = _player.getNameBounds();
    _owner.repaint(r.x - 10, r.y - 10, r.width, r.height);
  }



  public void showSpeak(boolean mustShow) {
    if (_playerModel != null) {
      if (_playerModel.isMustPaintSpeaker() != mustShow) {
        _playerModel.setMustPaintSpeaker(mustShow);
        //refreshSpeak();
      }
    }
  }

  public void invertSpeak() {
    if (_playerModel != null) {
      _playerModel.setMute(!_playerModel.isMute());
      //refreshSpeak();
    }
  }

  public String toString() {
    return _playerModel + ", " + _player.toString();
  }

  public void destroy() {}

  public void stop() {}

}
