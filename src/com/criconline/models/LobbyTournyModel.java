package com.criconline.models;

import com.criconline.SharedConstants;
import com.criconline.ValidationConstants;
import com.cricket.common.message.GameEvent;
import java.util.logging.Logger;
import com.cricket.mmog.GameType;
import com.cricket.common.message.TournyEvent;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.cricket.common.interfaces.TournyInterface;

/**
 * Lobby table model for Cricket game
 * @hibernate.subclass
 *    discriminator-value="1"
 */

public class LobbyTournyModel
    extends LobbyTableModel {

  static Logger _cat = Logger.getLogger(LobbyCricketModel.class.getName());

  protected double _fees = 0;
  protected double _buyin = 0;
  protected Date _date;
  protected int _level;
  protected int _state;
  protected int _di, _ri, _ji;
  protected String[][] _fielding, _batting;
  private int tournamentLevel = -1;

  public LobbyTournyModel(TournyEvent ge) {
    super(ge);
    name = ge.name();
    _gameType = new GameType(GameType.WORLD_CUP);
    _team_size = _gameType.teamSize();
    _cat.finest("Team size " + _team_size);
    _fees = Double.parseDouble(ge.get("fees"));
    _buyin = Double.parseDouble(ge.get("buy-in"));
    _level = Integer.parseInt(ge.get("level"));
    _state = Integer.parseInt(ge.get("state"));
    _date = new Date(Date.parse(ge.get("date")));
  }


  public void update(TournyEvent te){
    name = te.name();
    _gameType = new GameType(GameType.WORLD_CUP);
    _team_size = _gameType.teamSize();
    _cat.finest("Team size " + _team_size);
    _fees = Double.parseDouble(te.get("fees"));
    _buyin = Double.parseDouble(te.get("buy-in"));
    _level = Integer.parseInt(te.get("level"));
    _state = Integer.parseInt(te.get("state"));
    _date = new Date(Date.parse(te.get("date")));
    String[] intr = te.get("intervals").split("\\|");
    _di = Integer.parseInt(intr[0]);
    _ri = Integer.parseInt(intr[1]);
    _ji = Integer.parseInt(intr[2]);
  }


  /**
   * Copies.
   */

  /**public void copyWithoutSettings(LobbyTableModel model) {
    super.copyWithoutSettings(model);
    LobbyTournyModel cricketModel = (LobbyTournyModel) model;
    this.lowBet = cricketModel.lowBet;
    this.highBet = cricketModel.highBet;
    this.smallBlind = cricketModel.smallBlind;
    this.bigBlind = cricketModel.bigBlind;
    this.playersPerFlop = cricketModel.playersPerFlop;
    this.tournamentLevel = cricketModel.tournamentLevel;
  }

  public void copy(LobbyTableModel model) {
    super.copy(model);
  }**/


  /**
   * Gets the index of tournament level in TournamentConstants interface.
   * If game is not a tournament return -1.
   */
  public final int getTournamentLevel() {
    return tournamentLevel;
  }

  /**
   * Sets the index of tournament level in TournamentConstants interface.
   * If the game is tournament game and new value not equals the stored
   * value updates lowBet, highBet, smallBlind, bigBlind
   */
  public final void setTournamentLevel(int value) {
    this.tournamentLevel = value;
  }

  public boolean equalsByFields(Object obj) {
    if (obj instanceof LobbyTournyModel) {
      LobbyTournyModel table = (LobbyTournyModel) obj;
      return
          super.equalsByFields(table);
    }
    else {
      return false;
    }
  }

  public int getState(){
    return _state;
  }

  public String getStateString(){
    switch(_state){
      case TournyInterface.NOEXIST:
        return "no-exist";
      case TournyInterface.CREATED:
        return "Created";
      case TournyInterface.DECL:
        return "Declared";
      case TournyInterface.REG:
        return "Registering";
      case TournyInterface.JOIN:
        return "Waiting for Players";
      case TournyInterface.START:
        return "Starting now";
      case TournyInterface.RUNNING:
        return "Running";
      case TournyInterface.END:
        return "Winners Declared";
      case TournyInterface.FINISH:
        return "Winners Declared";
      case TournyInterface.CLEAR:
        return "Over";
      default:
        return "unknown";
    }
  }


  public int getLevel(){
    return _level;
  }

  public String getDate(){
    SimpleDateFormat sdf = new SimpleDateFormat("MMM.dd 'at' HH:mm z", Locale.US);
    //_cat.finest("Date = " + _date);
    return sdf.format(_date);
  }

  /**public int isValid() {
    return super.isValid();
  }**/

}
