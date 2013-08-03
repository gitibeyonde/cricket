package com.cricket.common.message;

import java.util.*;

public class ResponseString
    extends Response {
  String _strVal;

  public ResponseString(int result, int rname, String strVal) {
    super(result, rname);
    _strVal = strVal;
  }

  public ResponseString(String com) {
    super(com);
    _strVal = (String) _hash.get("SV");

  }

  public ResponseString(HashMap com) {
    super(com);
    _strVal = (String) _hash.get("SV");
  }

  public String getStringVal() {
    return _strVal;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&SV=").append(_strVal);
    return str.toString();
  }

}
