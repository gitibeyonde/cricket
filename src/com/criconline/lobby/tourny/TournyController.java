package com.criconline.lobby.tourny;

import com.criconline.models.LobbyCricketModel;
import com.criconline.models.CricketModel;
import com.criconline.models.LobbyTableModel;
import com.criconline.pitch.PitchSkin;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.criconline.resources.Bundle;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import com.cricket.mmog.PlayerStatus;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import com.criconline.server.EventGenerator;
import javax.swing.JFrame;
import com.criconline.actions.ActionVisitor;
import com.criconline.actions.TableServerAction;
import com.criconline.actions.MoveRequestAction;
import com.criconline.actions.ErrorAction;
import com.cricket.mmog.cric.util.ActionConstants;
import com.criconline.actions.LastMoveAction;
import com.criconline.actions.SimpleAction;
import com.criconline.actions.StageAction;
import com.criconline.actions.PlayerJoinAction;
import com.criconline.actions.Action;
import com.criconline.models.PlayerModel;
import com.criconline.anim.Gse;
import com.criconline.anim.MoveBuffer;
import com.criconline.anim.AnimationConstants;
import com.criconline.anim.AnimationManager;
import com.criconline.actions.ChatAction;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import com.cricket.common.message.TournyEvent;
import com.criconline.models.LobbyTournyModel;
import java.awt.Color;
import java.awt.Rectangle;
import com.cricket.common.interfaces.TournyInterface;
import com.cricket.common.message.ResponseInt;
import com.cricket.common.message.Response;
import com.cricket.common.message.ResponseTournyMyTable;
import com.cricket.common.message.GameEvent;
import com.cricket.common.message.ResponseTournyDetail;
import javax.swing.JOptionPane;
import com.criconline.Utils;
import com.criconline.ClientRoom;

