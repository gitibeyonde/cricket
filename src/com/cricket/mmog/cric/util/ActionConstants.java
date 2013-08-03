package com.cricket.mmog.cric.util;

public interface ActionConstants {

  // stages
  public static final int PREGAME = 0;
  public static final int START_GAME = 1; //ACTION_TYPE_STAGE
  public static final int PLAYER_SHIFT = 2; //ACTION_TYPE_STAGE
  public static final int PLAYER_STATS = 3; //ACTION_TYPE_STAGE
  public static final int END_GAME = 9; //ACTION_TYPE_STAGE
  public static final int UPDATE_GAME = 10; //ACTION_TYPE_CARD
  public static final int TMP_CONSTANT = 11;

  // game actions
  public static final int START = 100; //ACTION_TYPE_BETTING
  public static final int BOWL = 101; //ACTION_TYPE_BETTING
  public static final int FIELD = 102; //ACTION_TYPE_BETTING
  public static final int BAT = 103; //ACTION_TYPE_BETTING
  public static final int TOSS = 104; //ACTION_TYPE_BETTING
  public static final int HEAD = 105; //ACTION_TYPE_BETTING
  public static final int TAIL = 106; //ACTION_TYPE_BETTING
  public static final int FIELDING = 107; //ACTION_TYPE_BETTING
  public static final int BATTING = 108; //ACTION_TYPE_BETTING

  public static final int BATS_CHANGE = 109; //pitch change
  public static final int INNING_OVER = 110; //pitch change
  public static final int GAME_OVER = 111; //pitch change

  public static final int LEAVE = 124; //ACTION_TYPE_BETTING
  public static final int REMOVED = 125; //ACTION_TYPE_BETTING

  public static final int WIN = 126; //ACTION_TYPE_STAGE
  public static final int TOURNAMENT_WIN = 127; //ACTION_TYPE_STAGE

  public static final int WAIT = 136; //ACTION_TYPE_BETTING
  public static final int END = 137; //ACTION_TYPE_BETTING



  // auxiliary actions
  public static final int MOVE_REQUEST = 211;
  public static final int YOUR_TURN = 211;
  public static final int TABLE_INFO = 211;
  public static final int WAITER_CAN_JOIN = 213; //ACTION_TYPE_TABLE_SERVER
  public static final int CHAT = 214; //ACTION_TYPE_STAGE
  public static final int LOBBY_CHAT = 215; //ACTION_TYPE_STAGE

  public static final int NEW_GAME = 220; //ACTION_TYPE_TABLE_SERVER
  public static final int CASHIER = 221; //ACTION_TYPE_STAGE
  public static final int PAUSE = 222;

  // Agneya NEW
  public static final int PLAYER_MESSAGE = 222; //ACTION_TYPE_STAGE

  // END NEW
  // server actions
  public static final int PLAYER_REGISTERED = 300;
  public static final int PLAYER_UNREGISTERED = 301;
  public static final int PLAYER_JOIN = 302;
  public static final int PLAYER_LEAVE = 303; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_SITIN = 304; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_SITOUT = 305; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_REJOIN = 306;
  public static final int PLAYER_NEEDS_SITOUT = 307; //ACTION_TYPE_TABLE_SERVER
  public static final int SESSION_TIMEOUT = 308;
  public static final int IMMEDIATE_SHUTDOWN = 309; //ACTION_TYPE_TABLE_SERVER
  public static final int GRACEFUL_SHUTDOWN = 310; //ACTION_TYPE_TABLE_SERVER
  public static final int ADD_TO_WAITERS = 311;
  public static final int REMOVE_FROM_WAITERS = 312;
  public static final int STARTUP = 314;
  public static final int CHANGE_STATE = 315;
  public static final int MANUAL_IMMEDIATE_SHUTDOWN = 316; //ACTION_TYPE_TABLE_SERVER
  public static final int MANUAL_GRACEFUL_SHUTDOWN = 317; //ACTION_TYPE_TABLE_SERVER
  public static final int MANUAL_STARTUP = 318; //ACTION_TYPE_TABLE_SERVER
  public static final int MANUAL_CHANGE_STATE = 319; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_KICKED_OUT = 320; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_NEEDS_SITIN_TRUE = 321; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_NEEDS_SITIN_FALSE = 322; //ACTION_TYPE_TABLE_SERVER
  public static final int IS_ACCEPTING = 323; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_POST_JOIN = 324;
  public static final int PLAYER_REMOVE = 325;
  public static final int PLAYER_POST_REMOVE = 326;
  public static final int SIDEBAR_INFO = 327; //ACTION_TYPE_TABLE_SERVER
  public static final int PLAYER_NONE = 329; //ACTION_TYPE_SIMPLE
  public static final int PLAYER_SEAT_AVAILABLE = 330; //ACTION_TYPE_SIMPLE

  // error actions
  public static final int PLACE_OCCUPIED = 400; //ACTION_TYPE_ERROR
  public static final int UNSUFFICIENT_FUND = 401; //ACTION_TYPE_ERROR
  public static final int DECISION_TIMEOUT = 402;
  public static final int NO_MORE_WAITING = 403;

  /**
   * Player tried to register on table where another player from the same
   * remote host already has registered.
   */
  public static final int SAME_REMOTE_HOST = 404; //ACTION_TYPE_ERROR

  /**
   * Player tried to join table and there is a wait claim.
   */
  public static final int THERE_IS_CLAIM = 405; //ACTION_TYPE_ERROR

  /** Player have already staied in waiting list */
  public static final int ALREADY_WAIT = 406;

  /** Table not in ONLINE mode */
  public static final int TABLE_IS_OFFLINE = 407; //ACTION_TYPE_ERROR

  /** Cannot call to cashier for this game type (tournament) */
  public static final int CASHIER_UNAVAIBLE = 408; //ACTION_TYPE_ERROR

  /** Something strange happened  */
  public static final int UNKNOWN_ERROR = 409; //ACTION_TYPE_ERROR

  /** There is no such player session  */
  public static final int UNKNOWN_SESSION = 410;
  public static final int DELAY = 420;
  public static final int UPDATE = 421;

  // action types
  public static final int ACTION_TYPE_SIMPLE = 1000; //	ok
  public static final int ACTION_TYPE_STAGE = 1001; //	ok
  public static final int ACTION_TYPE_CARD = 1002; //	ok
  public static final int ACTION_TYPE_MOVEREQUEST = 1003; //	ok
  public static final int ACTION_TYPE_TABLE_SERVER = 1004; //	ok
  public static final int ACTION_TYPE_ERROR = 1005; //	ok
  public static final int ACTION_TYPE_PREBETTING = 1006; //	--
  public static final int ACTION_TYPE_LASTMOVE = 1007; //
  public static final int ACTION_TYPE_MOVE = 1008; //	ok

  public static final int BOARD_TARGET = -1;
  public static final int DEALER_TARGET = 0;

}
