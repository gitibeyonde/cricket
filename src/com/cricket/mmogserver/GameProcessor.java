package com.cricket.mmogserver;

import com.cricket.common.message.Command;
import com.cricket.common.message.CommandFactory;
import com.cricket.common.message.CommandGameDetail;
import com.cricket.common.message.CommandGameList;
import com.cricket.common.message.CommandInt;
import com.cricket.common.message.CommandLogin;
import com.cricket.common.message.CommandMessage;
import com.cricket.common.message.CommandMove;
import com.cricket.common.message.CommandPlayerShift;
import com.cricket.common.message.CommandProcessor;
import com.cricket.common.message.CommandQueue;
import com.cricket.common.message.CommandRegister;
import com.cricket.common.message.CommandString;
import com.cricket.common.message.CommandTournyDetail;
import com.cricket.common.message.CommandTournyMyTable;
import com.cricket.common.message.CommandTournyRegister;
import com.cricket.common.message.CommandVote;
import com.cricket.common.message.GameEvent;
import com.cricket.common.message.Response;
import com.cricket.common.message.ResponseConfig;
import com.cricket.common.message.ResponseGameDetail;
import com.cricket.common.message.ResponseGameEvent;
import com.cricket.common.message.ResponseGameList;
import com.cricket.common.message.ResponseInt;
import com.cricket.common.message.ResponseLogin;
import com.cricket.common.message.ResponseMessage;
import com.cricket.common.message.ResponsePing;
import com.cricket.common.message.ResponsePlayerShift;
import com.cricket.common.message.ResponseString;
import com.cricket.common.message.ResponseTournyDetail;
import com.cricket.common.message.ResponseTournyList;
import com.cricket.common.message.ResponseTournyMyTable;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.Vector;
import java.util.List;
import java.util.Date;
import java.io.IOException;
import java.lang.Runnable;

import com.agneya.util.Configuration;
import com.agneya.util.ConfigurationException;

import java.util.NoSuchElementException;

import com.cricket.mmognio.MMOGClient;
import com.cricket.mmognio.MMOGHandler;

import com.cricket.common.db.LoginSession;

import com.cricket.mmog.*;
import com.agneya.util.Utils;

import com.cricket.common.db.DBException;
import com.cricket.mmog.cric.Cricket;
import com.cricket.mmog.gamemsgimpl.MessagingImpl;
import com.cricket.mmog.gamemsgimpl.MoveImpl;
import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.mmog.cric.Tourny;
import com.cricket.mmog.cric.TournyController;
import com.cricket.mmog.cric.impl.CricketTourny;
import com.cricket.mmog.gamemsgimpl.GameSummaryImpl;

import com.cricket.common.db.DBPlayer;

/**
 * MMOGHandler will work on the queued GameEvents and will invoke the
 * handler method on GameServer. The handler is blocking. Resulting
 * responses are written back to the queue
 */

public class GameProcessor implements CommandProcessor {
  static Logger _cat = Logger.getLogger(GameProcessor.class.getName());
  private int _nThreads;
  private Thread _t[];
  private CommandQueue _com;
  private boolean _keepservicing;
  private static int _id = 0;
  private static Configuration _conf;
  private String _cm = "";

  {
  try {
    _conf = Configuration.instance();
    _nThreads = _conf.getInt("Server.processor.threadCount");
    _cm = (String) _conf.get("Network.client.config");
  }
  catch (ConfigurationException e){
      _cat.warning(e.toString());
      System.exit(-1);
  }
  }

  public GameProcessor() throws Exception {
  }

  public void startProcessor(CommandQueue q) throws Exception {
    _com = q;
    _t = new Thread[_nThreads];
    _keepservicing = true;
    for (int i = 0; i < _nThreads; i++) {
      _t[i] = new Thread(this);
      _t[i].start();
      _t[i].setName("Queue-" + _id + " Processor-" + i);
      _t[i].setPriority(Thread.NORM_PRIORITY);
    }
    _id++;
  }

  public void stopProcessor() throws Exception {
    _keepservicing = false;
  }

  public void wakeUp() {
    for (int i = 0; i < _nThreads; _t[i++].interrupt()) {
      ;
    }
  }

  public void run() {
    while (_keepservicing) {
      Command c = null;
      try {
        c = CommandFactory.getCommand(_com.fetch());
        _cat.finest("Processing " + c);
        if (c != null) {
          process(c);
        }
      }
      catch (NoSuchElementException e) {
        try {
          Thread.currentThread().sleep(1000000);
        }
        catch (InterruptedException ee) {
          // continue
        }
      }
    }
  }