public class TournyController extends javax.swing.JPanel implements
    TournyMessagesListener {
  static Logger _cat = Logger.getLogger(TournyController.class.getName());

  String _tid = "";
  LobbyTournyModel _lobbyTournyModel;
  TournyLobby _owner;
  /** Skin of this room */
  protected PitchSkin _skin = null;
  /** Input/Output manipulation organs */
  protected ResourceBundle _bundle;
  /** background */
  protected ImageIcon _tourny_background;
  EventGenerator _eventGen;
  boolean _present;

  public TournyController(TournyLobby owner, LobbyTournyModel ltm,
                          EventGenerator eventGen, PitchSkin skin, String tid) {
    _lobbyTournyModel = ltm;
    _owner = owner;
    _bundle = Bundle.getBundle();
    _eventGen = eventGen;
    _skin = skin;
    _tid = tid;
    _tourny_background = skin.getTournyBackround();
    setSize(_tourny_background.getIconWidth(), _tourny_background.getIconHeight());
    setBorder(null);
    setOpaque(true);
    setLayout(null);
    _present = false;
    repaint();
    _cat.fine("Created Tournament Controller ");
  }

  public void paintComponent(Graphics g) {
    //_cat.debug("Tourny controller paint");
    super.paintComponent(g);
    _tourny_background.paintIcon(this, g, 0, 0);

    g.setFont(Utils.standartFont);
    Rectangle tsummary = new Rectangle(600, 30, 200, 200);
    Graphics gcopy = g.create(tsummary.x, tsummary.y, tsummary.width,
                              tsummary.height);
    gcopy.setColor(Color.GREEN);
    gcopy.drawString(_lobbyTournyModel.getName(), 15, 10);
    gcopy.drawString(_lobbyTournyModel.getDate() + "", 10, 30);
    gcopy.drawString("Currently " + _lobbyTournyModel.getStateString(), 5, 50);
    gcopy.dispose();

    Rectangle tdetails = new Rectangle(10, 150, 200, 500);
    gcopy = g.create(tdetails.x, tdetails.y, tdetails.width, tdetails.height);
    gcopy.setColor(Color.GREEN);
    gcopy.drawString("Tournament Name: " + _lobbyTournyModel.getName(), 10, 25);
    gcopy.drawString("Date           : " + _lobbyTournyModel.getDate(), 10, 40);
    gcopy.drawString("Current State  : " + _lobbyTournyModel.getStateString(),
                     10, 55);
    gcopy.dispose();

  }

  public void tournyMessageReceived(Object obj) {
    _cat.fine("Actions=" + obj);
    if (obj instanceof TournyEvent) {
      TournyEvent actions = (TournyEvent) obj;
      _lobbyTournyModel.update(actions);

      // PLAYERS REG/WINNERS/PLAYING
      String pstr = actions.get("player");
      if (pstr != null) {
        _owner.resetRegPlayer();
        String[] players = pstr.split("\\|");
        _cat.fine("Players= " + actions.get("player"));
        for (int i = 0; players != null && i < players.length; i++) {
          _cat.fine("Adding " + players[i]);
          _owner.addRegPlayer((i + 1) + "", players[i], "");
          if (players[i].equals(_owner._serverProxy._name)) {
            _cat.fine("Player is present");
            _present = true;
          }
        }
      }
      setButtonState(actions);
    }
    else if (obj instanceof ResponseTournyDetail) {
      ResponseTournyDetail rtd = (ResponseTournyDetail) obj;
      String[] games = rtd.getTournyGameEvent();
      _owner.resetPitch();
      // PITCHES
      if (games != null) {
        GameEvent ge = new GameEvent();
        for (int i = 1; i < games.length; i++) {
          ge.init(games[i]);
          _cat.fine(ge.toString());
          _owner.addPitch(ge.getGameId() + "", "" + ge.get("name"), "", ge);
        }
      }
      tournyMessageReceived(new TournyEvent(rtd.getTournyEvent()));
    }
    else if (obj instanceof ResponseInt) {
      ResponseInt ri = (ResponseInt) obj;
      if (ri.responseName() == Response.R_TOURNYREGISTER) {
        _owner._regButton.setEnabled(false);
      }
    }
    else if (obj instanceof ResponseTournyMyTable) {
      ResponseTournyMyTable ri = (ResponseTournyMyTable) obj;
      // open the pitch
      if (ri.getResult() != 1) {
        JOptionPane.showMessageDialog(this,
            "Unable to goto your table.\nThere is no table allocated to you.",
                                      "ERROR", JOptionPane.ERROR_MESSAGE);
      }
      LobbyCricketModel lcm = new LobbyCricketModel();
      lcm.setName(ri.getGameId());
      ClientRoom cr = new ClientRoom(_owner._serverProxy, lcm, _owner);
    }
    else {
      throw new IllegalStateException("Unrecognized command " + obj);
    }
  }

  public void setButtonState(TournyEvent te) {
    switch (te.state()) {
      case TournyInterface.NOEXIST:
      case TournyInterface.CREATED:
      case TournyInterface.DECL:
        _owner._regButton.setEnabled(false);
        _owner._myPitchButton.setEnabled(false);
        _owner._openPitchButton.setEnabled(false);
        break;
      case TournyInterface.REG:
        if (_present) {
          _owner._regButton.setEnabled(false);
        }
        else {
          _owner._regButton.setEnabled(true);
        }

        break;
      case TournyInterface.JOIN:
      case TournyInterface.START:
      case TournyInterface.RUNNING:
        _owner._myPitchButton.setEnabled(true);
        _owner._openPitchButton.setEnabled(true);
        break;
      case TournyInterface.END:
      case TournyInterface.FINISH:
      case TournyInterface.CLEAR:
        _owner._regButton.setEnabled(false);
        _owner._myPitchButton.setEnabled(false);
        _owner._openPitchButton.setEnabled(false);
        break;
      default:
        break;
    }
    repaint();
  }
}
