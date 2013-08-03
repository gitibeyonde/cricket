package com.criconline.models;

import com.criconline.SharedConstants;
import com.criconline.ValidationConstants;
import com.cricket.common.message.GameEvent;
import java.util.logging.Logger;
import com.cricket.mmog.GameType;

/**
 * Lobby table model for Cricket game
 * @hibernate.subclass
 *    discriminator-value="1"
 */

public class LobbyCricketModel
    extends LobbyTableModel {

  static Logger _cat = Logger.getLogger(LobbyCricketModel.class.getName());

  protected String _name;
  protected double _fees = 0;
  protected double _buyin = 0;
  protected String[][] _fielding, _batting;
  protected GameType _type;
  private int tournamentLevel = -1;

  /**
   * Constructor.
   */
  public LobbyCricketModel() {
  }

  public LobbyCricketModel(GameEvent ge) {
    super(ge);
    name = ge.get("name");
    _type = new GameType(ge.getType());
    _team_size = _type.teamSize();
    _cat.finest("Team size " + _team_size);
    _fielding = ge.getFeildingPlayersDetails();
    _batting = ge.getBattingPlayersDetails();
    _fees = Double.parseDouble(ge.get("fees"));
    _buyin = Double.parseDouble(ge.get("buyin"));
  }

  /**
   * Constructor.
   */
  public LobbyCricketModel(LobbyTableModel model) {
    super(model.getName(),
          model.getGameType().intVal(),
          model.isRealMoneyTable(),
          model.getTeamSize(),
          model.getBallsPerPlayer(),
          model.getFee(),
          model.getDistrId()
          );
  }

  /**
   * Constructor.
   */
  public LobbyCricketModel(int id,
                          String name,
                          int gameType,
                          boolean playRealMoney,
                          int numPlayers,
                          double rakeRate,
                          double shortMaxRake,
                          double longMaxRake,
                          int potLimit,
                          double lowBet,
                          double highBet,
                          double smallBlind,
                          double bigBlind,
                          double minBuyIn,
                          double maxBuyIn,
                          int bpp,
                          double fee,
                          Integer distrId
                          ) {
    super(name, gameType, playRealMoney, numPlayers, bpp,
          fee,
          distrId);
  }

  /**
   * Constructor.
   */
  /**public LobbyCricketModel(LobbyCricketModel model) {
    copy(model);
  }***/

  /**
   * Copies.
   */

  /**public void copyWithoutSettings(LobbyTableModel model) {
    super.copyWithoutSettings(model);
    LobbyCricketModel cricketModel = (LobbyCricketModel) model;
    this.lowBet = cricketModel.lowBet;
    this.highBet = cricketModel.highBet;
    this.smallBlind = cricketModel.smallBlind;
    this.bigBlind = cricketModel.bigBlind;
    this.playersPerFlop = cricketModel.playersPerFlop;
    this.tournamentLevel = cricketModel.tournamentLevel;
  }**/

  /**public void copy(LobbyTableModel model) {
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
    if (obj instanceof LobbyCricketModel) {
      LobbyCricketModel table = (LobbyCricketModel) obj;
      return
          super.equalsByFields(table);
    }
    else {
      return false;
    }
  }

   /***public int isValid() {

    if (lowBet > highBet) {
      return ValidationConstants.LOW_BET_LT_HIGH_BET;
    }
    if (lowBet / 2 > smallBlind) {
      return ValidationConstants.SMALL_BLIND_GT_LOW_BET_DIV_2;
    }
    if (lowBet > bigBlind) {
      return ValidationConstants.BIG_BLIND_GT_LOW_BET;
    }
    if (minBuyIn < bigBlind) {
      return ValidationConstants.MIN_BUYIN_GT_BIG_BLIND;
    }
    if (isTournamentGame()) {
      if (minBuyIn != maxBuyIn) {
        return ValidationConstants.MIN_BUYIN_EQ_MAX_BUYIN_FOR_TOURNAMENT;
      }
    }
    else {
      if ( (gameLimitType != PokerConstants.REGULAR) && (minBuyIn >= maxBuyIn)) {
        return ValidationConstants.MIN_BUYIN_LT_MAX_BUYIN_FOR_NON_TOURNAMENT;
      }
    }
    return super.isValid();
  }**/

}
