package com.cricket.common.db;

import java.util.*;
import java.sql.*;
import java.util.logging.Logger;

import com.agneya.util.*;
import com.cricket.common.interfaces.TournyInterface;
import com.cricket.common.db.DBException;
import com.cricket.common.db.DBPlayer;

public class DBTourny {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(DBTourny.class.getName());

  private int tournyId;
  private int gameType;
  private int tournyState;
  private String tournyName;
  private double buyIn1;
  private double buyIn2;
  private int schedule0;
  private int schedule1;
  private int schedule2;
  private int schedule3;
  private int schedule4;
  private int declInt;
  private int regInt;
  private int joinInt;
  private java.util.Date lastModified;
  private String userId;

  public static final String TOURNY_ID;
  public static final String TOURNY_NAME;
  public static final String GAME_TYPE;
  public static final String TOURNY_STATE;
  public static final String BUY_IN_0;
  public static final String BUY_IN_1;
  public static final String SCHEDULE_0;
  public static final String SCHEDULE_1;
  public static final String SCHEDULE_2;
  public static final String SCHEDULE_3;
  public static final String SCHEDULE_4;
  public static final String DECL_INT;
  public static final String REG_INT;
  public static final String JOIN_INT;
  public static final String LAST_MODIFIED_TS;

  /**
   * Tourny register
   */
  public static final String USER_ID;
  public static final String TOURNY_ID_FK;
  public static final String BUYIN;
  public static final String FEES;
  public static final String CHIPS;
  public static final String REGISTER_TS;

  static {
    TOURNY_ID = "TOURNY_ID_PK";
    GAME_TYPE = "GAME_TYPE_ID_FK";
    TOURNY_NAME = "TOURNEY_NAME";
    TOURNY_STATE = "TOURNEY_STATE";
    BUY_IN_0 = "BUY_IN_0";
    BUY_IN_1 = "BUY_IN_1";
    SCHEDULE_0 = "SCHEDULE_0";
    SCHEDULE_1 = "SCHEDULE_1";
    SCHEDULE_2 = "SCHEDULE_2";
    SCHEDULE_3 = "SCHEDULE_3";
    SCHEDULE_4 = "SCHEDULE_4";
    DECL_INT = "DECL_INT";
    REG_INT = "REG_INT";
    JOIN_INT = "JOIN_INT";
    LAST_MODIFIED_TS = "LAST_MODIFIED_TIMESTAMP";
    USER_ID = "USER_ID_FK";
    TOURNY_ID_FK = "TOURNY_ID_FK";
    BUYIN = "BUYIN";
    FEES = "FEES";
    CHIPS = "CHIPS";
    REGISTER_TS = "REGISTER_TIMESTAMP";
  }

  public static final short RUNNING = 1;
  public static final short STOPPED = 1;

  public DBTourny( int type, String name, int buyin, int fees, int[] sch, int di,
                  int ri, int ji) {
    tournyId = 0;
    gameType = type;
    tournyName = name;
    buyIn1 = buyin;
    buyIn2 = fees;
    schedule0 = sch[0];
    schedule1 = sch[1];
    schedule2 = sch[2];
    schedule3 = sch[3];
    schedule4 = sch[4];
    declInt = di;
    regInt = ri;
    joinInt = ji;
    lastModified = new java.util.Date();
    _modified = true;
  }

  public DBTourny(int gid) throws DBException {
    get(gid);
  }

