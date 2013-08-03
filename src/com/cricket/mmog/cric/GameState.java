package com.cricket.mmog.cric;

import java.io.*;

public class GameState
    implements Serializable {

  //Cricket states
  public static final int OPEN = 1;
  public static final int U2 = 2;
  public static final int TOSS = 4;
  public static final int FIRST_INNING = 8;

  //
  public static final int SECOND_INNING = 16;
  public static final int INTERVAL = 32;
  public static final int RESULTS = 64;
  public static final int REPLAY = 128;


  // tourny states
  public static final int DECLARATION = 256;
  public static final int STARTED = 512;
  public static final int ENDED = 1024;


  public static final int OFFLINE = 536870912;
  public static final int UNDEFINED = 1073741824;

  private static String[] strings = new String[] {
      "open", "u2", "toss", "inning1",
      "inning2", "interval", "results", "replay",
      "declared", "started", "ended", "j3",
      "u4", "u5", "u6", "u7",
      "u4", "u5", "u6", "u7",
      "u4", "u5", "u6", "u7",
      "u4", "u5", "u6", "u7",
      "u4", "u5", "u6", "UNDEFINED",

  };

  static int[] values = new int[] {
      1, 2, 4, 8,
      16, 32, 64, 128,
      256, 512, 1024, 2048,
      4096, 8192, 16384, 32768,
      65536, 131072, 262144, 524288,
      1048576, 2097152, 4194304, 8388608,
      16777216, 33554432, 67108864, 134217728,
      268435456, 536870912, 1073741824

  };

  private int intVal;

  public GameState(int intVal) {
    this.intVal = intVal;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      if (intVal == values[i]) {
        return strings[i];
      }
      else if ( (intVal & values[i]) == values[i]) {
        buf.append(strings[i]).append("|`");
      }
    }
    if (buf.length() == 0) {
      throw new IllegalArgumentException("Invalid GameState : " + intVal);
    }
    return buf.deleteCharAt(buf.length() - 1).toString();
  }

  public static void main(String[] argv) {
  }

  public int intVal() {
    return intVal;
  }

  public boolean isReal() {
    return true;
  }

  public boolean isOpen() {
    return (intVal & OPEN) > 0;
  }

  public boolean isToss() {
    return (intVal & TOSS) > 0;
  }

  public boolean isPlaying() {
    return (intVal & (FIRST_INNING | SECOND_INNING)) > 0;
  }

  public boolean isFirstInning() {
    return (intVal & FIRST_INNING) > 0;
  }

  public boolean isSecondInning() {
    return (intVal & SECOND_INNING) > 0;
  }


  public boolean equals(GameState g) {
    return g.intVal == intVal;
  }

}
