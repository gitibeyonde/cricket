package com.cricket.common.message;

import java.util.*;

public class Command
    extends Event {
  protected int _cname;


  public Command(String session, int cname) {
    _session = session;
    _cname = cname;
    _hash.put("CSID", session);
    _hash.put("CN", String.valueOf(cname));
  }

  public Command(String str) {
    super(str);
    _session = (String) _hash.get("CSID");
    _cname = Integer.parseInt( (String) _hash.get("CN"));
  }

  public Command(HashMap hash) {
    super(hash);
    _session = (String) _hash.get("CSID");
    _cname = Integer.parseInt( (String) _hash.get("CN"));
  }

  public int getCommandName() {
    return _cname;
  }

  public String session() {
    return _session;
  }

  public void session(String s) {
    _hash.put("CSID", s);
    _session = s;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("CSID=").append(_session).append("&CN=").append(_cname);
    return str.toString();
  }

}