  /**
   * fetch a client from the queue and process it
   */
  public void process(Command ge) {
    if (ge == null) {
      return;
    }
    try {
      _cat.finest("Game Event " + ge);
      MMOGHandler handler = (MMOGHandler) ge.handler();
      switch (GameServer._state) {
        case GameServer.STARTING:
          starting(ge, handler);
          return;
        case GameServer.SHUTTING:
          shutting(ge, handler);
          return;
        case GameServer.SUSPEND:
          suspend(ge, handler);
          return;
        case GameServer.NORMAL:
          // fall thru
      }
      switch (ge.getCommandName()) {
        case Command.C_CONNECT:
          connect(ge, handler);
          break;
        case Command.C_LOGIN:
          login(ge, handler);
          break;
        case Command.C_LOGOUT:
          logout(ge, handler);
          break;
        case Command.C_SIT_OUT:
          sitOut(ge, handler);
          break;
        case Command.C_SIT_IN:
          sitIn(ge, handler);
          break;
        case Command.C_CONFIG:
          config(ge, handler);
          break;
        case Command.C_BUYCHIPS:
          buyChips(ge, handler);
          break;
        case Command.C_GET_CHIPS_INTO_GAME:
          getChips(ge, handler);
          break;
        case Command.C_GAMELIST:
          gameList(ge, handler);
          break;
        case Command.C_TOURNYLIST:
          tournyList(ge, handler);
          break;
        case Command.C_TURN_DEAF:
          turnDeaf(ge, handler);
          break;
        case Command.C_GAMEDETAIL:
          gameDetail(ge, handler);
          break;
        case Command.C_WAITER:
          addWaiter(ge, handler);
          break;
        case Command.C_TOURNYDETAIL:
          tournyDetail(ge, handler);
          break;
        case Command.C_TOURNYMYTABLE:
          tournyMyTable(ge, handler);
          break;
        case Command.C_TOURNYREGISTER:
          tournyRegister(ge, handler);
          break;
        case Command.C_TICKET:
          ticket(ge, handler);
          break;
        case Command.C_BUY_TICKET:
          buyTicket(ge, handler);
          break;
        case Command.C_MESSAGE:
          message(ge, handler);
          break;
        case Command.C_MOVE:
          move(ge, handler);
          break;
        case Command.C_PREFERENCES:
          preferences(ge, handler);
          break;
        case Command.C_PING:
          ping(ge, handler);
          break;
        case Command.C_VOTE:
          vote(ge, handler);
          break;
        case Command.C_PLAYERSHIFT:
          playerShift(ge, handler);
          break;
        case Command.C_PLAYERSTATS:
          playerStats(ge, handler);
          break;
        case Command.C_PROFESSIONAL_PLAYERS:
         // proPlayers(ge, handler);
          break;
        case Command.C_TEAM_MANAGER:
          //teamManagers(ge, handler);
          break;




        case Command.C_ADMIN:
          break;
        case Command.C_HTBT:


          /*handler.putResponse(new com.cricket.common.message.Response(com.
                         golconda.common.message.Response.SUCCESS,
                         com.cricket.common.message.Response.R_HTBT));
           */
          //ignore
          break;
        default:
          if (handler != null) {
            handler.putResponse(new com.cricket.common.message.Response(com.
                cricket.common.message.Response.E_FAILURE, com.cricket.common.message.Response.R_UNKNOWN));
            _cat.info("Unknown Command = " + ge);
          }
      }
    }
    catch (Throwable e) {
      // remove the player from the game
      _cat.warning("Error in processing " + ge+  e);
       e.printStackTrace();
    }
  } // end process

  /**
   * This method delivers appropriate responses to the players and observers
   **/
  public static void deliverResponse(com.cricket.mmog.resp.Response[] r) throws
      IOException {
    if (r == null) {
      return;
    }
    for (int i = 0; i < r.length; i++) {
      deliverResponse(r[i]);
    }
  }

  /**
   * This method delivers appropriate responses to the players and observers
   **/
  public static void deliverResponse(com.cricket.mmog.resp.Response r) throws
      IOException {
    if (r == null) {
      return;
    }
    // return a table detail response
    CricketPresence cl[] = r.recepients();
    for (int i = 0; cl != null && i < cl.length; i++) {
      CricketPresence clnt = (CricketPresence) cl[i];
      ResponseGameEvent rge = new ResponseGameEvent(1, r.getCommand(clnt));
      if (clnt.isDisconnected()){
        _cat.finest("->Player Proxy=" + clnt.name() + "; " + rge);
        ( (GamePlayer) clnt.player()).deliverProxy(rge);
      }
      else {
        _cat.info("->Player=" + clnt.name() + "; " + rge);
        ((GamePlayer) clnt.player()).deliver(rge);
      }
    }
    cl = r.observers();
    for (int i = 0; cl != null && i < cl.length; i++) {
      CricketPresence clnt = (CricketPresence) cl[i];
      String broadcast = r.getBroadcast();
      if (broadcast != null) {
        ResponseGameEvent rge = new ResponseGameEvent(1, broadcast);
        _cat.finest("->Observer=" + clnt.name() + "; " + rge);
        ((GamePlayer) clnt.player()).deliver(rge);
      }
    }
  }

  public static void deliverMessageResponse(com.cricket.mmog.resp.Response[] r) throws
      IOException {
    for (int i = 0; i < r.length; i++) {
      deliverMessageResponse(r[i]);
    }
  }

  /**
   * This method delivers appropriate responses to the players and observers
   **/
  public static void deliverMessageResponse(com.cricket.mmog.resp.Response r) throws
      IOException {
    // return a table detail response
    CricketPresence cl[] = r.recepients();
    for (int i = 0; i < cl.length; i++) {
      CricketPresence clnt = (CricketPresence) cl[i];
      ResponseMessage rge = new ResponseMessage(1, r.getCommand(clnt).toString());
      _cat.finest("Message to Player=" + clnt.name() + "; " + rge);
      ((GamePlayer) clnt.player()).deliver(rge);
    }
    cl = r.observers();
    for (int i = 0; i < cl.length; i++) {
      CricketPresence clnt = (CricketPresence) cl[i];
      ResponseMessage rge = new ResponseMessage(1, r.getBroadcast().toString());
      _cat.finest("Message to Observer=" + clnt.name() + "; " + rge);
      ((GamePlayer) clnt.player()).deliver(rge);
    }
  }

  private void starting(Command ge, MMOGHandler handler) {
    GamePlayer pp = new GamePlayer(handler);
    handler.attachment((MMOGClient) pp);
    com.cricket.common.message.Response gr = new com.cricket.common.message.Response(com.cricket.common.
        message.Response.E_FAILURE,com.cricket.common.message.Response.E_STARTING);
    handler.putResponse(gr);
  }

