package com.criconline.actions;

import com.cricket.mmog.cric.util.ActionConstants;

import com.criconline.SharedConstants;

public class MoveRequestAction extends Action {

  private boolean mine;
  private int button_position;
  private int actions;
  private double amount;
  private double amount_limit;
  String _tid;

//FROM BOTTOM PANEL
  public MoveRequestAction(String tid, int pos, String team, int actions,
                           double amount, double amount_limit) {
    super(ActionConstants.
          MOVE_REQUEST, ACTION_TYPE_MOVEREQUEST, pos, team);
    this.mine = true;
    this.amount = amount;
    this.amount_limit = amount_limit;
    this.actions = actions;
    _tid=tid;
  }

//FROM ACTION FACTORY
  public MoveRequestAction(String tid, int pos, String team, int bp, boolean mine,
                           int actions, double amount, double amount_limit) {
    super(ActionConstants.
          MOVE_REQUEST, ACTION_TYPE_MOVEREQUEST, pos, team);
    this.mine = mine;
    this.button_position = bp;
    this.amount = amount;
    this.amount_limit = amount_limit;
    this.actions = actions;
    _tid=tid;
  }

    //FROM ACTION FACTORY
      public MoveRequestAction(int id, int pos, String team, int bp, boolean mine,
                               int actions, double amount, double amount_limit) {
        super(id, ACTION_TYPE_MOVEREQUEST, pos, team);
        this.mine = mine;
        this.button_position = bp;
        this.amount = amount;
        this.amount_limit = amount_limit;
        this.actions = actions;
      }

  public double getAmount() {
    return amount;
  }

  public double getAmountLimit() {
    return amount_limit;
  }

  public int getAction() {
    return actions;
  }

  public int getButtonPosition() {
    return button_position;
  }

  public boolean mine() {
    return mine;
  }

  public String toString() {
    String str = button_position + "> Me=" + mine + ", " +  super.toString() + ", Act=";
    str += actionToString(actions) + ":" + amount + "~" + amount_limit +
          ", ";
    return str;
  }

  public void handleAction(ActionVisitor v) {
    v.handleMoveRequestAction(this);
  }

}
