package com.cricket.mmogserver;

import com.cricket.common.db.DBGame;
import com.cricket.common.db.GameSequence;

//import com.cricket.mmognet.GameController;
//import com.cricket.mmognet.NWServer;

import com.cricket.mmognio.MMOGAcceptor;

import java.util.logging.Logger;
import com.agneya.util.*;

import com.cricket.common.db.DBException;
import com.cricket.mmog.cric.*;
import com.cricket.mmog.GameType;
import com.cricket.mmog.cric.impl.CricketMatch;
import java.util.Calendar;
import java.util.Date;
import com.cricket.mmog.cric.impl.ScheduledMatch;


/**
 * PokerServer
 *
 * The PokerServer runs three main threads.  The first
 * is the user interface thread.  It gives you a simple
 * interface that responds to text commands, and quit.
 * The second is the ClientManager Thread.  It creates a
 * SocketServer and waits...  The last is a maintenance thread
 * that cleans up dead games and the such.
 */
public class GameServer {
  // set the category for logging
  static Logger _cat = Logger.getLogger(GameServer.class.getName());


  // set the configuration object
  static Configuration _conf;

  public static int _state;

  final static int STARTING = 0;
  final static int NORMAL = 1;
  final static int SHUTTING = 2;
  final static int SUSPEND = 3;
  final static int STOPPED = 4;

  private static MMOGAcceptor _a = null;

  public static void main(String[] args) throws Exception {

    // start the server and set the state as normal
    _state = STARTING;
    // create the singleton configuration object
    _conf = Configuration.instance();
    //PropertyConfigurator.configure(propFile );
   
    LogObserver lob = LogObserver.instance();

    //start the admin server
    //NWServer serv = new NWServer("com.cricket.mmognet.CommandProcessor");
    //serv.startServer(Configuration.instance().getInt("Admin.Network.port"));

    // create cricket from conf
    createCricketFromConf();

    // cricket tournaments
    createTournyFromConf();

    // start the nio server
    startAcceptorThread();

    /**GameController gc = new GameController(Configuration.instance().getProperty(
        "Admin.Network.server.ip"), Configuration.instance().getInt("Admin.Network.port"));
    gc.consoleLoop();
    System.exit(0); **/

  }

  public static boolean isShutting() {
    return _state == SHUTTING ? true : false;
  }

  public static void startAcceptorThread() throws Exception {
    // start the poker server thread which listens to the poker clients
    _a = new MMOGAcceptor();
    _a.startServer("com.cricket.mmogserver.GameProcessor");
    _state = NORMAL;
  }

  public static void stopAcceptorThread() throws Exception {
    if (_a != null) {
      _a.stopServer();
      _state = SHUTTING;
    }
  }

  public static int createMatch(Date d, String[] ta, String[] tb, String name, String tA, String tB, int type,
                                  int fees, int buyin, int balls_per_player) throws
      DBException {
    Cricket g = null;
    LogObserver lob = LogObserver.instance();
    DBGame dbg = null;
    int id = GameSequence.getNextGameId();
    g = new ScheduledMatch(d, ta, tb, id, name, type, tA, tB, balls_per_player, fees, buyin,
                    lob);
    _cat.finest("Match created " + g);

    return id;
  }

  public static int createCricket(String name, String tA, String tB, int type,
                                  int fees, int buyin, int balls_per_player) throws
      DBException {
    Cricket g = null;
    LogObserver lob = LogObserver.instance();
    DBGame dbg = null;
    int id = GameSequence.getNextGameId();
    if (new GameType(type).isMatch()) {
      g = new CricketMatch(id, name, type, tA, tB, balls_per_player, fees,
                           buyin, lob);
      _cat.finest("Match created " + g);
    }
    else {
      g = new Cricket(name, type, tA, tB, balls_per_player, fees, buyin,
                      lob);
      _cat.finest("Game created " + g);
    }
    //dbg.save();
    return id;
  }

