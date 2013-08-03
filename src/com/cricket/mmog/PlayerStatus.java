package com.cricket.mmog;

public class PlayerStatus {

  // active states1
  public static final long BATSMAN = 1;
  public static final long BOWLER = 2;
  public static final long LF1 = 4;
  public static final long RF1 = 8;

  public static final long LF2 = 16;
  public static final long RF2 = 32;
  public static final long LF3 = 64;
  public static final long RF3 = 128;

  // longermediate states3
  public static final long LF4 = 256;
  public static final long RF4 = 512;
  public static final long LF5 = 1024;
  public static final long RF5 = 2048;

  // inactive states 16 -32 // 4
  public static final long WICKET_KEEPER = 4096;
  public static final long RUNNER = 8192;
  public static final long EMPIRE = 16384;
  public static final long LEG_EMPIRE = 32768;

//5
  public static final long CAPTAIN = 65536;
  public static final long WISE_CAPTAIN = 131072;
  public static final long TOURNY = 262144;
  public static final long p4 = 524288;

//6
  public static final long WAITING = 1048576;
  public static final long NEW = 2097152;
  public static final long PRE_JOIN = 4194304;
  public static final long JOIN_REQUESTED = 8388608;

//7
  public static final long BATTING_DONE = 16777216;
  public static final long BOLD = 33554432;
  public static final long BROKE = 67108864;
  public static final long DISCONNECTED = 134217728;
//8
  public static final long SHILL = 0x10000000;
  public static final long REMOVED = 0x20000000;
  public static final long RESP_REQ = 0x40000000;
  public static final long U3 = 0x80000000;
//9
  public static final long U4 = 0x100000000L;
  public static final long U5 = 0x200000000L;
  public static final long U6 = 0x400000000L;
  public static final long U7 = 0x800000000L;
//10
  public static final long MATCH_PLAYER = 0x1000000000L;
  public static final long TOURNY_PLAYER = 0x2000000000L;
  public static final long PROFESSIONAL_PLAYER = 0x4000000000L;
  public static final long TEAM_MANAGER = 0x8000000000L;
//11
  public static final long MEMBER_OCA = 0x10000000000L;
  public static final long CHAIRMAN_OCA = 0x20000000000L;
  public static final long HONOURARY_BOARD_MEMBER = 0x40000000000L;
  public static final long THE_BOSS = 0x80000000000L;

////////////////////////////MASKS//////////////////////////////////////
  public static final long M_INACTIVE = 0x24E00000;
  public static final long M_FIELDER = 0x1FFE;  //includes bowler, includes wicket keeper

  public static final long M_PLAYING = 0xFFFF; // all players, excludes waiting
  public static final long M_PLAYER_TYPE = 0xFFFFF;
  public static final long M_RESET = 0x1FFFFFF; // batting done etc

  private static String[] strings = new String[] {
                                    "batsman", "bowler", "LF1", "RF1",
                                    "LF2", "RF2", "LF3", "RF3",
                                    "LF4", "RF4", "LF5", "RF5",
                                    "WICKET_KEEPER", "runner", "empire", "leg_empire",
                                    "captain", "wise captain", "tourny",  "p4",
                                    "waiting", "new", "pre-join", "can-join",
                                    "batting-done", "u2", "broke", "disconnected",
                                    "shill", "removed", "resp-req", "uu"
  };

  static long[] values = new long[] {
                        1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096,
                        8192, 16384, 32768, 65536, 131072, 262144, 524288,
                        1048576, 2097152, 4194304, 8388608, 16777216, 33554432,
                        67108864, 134217728, 268435456, 536870912, 1073741824,
  };

  private long longVal;

  public PlayerStatus(long longVal) {
    this.longVal = longVal;
  }
  
  public long longValue(){
      return longVal;
  }

  public static long longValue(String str) {
    for (int i = 0; i < strings.length; i++) {
      if (str.equals(strings[i])) {
        return values[i];
      }
    }
    return -1;
  }

  public static String stringValue(long mvId) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      if (mvId == values[i]) {
        return strings[i];
      }
      else if ((mvId & values[i]) == values[i]) {
        buf.append(strings[i]).append("|");
      }
    }
    if (buf.length() == 0) {
      return "";
    }
    return buf.deleteCharAt(buf.length() - 1).toString();
  }

  public static boolean idValid(long mvId) {
    if (mvId > 12 || mvId < 0) {
      return false;
    }
    return true;
  }

  public void setStatus(long ps) {
    longVal |= ps;
  }

  public static boolean isShill(long status) {
    return (status & PlayerStatus.SHILL) > 0;
  }

  public boolean isActive() {
    return (longVal & PlayerStatus.M_INACTIVE) == 0;
  }

  public long longVal() {
    return longVal;
  }

  public boolean isPreJoin() {
    return (longVal & PlayerStatus.PRE_JOIN) > 0;
  }

  public boolean isJoinRequested() {
    return ((longVal & PlayerStatus.JOIN_REQUESTED) > 0) && isPreJoin();
  }

  public boolean isNew() {
    return (longVal & PlayerStatus.NEW) > 0;
  }

  public boolean isWaiting() {
    return (longVal & PlayerStatus.WAITING) > 0;
  }

  public boolean isPlaying() {
    return (longVal & PlayerStatus.M_PLAYER_TYPE) > 0;
  }

  public boolean isBatsman() {
    return (longVal & PlayerStatus.BATSMAN) > 0;
  }

  public boolean isRunner() {
    return (longVal & PlayerStatus.RUNNER) > 0;
  }

  public boolean isBowler() {
    return (longVal & PlayerStatus.BOWLER) > 0;
  }

  public boolean isFielder() {
    return (longVal & PlayerStatus.M_FIELDER) > 0;
  }

  public boolean isLF1() {
    return (longVal & PlayerStatus.LF1) > 0;
  }

  public boolean isRF1() {
    return (longVal & PlayerStatus.RF1) > 0;
  }

  public boolean isLF2() {
    return (longVal & PlayerStatus.LF2) > 0;
  }

  public boolean isRF2() {
    return (longVal & PlayerStatus.RF2) > 0;
  }

  public boolean isLF3() {
    return (longVal & PlayerStatus.LF3) > 0;
  }

  public boolean isRF3() {
    return (longVal & PlayerStatus.RF3) > 0;
  }

  public boolean isLF4() {
    return (longVal & PlayerStatus.LF4) > 0;
  }

  public boolean isRF4() {
    return (longVal & PlayerStatus.RF4) > 0;
  }

  public boolean isLF5() {
    return (longVal & PlayerStatus.LF5) > 0;
  }

  public boolean isRF5() {
    return (longVal & PlayerStatus.RF5) > 0;
  }

  public boolean isWicketKeeper() {
    return (longVal & PlayerStatus.WICKET_KEEPER) > 0;
  }

  public boolean isDisconnected() {
    return (longVal & PlayerStatus.DISCONNECTED) > 0;
  }

  public long getPlayerType(){
    return longVal & M_PLAYING;
  }

  public String getPlayerTypeString(){
    return stringValue(longVal & M_PLAYING);
  }

  // implement this method ...
  public boolean equals(Object o) {
    if (o instanceof PlayerStatus){
      PlayerStatus ps = (PlayerStatus) o;
      return longVal == ps.longVal;
    }else {
      return false;
    }
  }


  public String toString() {
    return stringValue(longVal);
  }

  public static void main(String[] argv) {
    System.out.println("Player Status =" + stringValue(4224));
    System.out.println("Player Status =" + stringValue(4352));
    System.out.println("Player Status =" + stringValue(4608));
  }

}
