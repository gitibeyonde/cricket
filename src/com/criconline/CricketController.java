package com.criconline;

import com.cricket.common.db.DBCricketPlayer;

import com.criconline.models.LobbyCricketModel;
import com.criconline.models.CricketModel;
import com.criconline.models.LobbyTableModel;
import com.criconline.pitch.PitchSkin;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
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
import javax.swing.JOptionPane;
import com.criconline.util.MessagePopup;
import com.cricket.common.message.ResponsePlayerShift;
import com.criconline.anim.Animator;
import com.criconline.tts.Commentator;
import com.cricket.mmog.GameType;
import com.cricket.mmog.GameType;

import com.cricket.mmog.cric.util.MoveUtils;

import com.cricket.common.db.DBPlayer;

public class CricketController extends javax.swing.JPanel implements
    MouseMotionListener, MouseListener, ServerMessagesListener, ActionVisitor {
  static Logger _cat = Logger.getLogger(CricketController.class.getName());

  /** Model and View of a desk */
  protected ClientCricketModel _cricketModel = null;
  protected ClientCricketView _cricketView = null;
  /** Skin of this room */
  protected PitchSkin _skin = null;
  public JFrame _selPitch;

  /** Input/Output manipulation organs */
  public BottomPanel _bottomPanel = null;
  public MessagePanel _messagePanel = null;
  public Commentator _commentator = null;
  protected ResourceBundle _bundle;

  /** background */
  protected ImageIcon _background;

  public EventGenerator _eventGen;
  MoveBuffer _move_buffer;

  public CricketController(EventGenerator eventGen, PitchSkin skin,
                           BottomPanel bottomPanel, MessagePanel messagePanel,
                           String gid) {
    _bundle = Bundle.getBundle();
    _eventGen = eventGen;
    _skin = skin;
    _bottomPanel = bottomPanel;
    _messagePanel = messagePanel;
    _commentator = new Commentator();
    bottomPanel._cricketController = this;
    _move_buffer = MoveBuffer.instance(gid);
    _background = skin.getBackround();
    setSize(_background.getIconWidth(), _background.getIconHeight());
    setBorder(null);
    setOpaque(true);
    setLayout(null);
    setVisible(true);

    addMouseMotionListener(this);
    addMouseListener(this);
  }

  protected void setCricketModel(CricketModel cricModel) {
    //	Desk model & view
    long prevHandId = 0;
    if (_cricketModel != null) {
      prevHandId = _cricketModel.getGameRunId();
    }
    _cricketModel = new ClientCricketModel(cricModel, _skin, this, _bottomPanel);
    _cricketModel.setOldGameId(prevHandId);
    _cat.finest("oldHandId = " + _cricketModel.getOldGameId() + "newHandId = " +
               _cricketModel.getGameId());
    _cricketView = new ClientCricketView(_cricketModel, _skin);
    repaint(); // this will repaint the ground for the first time
  }

  public String getGid() {
    return _cricketModel.getGameId();
  }

  public void addCommentary(String html) {
    _messagePanel.addCommentary(html);
    _commentator.speak(html);
  }

  public void paintComponent(Graphics g) {
    //_cat.finest("CricketController paintComponent  start");
    super.paintComponent(g);

    Graphics scratchGraphics = g.create();
    try {
      if (_cricketView != null) {
        /** Draw background */
        _cricketView.paint(this, scratchGraphics);
      }
      if (_cricketModel != null) {
        /** Draw all room elements */
        _cricketModel.paint(this, scratchGraphics);
      }
    }
    finally {
      scratchGraphics.dispose();
    }

    //_cat.finest("CricketController paintComponent  end");
  }

  public void mouseDragged(MouseEvent e) {
    //_cat.finest("mouse");
    mouseMoved(e);
  }

  public void mousePressed(MouseEvent e) {
//    _cat.finest("mouse");
  }

  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      mouseClicked(e);
    }
  }

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {
    mouseMoved(new MouseEvent(this, -1, -1, -1, -999, -999, 0, false));
  }

  public void mouseMoved(MouseEvent e) {
    //_cat.finest("mouse x=" + e.getX() + ", y=" +  e.getY());
    if (_cricketModel == null || _cricketView == null) {
      return;
    }
    _cricketModel.mouseMoved(e.getX(), e.getY());
  }

  public void mouseClicked(MouseEvent e) {
//    _cat.finest("mouse");
    if (_cricketModel == null || _cricketView == null) {
      return;
    }
    _cricketModel.mouseClicked(e);
  }

  public String getGameId() {
    return _cricketModel.getGameId();
  }

  public GameType getGameType(){
    return _cricketModel.getGameType();
  }

  public PlayerModel getThisPlayer() {
    if (_cricketModel == null) {
      return null;
    }
    else {
      return _cricketModel._thisPlayerModel;
    }
  }

  public ClientPlayerController getPlayer(String team, int pos) {
    return _cricketModel.getPlayer(team, pos);
  }

  protected void tryPlayEffect(int effect) {
    if (effect >= 0 && _bottomPanel != null && !_bottomPanel.isMuting()) {
      SoundManager.playEffect(effect);
    }
  }

  protected void tryPlayEffectRep(int effect) {
    if (effect >= 0 && _bottomPanel != null && !_bottomPanel.isMuting()) {
      SoundManager.playEffectRepeatable(effect);
    }
  }

  public void tryExit() {
    _cat.info("tryExit");
  }

  public Dimension getPreferredSize() {
    return new Dimension(_background.getIconWidth(), _background.getIconHeight());
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public void serverMessageReceived(Object actions) {
    if (actions == null) {
      return;
    }
    _cat.finest("Actions=" + actions);
    handleNextEvent(actions);
  }

  protected void handleNextEvent(Object o) {
    if (o instanceof SimpleAction &&
        ((SimpleAction) o).getId() == ActionConstants.UPDATE) {
      if (_cricketModel != null) {
        //_cricketModel.update();
      }
      return;
    }
    if (o instanceof CricketModel) {
      _cat.finest("PP NEW CRICKET MODEL =" + o);
      setCricketModel((CricketModel) o);
      //updateTitle();
      return;
    }

    if (o instanceof PlayerModel) {
      _cat.finest("UPDATING THIS PLAYER MODEL " + o);
      _cricketModel.setThisPlayerModel((PlayerModel) o);
      return;
    }

    if (o instanceof PlayerModel[]) {
      //update the players
      PlayerModel[] v = (PlayerModel[]) o;
      for (int i = 0; i < v.length; i++) {
        _cricketModel.setPlayerModel(v[i]);
      }
      return;
    }
    com.criconline.actions.Action action = (com.criconline.actions.Action) o;
    if (_cricketModel != null) {
      action.handleAction(this);
    }
  }

  public void handleDefaultAction(Action action) {}

  public void handleErrorAction(ErrorAction action) {
    switch (action.getId()) {
      case ActionConstants.PLACE_OCCUPIED:
        _cricketModel.placeOccupied(action);
        break;
      case ActionConstants.UNSUFFICIENT_FUND:
        _cat.warning("Unsufficient funds ");
        JOptionPane.showMessageDialog(this, _bundle.getString("error.broke"),
                                      _bundle.getString("error"),
                                      JOptionPane.ERROR_MESSAGE);
        break;

    }
    ; handleDefaultAction(action);
  }

  public void handleTableServerAction(TableServerAction action) {
    switch (action.getId()) {
      case ActionConstants.PLAYER_JOIN:
        PlayerJoinAction pj = (PlayerJoinAction) action;
        _cat.finest("SETTING THIS PLAYER MODEL " + pj.getPlayer());
        if (pj.isMe()) {
          _cricketModel.setThisPlayerModel(pj.getPlayer());
        }
        _commentator.speak("Welcome " + pj.getPlayer().getDisplayName() +
                           " welcome to the cricket");
        break;
      default:
        _cat.finest("Unknown table server action " + action);

    }
  }

  public void handleStageAction(StageAction action) {
    _cat.finest("STAGE ACTIONS = " + action);
    switch (action.getId()) {
      case ActionConstants.UPDATE_GAME:
        _cat.finest("Updating games state " + action);
        CricketModel cm = (CricketModel) action.getObject();
        _cricketModel.setGameState(cm.getGameState());
        _cricketModel.setGameType(cm.getGameType());
        _cricketModel._runs = cm._runs;
        _cricketModel._balls = cm._balls;
        _cricketModel._pot = cm._pot;
        _cricketModel._battingTeam = cm._teamAname;
        _cricketModel._fieldingTeam = cm._teamBname;
        if (_cricketModel.getGameType().isMadness()){
            _cricketModel._tourny_state = cm._tourny_state;
            _cricketModel._delta = cm._delta;
        }
        break;
      case ActionConstants.BATS_CHANGE:
        _cat.finest("Bats Change=" + action);
        break;
      case ActionConstants.CHAT:
        _cat.finest("Chat received =" + action);
        _bottomPanel.appendUserChat((ChatAction) action);
        break;
      case ActionConstants.INNING_OVER:
        _cat.finest("Inning Over =" + action);
        String[][] winner = (String[][]) action.getObject();
        String winmesg = "<table width=200  bgcolor='#446530'><tr><td><font color='#FFFF00' size=5><p align=center><b>Winners</b></p>";
        winmesg += "<ol>";

        for (int i = 0; i < winner.length; i++) {
          winmesg += "<li><p>" + winner[i][0] + " wins " + winner[i][1] +
              " points</li>";
        }
        winmesg += "</ol></font></td></tr></table>";


        Thread t = new Thread( new MessagePopup(winmesg));
        t.start();
        break;
      case ActionConstants.PLAYER_SHIFT: // player shifts
        ResponsePlayerShift rps = (ResponsePlayerShift) action.getObject();
        _cricketModel._am.playerShift(rps.getType(), rps.getDx(), rps.getDy(), rps.getDz());
        break;
      case ActionConstants.PLAYER_STATS:
        DBPlayer db = (DBPlayer)action.getObject();
        _cat.finest("Rcvd stats = " + db);
        if (_cricketModel != null && db != null){
          _cricketModel._thisPlayerDB = new DBCricketPlayer( db.getDispName());
        }
        break;
      default:
    }
  }

  public void handleSimpleAction(SimpleAction action) {
    switch (action.getId()) {
      case ActionConstants.DECISION_TIMEOUT:
        break;
      default:
        _cat.info("       don't processing - " + action + " [" + action.getId() +
                  "|" + action.getType() + "|" + action.getPosition() + "]");
    }
    ; handleDefaultAction(action);
  }

  public void handleLastMoveAction(LastMoveAction action) {

    switch (action.getId()) {
      case ActionConstants.START:


        //_bottomPanel.normalizePanel();
        break;
      case ActionConstants.BOWL:
        _cat.finest("LAST ACTION=" + action);
        _move_buffer.setLastMove(AnimationConstants.BOWL, action);
        break;
      case ActionConstants.BAT:
        _cat.finest("LAST ACTION=" + action);
        _move_buffer.setLastMove(AnimationConstants.BATS, action);
        break;
      case ActionConstants.FIELD:
        _cat.finest("LAST ACTION=" + action);
        _move_buffer.setLastMove(AnimationConstants.FIELD, action);
        break;
      case ActionConstants.TOSS:
        _cat.finest("LAST ACTION=" + action);
        _move_buffer.setLastMove(AnimationConstants.TOSS, action);
        break;
      case ActionConstants.HEAD:
        _cat.finest("LAST ACTION=" + action);
        _move_buffer.setLastMove(AnimationConstants.HEAD, action);
        break;
      case ActionConstants.TAIL:
        _cat.finest("LAST ACTION=" + action);
        _move_buffer.setLastMove(AnimationConstants.TAIL, action);
        break;
      case ActionConstants.FIELDING:
        _cat.finest("LAST ACTION=" + action);
        _move_buffer.setLastMove(AnimationConstants.FIELDING, action);
        break;
      case ActionConstants.BATTING:
        _cat.finest("LAST ACTION=" + action);
        _move_buffer.setLastMove(AnimationConstants.BATTING, action);
        break;

      default:
        _cat.info("handleLastMoveAction()::default case  " + action);
        break;
    }
    _bottomPanel.appendUserChat(action);
    handleDefaultAction(action);
  }

  public void handleMoveRequestAction(MoveRequestAction action) {
    int action_button = action.getAction();
    boolean mine = action.mine();
    _cat.finest("NEXT MOVE=" + action);
    switch (action_button) {
      case ActionConstants.BAT:
        _move_buffer.setNextMove(mine, AnimationConstants.BATS, action);
        break;
      case ActionConstants.BOWL:
        _move_buffer.setNextMove(mine, AnimationConstants.BOWL, action);
        break;
      case ActionConstants.FIELD:
        _move_buffer.setNextMove(mine, AnimationConstants.FIELD, action);
        break;
      case ActionConstants.START:
        if (mine) {
          _bottomPanel.moveRequest(action);
        }
        break;
      case ActionConstants.TOSS:
        if (mine) {
          _bottomPanel.moveRequest(action);
        }
        break;
      case ActionConstants.HEAD:
        if (mine) {
          _bottomPanel.moveRequest(action);
        }
        break;
      case ActionConstants.TAIL:
        if (mine) {
          _bottomPanel.moveRequest(action);
        }
        break;
      case ActionConstants.FIELDING:
        if (mine) {
          _bottomPanel.moveRequest(action);
        }
        break;
      case ActionConstants.BATTING:
        if (mine) {
          _bottomPanel.moveRequest(action);
        }
        break;
      case ActionConstants.BATS_CHANGE:
        AnimationManager am;
        if (mine) {
          am = AnimationManager.instance(_cricketModel.getGameId());
          am.update(action, Gse.PITCH_CHANGE);
        }
        break;
      case ActionConstants.INNING_OVER:
        if (mine) {
          am = AnimationManager.instance(_cricketModel.getGameId());
          am.update(action, Gse.INNING_OVER);
        }
        break;
      case ActionConstants.GAME_OVER:
        if (mine) {
          am = AnimationManager.instance(_cricketModel.getGameId());
          am.update(action, Gse.GAME_OVER);
        }
        break;
        case ActionConstants.WAIT:
          _cat.info("WAIT rcvd--no impl");
        break;
      default:
        _cat.warning("Unknow move received " + action_button);
    }

    handleDefaultAction(action);
  }

  public boolean isFielderPresent(long pos){
    for (int i=0;i<_cricketModel._teamB.length;i++){
      if (_cricketModel._teamB[i]._playerModel._player_status.getPlayerType()==pos){
        return true;
      }
    }
    return false;
  }


}
