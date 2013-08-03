package com.criconline.pitch;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import javax.swing.ImageIcon;
import java.util.logging.Logger;
import com.criconline.models.PlayerModel;
import com.criconline.Utils;
import com.cricket.mmog.PlayerStatus;
import com.criconline.anim.Anime;
import com.criconline.anim.Animation;
import com.criconline.anim.AnimationConstants;


public class PitchSkin implements AnimationConstants {

  static Logger _cat = Logger.getLogger(PitchSkin.class.getName());

  protected static Color textColor = new Color(196, 196, 196);

  protected static ImageStripData _isd;


  /** Images */
  protected ImageIcon background = null;
  protected Animation empire = null;
  protected ImageIcon tourny_background = null;
  protected Animation[] players = null;
  protected ImageIcon speakIcon = null;
  protected ImageIcon placeInaccessibleIcon = null;
  protected ImageIcon bottonPanelBackground = null;


  /** font color for room's labels */
  protected Color fontColor;

  public PitchSkin() {
    background = Utils.getIcon("images/pitch.jpg");
    tourny_background = Utils.getIcon("images/tourny_lobby.jpg");
    speakIcon = Utils.getIcon("images/speak.gif");
    placeInaccessibleIcon = Utils.getIcon("images/placeInaccessible.gif");
    bottonPanelBackground = Utils.getIcon("images/bg_copy.png");
    //player_wait_icon = Utils.getIcon("images/man01.png");
    fontColor = textColor;
    fontColor = textColor;
    _isd = ImageStripData.instance();
  }


  public ImageIcon getBackround() {
    return (background);
  }

  public ImageIcon getTournyBackround() {
    return (tourny_background);
  }

  public ImageIcon getSpeakIcon() {
    return (speakIcon);
  }

  public ImageIcon getPlaceInaccessibleIcon() {
    return (placeInaccessibleIcon);
  }

  public ImageIcon getBottonPanelBackground() {
    return (bottonPanelBackground);
  }

  public Color getFontColor() {
    return fontColor;
  }

  public Animation getPlayersOutline(int ts, int num) {
    return new Animation(_isd.getOutline(ts, num));
  }

  public Animation getStumps(int i) {
    return new Animation(_isd.getStumps(i));
  }

  public Animation getArrow(String str) {
    return new Animation(_isd.getArrow(str));
  }

  public Animation getUmpire() {
    return new Animation(_isd.getUmpire());
  }

  public Animation getFielderMarker() {
    return new Animation(_isd.getFielderMarker());
  }

  public Animation getPitchPoint() {
    return new Animation(_isd.getPitchPoint());
  }

  public Animation getTossCoin() {
    return new Animation(_isd.getTossCoin());
  }

  public Animation getStumpsFall() {
    return new Animation(_isd.getStumpsFall());
  }

  public Animation getSpeakBubbles() {
    return new Animation(_isd.getSpeakBubbles());
  }

  public Animation getBallShadow() {
    return new Animation(_isd.getBallShadow());
  }

  public Animation getPlayersSkin(PlayerStatus ps, int ts, int num) {
    if (!ps.isPreJoin()) {
      return new Animation(_isd.getDefaultImageStrip(ps, ts, num));
    }
    else {
      return new Animation(_isd.getOutline(ts, num));
    }
  }

  public Animation getPlayersSkin(PlayerStatus ps, String action, int type) {
    return new Animation(_isd.getActionImageStrip(ps, action));
  }

  public Animation getMeter(String action) {
    return new Animation(_isd.getImageStrip("meter", action));
  }


  public Point getPlayerPlace(PlayerStatus ps, int num) {
    return null;
  }

}
