package com.criconline.actions;

import com.criconline.SharedConstants;
import com.cricket.mmog.cric.util.MoveParams;

public class MoveAction extends Action {
  MoveParams _md;

  public MoveAction(int id, int pos, String team) {
    super(id, ACTION_TYPE_MOVE, pos, team);
  }

  public MoveAction(int id, int pos, String team, MoveParams md) {
    this(id, pos, team);
    _md = md;
  }

  public void setType(int type) {
    this.type = type;
  }

  public MoveParams getMoveParams() {
    return _md;
  }

  public String toString() {
    return super.toString() + ", pos=" + pos + ", team=" + team;
  }

  public void handleAction(ActionVisitor v) {
    v.handleDefaultAction(this);
  }

}
