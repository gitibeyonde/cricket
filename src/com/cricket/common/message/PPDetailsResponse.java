package com.cricket.common.message;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PPDetailsResponse {
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

  public PPDetailsResponse(String id) {
    ppId=id;
  }
}
