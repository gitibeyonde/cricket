package com.cricket.common.message;

import java.util.*;
import com.cricket.mmog.cric.util.MoveParams;

public class CommandMove
    extends Command {
  int _move;
  String _tid;
  int _grid;
  int _pPos;
  String _pTeam;
  double _mAmt;
  MoveParams _moveDetails;

  public CommandMove(String session, int move, double amt, String tid) {
    super(session, Command.C_MOVE);
    _move = move;
    _mAmt = amt;
    _tid = tid;
  }

  public CommandMove(String session, int move, double amt, String tid, int grid) {
    super(session, Command.C_MOVE);
    _move = move;
    _mAmt = amt;
    _tid = tid;
    _grid = grid;
  }

  public CommandMove(String session, int move, double amt, String tid, int grid,
                     MoveParams move_det) {
    super(session, Command.C_MOVE);
    _move = move;
    _mAmt = amt;
    _tid = tid;
    _grid = grid;
    _moveDetails = move_det;
  }

  public CommandMove(HashMap str) {
    super(str);
    _tid =  (String) _hash.get("TID");
    _grid = Integer.parseInt( (String) (_hash.get("GRID") == null ? "-1" :
                                        _hash.get("GRID")));
    _moveDetails = new MoveParams((String) (_hash.get("MD") == null ? "" : _hash.get("MD")));
    _move = Integer.parseInt( (String) _hash.get("MV"));
    _pPos = Integer.parseInt( (String) _hash.get("MPOS"));
    _pTeam = (String) _hash.get("MTEAM");
    _mAmt = Double.parseDouble( (String) _hash.get("MAMT"));
  }

  public void setPlayerPosition(int i) {
    _pPos = i;
  }

  public void setPlayerTeam(String i) {
    _pTeam = i;
  }

  public int getMove() {
    return _move;
  }

  public double getMoveAmount() {
    return _mAmt;
  }

  public String getTableId() {
    return _tid;
  }

  public int getHandId() {
    return _grid;
  }

  public MoveParams getMoveDetails() {
    return _moveDetails;
  }

  public int getPlayerPosition() {
    return _pPos;
  }

  public String getPlayerTeam() {
    return _pTeam;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&MV=").append(_move).append("&MAMT=").append(_mAmt)
        .append("&MPOS=").append(_pPos);
    str.append("&MTEAM=").append(_pTeam);
    str.append("&TID=").append(_tid);
    str.append(
        "&GRID=").append(_grid);
    str.append(
        "&MD=").append(_moveDetails == null ? "" : _moveDetails.stringValue());
    return str.toString();
  }

}
