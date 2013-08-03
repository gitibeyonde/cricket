package com.criconline.pitch;

import java.util.Hashtable;
import java.awt.Point;
import com.criconline.anim.AnimationConstants;
import com.cricket.mmog.PlayerStatus;
import com.agneya.util.Rng;

public class ImageStripData implements AnimationConstants {
  private String _data[][] = {
                             // name, sub-category, startx, starty, endx, endy,   wdth, hight,     type, delay, hook, repeat count, animtae flag, zorder
                             //OUTLINE
                             {"player", "outline", "0", "840", "0", "840", "0",
                             "0", "30", "44", "1", "once", "200", "40", "1",
                             "0", "10"}
                             ,
                             //BATSMAN
                             {"batsman", "default", "420", "40", "420", "40",
                             "0", "0", "130", "200", "5", "repeat", "40", "40",
                             "1", "1", "5"}
                             ,
                             //
                             //{"batsman", "wait", "510", "140", "510", "140",
                            // "0", "0", "33", "63", "30", "once", "20", "40",
                            // "1", "1", "5"}
                             //,
                             //
                              {"batsman", "ready", "440", "70", "440", "70",
                              "0", "0", "130", "160", "10", "repeat", "40", "40",
                              "1", "1", "5"}
                              ,
                             //
                             {"batsman", "defence", "420", "40", "420", "40",
                             "0", "0", "180", "200", "25", "once", "4", "40",
                             "1", "1", "5"}
                             ,
                             //
                             {"batsman", "topdefence", "400", "10", "400", "10",
                             "0", "0", "184", "250", "25", "once", "4", "40",
                             "1", "1", "5"}
                             ,
                             //
                             {"batsman", "drive", "415", "40", "415", "40", "0",
                             "0", "180", "200", "25", "once", "3", "40", "1",
                             "1", "5"}
                             ,
                             //
                             {"batsman", "latehit", "420", "40", "420", "40",
                             "0", "0", "180", "200", "25", "once", "4", "40",
                             "1", "1", "5"}
                             ,
                             //
                             {"batsman", "earlyhit", "420", "40", "420", "40",
                             "0", "0", "180", "200", "25", "once", "4", "40",
                             "1", "1", "5"}
                             ,
                             //
                             {"batsman", "leg", "420", "40", "420", "40", "0",
                             "0", "184", "200", "19", "once", "4", "40", "1",
                             "1", "5"}
                             ,
                             //
                             {"batsman", "cut", "395", "40", "395", "40", "0",
                             "0", "180", "200", "25", "once", "6", "40", "1",
                             "1", "5"}
                             ,
                             //
                             {"batsman", "hook", "400", "25", "400", "25", "0",
                             "0", "184", "200", "25", "once", "4", "40", "1",
                             "1", "5"}
                             ,
                             //
                             {"batsman", "miss", "415", "60", "415", "60", "0",
                             "0", "180", "200", "25", "once", "4", "40", "1",
                             "1", "5"}
                             ,
                             //
                             {"batsman", "wide", "460", "40", "450", "40", "0",
                             "0", "185", "200", "30", "once", "4", "30", "1",
                             "1", "5"}
                             ,
                             //
                             /**{"batsman", "run", "420", "30", "420", "30",
                             "0", "0", "104", "155", "31", "repeat", "2", "40",
                                                        "1", "1", "5"},**/
                             //RUNNER
                             {"runner", "default", "350", "320", "350", "320",
                             "0", "0", "130", "160", "10", "repeat", "100", "40",
                             "1", "1", "1"}
                             ,
                             //
                             //{"runner", "run", "455", "350", "455", "350", "0",
                             //"0", "55", "150", "31", "repeat", "2", "40", "1",
                             //                             "1", "1"},
                             //BOWLER
                             {"bowler", "default", "530", "480", "530", "480",
                             "0", "0", "72", "192", "8", "repeat", "80", "40",
                             "1", "1", "5"}
                             ,

                             {"bowler", "runup", "520", "440", "500", "240",
                             "0", "0", "72", "192", "55", "once", "8", "40",
                             "1", "1", "5"}
                             ,

                             {"bowler", "block", "410", "370", "410", "370",
                             "0", "0", "104", "192", "17", "once", "8", "40",
                             "1", "1", "5"}
                             ,
                             // FIELDER COMMON
                             /**{"fielder", "marker", "210", "200", "210", "200",
                             "0", "0", "58", "32", "1", "once", "4000", "40",
                                                        "1", "1", "5"}, **/
                             //
                             {"fielder", "up", "210", "230", "210", "230", "0",
                             "0", "55", "150", "31", "repeat", "2", "40", "1",
                             "1", "5"}
                             , {"fielder", "down", "210", "230", "210", "230",
                             "0", "0", "104", "155", "31", "repeat", "2", "40",
                             "1", "1", "5"}
                             , {"fielder", "right", "210", "230", "210", "230",
                             "0", "0", "109", "160", "31", "repeat", "2", "40",
                             "1", "1", "5"}
                             , {"fielder", "left", "210", "230", "210", "230",
                             "0", "0", "109", "160", "31", "repeat", "2", "40",
                             "1", "1", "5"}
                             ,
                             // SLIP FIELDER
                             // OFF FIELDER
                             {"offfielder", "default", "210", "200", "210",
                             "200", "0", "0", "86", "166", "7", "repeat", "400",
                             "40", "1", "1", "5"}
                             , {"offfielder", "throw", "210", "200", "210",
                             "200", "0", "0", "100", "166", "20", "once", "10",
                             "40", "1", "1", "5"}
                             , {"offfielder", "catch", "210", "200", "210",
                             "200", "0", "0", "102", "176", "22", "once", "10",
                             "40", "1", "1", "5"}
                             ,
                             // ON FIELDER
                             {"onfielder", "throw", "760", "250", "760", "250",
                             "0", "0", "100", "166", "19", "once", "8", "40",
                             "1", "1", "5"}
                             , {"onfielder", "default", "760", "250", "760",
                             "250", "0", "0", "104", "166", "7", "repeat",
                             "400", "40", "1", "1", "5"}
                             , {"onfielder", "throw", "760", "250", "760",
                             "250", "0", "0", "100", "166", "19", "once", "8",
                             "40", "1", "1", "5"}
                             ,
                             /** // SLIP
                             {"slip", "throw", "760", "250", "760", "250", "0",
                             "0", "100", "166", "19", "once", "8", "40", "1",
                             "1"}, {"slip", "default", "760", "250", "760",
                             "250", "0", "0", "52", "149", "5", "repeat",
                             "400", "40", "1", "1"},

                                                        // LEG
                             {"leg", "throw", "760", "250", "760", "250", "0",
                             "0", "100", "166", "19", "once", "8", "40", "1",
                             "1"}, {"leg", "default", "760", "250", "760",
                             "250", "0", "0", "52", "166", "7", "repeat",
                             "400", "40", "1", "1"}, **/

                             //WICKET KEEPER
                             {"wk", "default", "450", "0", "450", "0", "0", "0",
                             "80", "174", "30", "repeat", "50", "40", "1", "1",
                             "3"}
                             , {"wk", "ready", "450", "0", "450", "0", "0", "0",
                             "80", "174", "4", "repeat", "100", "40", "1", "1",
                             "3"}
                             , {"wk", "block", "450", "0", "450", "0", "0", "0",
                             "91", "187", "11", "once", "10", "40", "1", "1",
                             "3"}
                             , {"wk", "catch", "450", "-25", "450", "-25", "0",
                             "-25", "91", "187", "20", "once", "4", "40", "1",
                             "1", "3"}
                             ,
                             //UMPIRE
                             {"umpire", "default", "470", "450", "470", "450",
                             "0", "0", "55", "134", "5", "repeat", "100", "40",
                             "1", "1", "6"}
                             ,
                             //timer
                             {"meter", "timer", "0", "0", "0", "0", "0", "0",
                             "80", "110", "30", "once", "6", "40", "1", "1",
                             "10"}
                             ,
                             //BALL PITCH
                             {"ball", "pitch", "500", "230", "500", "230", "0",
                             "0", "21", "12", "7", "repeat", "100", "40", "1",
                             "1", "10"}
                             ,
                             //BALL shadow
                             {"ball", "shadow", "500", "230", "500", "230", "0",
                             "0", "10", "10", "1", "once", "1000", "40", "1",
                             "1", "10"}
                             ,
                             //TOSS
                             {"toss", "coin", "200", "200", "200", "200", "0",
                             "0", "132", "131", "4", "once", "30", "40", "1",
                             "1", "10"}
                             ,
                             //BUBLES
                             {"speaks", "bubbles", "420", "400", "420", "400",
                             "0", "0", "80", "38", "2", "repeat", "500", "40",
                             "1", "1", "10"}
                             ,
                             //STUMPS ONE
                             {"stumps", "one", "495", "130", "495", "130", "0",
                             "0", "17", "37", "1", "repeat", "500", "40", "1",
                             "1", "4"}
                             ,
                             //STUMPS TWO
                             {"stumps", "two", "495", "440", "495", "440", "0",
                             "0", "17", "37", "1", "repeat", "500", "40", "1",
                             "1", "4"}
                             ,
                             //STUMPS FALL
                             {"stumps", "fall", "488", "118", "488", "118", "0",
                             "0", "30", "44", "7", "once", "20", "40", "1", "1",
                             "4"}
                             ,

                             /**  //ARROWS
                             {"arrow", "up", "390", "370", "390", "370", "0",
                             "0", "12", "34", "1", "repeat", "500", "40", "1",
                             "1"}, {"arrow", "right", "390", "370", "390",
                             "370", "0", "0", "34", "12", "1", "repeat", "500",
                             "40", "1", "1"}, {"arrow", "down", "390", "370",
                             "390", "370", "0", "0", "12", "34", "1", "repeat",
                             "500", "40", "1", "1"}, {"arrow", "left", "390",
                             "370", "390", "370", "0", "0", "34", "12", "1",
                             "repeat", "500", "40", "1", "1"}, {"arrow", "all",
                             "420", "500", "420", "500", "0", "0", "65", "44",
                             "1", "repeat", "500", "40", "1", "1"}, {"arrow",
                             "allxs", "420", "500", "420", "500", "0", "0",
                             "98", "43", "1", "repeat", "500", "40", "1", "1"}, **/


  };

