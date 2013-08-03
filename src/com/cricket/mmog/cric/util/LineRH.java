package com.cricket.mmog.cric.util;

public class LineRH {
  //y=_m*x + _c
  double _m;
  double _c;
  Coordinate _c1, _c2;

  /***
    THIS IS FOR THE CRICKET GROUND, RIGHT HANDED COORDINATE SYSTEM
   */

  public LineRH(Coordinate c1, double rad) {
    _c1 = new Coordinate(c1._x, c1._y, 0);
    //System.out.println("Slope degrees" + Math.toDegrees(rad));
    _m = Math.tan(rad);
    //System.out.println("Angle in degree=" + Math.toDegrees(Math.atan(_m)));
    _c = c1._x - _m * c1._y;
    //System.out.println("M=" + _m + ", " + "C=" + _c);
  }

  public LineRH(Coordinate c1, Coordinate c2) {
    //System.out.println(c1 + ", " + c2);
    _c1 = c1;
    _c2 = c2;
    _m = (c2._x - c1._x) / (c2._y - c1._y);
    if (!Double.isInfinite(_m)) {
      _c = c2._x - _m * c2._y;
    }
    else {
      _c = -c1._y;
    }
    //System.out.println("M=" + _m + ", " + "C=" + _c);
  }

  public int getY(int x) {
    if (_m > Integer.MAX_VALUE) {
      throw new IllegalStateException("MAX_VALUE");
    }
    else if (_m < Integer.MIN_VALUE) {
      throw new IllegalStateException("MIN_VALUE");
    }
    else {
      return (int) (( x - _c)/_m);
    }
  }


  public Coordinate closestPoint(Coordinate d) {
    int x, y;
    double m2 = -1 / _m;
    //System.out.println(" Perpendicular = " + m2);

    if (_m > Integer.MAX_VALUE || _m < Integer.MIN_VALUE) {
      //System.out.println("Slope is infinity");
      x = (int) _c1._x;
      y = (int) d._y;
    }
    else if (Math.abs(_m) < 0.1) {
      //System.out.println("Slope is almost zero");
      x = (int) d._x;
      y = (int) _c1._y;
    }
    else {
      //System.out.println("Normal slope");
      //(int) ((double) (d._y - m2 * d._x - _c) / (_m - m2));
      y = (int) ((double) (d._x - m2 * d._y - _c) / (_m - m2));
      //(int) (_m * x + _c);
      x = (int) ( _m * y + _c);
    }
    //System.out.println("ref point=" + d + " Line A=" + _c1);
    //System.out.println("X=" + x + ", Y=" + y);

    return new Coordinate(x, y, 0);
  }

  public Coordinate[] findIntercepts2(Coordinate c1, Coordinate c2) {
    Coordinate[] ci = new Coordinate[2];
    //System.out.println("Finding intercepts m=" + _m + ", c=" + _c);
    //System.out.println("c1=" + _c1 + ", c2=" + _c2);
    if (Math.abs(_m) > Integer.MAX_VALUE) {
      // vertical line
      ci[0] = new Coordinate(c1._x, 0, 0);
      ci[1] = new Coordinate(c2._x, 0, 0);
    }
    else if (Math.abs(_m) < 0.0001) {
      // horizontal line
      ci[0] = new Coordinate(0, c1._y, 0);
      ci[1] = new Coordinate(0, c2._y, 0);
    }
    else {
      //slanted line
      // take c1
      Coordinate[] ct1 = new Coordinate[2];
      Coordinate[] ct2 = new Coordinate[2];
      double x, y;
      x = c1._x;
      y = _m * x + _c;
      if (between(x, c1._x, c2._x) && between(y, c1._y, c2._y)) {
        ct1[0] = new Coordinate(x, y, 0);
        //System.out.println("ct10=" + ct1[0]);
      }
      y = c1._y;
      x = (y - _c) / _m;
      if (between(x, c1._x, c2._x) && between(y, c1._y, c2._y)) {
        ct1[1] = new Coordinate(x, y, 0);
        //System.out.println("ct11=" + ct1[1]);
      }

      x = c2._x;
      y = _m * x + _c;
      if (between(x, c1._x, c2._x) && between(y, c1._y, c2._y)) {
        ct2[0] = new Coordinate(x, y, 0);
        //System.out.println("ct20=" + ct2[0]);
      }
      y = c2._y;
      x = (y - _c) / _m;
      if (between(x, c1._x, c2._x) && between(y, c1._y, c2._y)) {
        ct2[1] = new Coordinate(x, y, 0);
        //System.out.println("ct21=" + ct2[1]);
      }

      if (ct1[0]!=null){
        ci[0] = ct1[0];
      }
      else {
        ci[0] = ct1[1];
      }
      if(ct2[0]!=null) {
        ci[1]=ct2[0];
      }
      else {
        ci[1]=ct2[1];
      }

      if (ct1[0]==null && ct1[1]==null){
        ci = ct2;
      }
      else if (ct2[0]==null && ct2[1]==null){
        ci = ct1;
      }
    }

    if (ci[0]==null){
      ci[0] = _c1;
    }
    if (ci[1]==null){
      ci[1] = _c2;
    }

    return ci;
  }


  public boolean between(double x, double x1, double x2) {
    if (x1 < x2) {
      //System.out.println("X1=" + x1 + " X=" + x + " X2=" + x2 + " res=" + (x >= x1 && x <= x2));
      return x >= x1 && x <= x2;
    }
    else {
      //System.out.println("X1=" + x1 + " X=" + x + " X2=" + x2 + " res=" + (x >= x2 && x <= x1));
      return x >= x2 && x <= x1;
    }
  }

  public static void main(String args[]) {
    /**LineRH lp = new LineRH(new Coordinate(200, 200, 0), 0);
    System.out.println(lp.closestPoint(new Coordinate(250, 250, 0)) + "== 250, 200");

    lp = new LineRH(new Coordinate(200, 200, 0), Math.PI);
    System.out.println(lp.closestPoint(new Coordinate(250, 250, 0)) + "== 250, 200");


    LineRH lp = new LineRH(new Coordinate(200, 200, 0), Math.PI/2);
    System.out.println(lp.closestPoint(new Coordinate(250, 250, 0)) + "== 200, 250");

    lp = new LineRH(new Coordinate(200, 200, 0), 3*Math.PI/2);
    System.out.println(lp.closestPoint(new Coordinate(250, 250, 0)) + "== 200, 250");

    lp = new LineRH(new Coordinate(200, 200, 0), Math.PI/4);
    System.out.println(lp.closestPoint(new Coordinate(250, 250, 0)) + "== 250, 250");

    lp = new LineRH(new Coordinate(200, 200, 0), 3*Math.PI/4);
    System.out.println(lp.closestPoint(new Coordinate(250, 250, 0)) + "== 200, 200");**/

    //for (int i = 200; i < 800; i += 20) {
      //System.out.println("X=" + i + ", Y=" + lp.getY(i));
    //}

  }

}
