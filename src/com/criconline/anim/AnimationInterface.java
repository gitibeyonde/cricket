package com.criconline.anim;

import java.awt.Rectangle;

public interface AnimationInterface extends Runnable {
  public void update();
  public Rectangle getRealCoords();
  public void refresh();
  public int getCurrentTrack();
  public void repaintOwner(Rectangle r);
  public long delay();
  public boolean isSequential();
}
