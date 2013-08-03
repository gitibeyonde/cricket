package com.cricket.mmog.cric.util;

import com.agneya.util.Utils;
import com.agneya.util.Rng;
import java.util.logging.Logger;
import com.criconline.anim.AnimationConstants;

public class MoveParams implements AnimationConstants {
  private static Logger _cat = Logger.getLogger(MoveParams.class.getName());
  public int _type;

  public int _sub_type;
  public int _timing;
  public int _abs_timing;
  public int _force;
  public double _direction;
  public int _contact;
  public int _spin;
  public int _confidence = 5;
  public int _agression = 1;
  public int _reaction = 1;
  public int _x, _y;


  public String stringValue() {
    StringBuffer sbuf = new StringBuffer();
    switch (_type) {
      case BATS:
        sbuf.append("bat|");
        break;
      case BOWL:
        sbuf.append("bowl|");
        break;
      case FIELD:
        sbuf.append("field|");
        break;
      case BATS_RUN:
        sbuf.append("run|");
        break;
      default:
        sbuf.append("unknown|");

    }
    sbuf.append(_sub_type).append("|");
    sbuf.append(_timing).append("|");
    sbuf.append(_abs_timing).append("|");
    sbuf.append(_force).append("|");
    sbuf.append(Utils.getRoundedString(_direction)).append("|");
    sbuf.append(_contact).append("|");
    sbuf.append(_spin).append("|");
    sbuf.append(_confidence).append("|");
    sbuf.append(_agression).append("|");
    sbuf.append(_reaction).append("|");
    sbuf.append(_x).append("|");
    sbuf.append(_y);
    return sbuf.toString();
  }

  public MoveParams(int i) {
    _type = i;
  }

  public MoveParams(String params) {
    if (params == null || params.trim().length() <= 1) {
      return;
    }
    String[] mv = params.split("\\|");
    assert mv.length >= 7:"Wrong params=" + params + ";";
    //
    if (mv[0].equals("bat")) {
      _type = BATS;
    }
    else if (mv[0].equals("bowl")) {
      _type = BOWL;
    }
    else if (mv[0].equals("field")) {
      _type = FIELD;
    }
    else if (mv[0].equals("run")) {
      _type = BATS_RUN;
    }
    else {
      _type = UNKNOWN;
    }
    //0=bat|1=-1|2=1|3=1213|4=1|5=0.00|6=1|7=0|8=2|9=1|10=391
    _sub_type = Integer.parseInt(mv[1]);
    _timing = Integer.parseInt(mv[2]);
    _abs_timing = Integer.parseInt(mv[3]);
    _force = Integer.parseInt(mv[4]);
    _direction = Double.parseDouble(mv[5]);
    _contact = Integer.parseInt(mv[6]);
    _spin = Integer.parseInt(mv[7]);
    _confidence = Integer.parseInt(mv[8]);
    _agression = Integer.parseInt(mv[9]);
    _reaction = Integer.parseInt(mv[10]);
    _x = Integer.parseInt(mv[11]);
    _y = Integer.parseInt(mv[12]);
  }

  public void setKeyMap(KeyMap km) {
    _spin = km.getSpin();
    _x = km.x();
    _y = km.y();
    _agression = km._count;
    _reaction = km.averageInterval();
  }

  public static MoveParams generate(int type) {
    MoveParams mp = new MoveParams(type);
    Rng rng = new Rng();
    mp._sub_type = -1;
    mp._timing = rng.nextIntBetween(1, 5);
    mp._abs_timing = mp._timing * 1000;
    mp._force = rng.nextIntBetween(1, 8);
    mp._contact = rng.nextIntBetween(1, 5);

    if (type == BATS) {
      mp._spin = 0;
      if (Math.random() > .5) {
        mp._direction = rng.doubleValueBetween(0, Math.PI / 2);
      }
      else {
        mp._direction = rng.doubleValueBetween(Math.PI * 3 / 2, 2 * Math.PI);
      }

    }
    else if (type == BOWL) {
      mp._x = 516;
      mp._y = 230;
      mp._spin = 0;
    }
    else {
      mp._x = 400;
      mp._y = 400;
      mp._spin = MISFIELD;
    }

    mp._confidence = rng.nextIntBetween(1, 6);
    mp._agression = rng.nextIntBetween(1, 5);
    mp._reaction = rng.nextIntBetween(100, 500);
    return mp;
  }

