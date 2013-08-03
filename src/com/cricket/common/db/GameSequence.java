package com.cricket.common.db;

import java.util.logging.Logger;

import java.sql.*;
import com.agneya.util.ConnectionManager;
import com.agneya.util.DBUtils;

import com.cricket.common.db.DBException;

// SQLSERVER/ORACLE

public class GameSequence {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(GameSequence.class.getName());
  final static String SEQ_NAME = "game_id_seq";

  private GameSequence() {
  }

  public static int getNextGameId() throws DBException {
    return DBUtils.getNextSeq(SEQ_NAME);
  }
}
