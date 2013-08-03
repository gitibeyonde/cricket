package com.cricket.common.db;

import java.util.*;
import java.sql.*;

import com.agneya.util.*;

import com.cricket.common.db.DBException;

import java.util.logging.Logger;
import java.util.Hashtable;
import com.cricket.mmog.PlayerPreferences;

import com.cricket.common.db.DBPlayer;


// SQLSERVER/ORACLE

public class DBCricketPlayer {
  // set the category for logging
  static Logger _cat = Logger.getLogger(DBCricketPlayer.class.getName());
  public static final String USERID;

  public static final String KAGRESSION;
  public static final String KCONFIDENCE;
  public static final String KSTRENGTH;
  public static final String KAGILITY;
  public static final String KCONSISTENCY;

  public static final String BAGRESSION;
  public static final String BCONFIDENCE;
  public static final String BSTRENGTH;
  public static final String BAGILITY;
  public static final String BCONSISTENCY;

  public static final String FAGRESSION;
  public static final String FCONFIDENCE;
  public static final String FSTRENGTH;
  public static final String FAGILITY;
  public static final String FCONSISTENCY;

  public static final String FOURS;
  public static final String SIXES;
  public static final String RUNS;
  public static final String BALLS;
  public static final String BOWLS;
  public static final String SCORED;
  public static final String FIELD;
  public static final String CATCHES;
  public static final String WICKETS;


  private String userId;

  public int kagression;
  public int kconfidence;
  public int kstrength;
  public int kagility;
  public int kconsistency;

  public int bagression;
  public int bconfidence;
  public int bstrength;
  public int bagility;
  public int bconsistency;

  public int fagression;
  public int fconfidence;
  public int fstrength;
  public int fagility;
  public int fconsistency;

  public int fours;
  public int sixes;
  public int runs;
  public int balls;
  public int bowls;
  public int scored;
  public int field;
  public int catches;
  public int wickets;


  static {
    USERID = "USERID";

    KAGRESSION = "KAGRESSION";
    KCONFIDENCE = "KCONFIDENCE";
    KSTRENGTH = "KSTRENGTH";
    KAGILITY = "KAGILITY";
    KCONSISTENCY = "KCONSISTENCY";

    BAGRESSION = "BAGRESSION";
    BCONFIDENCE = "BCONFIDENCE";
    BSTRENGTH = "BSTRENGTH";
    BAGILITY = "BAGILITY";
    BCONSISTENCY = "BCONSISTENCY";

    FAGRESSION = "FAGRESSION";
    FCONFIDENCE = "FCONFIDENCE";
    FSTRENGTH = "FSTRENGTH";
    FAGILITY = "FAGILITY";
    FCONSISTENCY = "FCONSISTENCY";

    FOURS = "FOURS";
    SIXES = "SIXES";
    RUNS = "RUNS";
    BALLS = "BALLS";
    BOWLS = "BOWLS";
    SCORED = "SCORED";
    FIELD = "FIELD";
    CATCHES = "CATCHES";
    WICKETS = "WICKETS";

  }

  static boolean _logPlayGames = false;

