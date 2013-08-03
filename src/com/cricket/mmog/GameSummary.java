package com.cricket.mmog;

import java.util.*;
import java.util.logging.Logger;

import com.agneya.util.Configuration;
import com.agneya.util.ConfigurationException;

import com.cricket.mmogserver.GamePlayer;
import com.cricket.mmogserver.GameProcessor;
import com.agneya.util.Utils;

/**
 *
 */
public class GameSummary
    extends TimerTask {
  static Logger _cat = Logger.getLogger(GameSummary.class.getName());
  private static double _pot;
  private static double _jpot;
  private static String _ip;

  public GameSummary() throws Exception {
    Configuration conf = Configuration.instance();
    _ip = (String) conf.get("Network.server.ip");
  }

  public void run() {
    //add the summary methods
  }

  public static String ip() {
    return _ip;
  }

}
