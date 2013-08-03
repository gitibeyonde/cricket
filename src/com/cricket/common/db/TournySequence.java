package com.cricket.common.db;

import com.agneya.util.DBUtils;

import com.cricket.common.db.DBException;

import java.util.logging.Logger;

import java.sql.*;

public class TournySequence {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(TournySequence.class.getName());
  final static String SEQ_NAME="tourny_id_seq";

  private TournySequence() {
  }


 public static int getNextGameId()throws DBException {
   return DBUtils.getNextSeq(SEQ_NAME);
 }

}