  public DBCricketPlayer(String stat, int type) {
    String[] stats = stat.split("'");
    if (type == 1) {
      if (stats.length < 23)return;
      userId = stats[0];
      fours = Integer.parseInt(stats[1]);
      sixes = Integer.parseInt(stats[2]);
      runs = Integer.parseInt(stats[3]);
      bowls = Integer.parseInt(stats[4]);
      scored = Integer.parseInt(stats[5]);
      field = Integer.parseInt(stats[6]);
      catches = Integer.parseInt(stats[7]);
      wickets = Integer.parseInt(stats[8]);


      kagression = Integer.parseInt(stats[9]);
      kconfidence = Integer.parseInt(stats[10]);
      kstrength = Integer.parseInt(stats[11]);
      kagility = Integer.parseInt(stats[12]);
      kconsistency = Integer.parseInt(stats[13]);


      bagression = Integer.parseInt(stats[14]);
      bconfidence = Integer.parseInt(stats[15]);
      bstrength = Integer.parseInt(stats[16]);
      bagility = Integer.parseInt(stats[17]);
      bconsistency = Integer.parseInt(stats[18]);


      fagression = Integer.parseInt(stats[19]);
      fconfidence = Integer.parseInt(stats[20]);
      fstrength = Integer.parseInt(stats[21]);
      fagility = Integer.parseInt(stats[22]);
      fconsistency = Integer.parseInt(stats[23]);
    }
    else if (type == 2) {
      if (stats.length < 28)return;
      userId = stats[0];
      fours = Integer.parseInt(stats[1]);
      sixes = Integer.parseInt(stats[2]);
      runs = Integer.parseInt(stats[3]);
      bowls = Integer.parseInt(stats[4]);
      scored = Integer.parseInt(stats[5]);
      field = Integer.parseInt(stats[6]);
      catches = Integer.parseInt(stats[7]);
      wickets = Integer.parseInt(stats[8]);


      kagression = Integer.parseInt(stats[9]);
      kconfidence = Integer.parseInt(stats[10]);
      kstrength = Integer.parseInt(stats[11]);
      kagility = Integer.parseInt(stats[12]);
      kconsistency = Integer.parseInt(stats[13]);


      bagression = Integer.parseInt(stats[14]);
      bconfidence = Integer.parseInt(stats[15]);
      bstrength = Integer.parseInt(stats[16]);
      bagility = Integer.parseInt(stats[17]);
      bconsistency = Integer.parseInt(stats[18]);


      fagression = Integer.parseInt(stats[19]);
      fconfidence = Integer.parseInt(stats[20]);
      fstrength = Integer.parseInt(stats[21]);
      fagility = Integer.parseInt(stats[22]);
      fconsistency = Integer.parseInt(stats[23]);

    }

  }

  public DBCricketPlayer() {
    synchronized (this) {
      try {
        _logPlayGames = Configuration.getInstance().getBoolean(
            "Auditor.Log.PlayGame");
      }
      catch (ConfigurationException e) {
        _cat.warning("Configuration exception "+  e);
      }
    }
  }

  public DBCricketPlayer(String user) {
    userId = user;
    setModified(true);
  }


