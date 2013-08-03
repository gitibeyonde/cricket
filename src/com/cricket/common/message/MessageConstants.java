package com.cricket.common.message;

public interface MessageConstants {

  public static final int C_CONNECT = 1;
  public static final int C_LOGIN = 2;
  public static final int C_HTBT = 3;
  public static final int C_GAMELIST = 4;
  public static final int C_TOURNYSTARTS = 5;
  public static final int C_GAMEDETAIL = 6;
  public static final int C_MOVE = 7;
  public static final int C_PROFESSIONAL_PLAYERS = 8;
  public static final int C_REGISTER = 9;
  public static final int C_PING = 10;
  public static final int C_TURN_DEAF = 11;
  public static final int C_ADMIN = 12;
  public static final int C_CONFIG = 14;
  public static final int C_LOGOUT = 15;
  public static final int C_MESSAGE = 16;

  // tourny
  public static final int C_TOURNYLIST = 17;
  public static final int C_TOURNYDETAIL = 18;
  public static final int C_TOURNYREGISTER = 19;
  public static final int C_BUYCHIPS = 20;
  public static final int C_TOURNYMYTABLE = 21;

  // bingo room
  public static final int C_BINGOROOMLIST = 22;
  public static final int C_BINGOROOMDETAIL = 23;
  public static final int C_TICKET = 24;
  public static final int C_BUY_TICKET = 25;

  public static final int C_SIT_OUT = 26;
  public static final int C_SIT_IN = 27;
  public static final int C_PREFERENCES = 28;
  public static final int C_GET_CHIPS_INTO_GAME = 29;
  public static final int C_WAITER = 30;
  public static final int C_CARD = 32;
  public static final int C_BANNER = 33;
  public static final int C_VOTE = 34;
  public static final int C_TABLE_CLOSED = 35;
  public static final int C_TABLE_OPEN = 36;
  public static final int C_PLAYERSHIFT = 37;
  public static final int C_PLAYERSTATS = 38;
  public static final int C_TEAM_MANAGER = 39;

  public static final int C_KILL_HANDLER = 97;

  // MOVES
   public static final int M_OPEN = 0;
   public static final int M_START = 1;
   public static final int M_TOSS = 2;
   public static final int M_HEAD = 3;
   public static final int M_TAIL = 4;
   public static final int M_FIELDING = 5;
   public static final int M_BATTING = 6;
   public static final int M_JOIN = 8;
   public static final int M_LEAVE = 9;
   public static final int M_SIT_IN = 10;
   public static final int M_OPT_OUT = 11;
   public static final int M_WAIT = 12;
   public static final int M_BAT = 14;
   public static final int M_BOWL = 15;
   public static final int M_FIELD = 16;
   public static final int M_DRINKS = 17;
   public static final int M_PING = 35;


   public static final int M_NONE = 999;
   public static final int M_ILLEGAL = 999;

   public static final int A_ILLEGAL = 999;


  public static final int R_CONNECT = 1;
  public static final int R_LOGIN = 2;
  public static final int R_HTBT = 3;
  public static final int R_GAMELIST = 4;
  public static final int R_TOURNYSTARTS = 5;
  public static final int R_GAMEDETAIL = 6;
  public static final int R_MOVE = 7;
  public static final int R_PROFESSIONAL_PLAYERS = 8;
  public static final int R_REGISTER = 9;
  public static final int R_PING = 10;

  // TURN_DEAF = 11
  public static final int R_ADMIN = 12;
  public static final int R_CONFIG = 14;
  public static final int R_LOGOUT = 15;
  public static final int R_MESSAGE = 16;

  // tourny
  public static final int R_TOURNYLIST = 17;
  public static final int R_TOURNYDETAIL = 18;
  public static final int R_TOURNYREGISTER = 19;
  public static final int R_BUYCHIPS = 20;
  public static final int R_TOURNYMYTABLE = 21;

  // bingo room
  public static final int R_BINGOROOMLIST = 22;
  public static final int R_BINGOROOMDETAIL = 23;
  public static final int R_TICKET = 24;
  public static final int R_BUY_TICKET = 25;

  public static final int R_SIT_OUT = 26;
  public static final int R_SIT_IN = 27;
  public static final int R_PREFERENCES = 28;
  public static final int R_GET_CHIPS_INTO_GAME = 29;
  public static final int R_WAITER = 30;
  public static final int R_CARD = 32;
  public static final int R_BANNER = 33;
  public static final int R_VOTE = 34;
  public static final int R_TABLE_CLOSED = 35;
  public static final int R_TABLE_OPEN = 36;
  public static final int R_PLAYERSHIFT = 37;
  public static final int R_PLAYERSTATS = 38;
  public static final int R_TEAM_MANAGER = 39;

  public static final int R_KILL_HANDLER = 97;
  public static final int R_BASIC = 98;
  public static final int R_UNKNOWN = 99;

  public static final int E_FAILURE = 0;
  public static final int E_SUCCESS = 1;
  public static final int E_AUTHENTICATE = 2;
  public static final int E_SHUTTING = 3;
  public static final int E_USER_EXISTS = 4;
  public static final int E_SUSPEND = 5;
  public static final int E_STARTING = 7;
  public static final int E_ALREADY_REGISTERED = 8;
  public static final int E_REGISTERATION_CLOSED = 9;
  public static final int E_STOPPING_GAME = 10;
  public static final int E_SUSPENDING_GAME = 11;
  public static final int E_ALREADY_LOGGED = 12;
  public static final int E_IP_REUSE = 14;
  public static final int E_BROKE = 15;
  public static final int E_NONEXIST = 16;
  public static final int E_JOINED = 17;
  public static final int E_PARTIALLY_FILLED = 18;
  public static final int E_CARD_FAILED = 19;
  public static final int E_PLAYER_REMOVED = 21;
  public static final int E_DISCONNECTED = 22;
  public static final int E_LOGGED_IN_AT_DIFF_LOCATION = 23;
  public static final int E_WIN_VIOLATED = 24;
  public static final int E_LOSS_VIOLATED = 25;
  public static final int E_OVER_SPENDING = 26;
  public static final int E_BUY_IN_NOT_ALLOWED = 27;
  public static final int E_VOTES_EXHAUSTED = 28;
  public static final int E_REGISTER = 29;
  public static final int E_AFF_PLAYER_REGISTER_FAILED = 30;
  public static final int E_PROVIDER_AUTHETICATION = 31;
  public static final int E_PROVIDER_BALANCE = 32;
  public static final int E_DB_FAILURE = 33;
  public static final int E_IP_RESTRICTED = 34;
  public static final int E_AFFILIATE_MISMATCH = 35;
  public static final int E_BANNED = 36;
  public static final int E_GAME_NOT_ALLOWED = 37;
  public static final int E_BUYIN_NOT_ALLOWED_BETWEEN_GAMES = 38;
  public static final int E_UNABLE_TO_JOIN = 39;
  public static final int E_REMOVED_FROM_WAITING=40;

}
