package com.cricket.common.message;

import java.util.*;

public class ResponseTableOpen
    extends Response {
  private String _tid[];
  private int _pos[];

  public ResponseTableOpen(String tid[], int pos[]) {
    super(1, R_TABLE_OPEN);
    _tid = tid;
    _pos = pos;
  }

  public ResponseTableOpen(String tid, int pos) {
    super(1, R_TABLE_OPEN);
    _tid = new String[1];
    _tid[0] = tid;
    _pos = new int[1];
    _pos[0] = pos;
  }

  public ResponseTableOpen(HashMap str) {
    super(str);
    int count = Integer.parseInt( (String) _hash.get("TCNT"));
    _tid = new String[count];
    _pos = new int[count];
    for (int i = 0; i < count; i++) {
      //_tid[i]=
      //_pos[i]=
    }
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    if (_tid != null) {
      str.append("&TCNT=").append(_tid.length);
      for (int i = 0; _tid != null && i < _tid.length; i++) {
        str.append("&TABLE" +
                   i).append("=").append(_tid[i]).append("|").append(_pos[i]);
      }
    }
    return str.toString();
  }

  public boolean equal(ResponseTableOpen r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
