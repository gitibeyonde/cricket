package com.cricket.common.message;

import java.util.*;

public class CommandPlayerShift
    extends Command {
  private String _gid;
  String _pid;
  private int _dx, _dy, _dz;


  public CommandPlayerShift(String session, String gid, String pid, int dx, int dy, int dz) {
    super(session, C_PLAYERSHIFT);
    _gid=gid;
    _pid=pid;
    _dx=dx;
    _dy=dy;
    _dz = dz;
  }

  public CommandPlayerShift(HashMap str) {
    super(str);
    _gid = (String) (_hash.get("gid"));
    _pid = (String) (_hash.get("player"));
    _dx = Integer.parseInt( (String) (_hash.get("dx")));
    _dy = Integer.parseInt( (String) (_hash.get("dy")));
    _dz = Integer.parseInt( (String) (_hash.get("dz")));
  }

  public int getDx() {
    return _dx;
  }
  public int getDy() {
    return _dy;
  }
  public int getDz() {
    return _dz;
  }

  public String getPid() {
    return _pid;
  }
  public String getGid() {
    return _gid;
  }


  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&gid=").append(_gid);
    str.append("&player=").append(_pid).append("&dx=").append(_dx);
    str.append("&dy=").append(_dy);
    str.append("&dz=").append(_dz);
    return str.toString();
  }

  public boolean equal(CommandPlayerShift r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }
}
