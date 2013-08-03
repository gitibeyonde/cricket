package com.cricket.common.message;

import java.util.*;
import com.agneya.util.*;

public class ResponseGameList
    extends Response {
  private int _gameCnt = 0;
  private int _playerCnt = 0;
  private String[] _games;
  private int _cnt;

  public ResponseGameList(int result, String[] games) {
    super(result, R_GAMELIST);
    _cnt = games.length;
    _games = games;
  }

  public ResponseGameList(String str) {
    super(str);
    _gameCnt = Integer.parseInt( (String) _hash.get("GC"));
    _playerCnt = Integer.parseInt( (String) _hash.get("PC"));
    _cnt = Integer.parseInt( (String) _hash.get("GMCNT"));
    _games = new String[_cnt];
    for (int i = 0; i < _cnt; i++) {
      _games[i] = (String) _hash.get("G" + i);
    }
  }

  public ResponseGameList(HashMap str) {
    super(str);
    _gameCnt = Integer.parseInt( (String) _hash.get("GC"));
    _playerCnt = Integer.parseInt( (String) _hash.get("PC"));
    _cnt = Integer.parseInt( (String) _hash.get("GMCNT"));
    _games = new String[_cnt];
    for (int i = 0; i < _cnt; i++) {
      _games[i] = (String) _hash.get("G" + i);
    }
  }

  public void setDetails(int gc, int pc) {
    _gameCnt = gc;
    _playerCnt = pc;
  }

  public int getGameCount() {
    return _cnt;
  }

  public int getPlayerCount() {
    return _playerCnt;
  }

  public String getGame(int i) {
    return _games[i];
  }

  public GameEvent getGameEvent(int i) {
    GameEvent ge = new GameEvent();
    ge.init(_games[i]);
    return ge;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&GC=").append(_gameCnt);
    str.append("&PC=").append(_playerCnt);
    str.append("&GMCNT=").append(_cnt);
    for (int i = 0; i < _cnt; i++) {
      str.append("&G").append(i).append("=").append(_games[i]);
    }
    return str.toString();
  }

  public boolean equal(ResponseGameList r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
