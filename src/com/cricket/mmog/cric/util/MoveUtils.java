package com.cricket.mmog.cric.util;

import com.criconline.anim.AnimationConstants;
import com.criconline.anim.AnimationEvent;
import com.cricket.mmog.PlayerStatus;
import java.util.logging.Logger;
import com.cricket.mmog.GameType;

public class MoveUtils implements AnimationConstants {
  static Logger _cat = Logger.getLogger(MoveUtils.class.getName());

  public MoveUtils() {
  }

  public static AnimationEvent getBatsAnimation(MoveParams mp){
    AnimationEvent bathit;
    int pitch_distance = mp.getBallHitDistance();
    _cat.finest("Pitch Distance =" + pitch_distance);
    //mp._direction = 6.9;
    if (mp._direction < 1.0) { //drive
      if (pitch_distance < 200){
        bathit = new AnimationEvent("defence", BATS, 0);
      }
      else if (pitch_distance < 400){
        bathit = new AnimationEvent("topdefence", BATS, 0);
      }
      else {
        bathit = new AnimationEvent("drive", BATS, 0);
      }
    }
    else if (mp._direction < 1.5) { // leg
      bathit = new AnimationEvent("leg", BATS, 0);
    }
    else if (mp._direction < 2.0) { //hook
      bathit = new AnimationEvent("hook", BATS, 0);
    }
    else if (mp._direction < 3.5) { // hit _wicket

      bathit = new AnimationEvent("earlyhit", BATS, 0);
    }
    else if (mp._direction < 4.5) { //cut

      bathit = new AnimationEvent("cut", BATS, 0);
    }
    else if (mp._direction < 5.5) { // midoff

      bathit = new AnimationEvent("cut", BATS, 0);
    }
    else if (mp._direction < 6.8) { // drive
      if (pitch_distance < 200){
        bathit = new AnimationEvent("defence", BATS, 0);
      }
      else if (pitch_distance < 400){
        bathit = new AnimationEvent("topdefence", BATS, 0);
      }
      else {
        bathit = new AnimationEvent("drive", BATS, 0);
      }
    }
    else if (mp._direction < 7.8) { //leg

      bathit = new AnimationEvent("hook", BATS, 0);
    }
    else if (mp._direction < 9.3) { // hook
      bathit = new AnimationEvent("hook", BATS, 0);
    }
    else { //drive
      if (pitch_distance < 200){
        bathit = new AnimationEvent("defence", BATS, 0);
      }
      else if (pitch_distance < 400){
        bathit = new AnimationEvent("topdefence", BATS, 0);
      }
      else {
        bathit = new AnimationEvent("drive", BATS, 0);
      }

    }
    //bathit = new AnimationEvent("cut", BATS, 0);
    return bathit;

  }

  public static boolean doFielding(MoveParams field, MoveParams bats){
    if (field.getFieldAction() == MISFIELD){
      return false;
    }
    else {
      return true;
    }
  }


