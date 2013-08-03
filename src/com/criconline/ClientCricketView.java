package com.criconline;

import java.awt.*;
import javax.swing.*;
import java.util.logging.Logger;
import com.criconline.pitch.PitchSkin;
import com.cricket.mmog.cric.GameState;
import com.cricket.mmog.PlayerStatus;
import com.criconline.anim.Animation;
import com.criconline.anim.AnimationConstants;


/** Desk for playing */
public class ClientCricketView
    implements Painter, AnimationConstants {
  static Logger _cat = Logger.getLogger(ClientCricketView.class.getName());

  /** background icon */
  private ImageIcon _background;
  public Animation _umpire, _stumps1, _stumps2;
  /** Poker model (data of the Game) */
  private ClientCricketModel _cricketModel;
  protected PitchSkin _roomSkin = null;

  //  RoomSkin <- coordinats of players, coor_s of chips, images for table, ...
  public ClientCricketView(ClientCricketModel model, PitchSkin roomSkin) {
    _roomSkin = roomSkin;
    _cricketModel = model;
    _background = roomSkin.getBackround();
    _umpire = roomSkin.getUmpire();
    _stumps1 = roomSkin.getStumps(0);
    _stumps2 = roomSkin.getStumps(1);
  }

  private static final int DELTA_Y = 12;

  public void paint(JComponent c, Graphics g) {
    //_cat.finest("ClientCricketView: paint start");
    if (_background.getIconHeight() > 0) {
      _background.paintIcon(c, g, -_cricketModel._am.cX(), -_cricketModel._am.cY());
    }
    _stumps1.paintIcon(c, g, _cricketModel._am.dispX(), _cricketModel._am.dispY());
    _stumps2.paintIcon(c, g, _cricketModel._am.dispX(), _cricketModel._am.dispY());
    _umpire.paintIcon(c, g, _cricketModel._am.dispX(), _cricketModel._am.dispY());
    //try {
     // _umpire.update();
    //}catch (Exception e){}
    Rectangle r = _cricketModel.getHandIdBounds();
    String handIdOld = "(prev #" +
        SharedConstants.intTo3NumSeparetedString(_cricketModel.
                                                 getOldGameId()) + ")";
    String handIdNew = "Match #" +_cricketModel.getGameId();

    Graphics gcopy = g.create(r.x, r.y, r.width, r.height);

    gcopy.setColor(Color.black);
    gcopy.drawString(handIdNew, 0 + 2, 10 + 2);
    gcopy.drawString(handIdOld, 0 + 2, DELTA_Y + 10 + 2);

    gcopy.setColor(Color.yellow);
    gcopy.drawString(handIdNew, 0, 10);
    gcopy.drawString(handIdOld, 0, DELTA_Y + 10);
    gcopy.dispose();

    //_cat.finest("Delta=" + _cricketModel._delta);
    if (_cricketModel._owner.getGameType().isMadness()  && _cricketModel._delta > 0){
      g.drawString("Only invited teams can join at pre-designated times. The procedure to play the tournament will be mailed to you few days before the tournament date.",
                   70, 100);
      g.drawString("Match starting at " + _cricketModel._tourny_date + " in " + _cricketModel._delta + " minutes.", 350, 130);

      g.setColor(Color.RED);
      g.drawString(_cricketModel._teamAname, 350, 400);
      g.drawString(" Vs ", 500, 400);
      g.drawString(_cricketModel._teamBname, 600, 400);

      g.setColor(Color.yellow);

      for (int i=0;i<_cricketModel._teamAInvited.length;i++){
        g.drawString(_cricketModel._teamAInvited[i], 5 , 290 + (10 * i));
      }

      for (int i=0;i<_cricketModel._teamBInvited.length;i++){
        g.drawString(_cricketModel._teamBInvited[i], 900 , 290 + (10 * i));
      }

    }
    //_cat.finest("ClientCricketView: paint end");
  }
}
