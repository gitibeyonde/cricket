package com.criconline.anim;

public class Gse {

  private Gse(int val) {
    intVal = val;
  }

  public static final Gse GAME_OVER = new Gse(0);
  public static final Gse GAME_BEGIN = new Gse(1);
  public static final Gse TOSS = new Gse(2);
  public static final Gse CHOICE = new Gse(3);
  public static final Gse ROUND_START = new Gse(4);
  public static final Gse NM_TOSS = new Gse(5);
  public static final Gse HEAD = new Gse(6);
  public static final Gse TAIL = new Gse(7);
  public static final Gse FIELDING = new Gse(8);
  public static final Gse BATTING = new Gse(9);

  // batsman states
  public static final Gse K_NM_BOWL = new Gse(10);
  public static final Gse K_LM_BOWL = new Gse(11);
  public static final Gse K_NM_BAT = new Gse(12);
  public static final Gse K_BAT = new Gse(13);
  public static final Gse K_LM_BAT = new Gse(14);
  public static final Gse K_NM_FIELD = new Gse(15);
  public static final Gse K_LM_FIELD = new Gse(16);

  // bowler states
  public static final Gse B_NM_BOWL = new Gse(20);
  public static final Gse B_BOWL = new Gse(21);
  public static final Gse B_LM_BOWL = new Gse(22);
  public static final Gse B_NM_BAT = new Gse(23);
  public static final Gse B_LM_BAT = new Gse(24);
  public static final Gse B_NM_FIELD = new Gse(25);
  public static final Gse B_LM_FIELD = new Gse(26);

  // fielder states
  public static final Gse F_NM_BOWL = new Gse(30);
  public static final Gse F_LM_BOWL = new Gse(31);
  public static final Gse F_NM_BAT = new Gse(32);
  public static final Gse F_LM_BAT = new Gse(33);
  public static final Gse F_NM_FIELD = new Gse(34);
  public static final Gse F_FIELD = new Gse(35);
  public static final Gse F_LM_FIELD = new Gse(36);

  public static final Gse BOWLER_MOVE_BEGIN = new Gse(50);
  public static final Gse BOWLER_MOVE_END = new Gse(51);
  public static final Gse BOWLER_RUN = new Gse(52);
  public static final Gse BAST_MOVE_BEGIN = new Gse(53);
  public static final Gse BALL_THROW = new Gse(54);
  public static final Gse BATS_MOVE_IN_HIT_MODE = new Gse(55);
  public static final Gse BATSMAN_HIT_LOOKUP = new Gse(56);
  public static final Gse BATSMAN_HIT_START = new Gse(57);
  public static final Gse FIELDER_MOVE_BEGIN = new Gse(58);
  public static final Gse BATSMAN_HIT = new Gse(59);
  public static final Gse BATSMAN_HIT_OVER = new Gse(60);
  public static final Gse BALL_FLIGHT_START = new Gse(61);
  public static final Gse FIELDER_MOVE_END = new Gse(62);
  public static final Gse FIELDER_RUN_START = new Gse(63);
  public static final Gse BALL_NEAR_FIELDER = new Gse(64);
  public static final Gse BALL_INTERRUPTED = new Gse(65);
  public static final Gse BALL_FLIGHT_OVER = new Gse(66);
  public static final Gse BALL_FLIGHT_RETURN = new Gse(67);
  public static final Gse BATSMAN_MISS = new Gse(68);
  public static final Gse BATSMAN_DEFAULT = new Gse(69);
  public static final Gse BATSMAN_OUT = new Gse(70);
  public static final Gse FIELDER_DEFAULT = new Gse(71);
  public static final Gse FIELDING_LOOKUP = new Gse(72);
  public static final Gse FIELDING_START = new Gse(73);
  public static final Gse FIELDING_MISS = new Gse(74);
  public static final Gse FIELDING_RCVD = new Gse(75);
  public static final Gse BATSMAN_MISS_WIDE = new Gse(76);


