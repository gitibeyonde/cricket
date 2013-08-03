package com.criconline.server;

import com.cricket.common.message.Command;
import com.cricket.common.message.Response;
import com.cricket.common.message.ResponsePlayerShift;

import java.util.Vector;
import java.util.logging.Logger;
import java.util.LinkedList;
import com.cricket.mmog.GameType;
import com.cricket.mmog.PlayerStatus;
import com.cricket.common.message.GameEvent;
import com.cricket.mmog.cric.util.ActionConstants;
import com.criconline.actions.*;
import com.criconline.models.LobbyTableModel;
import com.criconline.models.LobbyCricketModel;
import com.criconline.models.CricketModel;
import com.criconline.models.PlayerModel;
import com.cricket.mmog.cric.util.Team;
import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.mmog.cric.GameState;
import com.criconline.SharedConstants;
import java.util.Date;
import com.agneya.util.Utils;

import com.cricket.common.db.DBPlayer;

import javax.swing.JOptionPane;


/**
 * This class converts the GameEvent to various actions in the client
 */
public class ActionFactory {
  static Logger _cat = Logger.getLogger(ActionFactory.class.getName());
  public double _fees, _buyin;
  public GameType _type;
  public String _id = "";
  public long _grid = -99;
  public String _gameName;
  public String _teamAname, _teamBname;
  public String _teamAInvited, _teamBInvited;
  public int _pos = -1;
  public String _team = "";
  public String _name = "";
  public GameState _state;
  protected Date tourny_date;
  int tourny_state, delta;
  public int _team_size = -1;
  private LinkedList _gameEventQueue;
  private LinkedList _actionQueue;
  public EventGenerator _eventGen;
  public double _runs, _balls;
  private long _startDelay;
  public LobbyTableModel _lobbyTable;
  public boolean _joined;
  public String _client[];
  public CricketModel _cricketModel;

  public ActionFactory(EventGenerator _eventGen, String tid) {
    _id = tid;
    _gameEventQueue = new LinkedList();
    _actionQueue = new LinkedList();
  }

  public String getGid() {
    return _id;
  }

  public void reset() {
    _cat.finest("RESETTING STATE");
    _pos = -1;
    _team = "";
    _id = "";
  }

  public void init(GameEvent ge) {
    _grid = ge.getGameRunId();
    _id = ge.getGameId();
    _type = new GameType(ge.getType());
    _cat.finest("INITIALIZED GE " + ge);
    // initialize table fundamentals if available
    _fees = Double.parseDouble(ge.get("fees"));
    _buyin = Double.parseDouble(ge.get("buyin"));
    _type = new GameType(ge.getType());
    _teamAname = ge.get("A");
    _teamBname = ge.get("B");
    _team_size = _type.teamSize();
    _state = new GameState(ge.getGameState());
    _cat.finest("Team size = " + _team_size + " Game State=" + _state);
    _lobbyTable = new LobbyCricketModel(ge);
  }

  public synchronized void addGameEvent(GameEvent ge) {
    _gameEventQueue.add(ge);
  }

  public GameEvent fetchGameEvent() {
    return (GameEvent) _gameEventQueue.removeFirst();
  }

  public synchronized void addAction(Object action) {
    _actionQueue.add(action);
  }

  public synchronized void addPriorityAction(Object action) {
    _actionQueue.addFirst(action);
  }

  public SimpleAction DELAY() {
    return new SimpleAction(ActionConstants.DELAY);
  }

  public SimpleAction UPDATE() {
    return new SimpleAction(ActionConstants.UPDATE);
  }

  public Object fetchAction() {
    try {
      Object oa = (Object) _actionQueue.getFirst();
      if (oa instanceof SimpleAction &&
          ((SimpleAction) oa).getId() == ActionConstants.DELAY) {
        if (_startDelay == -1) {
          _startDelay = System.currentTimeMillis();
          //_cat.finest("Start delay ..." + _startDelay);
        }

        else if ((System.currentTimeMillis() - _startDelay) > 1000) {
          _actionQueue.removeFirst(); //discard the delay
          //_cat.finest("...............End delay ..." + System.currentTimeMillis());
          _startDelay = -1;
        }
        //_cat.finest("...............null..............");
        return null;
      }
      else {
        //_cat.finest("...............No Delay ..." + System.currentTimeMillis());
        return (Object) _actionQueue.removeFirst();
      }
    }
    catch (java.util.NoSuchElementException e) {
      //ignore
    }
    return null;
  }

