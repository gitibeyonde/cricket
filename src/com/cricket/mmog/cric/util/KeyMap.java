package com.cricket.mmog.cric.util;

import java.awt.event.KeyEvent;
import java.awt.Point;
import com.criconline.tts.Commentator;
import java.util.logging.Logger;
import com.criconline.anim.AnimationConstants;


public class KeyMap implements AnimationConstants {
  private static Logger _cat = Logger.getLogger(KeyMap.class.getName());
  public static final int U = KeyEvent.VK_UP;
  public static final int D = KeyEvent.VK_DOWN;
  public static final int R = KeyEvent.VK_RIGHT;
  public static final int L = KeyEvent.VK_LEFT;

  static final int REP = -2;

  Point _position;
  int _keys[];
  long _time[];
  int _count=0;
  int _type;

  String _shot_name[] = {
      //1
      "block",
      "straight front foot drive",
      "front foot off drive",
      "front foot on drive",
      "front foot cover drive",
      //6
      "cut",
      "square cut",
      "french cut",
      "leg glance",
      "pull",
      //11
      "sweep",
      "reverse sweep",
      "hook",
      "cow shot",
      "flick",
      //16
  };

  int _shot_keys[][] = {
      //1
      {},
      {D, REP},
      {D, D, D, R, REP},
      {D, D, D, L},
      {D, D, D, L, REP},
      //6
      {U, L, REP},
      {D, L, REP},
      {U, U, L, REP},
      {R, R, U, REP},
      {U, D, R, REP},
      //11
      {D, D, R ,REP},
      {D, D, D, R, REP},
      {U, U, D, D, R, REP},
      {U, U, D, D, R, REP},
      {L},
      //16
  };

  String _shot_desc[] = {
      //1
      "shot played with the bat vertical and angled down at the front, intended to stop the ball and drop it down quickly on to the pitch in front of the batsman.",
      "shot played with the bat sweeping down through the vertical. The ball travels swiftly along the ground in front of the striker.",
      "shot played with the bat sweeping down through the vertical. The ball travels swiftly along the ground in front of the striker.",
      "shot played with the bat sweeping down through the vertical. The ball travels swiftly along the ground in front of the striker.",
      "shot played with the bat sweeping down through the vertical. The ball travels swiftly along the ground in front of the striker.",
      //6
      "shot played with the bat close to horizontal, which hits the ball somewhere in the arc between cover and gully",
      "shot played with the bat close to horizontal, which hits the ball somewhere in the arc between cover and gully",
      "A misconnected off-side shot that deflects the ball unintentionally from the inner edge of the bat to fine leg or backward square-leg area.",
      "shot played off the bat at a glancing angle, through the slips area",
      "horizontal bat shot which pulls the ball around the batsman into the square leg area",
      // 11
      "Like a pull shot, except played with the backmost knee on the ground, so as to hit balls which bounce low",
      "An unconventional and hazardous sweep shot, hitting the ball on the off-side, anywhere between backward point and third man.",
      "Like a pull shot, but played to a bouncer and intended to hit the ball high in the air over square leg - hopefully for six runs",
      "a hard shot, usually in the air, across the line of a full-pitched ball, aiming to hit the ball over the boundary at cow corner, with very little regard to proper technique.",
      "a gentle movement of the wrist to move the bat, often associated with shots on the leg side.",
  };

  String _bestball_desc [] = {
      //1
     "block",
     "straight front foot drive",
     "front foot off drive",
     "front foot on drive",
     "front foot cover drive",
     //6
     "cut",
     "square cut",
     "french cut",
     "leg glance",
     "pull",
     //11
     "sweep",
     "The reverse sweep is played typically played against spinners to exploit fielding gaps, particularly when quick runs are needed",
     "hook",
     "cow shot",
     "flick",
     //16
  };


  String _ball_name [] = {
      "bouncer",
      "leg spin",
      "leg break",
      "leg cutter",
      "off spin",
      "off break",
      "off cutter",
      "bump ball",
      "dipper",
      "flipper",
      "floater",
      "In dipper",
      "In swing",
      "full length",
      "half volly",
      "long hop",
      "reverse",
      "shooter",
      "yorker",
      "full toss",
};

