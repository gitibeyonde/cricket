package com.criconline.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.text.MessageFormat;
import java.util.logging.Logger;

import com.criconline.SharedConstants;

import com.cricket.common.message.GameEvent;
import com.cricket.mmog.PlayerStatus;
import com.cricket.mmog.cric.GameState;
import com.cricket.mmog.GameType;
import com.cricket.mmog.cric.util.CricketConstants;
import java.util.Date;

public class CricketModel
    implements java.io.Serializable, CricketConstants {
  static Logger _cat = Logger.getLogger(CricketModel.class.getName());

  protected String _gameId = "";
  protected long _gameGRID = 0;
  protected String _name;
  /** Must view old hand id  */
  protected long _oldGameRunId = 0;
  protected PlayerModel[] _teamA, _teamB;
  public String _teamAname, _teamBname;
  public String[] _teamAInvited, _teamBInvited;
  public String _battingTeam, _fieldingTeam;
  protected int _team_size;
  protected GameType _game_type;
  protected GameState _game_state;
  public Date _tourny_date;
  public int _tourny_state, _delta;
  protected LinkedList _waiters = new LinkedList();
  //protected LobbyTableModel _lobbyTable = null;
  public int _runs = 0;
  public int _balls = 0;
  public int _bpp = 0;
  public double _pot = 0;
  public double _fees = 0;
  public double _buyin = 0;

  public CricketModel() {}

  public CricketModel(String id, long grid, String name, GameType gt, GameState gs, int ts, PlayerModel[] batters,
                      PlayerModel[] fielders, String ta, String tb,
                      int runs, int balls,  int bpp, double fees, double buyin, double pot
      ) {
    _gameId = id;
    _gameGRID = grid;
    _name = name;
    _game_type = gt;
    _game_state = gs;
    _team_size = ts;
    _teamA = batters;
    _teamB = fielders;
    _teamAname = ta;
    _teamBname = tb;
    _runs = runs;
    _balls = balls;
    _bpp = bpp;
    _fees = fees;
    _buyin = buyin;
    _pot = pot;

    //_cat.finest(this);
  }

  public CricketModel(CricketModel cm) {
    _gameId = cm._gameId;
    _gameGRID = cm._gameGRID;
    _name = cm._name;
    _game_type = cm._game_type;
    _game_state = cm._game_state;
    _team_size = cm._team_size;

    if (cm._teamB != null) {
      _teamB = new PlayerModel[cm._teamB.length];
      for (int i = 0; i < cm._teamB.length; i++) {
        if (cm._teamB[i] == null) {
          _teamB[i] = new PlayerModel(i, _teamBname, _team_size);
        }
        else {
          _teamB[i] = new PlayerModel(cm._teamB[i]);
        }
      }
    }
    if (cm._teamA != null) {
      _teamA = new PlayerModel[cm._teamA.length];
      for (int i = 0; i < cm._teamA.length; i++) {
        if (cm._teamA[i] == null) {
           _teamA[i]= new PlayerModel(i, _teamAname, _team_size);
        }
        else {
          _teamA[i] = new PlayerModel(cm._teamA[i]);
        }
      }
    }

    _teamAname = cm._teamAname;
    _teamBname = cm._teamBname;
    _teamAInvited = cm._teamAInvited;
    _teamBInvited = cm._teamBInvited;
    _tourny_date = cm._tourny_date;
    _tourny_state = cm._tourny_state;
    _delta = cm._delta;


    _runs = cm._runs;
    _balls = cm._balls;
    _fees = cm._fees;
    _bpp = cm._bpp;
    _buyin = cm._buyin;
    _pot = cm._pot;

    //_cat.finest(this);
  }

  public void updateModel(CricketModel cm){
    _gameId = cm._gameId;
    _gameGRID = cm._gameGRID;
    _name = cm._name;
    _game_type = cm._game_type;
    _game_state = cm._game_state;
    _team_size = cm._team_size;

    if (cm._teamB != null) {
      _teamB = new PlayerModel[cm._teamB.length];
      for (int i = 0; i < cm._teamB.length; i++) {
        if (cm._teamB[i] == null) {
          _teamB[i] = new PlayerModel(i, _teamBname, _team_size);
        }
        else {
          _teamB[i] = new PlayerModel(cm._teamB[i]);
        }
      }
    }
    if (cm._teamA != null) {
      _teamA = new PlayerModel[cm._teamA.length];
      for (int i = 0; i < cm._teamA.length; i++) {
        if (cm._teamA[i] == null) {
           _teamA[i]= new PlayerModel(i, _teamAname, _team_size);
        }
        else {
          _teamA[i] = new PlayerModel(cm._teamA[i]);
        }
      }
    }

    _teamAname = cm._teamAname;
    _teamBname = cm._teamBname;
    _teamAInvited = cm._teamAInvited;
    _teamBInvited = cm._teamBInvited;
    _tourny_date = cm._tourny_date;
    _tourny_state = cm._tourny_state;
    _delta = cm._delta;


    _runs = cm._runs;
    _balls = cm._balls;
    _bpp = cm._bpp;
    _fees = cm._fees;
    _buyin = cm._buyin;
    _pot = cm._pot;

    _cat.finest(this.toString());
  }

  public void setMatchParams(String tai, String tbi, Date td, int tstate, int delta){
    _teamAInvited = tai.split("'");
    _teamBInvited = tbi.split("'");
    _tourny_date = td;
    _tourny_state = tstate;
    _delta = delta;
    _cat.warning("DELTA=" + _delta);
  }


  public String getName(){
    return _name;
  }

  /**
   * Gets the current persistent hand id.
   */
  public final String getGameId() {
    return _gameId;
  }

  /**
   * Sets the current persistent hand id.
   */
  public final void setGameId(String gameId) {
    this._gameId = gameId;
  }

  public long getGameRunId(){
    return _gameGRID;
  }

  public void setGameRunId(long grid){
    _gameGRID = grid;
  }

  public double getRuns() {
    return _runs;
  }

  public void setRuns(int runs) {
    _runs = runs;
  }

  public double getBalls() {
    return _balls;
  }

  public void setBalls(int balls) {
    _balls = balls;
  }

  public GameState getGameState() {
    return _game_state;
  }

  public void setGameState(GameState state) {
    _game_state = state;
  }

  public GameType getGameType() {
    return _game_type;
  }

  public void setGameType(GameType type) {
    _game_type = type;
    _team_size = type.teamSize();
  }

  public int getTeamSize() {
    _cat.finest("Team size = " + _team_size);
    return _team_size;
  }

  public PlayerModel[] getBatters() {
    return _teamA;
  }

  public void setBatters(PlayerModel[] players) {
    _teamA = players;
  }

  public void setBatterAt(PlayerModel pm, int i) {
    _teamA[i] = pm;
  }

  public PlayerModel[] getFielders() {
    return _teamB;
  }

  public void setFielders(PlayerModel[] players) {
    _teamB = players;
  }

  public void setFielderAt(PlayerModel pm, int i) {
    _teamB[i] = pm;
  }

  /**
   * Gets the count of players filtered by state
   */
  /**public int getFilteredPlayerCount(int[] states) {
    int count = 0;
    for (int i = 0; i < _players.length; i++) {
      PlayerModel player = _players[i];
      if (player != null && player.filter(states)) {
        count++;
      }
    }
    return count;
     }**/

  /**
   * Gets the count of players at the pitch in a PLAY state.
   */
  public int getPlayingPlayerCount() {
    int count = 0;

    for (int i = 0; i < _teamA.length; i++) {
      PlayerModel player = _teamA[i];
      if (player != null && player.getPlayerStatus().isPlaying()) {
        count++;
      }
    }

    for (int i = 0; i < _teamB.length; i++) {
      PlayerModel player = _teamB[i];
      if (player != null && player.getPlayerStatus().isPlaying()) {
        count++;
      }
    }
    return count;
  }

  /**
   * Gets the count of players sitting at the table.
   */
  /**public int getSittingPlayerCount() {
    int count = 0;
    for (int i = 0; i < _players.length; i++) {
      PlayerModel player = _players[i];
      if (player != null && player.isSitting()) {
        count++;
     }
    }
    return count;
     }**/

  /**
   * Gets the count of players at the table.
   */
  /**public int getActivePlayerCount() {
    int count = 0;
    for (int i = 0; i < _players.length; i++) {
      PlayerModel player = _players[i];
      if (player != null) {
        count++;
//          if (player != null && player.getState() != PlayerModel.ABSENT) count++;
      }
    }
    return count;
     }**/

 /** public LobbyTableModel createLobbyTableModel(LobbyTableModel lobbyTable) {
    return new LobbyTableModel(lobbyTable);
  }***/

  public boolean hasPlayer(PlayerModel player) {
    if (_teamA != null) {
      for (int i = 0; i < _teamA.length; i++) {
        if (player.equals(_teamA[i])) {
          return true;
        }
      }
    }
    if (_teamB != null) {
      for (int i = 0; i < _teamB.length; i++) {
        if (player.equals(_teamB[i])) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Places the player in wating list for this cricket table.
   */
  /**REMpublic boolean addWaiter(PlayerModel player) {
    if (_waiters.indexOf(player) != -1) {
      return false;
    }
    if (hasPlayer(player)) {
      return false;
    }
    boolean result = _waiters.add(player);
    _lobbyTable.setWaiterCount(_waiters.size());
    return result;
     }**/

  /**
   * Removes the player from waiting list.
   */
  /**REMpublic void removeWaiter(PlayerModel player) {
    if (player == null) {
      return;
    }
    _waiters.remove(player);
    _lobbyTable.setWaiterCount(_waiters.size());
     }**/

  /**
   * Removes the first waiter for this cricket table.
   * @return PlayerModel - removed waiter.
   */
  /**REMpublic PlayerModel removeFirstWaiter() {
    if (_waiters.size() == 0) {
      return null;
    }
    PlayerModel result = (PlayerModel) _waiters.removeFirst();
    _lobbyTable.setWaiterCount(_waiters.size());
    return result;
     }**/

  /**
   * Clears waiting list.
   */
  /**REMpublic void clearWaitingList() {
    _waiters.clear();
    _lobbyTable.setWaiterCount(0);
     }

     public int getWaitingPlayerCount() {
    return _waiters.size();
     }**/


  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("CricketModel {").append(_gameId).
        append(", ").append(_gameGRID).append(", ").
        append(_team_size).append(", ").append(_game_type).
        append(", ").append(_game_state).append(",\n bats=");
    for (int i=0;i<_teamA.length;i++){
      s.append(_teamA[i]).append("\n");
    }
    s.append(", balls=");
    for (int i=0;i<_teamA.length;i++){
      s.append(_teamB[i]).append("\n");
    }
    s.append(" } ");
    return s.toString();
  }

}