  public synchronized void processPlayerShift(ResponsePlayerShift rps) {
    StageAction wsa = new StageAction(ActionConstants.PLAYER_SHIFT, -1, "");
    wsa.setObject(rps);
    addAction(wsa);
  }

  public synchronized void processGameEvent(GameEvent ge, int event) {
    boolean lm_shown = false;
    if (ge == null) {
      return;
    }
    _cat.finest("GE = " + _grid + "," + ge);

    StageAction wsa = getWinners(ge);
    if (wsa != null) {
      addAction(wsa);
    }

    if (_grid != ge.getGameRunId()) {
      init(ge);
      Object pm = getCricketModel(ge);
      //* NEW GAME
       StageAction sa = new StageAction(ActionConstants.START_GAME);
      addAction(sa);
      addAction(pm);
      _cat.finest("NEW CRICKET MODEL =" + pm);
    }
    else {
      StageAction sa = new StageAction(ActionConstants.UPDATE_GAME);
      sa.setObject(getCricketModel(ge));
      addAction(sa);
    }

    // Get this players detail
    String cpd[] = getThisPlayer(ge);
    if (cpd != null) {
      PlayerModel plrm = new PlayerModel(cpd, 2);
      _cat.finest("UPDATING THIS PLAYER MODEL " + plrm);
      addAction(plrm);
    }

    _client = cpd;

    // Get all players detail
    Vector pv = getPlayers(ge);
    if (pv != null) {
      PlayerModel pma[] = new PlayerModel[pv.size()];
      for (int i = 0; i < pv.size(); i++) {
        pma[i] = new PlayerModel((String[]) pv.get(i), _team_size);
        _cat.finest("UPDATING ALL PLAYER MODEL ADDING = " + pma[i]);
      }
      addAction(pma);
    }

    StageAction sa = getPlayerStats(ge);
    if (sa != null) {
      addAction(sa);
    }

    if (event != Response.R_MOVE) { // if move response then update the cricket and player model
      return;
    }

    // Last move
    Object[] ba = getLastMove(ge);
    if (ba != null && !lm_shown) {
      for (int i = 0; i < ba.length; i++) {
        if (ba[i] instanceof SimpleAction) {
          SimpleAction sc = (SimpleAction) ba[i];
          if (sc.getId() == ActionConstants.BATS_CHANGE) {
            addAction(sc);
          }
        }
        else if (!(ba[i] instanceof LastMoveAction)) {
          //addPriorityAction(ba[i]);
          addAction(ba[i]);
        }
        else {
          addAction(ba[i]);
        }
      }
    }

    MoveRequestAction[] bra = getNextMove(ge);
    if (bra != null) {
      for (int i = 0; i < bra.length; i++) {
        addAction(bra[i]);
      }
    }


    _cat.finest("END actions processing " + _actionQueue.size());
  }


