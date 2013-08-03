package com.cricket.common.message;

import java.util.*;
import com.cricket.mmog.GameType;

/**
 * <p>Title: Octopus Poker</p>
 * <p>Description: MultiPlayer online gaming framework</p>
 * <p>Copyright: Copyright (c) 1999</p>
 * <p>Company: agneya Softech</p>
 * @author aprateek
 * @version 0.1
 */

public class GameEvent {
  HashMap _ge;

  public GameEvent() {}

  public GameEvent(String ge) {
    init(ge);
  }

  public void init(String ge) {
    _ge = new HashMap();
    if (ge==null || ge.equals("null"))return;
    String[] nv = ge.toString().split(",");
    int i = 0;
    for (; i < nv.length; i++) {
      int ind = nv[i].indexOf("=");
      if (ind == -1) {
        continue;
      }
      String name = nv[i].substring(0, ind).trim();
      String value = nv[i].substring(ind + 1).trim();
      //System.out.println("Game Event name = " + name + ",   value = " + value);
      _ge.put(name, value);
    }
  }

  public String get(String name) {
    return (String) _ge.get(name);
  }


  public String[][] getBattingPlayersDetails() {
    String plrs[][] = null;
    try {
      String p = (String) _ge.get("batting");
      if (p==null || p.length() <= 1) {
        return null;
      }
      String plrs_det[] = p.split("`");
      plrs = new String[plrs_det.length][10];
      for (int i = 0; i < plrs_det.length; i++) {
        String str = new String(plrs_det[i]);
        //System.out.println(str);
        String plr_det_elem[] = str.split("\\|");
        for (int j = 0; j < plr_det_elem.length; j++) {
          //System.out.println(plr_det_elem[j]);
          plrs[i][j] = plr_det_elem[j];
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return plrs;
  }

  public String[][] getFeildingPlayersDetails() {
    String plrs[][] = null;
    try {
      String p = (String) _ge.get("fielding");
      if (p==null || p.length() <= 1) {
        return null;
      }
      String plrs_det[] = p.split("`");
      plrs = new String[plrs_det.length][10];
      for (int i = 0; i < plrs_det.length; i++) {
        String str = new String(plrs_det[i]);
        //System.out.println("----------" + str);
        String plr_det_elem[] = str.split("\\|");
        for (int j = 0; j < plr_det_elem.length; j++) {
          //System.out.println("...." + plr_det_elem[j]);
          plrs[i][j] = new String(plr_det_elem[j]);
        }
      }
    }
    catch (Exception e) {
      //e.printStackTrace();
    }
    return plrs;
  }

  public int getMaxPlayers(){
    GameType gt = new GameType(Integer.parseInt((String) _ge.get("type")));
    return gt.teamSize();
  }

  public String[] getWaitersDetails() {
    String wtr_str = (String) _ge.get("waiters");
    if (wtr_str != null) {
      return wtr_str.split("`");
    }
    else {
      return null;
    }
  }

  public String[][] getTournyWinners() {
    String plrs[][] = null;
    try {
      String winners_det[] = ( (String) _ge.get("winners")).split("`");
      plrs = new String[winners_det.length][3];
      for (int i = 0; i < winners_det.length; i++) {
        String str = new String(winners_det[i]);
        String plr_det_elem[] = str.split("\\|");
        for (int j = 0; j < plr_det_elem.length; j++) {
          plrs[i][j] = plr_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return plrs;
  }

  public int getGameState() {
    String ts = (String) _ge.get("state");
    if (ts != null) {
      return Integer.parseInt(ts);
    }
    return -1;
  }

  public String getPlayerDetailsString() {
    String pd = ( (String) _ge.get("player-details"));
    //System.out.println("GE=" + toString() + "\nP=" + pd +  "\nP1=" + (String) _ge.get("player-details1"));
    if (pd == null) {
      pd = "none";
    }
    return pd;
  }

  public String getPlayerStats(){
    return ( (String) _ge.get("ps"));
  }

  public String getGameId(){
    return ( (String) _ge.get("name"));
  }
      
  public String[][] getNextMove() {
    String mvs[][] = null;
    try {
      String mvs_det[] = ( (String) _ge.get("next-move")).split("`");
      mvs = new String[mvs_det.length][5];
      for (int i = 0; i < mvs_det.length; i++) {
        String str = new String(mvs_det[i]);
        String mvs_det_elem[] = str.split("\\|");
        for (int j = 0; j < mvs_det_elem.length; j++) {
          mvs[i][j] = mvs_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return mvs;
  }

  public String getNextMoveString() {
    String pd = ( (String) _ge.get("next-move"));
    if (pd == null) {
      pd = "none";
    }
    return pd;
  }

  public String getIllegalMoveString() {
    String illMove = null;
    illMove = ( (String) _ge.get("illegal-move"));
    return illMove;
  }

  public String[][] getLastMove() {
    String mvs[][] = null;
    try {
      String mvs_det[] = ( (String) _ge.get("last-move")).split("`");
      mvs = new String[mvs_det.length][5];
      for (int i = 0; i < mvs_det.length; i++) {
        String str = new String(mvs_det[i]);
        String mvs_det_elem[] = str.split("\\|");
        for (int j = 0; j < mvs_det_elem.length; j++) {
          mvs[i][j] = mvs_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return mvs;
  }
  public String getLastMoveString() {
    String pd = ( (String) _ge.get("last-move"));
    if (pd == null) {
      pd = "khali";
    }
    return pd;
  }

  public String[] getRegisteredPlayer() {
    return ( (String) _ge.get("registered-player")).split("\\|");
  }

  public String[][] getWinner() {
    String mvs[][] = null;
    try {
      String mvs_det[] = ( (String) _ge.get("winners")).split("`");
      mvs = new String[mvs_det.length][7];
      for (int i = 0; i < mvs_det.length; i++) {
        String str = new String(mvs_det[i]);
        String mvs_det_elem[] = str.split("\\|");
        for (int j = 0; j < mvs_det_elem.length; j++) {
          mvs[i][j] = mvs_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return mvs;
  }

  public String getWinnerString() {
    return ( (String) _ge.get("winner"));
  }



  public double getRake() {
    String r = (String) get("rake");
    return r != null ? Double.parseDouble(r) : 0;
  }


  public int getGameRunId() {
    int dp = -1;
    String dps = (String) _ge.get("grid");
    if (dps != null) {
      dp = Integer.parseInt(dps);
    }
    return dp;
  }

  public int getMsgGid() {
    int dp = -1;
    String dps = (String) _ge.get("msgGID");
    if (dps != null) {
      dp = Integer.parseInt(dps);
    }
    return dp;
  }

  public int getResponseId() {
    int dp = -1;
    String dps = (String) _ge.get("response-id");
    if (dps != null) {
      dp = Integer.parseInt(dps);
    }
    return dp;
  }

  public String getPotString() {
    return (String) _ge.get("pots");
  }

  public String[][] getPot() {
    String pts[][] = null;
    try {
      String pts_det[] = ( (String) _ge.get("pots")).split("`");
      pts = new String[pts_det.length][2];
      for (int i = 0; i < pts_det.length; i++) {
        String str = new String(pts_det[i]);
        String mvs_det_elem[] = str.split("\\|");
        for (int j = 0; j < mvs_det_elem.length; j++) {
          pts[i][j] = mvs_det_elem[j];
        }
      }
    }
    catch (Exception e) {
    }
    return pts;
  }


  public String[] getPartners() {
    String[] partners = null;
    String plyrs = ( (String) _ge.get("partners"));
    if (! (plyrs == null || plyrs.equals("null")) && plyrs.length() > 1) {
      partners = plyrs.split("\\|");
    }
    return partners;
  }

  public int getType() {
    String type = (String) _ge.get("type");
    if (type != null) {
      return Integer.parseInt(type);
    }
    else {
      return -1;
    }
  }


  public String toString() {
    Iterator e = _ge.keySet().iterator();
    StringBuffer str = new StringBuffer();
    for (; e.hasNext(); ) {
      String key = (String) e.next();
      str.append(key).append("=").append(_ge.get(key)).append(",");
    }
    return str.toString();
  }

  public static void main(String[] args) {

  }

}