  public synchronized boolean get(String user) throws DBException {
    if (user == null) {
      return false;
    }
    Connection conn = null;
    PreparedStatement ps = null;
    try {
      StringBuffer sb = new StringBuffer("select *");
      sb.append(" from T_CRICKET_USER where ");
      sb.append(USERID).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, user);
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        userId = user;
        
        kagression = r.getInt(KAGRESSION);
        kconfidence = r.getInt(KCONFIDENCE);
        kstrength = r.getInt(KSTRENGTH);
        kagility = r.getInt(KAGILITY);
        kconsistency = r.getInt(KCONSISTENCY);

        bagression = r.getInt(BAGRESSION);
        bconfidence = r.getInt(BCONFIDENCE);
        bstrength = r.getInt(BSTRENGTH);
        bagility = r.getInt(BAGILITY);
        bconsistency = r.getInt(BCONSISTENCY);

        fagression = r.getInt(FAGRESSION);
        fconfidence = r.getInt(FCONFIDENCE);
        fstrength = r.getInt(FSTRENGTH);
        fagility = r.getInt(FAGILITY);
        fconsistency = r.getInt(FCONSISTENCY);

        fours = r.getInt(FOURS);
        sixes = r.getInt(SIXES);
        runs = r.getInt(RUNS);
        balls = r.getInt(BALLS);
        bowls = r.getInt(BOWLS);
        scored = r.getInt(SCORED);
        field = r.getInt(FIELD);
        catches = r.getInt(CATCHES);
        wickets = r.getInt(WICKETS);

        r.close();
        ps.close();
        conn.close();
        _cat.finest(this.toString());
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
      _cat.warning("Unable to get player " + e.getMessage()+  e);
      _cat.severe(this.toString());

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
      throw new DBException(e.getMessage() + " -- while retriving db player");
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


  public int save(String name) throws DBException {
    userId=name;
    return save();
  }

  public int save() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    try {
      StringBuffer sb = new StringBuffer("insert into T_CRICKET_USER (");
      sb.append(USERID).append(",");

      sb.append(KAGRESSION).append(",");
      sb.append(KCONFIDENCE).append(",");
      sb.append(KSTRENGTH).append(",");
      sb.append(KAGILITY).append(",");
      sb.append(KCONSISTENCY).append(",");

      sb.append(BAGRESSION).append(",");
      sb.append(BCONFIDENCE).append(",");
      sb.append(BSTRENGTH).append(",");
      sb.append(BAGILITY).append(",");
      sb.append(BCONSISTENCY).append(",");

      sb.append(FAGRESSION).append(",");
      sb.append(FCONFIDENCE).append(",");
      sb.append(FSTRENGTH).append(",");
      sb.append(FAGILITY).append(",");
      sb.append(FCONSISTENCY).append(",");

      sb.append(FOURS).append(",");
      sb.append(SIXES).append(",");
      sb.append(RUNS).append(",");
      sb.append(BALLS).append(",");
      sb.append(BOWLS).append(",");
      sb.append(SCORED).append(",");
      sb.append(FIELD).append(",");
      sb.append(CATCHES).append(",");
      sb.append(WICKETS).append(")");
      sb.append(" values (");
      sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
      _cat.finest(sb.toString());
      conn = ConnectionManager.getConnection("GameEngine");
      conn.setAutoCommit(true);
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, userId);
      
      ps.setInt(2, kagression);
      ps.setInt(3, kconfidence);
      ps.setInt(4, kstrength);
      ps.setInt(5, kagility);
      ps.setInt(6, kconsistency);

      ps.setInt(7, bagression);
      ps.setInt(8, bconfidence);
      ps.setInt(9, bstrength);
      ps.setInt(10, bagility);
      ps.setInt(11, bconsistency);

      ps.setInt(12, fagression);
      ps.setInt(13, fconfidence);
      ps.setInt(14, fstrength);
      ps.setInt(15, fagility);
      ps.setInt(16, fconsistency);

      ps.setInt(17, fours);
      ps.setInt(18, sixes);
      ps.setInt(19, runs);
      ps.setInt(20, balls);
      ps.setInt(21, bowls);
      ps.setInt(22, scored);
      ps.setInt(23, field);
      ps.setInt(24, catches);
      ps.setInt(25, wickets);
      r = ps.executeUpdate();
      _cat.finest(this + "," + r);
      ps.close();
      //conn.commit();
      conn.close();

    }
    catch (SQLException e) {
      e.printStackTrace();
      _cat.warning("Unable to save Player info " + e.getMessage() + this);

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
      throw new DBException(e.getMessage() + " -- while insertinf user " +
                            userId);
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

  public synchronized int updateStats() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    try {
      // provider ?
      conn = ConnectionManager.getConnection("GameEngine");

      StringBuffer sb = new StringBuffer("update T_CRICKET_USER set ");

      sb.append(KAGRESSION).append(" = ?, ");
      sb.append(KCONFIDENCE).append(" = ?, ");
      sb.append(KSTRENGTH).append(" = ?, ");
      sb.append(KAGILITY).append(" = ?, ");
      sb.append(KCONSISTENCY).append(" = ?, ");

      sb.append(BAGRESSION).append(" = ?, ");
      sb.append(BCONFIDENCE).append(" = ?, ");
      sb.append(BSTRENGTH).append(" = ?, ");
      sb.append(BAGILITY).append(" = ?, ");
      sb.append(BCONSISTENCY).append(" = ?, ");

      sb.append(FAGRESSION).append(" = ?, ");
      sb.append(FCONFIDENCE).append(" = ?, ");
      sb.append(FSTRENGTH).append(" = ?, ");
      sb.append(FAGILITY).append(" = ?, ");
      sb.append(FCONSISTENCY).append(" = ?, ");

      sb.append(FOURS).append(" = ?, ");
      sb.append(SIXES).append(" = ?, ");
      sb.append(RUNS).append(" = ?, ");
      sb.append(BALLS).append(" = ?, ");
      sb.append(BOWLS).append(" = ?, ");
      sb.append(SCORED).append(" = ?, ");
      sb.append(FIELD).append(" = ?, ");
      sb.append(CATCHES).append(" = ?, ");
      sb.append(WICKETS).append(" = ?");
      sb.append(" where ");
      sb.append(USERID).append("= ?");
      _cat.finest(sb.toString());
      ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, kagression);
      ps.setInt(2, kconfidence);
      ps.setInt(3, kstrength);
      ps.setInt(4, kagility);
      ps.setInt(5, kconsistency);

      ps.setInt(6, bagression);
      ps.setInt(7, bconfidence);
      ps.setInt(8, bstrength);
      ps.setInt(9, bagility);
      ps.setInt(10, bconsistency);

      ps.setInt(11, fagression);
      ps.setInt(12, fconfidence);
      ps.setInt(13, fstrength);
      ps.setInt(14, fagility);
      ps.setInt(15, fconsistency);

      ps.setInt(16, fours);
      ps.setInt(17, sixes);
      ps.setInt(18, runs);
      ps.setInt(19, balls);
      ps.setInt(20, bowls);
      ps.setInt(21, scored);
      ps.setInt(22, field);
      ps.setInt(23, catches);
      ps.setInt(24, wickets);

      ps.setString(25, userId);

      _cat.finest("Update" + this);
      r = ps.executeUpdate();
      ps.close();
      //conn.commit();
      conn.close();
    }
    catch (SQLException e) {
      _cat.info("Unable to update Player's Play Wallet " + e.getMessage());
      _cat.info(this.toString());

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
      throw new DBException(e.getMessage() + " -- whilerefreshing bankroll");
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




  /**
   * Set the value of Password
   *
   * @param v new value
   */
  public void setDispName(String v) {
    userId = v;
    setModified(true);
  }


  /**
   * Get the DispName
   *
   * @return String
   */
  public String getDispName() {
    return userId;
  }



  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;


  /**
   * Promotion and Commission
   *
   * type 0 - user register bonus, 1 - Deposit bonus
   * @return int
   */
  public double calBonus(String userid, String bonus_code, double amnt,
                         int type) throws DBException {
    double total = 0;
    Connection conn = null;
    PreparedStatement ps = null;
    StringBuffer sb = new StringBuffer();

    if (type == 0 || type == 1) {

      try {
        sb.append("SELECT OFFER_TYPE_ID_FK FROM T_PROMOTION WHERE PROMO_NAME");
        sb.append("=?");

        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        ps.setString(1, bonus_code);
        _cat.finest(sb.toString());
        String offerId = null;
        ResultSet r = ps.executeQuery();
        if (r.next()) {
          offerId = r.getString("OFFER_TYPE_ID_FK");
        }
        r.close();
        ps.close();

        if (offerId != null) {
          int offer_percent = 0;
          double offer_max = 0;
          double offer_amount = 0;
          double offer_register = 0;

          sb = new StringBuffer();
          sb.append(
              "SELECT OFFER_PERCENT,OFFER_MAX,OFFER_AMOUNT,OFFER_REGISTER ");
          sb.append("FROM T_OFFER_TYPE WHERE OFFER_TYPE_ID_PK");
          sb.append("=?");

          ps = conn.prepareStatement(sb.toString());
          ps.setString(1, offerId);
          _cat.finest(sb.toString());
          ResultSet rs = ps.executeQuery();
          if (rs.next()) {
            offer_percent = rs.getInt("OFFER_PERCENT");
            offer_max = rs.getDouble("OFFER_MAX");
            offer_amount = rs.getDouble("OFFER_AMOUNT");
            offer_register = rs.getDouble("OFFER_REGISTER");
          }
          if (type == 0) {
            total = offer_register;
          }
          else if (type == 1) {
            if (offer_amount > 0) {
              total = offer_amount;
            }
            else {
              total = (amnt * offer_percent);
              if (total > 0) {
                total = total / 100;
              }
              if (total > offer_max) {
                total = offer_max;
              }
            }
          }

          rs.close();
          ps.close();
        }
        conn.close();
        if (total > 0) {
          updateUserBonus(userid, total);
        }
      }
      catch (SQLException e) {
        _cat.warning("Unable to calculate bonus " + e.getMessage()+  e);
        _cat.severe(this.toString());
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
        throw new DBException(e.getMessage() + " -- while calculating bonus");
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
    return total;
  }

  public void updateUserBonus(String userid, double amnt) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    StringBuffer sb = new StringBuffer();
    sb.append("UPDATE T_USER SET BINGO_BONUS_AMOUNT = BINGO_BONUS_AMOUNT + ");
    sb.append("? WHERE USERID").append("=?");

    try {
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setDouble(1, amnt);
      ps.setString(2, userid);
      _cat.finest(sb.toString());

      int r = ps.executeUpdate();
      ps.close();
      conn.close();
    }
    catch (SQLException e) {
      _cat.warning("Unable to update bonus in user table " + e.getMessage()+  e);
      _cat.severe(this.toString());
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
      throw new DBException(e.getMessage() + " -- while updating bonus");
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


  public String stringValue() {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(this.playerStat());
    return sbuf.toString();
  }



  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("Player:\n");
    str.append("DispName = ").append(getDispName()).append("\n");

    str.append("kagression = ").append(kagression).append(",");
    str.append("kconfidence = ").append(kconfidence).append(",");
    str.append("kstrength = ").append(kstrength).append(",");
    str.append("kagility = ").append(kagility).append(",");
    str.append("kconsistency = ").append(kconsistency).append("\n");

    str.append("bagression = ").append(bagression).append(",");
    str.append("bconfidence = ").append(bconfidence).append(",");
    str.append("bstrength = ").append(bstrength).append(",");
    str.append("bagility = ").append(bagility).append(",");
    str.append("bconsistency = ").append(bconsistency).append("\n");

    str.append("fagression = ").append(fagression).append(",");
    str.append("fconfidence = ").append(fconfidence).append(",");
    str.append("fstrength = ").append(fstrength).append(",");
    str.append("fagility = ").append(fagility).append(",");
    str.append("fconsistency = ").append(fconsistency).append("\n");


    str.append("fours = ").append(fours).append(",");
    str.append("sixes = ").append(sixes).append(",");
    str.append("runs = ").append(runs).append(",");
    str.append("bowls = ").append(bowls).append(",");
    str.append("scored = ").append(scored).append(",");
    str.append("field = ").append(field).append(",");
    str.append("catches = ").append(catches).append(",");
    str.append("wickets = ").append(wickets).append("\n");


    return (str.toString());
  }

  public String playerStat() {
    StringBuffer sbuf = new StringBuffer();

    sbuf.append(getDispName()).append("'");
    sbuf.append(fours).append("'");
    sbuf.append(sixes).append("'");
    sbuf.append(runs).append("'");
    sbuf.append(bowls).append("'");
    sbuf.append(scored).append("'");
    sbuf.append(field).append("'");
    sbuf.append(catches).append("'");
    sbuf.append(wickets).append("'");


    sbuf.append(kagression).append("'");
    sbuf.append(kconfidence).append("'");
    sbuf.append(kstrength).append("'");
    sbuf.append(kagility).append("'");
    sbuf.append(kconsistency).append("'");

    sbuf.append(bagression).append("'");
    sbuf.append(bconfidence).append("'");
    sbuf.append(bstrength).append("'");
    sbuf.append(bagility).append("'");
    sbuf.append(bconsistency).append("'");

    sbuf.append(fagression).append("'");
    sbuf.append(fconfidence).append("'");
    sbuf.append(fstrength).append("'");
    sbuf.append(fagility).append("'");
    sbuf.append(fconsistency);

    return sbuf.toString();
  }


  public static void main(String[] args) throws Exception {
   
  }

}
