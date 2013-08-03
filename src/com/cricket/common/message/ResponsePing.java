package com.cricket.common.message;

import java.util.*;

public class ResponsePing
    extends Response {
  long _time;

  public ResponsePing() {
    super(1, Response.R_PING);
    _time = System.currentTimeMillis();
  }

  public ResponsePing(String com) {
    super(com);
    _time = Long.parseLong( (String) _hash.get("TIME"));

  }

  public ResponsePing(HashMap com) {
    super(com);
    _time = Long.parseLong( (String) _hash.get("TIME"));
  }

  public long getTime() {
    return _time;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&TIME=").append(_time);
    return str.toString();
  }

}
