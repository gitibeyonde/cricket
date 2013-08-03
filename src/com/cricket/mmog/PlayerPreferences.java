package com.cricket.mmog;

public class PlayerPreferences {

  // active states
  /*
    private boolean bigSymbols = false;
    private boolean autoPostBlind = true;
    private boolean waitForBigBlind = true;
    private boolean showBestCards = true;
    private boolean muckLosingCards = true;
    private boolean randomDelay = false;
   */
  public static final int BOTS = 1;
  public static final int LEFT_HANDER = 2;
  public static final int u1 = 4;
  public static final int u2 = 8;

  public static final int K_NOVICE = 0x01;
  public static final int K_BEGINER = 0x02;
  public static final int K_MEDIUM = 0x03;
  public static final int K_EXPERT = 0x04;

  public static final int K_MASTER = 0x001;
  public static final int K_GRANDMASTER = 0x002;
  public static final int K_SUPERMASTER = 0x003;
  public static final int K_SUPERGRANDMASTER = 0x004;

  public static final int GUEST = 0x0001;
  public static final int REGISTERED = 0x00002;
  public static final int VERIFIED = 0x0003;
  public static final int SUBSCRIBER = 0x0004;

  public static final int B_NOVICE = 0x00001;
  public static final int B_BEGINER = 0x00002;
  public static final int B_MEDIUM = 0x00003;
  public static final int B_EXPERT = 0x00004;

  public static final int B_MASTER = 0x000001;
  public static final int B_GRANDMASTER = 0x000002;
  public static final int B_SUPERMASTER = 0x000003;
  public static final int B_SUPERGRANDMASTER = 0x000004;

  public static final int B_OVER_THE_WICKET = 0x0000001;
  public static final int B_ROUND_THE_WICKET = 0x0000002;
  public static final int B_SLOW = 0x0000003;
  public static final int B_FAST = 0x0000004;

  public static final int B_SPINNER = 0x00000001;
  public static final int B_u1 = 0x00000002;
  public static final int B_u2 = 0x00000003;
  public static final int B_u3 = 0x00000004;

  public static final int K_OPENER = 0x000000001;
  public static final int K_u1 = 0x000000002;
  public static final int K_u2 = 0x000000003;
  public static final int K_u3 = 0x000000004;

  public static final int PLAYER_TAGGING = 65536;
  public static final int DISABLE_CHAT = 131072;
  public static final int BANNED_PLAYER = 262144;

  private static String[] strings = new String[] {
      "bots", "left-hander", "u1", "u2",
      "k_novice", "k_beginer", "k_medium", "k_expert",
      "k_master", "k_grandmaster", "k_supermaster", "k_supergrandmaster",
      "guest", "registered", "verified", "subscriber",
      "b_novice", "b_beginer", "b_medium", "b_expert",
      "b_master", "b_grandmaster", "b_supermaster", "b_supergrandmaster",
      "u8", "u9", "u10", "u11",
      "u8", "u9", "u10", "u11"
  };

  static int[] values = new int[] {
      1, 2, 4, 8,
      16, 32, 64, 128,
      256, 512, 1024, 2048,
      4096, 8192, 16384, 32768,
      65536, 131072, 262144, 524288,
      1048576, 2097152, 4194304, 8388608,
      16777216, 33554432, 67108864, 134217728,
      268435456, 536870912, 1073741824};

  private int intVal;

  /**private PlayerPreferences(int intVal) {
    this.intVal = intVal;
  }**/

  public static int intValue(String str) {
    for (int i = 0; i < strings.length; i++) {
      if (str.equals(strings[i])) {
        return values[i];
      }
    }
    return 0;
  }

  public static String stringValue(int mvId) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      if (mvId == values[i]) {
        return strings[i];
      }
      else if ( (mvId & values[i]) == values[i]) {
        buf.append(strings[i]).append("|");
      }
    }
    if (buf.length() == 0) {
      return "";
    }
    return buf.deleteCharAt(buf.length() - 1).toString();
  }

  public static boolean idValid(int mvId) {
    if (mvId > 12 || mvId < 0) {
      return false;
    }
    return true;
  }

  public static void main(String[] argv) {
    System.out.println("Player Status =" + stringValue(12288));
    System.out.println("Player Status =" + stringValue(16777216));
  }

  public int intVal() {
    return intVal;
  }

  // implement this method ...
  public boolean equals(Object o) {
    return super.equals(o);
  }

  public int hashCode() {
    return super.hashCode();
  }

}
