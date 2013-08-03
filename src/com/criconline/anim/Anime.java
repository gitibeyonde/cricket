package com.criconline.anim;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import com.criconline.Utils;
import java.util.logging.Logger;
import com.criconline.exceptions.AnimationOverException;
import java.util.Vector;
import com.cricket.mmog.cric.util.Coordinate;
import java.io.File;

public class Anime implements AnimationConstants{
  private static Logger _cat = Logger.getLogger(Anime.class.getName());

  protected ImageIcon _strip;
  protected int _zOrder=0;
  protected int _height, _width;
  protected int _count;
  protected int _radius =50;

  /** nummber of current tact. If ==0 then not move */
  public int _currentFrame = 0;
  public int _type = ONCE;
  public int _direction = EAST;

  public Anime(){}

  public Anime(ImageIcon img, int wd, int ht, int cnt) {
    _strip = img;
    _width = wd;
    _height = ht;
    _count = cnt;
  }

  public Anime(String img, int wd, int ht, int cnt) {
    _strip = Utils.getIcon(img);
    _width = wd;
    _height = ht;
    _count = cnt;
  }

  public Anime(String img) {
    _strip = Utils.getIcon(img);
    _width = _strip.getIconHeight();
    _height = _strip.getIconHeight();
    _count = 0;
  }

  public Anime(ImageIcon img) {
    _strip = img;
    _width = _strip.getIconHeight();
    _height = _strip.getIconHeight();
    _count = 0;
  }

  /************************Anime Interface *****************************/
  public int getIconWidth() {
    return _height;
  }

  public int getIconHeight() {
    return _width;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    _strip.paintIcon(c, g, x, y);
  }

  /************************Anime Interface END*****************************/


  public void incrCurrentFrame() throws AnimationOverException {
    if (_count <= 1){
      _currentFrame=0;
    }
    else {
      if (_currentFrame == _count-1) {
        if (_type == CYCLIC) {
          _currentFrame = 0;
        }
        else if (_type == FORWARD_BACKWARD) {
          _currentFrame--;
          _direction = WEST;
        }
        else {
          throw new AnimationOverException("Dir=" + _direction + ", curFrame=" +
                                          _currentFrame + ", cnt=" + _count + ", IS= " + _strip);
        }
      }
      else if (_currentFrame == 0) {
        if (_type == CYCLIC) {
          _currentFrame++;
        }
        else if (_type == FORWARD_BACKWARD) {
          _currentFrame++;
          _direction = EAST;
        }
        else if (_type == ONCE) {
          _currentFrame++;
        }
        else {
          throw new AnimationOverException("Dir=" + _direction + ", curFrame=" +
                                          _currentFrame);
        }
      }
      else {
        if (_direction == EAST) {
          _currentFrame++;
        }
        else if (_direction == WEST) {
          _currentFrame--;
        }
        else {
          throw new AnimationOverException("Dir=" + _direction + ", curFrame=" +
                                          _currentFrame);
        }
      }
    }
    //_cat.finest("CURR FRAME=" + _currentFrame);
  }


  public void decrCurrentFrame() throws AnimationOverException {
    if (_count <= 1){
      _currentFrame=0;
    }
    else if (_currentFrame == 0){
      _currentFrame = _count -1;
    }
    else {
        _currentFrame--;
    }
    //_cat.finest("CURR FRAME=" + _currentFrame);
  }


  public int getCurrentFrame() {
    return _currentFrame;
  }

  public int getRemainingFrame() {
    return _count - _currentFrame;
  }

  public void setType(int type) {
    _type = type;
  }

  public int getCount() {
    return _count;
  }

  public int getRadius(){
    return _radius;
  }

  public void setRadius(int rad){
    _radius = rad;
  }

  public void setCurrentFrame(int i){
    _currentFrame =i;
  }

  public ImageIcon getStrip(){
    return _strip;
  }

  public int getZOrder(){
    return _zOrder;
  }

  public void setZOrder(int z){
    _zOrder = z;
  }

  public String toString() {
    File ff = new File(_strip.toString());
    return "Ima=" + ff.getName() + ",wd=" + _width + ",ht=" + _height;
  }
}
