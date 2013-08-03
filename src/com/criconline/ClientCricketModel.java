package com.criconline;

import com.cricket.common.db.DBCricketPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.*;

import com.criconline.resources.Bundle;
import com.criconline.actions.Action;
import com.criconline.actions.*;
import com.criconline.models.*;
import com.criconline.pitch.PitchSkin;
import com.criconline.server.EventGenerator;

import com.cricket.common.message.GameEvent;
import com.cricket.mmog.PlayerStatus;
import com.cricket.mmog.cric.util.ActionConstants;
import java.awt.event.MouseEvent;
import com.cricket.mmog.cric.util.Trajectory;
import com.cricket.mmog.cric.util.Coordinate;
import com.criconline.anim.AnimationManager;
import java.util.Enumeration;
import com.criconline.anim.Animation;
import com.criconline.anim.AnimationEvent;
import com.criconline.anim.Gse;
import com.criconline.anim.BallView;
import com.criconline.anim.MiscAnim;
import com.criconline.anim.Speaks;
import com.criconline.util.ScoreBoardPopup;
import com.criconline.pitch.ImageStrip;
import com.criconline.pitch.ImageStripData;
import com.criconline.anim.AnimationConstants;
import com.cricket.mmog.cric.util.MoveUtils;

import com.cricket.common.db.DBPlayer;

