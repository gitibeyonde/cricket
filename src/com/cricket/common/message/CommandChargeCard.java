package com.cricket.common.message;

import java.util.*;

public class CommandChargeCard
    extends Command {
  String _card_no;
  String _user_id;
  double _amount;

  public CommandChargeCard(String session, String card_no, String user_id,
                           double amount) {
    super(session, Command.C_CARD);
    _card_no = card_no;
    _user_id = user_id;
    _amount = amount;
  }

  public CommandChargeCard(String com) {
    super(com);
    _card_no = (String) _hash.get("CNUM");
    _user_id = (String) _hash.get("UN");
    _amount = Double.parseDouble( (String) _hash.get("AMT"));
  }

  public CommandChargeCard(HashMap com) {
    super(com);
    _card_no = (String) _hash.get("CNUM");
    _user_id = (String) _hash.get("UN");
    _amount = Double.parseDouble( (String) _hash.get("AMT"));
  }

  public String getUserName() {
    return _user_id;
  }

  public String getCardNumber() {
    return _card_no;
  }

  public double getAmount() {
    return _amount;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&CNUM=").append(_card_no).append("&UN=").append(_user_id).
        append("&AMT=").append(_amount);
    return str.toString();
  }

}
