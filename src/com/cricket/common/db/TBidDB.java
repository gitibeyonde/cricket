package com.cricket.common.db;

import java.util.*;
import java.sql.*;
import java.util.logging.Logger;

import com.agneya.util.*;

import com.cricket.common.db.DBException;

// SQLSERVER/ORACLE

public class TBidDB {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(TBidDB.class.getName());

  public String bidId;
  public String tmIdfk;
  public String ppId;
  public int price;
  public int status;
  public java.util.Date bid_date;

  /**
   *
   */
  public static final String BIDID;
  public static final String PROFID_FK;
  public static final String TMID_FK;
  public static final String PRICE;
  public static final String STATUS;
  public static final String TS;


  static {
    BIDID = "BIDID";
    PROFID_FK = "PROFID_FK";
    TMID_FK = "TMID_FK";
    PRICE = "PRICE";
    STATUS = "STATUS";
    TS = "TS";
  }


  public TBidDB() throws DBException {
  }

  public boolean getBidForPP(String ppId) throws DBException {
    Connection conn = null;
    PreparedStatement ps = null;
    try {
      StringBuffer sb = new StringBuffer("select *");
      sb.append(" from T_BIDS where ");
      sb.append(BIDID).append("=?");
      conn = ConnectionManager.getConnection("GameEngine");
      ps = conn.prepareStatement(sb.toString());
      ps.setString(1, ppId);
      _cat.finest(sb.toString());
      ResultSet r = ps.executeQuery();
      if (r.next()) {
        bidId = r.getString(BIDID);
        tmIdfk= r.getString(TMID_FK);
        ppId= r.getString(PROFID_FK);
        price= r.getInt(PRICE);
        status= r.getInt(STATUS);
        bid_date = r.getTimestamp(TS);
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
        StringBuffer sb = new StringBuffer("insert into T_BIDS ( ");
        sb.append(BIDID).append(",");
        sb.append(PROFID_FK).append(",");
        sb.append(TMID_FK).append(",");
        sb.append(PRICE).append(",");
        sb.append(STATUS).append(",");
        sb.append(TS).append(")");
        if (ConnectionManager.isOracle()) {
          sb.append(
              " values ( ?, ?, ?, ?, ?, ?)");
        }
        else {
          sb.append(
              " values (?, ?, ?, ?, ?, ? )");
        }
        _cat.finest(sb.toString());
        conn = ConnectionManager.getConnection("GameEngine");
        ps = conn.prepareStatement(sb.toString());
        int i = 1;
        ps.setString(i++, bidId);
        ps.setString(i++, tmIdfk);
        ps.setString(i++, ppId);
        ps.setInt(i++, price);
        ps.setInt(i++, status);
        ps.setTimestamp(i++,
                        new java.sql.Timestamp(bid_date == null ? 0 :
                                               bid_date.getTime()));

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
    str.append("TMBid:\n");
    str.append("tmIdfk = ").append(tmIdfk).append("\n");
    str.append("ppId = ").append(ppId).append("\n");
    str.append("price = ").append(price).append("\n");
    str.append("status = ").append(status).append("\n");
    return (str.toString());
  }

  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;

}
