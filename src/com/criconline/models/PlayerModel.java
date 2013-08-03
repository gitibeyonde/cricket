package com.criconline.models;

import java.text.MessageFormat;
import java.util.logging.Logger;
import com.cricket.mmog.PlayerStatus;
import com.cricket.mmog.cric.util.CricketConstants;

/**
 * Player model.
 */

public class PlayerModel
    implements java.io.Serializable, CricketConstants {
  static Logger _cat = Logger.getLogger(PlayerModel.class.getName());

  public String _name = "";
  public int _pos;
  public String _team;
  public PlayerStatus _player_status;
  public int _runs = 0;
  public int _bankRoll = 0;
  public int _ballsPlayed = 0;
  public int _ballsBowled = 0;
  public String _city = "";
  public char _sex = MALE;

  public int _team_size;

  public static final char MALE = 'M';
  public static final char FEMALE = 'F';


  public PlayerModel(int pos, String team, int ts) {
    _player_status = new PlayerStatus(PlayerStatus.PRE_JOIN);
    _team_size = ts;
    _pos = pos;
    _team = team;
    //_cat.finest(this);
  }

  /**
   * Copy constructor. Presumably will be used
   * only for copying from server to client.
   */
  public PlayerModel(PlayerModel player) {
    if (player != null) {
      _name = player._name;
      _pos = player._pos;
      _team = player._team;
      _team_size = player._team_size;
      _runs = player._runs;
      _ballsPlayed = player._ballsPlayed;
      _ballsBowled = player._ballsBowled;
      _bankRoll = player._bankRoll;
      _player_status = player._player_status;
      _city = player._city;
      _sex = player._sex;
      //_cat.finest(this);
    }
    else {
      throw new IllegalStateException("Player model is null");
    }
  }

  public PlayerModel(String[] pd, int ts) {
    _name = pd[2];
    _pos = Integer.parseInt(pd[0]);
    _team = pd[1];
    _team_size = ts;
    _player_status = new PlayerStatus(Integer.parseInt(pd[3]));
    _runs = Integer.parseInt(pd[5]);
    _ballsPlayed = Integer.parseInt(pd[6]);
    _ballsBowled = Integer.parseInt(pd[7]);
    _bankRoll = (int)Double.parseDouble(pd[8]);
    _sex = pd[4].equals("0") ? FEMALE : MALE;
    //_cat.finest(this);
  }

  public void updateModel(PlayerModel pm){
    //assert _name.equals(pm._name):"The player name does not match" + _name + " this=" + this;
    _name = pm._name;
    _pos = pm._pos;
    _team = pm._team;
    _team_size = pm._team_size;
    _runs = pm._runs;
    _ballsPlayed = pm._ballsPlayed;
    _ballsBowled = pm._ballsBowled;
    _bankRoll = pm._bankRoll;
    _player_status = pm._player_status;
  }

//###################################################
  public int getPosition() {
    return _pos;
  }

  public String getTeam() {
    return _team;
  }

//###################################################

  public void copy(PlayerModel player) {
      _name = player._name;
      _pos = player._pos;
      _team = player._team;
      _runs = player._runs;
      _ballsPlayed = player._ballsPlayed;
      _ballsBowled = player._ballsBowled;
      _bankRoll = player._bankRoll;
      _player_status = player._player_status;
      _city = player._city;
      _sex = player._sex;
  }


  /**
   * Get a string representation of this Hand.
   */
  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append(" { Name=" + (_name != null ? _name : "_"));
    s.append(", pos=" + _pos + ", Team=" + _team +
             ", status=" + _player_status+ ", BallsBowled=" + _ballsBowled +
             ", Runs=" + _runs + ", BallsPlayed=" + _ballsPlayed + " }");
    return s.toString();
  }

  public boolean equals(PlayerModel obj) {
    return
        _name == null ?
        (obj == null || obj._name == null ? true : false) :
        (obj == null || obj._name == null ? false : _name.equals(obj._name));
  }



  /**
   * @return
   */

  public PlayerStatus getPlayerStatus() {
    return _player_status;
  }

  /**
   * @param i
   */

  // Agneya FIX 30
  public void setPlayerStatus(PlayerStatus i) {
    _player_status = i;
  }

  /**
   * Gets the nickname of a player.
   */

  public final String getName() {
    return _name;
  }

  /**
   * Sets the nickname of a player.
   */
  public final void setName(String name) {
    _name = name;
  }

  /**
   * Gets the city of a player.
   */

  public final String getCity() {
    return _city;
  }

  /**
   * Sets the city of a player.
   */

  public final void setCity(String city) {
    _city = city;
  }


  /**
   * Gets the amount of player's bankroll
   */
  public final int getBankRoll() {
    return _bankRoll;
  }

  /**
   * Sets the amount of player's bankRoll
   */
  public final void setBankRoll(int bankRoll) {
    this._bankRoll = bankRoll;
  }

  /**
   * Gets an user's sex.
   * @return char - 'M' male, 'F' female.
   */
  public final char getSex() {
    return _sex;
  }

  /**
   * Sets an user's sex.
   */
  public final void setSex(char sex) {
    _sex = sex;
  }

  /**
   * Gets the name of a player to display. It consists of nickname and city
   * of a player.
   */
  public String getDisplayName() {
    StringBuffer sb = new StringBuffer();
    if (_name != null) {
      sb.append(_name);
    }
    else {
      sb.append("Unknown player");
    }
    if (_city != null) {
      sb.append(" (");
      sb.append(_city);
      sb.append(")");
    }
    return sb.toString();
  }

  public boolean isBowl(){
    return _player_status.isBowler();
  }

  public boolean isBats(){
    return _player_status.isBatsman();
  }

  public boolean isFielder(){
    return _player_status.isFielder();
  }

  public boolean isWaiting(){
    return _player_status.isWaiting();
  }

}
