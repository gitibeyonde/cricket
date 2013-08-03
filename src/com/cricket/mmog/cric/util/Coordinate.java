package com.cricket.mmog.cric.util;

import java.util.*;
import com.agneya.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Coordinate {
  public double _x, _y, _z;
  public Coordinate(double x, double y, double z) {
    _x = x;
    _y = y;
    _z = z;
  }

  public double distance(Coordinate c) {
    return Math.sqrt( (Math.pow(c._x - _x, 2)) + Math.pow(c._y - _y, 2) +
                     Math.pow(c._z - _z, 2));
  }

  public static void printCoordinates(Coordinate[] c) {
    Arrays.sort(c, new Comparator() {
      public int compare(Object o1, Object o2) {
        return (int) ( ( (Coordinate) o2)._y - ( (Coordinate) o1)._y);
      }
    });

    for (int i = 0; i < c.length; i++) {
      //System.out.print(c[i]);
    }
    for (int i = 0; i < c.length; i++) {
      for (int j = 0; j < (int) c[i]._x; j++) {
        System.out.print(" ");
      }
      System.out.print("*");
      while (i < c.length - 1 && (int) c[i]._y == (int) c[i + 1]._y) {
        if ( (int) c[i]._x != (int) c[i + 1]._x) {
          System.out.print(" * ");
        }
        i++;
      }
      System.out.println("");
    }
  }

  public boolean equals(Coordinate c){
    return c._x == _x && c._y == _y && c._z == _z;
  }

  public String toString() {
    return "(" + Utils.getRoundedString(_x) + "," + Utils.getRoundedString(_y) +
        "," + Utils.getRoundedString(_z) + ")";
  }

  public static void main(String args[]) {
    Coordinate c = new Coordinate(0, 0, 0);
    Coordinate c1 = new Coordinate(30, 40, 0);
    System.out.println(c.distance(c1));

  }

}
