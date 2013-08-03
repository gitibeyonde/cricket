package com.cricket.mmogserver;

import com.cricket.common.db.LoginSession;

import com.cricket.mmog.Game;
import com.cricket.mmognio.MMOGClient;
import com.cricket.mmognio.MMOGHandler;

import java.util.*;

import java.util.logging.Logger;
import com.agneya.util.*;

import com.cricket.common.db.DBException;
import com.cricket.mmog.*;
import com.cricket.mmog.CricketPlayer;
import com.cricket.mmog.gamemsgimpl.*;
import com.cricket.mmog.resp.Response;

public class GamePlayer extends CricketPlayer implements MMOGClient {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(GamePlayer.class.getName());


  /**
   * boolean: true if client is authenticated
   */
  private boolean _isAuthenticated = false;
  private boolean _dead = false;
  private MMOGHandler _handler;
  protected LoginSession _loginSession;
  public String _session;
  static Configuration _conf;
  static boolean _logPlayGames = false;
  protected static Hashtable _registry;

  static {
    _registry = new Hashtable();
  }

  public GamePlayer(MMOGHandler h) {
    super("", 0);
    _handler = h;
    _session = h._id;
    try {
      _conf = Configuration.instance();
      _logPlayGames = _conf.getBoolean("Auditor.Log.PlayGame");
    }
    catch (ConfigurationException e) {
      _cat.warning("Configuration exception "+  e);
    }
  }


  /**
   *
   */

  public Enumeration getPresenceList() {
    return _presence_registry.elements();
  }

  public int presenceCount() {
    return _presence_registry.size();
  }


  /**
   *
   */

  public static Enumeration getGPList() {
    return _registry.elements();
  }

  public int disableChat() {
    int new_pref = _dbPlayer.getPreferences() | PlayerPreferences.DISABLE_CHAT;
    _dbPlayer.setPreferences(new_pref);
    return new_pref;
  }

  public int enableChat() {
    int new_pref = _dbPlayer.getPreferences() & ~PlayerPreferences.DISABLE_CHAT;
    _dbPlayer.setPreferences(new_pref);
    return new_pref;
  }

  public int ban() {
    int new_pref = _dbPlayer.getPreferences() | PlayerPreferences.BANNED_PLAYER;
    _dbPlayer.setPreferences(new_pref);
    return new_pref;
  }

  public int unban() {
    int new_pref = _dbPlayer.getPreferences() &
                   ~PlayerPreferences.BANNED_PLAYER;
    _dbPlayer.setPreferences(new_pref);
    return new_pref;
  }

  public int getPreferences() {
    return _dbPlayer.getPreferences();
  }

  public void setHandler(MMOGHandler h) {
    _handler = h;
    _session = h._id;
  }

  public boolean isDisconnected() {
    return _handler.isDisconnected();
  }

  public void name(String name) {
    _registry.put(name, this);
    this._name = name;
  }

  public static GamePlayer getPlayer(String name) {
    return (GamePlayer) _registry.get(name);
  }

    public CricketPresence createPresence(String tid) {
        CricketPresence p = (CricketPresence) _presence_registry.get(tid);
        if (p == null) {
            p = new CricketPresence(tid, this);
        }
        _presence_registry.put(tid, p);
        return p;
    }

    public Response[] addWatch(String tid) {
    CricketPresence p = (CricketPresence)createPresence(tid);
    _cat.finest("Adding watch " + p);
    if (p.isPlayer()) {
      return com.cricket.mmog.Game.handle(new GameDetailsImpl(tid, p));
    }
    else {
      return com.cricket.mmog.Game.handle(new ObserveGameImpl(p, tid));
    }
  }

  public Response[] addGame(CricketPresence p, int pos, String team, double amt) throws
      DBException {
    _cat.info("Setting " + p + " team = " + team);
    String gid = p.getGID();
    p.setPos(pos);
    p.setTeamName(team);
    p.unsetBroke();
    _cat.info("Setting " + this +" in Game = " + gid);
    return com.cricket.mmog.Game.handle(new ObserverToPlayerImpl(gid, p));
  }

  public Response[] leaveGameOnly(CricketPresence p, boolean timeout) {
    if (p.isPlayer()) {
      Game g = Game.game(p.getGID());
      _cat.info("Keeping " + p + " for tourny/game " + p.getGID());
      Response[] r = com.cricket.mmog.Game.handle(new LeaveGameImpl(p.getGID(),
          p, timeout));
      return r;
    }
    else {
      Response[] r = {new com.cricket.mmog.cric.GameDetailsResponse((com.
          cricket.mmog.cric.Cricket) Game.game(p.getGID()))};
      return r;
    }
  }


  /**
   * This request is made from turnDeaf and is a explicit request that player wants
   * to leave this game
   */
  public Response[] leaveGameAndWatch(CricketPresence p) {
    if (p.isTournyPresence()) {
      _cat.finest("Tourny presence left " + p);
      p.setDisconnected();
      Response[] r = com.cricket.mmog.Game.handle(new LeaveGameImpl(p.getGID(),
          p, false));
      return r;
    }
    else {
      if (p.isPlayer()) {
        p.setDisconnected();
        _cat.info("Removing " + p + " from game " + p.getGID());
        Response[] r = com.cricket.mmog.Game.handle(new LeaveGameImpl(p.getGID(),
            p, false));
        //p.removeChips();
        //p.unsetState();
        _presence_registry.remove(this);
        return r;
      }
      else {
        return leaveWatch(p);
      }
    }
  }

