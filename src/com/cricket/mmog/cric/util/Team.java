package com.cricket.mmog.cric.util;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;

public class Team
    implements Serializable, CricketConstants {
  // set the category for logging
  static Logger _cat = Logger.getLogger(Team.class.getName());

  int _size;
  int _count;
  String _gid;
  public String _team_name;

  int[] _positions;
  CricketPresence[] _players;
  CricketPresence _captain;
  protected int _ball_count, _run_count;
  public int _state;

  long [] fielder_map = {
                        PlayerStatus.BOWLER,
                        PlayerStatus.WICKET_KEEPER,
                        PlayerStatus.LF1,
                        PlayerStatus.RF1,
                        PlayerStatus.LF2,
                        PlayerStatus.RF2,
                        PlayerStatus.LF3,
                        PlayerStatus.RF3,
                        PlayerStatus.LF4,
                        PlayerStatus.RF4,
                        PlayerStatus.LF5
  };


  public Team(String tn, String gid, int size) {
    _size = size;
    _gid = gid;
    _team_name = tn;
    _players = new CricketPresence[_size];
    _positions = new int[_size];
    for (int i = 0; i < _size; i++) {
      _positions[i] = -1;
    }
    _count = 0;
    _state = CREATED;
  }

  public void reset() {
    _ball_count = 0;
    _run_count=0;
    CricketPresence v[] = allPlayers(0);
    for (int i=0;i<v.length;i++){
      if (!v[i].isRemoved() && !v[i].isDisconnected() && !v[i].isBroke()){
        v[i].reset();
        v[i].resetStatus();
        v[i].setWaiting();
        _cat.finest("----reset -" + v[i]);
      }
      else {
        remove(v[i]);
        _cat.finest("----removed inactive -" + v[i]);
      }
    }
  }

  public int state(){
    return _state;
  }

  public void incrBallCount() {
    _ball_count++;
  }

  public int ballCount() {
    return _ball_count;
  }

  public void setFielding(int pos) {
    _state = FIELDING;
    // set bowler
    int k=0;
    for (int i=pos;i<_players.length;i++){
      if (_players[i]!=null && !_players[i].isNew()){
        _players[i].unsetBatsman();
        _players[i].unsetBold();
        _players[i].unsetWaiting();
        _players[i].setFielder(fielder_map[k++]);
        _cat.finest("Set fielder = " + _players[i]);
      }
    }
    for (int j=0;j<pos;j++){
      if (_players[j]!=null && !_players[j].isNew()){
        _players[j].unsetBatsman();
        _players[j].unsetBold();
        _players[j].unsetWaiting();
        _players[j].setFielder(fielder_map[k++]);
        _cat.finest("Set fielder = " + _players[j]);
      }
    }
  }

  public boolean isFielding() {
    return _state == FIELDING;
  }

  public boolean setBatting() {
    _state = BATTING;
    // set batting
    for (int i=0;i<_players.length;i++){
      if (_players[i]!=null && !_players[i].isNew()){
        _players[i].unsetFielder();
        _players[i].setWaiting();
        _cat.finest("All batsman = " + _players[i]);
      }
    }
    CricketPresence[] v = getPotentialBatsman();
    if (v==null || v.length == 0){
      return false;
    }
    else {
      v[0].unsetWaiting();
      v[0].setBatsman();
      v[0].setBattingDone();
      _cat.finest("Setting batsman = " + v[0]);
      return true;
    }
  }

  /**public boolean setRunner() {
    Presence[] v = getPotentialBatsman();
    if (v==null || v.length == 0){
      return false;
    }
    else {
      v[0].unsetWaiting();
      v[0].setRunner();
      v[0].setBattingDone();
      _cat.finest("Setting batsman = " + v[0]);
      return true;
    }
  }**/

  public boolean isBatting() {
    return _state == BATTING;
  }

  public void setWaiting() {
    _state = WAITING;
  }

  public boolean isWaiting() {
    return _state == WAITING;
  }

  public CricketPresence[] getFielder() {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null && ((CricketPresence)p).isFielder() && !((CricketPresence)p).isBowler() && !((CricketPresence)p).isNew();
      }
    };
    return selectPlayers(selector(), -1, c);
  }

  public CricketPresence[] getBowlerAndFielder() {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null && p.isFielder() && !p.isNew();
      }
    };
    return selectPlayers(selector(), -1, c);
  }

  public CricketPresence[] getPotentialBatsman() {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null && !p.isBattingDone() && !p.isNew();/** && !p.isRunner();**/
      }
    };
    return selectPlayers(selector(), -1, c);
  }

  public CricketPresence[] getBatsman() {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null && p.isBatsman() && !p.isNew();
      }
    };
    return selectPlayers(selector(), -1, c);
  }


  public CricketPresence getBowler() {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null && p.isBowler() && !p.isNew();
      }
    };
    return selectPlayers(selector(), -1, c)[0];
  }


  public String stringValue() {
    CricketPresence[] v = allPlayers(-1);
    if (v.length == 0) {
      return ",";
    }
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < v.length; i++) {
      buf.append(v[i].pos());
      buf.append("|").append(_team_name);
      buf.append("|").append(v[i].name());
      buf.append("|").append(v[i].status());
      buf.append("|").append(v[i].gender());
      buf.append("|").append(v[i].runs());
      buf.append("|").append(v[i].ballsPlayed());
      buf.append("|").append(v[i].ballsBowled());
      buf.append("|").append(v[i].getWorth());
      buf.append("|0");
      buf.append("`");
    }
    buf.deleteCharAt(buf.length() - 1);
    buf.append(",");
    return buf.toString();
  }

  public String toString(){
    return "Ts="+ _size + ", Plrs=" + _players.length + ", Tn=" + _team_name + ", " + stringValue();
  }

  public int nextVacant() {
    int vacant = -1;
    for (int i = 0; i < _size; i++) {
      _cat.finest("Position = " + _positions[i]);
      if (_positions[i] == -1) {
        vacant = i;
        break;
      }
    }
    return vacant;
  }

  public boolean canJoin(CricketPresence p) {
    // check if this player is already in a team
    for (int i = 0; i < _players.length; i++) {
      if (_players[i] != null && _players[i].name().equals(p.name())) {
        _cat.warning("The player is already there on the team " + p._team_name);
        _players[i] = p;
        return true;
      }
    }
    if (p == null || p.pos() >= _size ||
        p.pos() < 0 || _players[p.pos()] != null) {
      _cat.info("The player position is not valid " + p.pos());
      return false;
    }
    else {
      _players[p.pos()] = p;
      _positions[p.pos()] = _count++;
      p.team(this);
      return true;
    }
  }

  public void remove(CricketPresence p) {
    _cat.finest("Removing " + p + " from " + this);
    int pos = p.pos();
    p.setRemoved();

    if (_players[pos] == null || !p.name().equals(_players[pos].name())) {
      _cat.warning("This is not the seated player on this position " + p +
                 " \n Seated player = " + _players[pos]);
      return;
    }

    int joinPos = _positions[pos];
    if (joinPos == -1) {
      _cat.warning("The presence is already removed " + p);
      return;
    }
    --_count;
    _players[pos] = null;
    _positions[pos] = -1;
    for (int i = 0; i < _positions.length; i++) {
      if (_positions[i] > joinPos) {
        --_positions[i];
      }
      else if (_positions[i] == joinPos && joinPos != -1) {
        _cat.warning(
            "Two players cannot have same join Pos " + joinPos + " player " +
            _players[i]);
      }
    }
  }

  public int count() {
    return _count;
  }

  public Presence selectPresence(PresenceSelector s, int startPos, Constraint c) {
    s.startPos(startPos);
    return s.selectPresence(c);
  }

  public CricketPresence[] selectPlayers(PresenceSelector s, int startPos,
                                  Constraint c) {
    s.startPos(startPos);
    return s.select(c);
  }

  // listing starts from the position of current player
  public Presence[] allCompanions(Presence p) {
    return findCompanions(p, false);
  }

  public Presence[] activeCompanions(Presence p) {
    return findCompanions(p, true);
  }

  private Presence[] findCompanions(final Presence plyr,
                                    final boolean onlyActive) {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null && p != plyr &&
            (!onlyActive ||
             (p.isActive() || p.isNew()));
      }
    };
    return selectPlayers(selector(), plyr.pos(), c);
  }

  public CricketPresence[] allPlayers(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null && !p.isRemoved();
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

  public Presence[] newPlayers(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null && p.isNew();
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

  public Presence[] inActivePlayers(int startPos) {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null &&
            (!p.isActive());
      }
    };
    return selectPlayers(selector(), startPos, c);
  }

