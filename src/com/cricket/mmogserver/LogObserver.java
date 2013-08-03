package com.cricket.mmogserver;

import com.cricket.common.db.GameRunSequence;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import com.agneya.util.*;

import com.cricket.common.db.DBException;
import com.cricket.mmog.*;
import com.cricket.mmog.cric.Cricket;

public class LogObserver
    implements Observer, Serializable {
  // set the category for logging
  static Logger _cat = Logger.getLogger(LogObserver.class.getName());

  private static Object _dummy = new Object();
  private static LogObserver _lob = null;
  private static GameRunSequence _grs = null;
  static Configuration _conf;
  static boolean _logPlayGames = false;

  public static LogObserver instance() {
    if (_lob == null) {
      synchronized (_dummy) {
        if (_lob == null) {
          _lob = new LogObserver();
        }
      }
    }
    return _lob;
  }

  private LogObserver() {
    try {
      _grs = new GameRunSequence();
      _conf = Configuration.instance();
      _logPlayGames = _conf.getBoolean("Auditor.Log.PlayGame");
    }
    catch (ConfigurationException e) {
      _cat.warning("Configuration exception "+  e);
    }
    catch (DBException e) {
      _cat.warning("DB exception "+  e);
    }

  }

  /**
   * the update is called when a new game run
   * at the beginning of a new game run update the game id for all the clients
   * associated with the new game run
   *
   * @param o Observable
   * @param arg Object
   */
  public void update(Observable o, Object arg) {
    try {
      if (arg == GameStateEvent.GAME_BEGIN) {
        Game g = (Game) o;
        if (g instanceof Cricket) {
          Cricket pg = (Cricket) g;
          // setup the game run Id
          int seq = _grs.getNextGameRunId();
          g.grid(seq);
          _cat.finest("Sequence = " + seq);
          g.startTime(Calendar.getInstance());
        }
      }
      else if (arg == GameStateEvent.GAME_OVER) {
        Game g = (Game) o;
        if (g instanceof Cricket ){
          Cricket cg = (Cricket)g;
          CricketPresence[] pv = cg.activePlayers();
          for (int i = 0; i < pv.length; i++) {
            if (pv[i].player() instanceof GamePlayer) {
              GamePlayer gp = (GamePlayer) pv[i].player();
              _cat.finest("GEW=" + pv[i] + ", GP=" + gp);
              _cat.finest(gp.getDBPlayer().toString());
            }
          }
        }
      }
    }
    catch (DBException e) {
      _cat.warning("Unable to update state in DB"+  e);
    }
    catch (Exception e) {
      _cat.warning("Unable to update state in DB"+  e);
    }

  } // end update

} //end LoggingObserver