public class ClientCricketModel extends CricketModel implements Painter,
    AnimationConstants {

  static Logger _cat = Logger.getLogger(ClientCricketModel.class.getName());


  /** Root component (owner) */
  public CricketController _owner;


  /** ClientPoker View of MVC struct */
  //private ClientCricketView _circketView = null;
  /** Room skin */
  protected PitchSkin _skin;

  /** Players */
  protected ClientPlayerController[] _teamA;
  protected ClientPlayerController[] _teamB;

  protected ClientPlayerController _runner;
  protected BallView _ball;
  protected MiscAnim _misc;
  protected Speaks _speaks;
  ImageIcon _infoBar1, _infoBar2, _groundSummary, _scoreBoard, _f1, _k1, _b1;
  Animation _aggression_bar, _reaction_bar, _confidence_bar;
  private ImageIcon _chair_l, _chair_r;

  /** Bottom panel - user input and output */
  protected BottomPanel _bottomPanel;

  /** player model */
  public PlayerModel _thisPlayerModel;
  public DBCricketPlayer _thisPlayerDB;


  /** if we wait for server response on SIT_IN action - waitRespone == true */
  private boolean waiting_for_join_response = false;
  private boolean proceeded = false;


  /** check for tournament games */
  private boolean isTournamentGame;
  private boolean isTournamentRunning;

  AnimationManager _am;
  ScoreBoardPopup _popup;

  public ClientCricketModel() {}

  public ClientCricketModel(CricketModel cricketModel, PitchSkin skin,
                            CricketController owner, BottomPanel bottomPanel) {
    super(cricketModel);
    _skin = skin;
    _owner = owner;
    _bottomPanel = bottomPanel;
    _am = AnimationManager.instance(_gameId);
    _am.setCricketController(_owner);

    //	Players at the desk
    PlayerModel battersModels[] = cricketModel.getBatters();
    PlayerModel fieldersModels[] = cricketModel.getFielders();

    _team_size = cricketModel.getTeamSize();
    _am.resetAnimators();
    _am.addAnimator(_bottomPanel);

    _speaks = new Speaks(owner, _skin, _am);
    _am.addAnimator(_speaks);

    _misc = new MiscAnim(owner, _skin, _am);
    _am.addAnimator(_misc);

    _ball = new BallView(owner, _skin, _am);
    _am.addAnimator(_ball);

    //_infoBar1, _infoBar2, _groundSummary, _scoreBoard, _f1, _k1, _b1;
    _infoBar1 = Utils.getIcon("images/info_bar1.png");
    _infoBar2 = Utils.getIcon("images/info_bar2.png");
    _groundSummary = Utils.getIcon("images/ground_summary.png");
    _scoreBoard = Utils.getIcon("images/score_board.png");
    _f1 = Utils.getIcon("images/f1.png");
    _k1 = Utils.getIcon("images/k1.png");
    _b1 = Utils.getIcon("images/b1.png");
    _aggression_bar = new Animation(ImageStripData.getGraphBar());
    _reaction_bar = new Animation(ImageStripData.getGraphBar());
    _confidence_bar = new Animation(ImageStripData.getGraphBar());
    _chair_l = Utils.getIcon("images/bench.png");
    _chair_r = Utils.getIcon("images/bench.png");


    _cat.finest("DELTA=" + _delta + ", Ts=" + _team_size + ", Bats=" + battersModels.length +
               ", Field=" + fieldersModels.length);
    _teamA = new ClientPlayerController[_team_size];
    for (int i = 0; i < battersModels.length; i++) {
      if (battersModels[i] != null) {
        _teamA[i] = new ClientPlayerController(battersModels[i], skin, owner,
                                               _am, _team_size, i);
      }
      else {
        _teamA[i] = new ClientPlayerController(new PlayerModel(i, _teamAname,
            _team_size), skin, owner, _am, _team_size, i);
      }
    }

    _teamB = new ClientPlayerController[_team_size];
    for (int i = 0; i < fieldersModels.length; i++) {
      if (fieldersModels[i] != null) {
        _teamB[i] = new ClientPlayerController(fieldersModels[i], skin, owner,
                                               _am, _team_size, i + _team_size);
      }
      else {
        _teamB[i] = new ClientPlayerController(new PlayerModel(i, _teamBname,
            _team_size), skin, owner, _am, _team_size, i + _team_size);
      }
    }

    PlayerModel runner = new PlayerModel( -1, "CPU", -1);
         runner.setPlayerStatus(new PlayerStatus(PlayerStatus.RUNNER));
         runner.setName("cpu");
         _runner = new ClientPlayerController(runner, skin, owner, _am, -1, -1);

//    _cat.finest("PAINTING " + this);
    _owner.tryPlayEffectRep(SoundManager.WAIT1);
    _am.camReset();
  }

  public int getTeamSize() {
    return _team_size;
  }

  public ClientPlayerController[] getClientBatters() {
    return _teamA;
  }

  public ClientPlayerController[] getClientFielders() {
    return _teamB;
  }

  public ClientPlayerController getPlayer(String team, int pos) {
    if (_teamAname.equals(team)) {
      return _teamA[pos];
    }
    else if (_teamBname.equals(team)) {
      return _teamB[pos];
    }
    else {
      throw new IllegalStateException("Unknown team=" + team);
    }
  }

  public void paint(JComponent c, Graphics g) {
    //_cat.finest("ClientCricketModel: paint start");
    //_cat.finest("PAINTING " + this);
    if (_game_state.isPlaying()) {
      _runner.paint(_owner, g);
    }


    for (int i = 0; i < _teamB.length; i++) {
      if (_teamB[i] == null) {
        continue;
      }
      _teamB[i].paint(_owner, g);
    }
    for (int i = 0; i < _teamA.length; i++) {
      if (_teamA[i] == null) {
        continue;
      }
      _teamA[i].paint(_owner, g);
    }

    //paint ground summary
    Graphics gcopy_gs = g.create(5, 20, _groundSummary.getIconWidth(),
                                 _groundSummary.getIconHeight());
    _groundSummary.paintIcon(_owner, gcopy_gs, 0, 0);
    int scaling = 10; //1400/_groundSummary.getIconWidth();
    int y_shift = 47;
    int x_shift = 22;
    /**_f1.paintIcon(_owner, gcopy_gs, _wk.getPos().x / scaling + x_shift,
                  _wk.getPos().y / scaling + y_shift);**/
    for (int i = 0; i < _teamB.length; i++) {
      if (_teamB[i] == null) {
        continue;
      }
      if (_teamB[i]._playerModel.isBats()) {
        _k1.paintIcon(_owner, gcopy_gs,
                      _teamB[i].getPos().x / scaling + x_shift,
                      _teamB[i].getPos().y / scaling + y_shift);
      }
      else if (_teamB[i]._playerModel.isBowl()) {
        _b1.paintIcon(_owner, gcopy_gs,
                      _teamB[i].getPos().x / scaling + x_shift,
                      _teamB[i].getPos().y / scaling + y_shift);
      }
      else if (_teamB[i]._playerModel.isFielder()) {
        _f1.paintIcon(_owner, gcopy_gs,
                      _teamB[i].getPos().x / scaling + x_shift,
                      _teamB[i].getPos().y / scaling + y_shift);
      }
    }
    for (int i = 0; i < _teamA.length; i++) {
      if (_teamA[i] == null) {
        continue;
      }
      if (_teamA[i]._playerModel.isBats()) {
        _k1.paintIcon(_owner, gcopy_gs,
                      _teamA[i].getPos().x / scaling + x_shift,
                      _teamA[i].getPos().y / scaling + y_shift);
      }
      else if (_teamA[i]._playerModel.isBowl()) {
        _b1.paintIcon(_owner, gcopy_gs,
                      _teamA[i].getPos().x / scaling + x_shift,
                      _teamA[i].getPos().y / scaling + y_shift);
      }
      else if (_teamA[i]._playerModel.isFielder()) {
        _f1.paintIcon(_owner, gcopy_gs,
                      _teamA[i].getPos().x / scaling + x_shift,
                      _teamA[i].getPos().y / scaling + y_shift);
      }
    }
    gcopy_gs.dispose();

    if (_thisPlayerDB != null) {
      int conf =0;
      if(_thisPlayerDB.runs> 0){
        conf = (_thisPlayerDB.sixes * 60) / _thisPlayerDB.runs;
        conf = conf > 10 ? 10 : conf;
      }
      int aggr = (_thisPlayerDB.bagression + _thisPlayerDB.fagression +
                  _thisPlayerDB.kagression) / 3;
      aggr = aggr > 10 ? 10 : aggr;
      int react = (_thisPlayerDB.bagility + _thisPlayerDB.fagility +
                   _thisPlayerDB.kagility) / 150;
      react = react > 10 ? 10 : react;

      //_cat.finest("Conf=" + conf + ", aggr=" + aggr + ", react=" + react);

      g.setColor(Color.RED);
      g.setFont(Utils.boldFont);

      g.drawString("     " + _thisPlayerDB.getDispName(), 0, 190);
      // display power graph
      _confidence_bar.setCurrentFrame(conf);
      _confidence_bar.paintIcon(c, g, 0, 200);
      //g.drawString("Confidence", 0, 210);
      _aggression_bar.setCurrentFrame(aggr);
      _aggression_bar.paintIcon(c, g, 0, 210);
      //g.drawString("Strength", 0, 230);
      _reaction_bar.setCurrentFrame(react);
      _reaction_bar.paintIcon(c, g, 0, 220);
      //g.drawString("Reaction", 0, 250);
      g.setFont(Utils.smallFont);

      g.drawString(" Runs......" + _thisPlayerDB.runs, 0, 240);
      g.drawString(" Fours....." + _thisPlayerDB.fours, 0, 250);
      g.drawString(" Sixes....." + _thisPlayerDB.sixes, 0, 260);
      g.drawString(" Wickets.." + _thisPlayerDB.wickets, 0, 270);
      g.drawString(" Catches.." + _thisPlayerDB.catches, 0, 280);
    }

    //score board
    Graphics gcopy_sb = g.create(825, 15, _scoreBoard.getIconWidth(),
                                 _scoreBoard.getIconHeight());
    _scoreBoard.paintIcon(_owner, gcopy_sb, 0, 0);
    gcopy_sb.setColor(Color.YELLOW);
    gcopy_sb.setFont(Utils.boldFont);
    gcopy_sb.drawString(_name, 60, 15);
    //gcopy_sb.setColor(Color.RED);
   // gcopy_sb.drawString("Prize Pool   $" + _pot, 50, 30);

    gcopy_sb.setColor(Color.LIGHT_GRAY);
    gcopy_sb.setFont(Utils.normalFont);
    // show batting team
    if (_battingTeam != null && _game_state.isPlaying()) {
      if (_game_state.isFirstInning()) {
        gcopy_sb.drawString(_battingTeam + " First Inning ", 40, 60);
      }
      else if (_game_state.isSecondInning()) {
        gcopy_sb.drawString(_battingTeam + " Second Inning ", 40, 60);
      }
      //if (_game_state.isPlaying()) {
      if (_battingTeam.equals(_teamAname)) {
        // team A is batting
        for (int i = 0; i < _teamA.length; i++) {
          if (_teamA[i] == null) {
            continue;
          }

          if (!_teamA[i].getPlayerName().equals("")) {
            if (_teamA[i]._playerModel._ballsPlayed != 0) {
              gcopy_sb.drawString(_teamA[i].getPlayerName() + "  " +
                                  _teamA[i]._playerModel._runs + "/" +
                                  _teamA[i]._playerModel._ballsPlayed, 45,
                                  80 + 15 * i);
            }
            else {
              gcopy_sb.drawString(_teamA[i].getPlayerName() + "  ___", 45,
                                  80 + 15 * i);

            }
          }
        }
      }
      else {
        for (int i = 0; i < _teamB.length; i++) {
          if (_teamB[i] == null) {
            continue;
          }
          if (!_teamB[i].getPlayerName().equals("")) {
            if (_teamB[i]._playerModel._ballsPlayed != 0) {
              gcopy_sb.drawString(_teamB[i].getPlayerName() + "  " +
                                  _teamB[i]._playerModel._runs + "/" +
                                  _teamB[i]._playerModel._ballsPlayed, 45,
                                  80 + 15 * i);
            }
            else {
              gcopy_sb.drawString(_teamB[i].getPlayerName() + "  ___", 45,
                                  80 + 15 * i);
            }
          }
        }
      }
      //}
    }
    gcopy_sb.dispose();

    // paint the batting team and the score
    g.setFont(Utils.bigFont);
    Graphics gcopy = g.create(0, 540, 1000, 50);
    _infoBar2.paintIcon(_owner, gcopy, _infoBar1.getIconWidth() - 10, 0);
    gcopy.setColor(Color.WHITE);
    _infoBar1.paintIcon(_owner, gcopy, 0, 0);
    if (_battingTeam != null) {
      gcopy.drawString(_battingTeam + " " + _runs + "/" + _balls, 5, 22);
    }
    gcopy.dispose();

    if (_game_state.isPlaying() && _battingTeam != null) {
      //_cat.finest("ClientCricketModel: paint end");
      _ball.paint(_owner, g);
    }
    _misc.paint(_owner, g);
    _speaks.paint(_owner, g);
    _chair_r.paintIcon(c, g, 0, SCREEN_HEIGHT - _chair_r.getIconHeight());
    _chair_l.paintIcon(c, g, SCREEN_WIDTH - _chair_l.getIconWidth(),
                       SCREEN_HEIGHT - _chair_l.getIconHeight());

  }

  public void mouseMoved(int mouseX, int mouseY) {
    boolean procceded = false;
    //_cat.finest("MOUSE over ...x=" + mouseX + ", y=" + mouseY + " bat=" +
    //          _teamA.length);

    if (_game_state.isPlaying() &&
        _owner._cricketView._umpire.getBounds().contains(mouseX, mouseY)) {
      //_popup = new ScoreBoardPopup(this, new Point(0, 0));
    }
    else if (_popup != null) {
      //_popup.update(this);
      //_popup.setVisible(true);
    }

    for (int i = 0; i < _teamA.length; i++) {
      if (_teamA[i] != null) {
        if (_teamA[i]._playerView.getBounds().contains(mouseX, mouseY)) {
          procceded = _teamA[i].mouseOver(mouseX, mouseY);
        }
      }
    }

    for (int i = 0; i < _teamB.length; i++) {
      if (_teamB[i] != null) {
        if (_teamB[i]._playerView.getBounds().contains(mouseX, mouseY)) {
          procceded = _teamB[i].mouseOver(mouseX, mouseY);
        }
      }
    }

  }

  public void mouseClicked(MouseEvent e) {
    int mouseX = e.getX();
    int mouseY = e.getY();
    _cat.finest("MOUSE clicked ...x=" + mouseX + ", y=" + mouseY + " bat=" +
               _teamA.length);
    //int playerNo = _owner.getPlayerNo();
    //new Exception().printStackTrace();
    
    if (_game_type.isMadness() && _delta > 0){
           JOptionPane.showMessageDialog(_owner,
                                         "Please, join the match at the designated time.", "INFO",
                                 JOptionPane.INFORMATION_MESSAGE);
    
           return;
      
     }

    for (int i = 0; i < _teamA.length; i++) {
      if (_teamA[i] == null) { // || _batters[i]._player_status.isJoinRequested()) {
        continue;
      }
      //_cat.finest("Batsman Bounds = " + _teamA[i]._playerView.getBounds());
      if (_teamA[i]._playerView.getBounds().contains(mouseX, mouseY)
          //&& i != playerNo
          ) {
        // --- proced mouse click on player i
        _cat.finest("Mouse clicked on " + i);
        if (!e.isPopupTrigger()) {
          if (_thisPlayerModel == null) {
            if (_teamA[i]._playerModel.isPreJoin() &&
                !waiting_for_join_response) {
                if (_game_type.isMadness()){
                    boolean exists=false;
                    //_cat.finest("Name=" + _owner._eventGen._name);
                    for (int k=0;k<_teamAInvited.length;k++){
                        //_cat.finest("Team=" + _teamAInvited[k]);
                        if (_owner._eventGen._name.equals(_teamAInvited[k])){
                            exists=true;
                            break;
                        }
                    } 
                    if (!exists){
                        JOptionPane.showMessageDialog(_owner,
                                                      "Please, join the right team.", "INFO",
                                              JOptionPane.INFORMATION_MESSAGE);
                        
                        return;  
                    }
                }
              _cat.finest("Inviting to seat pos =" + i + " team=" + _teamAname);
              _teamA[i].setShow(false);
              _teamA[i]._playerView.refresh();
              _teamA[i].setState(PlayerStatus.JOIN_REQUESTED);
              setWaitingForResponse(_bottomPanel.inviteToSeat(i, _teamAname, 0));
            }
          }
          else if (_thisPlayerModel.getPlayerStatus().isActive()) {
            _teamA[i]._playerView.invertSpeak();
            _cat.info("Invite to chat player #" + i);
          }
          break;
        }
        else {
          _cat.finest("Popup ");
          proceeded = true;
          //procedPopUp(i, _owner._clientRoom, _batters[i]+  e);
          break;
        }
      }
    }

    //playerNo -= _team_size;

    for (int i = 0; i < _teamB.length; i++) {
      if (_teamB[i] == null || proceeded == true) {
        continue;
      }

      //_cat.finest("Fielders Bounds = " + _teamB[i]._playerView.getBounds());
      if (_teamB[i]._playerView.getBounds().contains(mouseX, mouseY)
          //&&  i != playerNo
          ) {
        // --- proced mouse click on player i
        _cat.finest("Mouse clicked on " + i);
        if (!e.isPopupTrigger()) {
          if (_thisPlayerModel == null) {
            if (_teamB[i]._playerModel.isPreJoin() &&
                !waiting_for_join_response) {
                if (_game_type.isMadness()){
                    boolean exists=false;
                    //_cat.finest("Name=" + _owner._eventGen._name);
                    for (int k=0;k<_teamBInvited.length;k++){
                        //_cat.finest("Team=" + _teamBInvited[k]);
                        if (_owner._eventGen._name.equals(_teamBInvited[k])){
                            exists=true;
                            break;
                        }
                    } 
                    if (!exists){
                        JOptionPane.showMessageDialog(_owner,
                                                      "Please, join the right team.", "INFO",
                                              JOptionPane.INFORMATION_MESSAGE);
                        
                        return;  
                    }
                }
              _cat.finest("Inviting to seat pos =" + i + " team=" + _teamAname);
              _teamB[i].setShow(false);
              _teamB[i]._playerView.refresh();
              setWaitingForResponse(_bottomPanel.inviteToSeat(i, _teamBname, 0));
            }
          }
          else if (_thisPlayerModel.getPlayerStatus().isActive()) {
            _teamB[i]._playerView.invertSpeak();
            _cat.info("Invite to chat player #" + i);
          }
          break;
        }
        else {
          _cat.finest("Popup ");
          proceeded = true;
          //procedPopUp(i, _owner._clientRoom, _fielders[i]+  e);
          break;
        }
      }
    }
  }


  /* ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||| */
  /* |||||||||||||||||||||||||||||||| Action reaction |||||||||||||||||||||||||||| */
  /* ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||| */
  /**
   * move some of pot chips to some player. BettingAction.Bet - how many bax move;
   * BettingAction.Target - who destination of chips
   */

  public String[] step1(String s) {
    String[] str = s.split("`");
    return str;
  }

  public void step2(String s) {
    String[] s1 = s.split("\\|");
    if (s1.length != 0) {
      _cat.finest("Excep|" + s1[0] + "|");
      int a = Integer.parseInt(s1[0]);
      step3(s1[1], a);
    }
  }

  public void step3(String s, int a) {
    String[] s2 = s.split("'");
    int b = a;
  }


//	============= HandID magic =============

  public void setUpdateHandId(int newHandId) {
    //Logger.log(Logger.LOG_HALT,	"  ==[ new hand: " + newHandId + " ]==");
    _oldGameRunId =  getGameRunId();
    setGameRunId(newHandId);
    refreshHanddId();
  }

  public void refreshHanddId() {
    _owner.repaint(0, 0, 100, 40);
  }

  public long getOldGameId() {
    return _oldGameRunId;
  }

  public void setOldGameId(long old_GameId) {
    _oldGameRunId = old_GameId;
  }

  private static Rectangle handIdBounds = new Rectangle(0, 0, 130, 30);

  public Rectangle getHandIdBounds() {
    return handIdBounds;
  }

  private static Rectangle potBounds = new Rectangle(950, 0, 130, 30);

  public Rectangle getPotBounds() {
    return potBounds;
  }


//============= HandID magic =============

  public boolean isWaitingForResponse() {
    return waiting_for_join_response;
  }

  public void setWaitingForResponse(boolean b) {
    waiting_for_join_response = b;
    proceeded = b;
  }

  public void placeOccupied(Action action) {
    _cat.warning("Place " + action.getPosition() + " is occupied");
    setWaitingForResponse(false);
  }


  /**
   * @return
   */

  public boolean isTournamentGame() {
    return isTournamentGame;
  }

  class ListWithPoster implements List {
    List list;
    ServerMessagesListener listener;

    public ListWithPoster(ServerMessagesListener listener) {
      list = new Vector();
      this.listener = listener;
    }

    public int size() {
      return list.size();
    }

    public void clear() {
      boolean wasEmpty = list.isEmpty();
      list.clear();
      check();
    }

    public boolean isEmpty() {
      return list.isEmpty();
    }

    public Object[] toArray() {
      return list.toArray();
    }

    public Object get(int index) {
      return list.get(index);
    }

    public Object remove(int index) {
      Object object = list.remove(index);
      check();
      return object;
    }

    public void add(int index, Object element) {
      list.add(index, element);
    }

    public int indexOf(Object o) {
      return list.indexOf(o);
    }

    public int lastIndexOf(Object o) {
      return list.lastIndexOf(o);
    }

    public boolean add(Object o) {
      return list.add(o);
    }

    public boolean contains(Object o) {
      return list.contains(o);
    }

    public boolean remove(Object o) {
      boolean b = list.remove(o);
      check();
      return b;
    }

    public boolean addAll(int index, Collection c) {
      return list.addAll(index, c);
    }

    public boolean addAll(Collection c) {
      return list.addAll(c);
    }

    public boolean containsAll(Collection c) {
      return list.containsAll(c);
    }

    public boolean removeAll(Collection c) {
      boolean b = list.removeAll(c);
      return b;
    }

    public boolean retainAll(Collection c) {
      boolean b = list.retainAll(c);
      check();
      return false;
    }

    public Iterator iterator() {
      return list.iterator();
    }

    public List subList(int fromIndex, int toIndex) {
      return list.subList(fromIndex, toIndex);
    }

    public ListIterator listIterator() {
      return list.listIterator();
    }

    public ListIterator listIterator(int index) {
      return list.listIterator(index);
    }

    public Object set(int index, Object element) {
      return list.set(index, element);
    }

    public Object[] toArray(Object[] a) {
      return list.toArray(a);
    }

    private void check() {
      if (list.isEmpty()) {
        listener.serverMessageReceived(null);
      }
    }
  }

  public String getThisPlayerName() {
    return _thisPlayerModel == null ? "" : _thisPlayerModel.getName();
  }

  public void setThisPlayerModel(PlayerModel model) {
    _cat.finest("Setting Player Model " + model);
    _thisPlayerModel = model;
    if (model._team.equals(_teamAname)) {
      _teamA[model._pos].setState(model._player_status);
      _teamA[model._pos]._isMe = true;

    }
    else if (model._team.equals(_teamBname)) {
      _teamB[model._pos].setState(model._player_status);
      _teamB[model._pos]._isMe = true;

    }
    else {
      throw new IllegalStateException("Unknown team name " + model);
    }
    _am.setThisPlayerModel(_thisPlayerModel);
    _bottomPanel.chatPanelSetVisible(true);
  }

  public void setThisPlayerState(PlayerStatus state) {
    _thisPlayerModel.setPlayerStatus(state);
    _bottomPanel.updatePlayerState(state);
    if (state.isDisconnected()) {
      JOptionPane.showMessageDialog(_owner,
          "You are disconencted from the server,\n close the client and reconnect",
                                    "ERROR", JOptionPane.ERROR_MESSAGE);
      return;

    }

  }

  public void setPlayerModel(PlayerModel model) {
    String team = model._team;
    int pos = model._pos;
    if (model._team.equals(_teamAname)) {
      if (_teamA[pos] != null) {
        //_cat.finest("UPDATING ALL PLAYER MODEL " + _teamA[pos]);
        _teamA[pos].updateController(model, _am, _team_size, pos);
      }
      else {
        //_cat.finest("CREATING ALL PLAYER MODEL " + _teamA[pos]);
        _teamA[pos] = new ClientPlayerController(model, _skin, _owner, _am,
                                                 _team_size, pos);
      }
    }
    else if (model._team.equals(_teamBname)) {
      if (_teamB[pos] != null) {
        //_cat.finest("UPDATING ALL PLAYER MODEL " + _teamB[pos]);
        _teamB[pos].updateController(model, _am, _team_size, _team_size + pos);
      }
      else {
        //_cat.finest("CREATING ALL PLAYER MODEL " + _teamB[pos]);
        _teamB[pos] = new ClientPlayerController(model, _skin, _owner, _am,
                                                 _team_size, _team_size + pos);
      }
    }
    _owner.repaint();
  }

}
