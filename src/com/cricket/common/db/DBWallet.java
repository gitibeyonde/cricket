package com.cricket.common.db;

import java.util.Date;
import java.sql.*;

import com.agneya.util.*;

import com.cricket.common.db.DBException;

import java.util.logging.Logger;

// SQLSERVER/ORACLE

public class DBWallet {
  // set the category for logging
  static Logger _cat = Logger.getLogger(DBWallet.class.getName());

  public static final String TRANSACTION_ID;
  public static final String DISPLAY_NAME;
  public static final String AMOUNT;
  public static final String OPERATION;
  public static final String TIME_STAMP;
  public static final String SESSION;
  public static final String AFFILIATE;
  public static final String ACCOUNT_ID;
  public static final String CCSM_STATUS;

  public static final String WALLET_TRANSACTION_TABLE =
      "T_WALLET_TRANSACTION";

  static {
  	TRANSACTION_ID = "TRANSACTION_ID";
    DISPLAY_NAME = "USER_ID_FK";
    AMOUNT = "TRANSACTION_AMOUNT";
    OPERATION = "WALLET_OPR_TYPE_ID";
    TIME_STAMP = "TRANSACTION_TIMESTAMP";
    SESSION = "SESSION_ID_FK";
    AFFILIATE = "AFFILIATE_ID_FK";
    ACCOUNT_ID = "ACCOUNT_ID";
    CCSM_STATUS = "CCSM_STATUS";
  }

  public static int PM_BANKROLL = 1;
  public static int PM_REDEEMPTION = 2;
  public static int PM_REDEEMPTION_FLOWBACK = 3;
  public static int PM_GETCHIPS = 4;
  public static int PM_USE_BONUS = 5;


  public static int PM_ADD_BONUS = 6;
  public static int RM_BANKROLL = 7;
  public static int RM_REDEEMPTION = 8;
  public static int RM_REDEEMPTION_FLOWBACK = 9;
  public static int RM_GETCHIPS = 10;


  public static int RM_USE_BONUS = 11;
  public static int RM_ADD_BONUS = 12;
  public static int BUY_TOURNY_CHIPS = 14;
  public static int RM_CARD = 15;
  public static int BUY_SITNGO_CHIPS = 16;


  public static int BUY_PLAY_CHIPS = 17;
  public static int BUY_REAL_CHIPS = 18;
  public static int SITNGO_WIN = 19;

  public static int RM_TO_PROVIDER = 20;
  public static int RM_FROM_PROVIDER = 21;
  public static int TOURNY_WIN = 22;


  public static int AFF_COMMISSION = 23;
  public static int PAYNOVA = 24;
  public static int NETTELLER = 25;
  public static int FIREPAY = 26;
  public static int PREPAIDATM = 27;
  public static int CITADEL = 28;


  public static int WIRE = 101;
  public static int CASH = 102;
  public static int CHEQUE = 103;


  private int transactionId=0;
  private String displayName;
  private double amount;
  private int operation;
  private String session;
  private java.sql.Timestamp timeStamp;
  private String affiliate;
  private String merchantid;
  private String status="COMPLETED";  //COMPLETED, FAILED, APPROVED, PENDING

  public DBWallet() {
  }