  public static MoveParams generate(int type, MoveParams mpo) {
    MoveParams mp = new MoveParams(type);
    Rng rng = new Rng();
    mp._timing = -1;
    mp._abs_timing = -1;
    if (type != FIELD) {
      mp._force = rng.nextIntBetween(1, 8);
      mp._contact = rng.nextIntBetween(1, 6);
      mp._spin = mpo._spin;
    }
    else {
      mp._x = 400;
      mp._y = 400;
      mp._spin = MISFIELD;
    }
    mp._confidence = rng.nextIntBetween(1, 5);
    mp._sub_type = rng.nextIntBetween(1, 10);
    mp._direction = rng.doubleValueBetween(0, 6.285);
    mp._agression = rng.nextIntBetween(1, 5);
    mp._reaction = rng.nextIntBetween(100, 500);

    return mp;
  }

  public void setX(int x) {
    _x = x;
  }

  public void setY(int y) {
    _y = y;
  }

  public int getX() {
    return _x;
  }

  public int getY() {
    return _y;
  }

  public int getSpin() {
    return _spin;
  }

  public int getFieldAction() {
    return _spin;
  }

  public int getAgression() {
    return _agression;
  }

  public int getReaction() {
    return _reaction;
  }

  public int geConfidence() {
    return _confidence;
  }


  //CATCH = 50;
  //FRONT_BLOCK = 51;
  //LEFT_BLOCK = 52;
  //RIGHT_BLOCK = 53;
  //MISFIELD = 54;
  public void setFieldAction(int action) {
    _spin = action;
  }

  public String toString() {
    StringBuffer sbuf = new StringBuffer();
    switch (_type) {
      case BATS:
        sbuf.append("bat|");
        break;
      case BOWL:
        sbuf.append("bowl|");
        break;
      case FIELD:
        sbuf.append("field|");
        break;
      case BATS_RUN:
        sbuf.append("run|");
        break;
      default:
        sbuf.append("unknown|");

    }
    sbuf.append("sub_type=").append(_sub_type).append("|");
    sbuf.append("timing=").append(_timing).append("|");
    sbuf.append("abs_timing=").append(_abs_timing).append("|");
    sbuf.append("force=").append(_force).append("|");
    sbuf.append("dir=").append(Utils.getRoundedString(_direction)).append("|");
    sbuf.append("contact=").append(_contact).append("|");
    sbuf.append("spin=").append(_spin).append("|");
    sbuf.append("conf=").append(_confidence).append("|");
    sbuf.append("agr=").append(_agression).append("|");
    sbuf.append("react=").append(_agression).append("|");
    sbuf.append("x=").append(_x).append("|");
    sbuf.append("y=").append(_y);

    return sbuf.toString();
  }

  public int getBallHitDistance() {
    return (_confidence + _contact) * (_force) * 20;
  }


  public String getBallCommentary() {
    StringBuffer sbuf = new StringBuffer();
    // check if ball is wide
    //sbuf.append("|" + getSpin() + "[" + getX() + ", " + getY() + "]");
    switch (getSpin()) {
      case -3: // far leg
        if (getX() > PP_RIGHT) {
          sbuf.append(" wide ball too far off on the on side ");
        }
        else if (getX() < PP_LEFT) {
          sbuf.append(" a fine ball with good direction and spin ");
        }
        break;
      case -2:
        if (getX() > PP_RIGHT) {
          sbuf.append(" wide ball too far off on the on side ");
        }
        else if (getX() < PP_LEFT) {
          sbuf.append(" a fine ball with good direction and spin ");
        }
        break;
      case -1:

        if (getX() > PP_RIGHT) {
          sbuf.append(" ball going on the leg side ");
        }
        else if (getX() < PP_LEFT) {
          sbuf.append(" a fine ball with good direction and spin ");
        }
        break;
      case 0:
        if (getX() > PP_LEFT && getX() > PP_RIGHT) {
          sbuf.append(" straight ball ");
        }
        break;
      case 1:
        if (getX() < PP_LEFT) {
          sbuf.append(" ball moving to off side ");
        }
        else if (getX() > PP_RIGHT) {
          sbuf.append(" a fine ball with good direction and spin ");
        }
        break;
      case 2:
        if (getX() < PP_LEFT) {
          sbuf.append(" wide ball too far off on the on side ");
        }
        else if (getX() > PP_RIGHT) {
          sbuf.append(" a fine ball with good direction and spin ");
        }

        break;
      case 3:
        if (getX() < PP_LEFT) {
          sbuf.append(" wide ball too far off on the on side ");
        }
        else if (getX() > PP_RIGHT) {
          sbuf.append(" a fine ball with good direction and spin ");
        }

        break;
      default:
        throw new IllegalStateException("Wild spin value " + getSpin());
    }

    // comment on pitch coordinate
    if (getY() < PP_OVER_PITCH) {
      sbuf.append(" a yorker, over pitched ");
    }
    else if (getY() > PP_SHORT_PITCH) {
      sbuf.append(" a short pitched ");
    }
    else {
      sbuf.append(" with a good length ");
    }

    _cat.finest("COMM==" + sbuf.toString());
    return sbuf.toString();
  }

