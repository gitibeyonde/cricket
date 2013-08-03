package com.cricket.mmog;

import java.io.*;

public class GameType
    implements Serializable {

  //Poker moves
  public final static int PRACTISE = 1;
  public final static int FOUR_PLAYER = 2;
  public final static int SIX_PLAYER = 4;
  public final static int EIGHT_PLAYER = 8;

  public final static int PROF_FOUR = 16;
  public final static int PROF_SIX = 32;
  public final static int PROF_EIGHT = 64;
  public final static int PROF_ELEVEN = 128;

  public final static int JUNE2008 = 256;
  public final static int JULY2008 = 512;
  public final static int AUG2008 = 1024;
  public static final int SEPT2008 = 2048;


  public final static int WORLD_CUP = 0x1000;
  public final static int CP_LEAGUE = 0x2000;
  public final static int BOT_TABLE = 0x4000;
  public final static int RANDOM_BOT_TABLE = 0x8000;

  //




// MASKS////////////////////////////////////////////
  public static final int LOWER_HALF_SHOTS= PRACTISE | FOUR_PLAYER | SIX_PLAYER | PROF_FOUR | PROF_SIX | WORLD_CUP | CP_LEAGUE | JUNE2008 | JULY2008 | AUG2008 | SEPT2008;
  public static final int CRICKET_MASK = 0xFFFF;
  public static final int PRACTISE_MASK = 0x0F;
  public static final int MATCH_MASK = 0x0F0;
  public static final int MADNESS_MATCH = 0xF00;
  public static final int TOURNY_MASK = 0xFFFF;


  private static String[] strings = new String[] {
      "Practice", "Roadies", "Park", "Stadium",
      "Pro-Practice", "Pro-Roadies", "Pro-Stadium", "Pro-Ranjee",
      "June 2008 Cricket Madness Tourny", "July 2008 Cricket Madness Tourny", "August 2008 Cricket Madness Tourny", "Sept 2008 Cricket Madness Tourny",
      "World-Cup", "CP-League", "bot", "random",
      "u4", "u5", "u6", "u7",
      "u4", "u5", "u6", "u7"
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

  public GameType(int intVal) {
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
      throw new IllegalArgumentException("Invalid GameType : " + intVal);
    }
    return buf.deleteCharAt(buf.length() - 1).toString();
  }

  public static void main(String[] argv) {
  }

  public int intVal() {
    return intVal;
  }

  public boolean isReal() {
    return false;
  }

  public static boolean isReal(int gameType) {
          return false;
  }

  public static boolean isPlay(int gameType) {
          return true;
  }

  public int teamSize(){
    switch (intVal){
      case GameType.PRACTISE:
        return 1;
      case GameType.FOUR_PLAYER:
        return 4;
      case GameType.SIX_PLAYER:
        return 6;
      case GameType.EIGHT_PLAYER:
        return 8;
      case GameType.PROF_FOUR:
        return 4;
      case GameType.PROF_SIX:
        return 6;
      case GameType.PROF_EIGHT:
        return 8;
      case GameType.PROF_ELEVEN:
        return 11;
      case GameType.JUNE2008:
        return 6;
      case GameType.JULY2008:
        return 6;
      case GameType.AUG2008:
        return 6;
      case GameType.SEPT2008:
        return 6;
      case GameType.WORLD_CUP:
        return 4;
      case GameType.CP_LEAGUE:
        return 6;

      default:
        throw new IllegalStateException("Unknown game type " + intVal);
    }
  }

  public boolean isOnePlayer(){
    return (intVal & PRACTISE) > 0;
  }
  public boolean isFourPlayer(){
    return (intVal & (FOUR_PLAYER | PROF_FOUR | WORLD_CUP)) > 0;
  }
  public boolean isSixPlayer(){
    return (intVal & (SIX_PLAYER | PROF_SIX) | CP_LEAGUE | JUNE2008 | JULY2008 | AUG2008 |  SEPT2008) > 0;
  }
  public boolean isEightPlayer(){
    return (intVal & (EIGHT_PLAYER | PROF_EIGHT)) > 0;
  }
  public boolean isElevenPlayer(){
    return (intVal & PROF_ELEVEN) > 0;
  }



  public boolean isLowerHalfPitch(){
    return (intVal & LOWER_HALF_SHOTS)>0;
  }

  public boolean isTourny(){
    return (intVal & TOURNY_MASK ) > 0;
  }

  public boolean isMatch(){
    return (intVal & MATCH_MASK ) > 0;
  }

  public boolean isCricket(){
    return (intVal & CRICKET_MASK ) > 0;
  }


  public boolean isPractise(){
    return (intVal & PRACTISE_MASK ) > 0;
  }


  public boolean isMadness(){
    return (intVal & MADNESS_MATCH ) > 0;
  }

  public boolean equals(GameType g) {
    return g.intVal == intVal;
  }


    public boolean isBotGame() {
        return (intVal & BOT_TABLE) > 0;
    }

    public boolean isRandomBotGame() {
        return (intVal & RANDOM_BOT_TABLE) > 0;
    }

}
