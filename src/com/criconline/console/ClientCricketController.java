package com.criconline.console;

import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import com.criconline.resources.*;
import com.criconline.*;
import com.criconline.actions.*;
import com.criconline.actions.Action;
import com.criconline.models.*;
import com.criconline.pitch.PitchSkin;
import com.cricket.common.message.GameEvent;
import com.cricket.mmog.PlayerStatus;
import com.criconline.util.*;
import javax.swing.JOptionPane;
import com.cricket.mmog.cric.util.ActionConstants;
import com.criconline.server.EventGenerator;
import com.cricket.mmog.cric.GameState;

public class ClientCricketController extends CricketController
     {

  static Logger _cat = Logger.getLogger(ClientCricketController.class.getName());

  /** Players parametrs */
  // Frame
  public ClientRoom _clientRoom;

  public ClientCricketController(EventGenerator eg, PitchSkin skin, BottomPanel bottomPanel,
                                 MessagePanel messagePanel, ClientRoom clientRoom) {
    super(eg, skin, bottomPanel, messagePanel, clientRoom.getName());
    _cat.finest("Creating client cricket controller");
    _selPitch = clientRoom;
    _clientRoom = clientRoom;
  }

  public void paintComponent(Graphics g) {
    //_cat.finest("PractisePitch paintComponent  start");
    super.paintComponent(g);
    //_cat.finest("PractisePitch paintComponent  end");
  }

  public Dimension getPreferredSize() {
    return new Dimension(_background.getIconWidth(), _background.getIconHeight());
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public ClientCricketView getView() {
    return (_cricketView);
  }

  public ClientCricketModel getModel() {
    return (_cricketModel);
  }


  /**
   * @return chips that player have at DB
   */
  public double getPlayerChips() {
    return 0; //clientRoom.getPlayerChips();
  }



  public void appendActionToLog(Action a) {
     /**switch (a.getId()) {
     case ActionConstants.BET_REQUEST:
        bottomPanel.logItsYourTurn(model.getClientPlayerName());
        break;
             case ActionConstants.START_GAME:
        bottomPanel.appendSep();
        break;
             default:
        String s = a.toMessage(getPlayerName(a));
//*********************************************
        playerName = getPlayerName(a); // It will have the current Player name.
//*********************************************
                        if (s.length() > 0) {
                          _cat.finest("Player name = " + s);
                          bottomPanel.appendLog(s.toString());
                        }
    }**/
  }

  public void serverErrorOccured(String info) {
    new Exception(info).printStackTrace();
    JOptionPane.showMessageDialog(this, _bundle.getString("server.error"),
                                  _bundle.getString("error"),
                                  JOptionPane.ERROR_MESSAGE);
    _clientRoom.markWindowAsClosed();

    //++++++++++++++++++++++++++++++++++++++++++++clientRoom.forcedCloseRoom();
  }

}
