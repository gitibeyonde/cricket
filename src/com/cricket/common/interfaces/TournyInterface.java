package com.cricket.common.interfaces;

import com.cricket.mmog.CricketPresence;
import com.cricket.mmog.Player.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public interface TournyInterface {

  public static final int NOEXIST = 0;
  public static final int CREATED = 1;
  public static final int DECL = 2;
  public static final int REG = 4;
  public static final int JOIN = 8;
  public static final int START = 16;
  public static final int RUNNING = 32;
  public static final int END = 64;
  public static final int FINISH = 128;
  public static final int CLEAR = 256;

  public static final int FINISH_CYCLES = 5;

  public CricketPresence[] winners();

  public int state();

  public String name();

  public int buyIn();

  public int fees();

  public int chips();

  public double prize(int i);

  public boolean tournyOver();
}