  public static final Gse PITCH_CHANGE = new Gse(80);
  public static final Gse INNING_OVER = new Gse(81);
  public static final Gse WIDE_BALL = new Gse(82);

  public static final Gse MOVE_NOT_FOUND = new Gse(90);
  public static final Gse UNKNOWN = new Gse(99);

  public static String stringValue(Gse event) {
    switch (event.intVal) {
      case 0:
        return "GAME_OVER";
      case 1:
        return "GAME_BEGIN";
      case 2:
        return "TOSS";
      case 3:
        return "CHOICE";
      case 4:
        return "ROUND_START";
      case 5:
        return "NM_TOSS";
      case 6:
        return "HEAD";
      case 7:
        return "TAIL";
      case 8:
        return "FIELDING";
      case 9:
        return "BATTING";

        // BATSMAN
      case 10:
        return "K_NM_BOWL";
      case 11:
        return "K_LM_BOWL";
      case 12:
        return "K_NM_BAT";
      case 13:
        return "K_BAT";
      case 14:
        return "K_LM_BAT";
      case 15:
        return "K_NM_FIELD";
      case 16:
        return "K_LM_FIELD";
        // bowler states
      case 20:
        return "B_NM_BOWL";
      case 21:
        return "B_BOWL";
      case 22:
        return "B_LM_BOWL";
      case 23:
        return "B_NM_BAT";
      case 24:
        return "B_LM_BAT";
      case 25:
        return "B_NM_FIELD";
      case 26:
        return "B_LM_FIELD";
        // fielder states
      case 30:
        return "F_NM_BOWL";
      case 31:
        return "F_LM_BOWL";
      case 32:
        return "F_NM_BAT";
      case 33:
        return "F_LM_BAT";
      case 34:
        return "F_NM_FIELD";
      case 35:
        return "F_FIELD";
      case 36:
        return "F_LM_FIELD";
        //animations states
      case 50:
        return "BOWLER_MOVE_BEGIN";
      case 51:
        return "BOWLER_MOVE_END";
      case 52:
        return "BOWLER_RUN";
      case 53:
        return "BAST_MOVE_BEGIN";
      case 54:
        return "BALL_THROW";
      case 55:
        return "BATS_MOVE_IN_HIT_MODE";
      case 56:
        return "BATSMAN_HIT_LOOKUP";
      case 57:
        return "BATSMAN_HIT_START";
      case 58:
        return "FIELDER_MOVE_BEGIN";
      case 59:
        return "BATSMAN_HIT";
      case 60:
        return "BATSMAN_HIT_OVER";
      case 61:
        return "BALL_FLIGHT_START";
      case 62:
        return "FIELDER_MOVE_END";
      case 63:
        return "FIELDER_RUN_START";
      case 64:
        return "BALL_FLIGHT_NEAR_FIELDER";
      case 65:
        return "BALL_INTERRUPTED";
      case 66:
        return "BALL_FLIGHT_OVER";
      case 67:
        return "BALL_FLIGHT_RETURN";
      case 68:
        return "BATSMAN_MISS";
      case 69:
        return "BATSMAN_DEFAULT";
      case 70:
        return "BATSMAN_OUT";
      case 71:
        return "FIELDER_DEFAULT";
      case 72:
        return "FIELDING_LOOKUP";
      case 73:
        return "FIELDING_START";
      case 74:
        return "FIELDING_MISS";
      case 75:
        return "FIELDING_RCVD";
      case 76:
        return "BATSMAN_MISS_WIDE";
      case 80:
        return "PITCH_CHANGE";
      case 81:
        return "INNING_OVER";
      case 82:
        return "WIDE_BALL";

      default:
        return "UNKNOWN";
    }
  }

  public int intValue() {
    return intVal;
  }

  public int hashCode() {
    return intVal;
  }

  public boolean equals(Object o) {
    return ( (o.getClass() == this.getClass()) &&
            ( (Gse) o).intVal == this.intVal);
  }

  int intVal;

  public String toString() {
    return Gse.stringValue(this);
  }
}
