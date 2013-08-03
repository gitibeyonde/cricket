package com.cricket.common.message;

import java.util.*;

public class ResponseTournyMyTable
    extends Response {
  private String _tid;
  private String _gid;
  private int _pos;

  public ResponseTournyMyTable(int result, String tid, String gid, int pos) {
    super(result, R_TOURNYMYTABLE);
    _tid = tid;
    _gid = gid;
    _pos = pos;
  }

  public ResponseTournyMyTable(String str) {
    super(str);
    _tid =  (String) _hash.get("TID");
    _gid = (String) _hash.get("GID");
    _pos = Integer.parseInt( (String) _hash.get("POS"));
  }

  public ResponseTournyMyTable(HashMap str) {
    super(str);
    _tid = (String) _hash.get("TID");
    _gid = (String) _hash.get("GID");
    _pos = Integer.parseInt( (String) _hash.get("POS"));
  }

  public String getTid() {
    return _tid;
  }

  public String getGameId() {
    return _gid;
  }

  public int getPosition() {
    return _pos;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&TID=").append(_tid);
    str.append("&GID=").append(_gid);
    str.append("&POS=").append(_pos);
    return str.toString();
  }

  public boolean equal(ResponseTournyMyTable r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