  private static Hashtable _registry = new Hashtable();
  private static Object _dummy = new Object();
  private static ImageStripData _is = null;
  private static Rng _rng = new Rng();

  public static ImageStripData instance() {
    if (_is == null) {
      synchronized (_dummy) {
        if (_is == null) {
          _is = new ImageStripData();
        }
      }
    }
    return _is;
  }

  private ImageStripData() {
    for (int i = 0; i < _data.length; i++) {
      String _name;
      String _sub_category;
      Point _start;
      Point _end;
      int _dx, _dy;
      int _frames;
      int _width;
      int _height;
      int _type = ONCE;
      int _delay;
      int _hook;
      int _repeat_count;
      boolean _animate;
      int _zorder;
      _name = _data[i][0];
      _sub_category = _data[i][1];
      _start = new Point(Integer.parseInt(_data[i][2]),
                         Integer.parseInt(_data[i][3]));
      _end = new Point(Integer.parseInt(_data[i][4]),
                       Integer.parseInt(_data[i][5]));
      _dx = Integer.parseInt(_data[i][6]);
      _dy = Integer.parseInt(_data[i][7]);
      _width = Integer.parseInt(_data[i][8]);
      _height = Integer.parseInt(_data[i][9]);
      _frames = Integer.parseInt(_data[i][10]);
      if (_data[i][11].equals("once")) {
        _type = ONCE;
      }
      else if (_data[i][11].equals("repeat")) {
        _type = CYCLIC;
      }
      _delay = Integer.parseInt(_data[i][12]);
      _hook = Integer.parseInt(_data[i][13]);
      _repeat_count = Integer.parseInt(_data[i][14]);
      _animate = Integer.parseInt(_data[i][15]) == 1 ? true : false;
      _zorder = Integer.parseInt(_data[i][16]);

      ImageStrip is = new ImageStrip(_name, _sub_category, _width, _height,
                                     _frames, _type, _start, _end, _dx, _dy,
                                     _delay, _hook, _repeat_count, _animate,
                                     _zorder);
      _registry.put(_name + _sub_category, is);
    }
  }

