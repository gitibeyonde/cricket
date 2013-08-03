package com.cricket.common.db;

import java.util.*;
import java.sql.*;

import com.agneya.util.*;

import com.cricket.common.db.DBException;

import java.util.logging.Logger;
import java.util.Hashtable;
import com.cricket.mmog.PlayerPreferences;


// SQLSERVER/ORACLE

public class DBPlayer {
  // set the category for logging
  static Logger _cat = Logger.getLogger(DBPlayer.class.getName());
  public static final String USERID;
  public static final String PASSWORD;
  public static final String EMAIL_ID;
  public static final String TITLE;
  public static final String FIRST_NAME;
  public static final String MIDDLE_NAME;
  public static final String LAST_NAME;
  public static final String GENDER;
  public static final String DOB;
  public static final String AFFILIATE;
  public static final String ROLE_MASK;
  public static final String BONUS_CODE;
  public static final String PREFERENCES;
  public static final String REG_TS;
  public static final String POINTS;
  public static final String POINTS_TS;
  public static final String PLAY_CHIPS;
  public static final String PLAY_CHIPS_TS;

  public static final String RANK;

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

  public static final String VALIDATEID;
  public static final String LOCALE;

  private String userId;
  private String password;
  private String emailId;
  private String title;
  private String fname;
  private String mname;
  private String lname;
  private int gender;
  private java.util.Date dob;
  private String affiliate;
  private int role_mask;
  private String bonusCode;
  private int preferences;
  private java.sql.Timestamp regTs;
  private double playChips;
  private java.sql.Timestamp playChipsTs;

  private int points;
  private java.sql.Timestamp pointsTs;
  private int rank;

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

  private String validated;
  private String locale;

  static {
    USERID = "USERID";
    PASSWORD = "PASSWORD";
    EMAIL_ID = "EMAILID";
    TITLE = "TITLE";
    FIRST_NAME = "FIRSTNAME";
    MIDDLE_NAME = "MIDDLENAME";
    LAST_NAME = "LASTNAME";
    GENDER = "GENDER";
    DOB = "DOB";
    AFFILIATE = "AFFILIATE_ID_FK";
    ROLE_MASK = "ROLE_MASK";
    BONUS_CODE = "REG_BONUS_CODE";
    PREFERENCES = "PREFERENCES";
    REG_TS = "REG_TIMESTAMP";
    RANK = "RANK";
    POINTS = "POINTS";
    POINTS_TS = "POINTS_TIMESTAMP";
    PLAY_CHIPS = "PLAY_CHIPS";
    PLAY_CHIPS_TS = "PLAY_CHIPS_TIMESTAMP";


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

    VALIDATEID = "VALIDATEID";
    LOCALE = "LOCALE";
  }

  static boolean _logPlayGames = false;

