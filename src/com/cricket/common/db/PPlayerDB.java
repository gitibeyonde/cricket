package com.cricket.common.db;

import java.util.*;
import java.sql.*;
import java.util.logging.Logger;

import com.agneya.util.*;

import com.cricket.common.db.DBException;

// SQLSERVER/ORACLE

public class PPlayerDB {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(PPlayerDB.class.getName());

  public String ppId;
  public int pTournyPlayed;
  public int pTRuns;
  public int pTBalls;
  public int pTScoringRate;
  public int pTCatches;
  public int pWickets;
  public int agression;
  public int confidence;
  public int strength;
  public int agility;
  public int consistency;
  public int ranking;
  public int rating;
  public int enabled;
  public java.util.Date creation;
  public int hired;
  public String hirerId;
  public java.util.Date hireDate;

  public static final String USERID_FK;
  public static final String PROF_TOURNAMENTS;
  public static final String PT_RUNS;
  public static final String PT_BALLS;
  public static final String PT_SCORING_RATE;
  public static final String PT_CATCHES;
  public static final String PT_WICKETS;
  public static final String AGGRESSION;
  public static final String STRENGTH;
  public static final String AGILITY;
  public static final String CONFIDENCE;
  public static final String CONSISTENCY;
  public static final String RANKING;
  public static final String ENABLED;
  public static final String CREATION_TS;
  public static final String HIRED;
  public static final String HIRE_ID_FK;
  public static final String HIRE_TS;


  static {
    USERID_FK = "USERID_FK";
    PROF_TOURNAMENTS = "PROF_TOURNAMENTS";
    PT_RUNS = "PT_RUNS";
    PT_BALLS = "PT_BALLS";
    PT_SCORING_RATE = "PT_SCORING_RATE";
    PT_CATCHES = "PT_CATCHES";
    PT_WICKETS = "PT_WICKETS";
    AGGRESSION = "AGGRESSION";
    STRENGTH = "STRENGTH";
    AGILITY = "AGILITY";
    CONFIDENCE = "CONFIDENCE";
    CONSISTENCY = "CONSISTENCY";
    RANKING = "RANKING";
    ENABLED = "ENABLED";
    CREATION_TS = "CREATION_TS";
    HIRED = "HIRED";
    HIRE_ID_FK = "HIRE_ID_FK";
    HIRE_TS = "HIRE_TS";
  }


  public PPlayerDB(int gid) throws DBException {
    get(gid);
  }

  public boolean get(int gid) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;
    try {
      StringBuffer sb = new StringBuffer("select *");
      sb.append(" from T_PROFESSIONAL where ");
      sb.append(USERID_FK).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, gid);
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        ppId = r.getString(USERID_FK);
        pTournyPlayed = r.getInt(PROF_TOURNAMENTS);
        pTRuns= r.getInt(PT_RUNS);
        pTBalls= r.getInt(PT_BALLS);
        pTScoringRate= r.getInt(PT_SCORING_RATE);
        pTCatches= r.getInt(PT_CATCHES);
        pWickets= r.getInt(PT_WICKETS);
        agression= r.getInt(AGGRESSION);
        confidence= r.getInt(CONFIDENCE);
        strength= r.getInt(STRENGTH);
        agility= r.getInt(AGILITY);
        consistency= r.getInt(CONSISTENCY);
        ranking= r.getInt(RANKING);
        rating= r.getInt(CREATION_TS);
        enabled= r.getInt(ENABLED);
        creation = r.getTimestamp(CREATION_TS);
        hired= r.getInt(HIRED);
        hirerId= r.getString(HIRE_ID_FK);
        hireDate = r.getTimestamp(HIRE_TS);
        r.close();
        ps.close();
        conn.close();
        return true;
      }
      else {
        r.close();
        ps.close();
        conn.close();
        return false;
      }
    }
    catch (SQLException e) {
      _cat.warning("Error in getting game "+  e);
      try {
        if (ps != null) {
          ps.close();
        }
        if (conn != null) {
          conn.rollback();
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
      throw new DBException(e.getMessage() + " -- while getting Game");
    }
    finally {
      try {
        if (ps != null) {
          ps.close();
        }
        if (conn != null) {
          conn.close();
        }
      }
      catch (SQLException se) {
        //ignore
      }
    }
  }

  public int save() throws DBException {
    int r = -1;
    Connection conn = null;
    PreparedStatement ps = null;

    if (_modified) {
      try {
        StringBuffer sb = new StringBuffer("insert into T_PROFESSIONAL ( ");
        sb.append(USERID_FK).append(",");
        sb.append(PROF_TOURNAMENTS).append(",");
        sb.append(PT_RUNS).append(",");
        sb.append(PT_BALLS).append(",");
        sb.append(PT_SCORING_RATE).append(",");
        sb.append(PT_CATCHES).append(",");
        sb.append(PT_WICKETS).append(",");
        sb.append(AGGRESSION).append(",");
        sb.append(STRENGTH).append(",");
        sb.append(AGILITY).append(",");
        sb.append(CONFIDENCE).append(",");
        sb.append(CONSISTENCY).append(",");
        sb.append(RANKING).append(",");
        sb.append(ENABLED).append(",");
        sb.append(CREATION_TS).append(",");
        sb.append(HIRED).append(",");
        sb.append(HIRE_ID_FK).append(",");
        sb.append(HIRE_TS).append(")");
        if (ConnectionManager.isOracle()) {
          sb.append(
              " values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }
        else {
          sb.append(
              " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
        }
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        int i = 1;
        ps.setString(i++, ppId);
        ps.setInt(i++, pTournyPlayed);
        ps.setInt(i++, pTRuns);
        ps.setInt(i++, pTBalls);
        ps.setInt(i++, pTScoringRate);
        ps.setInt(i++, pTCatches);
        ps.setInt(i++, pWickets);
        ps.setInt(i++, agression);
        ps.setInt(i++, confidence);
        ps.setInt(i++, strength);
        ps.setInt(i++, agility);
        ps.setInt(i++, consistency);
        ps.setInt(i++, ranking);
        ps.setInt(i++, rating);
        ps.setInt(i++, enabled);
        ps.setTimestamp(i++,
                        new java.sql.Timestamp(creation == null ? 0 :
                                               creation.getTime()));

        ps.setInt(i++, hired);
        ps.setString(i++, hirerId);
        ps.setTimestamp(i++,
                        new java.sql.Timestamp(hireDate == null ? 0 :
                                               hireDate.getTime()));

        r = ps.executeUpdate();
        _cat.finest(this.toString());
        ps.close();
        conn.close();
      }
      catch (SQLException e) {
        _cat.warning("Unable to save  GRS" + e.getMessage()+  e);
        _cat.severe(this.toString());

        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.rollback();
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
        throw new DBException(e.getMessage() + " -- while getting Game");
      }
      finally {
        try {
          if (ps != null) {
            ps.close();
          }
          if (conn != null) {
            conn.close();
          }
        }
        catch (SQLException se) {
          //ignore
        }
      }

    }
    return r;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("PPPlayer:\n");
    str.append("UserID = ").append(ppId).append("\n");
    str.append("pTournyPlayed = ").append(pTournyPlayed).append("\n");
    str.append("pTRuns = ").append(pTRuns).append("\n");
    str.append("pTBalls = ").append(pTBalls).append("\n");
    str.append("hired = ").append(hired).append("\n");
    str.append("hirerId = ").append(hirerId).append("\n");
    return (str.toString());
  }

  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;

}
