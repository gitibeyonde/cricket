package com.cricket.mmog.cric.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Test {
  public Test() {
  }

  public static void main(String args[]) {

    Coordinate start_bowl = new Coordinate(5, 5, 0);
    Coordinate pitch_bowl = new Coordinate(50, 50, 0);
    Trajectory t = new Trajectory(start_bowl, pitch_bowl);
    t.setBoundary(new Coordinate(0, 0, 0),
                  new Coordinate(80, 80, 0));
    t.setBowl(new MoveParams(MoveParams.BOWL));
    t.setCameraAngle(45);
    Coordinate[][] v = t.path();
    System.out.println("Path--------------" + v.length);
   // for (int i = 0; i < v.length; i++) {
    //  System.out.println(v[i]);
    //}
    //Coordinate.printCoordinates(v);
    /**
         int q1=0, q2=0, q3=0, q4=0;

         for (int i = 0; i < 100000; i++) {
      int pitch_distance = (int) (Math.random() * 10);
      double direction = (Math.random() * 2 * Math.PI);
      int pitch_x = (int) (Math.sin(direction) * pitch_distance);
      int pitch_y = (int) (Math.cos(direction) * pitch_distance);
      if (pitch_x>0 && pitch_y>0){
        q1++;
      }
      else if (pitch_x>0 && pitch_y < 0){
        q2++;
      }
      else if (pitch_x<0 && pitch_y < 0){
        q3++;
      }
      else if (pitch_x<0 && pitch_y > 0){
        q4++;
      }


      System.out.println("Dist=" + pitch_distance + ", Direction=" +
                         direction +
                         ", x=" + pitch_x + ", y=" + pitch_y);
         }

         System.out.println(q1 + ", " + q2 + ", " + q3 + ", "+ q4);
     **/
  }

}
