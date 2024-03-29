package com.cricket.common.message;

import java.util.*;

public class ResponseGameEvent
    extends Response {
  private String _gameEvent;

  public ResponseGameEvent(int result, String ge) {
    super(result, R_MOVE);
    _gameEvent = ge;
  }

  public ResponseGameEvent(String str) {
    super(str);
    _gameEvent = (String) _hash.get("GE");
  }

  public ResponseGameEvent(HashMap str) {
    super(str);
    _gameEvent = (String) _hash.get("GE");
  }

  public String getGameEvent() {
    return _gameEvent;
  }

  public void setGameEvent(String ge) {
    _gameEvent = ge;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&GE=").append(_gameEvent);
    return str.toString();
  }

  public boolean equal(ResponseGameEvent r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
