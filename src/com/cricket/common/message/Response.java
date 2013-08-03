package com.cricket.common.message;

import java.util.*;

public class Response
    extends Event {

  int _result;

  /**
   * name of the response
   */
  public int _response_name = R_BASIC;


  public Response(String str) {
    super(str);
    _session = (String) _hash.get("CSID");
    _response_name = Integer.parseInt( (String) _hash.get("RNAME"));
    _result = Integer.parseInt( (String) _hash.get("CR"));
  }

  public Response(HashMap str) {
    super(str);
    _session = (String) _hash.get("CSID");
    _response_name = Integer.parseInt( (String) _hash.get("RNAME"));
    _result = Integer.parseInt( (String) _hash.get("CR"));
  }

  public Response(int result, int rname) {
    _result = result;
    _response_name = rname;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("CSID=").append(_session).append("&RNAME=")
        .append(_response_name).append("&CR=").append(_result);
    return str.toString();
  }

  public String session() {
    return _session;
  }

  public void session(String session) {
    _hash.put("CSID", session);
    _session = session;
  }

  public int getResult() {
    return _result;
  }

  public int responseName() {
    return _response_name;
  }

  public boolean equal(Response r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