  public DBPlayer(String stat, int type) {
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

      affiliate = stats[24];
      emailId = stats[25];
      gender = Integer.parseInt(stats[26]);
      preferences = Integer.parseInt(stats[27]);
      rank = Integer.parseInt(stats[28]);

    }

  }

  public DBPlayer() {
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

  public DBPlayer(String user, String pass) {
    userId = user;
    password = MD5.encode(pass);
    setModified(true);
  }

  public boolean get(String user, String passwd, String affl) throws
      DBException {

    boolean player_authenticated = false;
    _cat.finest("Affiliate =" + affl);

    // try the default login
    setAffiliate(affl);
    player_authenticated = get(user, passwd);
    /**if (affiliate != null && !affl.equals(affiliate) &&
        !affiliate.equals("admin")) {
      player_authenticated = false;
    }**/
    return player_authenticated;
  }

  public synchronized boolean get(String user, String pass) throws DBException {
    if (user == null) {
      return false;
    }
    Connection conn = null;
    PreparedStatement ps = null;
    try {
      StringBuffer sb = new StringBuffer("select *");
      sb.append(" from T_USER where ");
      sb.append(USERID).append("=? and ");
      sb.append(PASSWORD).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, user);
      ps.setString(2, MD5.encode(pass));
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        userId = user;
        password = pass;
        emailId = r.getString(EMAIL_ID);
        fname = r.getString(FIRST_NAME);
        lname = r.getString(LAST_NAME);
        affiliate = r.getString(AFFILIATE);
        gender = r.getInt(GENDER);
        dob = r.getDate(DOB);
        affiliate = r.getString(AFFILIATE);
        role_mask = r.getInt(ROLE_MASK);
        rank = r.getInt(RANK);
        regTs = r.getTimestamp(REG_TS);
        bonusCode = r.getString(BONUS_CODE);
        preferences = r.getInt(PREFERENCES);
        points = r.getInt(POINTS);
        pointsTs = r.getTimestamp(POINTS_TS);

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

        validated = r.getString(VALIDATEID);
        locale = r.getString(LOCALE);

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

  public synchronized boolean get(String user) throws DBException {
    if (user == null) {
      return false;
    }
    Connection conn = null;
    PreparedStatement ps = null;
    try {
      StringBuffer sb = new StringBuffer("select *");
      sb.append(" from T_USER where ");
      sb.append(USERID).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, user);
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        userId = user;
        password = r.getString(PASSWORD);
        emailId = r.getString(EMAIL_ID);
        fname = r.getString(FIRST_NAME);
        lname = r.getString(LAST_NAME);
        affiliate = r.getString(AFFILIATE);
        gender = r.getInt(GENDER);
        dob = r.getDate(DOB);
        affiliate = r.getString(AFFILIATE);
        role_mask = r.getInt(ROLE_MASK);
        rank = r.getInt(RANK);
        regTs = r.getTimestamp(REG_TS);
        bonusCode = r.getString(BONUS_CODE);
        preferences = r.getInt(PREFERENCES);
        points = r.getInt(POINTS);
        pointsTs = r.getTimestamp(POINTS_TS);

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

        validated = r.getString(VALIDATEID);
        locale = r.getString(LOCALE);


        if (affiliate != r.getString(AFFILIATE)) {
          _cat.warning("Affiliate in the database " + r.getString(AFFILIATE) +
                     " is not same as the passed affiliate " + affiliate);
        }

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

  public boolean checkPassword(String pass) {
    return MD5.encode(pass).equals(password);
  }

  public boolean refreshPoints() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    try {
      StringBuffer sb = new StringBuffer("select ");
      sb.append(POINTS);
      sb.append(" from T_USER where ");
      sb.append(USERID).append("=? and ");
      sb.append(PASSWORD).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, userId);
      ps.setString(2, MD5.encode(password));
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        points = r.getInt(POINTS);
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
      _cat.warning("Unable to update Player's Session " + e.getMessage()+  e);
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

  }

  public int updatePreferences() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    try {
      StringBuffer sb = new StringBuffer("update T_USER  set ");
      sb.append(PREFERENCES).append(" = ? ");
      sb.append(" where ");
      sb.append(USERID).append("= ? ");
      _cat.finest(sb.toString());
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, preferences);
      ps.setString(2, userId);
      r = ps.executeUpdate();
      ps.close();
      //conn.commit();
      conn.close();
      _cat.finest(this.toString());
    }
    catch (SQLException e) {
      _cat.warning("Unable to update Player's preferences " + e.getMessage()+  e);
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

  public int register() throws DBException {
    return save();
  }

  public int save() throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    try {
      StringBuffer sb = new StringBuffer("insert into T_USER (");
      sb.append(USERID).append(",");
      sb.append(PASSWORD).append(",");
      sb.append(EMAIL_ID).append(",");
      sb.append(GENDER).append(",");
      sb.append(DOB).append(",");
      sb.append(AFFILIATE).append(",");
      sb.append(RANK).append(",");
      sb.append(ROLE_MASK).append(",");
      sb.append(REG_TS).append(",");
      sb.append(BONUS_CODE).append(",");
      sb.append(POINTS).append(",");
      sb.append(POINTS_TS).append(",");
      sb.append(PLAY_CHIPS).append(",");
      sb.append(PLAY_CHIPS_TS).append(",");

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
      sb.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
      _cat.finest(sb.toString());
      conn = ConnectionManager.getConnection("GameEngine");
      conn.setAutoCommit(true);
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, userId);
      ps.setString(2, password);
      ps.setString(3, emailId);
      ps.setInt(4, gender);
      ps.setTimestamp(5, new java.sql.Timestamp(dob == null ? 0 : dob.getTime()));
      ps.setString(6, affiliate == null ? "admin" : affiliate);
      ps.setInt(7, rank);
      ps.setShort(8, (short) 1);
      ps.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));
      ps.setString(10, bonusCode);
      ps.setDouble(11, points);
      ps.setTimestamp(12,
          pointsTs = new java.sql.Timestamp(System.currentTimeMillis()));

      ps.setDouble(13, playChips);
      ps.setTimestamp(14,
          playChipsTs = new java.sql.Timestamp(System.currentTimeMillis()));

      ps.setInt(15, kagression);
      ps.setInt(16, kconfidence);
      ps.setInt(17, kstrength);
      ps.setInt(18, kagility);
      ps.setInt(19, kconsistency);

      ps.setInt(20, bagression);
      ps.setInt(21, bconfidence);
      ps.setInt(22, bstrength);
      ps.setInt(23, bagility);
      ps.setInt(24, bconsistency);

      ps.setInt(25, fagression);
      ps.setInt(26, fconfidence);
      ps.setInt(27, fstrength);
      ps.setInt(28, fagility);
      ps.setInt(29, fconsistency);

      ps.setInt(30, fours);
      ps.setInt(31, sixes);
      ps.setInt(32, runs);
      ps.setInt(33, balls);
      ps.setInt(34, bowls);
      ps.setInt(35, scored);
      ps.setInt(36, field);
      ps.setInt(37, catches);
      ps.setInt(38, wickets);
      r = ps.executeUpdate();
      _cat.finest(this + "," + r);
      ps.close();
      //conn.commit();
      conn.close();

      // if bonus code is not null then update the bonus
      if (bonusCode != null && bonusCode.trim().length() > 3) {
        calBonus(userId, bonusCode, 0, 0);
      }
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

      StringBuffer sb = new StringBuffer("update T_USER set ");
      sb.append(POINTS).append(" = ?, ");
      sb.append(PREFERENCES).append(" = ?, ");
      sb.append(POINTS_TS).append(" = ?, ");

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
      ps.setDouble(1, points);
      ps.setInt(2, preferences);
      ps.setTimestamp(3,
          pointsTs = new java.sql.Timestamp(System.currentTimeMillis()));

      ps.setInt(4, kagression);
      ps.setInt(5, kconfidence);
      ps.setInt(6, kstrength);
      ps.setInt(7, kagility);
      ps.setInt(8, kconsistency);

      ps.setInt(9, bagression);
      ps.setInt(10, bconfidence);
      ps.setInt(11, bstrength);
      ps.setInt(12, bagility);
      ps.setInt(13, bconsistency);

      ps.setInt(14, fagression);
      ps.setInt(15, fconfidence);
      ps.setInt(16, fstrength);
      ps.setInt(17, fagility);
      ps.setInt(18, fconsistency);

      ps.setInt(19, fours);
      ps.setInt(20, sixes);
      ps.setInt(21, runs);
      ps.setInt(22, balls);
      ps.setInt(23, bowls);
      ps.setInt(24, scored);
      ps.setInt(25, field);
      ps.setInt(26, catches);
      ps.setInt(27, wickets);

      ps.setString(28, userId);

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


  public int buyTournyChips(Connection conn, String session, double amount) throws
      DBException {
    PreparedStatement ps = null;

    int r = -1;
    if (points < amount) {
      return r;
    }
    points -= amount;

    try {
      /**
       * Create a transactio in the DBWallet
       */
      DBWallet dbw = new DBWallet();
      dbw.setDisplayName(userId);
      dbw.setSession(session);
      dbw.setAmount(amount);
      dbw.setOperation(DBWallet.BUY_TOURNY_CHIPS);
      dbw.setAffiliate(affiliate);
      _cat.finest(dbw.toString());
      dbw.save(conn);

      StringBuffer sb = new StringBuffer("update T_USER set ");
      sb.append(POINTS).append(" = ?, ");
      sb.append(POINTS_TS).append(" = ? ");
      sb.append(" where ");
      sb.append(USERID).append("= ?");
      _cat.finest(sb.toString());
      ps = conn.prepareStatement(sb.toString());
      ps.setDouble(1, points);
      ps.setTimestamp(2,
          pointsTs = new java.sql.Timestamp(System.currentTimeMillis()));
      ps.setString(3, userId);
      r = ps.executeUpdate();
      ps.close();
      //ConnectionManager.returnConnection(conn, "GameEngine");
      _cat.finest("Buying real chips for " + amount + "Player = " + this);
    }
    catch (SQLException e) {
      _cat.warning("Unable to update Player's Play Wallet " + e.getMessage()+  e);
      _cat.warning(this.toString());

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
      throw new DBException(e.getMessage() + " -- while buyTournyChips");
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

  public static int updatePlayerRank(String uid, int rank) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;
    try {
      /**
       * Create a transactio in the DBWallet
       */
      conn = ConnectionManager.getConnection("GameEngine");
      //conn.setAutoCommit(false);

      StringBuffer sb = new StringBuffer("update T_USER set ");
      sb.append(RANK).append(" = ? ");
      sb.append(" where ");
      sb.append(USERID).append("= ?");
      _cat.finest(sb.toString());
      ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, rank);
      ps.setString(2, uid);
      r = ps.executeUpdate();
      ps.close();
      //conn.commit();
      conn.close();
    }
    catch (SQLException e) {
      _cat.warning("Unable to update Player's Rank " + e.getMessage()+  e);

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

  public int addToPoints(String session, double amount, String type) throws
      DBException {
    return addToPoints(session, amount, type, -1);
  }

  public int addToPoints(String session, double amount, String type,
                         int transid) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;

    int r = -1;

    // refresh the amounts
    //refreshBankroll();
    //realBankroll += amount;
    try {
      /**
       * Create a transactio in the DBWallet
       */
      conn = ConnectionManager.getConnection("GameEngine");
      //conn.setAutoCommit(false);
      int trans_type;
      String trans_desc;
      if (type.equals("sitngo")) {
        trans_type = DBWallet.SITNGO_WIN;
        trans_desc = "Credit of sitngo win money to affiliate account";
      }
      else if (type.equals("mtt")) {
        trans_type = DBWallet.TOURNY_WIN;
        trans_desc = "Credit of tourny win money to affiliate account";
      }
      else if (type.equals("paynova")) {
        trans_type = DBWallet.PAYNOVA;
        trans_desc = "Credit of tourny win money to affiliate account";
      }
      else if (type.equals("wire")) {
        trans_type = DBWallet.WIRE;
        trans_desc = "The wire from user has been received";
      }
      else if (type.equals("cheque")) {
        trans_type = DBWallet.CHEQUE;
        trans_desc = "Cheque is cleared by the bank";
      }
      else if (type.equals("cash")) {
        trans_type = DBWallet.CASH;
        trans_desc = "Cash received from user";
      }
      else if (type.equals("netteller")) {
        trans_type = DBWallet.NETTELLER;
        trans_desc = "Credit of tourny win money to affiliate account";
      }
      else {
        if (amount < 0) {
          trans_type = DBWallet.RM_REDEEMPTION;
        }
        else {
          trans_type = DBWallet.RM_BANKROLL;
        }
        trans_desc = "Real money credit to affiliate";
      }

      DBWallet dbw = new DBWallet();
      dbw.setDisplayName(userId);
      dbw.setSession(session);
      dbw.setAmount(amount);
      dbw.setOperation(trans_type);
      dbw.setAffiliate(affiliate);
      _cat.finest(dbw.toString());

      if (transid > 0) {
        dbw.save(conn, transid);
      }
      else {
        dbw.save(conn);
      }

      StringBuffer sb = new StringBuffer("update T_USER set ");
      sb.append(POINTS).append(" = ?, ");
      sb.append(POINTS_TS).append(" = ? ");
      sb.append(" where ");
      sb.append(USERID).append("= ?");
      _cat.finest(sb.toString());
      ps = conn.prepareStatement(sb.toString());
      ps.setDouble(1, points);
      ps.setTimestamp(2,
          pointsTs = new java.sql.Timestamp(System.currentTimeMillis()));
      ps.setString(3, userId);
      r = ps.executeUpdate();
      ps.close();
      //conn.commit();
      conn.close();
      _cat.finest("adding money to real bankroll " + amount + "Player = " + this);
    }
    catch (SQLException e) {
      _cat.warning("Unable to update Player's Play Wallet " + e.getMessage()+  e);
      _cat.severe(this.toString().toString());

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


//PlayerPreferences.BANNED_PLAYER
  public static void modifyPreferences(String name, int new_pref, boolean add) {
    Connection conn = null;
    PreparedStatement ps = null;

    int pref = -1;
    try {
      /**
       * Create a transactio in the DBWallet
       */
      conn = ConnectionManager.getConnection("GameEngine");
      //conn.setAutoCommit(false);
      StringBuffer sb = new StringBuffer("select ");
      sb.append(PREFERENCES);
      sb.append(" from T_USER where ");
      sb.append(USERID).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, name);
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        pref = r.getInt(PREFERENCES);
      }
      r.close();
      ps.close();

      if (pref == -1) {
        _cat.warning("Unable to get prefrences from the DB for player " + name);
        conn.close();
        return;
      }

      sb = new StringBuffer("update T_USER set ");
      sb.append(PREFERENCES).append(" = ? ");
      sb.append(" where ");
      sb.append(USERID).append("= ?");
      _cat.finest(sb.toString());
      ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, add ? (pref | new_pref) : (pref & ~new_pref));
      ps.setString(2, name);
      ps.executeUpdate();
      ps.close();
      //conn.commit();
      conn.close();
    }
    catch (SQLException e) {
      _cat.warning("Unable to ban/unban player " + e.getMessage()+  e);

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


  /**
   * Get the Password
   *
   * @return String
   */
  public String getPassword() {
    return password;
  }


  /**
   * Set the value of Password
   *
   * @param v new value
   */
  public void setPassword(String v) {
    password = MD5.encode(v);
    setModified(true);
  }

  public String getFirstName() {
    return fname;
  }

  public void setFirstName(String v) {
    if (!v.equals(this.fname)) {
      this.fname = v;
      setModified(true);
    }
  }

  public String getLastName() {
    return lname;
  }

  public void setLastName(String v) {
    if (!v.equals(this.lname)) {
      this.lname = v;
      setModified(true);
    }
  }


  /**
   * Get the EmailId
   *
   * @return String
   */
  public String getEmailId() {
    return emailId;
  }


  /**
   * Set the value of EmailId
   *
   * @param v new value
   */
  public void setEmailId(String v) {
    if (!v.equals(this.emailId)) {
      this.emailId = v;
      setModified(true);
    }
  }


  /**
   * Get the Affiliate
   *
   * @return String
   */
  public String getAffiliate() {
    return affiliate;
  }


  /**
   * Set the value of Affiliate
   *
   * @param v new value
   */
  public void setAffiliate(String v) {
    if (!v.equals(this.affiliate)) {
      this.affiliate = v;
      setModified(true);
    }

  }


  /**
   * Get the BonusCode
   *
   * @return String
   */
  public String getBonusCode() {
    return bonusCode;
  }


  /**
   * Set the value of Bonus Code
   *
   * @param v new value
   */
  public void setBonusCode(String v) {
    if (v != null && (bonusCode == null || !v.equals(this.bonusCode))) {
      this.bonusCode = v;
      setModified(true);
    }
  }


  /**
   * Get the Gender
   *
   * @return int
   */
  public int getGender() {
    return gender;
  }


  /**
   * Set the value of Gender
   *
   * @param v new value
   */
  public void setGender(int v) {
    if (this.gender != v) {
      this.gender = v;
      setModified(true);
    }
  }


  /**
   * Get the Dob
   *
   * @return Date
   */
  public java.util.Date getDob() {
    return dob;
  }


  /**
   * Set the value of Dob
   *
   * @param v new value
   */
  public void setDob(java.util.Date v) {
    if (dob == null || !dob.toString().equals(v.toString())) {
      this.dob = v;
      setModified(true);
    }

  }


  public int getRoles() {
    return role_mask;
  }

  public void setRoles(int v) {
    if (this.role_mask != v) {
      this.role_mask = v;
      setModified(true);
    }

  }


  public int getPreferences() {
    return preferences;
  }

  public void setPreferences(int v) {
    if (this.preferences != v) {
      this.preferences = v;
      setModified(true);
    }
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int v) {
    Utils.getRounded(v);
    if (this.points != v) {
      this.points = v;
      setModified(true);
    }
  }

  public java.util.Date getPointsTs() {
    return pointsTs;
  }

  public void setPointsTs(long v) {
    this.pointsTs = new java.sql.Timestamp(v);
  }


  public void setPlayChips(double v) {
    v = Utils.getRounded(v);
    if (this.playChips != v) {
      this.playChips = v;
      setModified(true);
    }
  }

  /**
   * Get the RegTs
   *
   * @return Date
   */
  public java.util.Date getRegTs() {
    return regTs;
  }


  /**
   * Set the value of RegTs
   *
   * @param v new value
   */
  public void setRegTs(long v) {
    this.regTs = new java.sql.Timestamp(v);
  }


  /**
   * Get the Rank
   *
   * @return int
   */
  public int getRank() {
    return rank;
  }


  /**
   * Set the value of Rank
   *
   * @param v new value
   */
  public void setRank(int v) {
    if (this.rank != v) {
      this.rank = v;
      setModified(true);
    }

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

  public String getValidateId() {
    return validated;
  }

  public void setValidateId(String vid) {
    validated = vid;
    setModified(true);
  }

  public String stringValue() {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(this.playerStat()).append("'");
    sbuf.append(getAffiliate()).append("'");
    sbuf.append(getEmailId()).append("'");
    sbuf.append(getGender()).append("'");
    sbuf.append(getPreferences()).append("'");
    sbuf.append(getRank());
    return sbuf.toString();
  }


  public boolean equals(Object o){
    if (o instanceof DBPlayer){
      DBPlayer co = (DBPlayer)o;
      return co.getDispName().equals(getDispName());
    }
    return false;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("Player:\n");
    str.append("DispName = ").append(getDispName()).append("\n");
    str.append("Password = ").append(getPassword()).append("\n");
    str.append("EmailId = ").append(getEmailId()).append("\n");
    str.append("Affiliate = ").append(getAffiliate()).append("\n");
    str.append("Gender = ").append(getGender()).append("\n");
    str.append("Dob = ").append(getDob()).append("\n");
    str.append("Preferences = ").append(getPreferences()).append("\n");

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

    str.append("RegTs = ").append(getRegTs()).append("\n");

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

  public static DBPlayer[] getRankedPlayers(int rank) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;
    Vector v = new Vector();
    try {
      StringBuffer sb = new StringBuffer("select *");
      sb.append(" from T_USER where ");
      sb.append(RANK).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, rank);
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      while (r.next()) {
        DBPlayer p = new DBPlayer();
        p.userId = r.getString(USERID);
        p.emailId = r.getString(EMAIL_ID);
        p.fname = r.getString(FIRST_NAME);
        p.lname = r.getString(LAST_NAME);
        p.affiliate = r.getString(AFFILIATE);
        p.gender = r.getInt(GENDER);
        p.dob = r.getDate(DOB);
        p.affiliate = r.getString(AFFILIATE);
        p.role_mask = r.getInt(ROLE_MASK);
        p.rank = rank;
        p.regTs = r.getTimestamp(REG_TS);
        p.bonusCode = r.getString(BONUS_CODE);
        p.preferences = r.getInt(PREFERENCES);
        p.points = r.getInt(POINTS);
        p.pointsTs = r.getTimestamp(POINTS_TS);

        p.kagression = r.getInt(KAGRESSION);
        p.kconfidence = r.getInt(KCONFIDENCE);
        p.kstrength = r.getInt(KSTRENGTH);
        p.kagility = r.getInt(KAGILITY);
        p.kconsistency = r.getInt(KCONSISTENCY);

        p.bagression = r.getInt(BAGRESSION);
        p.bconfidence = r.getInt(BCONFIDENCE);
        p.bstrength = r.getInt(BSTRENGTH);
        p.bagility = r.getInt(BAGILITY);
        p.bconsistency = r.getInt(BCONSISTENCY);

        p.fagression = r.getInt(FAGRESSION);
        p.fconfidence = r.getInt(FCONFIDENCE);
        p.fstrength = r.getInt(FSTRENGTH);
        p.fagility = r.getInt(FAGILITY);
        p.fconsistency = r.getInt(FCONSISTENCY);

        p.fours = r.getInt(FOURS);
        p.sixes = r.getInt(SIXES);
        p.runs = r.getInt(RUNS);
        p.balls = r.getInt(BALLS);
        p.bowls = r.getInt(BOWLS);
        p.scored = r.getInt(SCORED);
        p.field = r.getInt(FIELD);
        p.catches = r.getInt(CATCHES);
        p.wickets = r.getInt(WICKETS);

        p.validated = r.getString(VALIDATEID);
        p.locale = r.getString(LOCALE);

        v.add(p);
      }
      r.close();
      ps.close();
      conn.close();
    }
    catch (SQLException e) {
      _cat.warning("Unable to get player " + e.getMessage()+  e);

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
    return (DBPlayer[]) v.toArray(new DBPlayer[v.size()]);
  }


  public static void main(String[] args) throws Exception {

    //registration
    /**DBPlayer newp = new DBPlayer("coolcat", "coolcat");
         newp.setDob(new java.util.Date(1971, 10, 8));
         newp.setEmailId("coolcat@test.com");
         newp.setGender(0);
         newp.setRegTs(System.currentTimeMillis());
         System.out.println(newp);
         newp.save();**/DBPlayer bp = new DBPlayer();
    if (bp.get("coolcat", "coolcat")) {
      System.out.println(bp);
    }
    else {
      System.out.println("Player does not exists");
    }
  }

}
