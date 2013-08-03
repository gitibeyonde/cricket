package com.criconline.actions;

import com.cricket.mmog.cric.util.ActionConstants;
import com.criconline.models.PlayerModel;

/**
 * <p>This class is simple message sended from Table Server Implementation
 * to Table Controller when new player registers on the table or when
 * a registerd player leaves the table.</p>
 * @version 1.0
 */

public class PlayerJoinAction
    extends TableServerAction {

  private PlayerModel player;
  private double cash;
  private boolean me;

  /**
   * Constructor.
   */
  public PlayerJoinAction(int pos, String team, PlayerModel player, boolean isMe) {
    //int id, int type, int target
    super(ActionConstants.PLAYER_JOIN, pos, team);
    this.player = player;
    me = isMe;
  }

  /**
   * Constructor.
   */
  public PlayerJoinAction(int pos, String team, double cash) {
    super(ActionConstants.PLAYER_JOIN, pos, team);
    this.cash = cash;
  }

  /**
   * Gets the player model. Note that the player model can be null in case of
   * 'player leave' action.
   */
  public final PlayerModel getPlayer() {
    return player;
  }

  public final int getPoisiton() {
    return super.getPosition();
  }

  public final boolean isMe() {
    return me;
  }

  public final double getCash() {
    return cash;
  }

}