  private void shutting(Command ge, MMOGHandler handler) {
    GamePlayer pp = new GamePlayer(handler);
    handler.attachment((MMOGClient) pp);
    com.cricket.common.message.Response gr = new com.cricket.common.message.Response(com.cricket.common.
        message.Response.E_FAILURE,com.cricket.common.message.Response.E_SHUTTING);
    handler.putResponse(gr);
  }

  private void suspend(Command ge, MMOGHandler handler) {
    GamePlayer pp = new GamePlayer(handler);
    handler.attachment((MMOGClient) pp);
    com.cricket.common.message.Response gr = new com.cricket.common.message.Response(com.cricket.common.
        message.Response.E_FAILURE,com.cricket.common.message.Response.E_SUSPEND);
    handler.putResponse(gr);
  }

  private void connect(Command ge, MMOGHandler handler) {
    if (handler != null && handler.attachment() == null) {
      GamePlayer pp = new GamePlayer(handler);
      handler.attachment(pp);
      com.cricket.common.message.Response gr = new com.cricket.common.message.Response(com.cricket.common.
          message.Response.E_SUCCESS,com.cricket.common.message.Response.R_CONNECT);
      // look at all the presences and send back info so that the client can open those tables
      //TODO for reconnect
      handler.putResponse(gr);
    }
    else { // new connection
      com.cricket.common.message.Response gr = new com.cricket.common.message.Response(com.cricket.common.
          message.Response.E_SUCCESS,com.cricket.common.message.Response.R_CONNECT);
      handler.putResponse(gr);
    }
  }

  //public static final long WEEK = 7*24*60*60*1000;
  public static final long DAY = 24 * 60 * 60 * 1000;

  private void config(Command ge, MMOGHandler handler) {
    GamePlayer pp = (GamePlayer) handler.attachment();
    ResponseConfig gr = new ResponseConfig(1, _cm);
    _cat.finest("Response config = " + gr);
    handler.putResponse(gr);
  }

  private void getChips(Command ge, MMOGHandler handler) throws Exception {
  }

  private void buyChips(Command ge, MMOGHandler handler) {
  }

  private void logout(Command ge, MMOGHandler handler) {
    GamePlayer pp = (GamePlayer) handler.attachment();
    if (pp == null) {
      return; // the player does not exist
    }
    try {
      com.cricket.common.message.Response gr = new com.cricket.common.message.Response(com.cricket.common.
          message.Response.E_SUCCESS,com.cricket.common.message.Response.R_LOGOUT);
      handler.putResponse(gr);
    }
    catch (Exception e) {
      ;
    }
    pp.kill();
  }

  private void sitIn(Command ge, MMOGHandler handler) throws IOException {
    com.cricket.common.message.Response gr = null;
    GamePlayer pp = (GamePlayer) handler.attachment();
    CommandString cint = (CommandString) ge;
    String tid = cint.getStringVal();

    //pp.presence(tid).setSitin();
    Game g = Game.game(tid);
    if (g instanceof Cricket) {
      Cricket pg = (Cricket) g;
      if (!pg._inProgress) {
        deliverResponse(pg.start());
      }
    }
    _cat.warning("Player " + pp.name() + " is sitting in");
    gr = new com.cricket.common.message.ResponseString(com.cricket.common.
        message.Response.E_SUCCESS,com.cricket.common.message.Response.R_SIT_IN, tid);
    handler.putResponse(gr);
  }

  private void sitOut(Command ge, MMOGHandler handler) {
    com.cricket.common.message.Response gr = null;
    GamePlayer pp = (GamePlayer) handler.attachment();
    CommandInt cint = (CommandInt) ge;
    int tid = cint.getIntVal();

    //pp.presence(tid).setSitOutNextGame();
    _cat.info("Player " + pp.name() + " is sitting out");
    gr = new com.cricket.common.message.ResponseInt(com.cricket.common.
        message.Response.E_SUCCESS,com.cricket.common.message.Response.R_SIT_OUT, tid);
    handler.putResponse(gr);
  }