  public static void createCricketFromConf() throws Exception {
    int gn = 1;
    String game_str = "Cricket.Game" + gn;
    _cat.finest(game_str + "=" + (String) _conf.get(game_str));
    while ((String) _conf.get(game_str) != null) {
      int game_count = _conf.getInt(game_str);
      _cat.finest(game_str + " Game count = " + game_count);
      String game_name = (String) _conf.get(game_str + ".Name");
      _cat.finest(game_str + " Game name = " + game_name);
      String teamAname = (String) _conf.get(game_str + ".TeamA");
      _cat.finest(game_str + " Game TeamA = " + teamAname);
      String teamBname = (String) _conf.get(game_str + ".TeamB");
      _cat.finest(game_str + " Game TeamB = " + teamBname);

      int game_type = _conf.getInt(game_str + ".Type");
      _cat.finest(game_str + " Game type = " + game_type);
      int balls = _conf.getInt(game_str + ".Balls");
      int fees = _conf.getInt(game_str + ".Fees");
      int buyin = _conf.getInt(game_str + ".Buyin");
      _cat.finest(game_str + " Game type = " + game_type + ", balls=" + balls + ", time=" + (String)_conf.get(game_str + ".Time"));
      Date d = new Date();
      if (game_type==256){
        _cat.finest("Date=" + d.toString());
        d = Utils.getDateFromString((String)_conf.get(game_str + ".Time"));
        // get teams
        String []ta =  ((String) _conf.get(game_str + ".TeamA.Players")).split(",");
        String []tb =  ((String) _conf.get(game_str + ".TeamB.Players")).split(",");
        createMatch(d, ta, tb, game_name, teamAname, teamBname, game_type, fees, buyin,
                      balls);
      }
      else {
        for (int j = 0; j < game_count; j++) {
          createCricket(game_name, teamAname, teamBname, game_type, fees,
                        buyin,
                        balls);
        }
      }
      gn++;
      game_str = "Cricket.Game" + gn;
      _cat.finest(game_str + "=" + (String) _conf.get(game_str));
    }
  }

  public static String createTourny(String name, int[] schedule, int buyin,
                                 int fees, int chips, int team_size, int decl,
                                 int reg, int join) throws DBException {

    LogObserver lob = LogObserver.instance();
    TournyController tc = TournyController.instance();
    return tc.addTourny(name, schedule, buyin, fees, chips, team_size, decl,
                        reg, join, lob);
  }


  public static void createTournyFromConf() throws Exception {
    //DBInitGames dbi = new DBInitGames();
    //MTTRow[] mt = dbi.getMTT();

    /**for (int i = 0; i < mt.length; i++) {
      if (mt[i].gameState==0)continue;
      createTourny(mt[i]);
         }**/

    int gn = 1;
    String game_str = "Cricket.Tourny" + gn;
    _cat.finest(game_str + "=" + (String) _conf.get(game_str));
    while ((String) _conf.get(game_str) != null) {
      int game_count = _conf.getInt(game_str);
      _cat.finest(game_str + " Game count = " + game_count);
      String game_name = (String) _conf.get(game_str + ".Name");
      _cat.finest(game_str + " Game name = " + game_name);
      String teamAname = (String) _conf.get(game_str + ".TeamA");
      _cat.finest(game_str + " Game TeamA = " + teamAname);
      String teamBname = (String) _conf.get(game_str + ".TeamB");
      _cat.finest(game_str + " Game TeamB = " + teamBname);
      int game_type = _conf.getInt(game_str + ".Type");
      _cat.finest(game_str + " Game type = " + game_type);
      int balls = _conf.getInt(game_str + ".Balls");
      int team_size = _conf.getInt(game_str + ".TeamSize");
      _cat.finest(game_str + " Team size = " + team_size);

      int fees = _conf.getInt(game_str + ".Fees");
      _cat.finest(game_str + " Fees = " + fees);
      int buyin = _conf.getInt(game_str + ".Buyin");

      String sc_str = (String) _conf.get(game_str + ".Schedule");
      _cat.finest(game_str + " Schedule = " + sc_str);
      String[] sc = sc_str.split(":");
      int[] schedule = new int[5];
      for (int i = 0; i < 5; i++) {
        schedule[i] = sc[i].equals("*") ? -1 : Integer.parseInt(sc[i]);
      }

      int decl = _conf.getInt(game_str + ".Decl");
      _cat.finest(game_str + " Decl = " + decl);
      int reg = _conf.getInt(game_str + ".Reg");
      _cat.finest(game_str + " Reg = " + reg);
      int join = _conf.getInt(game_str + ".Join");
      _cat.finest(game_str + " Join = " + join);


      for (int j = 0; j < game_count; j++) {
        createTourny(game_name, schedule, buyin, fees, 800, team_size, decl,
                     reg, join);
      }
      gn++;
      game_str = "Cricket.Tourny" + gn;
      _cat.finest(game_str + "=" + (String) _conf.get(game_str));
    }


  }


} // end CricketServer