  public static ImageStrip getImageStrip(String name, String category) {
    return (ImageStrip) _registry.get(name + category);
  }

  public static ImageStrip getDefaultImageStrip(PlayerStatus ps, int ts,
                                                int num) {
    if (ps.isPreJoin()) {
      return getOutline(ts, num);
    }
    else if (ps.isWaiting()) {
      return getPlaceHolder(ts, num);
    }
    else if (ps.isWicketKeeper()) {
      return getImageStrip("wk", "default");
    }
    else {
      return getActionImageStrip(ps, "default");
    }
  }

  public static ImageStrip getStumps(int i) {
    if (i == 0) {
      return getImageStrip("stumps", "one");
    }
    else {
      return getImageStrip("stumps", "two");
    }
  }

  public static ImageStrip getArrow(String type) {
    return getImageStrip("arrow", type);
  }


  public static ImageStrip getUmpire() {
    return getImageStrip("umpire", "default");
  }

  public static ImageStrip getFielderMarker() {
    return getImageStrip("fielder", "marker");
  }

  public static ImageStrip getPitchPoint() {
    return getImageStrip("ball", "pitch");
  }

  public static ImageStrip getTossCoin() {
    return getImageStrip("toss", "coin");
  }

  public static ImageStrip getStumpsFall() {
    return getImageStrip("stumps", "fall");
  }

  public static ImageStrip getSpeakBubbles() {
    return getImageStrip("speaks", "bubbles");
  }

  public static ImageStrip getBallShadow() {
    return getImageStrip("ball", "shadow");
  }