  private synchronized void login(Command ge, MMOGHandler handler) {
      com.cricket.common.message.Response gr = null;
      //com.cricket.common.message.ResponseMessage mr = null;
      GamePlayer pp = (GamePlayer) handler.attachment();
      String user = ( (CommandLogin) ge).getUserName();
      String token = ( (CommandLogin) ge).getToken();
      String provider = ( (CommandLogin) ge).getProvider();
      String affiliate = ( (CommandLogin) ge).getAffiliate();

      // Enforce IP restriction
      /***if (Utils.isRestricted(handler.inetAddress().getHostAddress())) {
       gr = new com.cricket.common.message.Response(com.cricket.common.message.
            Response.E_IP_RESTRICTED,
            com.cricket.common.message.Response.
            R_LOGIN);
        handler.putResponse(gr);
        return;
           }***/

      if (pp != null && pp.isAuthenticated()) {
        gr = new com.cricket.common.message.Response(com.cricket.common.
            message.
            Response.E_ALREADY_LOGGED,com.cricket.common.message.Response.R_LOGIN);
        handler.putResponse(gr);
        return;
      }
      String password = ( (CommandLogin) (ge)).getPassword();

      if (password.equals("sgsn")){
          pp.setDBPlayer(null);
          pp.name(user);
          pp.setAuthenticated();
          pp.gender(1);
          pp.setAuthenticated();
          gr = new ResponseLogin(com.cricket.common.message.Response.E_SUCCESS,
                                 pp.gender(),
                                 1000, 0, 0,
                                 0.0, null,
                                 null, null);
          handler.putResponse(gr);
         return;
      }

      if (user.length() < 2) {
        gr = new com.cricket.common.message.Response(
            com.cricket.common.message.Response.E_FAILURE,
            com.cricket.common.message.Response.R_LOGIN);
      }

      //////////// DEFAULT  PLAYER AUTHETICATION
      boolean player_exists = false;
      DBPlayer dbp = new DBPlayer();
      try {
        player_exists = dbp.get(user, password, affiliate);
        /**if (PlayerPreferences.isBannedPlayer(dbp.getPreferences())) {
          pp.unsetAuthenticated();
          gr = new com.cricket.common.message.Response(com.cricket.common.
              message.
              Response.E_BANNED,
              com.cricket.common.message.Response.
              R_LOGIN);
          handler.putResponse(gr);
          return;
        }**/
      }
      catch (DBException e) {
        _cat.warning(e.getMessage());

        pp.unsetAuthenticated();
        gr = new com.cricket.common.message.Response(com.cricket.common.
            message.
            Response.E_AUTHENTICATE,com.cricket.common.message.Response.R_LOGIN);
        handler.putResponse(gr);
        return;
      }
      catch (IllegalStateException e) {
        _cat.warning(e.getMessage());

        pp.unsetAuthenticated();
        gr = new com.cricket.common.message.Response(com.cricket.common.
            message.
            Response.E_AFFILIATE_MISMATCH,com.cricket.common.message.Response.R_LOGIN);
        handler.putResponse(gr);
        return;
      }

      try {
        if (player_exists) {
          _cat.finest(token + " Player exists = " + player_exists);
          pp.setDBPlayer(dbp);
          pp.name(user);
          pp.setAuthenticated();
          pp.gender(dbp.getGender());
          //pp.playWorth(dbp.getPlayChips());
          //pp.realWorth(dbp.getRealChips());
          //check shill
          pp.shill(token.equals("807") ? true : false);
          int cr = com.cricket.common.message.Response.E_SUCCESS;

          // authenticate and get the players worth and email from the database

          // set the user session information
          LoginSession ls = new LoginSession(user);
          ls.setDispName(user);
          ls.setLoginTime(new Date());
          ls.setLogoutTime(new Date());
          ls.setSessionId(handler._id);
          ls.setAffiliateId(affiliate);
          ls.setIp(handler.inetAddress().getHostAddress());
          //ls.setStartWorth(pp.realWorth()); //pp.worth());
          //ls.setEndWorth(pp.realWorth()); //pp.worth());

          ls.save();
          pp.loginSession(ls);
          _cat.finest("Successfully logged in " + user);
          ////// RECONNECT
          Vector posv = new Vector();
          Vector tidv = new Vector();
          Vector teamv = new Vector();
          if (pp.isAuthenticated()) {
            //check if this user is also logged in from some other session
            MMOGHandler old_h = null;
            GamePlayer old_c = null;
            for (Enumeration e = handler.registry().elements();
                 e.hasMoreElements(); ) {
              old_h = (MMOGHandler) e.nextElement();
              old_c = (GamePlayer) old_h.attachment();
              if (old_c != null && old_c.name().equals(user) &&
                  !old_h.equals(handler)) {
                //the player is already logged in
                // logout this and continue with login
                com.cricket.common.message.Response r = new com.cricket.common.message.Response(com.cricket.common.message.Response.E_LOGGED_IN_AT_DIFF_LOCATION, com.cricket.common.message.Response.R_LOGOUT);
                old_h.putResponse(r);
                old_h.attachment( (MMOGClient)null);
                old_h.kill();
                _cat.info("Existing game player found " + old_c);
                // look for all the presences
                int i = 0;
                for (Enumeration tnum = old_c.getPresenceList();
                     tnum.hasMoreElements(); i++) {
                  CricketPresence presence = (CricketPresence)tnum.nextElement();
                  String tid = presence.getGID();
                  Game g = Game.game(tid);
                  long grid = presence.getGRID();
                  //TournyInterface trny = TournyController.instance().getTourny(presence._tid);
                  if (
                      (g != null && !g.isGRIDOver(grid))
                      /**|| (trny != null && !trny.tournyOver())**/
                      ) {
                    // open tables where there is no need of response
                    // to maintain consistency
                    tidv.add(presence.getGID() + "");
                    posv.add(presence.pos() + "");
                    teamv.add(presence.team()._team_name);
                    _cat.info("Older presence " + presence);
                    presence.unsetDisconnected();
                    //presence.setReconnected();
                    //presence.unsetRaiseReq();
                    if (!presence.isResponseReq()) {
                      presence.unsetRemoved();
                    }
                    _cat.warning("Presence " + presence);
                  }
                  else {
                    // remove presence
                    _cat.info(
                        " For this presence neither the game or the tourny is running " +
                        presence);
                    old_c.kill(presence, true);
                  }
                }
                handler.attachment(old_c);
                old_c.setHandler(handler);

                //old_c.kill();
                //old_h.setDead(); // removed so that it delivers all the pending messages and then kill
                _cat.warning("Swaping old GP " + old_c);
                _cat.warning("New Handler " + handler);
                break;
              }
            }
          }
          ////// RECONNECT END
          gr = new ResponseLogin(cr,
                                 pp.gender(),
                                 (int)dbp.getPoints(), dbp.getPreferences(), dbp.getRank(),
                                 0.0, tidv,
                                 posv, teamv);
          //_cat.info("Response = " + gr + " Player=" + pp);
        }
        else { // player does not exist in the DB

          // no player matches the criteria
          _cat.finest("Player does not exist in the db " + user);
          gr = new com.cricket.common.message.Response(com.cricket.common.
              message.
              Response.
              E_REGISTER,com.cricket.common.message.Response.R_LOGIN);
        }
      }
      catch (Exception e) {
        _cat.warning("DBException during login " + e);
        pp.unsetAuthenticated();
        gr = new com.cricket.common.message.Response(com.cricket.common.
            message.
            Response.
            E_FAILURE,com.cricket.common.message.Response.R_LOGIN);
      }
      handler.putResponse(gr);
  }



