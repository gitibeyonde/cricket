package com.cricket.mmog.cric.util;

import com.cricket.mmog.CricketPlayer;
import com.cricket.mmog.CricketPresence;
import com.cricket.mmog.GameType;
import com.cricket.mmog.cric.Cricket;


public class Pitch implements CricketConstants {
  String _gid;
  public Team _teamA, _teamB;
  String _teamAName, _teamBName;
  public Team _fielding, _batting;
  Cricket _g;

  public Pitch(Cricket g, String gid, String tnA, String tnB) {
    _g = g;
    _gid = gid;
    int ts = _g.type().teamSize();
    _teamA = new Team(tnA, gid, ts);
    _teamAName = tnA;
    _batting = _teamA;
    _teamB = new Team(tnB, gid, ts);
    _teamBName = tnB;
    _fielding = _teamB;

  }

  public void unsetNew() {
    _teamA.unsetNew();
    _teamB.unsetNew();
  }

  public void setCaptains() {
    _teamA.setCaptain();
    _teamB.setCaptain();
  }

  public void setGrid(long grid) {
    CricketPresence[] v = allPlayers();
    for (int i = 0; i < v.length; i++) {
      v[i].setGRID(grid);
    }
  }

  public void setFieldingTeamABattingTeamB() {
    _teamA.setFielding(0);
    _fielding = _teamA;
    _teamB.setBatting();
    //_teamB.setRunner();
    _batting = _teamB;
  }

  public void setFieldingTeamBBattingTeamA() {
    _teamB.setFielding(0);
    _fielding = _teamB;
    _teamA.setBatting();
    _batting = _teamA;
  }

  public void toggleBattingFielding() {
    if (_teamA.isBatting()) {
      setFieldingTeamABattingTeamB();
    }
    else {
      setFieldingTeamBBattingTeamA();
    }
  }

  public boolean advanceBowler() {
    CricketPresence bowl = _fielding.getBowler();
    if (bowl == null) {
      return false;
    }

    if (_fielding.activePlayers(0).length <= 1) {
      return true;
    }
    else {
      CricketPresence next = _fielding.nextActive(bowl.pos());
      _fielding.setFielding(next.pos());
      //System.out.println("New Bowler " + _fielding.getBowler());
      return true;
    }
  }

  public boolean advanceBatsman() {
    return _batting.setBatting();
  }

  public Team getTeamA() {
    return _teamA;
  }

  public Team getTeamB() {
    return _teamB;
  }

  public Team getFieldingTeam() {
    return _fielding;
  }

  public Team getBattingTeam() {
    return _batting;
  }

  public CricketPresence[] getBatsman() {
    return _batting.getBatsman();
  }

  public CricketPresence getBowler() {
    return _fielding.getBowler();
  }


  public CricketPresence[] getFielders() {
    return _fielding.getFielder();
  }


  public CricketPresence[] getBowlerAndFielders() {
    return _fielding.getBowlerAndFielder();
  }


  public boolean canJoinTeamA(CricketPresence p) {
    //System.out.println("Plr team = " + p._team_name + " team A = " +
    //                   _teamA._team_name);

    if (p._team_name.equals(_teamA._team_name)) {
      return _teamA.canJoin(p);
    }
    else {
      return false;
    }
  }


  public boolean canJoinTeamB(CricketPresence p) {
    //System.out.println("Plr team = " + p._team_name + " team B = " +
    //                  _teamB._team_name);

    if (p._team_name.equals(_teamB._team_name)) {
      return _teamB.canJoin(p);
    }
    else {
      return false;
    }
  }

  public boolean onPitch(CricketPresence p) {
    return _teamB.onTable(p) || _teamA.onTable(p);
  }

  public CricketPresence getTeamACaptain() {
    return _teamA.captain();
  }

  public CricketPresence getTeamBCaptain() {
    return _teamB.captain();
  }


  public CricketPresence[] allPlayers() {
    CricketPresence[] ta = null;
    CricketPresence[] tb = null;
    int length = 0;
    if (_teamA != null) {
      ta = _teamA.allPlayers(0);
      length += ta.length;
    }
    if (_teamB != null) {
      tb = _teamB.allPlayers(0);
      length += tb.length;
    }
    CricketPresence[] p = new CricketPresence[length];
    int i = 0;
    for (; ta != null && i < ta.length; i++) {
      p[i] = ta[i];
    }
    for (int j = 0; tb != null && j < tb.length; j++, i++) {
      p[i] = tb[j];
    }
    return p;
  }

  public void reset() {
    //System.out.println("RESETTING PITCH");
    _teamA.reset();
    _teamB.reset();
  }

  public void remove(CricketPresence p) {
    p.team().remove(p);
  }

  public void removeDisconnected() {
    _teamA.removeDisconnected();
    _teamB.removeDisconnected();
  }


  public String stringValue() {
    return "fielding=" + (_fielding != null ? _fielding.stringValue() : "") +
        ",batting=" + (_batting != null ? _batting.stringValue() : ",");

  }

  public String toString() {
    return "Ts=" + _teamA._size + ", " + "fielding=" +
        (_fielding != null ? _fielding.toString() : "") + ",batting=" +
        (_batting != null ? _batting.toString() : ",");
  }
}
