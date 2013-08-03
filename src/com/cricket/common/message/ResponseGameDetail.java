package com.cricket.common.message;

import java.util.*;

//import com.cricket.game.GameSummary;

public class ResponseGameDetail
    extends Response {
  private String _gameEvent;
  private String _ip;

  public ResponseGameDetail(int result, String ge) {
    super(result, R_GAMEDETAIL);
    _gameEvent = ge;
  }

  public ResponseGameDetail(String str) {
    super(str);
    _gameEvent = (String) _hash.get("GE");
  }

  public ResponseGameDetail(HashMap str) {
    super(str);
    _gameEvent = (String) _hash.get("GE");
  }

  public String getGameEvent() {
    return _gameEvent;
  }

  public void setGameEvent(String ge) {
    _gameEvent = ge;
  }

  public void setIp(String ip) {
    _ip = ip;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&IP=").append(_ip);
    str.append("&GE=").append(_gameEvent);
    return str.toString();
  }

  public boolean equal(ResponseGameDetail r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