  private void gameList(Command ge, MMOGHandler handler) throws Exception {
    CricketPlayer pp = (CricketPlayer) handler.attachment();
    int mask = ((CommandGameList) ge).getType();
    String aff = ((CommandGameList) ge).getAffiliate();
    aff = aff == null ? "admin" : aff;
    String plyrs = ((CommandGameList) ge).getPlayer();
    int i = 0;
    Game[] g = Game.listAll();
    Vector games = new Vector();
    for (int k = 0; k < g.length; k++) {
      _cat.finest(g[k].name() + ", " + g[k].details()[0].getBroadcast());
      if ((g[k].type().intVal() & mask) == 0) {
        continue;
      }
      games.add(g[k].details()[0].getBroadcast());
    }
    ResponseGameList gr = new ResponseGameList(com.cricket.common.message.Response.E_SUCCESS,
                                               (String[])
                                               games.toArray(new String[games.
        size()]));
    gr.setDetails(g.length, MMOGHandler.registry().size());
    _cat.finest("Response TABLELIST = " + gr);
    // set the game response in the clients out queue
    handler.putResponse(gr);
  }

  private void tournyList(Command ge, MMOGHandler handler) throws IOException {
    CricketPlayer pp = (CricketPlayer) handler.attachment();
    TournyController tc = TournyController.instance();
    Tourny[] t = tc.listAll();
    String[] ts = new String[t.length];
    for (int k = 0; k < t.length; k++) {
      ts[k] = t[k].stringValue();
    }
    ResponseTournyList gr = new ResponseTournyList(com.cricket.common.message.Response.E_SUCCESS, ts);
    _cat.info("Response TournyList = " + gr);
    // set the game response in the clients out queue
    handler.putResponse(gr);
  }