  public static int calculateRun(MoveParams bowl, MoveParams bat, MoveParams field){
    int btim = bat.isBatTimingCorrect(bowl);
    if (btim==EARLY_HIT || btim == LATE_HIT){
      return 0;
    }
    else if (btim==WIDE){
      return 1;
    }
    if (field != null && field.getFieldAction() != field.MISFIELD ){
      return 0;
    }
    int dist = bat.getBallHitDistance();
    double rad = bat._direction;
    _cat.finest("Rad = " + rad + ", dist=" + dist + " {" + bat._confidence + ", " + bat._force + ", " +  bat._timing  + ", " + bat._contact + " }");
      if (dist > 950) {
        return 6;
      }
      else if (dist > 650) {
        return 4;
      }
      else if (dist > 200) {
        return 2;
      }
      else if (dist > 100) {
        return 1;
      }
      else {
        return 0;
      }
  }

/**
  public final static double[][] _map = { // start radians, end radians, batsman move, start distance, end distance, fielder, fielder_move
                                          //drive
                                          { 0, 0.4, DRIVE, 100, 150, NONE, NONE },
                                          { 0.4, 0.8, DRIVE,100, 150, RF3, CATCH },
                                          { 0.8, 1.0, DRIVE, 100, 150, NONE, NONE },
                                          //leg
                                          { 1.0, 1.3, LEG, 100, 150, RF1, CATCH },
                                          { 1.3, 1.7, LEG, 100, 150, NONE, NONE },
                                          //hook
                                          { 1.7, 2, HOOK, 100, 150, RF2, CATCH },
                                          { 2, 2.4, HOOK, 100, 150, NONE, NONE },
                                          { 2.4, 2.8, HOOK, 100, 150, NONE, NONE },
                                          // hit wicket
                                          { 2.8, 3.5, HITWC, 100, 150, NONE, NONE },
                                          // cut
                                          { 3.5, 4, CUT, 100, 150, NONE, NONE },
                                          { 4, 4.5, CUT, 100, 150, LF2, CATCH },
                                          { 4.5, 5, CUT, 100, 150, NONE, NONE },
                                          // mid off
                                          { 5, 5.4, MIDOFF, 100, 150, LF1, CATCH },
                                          { 5.4, 5.9, MIDOFF, 100, 150, NONE, NONE },
                                          //drive
                                          { 5.9, 6.2, DRIVE, 100, 150, LF3, CATCH },
                                          { 6.2, 0, DRIVE, 100, 150, NONE, NONE },
    } ;**/

   public static String whichFielderStr(GameType gt, MoveParams bats){
     return PlayerStatus.stringValue(getPlyerStatus(getFielderMove(gt, bats, -1)));
   }
   public static long whichFielder(GameType gt, MoveParams bats){
        return getPlyerStatus(getFielderMove(gt, bats, -1));
   }
    public synchronized static long getFielderMove(GameType gt, MoveParams bats, long ps_fielder){
      double rad = bats._direction;
      int distance = bats.getBallHitDistance();
      if (ps_fielder==-1){
        ps_fielder = PlayerStatus.M_FIELDER;
      }
      PlayerStatus ps = new PlayerStatus(ps_fielder);
      if (gt.isOnePlayer()){
        // no fielding moves for these types
        if ((rad >= 5.5 || rad < 1) && ps.isBowler()&& distance > 100){
         _cat.info("Field move for bowler");
         return BOWL;
       }
      }
      else if (gt.isFourPlayer()){
        if (rad >= 4.5 && rad < 5.8 && ps.isLF1() && distance > 100) {
          _cat.info("Field move for lf1");
          return LF1;
          // LF1
        }
        if (rad < 2.0 && rad >= 0.7 && ps.isRF1()&& distance > 100) {
          _cat.info("Field move for rf1");
          return RF1;
          // LF1
        }
        if ((rad >= 5.8 || rad < 0.7) && ps.isBowler()&& distance > 100){
          _cat.info("Field move for bowler");
          return BOWL;
        }
      }
      // small pitch 64
      else if(gt.isSixPlayer()){
        if (rad > 4.71 && rad < 5.5 ) {
          _cat.info("Field move for lf1");
          if (distance  > 100 && ps.isLF1()){
            return LF1;
          }
        }
        if (rad > 5.5 && rad < 6.3 ) {
          _cat.info("Field move for lf2");
          if (distance  > 100 && ps.isLF1()){
            return LF2;
          }
        }
        if (rad < 1.5 && rad > 0.3) {
          _cat.info("Field move for rf1");
          if (distance > 100 && ps.isRF1()){
            return RF1;
          }
        }
        if ((rad > 6.0 || rad < 0.5) && ps.isBowler() && distance > 100){
          _cat.info("Field move for bowler");
          return BOWL;
        }
      }

      else if(gt.isEightPlayer()){
        if (rad > 5 && rad < 6.0) {
          _cat.info("Field move for lf1");
          if (distance < 750 && ps.isLF1() ){
            return LF1;
          }
          else if ( ps.isLF2() ){
            return LF2;
          }
        }
        if (rad < 1.5 && rad > 0.5 && ps.isRF1()) {
          _cat.info("Field move for rf1");
          if (distance < 750){
            return RF1;
          }
          else {
            return RF2;
          }

        }
        if ((rad > 6.0 || rad < 0.5)){
          _cat.info("Field move for bowler");
          if (distance < 400 && ps.isBowler()){
            return BOWL;
          }
          else if (rad > 0.6 && distance > 400 && ps.isLF2()){
            return LF2;
          }
          else if (rad<0.5 && distance > 400 && ps.isRF2()){
            return RF2;
          }
        }
      }
      else if(gt.isElevenPlayer()){
        if (rad < 0.79 && rad >= 0 && distance < 500 && ps.isBowler()){
          return BOWL;
        }
        else if (rad < 0.79 && rad >= 0 &&distance >= 500 && ps.isRF3()){
          return RF3;
        }
        else if(rad < 1.5 && rad >= 0.52 && distance < 500 && ps.isRF1()){
          return RF1;
        }
        else if(rad < 1.5 && rad >= 0.79 && distance >= 500 && ps.isRF2()){
          return RF2;
        }
        else if (rad < 2.36 && rad >= 1.5 && ps.isRF4()){
          return RF4;
        }
        else if (rad < 3.93 && rad >= 2.36 && ps.isLF5()){
          return LF5;
        }
        else if (rad < 4.71 && rad >= 3.93 && ps.isLF4()){
          return LF4;
        }
        else if (rad < 5.76 && rad >= 4.71 && distance < 500 && ps.isLF1()){
          return LF1;
        }
        else if (rad < 5.5 && rad >= 4.71 && distance >= 500 && ps.isLF2()){
          return LF2;
        }
        else if (rad < 6.28 && rad >= 5.5 && distance >= 500 && ps.isLF3()){
          return LF3;
        }
        else if (rad < 6.28 && rad >= 5.5 && distance < 500 && ps.isLF3()){
          return BOWL;
        }

      }
      else {
        throw new IllegalStateException("Unrecognized game type");
      }
      _cat.finest("Rad = " + rad + ", dist=" + distance + ", PS=" + ps.toString());
       return NONE;
     }

