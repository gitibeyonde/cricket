package com.cricket.common.db;

import java.util.*;
import java.sql.*;
import java.util.logging.Logger;

import com.agneya.util.*;

import com.cricket.common.db.DBException;

// SQLSERVER/ORACLE

public class DBGame {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(DBGame.class.getName());
  private int gameId;
  private int gameType;
  private String gameName;
  private String ip;
  private int high_rank = 0;
  private int low_rank = 0;
  private String affiliates;
  private String partner1_affiliates;
  private String partner2_affiliates;
  private int minPlayers, maxPlayers;
  private double minRaise, maxBet;
  private int maxRounds, maxLoops;
  private double smallBlind, bigBlind;
  private short gameState;
  private short private_table;
  private java.util.Date lastModified;
  private String[] players;

  public static final String GAME_ID;
  public static final String GAME_TYPE;
  public static final String GAME_NAME;
  public static final String IP;
  public static final String LOW_RANK;
  public static final String HIGH_RANK;
  public static final String AFFILIATE_ID;
  public static final String PARTNER1_AFFILIATE_ID;
  public static final String PARTNER2_AFFILIATE_ID;
  public static final String MIN_PLAYERS;
  public static final String MAX_PLAYERS;
  public static final String MIN_RAISE;
  public static final String MAX_BET;
  public static final String MAX_ROUNDS;
  public static final String MAX_LOOPS;
  public static final String SMALL_BLIND;
  public static final String BIG_BLIND;
  public static final String GAME_STATE;
  public static final String PRIVATE_GAME;
  public static final String LAST_MODIFIED;

  public static final String GAME_ID_FK;
  public static final String USER_ID_FK;

  static {
    GAME_ID = "GAME_ID_SEQ_PK";
    GAME_TYPE = "GAME_TYPE_ID_FK";
    GAME_NAME = "GAME_NAME";
    IP = "GAMESERVER_IP";
    LOW_RANK = "LOW_RANK";
    HIGH_RANK = "HIGH_RANK";
    AFFILIATE_ID = "AFFILIATE_ID_FK";
    PARTNER1_AFFILIATE_ID = "PARTNER1_AFFILIATE_ID_FK";
    PARTNER2_AFFILIATE_ID = "PARTNER2_AFFILIATE_ID_FK";
    MIN_PLAYERS = "MIN_PLAYERS";
    MAX_PLAYERS = "MAX_PLAYERS";
    MIN_RAISE = "MIN_RAISE";
    MAX_BET = "MAX_BET";
    MAX_ROUNDS = "MAX_ROUNDS";
    MAX_LOOPS = "MAX_LOOPS";
    SMALL_BLIND = "SMALL_BLIND";
    BIG_BLIND = "BIG_BLIND";
    GAME_STATE = "GAME_STATE";
    PRIVATE_GAME = "PRIVATE_GAME";
    LAST_MODIFIED = "LAST_MODIFIED_TIMESTAMP";
    //invited players
    GAME_ID_FK = "GAME_ID_SEQ_FK";
    USER_ID_FK = "USER_ID_FK";
    // affiliates
  }

  public static final short RUNNING = 1;
  public static final short STOPPED = 1;

  public DBGame(int id, int type, String name, String ip, String[] aff,
                String[] plyrs, int rank, int minp,
                int maxp,
                double minr, double minb, double smlb, double bigb, int maxrnd,
                int maxlps) {
    gameId = id;
    gameType = type;
    gameName = name;
    this.ip = ip;
    if (aff == null) {
      affiliates = "admin";
    }
    else {
      affiliates = aff.length > 0 ? aff[0] : "admin";
      partner1_affiliates = aff.length > 1 ? aff[1] : null;
      partner2_affiliates = aff.length > 2 ? aff[2] : null;
    }
    high_rank = rank;
    minPlayers = minp;
    maxPlayers = maxp;
    minRaise = minr;
    maxBet = minb;
    smallBlind = smlb;
    bigBlind = bigb;
    maxRounds = maxrnd;
    maxLoops = maxlps;
    gameState = 1;
    if (plyrs!=null){
      private_table = 1;
      players = plyrs;
    }
    lastModified = new java.util.Date();
    _modified = true;
  }

  public DBGame(int gid) throws DBException {
    get(gid);
  }