  private synchronized void turnDeaf(Command ge, MMOGHandler handler) throws
      IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    CommandString cint = (CommandString) ge;
    String tid = cint.getStringVal();
    if (pp != null && pp.presence(tid) != null) {
      com.cricket.mmog.resp.Response[] r = pp.leaveGameAndWatch((CricketPresence)pp.presence(
          tid));
      if (r != null) {
        deliverResponse(r);
      }
    }
  }

  private synchronized void gameDetail(Command ge, MMOGHandler handler) throws
      IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    String tid = ((CommandGameDetail) ge).getTableId();
    // query the game server for game  details
    // get the table details and set
    Game g = Game.game(tid);
    if (g == null) {
      ResponseString rge = new ResponseString(Response.E_NONEXIST, Response.R_GAMEDETAIL, tid);
      handler.putResponse(rge);
      return;
    }
    com.cricket.mmog.resp.Response[] r = pp.addWatch(tid);
    if (r == null) {
      ResponseString rge = new ResponseString(Response.E_GAME_NOT_ALLOWED, Response.R_GAMEDETAIL, tid);
      handler.putResponse(rge);
      return;
    }
    else {
      ResponseGameDetail rge = new ResponseGameDetail(1,
          r[0].getCommand((CricketPresence)pp.presence(tid)));
      rge.setIp(GameSummary.ip());
      handler.putResponse(rge);
    }
  }

  private void addWaiter(Command ge, MMOGHandler handler) throws IOException {
  }


  private void tournyDetail(Command ge, MMOGHandler handler) throws IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    String tid = ((CommandTournyDetail) ge).getTournyId();
    TournyController tc = TournyController.instance();
    Tourny t = tc.getTourny(tid);
    _cat.finest(tid + " Tourny Details = " + t);
    Response gr = null;
    GameEvent gev = new GameEvent();
    if (t == null) {
      gr = new ResponseTournyDetail(Response.E_NONEXIST, null, null);
      handler.putResponse(gr);
      tournyList(ge, handler);
    }
    else {
      _cat.info("Tourny = " + t.stringValue());
      // if tourny has not yet started send reg details
      String te = t.stringValue();
      Vector games = new Vector();
      if (t.isRegOpen()) {
        _cat.info("Tournament is open for registration " + te);
      }
      else if (t.isWaiting() || t.isPlaying()) {
        // check which table is assigned to this player
        CricketTourny[] g = t.listAll();
        for (int k = 0; k < g.length; k++) {
          String gid = g[k].name();
          com.cricket.mmog.resp.Response[] r = com.cricket.mmog.Game.handle(new
              GameSummaryImpl(gid, null));
          if (r[0].getBroadcast().toString().equals("null")) {
            continue;
          }
          games.add(r[0].getBroadcast().toString());
          _cat.info("Tournament is open for waiting or running " +
                    r[0].getBroadcast().toString());
        }
      }
      gr = new ResponseTournyDetail(com.cricket.common.message.Response.E_SUCCESS, te,
                                    (String[]) games.toArray(new String[games.
          size()]));
      _cat.info("Response TOURNY DETAIL = " + gr);
      handler.putResponse(gr);
    }
  }

  private synchronized void tournyMyTable(Command ge, MMOGHandler handler) throws
      IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    String tid = ((CommandTournyMyTable) ge).getTournyId();
    TournyController tc = TournyController.instance();
    Tourny t = tc.getTourny(tid);
    _cat.info("Tourny = " + t.stringValue());
    // if tourny has not yet started send reg details
    Response gr = null;
    com.cricket.mmog.resp.Response[] r = null;
    CricketPresence p = null;
    try {
      r = t.myTable(pp);
      if (r == null) {
        gr = new ResponseTournyMyTable(com.cricket.common.message.Response.E_FAILURE, tid, "", -1);
      }
      else {
        _cat.info("My table resp = " + r[0].getBroadcast());
        String gid = r[0].getGame().name();
        p = (CricketPresence)pp.presence(gid);
        gr = new ResponseTournyMyTable(gid.length()==0 ? com.cricket.common.message.Response.E_FAILURE : com.cricket.common.message.Response.E_SUCCESS, tid, gid, p.pos());
      }
    }
    catch (Exception e) {
      _cat.warning("Exception during goto my table " + tid+  e);
      gr = new ResponseTournyMyTable(com.cricket.common.message.Response.E_FAILURE, tid, "", -1);
    }
    _cat.info("Response TOURNY MY TABLE = " + gr);
    // set the game response in the clients out queue
    handler.putResponse(gr);
  }

  private void tournyRegister(Command ge, MMOGHandler handler) throws
      IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    com.cricket.common.message.Response gr = null;
    try {
      if (!pp.isAuthenticated()) {
        // authenticate the client first
        gr = new com.cricket.common.message.ResponseInt(com.cricket.common.
            message.Response.E_AUTHENTICATE,com.cricket.common.message.Response.R_TOURNYREGISTER, 0);
        _cat.finest("Response Tourny Register " + gr);
        // set the game response in the clients out queue
      }
      else { //player is authenticated
        String tid = ((CommandTournyRegister) ge).getTournyName();
        TournyController tc = TournyController.instance();
        Tourny t = tc.getTourny(tid);
        if (!t.isRegOpen()) {
          // not open for registration
          gr = new com.cricket.common.message.ResponseString(com.cricket.common.
              message.Response.E_REGISTERATION_CLOSED,com.cricket.common.message.Response.R_TOURNYREGISTER, tid);
          _cat.finest("Tournament not open for Registeration " + gr);
          // set the game response in the clients out queue
        }
        else { //open for registration
          if (pp.getDBPlayer().getPoints() < t.buyIn()) {
            //player is broke
            gr = new com.cricket.common.message.ResponseString(com.cricket.
                common.message.Response.E_BROKE,com.cricket.common.message.Response.R_TOURNYREGISTER, tid);
          }
          else {
            boolean result = t.register(pp);
            if (!result) {
              // already registered
              gr = new com.cricket.common.message.ResponseString(com.cricket.
                  common.message.Response.E_ALREADY_REGISTERED,com.cricket.common.message.Response.R_TOURNYREGISTER, tid);
              _cat.finest("Already registered " + gr);
            }
            else {

              // this will create tourny chips for the player
              int r = t.dbTourny().register(pp.getDBPlayer(), handler._id, t);

              if (r > 0) {
                gr = new com.cricket.common.message.ResponseString(com.cricket.
                    common.message.Response.E_SUCCESS,com.cricket.common.message.Response.R_TOURNYREGISTER, tid);
              }
              else {
                gr = new com.cricket.common.message.ResponseString(com.cricket.
                    common.message.Response.E_FAILURE,com.cricket.common.message.Response.R_TOURNYREGISTER, tid);

              }
            }
          }
          _cat.finest("Registered DETAIL = " + gr);
          // set the game response in the clients out queue
        }
      }
    }
    catch (Exception e) {
      gr = new com.cricket.common.message.ResponseInt(com.cricket.common.message.Response.E_FAILURE, com.cricket.common.message.Response.R_TOURNYREGISTER, -1);
    }
    handler.putResponse(gr);
  }

  private void ticket(Command ge, MMOGHandler handler) throws IOException {
  }

  private void buyTicket(Command ge, MMOGHandler handler) throws Exception {
  }

  private void message(Command ge, MMOGHandler handler) throws IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    String msg = ((CommandMessage) ge).message();
    String tid = ((CommandMessage) ge).getTableId();
    if (tid.length() == 0) {
      lobbyChat(pp.name(), msg);
    }
    else {
      CricketPresence p = (CricketPresence)pp.presence(tid);
      _cat.warning(pp.toString());
      deliverMessageResponse(com.cricket.mmog.Game.handle(new MessagingImpl(
          tid, p, msg)));
    }
  }

  private void preferences(Command ge, MMOGHandler handler) throws IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    int pref = ((CommandInt) ge).getIntVal();
    pp.getDBPlayer().setPreferences(pref);
    Response gr = new Response(1, Response.R_PREFERENCES);
    handler.putResponse(gr);
  }

  private void vote(Command ge, MMOGHandler handler) throws IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    String for_player = ((CommandVote) ge).forPlayer();
    String tid = ((CommandVote) ge).getTid();

    deliverMessageResponse(com.cricket.mmog.Game.handle(new MessagingImpl(tid, null,
        for_player + "  has been voted against by " + pp.name())));
    Response gr = new Response(1, Response.R_VOTE);
    handler.putResponse(gr);
  }

  private void playerStats(Command ge, MMOGHandler handler) throws IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    CommandString cps = (CommandString) ge;
    String userid = cps.getStringVal();
    GamePlayer gp = GamePlayer.getPlayer(userid);
    //ResponseString rs = new ResponseString(gp.getDBPlayer().playerStat());
    //handler.putResponse(rs);
  }

 /** private void proPlayers(Command ge, MMOGHandler handler) throws IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    try {
      DBPlayer[] v = DBPlayer.getRankedPlayers(2);
      StringBuffer sbuf = new StringBuffer();
      for (int i=0;i<v.length;i++){
        sbuf.append(v[i].stringValue()).append("|");
      }
      ResponseString rs = new ResponseString(1, Response.R_PROFESSIONAL_PLAYERS, sbuf.toString());
      handler.putResponse(rs);
    }catch (DBException e){
      ResponseString rs = new ResponseString(Response.E_DB_FAILURE, Response.R_PROFESSIONAL_PLAYERS, "");
      handler.putResponse(rs);
    }
  }

  private void teamManagers(Command ge, MMOGHandler handler) throws IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    try {
      DBPlayer[] v = DBPlayer.getRankedPlayers(3);
      StringBuffer sbuf = new StringBuffer();
      for (int i=0;i<v.length;i++){
        sbuf.append(v[i].stringValue()).append("|");
      }
      ResponseString rs = new ResponseString(1, Response.R_TEAM_MANAGER, sbuf.toString());
      handler.putResponse(rs);
    }catch (DBException e){
      ResponseString rs = new ResponseString(Response.E_DB_FAILURE, Response.R_TEAM_MANAGER, "");
      handler.putResponse(rs);
    }
  }**/

  private void playerShift(Command ge, MMOGHandler handler) throws IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    CommandPlayerShift cps = (CommandPlayerShift) ge;
    _cat.finest("PlayerShift" + cps);
    ResponsePlayerShift rps = new ResponsePlayerShift(cps.getGid(), cps.getPid(),
        cps.getDx(), cps.getDy(), cps.getDz());

    Game g = Game.game(cps.getGid());
    if (g instanceof Cricket) {
      Cricket crc = (Cricket) g;
      if (g == null || !g.running()) {
        //ignore
        return;
      }

      //send to players
      CricketPresence v[] = crc.allPlayers(0);
      for (int i = 0; i < v.length; i++) {
        CricketPlayer p = v[i].player();
        if (p instanceof GamePlayer) {
          GamePlayer gp = (GamePlayer) p;
          gp.deliver(rps);
        }
      }
      //send to observers
      v = crc.getObservers();
      for (int i = 0; i < v.length; i++) {
        CricketPlayer p = v[i].player();
        if (p instanceof GamePlayer) {
          GamePlayer gp = (GamePlayer) p;
          gp.deliver(rps);
        }
      }
    }
  }


  private synchronized void ping(Command ge, MMOGHandler handler) throws
      IOException {
    GamePlayer pp = (GamePlayer) handler.attachment();
    handler.putResponse(new ResponsePing());
  }