  ///////////////CRICKET MODEL////////////////////////
  public CricketModel getCricketModel(GameEvent ge) {
    _id = (String) ge.get("name");
    _grid = Integer.parseInt((String) ge.get("grid"));
    _gameName = ge.get("name");
    _type = new GameType(ge.getType());
    if (_type.isMadness()){
     _teamAInvited = (String)ge.get("TeamAPlayers");
     _teamBInvited = (String)ge.get("TeamBPlayers");
     tourny_date = Utils.getDateFromString((String)ge.get("date"));
     tourny_state = Integer.parseInt((String)ge.get("tstate"));
     delta = Integer.parseInt((String)ge.get("delta"));
     _cat.finest("DELTA=" + delta + ", " + _teamAInvited + _teamBInvited + ", " + tourny_date);
   }

    _state = new GameState(ge.getGameState());
    _teamAname = ge.get("A");
    _teamBname = ge.get("B");
    _team_size = _type.teamSize();
    int runs = Integer.parseInt((String) ge.get("runs"));
    int balls = Integer.parseInt((String) ge.get("balls"));
    int bpp = Integer.parseInt((String) ge.get("bpp"));
    double fees = Double.parseDouble((String) ge.get("fees"));
    double buyin = Double.parseDouble((String) ge.get("buyin"));
    double pot = Double.parseDouble((String) ge.get("pot"));

    _cat.finest("Team size=" + _team_size);
    String[][] batters = ge.getBattingPlayersDetails();
    String[][] fielders = ge.getFeildingPlayersDetails();

    PlayerModel[] fielding = new PlayerModel[_team_size];
    PlayerModel[] batting = new PlayerModel[_team_size];

    for (int i = 0; batters != null && i < batters.length; i++) {
      int pos = Integer.parseInt(batters[i][0]);
      batting[pos] = new PlayerModel(batters[i], _team_size);
    }
    for (int i = 0; fielders != null && i < fielders.length; i++) {
      int pos = Integer.parseInt(fielders[i][0]);
      fielding[pos] = new PlayerModel(fielders[i], _team_size);
    }

    CricketModel pm = new CricketModel(_id, _grid, _gameName, _type, _state,
                                       _team_size, batting, fielding,
                                       _teamAname, _teamBname, runs, balls, bpp,
                                       fees, buyin, pot);
    if(_type.isMadness()){
      pm.setMatchParams(_teamAInvited, _teamBInvited, tourny_date, tourny_state, delta);
    }
    return pm;
  }


////////////////////////PLAYER MODEL////////////////////////////////
  public Object[] getPlayerModels(GameEvent ge) {
    Object[] models = new Object[2];
    String[][] plrs = ge.getFeildingPlayersDetails();
    PlayerModel plm = null;
    SimpleAction plr_waits_dealer_passes = null;
    PlayerModel[] players = new PlayerModel[SharedConstants.MAX_PLAYER_COUNT];

    for (int i = 0; plrs != null && i < plrs.length; i++) {
      int pos = Integer.parseInt(plrs[i][0]);
      int status = Integer.parseInt(plrs[i][4]);

      players[pos] = new PlayerModel(plrs[i], _team_size);
      if (pos == _pos) {
        plm = new PlayerModel(plrs[i], _team_size);
      }
    }

    String[][] pts = ge.getPot();
    models[0] = plm;
    return models;
  }