  public boolean isBold() {
    if (_timing < 1) {
      return true;
    }
    else if (_timing > 6) {
      return true;
    }
    else {
      switch (_spin) {
        case -3:
          if (isPPBet(getX(), PP_LEFT + INTERVAL)) {
            return true;
          }
        case -2:
          if (isPPBet(getX(), PP_LEFT + 2 * INTERVAL)) {
            return true;
          }
        case -1:
          if (isPPBet(getX(), PP_LEFT + 3 * INTERVAL)) {
            return true;
          }
        case 0:
          if (isPPBet(getX(), PP_RIGHT - 5 * INTERVAL) ||
              isPPBet(getX(), PP_LEFT + 4 * INTERVAL)) {
            return true;
          }
        case 1:
          if (isPPBet(getX(), PP_RIGHT - 4 * INTERVAL)) {
            return true;
          }
        case 2:
          if (isPPBet(getX(), PP_RIGHT - 3 * INTERVAL)) {
            return true;
          }
        case 3:
          if (isPPBet(getX(), PP_RIGHT - 2 * INTERVAL)) {
            return true;
          }

        default:
          return false;
      }
    }
  }

  public static int INTERVAL = 10;
  public boolean isWideBall() {
    // check if ball is wide
    if (isPPBet(getX(), PP_RIGHT - INTERVAL)) {
      switch (getSpin()) {
        case -3:
        case -2:
        case -1:
        case 0:
          return true;
        default:
          return false;
      }
    }
    if (isPPBet(getX(), PP_RIGHT - 2 * INTERVAL)) {
      switch (getSpin()) {
        case -3:
        case -2:
        case -1:
          return true;
        default:
          return false;
      }
    }
    if (isPPBet(getX(), PP_RIGHT - 3 * INTERVAL)) {
      switch (getSpin()) {
        case -3:
        case -2:
          return true;
        default:
          return false;
      }
    }
    if (isPPBet(getX(), PP_RIGHT - 4 * INTERVAL)) {
      switch (getSpin()) {
        case -3:
          return true;
        default:
          return false;
      }
    }

    if (isPPBet(getX(), PP_LEFT)) {
      switch (getSpin()) {
        case 3:
        case 2:
        case 1:
        case 0:
          return true;
        default:
          return false;
      }
    }
    if (isPPBet(getX(), PP_LEFT + INTERVAL)) {
      switch (getSpin()) {
        case 3:
        case 2:
        case 1:
          return true;
        default:
          return false;
      }
    }
    if (isPPBet(getX(), PP_LEFT + 2 * INTERVAL)) {
      switch (getSpin()) {
        case 3:
        case 2:
          return true;
        default:
          return false;
      }
    }
    if (isPPBet(getX(), PP_LEFT + 3 * INTERVAL)) {
      switch (getSpin()) {
        case 3:
          return true;
        default:
          return false;
      }
    }

    return false;
  }

  public boolean isPPBet(int ppx, int start) {
    if (ppx >= start && ppx <= start + INTERVAL) {
      return true;
    }
    else {
      return false;
    }
  }

  public String getBatCommentary() {
    StringBuffer sbuf = new StringBuffer();

    if (_timing == 2) {
      sbuf.append("  late timing,   ");
    }
    else if (_timing == 3) {
      sbuf.append("   well timed,   ");
    }
    else {
      sbuf.append("  bad timing,   ");
    }

    _cat.finest(this +"comm==" + sbuf.toString());
    return sbuf.toString();

  }

  public String getShotCommentary() {
    StringBuffer sbuf = new StringBuffer();
    if (getBallHitDistance() > 200) {
      sbuf.append("  and it is a good shot going for the boundary  ");
    }
    else {
      sbuf.append("  and it is a good shot going for the boundary  ");
    }
    _cat.finest(this +"comm==" + sbuf.toString());
    return sbuf.toString();
  }