  public static ImageStrip getActionImageStrip(PlayerStatus ps, String action) {
    if (ps.isBatsman()) {
      return getImageStrip("batsman", action);
    }
    /**else if (ps.isRunner()) {
      return getImageStrip("runner", action);
         }**/
    else if (ps.isBowler()) {
      if (action.equals("up") || action.equals("down") || action.equals("right") ||
          action.equals("left")) {
        return getImageStrip("fielder", action);
      }
      else if (action.equals("throw") || action.equals("catch")) {
        return getImageStrip("bowler", "block");
      }
      else {
        return getImageStrip("bowler", action);
      }
    }
    else if (ps.isWicketKeeper()) {
      return getImageStrip("wk", action);
    }
    else if (ps.isFielder()) {
      if (action.equals("up") || action.equals("down") || action.equals("right") ||
          action.equals("left")) {
        return getImageStrip("fielder", action);
      }
      else if (action.equals("throw") || action.equals("default") ||
               action.equals("catch")) {

        if (ps.isLF1()) {
          ImageStrip lf1 = getImageStrip("offfielder", action);
          lf1._start = new Point(160, 200);
          lf1._end = new Point(160, 200);
          return lf1;
        }
        else if (ps.isLF2()) {
          ImageStrip lf2 = getImageStrip("offfielder", action);
          lf2._start = new Point(50, 500);
          lf2._end = new Point(50, 500);
          return lf2;
        }
        else if (ps.isLF3()) {
          ImageStrip lf3 = getImageStrip("offfielder", action);
          lf3._start = new Point(150, 650);
          lf3._end = new Point(150, 650);
          return lf3;
        }
        else if (ps.isLF4()) {
          ImageStrip lf4 = getImageStrip("slip", action);
          lf4._start = new Point(200, -70);
          lf4._end = new Point(200, -70);
          return lf4;
        }
        else if (ps.isLF5()) {
          ImageStrip lf5 = getImageStrip("slip", action);
          lf5._start = new Point(400, -250);
          lf5._end = new Point(400, -250);
          return lf5;
        }
        else if (ps.isRF1()) {
          ImageStrip rf1 = getImageStrip("onfielder", action);
          rf1._start = new Point(800, 200);
          rf1._end = new Point(800, 200);
          return rf1;
        }
        else if (ps.isRF2()) {
          ImageStrip rf2 = getImageStrip("onfielder", action);
          rf2._start = new Point(900, 500);
          rf2._end = new Point(900, 500);
          return rf2;
        }
        else if (ps.isRF3()) {
          ImageStrip rf3 = getImageStrip("onfielder", action);
          rf3._start = new Point(800, 650);
          rf3._end = new Point(800, 650);
          return rf3;
        }
        else if (ps.isRF4()) {
          ImageStrip rf4 = getImageStrip("leg", action);
          rf4._start = new Point(760, -50);
          rf4._end = new Point(760, -50);
          return rf4;
        }
        else if (ps.isRF5()) {
          ImageStrip rf5 = getImageStrip("leg", action);
          rf5._start = new Point(1100, -100);
          rf5._end = new Point(1100, -100);
          return rf5;
        }
        else {
          // throw new IllegalStateException("Unknown player status " + ps);
        }
      }
      else {
        //throw new IllegalStateException("Unknown player status " + ps);
      }
    }
    else if (ps.isRunner()) {
      ImageStrip runner = getImageStrip("runner", action);
      return runner;
    }

    throw new IllegalStateException("Unknown player status " + ps.longVal());
  }

  public static ImageStrip getOutline(int team_size, int num) {
    ImageStrip po = (ImageStrip) _registry.get("playeroutline");
    // calculate position
    if (num < team_size) {
      // team A
      po._start = new Point(35 * num, 480);
      po._end = new Point(po._start);
    }
    else if (num >= team_size) {
      int new_num = num - team_size;
      po._start = new Point(960 - (35 * new_num), 480);
      po._end = new Point(po._start);
    }
    return po;
  }

  public static ImageStrip getPlaceHolder(int team_size, int num) {
    ImageStrip po = null;
    // calculate position
    if (num < team_size) {
      // team A
      po = new ImageStrip("p" + (num + 1), 43, 47, 0, 0,
                          new Point(0 + 43 * num, 650),
                          new Point(0 + 40 * num, 650), 0, 0, 1000, 0, 0, false,
                          10);
    }
    else if (num >= team_size) {
      int new_num = num - team_size + 1;
      po = new ImageStrip("p" + (new_num), 43, 47, 0, 0,
                          new Point(1000 - (43 * new_num), 650),
                          new Point(1000 - (43 * new_num), 650), 0, 0, 1000, 0,
                          0, false, 10);
    }
    return po;
  }


  public static ImageStrip getGraphBar() {
    return new ImageStrip("bar", 137, 8, 11, 0, new Point(0, 0), new Point(0, 0),
                          0, 0, 1000, 0, 0, false, 10);
  }

}
