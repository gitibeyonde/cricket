package com.criconline.actions;

import com.criconline.SharedConstants;
import com.cricket.mmog.cric.util.MoveParams;

/**
 * This is the last move action.
 * @author Yuriy Guskov
 */
public class LastMoveAction extends Action {

  private MoveParams _mp;
  private double bet, pot, rake, chips, amt_at_table;
  private boolean _me = false;

  public LastMoveAction(int id, int pos, String team, MoveParams mp,
                        boolean me) {
    super(id, ACTION_TYPE_LASTMOVE, pos, team);
    _mp = mp;
    _me = me;
  }

  public MoveParams getMoveParams(){
    return _mp;
  }

  public void setType(int type) {
    this.type = type;
  }

  public double getBet() {
    return bet;
  }

  public double getPot() {
    return pot;
  }

  public double getRake() {
    return rake;
  }

  public double getChips() {
    return chips;
  }

  public double getAmountAtTable() {
    return amt_at_table;
  }

  public boolean isMe() {
    return _me;
  };


  public String toString() {
    return "pos=" + pos + ", team=" + team + ", action=" + actionToString(this) + ", MP=" + (_mp==null ? "" : _mp.stringValue());
  }

  public void handleAction(ActionVisitor v) {
    v.handleLastMoveAction(this);
  }

}
