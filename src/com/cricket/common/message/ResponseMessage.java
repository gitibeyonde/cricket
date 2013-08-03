package com.cricket.common.message;

import java.util.*;
import com.agneya.util.*;

public class ResponseMessage
    extends Response {
  private String _gm;
  String _tid = "";

  public ResponseMessage(int result, String gm) {
    super(result, R_MESSAGE);
    _gm = gm;
  }

  public ResponseMessage(int result, String gm, String tid) {
    super(result, R_MESSAGE);
    _gm = gm;
    _tid = tid;
  }

  public ResponseMessage(HashMap str) {
    super(str);
    _gm = (String) _hash.get("GM");
  }

  public String getType() {
    GameEvent ge = new GameEvent();
    ge.init( (String) _hash.get("GM"));
    return ge.get("type");
  }

  public String getGameId() {
    GameEvent ge = new GameEvent();
    ge.init( (String) _hash.get("GM"));

    return ge.get("name");
  }

  public String getMessage() {
    GameEvent ge = new GameEvent();
    ge.init( (String) _hash.get("GM"));
    return new String(ge.get("message"));
  }

  public GameEvent getGM() {
    GameEvent ge = new GameEvent();
    ge.init( (String) _hash.get("GM"));
    return ge;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&GM=").append(_gm);
    return str.toString();
  }

  public boolean equal(ResponseMessage r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
