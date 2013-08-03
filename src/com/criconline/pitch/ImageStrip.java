package com.criconline.pitch;

import javax.swing.ImageIcon;
import java.awt.Point;
import com.criconline.Utils;
import java.util.Hashtable;

public class ImageStrip {
  public String _name;
  public String _sub_category;
  public ImageIcon _strip;
  public int _frames;
  public int _width;
  public int _height;
  public int _type;
  public int _delay;
  public int _hook;
  public int _repeat_count;
  public boolean _animate;
  public Point _start;
  public Point _end;
  public int _deltaX;
  public int _deltaY;
  public int _zorder;


  public ImageStrip(String name, String sn, int wd, int ht, int fr, int type, Point start, Point end, int dx, int dy, int delay, int hook, int repeat_count, boolean animate, int zorder) {
    _name = name;
    _sub_category = sn;
    _width = wd;
    _height = ht;
    _start = start;
    _type = type;
    _frames = fr;
    _end = end;
    _deltaX=dx;
    _deltaY=dy;
    _delay = delay;
    _hook = hook;
    _repeat_count = repeat_count;
    _animate = animate;
    _zorder = zorder;
    StringBuffer sb = new StringBuffer("strips/");
    sb.append(_name).append("_").append(_sub_category).append(".png");
    //System.out.println("ImageStrip=" + sb.toString());
    _strip = Utils.getIcon(sb.toString());
  }

  public ImageStrip(String image_name, int wd, int ht, int fr, int type, Point start, Point end, int dx, int dy, int delay, int hook, int repeat_count, boolean animate, int zorder) {
      _name = image_name;
      _sub_category = null;
      _width = wd;
      _height = ht;
      _start = start;
      _type = type;
      _frames = fr;
      _end = end;
      _deltaX=dx;
      _deltaY=dy;
      _delay = delay;
      _hook = hook;
      _repeat_count = repeat_count;
      _animate = animate;
      _zorder = zorder;
      StringBuffer sb = new StringBuffer("images/");
      sb.append(_name).append(".png");
      //System.out.println("ImageStrip=" + sb.toString());
      _strip = Utils.getIcon(sb.toString());
  }
}