//public final PresenceSelector SELECTOR_IMPL = new SelectorImpl();

  public PresenceSelector selector() {
    return new SelectorImpl();
  }

  public Presence prev(int pos) {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null;
      }
    };
    PresenceSelector s = selector();
    s.startPos(pos);
    return s.selectPrevPresence(c);
  }

  public Presence next(int pos) {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null;
      }
    };
    PresenceSelector s = selector();
    s.startPos(pos);
    return selectPresence(s, pos, c);
  }

  public CricketPresence nextActive(int pos) {
    Constraint c = new Constraint() {
      public boolean satisfy(CricketPresence p) {
        return p != null && p.isActive(); // this condition should exactly be same as
        //"activePlayers() method
      }
    };
    PresenceSelector s = selector();
    s.startPos(pos);
    return s.selectPresence(c);
  }

  public Presence[] activePlayers(int startPos) {
     Constraint c = new Constraint() {
       public boolean satisfy(CricketPresence p) {
         return p != null &&
             (p.isActive());
       }
     };
     return selectPlayers(selector(), startPos, c);
  }


  public Presence[] eligiblePlayers(int startPos) {
     Constraint c = new Constraint() {
       public boolean satisfy(CricketPresence p) {
         return p != null && !p.isRemoved() && !p.isDisconnected() && !p.isBroke();
       }
     };
     return selectPlayers(selector(), startPos, c);
  }

  public boolean onTable(Presence p) {
    return! (p == null || p.pos() >= _size ||
             p.pos() < 0 ||
             _players[p.pos()] != p);
  }


  public void unsetNew(){
    Presence []v = allPlayers(0);
    for (int i=0;i<v.length;i++){
      v[i].unsetNew();
    }
  }

  public void setCaptain() {
    if (_captain == null) {
      // no captain allocated yet
      CricketPresence firstJoinee = firstJoinee();
      _captain = firstJoinee;
      //_cat.finest(allPlayers(-1).length);
    }
    else if (activePlayers(-1).length >= 1 ){
        _captain.unsetCaptain();
        _captain = nextActive(_captain.pos());
    }
    else {
      throw new IllegalStateException("Captain cannot be selected ");
    }
    _captain.setCaptain();
    _cat.info("Setting captain " + _captain);
    return;
  }

  public CricketPresence captain() {
    return _captain;
  }