  public boolean get(int gid) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;
    try {
      StringBuffer sb = new StringBuffer("select *");
      sb.append(" from T_TOURNY_LIVE where ");
      sb.append(TOURNY_ID).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, gid);
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        tournyId = r.getInt(TOURNY_ID);
        gameType = r.getInt(GAME_TYPE);
        tournyName = r.getString(TOURNY_NAME);
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
      _cat.warning("ERROR in fetching Tourny creation"+ e);
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
      throw new DBException(e.getMessage());
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
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    if (_modified) {
      try {
        StringBuffer sb = new StringBuffer("insert into T_TOURNY_LIVE ( ");
        sb.append(TOURNY_ID).append(",");
        sb.append(GAME_TYPE).append(",");
        sb.append(TOURNY_NAME).append(",");
        sb.append(BUY_IN_0).append(",");
        sb.append(BUY_IN_1).append(",");
        sb.append(SCHEDULE_0).append(",");
        sb.append(SCHEDULE_1).append(",");
        sb.append(SCHEDULE_2).append(",");
        sb.append(SCHEDULE_3).append(",");
        sb.append(SCHEDULE_4).append(",");
        sb.append(DECL_INT).append(",");
        sb.append(REG_INT).append(",");
        sb.append(JOIN_INT).append(",");
        sb.append(LAST_MODIFIED_TS).append(")");
        sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        int i = 1;
        ps.setInt(i++, tournyId);
        ps.setInt(i++, gameType);
        ps.setString(i++, tournyName);
        ps.setDouble(i++, buyIn1);
        ps.setDouble(i++, buyIn2);
        ps.setInt(i++, schedule0);
        ps.setInt(i++, schedule1);
        ps.setInt(i++, schedule2);
        ps.setInt(i++, schedule3);
        ps.setInt(i++, schedule4);
        ps.setInt(i++, declInt);
        ps.setInt(i++, regInt);
        ps.setInt(i++, joinInt);
        ps.setTimestamp(i++,
                        new java.sql.Timestamp(lastModified == null ?
                                               System.currentTimeMillis() :
                                               lastModified.getTime()));
        r = ps.executeUpdate();
        _cat.finest(this.toString());
        ps.close();
        conn.close();
      }
      catch (SQLException e) {
        _cat.warning("Unable to save Toruny " + e);
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
        throw new DBException(e.getMessage());
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

  public int updateState() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    if (_modified) {
      try {
        StringBuffer sb = new StringBuffer(
            "update T_LOGIN_SESSION_LIVE set ");
        sb.append(TOURNY_STATE).append(" = ? ");
        sb.append(" where ");
        sb.append(TOURNY_ID).append("= ? ");
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        ps.setInt(1, tournyState);
        ps.setInt(2, tournyId);
        r = ps.executeUpdate();
        ps.close();
        conn.close();
        _cat.finest(this.toString());
      }
      catch (SQLException e) {
        _cat.warning("Unable to update Player's Session " + e);
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
        throw new DBException(e.getMessage());
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

  public int register(DBPlayer gp, String session, TournyInterface tin) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    userId = gp.getDispName();
    int r = -1;
    try {
      conn = ConnectionManager.getConnection("GameEngine");
      conn.setAutoCommit(false);

      //r=gp.buyTournyChips(conn, session, tin.buyIn() + tin.fees());
        new Exception().printStackTrace();
      if (r == -1){
        conn.rollback();
        conn.close();
        return r;
      }

      StringBuffer sb = new StringBuffer(
          "insert into T_REGISTERED_TOURNY ( ");
      sb.append(USER_ID).append(",");
      sb.append(TOURNY_ID_FK).append(",");
      sb.append(BUYIN).append(",");
      sb.append(FEES).append(",");
      sb.append(CHIPS).append(",");
      sb.append(REGISTER_TS).append(")");
      _cat.finest(sb.toString());
      sb.append(" values ( ?, ?, ?, ?, ?, ? )");
      _cat.finest(userId + ", " + tournyId);
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, userId);
      ps.setInt(2, tournyId);
      ps.setDouble(3, tin.buyIn());
      ps.setDouble(4, tin.fees());
      ps.setDouble(5, tin.chips());
      ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
      r = ps.executeUpdate();
      ps.close();
      conn.commit();
      conn.close();
    }
    catch (SQLException e) {
      _cat.warning("Unable to save tourny registration info " + e);
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
      throw new DBException(e.getMessage());
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

    return r;
  }

  public static int getTournyChips(String userId, int gid) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int chips = -1;
    try {
      StringBuffer sb = new StringBuffer("select *");
      sb.append(" from T_REGISTERED_TOURNY where ");
      sb.append(TOURNY_ID_FK).append("= ?  and ");
      sb.append(USER_ID).append("=? ");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, gid);
      ps.setString(2, userId);
      _cat.finest(sb + ", " + gid + " ," + userId);
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        chips = r.getInt(CHIPS);
        r.close();
        ps.close();
        conn.close();
      }
      else {
        r.close();
        ps.close();
        conn.close();
      }
    }
    catch (SQLException e) {
      _cat.warning("Unable to update Player's Play Wallet " + e);
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
      throw new DBException(e.getMessage());
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

    _cat.finest("Tourny chips = " + chips);
    return chips;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("DBTourny:\n");
    str.append("TournyID = ").append(tournyId).append("\n");
    str.append("TournyName = ").append(tournyName).append("\n");
    str.append("GameType = ").append(gameType).append("\n");
    str.append("UserID = ").append(userId).append("\n");
    return (str.toString());
  }

  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;

}
