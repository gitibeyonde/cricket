package com.criconline.anim;

import com.criconline.Painter;
import java.util.Observable;

public interface Animator extends Runnable, Painter {
  public void animate(AnimationEvent enmt, int frame);
  public void collision(Animation a);
  public void shift(int x, int y, int z);
  public Animation getAnimation();
  public int delay();
  public AnimationEvent getDefaultAnimation();
  public String getType();
  public int getZOrder();
}
