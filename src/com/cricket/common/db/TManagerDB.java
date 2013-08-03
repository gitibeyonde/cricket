package com.cricket.common.db;

import java.util.*;
import java.sql.*;
import java.util.logging.Logger;

import com.agneya.util.*;

import com.cricket.common.db.DBException;

// SQLSERVER/ORACLE

public class TManagerDB {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(TManagerDB.class.getName());

  public String tmId;
  public int conductedMatches;
  public int win;
  public int ranking;
  public String p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14;
  public java.util.Date creation;

  /**
     * */
  public static final String USERID_FK;
  public static final String MATCHES_CONDUCTED;
  public static final String WIN;
  public static final String PLR1_FK;
  public static final String PLR2_FK;
  public static final String PLR3_FK;
  public static final String PLR4_FK;
  public static final String PLR5_FK;
  public static final String PLR6_FK;
  public static final String PLR7_FK;
  public static final String PLR8_FK;
  public static final String PLR9_FK;
  public static final String PLR10_FK;
  public static final String PLR11_FK;
  public static final String PLR12_FK;
  public static final String PLR13_FK;
  public static final String PLR14_FK;
  public static final String RANKING;
  public static final String CREATION_TS;


  static {
    USERID_FK = "USERID_FK";
    MATCHES_CONDUCTED = "MATCHES_CONDUCTED";
    WIN = "WIN";
    PLR1_FK = "PLR1_FK";
    PLR2_FK = "PLR2_FK";
    PLR3_FK = "PLR3_FK";
    PLR4_FK = "PLR4_FK";
    PLR5_FK = "PLR5_FK";
    PLR6_FK = "PLR6_FK";
    PLR7_FK = "PLR7_FK";
    PLR8_FK = "PLR8_FK";
    PLR9_FK = "PLR9_FK";
    PLR10_FK = "PLR10_FK";
    PLR11_FK = "PLR11_FK";
    PLR12_FK = "PLR12_FK";
    PLR13_FK = "PLR13_FK";
    PLR14_FK = "PLR14_FK";
    RANKING = "RANKING";
    CREATION_TS = "CREATION_TS";

  }


  public TManagerDB(int gid) throws DBException {
    get(gid);
  }

  public boolean get(int gid) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;
    try {
      StringBuffer sb = new StringBuffer("select *");
      sb.append(" from T_TEAM_MANAGER where ");
      sb.append(USERID_FK).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, gid);
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        tmId = r.getString(USERID_FK);
        conductedMatches = r.getInt(MATCHES_CONDUCTED);
        win= r.getInt(WIN);
        p1= r.getString(PLR1_FK);
        p2= r.getString(PLR1_FK);
        p3= r.getString(PLR1_FK);
        p4= r.getString(PLR1_FK);
        p5= r.getString(PLR1_FK);
        p6= r.getString(PLR1_FK);
        p7= r.getString(PLR1_FK);
        p8= r.getString(PLR1_FK);
        p9= r.getString(PLR1_FK);
        p10= r.getString(PLR1_FK);
        p11= r.getString(PLR1_FK);
        p12= r.getString(PLR1_FK);
        p13= r.getString(PLR1_FK);
        p14= r.getString(PLR1_FK);
        ranking= r.getInt(RANKING);
        creation = r.getTimestamp(CREATION_TS);
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
        StringBuffer sb = new StringBuffer("insert into T_TEAM_MANAGER ( ");
        sb.append(USERID_FK).append(",");
        sb.append(MATCHES_CONDUCTED).append(",");
        sb.append(WIN).append(",");
        sb.append(PLR1_FK).append(",");
        sb.append(PLR2_FK).append(",");
        sb.append(PLR3_FK).append(",");
        sb.append(PLR4_FK).append(",");
        sb.append(PLR5_FK).append(",");
        sb.append(PLR6_FK).append(",");
        sb.append(PLR7_FK).append(",");
        sb.append(PLR8_FK).append(",");
        sb.append(PLR9_FK).append(",");
        sb.append(PLR10_FK).append(",");
        sb.append(PLR11_FK).append(",");
        sb.append(PLR12_FK).append(",");
        sb.append(PLR13_FK).append(",");
        sb.append(PLR14_FK).append(",");
        sb.append(RANKING).append(",");
        sb.append(CREATION_TS).append(")");
        if (ConnectionManager.isOracle()) {
          sb.append(
              " values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }
        else {
          sb.append(
              " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
        }
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        int i = 1;
        ps.setString(i++, tmId);
        ps.setInt(i++, conductedMatches);
        ps.setInt(i++, win);
        ps.setString(i++, p1);
        ps.setString(i++, p2);
        ps.setString(i++, p3);
        ps.setString(i++, p4);
        ps.setString(i++, p5);
        ps.setString(i++, p6);
        ps.setString(i++, p7);
        ps.setString(i++, p8);
        ps.setString(i++, p9);
        ps.setString(i++, p10);
        ps.setString(i++, p11);
        ps.setString(i++, p12);
        ps.setString(i++, p13);
        ps.setString(i++, p14);
        ps.setInt(i++, ranking);
        ps.setTimestamp(i++,
                        new java.sql.Timestamp(creation == null ? 0 :
                                               creation.getTime()));

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
    str.append("UserID = ").append(tmId).append("\n");
    str.append("conductedMatches = ").append(conductedMatches).append("\n");
    str.append("win = ").append(win).append("\n");
    str.append("ranking = ").append(ranking).append("\n");
    str.append("p1 = ").append(p1).append("\n");
    str.append("p2 = ").append(p2).append("\n");
    str.append("p3 = ").append(p3).append("\n");
    str.append("p4 = ").append(p4).append("\n");
    str.append("p5 = ").append(p5).append("\n");
    str.append("p6 = ").append(p6).append("\n");
    return (str.toString());
  }

  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;

}