  public InfoAction getInfoAction(GameEvent ge) {
    String pd[] = getThisPlayer(ge);
    double raise_amt = 0, bet_amount = 0;
    if (pd != null) {
      bet_amount = Double.parseDouble(pd[2]);
      //raise_amt = ge.getRaiseAmount();
    }
    String pots[][] = ge.getPot();

    //TODO integrate all the pots -- support for multinple pots...? -sn-
    double total_pot = 0;
    if (pots != null) {
      for (int k = 0; k < pots.length; k++) {
        total_pot += (Double.parseDouble(pots[k][1]));
      }
    }

    InfoAction ia = new InfoAction(bet_amount, total_pot, ge.getRake(),
                                   (raise_amt > 0) ? true : false, raise_amt);
    _cat.finest("Info Action, pot=" + total_pot);
    return ia;

  }


//////////////NEXT MOVE////////////////////////
  public MoveRequestAction[] getNextMove(GameEvent ge) {
    String tps = ge.get("tp");
    String[] tpa = null;
    int tpos = -1;
    String tteam = null;
    if (tps != null) {
      tpa = tps.split("\\|");
      tpos = Integer.parseInt(tpa[0]);
      tteam = tpa[1];
    }

    String nms = ge.getNextMoveString();
    _cat.finest("Next move = " + nms);
    String nm[][] = ge.getNextMove();
    if (nm == null) {
      return null;
    }
    int pos;
    String team;
    String name;
    int move;
    // Loop through all the moves
    Vector my_nm = new Vector();
    Vector your_nm = new Vector();

    // create the Bet Request Action
    for (int i = 0; i < nm.length; i++) {
      pos = Integer.parseInt(nm[i][0]);
      team = nm[i][1];
      name = nm[i][2];
      move = getAction(nm[i][3]);
      
      if (move==ActionConstants.WAIT && Integer.parseInt(nm[i][4]) < 0){
          _cat.warning("Join failed ");
          // THE tourny does not exists
          JOptionPane.showMessageDialog(_eventGen._owner,
                                        "Unable to seat, try the other team.", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
      }

      if (tps != null) {
        _cat.finest("Next move " + move + ", tpos=" + tpos + ", tteam= " + tteam +
                   ", pos=" + pos + ", team= " + team + ", joined=" + _joined);

        if (_joined && tpos == pos && tteam.equals(team) &&
            move != ActionConstants.UNKNOWN_ERROR &&
            move != ActionConstants.WAIT) {
          _name = name;
          my_nm.add(nm[i]);
        }
        else if (pos == 0 && team.equals("Pitch")) {
          my_nm.add(nm[i]);
        }
        else {
          your_nm.add(nm[i]);
        }
      }
      else {
        if (pos == 0 && team.equals("Pitch")) {
          my_nm.add(nm[i]);
        }
        else {
          your_nm.add(nm[i]);
        }
      }
    }

    int size = my_nm.size() + your_nm.size();

    if (size == 0) {
      _cat.finest("No next move");
      return null;
    }

    // Loop through all the moves
    int i = 0;

    Vector nmb = new Vector();

    for (; i < my_nm.size(); i++) {
      boolean mine;
      int mpos;
      String mteam;
      int actions;
      double amount;
      double amount_limit;

      mine = true;
      mpos = Integer.parseInt(nm[i][0]);
      mteam = nm[i][1];
      String[] nmm = (String[]) my_nm.get(i);
      actions = getAction(nmm[3]);
      _cat.finest(MoveRequestAction.actionToString(actions));
      if (nm[i][4].indexOf("-") < 1) {
        amount = Double.parseDouble(nm[i][4]);
        amount_limit = -99; //indicates no slider
      }
      else {
        String str = nm[i][4].split("-")[0];
        String end = nm[i][4].split("-")[1];
        amount = Double.parseDouble(str);
        //note: >0(range), ==0(potlimit), ==-1(nolimit)
        amount_limit = Double.parseDouble(end);
      }

      MoveRequestAction bra = new MoveRequestAction(ActionConstants.
          MOVE_REQUEST, mpos, mteam, i, mine, actions, amount, amount_limit);
      _cat.finest("Next Move = " + bra);
      nmb.add(bra);

    }
    for (int j = 0; i < size; i++, j++) {
      boolean mine;
      int mpos;
      String mteam;
      int actions;
      double amount;
      double amount_limit;

      mine = false;
      mpos = Integer.parseInt(nm[i][0]);
      mteam = nm[i][1];
      String[] nmm = (String[]) your_nm.get(j);
      actions = getAction(nmm[3]);
      _cat.finest(MoveRequestAction.actionToString(actions));
      if (nm[i][4].indexOf("-") < 1) {
        amount = Double.parseDouble(nm[i][4]);
        amount_limit = -99; //indicates no slider
      }
      else {
        String str = nm[i][4].split("-")[0];
        String end = nm[i][4].split("-")[1];
        amount = Double.parseDouble(str);
        //note: >0(range), ==0(potlimit), ==-1(nolimit)
        amount_limit = Double.parseDouble(end);
      }

      MoveRequestAction bra = new MoveRequestAction(ActionConstants.
          MOVE_REQUEST, mpos, mteam, j, mine, actions, amount, amount_limit);
      _cat.finest("Next Move = " + bra);
      nmb.add(bra);

    }

    return (MoveRequestAction[]) nmb.toArray(new MoveRequestAction[nmb.size()]);
  }


  /////////////////////LAST MOVE/////////////////////////////////
  public Object[] getLastMove(GameEvent ge) {
    String[][] fielders = ge.getFeildingPlayersDetails();
    String[][] batters = ge.getBattingPlayersDetails();
    Vector a = new Vector();
    String[][] lma = ge.getLastMove();
    //
    for (int l = 0; lma != null && l < lma.length; l++) {
      String lml[] = lma[l];
      _cat.finest("Last move = " + lml[0] + lml[1] + lml[2] + lml[3]);
      int pos = Integer.parseInt(lml[0]);
      String team = lml[1];
      String name = lml[2];
      int move = getAction(lml[3]);
      double amt = Double.parseDouble(lml[4]);
      _cat.finest("team=" + team + ", pos=" + pos + ", move= " + move +
                 ", name=" + name);
      _cat.finest("teamA=" + _teamAname + ", teamB=" + _teamBname);

      String lmp[] = null;
      for (int i = 0; batters != null && i < batters.length; i++) {
        _cat.finest(batters[i][2]);
        if (pos == Integer.parseInt(batters[i][0]) && team.equals(batters[i][1])) {
          _cat.info(" Batter " + batters[i][2] + " makes the last move");
          lmp = batters[i];
          break;
        }
      } // getting the last move player as lmp
      if (lmp == null) {
        for (int i = 0; fielders != null && i < fielders.length; i++) {
          _cat.finest(fielders[i][2]);
          if (pos == Integer.parseInt(fielders[i][0]) &&
              team.equals(fielders[i][1])) {
            _cat.info(" Fielder " + fielders[i][2] + " makes the last move");
            lmp = fielders[i];
            break;
          }
        } // getting the last move player as lmp
      }
      if (move == ActionConstants.PLAYER_JOIN) {
        if (_eventGen._name.equals(lml[2])) { // this player joins
          _pos = pos;
          _team = team;
          _joined = true;
          a.add(new PlayerJoinAction(pos, team, new PlayerModel(lmp, _team_size), true));
        }
        else {
          a.add(new PlayerJoinAction(pos, team, new PlayerModel(lmp, _team_size), false));
        }
        _cat.finest(lml[2] + " JOINED " + _eventGen._name + " this pos = " +
                   _pos);
      }
      else if (move == ActionConstants.BATS_CHANGE) {
        _cat.finest("Getting simple action = ");
        a.add(new SimpleAction(ActionConstants.BATS_CHANGE));
      }
      else if (move == ActionConstants.GAME_OVER) {
        _cat.finest("Getting simple action = ");
        a.add(new SimpleAction(ActionConstants.GAME_OVER));
      }
      else if (move == ActionConstants.INNING_OVER) {
        _cat.finest("Getting simple action = ");
        a.add(new SimpleAction(ActionConstants.INNING_OVER));
      }
      else if (move == ActionConstants.PLAYER_LEAVE) {
        a.add(new TableServerAction(ActionConstants.PLAYER_LEAVE, pos, team));
      }
      else if (move == ActionConstants.PLACE_OCCUPIED) {
        a.add(new ErrorAction(ActionConstants.PLACE_OCCUPIED, pos, ""));
      } // LAST MOVE -- NONE
      else { // ALL OTHER MOVES
        String mps = ge.get("md");
        MoveParams mp = new MoveParams(mps);
        // int id, int target, double bet, double pot, double rake, double chips, double amtAt Table
        LastMoveAction ba = new LastMoveAction(move, pos, team, mp,
                                               pos == this._pos);
        _cat.info("Last Move = " + ba + " pos " + pos);
        a.add(ba);
      }
    }
    return (Object[]) a.toArray(new Object[a.size()]);
  }


//TODO - Handle multiple winners tooo...
  public StageAction getWinners(GameEvent ge) {
    String[][] winner = ge.getWinner();
    if (winner == null) {
      return null;
    }
    _cat.finest("WINNERs = " + winner[0][0] + winner[0][1] + winner[0][2]);
    StageAction sa = new StageAction(ActionConstants.INNING_OVER);
    sa.setObject(winner);
    return sa;
  }

  public String[] getFeildingPlayers(GameEvent ge) {
    String pd[][] = ge.getFeildingPlayersDetails();
    for (int i = 0; pd != null && i < pd.length; i++) {
      if (Integer.parseInt(pd[i][0]) == _pos) { //        _cat.finest("Player Details" + pd[i]);
        return pd[i];
      }
    }
    return null;
  }

  public Vector getPlayers(GameEvent ge) {
    Vector v = new Vector();
    String pd[][] = ge.getBattingPlayersDetails();
    for (int i = 0; pd != null && i < pd.length; i++) {
      v.add(pd[i]);
    }
    pd = ge.getFeildingPlayersDetails();
    for (int i = 0; pd != null && i < pd.length; i++) {
      v.add(pd[i]);
    }
    return v;
  }

  public String[] getThisPlayer(GameEvent ge) {
    String pd[][] = ge.getBattingPlayersDetails();
    _cat.finest("Pos=" + _pos + ", Team=" + _team);
    for (int i = 0; pd != null && i < pd.length; i++) {
      //_cat.finest("Player Details" + pd[i]);
      if (Integer.parseInt(pd[i][0]) == _pos && pd[i][1].equals(_team)) {
        return pd[i];
      }
    }
    pd = ge.getFeildingPlayersDetails();
    for (int i = 0; pd != null && i < pd.length; i++) {
      //_cat.finest("Player Details" + pd[i]);
      if (Integer.parseInt(pd[i][0]) == _pos && pd[i][1].equals(_team)) {
        return pd[i];
      }
    }
    return null;
  }


  public StageAction getPlayerStats(GameEvent ge) {
    String ps = ge.getPlayerStats();
    _cat.finest("Getting players stat=" + ps);
    if (ps == null) {
      return null;
    }
    //DBPlayer db = new DBPlayer(ps, 1);
    new Exception().printStackTrace();
    StageAction sa = new StageAction(ActionConstants.PLAYER_STATS);
    //sa.setObject(db);
    new Exception().printStackTrace();
    return sa;
  }

  public static int getAction(String mov) {
    int mov_id;
    _cat.finest("getAction(String mov) = " + mov);
    if (mov.equals("join")) {
      mov_id = ActionConstants.PLAYER_JOIN;
    }
    else if (mov.equals("toss")) {
      mov_id = ActionConstants.TOSS;
    }
    else if (mov.equals("head")) {
      mov_id = ActionConstants.HEAD;
    }
    else if (mov.equals("tail")) {
      mov_id = ActionConstants.TAIL;
    }
    else if (mov.equals("fielding")) {
      mov_id = ActionConstants.FIELDING;
    }
    else if (mov.equals("batting")) {
      mov_id = ActionConstants.BATTING;
    }
    else if (mov.equals("leave")) {
      mov_id = ActionConstants.PLAYER_LEAVE;
    }
    else if (mov.equals("bat")) {
      mov_id = ActionConstants.BAT;
    }
    else if (mov.equals("bowl")) {
      mov_id = ActionConstants.BOWL;
    }
    else if (mov.equals("field")) {
      mov_id = ActionConstants.FIELD;
    }
    else if (mov.equals("start")) {
      mov_id = ActionConstants.START;
    }
    else if (mov.equals("BatsChange")) {
      mov_id = ActionConstants.BATS_CHANGE;
    }
    else if (mov.equals("InningOver")) {
      mov_id = ActionConstants.INNING_OVER;
    }
    else if (mov.equals("GameOver")) {
      mov_id = ActionConstants.GAME_OVER;
    }
    else if (mov.equals("wait")) {
      mov_id = ActionConstants.WAIT;
    }
    else if (mov.equals("none")) {
      mov_id = ActionConstants.PLAYER_NONE;
    }
    else {
      mov_id = ActionConstants.UNKNOWN_ERROR;
      throw new IllegalStateException("Unknown move " + mov);
    }
    return mov_id;
  }

  public static int getMove(int action) {
    _cat.finest("############################################# action = " +
               action);
    int mov_id;
    if (action == ActionConstants.PLAYER_JOIN) {
      mov_id = Command.M_JOIN;
    }
    else if (action == ActionConstants.START) {
      mov_id = Command.M_START;
    }
    else if (action == ActionConstants.TOSS) {
      mov_id = Command.M_TOSS;
    }
    else if (action == ActionConstants.HEAD) {
      mov_id = Command.M_HEAD;
    }
    else if (action == ActionConstants.TAIL) {
      mov_id = Command.M_TAIL;
    }
    else if (action == ActionConstants.BATTING) {
      mov_id = Command.M_BATTING;
    }
    else if (action == ActionConstants.FIELDING) {
      mov_id = Command.M_FIELDING;
    }
    else if (action == ActionConstants.BAT) {
      mov_id = Command.M_BAT;
    }
    else if (action == ActionConstants.BOWL) {
      mov_id = Command.M_BOWL;
    }
    else if (action == ActionConstants.FIELD) {
      mov_id = Command.M_FIELD;
    }

    else if (action == ActionConstants.PLAYER_LEAVE) {
      mov_id = Command.M_LEAVE;
    }
    else if (action == ActionConstants.PLAYER_SITIN) {
      mov_id = Command.M_JOIN;
    }
    else if (action == ActionConstants.PLAYER_SITOUT) {
      mov_id = Command.M_OPT_OUT;
    }
    else {
      mov_id = Command.M_NONE;
      throw new IllegalStateException("Unknown move " + action);
    }
    _cat.finest("move id = " + mov_id);
    return mov_id;
  }
}
