package com.cricket.mmog;

public class Moves {

  //Poker moves
  //###1
  public static final long JOIN = 1;
  public static final long HEAD = 2;
  public static final long TAIL = 4;
  public static final long TOSS = 8;

  //###2
  public static final long B_BOWL = 0x10;
  public static final long B1 = 0x20;
  public static final long B2 = 0x40;
  public static final long B3 = 0x80;

  //###3
  public static final long B4 = 0x100;
  public static final long B5 = 0x200;
  public static final long B6 = 0x400;
  public static final long B7 = 0x800;

  //###4
  public static final long B12 = 0x1000;
  public static final long B14 = 0x2000;
  public static final long B8 = 0x4000;
  public static final long B9 = 0x8000;

  //###5
  public static final long FIELDING = 0x10000;
  public static final long BATTING = 0x20000;
  public static final long B10 = 0x40000;
  public static final long B11 = 0x80000;

  //Bingo specific
  //###6
  public static final long K_BAT = 0x100000;
  public static final long K_RUN = 0x200000;
  public static final long K2 = 0x400000;
  public static final long K3 = 0x800000;

  //Blackjack specific
  public static final long K4 = 0x1000000; //16777216;
  public static final long K5 = 0x2000000;
  public static final long K6 = 0x4000000;
  public static final long K7 = 0x8000000;

  public static final long K8 = 0x10000000; //268435456;
  public static final long K9 = 0x20000000;
  public static final long K10 = 0x40000000;
  public static final long K14 = 0x80000000;

  public static final long F_FIELD = 0x100000000L;
  public static final long F1 = 0x200000000L;
  public static final long F2 = 0x400000000L;
  public static final long F3 = 0x800000000L;

  public static final long DRINKS = 0x1000000000L;
  public static final long START = 0x2000000000L;
  public static final long K12 = 0x4000000000L;
  public static final long NONE = 0x8000000000L;

  public static final long MOVE = 0x10000000000L;
  public static final long U10 = 0x20000000000L;
  public static final long U11 = 0x40000000000L;
  public static final long LEAVE = 0x80000000000L;

// VARIOUS MASKS
  public static final long CRIC_MASK = 0xFFFFFFFFFFFFFFFFL;
  public static final long RESP_REQ = 0xFF00FF00FEL;

  private static String[] strings = new String[] {
      "join", "head", "tail", "toss",
      "bowl", "b", "b", "b",
      "b", "b", "b", "b",
      "b", "b1", "b2", "b3",
      "fielding", "batting", "b6", "b7",
      "bat", "run", "k", "k",
      "k", "k", "k", "k",
      "k", "k", "k", "k2",
      "field", "f", "f", "f",
      "drinks", "start", "k9", "none",
      "u9", "u10", "u11", "leave"
  };

  static long[] values = new long[] {
      1, 2, 4, 8,
      16, 32, 64, 128,
      256, 512, 1024, 2048,
      4096, 8192, 16384, 32768,
      65536, 131072, 262144, 524288,
      1048576, 2097152, 4194304, 8388608,
      16777216, 33554432, 67108864, 134217728,
      268435456, 536870912, 1073741824, 0x80000000,
      0x100000000L, 0x200000000L, 0x400000000L, 0x800000000L,
      0x1000000000L, 0x2000000000L, 0x4000000000L, 0x8000000000L,
      0x10000000000L, 0x20000000000L, 0x40000000000L, 0x80000000000L,
      0x100000000000L, 0x200000000000L, 0x400000000000L, 0x800000000000L,
      0x1000000000000L, 0x2000000000000L, 0x4000000000000L, 0x8000000000000L,
  };

  private long intVal;

  /**private Moves(long intVal) {
    this.intVal = intVal;
  }**/

  public static long intValue(String str) {
    for (int i = 0; i < strings.length; i++) {
      if (str.equals(strings[i])) {
        return values[i];
      }
    }
    return -9;
  }

  public static int intIndex(long move) {
    byte index = -1;
    while (move > 0) {
      index++;
      move >>>= 1;
    }
    return index;
  }

  public static String stringValue(long mvId) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      if (mvId == values[i]) {
        return strings[i];
      }
      else if ( (mvId & values[i]) > 0) {
        buf.append(strings[i]).append("|");
      }
    }
    if (buf.length() == 0) {
      throw new IllegalArgumentException("Invalid mvId : " + mvId);
    }
    return buf.deleteCharAt(buf.length() - 1).toString();
  }

  public static boolean idValid(long mvId) {
    if (mvId > 12 || mvId < 0) {
      return false;
    }
    return true;
  }

  public static void main(String[] argv) {
    System.out.println(Moves.stringValue(1048576));
  }

  public long intVal() {
    return intVal;
  }

  // implement this method ...
  public boolean equals(Object o) {
    return super.equals(o);
  }

  public int hashCode() {
    return super.hashCode();
  }

  public static boolean responseRequired(long move) {
    return (move & RESP_REQ) > 0;
  }

}