// Presence who was first to occupy a position on the table
// could be an observer, who decided to sit on a table and observe and would
// be prompted every time about the moves that should be made.
  public CricketPresence firstJoinee() {
    CricketPresence[] p = allPlayers(-1);
    for (int i = 0; i < p.length; i++) {
      if (_positions[p[i].pos()] == 0) {
        return p[i];
      }
    }
    _cat.warning("First joinee has to be present " + p.length);
    return p[0];
  }

  public void removeAll() {
    _size = 0;
    _count = 0;
    _positions = new int[] {};
    _players = null;
  }

  public void removeDisconnected(){
    CricketPresence[] p = allPlayers(-1);
    for (int i = 0; i < p.length; i++) {
      if (p[i].isDisconnected()){
        remove(p[i]);
        _cat.finest("Removing disconnected player " + p[i]);
      }
    }
  }

  class SelectorImpl
      implements PresenceSelector {

    public void startPos(int pos) {
      this.pos = pos;
    }

    int startPos() {
      return pos;
    }

    public CricketPresence selectPresence(Constraint c) {
      int l = _players.length;
      int e = l;
      int st = startPos() + 1;
      for (int i = (st == l ? 0 : st); --e > -1; i = (++i == l ? 0 : i)) {
        if (c.satisfy(_players[i])) {
          return _players[i];
        }
      }
      return null;
    }

    public CricketPresence selectPrevPresence(Constraint c) {
      int l = _players.length; // length
      int e = l;
      int st = startPos() - 1; // tentative start pos
      for (int i = (st == -1 ? (l - 1) : st); --e > -1;
           i = (--i == -1 ? (l - 1) : i)) {
        if (c.satisfy(_players[i])) {
          return _players[i];
        }
      }
      return null;
    }

    public CricketPresence[] select(Constraint c) {
      int l = _players.length; // length
      int e = l;
      ArrayList v = new ArrayList();
      int st = startPos() + 1; // tentative start pos
      for (int i = (st == l ? 0 : st); --e > -1; i = (++i == l ? 0 : i)) {
        if (c.satisfy(_players[i])) {
          v.add(_players[i]);
        }
      }
      v.trimToSize();
      return (CricketPresence[]) (v.toArray(new CricketPresence[] {}));
    }

    private int pos;
  }

}

interface PresenceSelector {
  CricketPresence selectPresence(Constraint c);

  CricketPresence selectPrevPresence(Constraint c);

  CricketPresence[] select(Constraint c);

  /***
   *        startPos is excluded
   ***/
  void startPos(int pos);
}

interface Constraint {
  boolean satisfy(CricketPresence p);
}