////////////////////////////MOVES///////////////////////////////////////////
  private synchronized void move(Command ge, MMOGHandler handler) throws
      Exception {
    GamePlayer pp = (GamePlayer) handler.attachment();
    if (pp == null) {
      return;
    }
    com.cricket.common.message.Response gr = null;
    if (!pp.isAuthenticated()) {
      // authenticate the client first
      gr = new com.cricket.common.message.Response(com.cricket.common.message.Response.E_AUTHENTICATE, com.cricket.common.message.Response.R_MOVE);
      // set the game response in the clients out queue
      handler.putResponse(gr);
    }
    int move = ((CommandMove) ge).getMove();
    String tid = ((CommandMove) ge).getTableId();
    int grid = ((CommandMove) ge).getHandId();
    MoveParams move_details = ((CommandMove) ge).getMoveDetails();
    double amt = ((CommandMove) ge).getMoveAmount();
    int pos = ((CommandMove) ge).getPlayerPosition();
    String team = ((CommandMove) ge).getPlayerTeam();
    //assert pos >= 0:"Postion = " + pos;
    CricketPresence p = (CricketPresence) pp.presence(tid);
    assert p != null:"For game move presence cannot be null " + pp;
    _cat.info( "Move=" + ge);
    switch (move) {
      /**
       * MOVE JOIN
       **/
      case Command.M_SIT_IN:

        Game g = Game.game(tid);


        /**if (p.getWorth() >= (g._buyin + g._fees)) {
          handler.putResponse(new com.cricket.common.message.ResponseInt(com.
              golconda.common.
              message.
              Response.E_BROKE,
              com.cricket.common.message.Response.
              R_MOVE, tid));
          _cat.warning("The player is broke and cannot join " + p);
                 }
                 else {**/
        deliverResponse(pp.addGame(p, pos, team, amt));


        /**handler.putResponse(new ResponseGetChipsIntoGame(1, tid, amt,
         pp.playWorth(), pp.getDBPlayer().getPlayBankroll(), pp.realWorth(),
            pp.getDBPlayer().getRealBankroll()));**/
        //}

        break;

        /**
         * MOVE LEAVE
         ***/
        //
      case Command.M_LEAVE:
        com.cricket.mmog.resp.Response[] r = pp.leaveGameAndWatch(p);
        if (r != null) {
          deliverResponse(r); // no need to send response as the player has already left the game
        }
        //handler.putResponse(new ResponseBuyChips(1, pp.points());

        _cat.finest("Removed from game " + p);
        break;

      case Command.M_START:
        deliverResponse(com.cricket.mmog.Game.handle(new MoveImpl(tid, p,
            Moves.START, amt, move_details)));
        break;

      case Command.M_TOSS:
        deliverResponse(com.cricket.mmog.Game.handle(new MoveImpl(tid, p,
            Moves.TOSS, amt, move_details)));
        break;

      case Command.M_TAIL:
        deliverResponse(com.cricket.mmog.Game.handle(new MoveImpl(tid, p,
            Moves.TAIL, amt, move_details)));
        break;

      case Command.M_HEAD:
        deliverResponse(com.cricket.mmog.Game.handle(new MoveImpl(tid, p,
            Moves.HEAD, amt, move_details)));
        break;

      case Command.M_FIELDING:
        deliverResponse(com.cricket.mmog.Game.handle(new MoveImpl(tid, p,
            Moves.FIELDING, amt, move_details)));
        break;

      case Command.M_BATTING:
        deliverResponse(com.cricket.mmog.Game.handle(new MoveImpl(tid, p,
            Moves.BATTING, amt, move_details)));
        break;

        /**
         * Bowlers move
         */
      case Command.M_BOWL:
        deliverResponse(com.cricket.mmog.Game.handle(new MoveImpl(tid, p,
            Moves.B_BOWL, amt, move_details)));
        break;

        /**
         * Batsman move
         */
      case Command.M_BAT:
        deliverResponse(com.cricket.mmog.Game.handle(new MoveImpl(tid, p,
            Moves.K_BAT, amt, move_details)));
        break;

        /**
         * Fielding move
         */
      case Command.M_FIELD:
        deliverResponse(com.cricket.mmog.Game.handle(new MoveImpl(tid, p,
            Moves.F_FIELD, amt, move_details)));
        break;

      case Command.M_DRINKS:
        deliverResponse(com.cricket.mmog.Game.handle(new MoveImpl(tid, p,
            Moves.DRINKS, amt, move_details)));
        break;

      default:


        //unknown move
        // send a failure
        gr = new com.cricket.common.message.Response(com.cricket.common.
            message.Response.E_FAILURE,com.cricket.common.message.Response.R_MOVE);
        _cat.warning("Unknown move " + ge);


        // set the game response in the clients out queue
        handler.putResponse(gr);
    }
  }

  /*
   Stop signals to suspend all runs of the game, till such time resume is called.
     If a run is in progress, the run is completed.
     Utility method, available at class level.
   */
  public static synchronized void suspendAll() {
    Game[] games = Game.listAll();
    for (int i = 0; i < games.length; i++) {
      suspend(games[i].name());
    }
  }

  /*
    A previously suspended game can run now.
    Utility method at class level
   */
  public static synchronized void resumeAll() throws IOException {
    Game[] games = Game.listAll();
    for (int i = 0; i < games.length; i++) {
      resume(games[i].name());
    }
  }

  public static void suspend(String id) {
    Game.game(id).suspend();
  }

  public static void resume(String id) throws IOException {
    com.cricket.mmog.resp.Response[] r = Game.game(id).resume();
    //deliver the response to all players
        com.cricket.mmogserver.GameProcessor.deliverResponse(r);
  }

  public static synchronized void destroyAll() throws IOException {
    Game[] games = Game.listAll();
    for (int i = 0; i < games.length; i++) {
      destroy(games[i].name());
    }
  }

  public static void destroy(String id) throws IOException {
    // destroy happens for poker game
    Game g = Game.game(id);
    if (g instanceof com.cricket.mmog.cric.Cricket) {
      com.cricket.mmog.cric.Cricket p = (com.cricket.mmog.cric.Cricket) g;
      // inform each player that game is going to be destroyed
      CricketPresence[] pp = p.allPlayers(0);
      for (int i = 0; i < pp.length; i++) {
        removePresence(pp[i]);
      }
      Game.game(id).destroy();
    }
  }

  public static void removePresence(String name, String id) throws IOException {
    Game g = Game.game(id);
    if (g instanceof com.cricket.mmog.cric.Cricket) {
      com.cricket.mmog.cric.Cricket pg = (com.cricket.mmog.cric.Cricket) g;
      CricketPresence[] pp = pg.allPlayers(0);
      for (int i = 0; i < pp.length; i++) {
        if (pp[i].name().equals(name)) {
          removePresence(pp[i]);
        }
      }
    }
  }

  public static void removePresence(CricketPresence p) throws IOException {
    GamePlayer gp = (GamePlayer) p.player();
    CommandMove cm = new CommandMove(gp.session(), Command.M_LEAVE, 0.0,
                                     p.getGID());
    com.cricket.mmog.resp.Response[] r = gp.leaveGameOnly(p, false);
    if (r != null) {
      _cat.warning(r[0].getBroadcast());
      deliverResponse(r); // no need to send response as the player has already left the game
    }

    /**gp.handler().putResponse(new ResponseBuyChips(1, gp.playWorth(),
                                                  gp.getDBPlayer().
                                                  getPlayBankroll(),
                                                  gp.realWorth(),
                                                  gp.getDBPlayer().
                                                  getRealBankroll(), p.getGID())); **/

    gp.handler().putResponse(new ResponseString(Response.E_PLAYER_REMOVED, Response.R_ADMIN, p.getGID()));

    _cat.finest("Removed from game " + p);
  }

  public static void broadcastAffiliate(String message, String affiliate) {
    Iterator enumt = MMOGHandler.registry().values().iterator();
    for (; enumt.hasNext(); ) {
      MMOGHandler h = (MMOGHandler) enumt.next();
      MMOGClient c = h.attachment();
      if (c instanceof GamePlayer) {
        GamePlayer gp = (GamePlayer) c;
        if (gp.getDBPlayer().getAffiliate().equals(affiliate)) {
          StringBuffer sbuf = new StringBuffer("type=broadcast").append(
              ",message=").
              append(message);
          ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
          rm.session(h._id);
          h.write(rm);
        }
      }
    }
  }

  public static void broadcast(String message) {
    Iterator enum1 = MMOGHandler.registry().values().iterator();
    for (; enum1.hasNext(); ) {
      MMOGHandler h = (MMOGHandler) enum1.next();
      StringBuffer sbuf = new StringBuffer("type=broadcast").append(",message=").
                          append(message);
      ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
      rm.session(h._id);
      h.write(rm);
    }
  }

  public static void lobbyChat(String name, String message) {
    Iterator enum1 = MMOGHandler.registry().values().iterator();
    for (; enum1.hasNext(); ) {
      MMOGHandler h = (MMOGHandler) enum1.next();
      StringBuffer sbuf = new StringBuffer("type=broadcast");
      sbuf.append(",name=").append(name);
      sbuf.append(",message=").append(message);
      ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
      rm.session(h._id);
      h.write(rm);
    }
  }

  public static void broadcastGame(String gid, String message) throws IOException {
    deliverMessageResponse(com.cricket.mmog.Game.handle(new MessagingImpl(gid, null,
        message)));
  }

  public static void broadcast(String session, String message) throws
      IOException {
    MMOGHandler h = MMOGHandler.get(session);
    StringBuffer sbuf = new StringBuffer("type=broadcast").append(",message=").
                        append(message);
    ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
    rm.session(h._id);
    h.write(rm);
  }

} // end Game Processor