  public int isBatTimingCorrect(MoveParams bowl) {
    int batSpinRequired = calculateBatsSpinRequired(bowl);
    _cat.finest("Bat Spin = " + _spin + ", req " + batSpinRequired);
    _cat.finest("Timing=" + _timing);
    if ((Math.abs(batSpinRequired - _spin) > 1)) {
      return WIDE;
    }
    if (_timing < 1) {
      return EARLY_HIT;
    }
    else if (_timing > 6) {
      return LATE_HIT;
    }
    return HIT;
  }

  private int calculateBatsSpinRequired(MoveParams bowl) {
    int spin = bowl._spin;
    _cat.finest("Bowl coord=" + bowl.getX() + ", " + bowl.getY());
    if (isPPBet(bowl.getX(), PP_LEFT)) {
      switch (spin) {
        case -3:
          return 0;
        case -2:
          return -1;
        case -1:
          return -2;
        case 0:
          return -3;
        case 1:
          return -4;
        case 2:
          return -5;
        case 3:
          return -6;
      }
    }
    if (isPPBet(bowl.getX(), PP_LEFT + INTERVAL)) {
      switch (spin) {
        case -3:
          return 1;
        case -2:
          return 0;
        case -1:
          return -1;
        case 0:
          return -2;
        case 1:
          return -3;
        case 2:
          return -4;
        case 3:
          return -5;
      }
    }
    if (isPPBet(bowl.getX(), PP_LEFT + 2 * INTERVAL)) {
      switch (spin) {
        case -3:
          return 2;
        case -2:
          return 1;
        case -1:
          return 0;
        case 0:
          return -1;
        case 1:
          return -2;
        case 2:
          return -3;
        case 3:
          return -4;
      }
    }
    if (isPPBet(bowl.getX(), PP_LEFT + 3 * INTERVAL)) {
      switch (spin) {
        case -3:
          return 3;
        case -2:
          return 2;
        case -1:
          return 1;
        case 0:
          return 0;
        case 1:
          return -1;
        case 2:
          return -2;
        case 3:
          return -3;
      }
    }
    if (isPPBet(bowl.getX(), PP_LEFT + 4 * INTERVAL)) {
      switch (spin) {
        case -3:
          return 4;
        case -2:
          return 3;
        case -1:
          return 2;
        case 0:
          return 1;
        case 1:
          return 0;
        case 2:
          return -1;
        case 3:
          return -2;
      }
    }

    if (isPPBet(bowl.getX(), PP_RIGHT - 5 * INTERVAL)) {
      switch (spin) {
        case -3:
          return 4;
        case -2:
          return 3;
        case -1:
          return 2;
        case 0:
          return 1;
        case 1:
          return 0;
        case 2:
          return -1;
        case 3:
          return -2;
      }
    }
    if (isPPBet(bowl.getX(), PP_RIGHT - 4 * INTERVAL)) {
      switch (spin) {
        case -3:
          return 3;
        case -2:
          return 2;
        case -1:
          return 1;
        case 0:
          return 0;
        case 1:
          return -1;
        case 2:
          return -2;
        case 3:
          return -3;
      }
    }
    if (isPPBet(bowl.getX(), PP_RIGHT - 3 * INTERVAL)) {
      switch (spin) {
        case -3:
          return 4;
        case -2:
          return 3;
        case -1:
          return 2;
        case 0:
          return 1;
        case 1:
          return 0;
        case 2:
          return -1;
        case 3:
          return -2;
      }
    }
    if (isPPBet(bowl.getX(), PP_RIGHT - 2 * INTERVAL)) {
      switch (spin) {
        case -3:
          return 5;
        case -2:
          return 4;
        case -1:
          return 3;
        case 0:
          return 2;
        case 1:
          return 1;
        case 2:
          return 0;
        case 3:
          return -1;
      }

    }
    if (isPPBet(bowl.getX(), PP_RIGHT - INTERVAL)) {
      switch (spin) {
        case -3:
          return 6;
        case -2:
          return 5;
        case -1:
          return 4;
        case 0:
          return 3;
        case 1:
          return 2;
        case 2:
          return 1;
        case 3:
          return 0;
      }

    }
    return 0;
  }

  public static String getAnalysis(MoveParams bats, MoveParams ball) {
    if (bats.isBatTimingCorrect(ball) == HIT) {
      return " well played";
    }
    else {
      return " could have been tried better ";
    }
  }

}
