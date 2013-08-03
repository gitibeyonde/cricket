package com.cricket.mmog.cric.util;

public class Line {
  //y=_m*x + _c
  double _m;
  double _c;
  Coordinate _c1, _c2;

  public Line(Coordinate c1, Coordinate c2) {
    //System.out.println(c1 + ", " + c2);
    _c1 = c1;
    _c2 = c2;
    _m = (c2._y - c1._y) / (c2._x - c1._x);
    if (!Double.isInfinite(_m)) {
      _c = c2._y - _m * c2._x;
    }
    else {
      _c = -c1._x;
    }
    //System.out.println("M=" + _m + ", " + "C=" + _c);
  }

// TO DELETE
  public Coordinate[] findIntercepts(Coordinate c1, Coordinate c2) {
    Coordinate[] ci = new Coordinate[2];
    int i = 0;
    // c1 x
    double x, y;
    x = c1._x;
    y = _m * x + _c;
    if (!Double.isInfinite(y) && between(x, c1._x, c2._x) &&
        between(y, c1._y, c2._y)) {
      ci[i] = new Coordinate(x, y, 0);
      //System.out.println("One Between c1 and c2 x=" + ci[i]);
      i++;
    }
    //c1 y
    y = c1._y;
    if (Double.isInfinite(_m)) {
      x = -_c;
    }
    else {
      x = (y - _c) / _m;
    }
    if (between(x, c1._x, c2._x) && between(y, c1._y, c2._y)) {
      ci[i] = new Coordinate(x, y, 0);
      //System.out.println("Two Between c1 and c2 y=" + ci[i]);
      i++;
    }

    if (ci[1] != null && ci[0].equals(ci[1])) {
      i--;
    }
    // c2 x
    x = c2._x;
    y = _m * x + _c;
    if (!Double.isInfinite(y) && between(x, c1._x, c2._x) &&
        between(y, c1._y, c2._y)) {
      ci[i] = new Coordinate(x, y, 0);
      //System.out.println("Three Between c1 and c2 y=" + ci[i]);
      i++;
    }
    // c2 y
    y = c2._y;
    if (Double.isInfinite(_m)) {
      x = -_c;
    }
    else {
      x = (y - _c) / _m;
    }

    if (between(x, c1._x, c2._x) && between(y, c1._y, c2._y) && i == 1) {
      ci[i] = new Coordinate(x, y, 0);
      //System.out.println(" Four Between c1 and c2 y=" + ci[i]);
      i++;
    }
    if (ci[0] == null || ci[1] == null) {
      throw new IllegalStateException(ci[0] + ", " + ci[1]);
    }
    return ci;
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


  public int getY(int x) {
    return (int) (_m * x + _c);
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

  public Coordinate closestPoint(Coordinate d) {
    int x, y;
    double m2 = -1 / _m;
    //System.out.println(" Perpendicular = " + m2);
    //System.out.println(" Slope Deg = " + Math.toDegrees(Math.atan(_m)));
    //System.out.println(" Perp Deg = " + Math.toDegrees(Math.atan(m2)));

    if (_m != 0) {
      x = (int) ((double) (d._y - m2 * d._x - _c) / (_m - m2));
    }
    else {
      x = (int) d._x;
    }
    if (m2 < 0.00001) {
      y = (int) d._y;
    }
    else {
      y = (int) (_m * x + _c);
    }
    //System.out.println("ref point=" + d + " Line A="  + _c1);
    //System.out.println("X=" + x + ", Y=" + y);
    return new Coordinate(x, y, 0);
  }

  public static void main(String args[]) {

    Line l = new Line(new Coordinate(400, 500, 0), new Coordinate(200, 200, 0));
    Coordinate[] v = l.findIntercepts(new Coordinate(0, 0, 0),
                                      new Coordinate(800, 600, 0));

    for (int i = 0; i < v.length; i++) {
      System.out.println(v[i]);
    }
    Coordinate[] v2 = l.findIntercepts2(new Coordinate(0, 0, 0),
                                      new Coordinate(800, 600, 0));

    for (int i = 0; i < v2.length; i++) {
      System.out.println(v2[i]);
    }

    System.out.println("--------------\n");

    l = new Line(new Coordinate(400, 100, 0), new Coordinate(400, 500, 0));
    v = l.findIntercepts(new Coordinate(0, 0, 0), new Coordinate(800, 600, 0));
    v2 = l.findIntercepts(new Coordinate(0, 0, 0), new Coordinate(800, 600, 0));

    for (int i = 0; i < v.length; i++) {
      System.out.println(v[i]);
    }
    for (int i = 0; i < v2.length; i++) {
      System.out.println(v2[i]);
    }

  }

}
