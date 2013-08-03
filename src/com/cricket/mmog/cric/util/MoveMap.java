package com.cricket.mmog.cric.util;

import com.criconline.anim.AnimationConstants;

public class MoveMap implements AnimationConstants
{
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
  } ;

  public MoveMap() {
  }

  public synchronized static int getFielder(double rad){
    for (int i=0;i<_map.length;i++){
      double min_rad = _map[i][0];
      //System.out.println(min_rad);
      if (min_rad < rad)continue;
      if (i==0){
        return (int) _map[_map.length - 1][5];
      }else {
        return (int) _map[i - 1][5];
      }
    }
    return NONE;
  }

  public synchronized static int getShot(double rad){
    for (int i=0;i<_map.length;i++){
      double min_rad = _map[i][0];
      //System.out.println(min_rad);
      if (min_rad < rad)continue;
      return (int)_map[i-1][2];
    }
    return NONE;
  }

  public synchronized static int getFielderMove(double rad, int distance){
     for (int i=0;i<_map.length;i++){
       double min_rad = _map[i][0];
       //System.out.println(min_rad);
       if (min_rad < rad)continue;
       int st_range = (int)_map[i-1][3];
       int ed_range = (int)_map[i-1][4];
       if (distance > st_range && distance < ed_range){
         return (int)_map[i-1][6];
       }
     }
     return NONE;
   }


  public static void main(String args[]){
    //System.out.println(getFielder(1.1));
    //System.out.println(getShot(1.1));
    for (int i=0;i<100;i++){
      System.out.println( (int) ( (4) * Math.random()) + 3);
    }
  }


}