  public boolean get(int gid) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;
    try {
      StringBuffer sb = new StringBuffer("select *");
      sb.append(" from T_GAME_LIVE where ");
      sb.append(GAME_ID).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, gameId);
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        gameId = r.getInt(GAME_ID);
        gameType = r.getInt(GAME_TYPE);
        gameName = r.getString(GAME_NAME);
        ip = r.getString(IP);
        high_rank = r.getInt(HIGH_RANK);
        low_rank = r.getInt(LOW_RANK);
        affiliates = r.getString(AFFILIATE_ID);
        partner1_affiliates = r.getString(PARTNER1_AFFILIATE_ID);
        partner2_affiliates = r.getString(PARTNER2_AFFILIATE_ID);
        minPlayers = r.getInt(MIN_PLAYERS);
        maxPlayers = r.getInt(MAX_PLAYERS);
        minRaise = r.getDouble(MIN_RAISE);
        maxBet = r.getDouble(MAX_BET);
        maxRounds = r.getInt(MAX_ROUNDS);
        maxLoops = r.getInt(MAX_LOOPS);
        smallBlind = r.getDouble(SMALL_BLIND);
        bigBlind = r.getDouble(BIG_BLIND);
        lastModified = r.getTimestamp(LAST_MODIFIED);
        gameState = r.getShort(GAME_STATE);
        private_table = r.getShort(PRIVATE_GAME);
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
      _cat.warning("Error in getting game "+ e);
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
        StringBuffer sb = new StringBuffer("insert into T_GAME_LIVE ( ");
        sb.append(GAME_ID).append(",");
        sb.append(GAME_TYPE).append(",");
        sb.append(GAME_NAME).append(",");
        sb.append(IP).append(",");
        sb.append(HIGH_RANK).append(",");
        sb.append(LOW_RANK).append(",");
        sb.append(AFFILIATE_ID).append(",");
        sb.append(PARTNER1_AFFILIATE_ID).append(",");
        sb.append(PARTNER2_AFFILIATE_ID).append(",");
        sb.append(MIN_PLAYERS).append(",");
        sb.append(MAX_PLAYERS).append(",");
        sb.append(MIN_RAISE).append(",");
        sb.append(MAX_BET).append(",");
        sb.append(MAX_ROUNDS).append(",");
        sb.append(MAX_LOOPS).append(",");
        sb.append(SMALL_BLIND).append(",");
        sb.append(BIG_BLIND).append(",");
        sb.append(GAME_STATE).append(",");
        sb.append(PRIVATE_GAME).append(",");
        sb.append(LAST_MODIFIED).append(")");
        if (ConnectionManager.isOracle()) {
          sb.append(
              " values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
        }
        else {
          sb.append(
              " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
              }
        _cat.finest(this.toString());
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        int i = 1;
        ps.setInt(i++, gameId);
        ps.setInt(i++, gameType);
        ps.setString(i++, gameName);
        ps.setString(i++, ip);
        ps.setInt(i++, high_rank);
        ps.setInt(i++, low_rank);
        ps.setString(i++, affiliates);
        ps.setString(i++, partner1_affiliates);
        ps.setString(i++, partner2_affiliates);
        ps.setInt(i++, minPlayers);
        ps.setInt(i++, maxPlayers);
        ps.setDouble(i++, minRaise);
        ps.setDouble(i++, maxBet);
        ps.setInt(i++, maxRounds);
        ps.setInt(i++, maxLoops);
        ps.setDouble(i++, smallBlind);
        ps.setDouble(i++, bigBlind);
        ps.setInt(i++, gameState);
        ps.setInt(i++, private_table);
        ps.setTimestamp(i++,
                        new java.sql.Timestamp(lastModified == null ? 0 :
                                               lastModified.getTime()));

        r = ps.executeUpdate();
        // if it is private table update the player list too
        if (private_table == 1 && players != null) {
          //update t_private_game
          saveUser(conn, players);
        }
        ps.close();
        conn.close();
      }
      catch (SQLException e) {
        _cat.warning("Unable to save  GRS" + e.getMessage()+ e);
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

  public void saveUser(Connection conn, String[] players) throws DBException {
    Statement ps = null;

    try {
      ps = conn.createStatement();

      for (int i = 0; i < players.length && players[i].trim().length() > 2; i++) {
        StringBuffer sb = new StringBuffer("insert into T_PRIVATE_PLAYERS ( ");
        sb.append(GAME_ID_FK).append(",");
        sb.append(USER_ID_FK).append(")");
        sb.append(
            " values ('" + gameId + "', '" + players[i] + "' )");
        _cat.finest(sb.toString());
        ps.addBatch(sb.toString());
      }

      ps.executeBatch();
      _cat.finest(this.toString());
      ps.close();
      conn.close();
    }
    catch (SQLException e) {
      _cat.warning("Unable to save player list" + e);
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

  public int updateState() throws DBException {
    int r = -1;
    Connection conn = null;
    PreparedStatement ps = null;

    if (_modified) {
      try {
        StringBuffer sb = new StringBuffer(
            "update T_LOGIN_SESSION_LIVE set ");
        sb.append(GAME_STATE).append(" = ? ");
        sb.append(" where ");
        sb.append(GAME_ID).append("= ? ");
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        ps.setInt(1, gameState);
        ps.setInt(2, gameId);
        r = ps.executeUpdate();
        ps.close();
        conn.close();
        _cat.finest(this.toString());
      }
      catch (SQLException e) {
        _cat.warning("Unable to update Player's Session " +  e);
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
    str.append("DBGame:\n");
    str.append("GameID = ").append(gameId).append("\n");
    str.append("GameName = ").append(gameName).append("\n");
    str.append("GameType = ").append(gameType).append("\n");
    str.append("Affiliate = ").append(affiliates).append("\n");
    str.append("Partner1 = ").append(partner1_affiliates).append("\n");
    str.append("Partner2 = ").append(partner2_affiliates).append("\n");
    str.append("GameType = ").append(gameType).append("\n");
    return (str.toString());
  }

  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;

}