  public synchronized int save(Connection conn, int trans_id) throws SQLException {
  	if (trans_id==0){
  		throw new IllegalStateException("Set the transactionId");
  	}
  	transactionId=trans_id;
    if (_modified) {
      StringBuffer sb = new StringBuffer("insert into ");
      sb.append(WALLET_TRANSACTION_TABLE).append("( ");
      sb.append(TRANSACTION_ID).append(",");
      sb.append(DISPLAY_NAME).append(",");
      sb.append(AMOUNT).append(",");
      sb.append(OPERATION).append(",");
      sb.append(SESSION).append(",");
      sb.append(AFFILIATE).append(",");
      sb.append(ACCOUNT_ID).append(",");
      sb.append(CCSM_STATUS).append(",");
      sb.append(TIME_STAMP).append(")");
      sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?, ? )");
      PreparedStatement ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, transactionId);
      ps.setString(2, displayName);
      ps.setDouble(3, amount);
      ps.setInt(4, operation);
      ps.setString(5, session);
      ps.setString(6, affiliate);
      ps.setString(7, merchantid);
      ps.setString(8, status);
      ps.setTimestamp(9,
                      timeStamp= new java.sql.Timestamp(System.currentTimeMillis()));
      _cat.finest(this.toString());
      int r = ps.executeUpdate();
      ps.close();
      return r;
    }
    else {
      return -1;
    }
  }



  public synchronized int save(Connection conn) throws DBException, SQLException {
    if (_modified) {
      transactionId=DBUtils.getNextMicrosoft(conn, "transaction_id_seq");
      StringBuffer sb = new StringBuffer("insert into ");
      sb.append(WALLET_TRANSACTION_TABLE).append("( ");
      sb.append(TRANSACTION_ID).append(",");
      sb.append(DISPLAY_NAME).append(",");
      sb.append(AMOUNT).append(",");
      sb.append(OPERATION).append(",");
      sb.append(SESSION).append(",");
      sb.append(AFFILIATE).append(",");
      sb.append(ACCOUNT_ID).append(",");
      sb.append(CCSM_STATUS).append(",");
      sb.append(TIME_STAMP).append(")");
      sb.append(" values ( ?, ?, ?, ?, ?, ?, ?, ?, ? )");
      PreparedStatement ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, transactionId);
      ps.setString(2, displayName);
      ps.setDouble(3, amount);
      ps.setInt(4, operation);
      ps.setString(5, session);
      ps.setString(6, affiliate);
      ps.setString(7, merchantid);
      ps.setString(8, status);
      ps.setTimestamp(9,
                      timeStamp= new java.sql.Timestamp(System.currentTimeMillis()));
      _cat.finest(this.toString());
      int r = ps.executeUpdate();
      ps.close();
      return transactionId;
    }
    else {
      return -1;
    }
  }
  /**
   * Set the value of Operation
   *
   * @param v new value
   */
  public void setTransactionId(int v) {
    if (v!=operation) {
      this.transactionId = v;
      setModified(true);
    }
  }

  public int getTransactionId() {
    return transactionId;
  }

  public String getStatus() {
    return status;
  }


  public void setStatus(String v) {
    if (status == null || !status.equals(v)) {
      this.status = v;
      setModified(true);
    }
  }

  public String getMerchantId() {
    return merchantid;
  }


  public void setMerchantId(String v) {
    if (merchantid == null || !merchantid.equals(v)) {
      this.merchantid = v;
      setModified(true);
    }

  }

  /**
   * Get the DisplayName
   *
   * @return String
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Set the value of DispName
   *
   * @param v new value
   */
  public void setDisplayName(String v) {
    if (displayName == null || !displayName.equals(v)) {
      this.displayName = v;
      setModified(true);
    }

  }

  /**
   * Get the Amount
   *
   * @return float
   */
  public double getAmount() {
    return amount;
  }

  /**
   * Set the value of Amount
   *
   * @param v new value
   */
  public void setAmount(double v) {
    if (this.amount != v) {
      this.amount = v;
      setModified(true);
    }

  }

  /**
   * Get the Operation
   *
   * @return String
   */
  public int getOperation() {
    return operation;
  }

  /**
   * Set the value of Operation
   *
   * @param v new value
   */
  public void setOperation(int v) {
    if (v != operation) {
      this.operation = v;
      setModified(true);
    }
  }

  public String getSession() {
    return session;
  }

  public void setSession(String v) {
    if (!v.equals(this.session)) {
      this.session = v;
      setModified(true);
    }
  }

  public String getAffiliate() {
    return affiliate;
  }

  public void setAffiliate(String v) {
    if (v == null) {
      affiliate = "admin";
    }
    else if (!v.equals(affiliate)) {
      this.affiliate = v;
      setModified(true);
    }
  }

  /**
   * Get the TimeStamp
   *
   * @return Date
   */
  public java.sql.Timestamp getTimeStamp() {
    return timeStamp;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("WalletTransaction: " + transactionId + "\n");
    str.append("DisplayName = ").append(getDisplayName()).append("\n");
    str.append("Amount = ").append(getAmount()).append("\n");
    str.append("Operation = ").append(getOperation()).append("\n");
    str.append("Session = ").append(getSession()).append("\n");
    str.append("Affiliate = ").append(getAffiliate()).append("\n");
    str.append("MerchantId = ").append(getMerchantId()).append("\n");
    str.append("Status = ").append(getStatus()).append("\n");
    str.append("TimeStamp = ").append(getTimeStamp()).append("\n");
    return (str.toString());
  }

  public void setModified(boolean val) {
    _modified = val;
  }

  public boolean _modified = false;

}