     public static long getPlyerStatus(long anim_fielder) {
        if (anim_fielder == LF1) {
          return PlayerStatus.LF1;
        }
        else if (anim_fielder == RF1) {
          return PlayerStatus.RF1;
        }
        else if (anim_fielder == LF2) {
          return PlayerStatus.LF2;
        }
        else if (anim_fielder == RF2) {
          return PlayerStatus.RF2;
        }
        else if (anim_fielder == LF3) {
          return PlayerStatus.LF3;
        }
        else if (anim_fielder == RF3) {
          return PlayerStatus.RF3;
        }
        else if (anim_fielder == LF4) {
          return PlayerStatus.LF4;
        }
        else if (anim_fielder == RF4) {
          return PlayerStatus.RF4;
        }
        else if (anim_fielder == LF5) {
          return PlayerStatus.LF5;
        }
        else if (anim_fielder == RF5) {
          return PlayerStatus.RF5;
        }
        else if (anim_fielder == WICKET_KEEPER) {
          return PlayerStatus.WICKET_KEEPER;
        }
        else if (anim_fielder == BOWL) {
          return PlayerStatus.BOWLER;
        }
        else {
          _cat.warning("Wrong player status ------------------!!!" +
                    anim_fielder);
          return 0; // NONE
        }
      }

    public static void main(String args[]){
      //System.out.println(getFielder(1.1));
      //System.out.println(getShot(1.1));
      for (int i=0;i<100;i++){
        System.out.println( (int) ( (4) * Math.random()) + 3);
      }
    }


}