  int _ball_keys [][] = {
      {U, REP},//"a fast short pitched delivery that rises up near the batsman's head",
      {L},//"a leg spin delivery",
      {L, REP},//"a leg spin delivery turning very fast",
      {U, L, REP},//"a fast leg spin delivery",
      {R}, //"a off spin delivery",
      {R, REP},//"a off break delivery turning very fast",
      {U, R, REP},//"a fast off spin delivery",
      {U},//"a bump ball, pitching very close to the batsman",
      //"a delivery bowled with curves away from the batsman before pitching",
      //"a leg spin delivery with under-spin, so it bounces lower than normal",
      //"a delivery bowled by a spinner that travels in a highly arched path appearing to 'float' in the air",
      //"a delivery bowled with curves into the batsman before pitching",
      //"a delivery that curves into the batsman after pitching",
      //"a delivery that pitches closer to the batsman than a ball pitching on a good length, but further away than a half-volley",
      //"a delivery that bounces just short of the block hole. Usually easy to drive or glance away",
      //"a delivery that is much too short to be a good length delivery, but without the sharp lift of a bouncer",
      //"a slower ball released from the back of the hand",
      //"a delivery that skids after pitching (i.e. doesn't bounce as high as would be expected), usually at a quicker pace, resulting in a batsman unable to hit the ball cleanly.",
      //"a (usually fast) delivery that is pitched very close to the batsman.",
      //"a delivery that reaches the batsman on the full, i.e. without bouncing",
  };

  String _ball_desc [] = {
      "a fast short pitched delivery that rises up near the batsman's head",
      "a leg spin delivery",
      "a leg spin delivery turning very fast",
      "a fast leg spin delivery",
      "a off spin delivery",
      "a off break delivery turning very fast",
      "a fast off spin delivery",
      "a bump ball, pitching very close to the batsman",
      "a delivery bowled with curves away from the batsman before pitching",
      "a leg spin delivery with under-spin, so it bounces lower than normal",
      "a delivery bowled by a spinner that travels in a highly arched path appearing to 'float' in the air",
      "a delivery bowled with curves into the batsman before pitching",
      "a delivery that curves into the batsman after pitching",
      "a delivery that pitches closer to the batsman than a ball pitching on a good length, but further away than a half-volley",
      "a delivery that bounces just short of the block hole. Usually easy to drive or glance away",
      "a delivery that is much too short to be a good length delivery, but without the sharp lift of a bouncer",
      "a slower ball released from the back of the hand",
      "a delivery that skids after pitching (i.e. doesn't bounce as high as would be expected), usually at a quicker pace, resulting in a batsman unable to hit the ball cleanly.",
      "a (usually fast) delivery that is pitched very close to the batsman.",
      "a delivery that reaches the batsman on the full, i.e. without bouncing",

  };

  String _bestshot_desc [] = {};

  public KeyMap(int type) {
    _keys = new int[1000];
    _time = new long[1000];
    _position = new Point(0,0);
    _type=type;
  }

  public void addKey(int kc){
    //_cat.finest("Adding key = " + kc);
    _time[_count]=System.currentTimeMillis();
    _keys[_count++]=kc;
  }

  public int size(){
    return _count;
  }

  public int key(int i){
    return i >= _count ? -1 : _keys[i];
  }

  public void reset(){
    _position = new Point(0,0);
    _count=0;
  }

  public void setPosition(Point p){
    _position = p;
  }

  public boolean validate(){
    boolean validate=false;
    for (int i=1;i<_shot_keys.length;i++){
      int km1[] = _shot_keys[i];
      for (int j=i+1;j<_shot_keys.length;j++){
        int km2[] = _shot_keys[j];
        for (int k=0; k<km1.length;k++){
          System.out.println(km1[k] + " Comp " + km2[k]);
          if (km1[k]!=km2[k]){
            validate = true;
            break;
          }
        }
        if (!validate){
          System.out.println(_shot_name[i] + "=" + _shot_name[j]);
          return validate;
        }
      }
    }
    return validate;
  }

  public int getSpin(){
    int spin = -dirX()/10;
    spin = spin < -3 ? -3 : spin;
    spin = spin > 3 ? 3 : spin;
    return spin;
  }

  public int dirX(){
    int d=0;
    for (int i=0;i<_count;i++){
      if (_keys[i]==L){
        d--;
      }
      else if (_keys[i]==R){
        d++;
      }
    }
    return d;
  }

  public int dirY(){
    int d=0;
    for (int i=0;i<_count;i++){
      if (_keys[i]==U){
        d--;
      }
      else if (_keys[i]==D){
        d++;
      }
    }
    return d;
  }

  public int x(){
    return _position.x;
  }

  public int y(){
    return _position.y;
  }

  public int averageInterval(){
    _cat.finest(this.toString());
    int ai=0;
    for (int i=0;i<_count-1;i++){
      ai += ( _time[_count + 1] - _time[_count]);
    }
    return _count > 1 ? ai/_count : 100;
  }

  public String toString(){
    StringBuffer sbuf = new StringBuffer("KM=");
    for (int i=0;i<_count;i++){
      sbuf.append(_keys[i]).append(", ");
      sbuf.append(_time[i]).append(", ");
    }
    sbuf.append("[").append(_position.x).append(", ").append(_position.y).append("]");
    return sbuf.toString();
  }

  public static void main(String args[]){
    KeyMap km = new KeyMap(BATS);
    System.out.println(km.validate());
  }


}
