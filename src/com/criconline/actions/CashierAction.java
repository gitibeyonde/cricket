package com.criconline.actions;

/**
 * Call to cashier action.
 * @author Halt
 * @author Kom
 */
public class CashierAction extends StageAction {

  double amount = 0;

  public CashierAction(int pos, String team, double amount) {
    super(CASHIER, pos, team);
    this.amount = amount;
  }

  public double getAmount() {
    return amount;
  }

  public int getPosition() {
    return pos;
  }

  public String getTeam() {
    return team;
  }

  public String toString() {
    return "Call to cashier for " + amount;
  }
}


