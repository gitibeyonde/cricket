package com.cricket.common.message;

import java.util.*;

public class CommandGameDetail
    extends Command {
  String _tid;

  public CommandGameDetail(String session, String tid) {
    super(session, Command.C_GAMEDETAIL);
    _tid = tid;
  }

  public CommandGameDetail(String str) {
    super(str);
    _tid = (String) _hash.get("TID");

  }

  public CommandGameDetail(HashMap str) {
    super(str);
    _tid = (String) _hash.get("TID");

  }

  public String getTableId() {
    return _tid;
  }

  public String toString() {
    return super.toString() + "&TID=" + _tid;
  }

}
