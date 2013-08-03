package com.criconline.models;

import com.criconline.Copyable;
import java.util.logging.Logger;
import com.cricket.common.message.GameEvent;
import com.cricket.mmog.GameType;
import com.criconline.Validable;
import com.cricket.mmog.cric.GameState;
import com.cricket.mmog.cric.util.CricketConstants;
import com.cricket.common.message.TournyEvent;
import java.util.Calendar;
import com.agneya.util.Utils;

import com.criconline.SharedConstants;

import java.util.Date;

/**
 * The model representing a pitch or tournament entry on the main lobby
 *
 * @author not attributable
 * @version 1.0
 */

public class LobbyTableModel
    implements java.io.Serializable, CricketConstants {
  static Logger _cat = Logger.getLogger(LobbyTableModel.class.getName());

  public int _total_games, _total_players;

  protected String name;
  protected GameType _gameType;
  public int _team_size = 0;
  protected int playerCount = 0;
  protected int gameLimitType = REGULAR;


  /** tournament game specific fields */
  protected int bpp = 0;
  protected double fee = 0;
  protected double prizePool;
  protected String roomSkinClassName = null;
  public String _teamAname, _teamBname;
  public String _teamAInvited, _teamBInvited;
  public int _fielding_size, _batting_size;
  protected GameState _game_state;
  protected int tourny_state;
  protected Date tourny_date;
  private Integer distrId = null;
  private int waiterCount = 0;
  private boolean isDeleted = false;
  private int userId;
  public String[][] _batters, _fielders;
  public String[] _waiters;

  public LobbyTableModel() {
  }


  public LobbyTableModel(GameEvent ge) {
    name = ge.get("name");
    _gameType = new GameType(ge.getType());
    if (_gameType.isMadness()){
      _teamAInvited = (String)ge.get("TeamAPlayers");
      _teamBInvited = (String)ge.get("TeamBPlayers");
      tourny_date = Utils.getDateFromString((String)ge.get("date"));
    }
    _game_state = new GameState(Integer.parseInt(ge.get("state")));
    tourny_state = Integer.parseInt(ge.get("state"));
    _teamAname = ge.get("A");
    _teamBname = ge.get("B");
    bpp = Integer.parseInt(ge.get("bpp"));
    //_team_size = ge.getGameId();
    _batters = ge.getFeildingPlayersDetails();
    _fielding_size  = _batters==null? 0 : _batters.length;
    _fielders = ge.getBattingPlayersDetails();
    _batting_size  = _fielders==null? 0 : _fielders.length;
    this.fee = 0;
    _cat.finest(this.toString());
  }

  public LobbyTableModel(TournyEvent ge) {
    _game_state = new GameState(1);
  }
  /**
   * Constuctor.
  */

  public LobbyTableModel(String name,
                         int gameType,
                         boolean playRealMoney,
                         int team_size,
                         int bpp,
                         double fee,
                         Integer distrId
                         ) {
  
    this.distrId = distrId;
    this.name = name;
    this._gameType = new GameType(gameType);
    this._team_size = team_size;
    this.fee = fee;
    this.bpp = bpp;
  }


  /**
   * Copies.
   */

  public LobbyTableModel(LobbyTableModel model) {
    distrId = model.distrId;
    name = model.name;
    _gameType = model._gameType;
    _team_size = model._team_size;
    playerCount = model.playerCount;
    gameLimitType = model.gameLimitType;
    fee = model.fee;
    bpp = model.bpp;
    prizePool = model.prizePool;
    roomSkinClassName = model.roomSkinClassName;
    _game_state = model._game_state;
    tourny_state = model.tourny_state;
    userId = model.userId;
    distrId = model.distrId;
    waiterCount = model.waiterCount;
    _fielders = model._fielders;
    _batters = model._batters;
  }


  public final String getName() {
    return name;
  }

  /**
   * Sets the table name.
   */
  public final void setName(String newValue) {
    name = newValue.trim();
  }

  /**
   * Gets the game type id for this table.
   * @hibernate.property
   *   update="false"
   *   insert="false"
   */
  public final GameType getGameType() {
    return _gameType;
  }

  /**
   * Sets the table id.
   */
  public final void setGameType(int newValue) {
    _gameType = new GameType(newValue);
  }

  /**
   * Gets real money flag.
   * @hibernate.property
   *   column="realMoney"
   */
  public final boolean isRealMoneyTable() {
    return _gameType.isReal();
  }


  /**
   * Gets the table max player count.
   * @hibernate.property
   *   column="maxPlayers"
   */
  public final int getTeamSize() {
    return _team_size;
  }

  public final int getPlayerCapacity() {
     return _team_size;
  }
  /**
   * Sets the table max player count.
   */
  public void setTeamSize(int newValue) {
    _team_size = newValue;
  }

  /**
   * Gets the count of players are plaing at the table.
   */

  public final int getPlayerCount() {
    return playerCount;
  }

  /**
   * Sets the count of players are plaing at the table.
   */
  public final void setPlayerCount(int playerCount) {
    this.playerCount = playerCount < 0 ? 0 : playerCount;
  }



  /**
   * Gets the limit type for this table.
   * @hibernate.property
   */
  public final int getGameLimitType() {
    return gameLimitType;
  }


  public final void setGameLimitType(int gameLimitType) {
    this.gameLimitType = gameLimitType;
  }

  public final int getBallsPerPlayer(){
    return bpp;
  }



  /**
   * Gets the money sum which player paid to lobby at start of tournament.
   * @hibernate.property
   */
  public final double getFee() {
    return fee;
  }


  /**
   * Sets the money sum which player paid to lobby at start of tournament.
   */
  public final void setFee(double newValue) {
    fee = newValue;
  }


  /**
   * Gets the prize pool of tournament.
   * @hibernate.property
   */
  public final double getPrizePool() {
    return prizePool;
  }

  /**
   * Sets the prize pool of tournament.
   */
  public final void setPrizePool(double newValue) {
    prizePool = newValue;
  }

  /**
   * Gets the class name of room skin.
   * @hibernate.property
   *   column="roomSkinClass"
   */
  public String getRoomSkinClassName() {
    return roomSkinClassName;
  }

  /**
   * Sets the class name of room skin.
   */
  public void setRoomSkinClassName(String newValue) {
    roomSkinClassName = newValue;
  }

  public final GameState getGameState() {
    return _game_state;
  }


  public final int getTournyState() {
    return tourny_state;
  }
  
  public final void setGameState(GameState newValue) {
    _game_state = newValue;
  }

  /**
   * Gets a tournament prize distribution id for this table.
   * @hibernate.property
   */
  public final Integer getDistrId() {
    return distrId;
  }

  /**
   * Sets the state of this poker table in a table server.
   */
  public final void setDistrId(Integer newValue) {
    distrId = newValue;
  }

  /**
   * Is this table a tournament poker table ?
   */
  public final boolean isTournamentGame() {
    return (gameLimitType == TOURNAMENT);
  }

  public final boolean isAcceptingPlayers(){
    return _game_state.isOpen();
  }

  public boolean equalsByFields(Object obj) {
    if (obj instanceof LobbyTableModel) {
      LobbyTableModel table = (LobbyTableModel) obj;
      return
          SharedConstants.equals(name, table.name) &&
          _gameType == table._gameType &&
          _team_size == table._team_size &&
          fee == table.fee; //&&
          //prizePool == table.prizePool &&
          //SharedConstants.equals(roomSkinClassName, table.roomSkinClassName) &&
          //state == table.state &&
          //SharedConstants.equals(distrId, table.distrId) &&
          //SharedConstants.equals(settings, table.settings);
    }
    else {
      return false;
    }
  }

  /**
   * Tables are equal when they table ids are equal.
   */
  public boolean equals(Object obj) {
    if (obj instanceof LobbyTableModel) {
      return SharedConstants.equals(name, ( (LobbyTableModel) obj).getName()); //id == ( (LobbyTableModel) obj).getId();
    }
    else {
      return false;
    }
  }



  /***private Object settings;


  public Object getSettings() {
    return settings;
  }

  public void setSettings(Object settings) {
    this.settings = settings;
    printSettings();
  }**/

  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
  }

  /**
   * Gets an user id
   * @hibernate.property
   */
  public final int getUserId() {
    return userId;
  }

  /**
   * Sets an user id.
   */
  public final void setUserId(int userId) {
    this.userId = userId;
  }

  /**
   * Gets a waiters list size for this table. Note that this field is not a
   * persistent field.
   */
  public final int getWaiterCount() {
    return waiterCount;
  }

  /**
   * Sets a waiters list size for this table. Note that this field is not a
   * persistent field.
   */
  public final void setWaiterCount(int waiterCount) {
    //Logger.printStackTraceToLog("waiter_count");
    //Logger.log("waiter_count", "" + waiterCount);
    if (waiterCount < 0) {
      waiterCount = 0;
    }
    this.waiterCount = waiterCount;
    //Logger.log("waiters", ">>>> " + name + " waiterCount = " + waiterCount);
  }


  public String gameLimitTypeToString() {
    switch (gameLimitType) {
      case CricketConstants.REGULAR:
        return "L";
      case CricketConstants.NO_LIMIT:
        return "NL";
      case CricketConstants.POT_LIMIT:
        return "PL";
      case CricketConstants.TOURNAMENT:
        return "T";
    }
    return "";
  }

  /***public int isValid() {
    return ( (Validable) settings).isValid(this);
  }***/

  public String[][] getFieldersDetails(){
    return _fielders;
  }

  public String[][] getBattersDetails(){
    return _batters;
  }
  public String[] getWaitersDetails(){
     return _waiters;
  }
  public String toString(){
    return  name;
  }

}

