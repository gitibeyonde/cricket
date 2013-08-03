package com.cricket.mmog.cric.util;

public interface CricketConstants {

  public static final int BATTING_TEAM = 1;
  public static final int FIELDING_TEAM = 2;


  // when game is on
  public static final int CREATED = 0;
  public static final int BATTING = 1;
  public static final int FIELDING = 2;
  public static final int WAITING = 3;

  // game types
  public static final int CRIC1 = 1;
  public static final int CRIC2 = 2;
  public static final int CRIC3 = 4;
  public static final int CRIC4 = 8;

  public static final int CRIC5 = 16;
  public static final int CRIC6 = 32;
  public static final int CRIC8 = 64;
  public static final int CRIC10 = 128;

  //
  public static final int ONE_DAY = 256;
  public static final int TEST_MATCH = 512;
  public static final int TOURNY = 1024;
  public static final int FINALS = 2048;

  // game limits
  public static final int REGULAR = 0; // high-low
  public static final int NO_LIMIT = 1;
  public static final int POT_LIMIT = 2;
  public static final int TOURNAMENT = 3;

  // player types
  public static final int PLAYER_REGULAR = 0;
  public static final int PLAYER_BUTTON = 1;
  public static final int PLAYER_SMALL_BLIND = 2;
  public static final int PLAYER_BIG_BLIND = 3;
  public static final int PLAYER_EARLY = 4;
  public static final int PLAYER_MIDDLE = 5;
  public static final int PLAYER_LATE = 6;

}
