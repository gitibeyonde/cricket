package com.cricket.mmog.cric.util;

import java.util.*;

public class TrajectoryRH {
  int _theta;
  double _pitch;
  int _type;
  MatrixRH _m;
  LineRH _l;
  double _distance;
  Coordinate _sp, _ep;
  Coordinate _ep1, _ep2;
  int _height = 5;
  MoveParams _mp;

  int HIT = 1;
  int BOWL = 2;

  public TrajectoryRH(Coordinate c1, Coordinate c2) {
    //System.out.println(c1 + "  " + c2);
    _sp = c1;
    _ep = c2;
    _pitch = c1.distance(c2);
    _type = HIT;
    _m = new MatrixRH(c1, c2);
    _l = new LineRH(c1, c2);
    _distance = c1.distance(c2) * 2;
    //System.out.println("Distance based on pitch point = " + _distance);
  }

  public void setBowl(MoveParams mp) {
    _type = BOWL;
    _mp = mp;
  }

  public void setHit(MoveParams mp) {
    _type = HIT;
    _mp = mp;
  }

  public void setCameraAngle(double degrees) {
    _m.setCameraAngle(degrees);
  }

  public void setBoundary(Coordinate c1, Coordinate c2) {
    Coordinate[] ep = _l.findIntercepts2(c1, c2);
    if (ep[0].distance(_sp) <= ep[1].distance(_sp)) {
      _ep1 = ep[0];
      _ep2 = ep[1];
    }
    else {
      _ep1 = ep[1];
      _ep2 = ep[0];
    }
    _distance = _sp.distance(_ep2) < _distance ? _sp.distance(_ep2) : _distance;
    //System.out.println("SP=" + _sp + ", Distance=" + _distance + ", EP=" + _ep2);
  }

  public Coordinate[][] path() {
    Vector c = new Vector();
    Vector sc = new Vector();
    Coordinate retval[][] = new Coordinate[2][];
    double origin = 0;
    double dist = _pitch;
    //System.out.println(dist);
    double x_s = 0;
    double x = 0;
    int _bowl_state = 0;
    int size = (int) _distance;
    double y = 0; // for spin
    int incr=4;
    if (_type == HIT) {
      _height = 4;
      incr=5;
    }
    else {
      _height = 4;
      incr=4;
    }
    //System.out.println("size=" + size );
    for (int i = 0; i < size; i+=incr, x+=incr) {
      x_s = x - origin;
      double z = (x_s - (x_s * x_s) / dist) * _height;
      if (y != 0) {
        y -= 0.6 * _mp._spin;
      }
      //System.out.println("size=" + size + ", Y=" + y);
      if (z <= 0 && _bowl_state < 4) {
        //System.out.println("typ=" + _type + " bs=" + _bowl_state);
        if (_type == BOWL && _bowl_state == 1) {
          //change matrix
          y = 1;
          _height =4;
          //System.out.println("Change Matrix=" + _m);
        }
        _bowl_state += 1;
        dist = 0.8 * dist;
        origin = x;
        x_s = x - origin;
        if (_bowl_state >= 3) {
          _height = 0;
        }
        z = x_s - (x_s * x_s) / dist;
        if (incr>1){
          incr--;
        }
      }
      c.add(_m.transform(new Coordinate(x, y, z)));
      sc.add(_m.transform(new Coordinate(x, y, 0)));
      //System.out.println("---" + new Coordinate(x, y, z) + i);
    }
    retval[0] = (Coordinate[]) c.toArray(new Coordinate[c.size()]);
    retval[1] = (Coordinate[]) sc.toArray(new Coordinate[sc.size()]);
    return retval;
  }

  public static void main(String args[]) {
    TrajectoryRH t = new TrajectoryRH(new Coordinate(0, 0, 0),
                                  new Coordinate(100, 50, 0));

    Coordinate[][] v = t.path();

    for (int i = 0; i < v[0].length; i++) {
    System.out.println(v[0][i] + "==s==" + v[1][i]);
    }

  }

}
