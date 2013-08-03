package com.cricket.mmog.cric.util;


public class Matrix {
  double _m[][];

  public Matrix() {
    _m = new double[4][4];
  }

  /**
   * The two parameters define the transformed coordinate system
   * with origin at first coordinate and x-axis pointing in
   * the direction of the other coordinate
   *
   */

  public Matrix(Coordinate c1, Coordinate c2) {
    _m = new double[4][4];
    double height = (c2._y - c1._y);
    double width = (c2._x - c1._x);
    double hypotenuse = Math.sqrt( (height * height) + (width * width));
    double cosTheta = width / hypotenuse;
    double sinTheta = height / hypotenuse;
    //System.out.println("cosTheta=" + cosTheta + "sinTheta=" + sinTheta);

    _m[0][0] = cosTheta;
    _m[0][1] = -1 * sinTheta;
    _m[0][2] = 0;
    _m[0][3] = c1._x;
    _m[1][0] = sinTheta;
    _m[1][1] = cosTheta;
    _m[1][2] = 0;
    _m[1][3] = c1._y;
    _m[2][0] = 0;
    _m[2][1] = 0;
    _m[2][2] = 1;
    _m[2][3] = c1._z;
    _m[3][0] = 0;
    _m[3][1] = 0;
    _m[3][2] = 0;
    _m[3][3] = 1;
  }

  /**
   * This defines the angle of the screen with the ground
   *
   */
  public void setCameraAngle(double degrees) {
    double radian = (degrees) / 360;
    double cosPhi = Math.cos(radian);
    double sinPhi = Math.sin(radian);
    // add this transformation
    double cosTheta = _m[0][0];
    double sinTheta = _m[1][0];
    _m[1][0] = sinTheta * cosPhi;
    _m[1][1] = cosTheta * cosPhi;
    //System.out.println("cosTheta * cosPhi=" + cosTheta * cosPhi);
    _m[1][2] = -sinPhi;
    _m[2][0] = sinTheta * sinPhi;
    _m[2][1] = cosTheta * sinPhi;
    _m[2][2] = cosPhi;
  }

  public Coordinate transform(Coordinate c) {
    double x, y, z;
    x = (c._x * _m[0][0]) + (c._y * _m[0][1]) + (c._z * _m[0][2]) + _m[0][3];
    y = (c._x * _m[1][0]) + (c._y * _m[1][1]) + (c._z * _m[1][2]) + _m[1][3];
    //System.out.println("MAtrixy=" + c._y );
    //System.out.println("a=" + (c._x * _m[1][0])  +", b=" + (c._y * _m[1][1]) +" ,c="+ (c._z * _m[1][2] + _m[1][3]));
    z = (c._x * _m[2][0]) + (c._y * _m[2][1]) + (c._z * _m[2][2]) + _m[2][3];
    return new Coordinate(x, y, z);
  }

  public String toString() {
    String str = "";
    for (int i = 0; i < 4; i++) {
      str += "|" + _m[i][0] + ", " + _m[i][1] + ", " + _m[i][2] + ", " +
          _m[i][3] + " " + "|\n";
    }
    return str;
  }

  public static void main(String args[]) {
    Matrix m = new Matrix(new Coordinate(200, 150, 0),
                          new Coordinate(400, 300, 0));
    System.out.println(m);
    Coordinate c = new Coordinate(50, 0, 0);
    System.out.println(c);
    Coordinate t_c = m.transform(c);
    System.out.println(t_c);

    System.out.println("-------------------\n\n");

    m = new Matrix(new Coordinate(200, 0, 0), new Coordinate(800, 0, 0));
    System.out.println(m);
    c = new Coordinate(40, 0, 0);
    System.out.println(c);
    t_c = m.transform(c);
    System.out.println(t_c);

    System.out.println("-------------------\n\n");

    m = new Matrix(new Coordinate(0, -100, 0), new Coordinate(0, 600, 0));
    System.out.println(m);
    c = new Coordinate(40, 0, 0);
    System.out.println(c);
    t_c = m.transform(c);
    System.out.println(t_c);

    System.out.println("-------------------\n\n");

    m = new Matrix(new Coordinate(0, 0, 0), new Coordinate(0, 20, 0));
    System.out.println(m);
    c = new Coordinate(20, 0, 0);
    System.out.println(c);
    t_c = m.transform(c);
    System.out.println(t_c);

    System.out.println("-------------------\n\n");

  }

}
