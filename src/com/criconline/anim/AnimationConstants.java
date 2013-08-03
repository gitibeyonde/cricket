package com.criconline.anim;

public interface AnimationConstants {


  public final static int ONCE = 1;
  public final static int CYCLIC = 2;
  public final static int FORWARD = 3;
  public final static int FORWARD_BACKWARD = 4;
  public final static int BACKWARD = 5;

  public final static int EAST = 1;
  public final static int WEST = 2;
  public final static int SOUTH = 3;
  public final static int NORTH = 4;


  public static final int BATS = 0;
  public static final int BOWL = 1;
  public static final int BALL = 2;
  public static final int FIELD = 3;
  public static final int TOSS = 4;
  public static final int HEAD = 5;
  public static final int TAIL = 6;
  public static final int FIELDING = 7;
  public static final int BATTING = 8;
  public static final int BATS_RUN = 9;


  public static final int LF1 = 11;
  public static final int RF1 = 12;
  public static final int LF2 = 13;
  public static final int RF2 = 14;
  public static final int LF3 = 15;
  public static final int RF3 = 16;
  public static final int LF4 = 17;
  public static final int RF4 = 18;
  public static final int LF5 = 19;
  public static final int RF5 = 20;


  public static final int WICKET_KEEPER = 21;
  public static final int RUNNER = 22;

  public static final int MISC = 25;
  public static final int SPEAKS = 26;


  public static final int BOTTOMPANEL = 30;
  public static final int METER = 40;

  public static final int BLOCK = 50;
  public static final int CATCH = 51;
  public static final int LEFT_BLOCK = 52;
  public static final int RIGHT_BLOCK = 53;
  public static final int MISFIELD = 54;

  public static final int DRIVE = 60;
  public static final int LEG = 61;
  public static final int HOOK = 62;
  public static final int CUT = 63;
  public static final int MIDOFF = 64;
  public static final int HIT = 65;
  public static final int EARLY_HIT = 66;
  public static final int LATE_HIT = 67;
  public static final int WIDE = 68;
  public static final int HITWC = 69;


  public static final int NONE = 70;

  public static final int UNKNOWN = 99;


  public static final int NAME_WIDTH = 120;
  public static final int NAME_HEIGHT = 30;

  public static final int DELAY = 20;
  public static final double SLOW = 1.2;
  public static final double NORMAL = 1;
  public static final double FAST = 0.8;
  public static final double VFAST = 0.6;

  public static final int SCREEN_WIDTH=1000;
  public static final int SCREEN_HEIGHT=700;

  public static final int BOTTOM_PANEL_HEIGHT=110;
  public static final int CHAT_PANEL_HEIGHT=20;
  public static final int CHAT_PANEL_WIDTH=250;

  public static final int MESSAGE_HEIGHT=50;

  public static final int PITCH_WIDTH=1600;
  public static final int PITCH_HEIGHT=1600;

  public static final int RANGE_X=600;
  public static final int RANGE_Y=900;

  public static final int DEFAULT_POS_X=300;
  public static final int DEFAULT_POS_Y=500;


  //PITCH PARAMS
  static final int MID_WICKET = 500;
  static final int WICKET = 150;
  static final int BATS_CREASE = 160; // popping crease
  static final int BOWLS_CREASE = 340;
  static final int BOWLS_STUMPS = 370;


//HANDED
  static final int LEFT = 0;
  static final int RIGHT = 1;

// KEY Event type
  static int KEY_PRESSED = 1;
  static int KEY_RELEASED = 2;
  static int KEY_TYPED = 4;


  // Batsman states during a hit
  public static final int NEUTRAL=0;
  public static final int TAKE_POSITION=1;
  public static final int SELECT_SHOT=2;
  // Bowling states during a bowling action
  public static final int SELECT_PITCH_POINT=1;
  public static final int PITCH_POINT_Y_SPREAD=50;
  public static final int PITCH_POINT_X_SPREAD=50;
  public static final int SELECT_SPIN=2;

  //PITCH POINT BOUNDS
  static final int PP_X = 500;
  static final int PP_Y = 230;
  static final int PP_TOP = PP_Y-PITCH_POINT_Y_SPREAD;
  static final int PP_BOTTOM = PP_Y+PITCH_POINT_Y_SPREAD;
  static final int PP_LEFT = PP_X-PITCH_POINT_X_SPREAD;
  static final int PP_RIGHT = PP_X+PITCH_POINT_X_SPREAD;
  static final int PP_OVER_PITCH = PP_Y-20;
  static final int PP_SHORT_PITCH = PP_Y+20;

  //PLAYER FIELDER VIEW
  static int DT = 1;
  static int UP = 2;
  static int DN = 4;
  static int RT = 8;
  static int LT = 16;
  static int FD = 32;
}
