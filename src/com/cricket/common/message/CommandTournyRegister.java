package com.cricket.common.message;

import java.util.*;

public class CommandTournyRegister
    extends Command {
  String _tid;

  public CommandTournyRegister(String session, String tid) {
    super(session, Command.C_TOURNYREGISTER);
    _tid = tid;
  }

  public CommandTournyRegister(String str) {
    super(str);
    _tid =  (String) _hash.get("TID");

  }

  public CommandTournyRegister(HashMap str) {
    super(str);
    _tid =(String) _hash.get("TID");

  }

  public String getTournyName() {
    return _tid;
  }

  public String toString() {
    return super.toString() + "&TID=" + _tid;
  }

}