  public Response[] leaveWatch(CricketPresence p) {
    if (p.isObserver()) {
      _cat.info("Removing observer " + p.name() + " from game " + p.getGID());
      //new Exception().printStackTrace();
      _presence_registry.remove(p);
      return com.cricket.mmog.Game.handle(new LeaveWatchImpl(p, p.getGID()));
    }
    else {
      _cat.info("Removing a non observer " + p.name() + " from game " +
                p.getGID());
      Response[] r = {new com.cricket.mmog.cric.GameDetailsResponse((com.
          cricket.mmog.cric.Cricket) Game.game(p.getGID()))};
      return r;
    }
  }

  public void deliver(com.cricket.common.message.Response r) {
    if (_dead) {
      return;
    }
    _handler.putResponse(r);
  }

  public void deliverProxy(com.cricket.common.message.Response r) {
    _handler.writeProxy(r);
  }


  public boolean isDead() {
    return _dead;
  }


  /**
   * kill the client, remove it from the hashtable and set the state to DIED
   */
  public synchronized void kill() {
    if (_dead) {
      return;
    }
    boolean hasTournyPresence = false;
    for (Enumeration enumz = _presence_registry.elements();
                             enumz.hasMoreElements(); ) {
      try {
        CricketPresence presence = (CricketPresence) enumz.nextElement();
        kill(presence, false);
        if (presence.isTournyPresence()) {
          hasTournyPresence = true;
        }
      }
      catch (Exception e) {
        _cat.warning("Name=" + name()+  e);
      }
    } // remove player from all tables he is playing
    if (_loginSession != null) {
      try {
        _loginSession.updateLogout();
        _loginSession.setLogoutTime(new java.util.Date());
        _loginSession.updateLogout();
        //_dbPlayer.updateStats();
      }
      catch (Exception e) {
        _cat.warning(  e.toString());
      }
    }
    if (!hasTournyPresence) {
      _dead = true;
      _registry.remove(_name);
      _handler.kill();
      _cat.finest("Killing " + this);
    }
  }


  /**
   *
   */

  public void kill(CricketPresence p, boolean forceKill) {
    if (forceKill) {
      p.player().removePresence(p);
    }
    try {
      if (Game.game(p.getGID()) != null) {
        if (p.isPlayer()) {
          Game g = Game.game(p.getGID());
          // p is a player remove him from the game
          if (!g.type().isTourny()) {
            Response[] r = leaveGameOnly(p, forceKill);
            p.unsetDisconnected();
                        GameProcessor.deliverResponse(r);
            p.setDisconnected();

            _cat.warning("Removing presence " + p + " from game " + p.getGID());

            // update the real chips
            //_dbPlayer.setPoints(points());
            //_dbPlayer.setPointsTs(System.currentTimeMillis());
            _cat.finest(_dbPlayer.toString());
            //_dbPlayer.updateStats();

            //change the win amount and wagered amount only for real games
            // in login session
            if (_loginSession != null) {
              _loginSession.setLogoutTime(new java.util.Date());
              _loginSession.setWinAmount(_loginSession.getWinAmount());
              //_loginSession.setEndWorth(points());
              _loginSession.setWagered(_loginSession.getWagered());
              //_cat.finest(_loginSession);
              _loginSession.updateLogout();
              _loginSession = null;
            }

          }
          else { //it is a tourny
            // check if player or handler is null
            p.setDisconnected();
            _cat.finest("Setting the presence as disconnected .." + p);
            _handler.setDisconnected();
          }
        }
        else if (p.isObserver()) {
          // no response is generated
          leaveWatch(p);
          //new Exception().printStackTrace();
          _cat.warning("Removing observer " + p + "from game " + p.getGID());
        }
        else {
          p.player().removePresence(p);
        }
      }
    }
    catch (Exception e) {
      _cat.warning("Name=" + name()+  e);
    }
  }

  public long getGRID(int tid) {
    return ((CricketPresence) _presence_registry.get("" + tid)).getGRID();
  }

  public void setGRID(int tid, long grid) {
    ((CricketPresence) _presence_registry.get("" + tid)).setGRID(grid);
  }

  public boolean refreshBankroll() {
    /*try {
      _dbPlayer.refreshBankroll();
      ResponseBuyChips gr = new ResponseBuyChips(1, _dbPlayer.getPlayChips(),
                                                 _dbPlayer.getPlayBankroll(),
                                                 _dbPlayer.getRealChips(),
                                                 _dbPlayer.getRealBankroll(),
                                                 -1);
      _cat.finest(gr);
      _handler.putResponse(gr);
      return true;
         }
         catch (DBException e) {
      e.printStackTrace();
      return false;
         }*/
    return false;
  }

  public void setAuthenticated() {
    _isAuthenticated = true;
  }

  public void unsetAuthenticated() {
    _isAuthenticated = false;
  }

  public boolean isAuthenticated() {
    return _isAuthenticated;
  }

  public void handler(MMOGHandler h) {
    _handler = h;
  }

  public MMOGHandler handler() {
    return _handler;
  }

  public LoginSession loginSession() {
    return _loginSession;
  }

  public void loginSession(LoginSession ls) {
    _loginSession = ls;
  }

  public String session() {
    return _session;
  }

  public String toString() {
    return super.toString() + ", PresenceCount=" + _presence_registry.size() +
        ", Dead=" + _dead;
  }

}
