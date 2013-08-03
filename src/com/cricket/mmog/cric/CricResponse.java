package com.cricket.mmog.cric;

import java.util.*;
import java.util.logging.Logger;
import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.resp.*;
import com.agneya.util.Utils;

import com.cricket.mmog.cric.impl.ScheduledMatch;
import com.cricket.mmog.resp.Response;

public class CricResponse
    implements Response {
  // set the category for logging
  static Logger _cat = Logger.getLogger(CricResponse.class.getName());

  /***
   * Response interface implementation
   */

  public void setGame(Game g) {
    this._g = (Cricket) g;
  }

  public Game getGame() {
    return _g;
  }

  public void addRecepient(CricketPresence p) {
    _map.put(p, "no-command-set-yet");
  }

  public void addRecepients(CricketPresence[] p) {
    for (int k = p.length; --k > -1; ) {
      _map.put(p[k], "no-command-set-yet");
    }
  }

  public void addObservers(CricketPresence[] observers) {
    throw new IllegalStateException("No observers in BJ");
  }

  public CricketPresence[] observers() {
    CricketPresence[] pl = new CricketPresence[_observers.size()];
    return (CricketPresence[]) _observers.toArray(pl);
  }

  public CricketPresence removeRecepient(CricketPresence p) {
    return (CricketPresence) _map.remove(p);
  }

  public boolean recepientExists(CricketPresence p) {
    return _map.containsKey(p);
  }

  public void broadcast(CricketPresence[] p, String command) {
    //Object v ;
    _broadcast = command;
    if (p == null) {
      return;
    }
    for (int k = p.length; --k > -1; ) {
      if (_map.containsKey(p[k])) {
        _map.put(p[k], command + "," + _map.get(p[k])); // string concat ?
      }
      else {
        _map.put(p[k], command);
      }
    }
  }

  public void setCommand(CricketPresence p, String command) {
    //_cat.finest("set Command " + command + " for player " + p);
    //map.put( p, command ) ;
    if (_map.containsKey(p)) {
      command = _map.get(p) + "," + command;
    }
    _map.put(p, command);
  }

  public String getBroadcast() {
    return _broadcast; //...
    // revisit later
  }

  public String getCommand(CricketPresence p) {
    return (String) _map.get(p);
  }

  public CricketPresence[] recepients() {
    CricketPresence[] pl = new CricketPresence[_map.size()]; // optimistic size prediction
    return (CricketPresence[]) _map.keySet().toArray(pl);
  }

  /***
   * Response interface implementation ends
   */

  public CricResponse(Cricket g) {
    this._g = g;
    int pos = 0;
    _observers = g.observers();
    for (Iterator i = _observers.iterator(); i.hasNext(); ) {
      _cat.finest("Observer = " + i.next());
    }
    _allPlayers = (CricketPresence[]) g.allPlayers(pos);
    ++msgGID;
  }

  /*
    Enhances the command string with information that the poker normally
    would not either have or concern itself with.
   Interface Enahncer has method enhance( String command, Player invokee, Game g )
   */
  public String enhanceCommand(String command, CricketPresence p,
                               Cricket g /*Enhancer e*/) {
    return null; // e.enhance( command, invokee, g )
  }

  protected StringBuffer header() {
    StringBuffer buf = new StringBuffer();
    buf.append("grid=").append(_g.grid());
    buf.append(",name=").append(_g.name());
    buf.append(",type=").append(_g.type().intVal());
    buf.append(",state=").append(_g.state().intVal());
  if (_g instanceof ScheduledMatch) {
    ScheduledMatch gm = (ScheduledMatch) _g;
    buf.append(",delta=").append(gm._delta);
    buf.append(",tstate=").append(gm._tstate);
    buf.append(",date=").append(Utils.getFormattedDate(gm.
        getScheduleDate()));
    buf.append(",TeamAPlayers=");
    for (int i = 0; i < gm._ta.length; i++) {
      buf.append(gm._ta[i]).append("'");
    }
    buf.append(",TeamBPlayers=");
    for (int i = 0; i < gm._tb.length; i++) {
      buf.append(gm._tb[i]).append("'");
    }
  }
    buf.append(",fees=").append(_g._fees);
    buf.append(",buyin=").append(_g._buyin);
    buf.append(",bpp=").append(_g._ball_per_player);
    buf.append(",pot=").append(_g._pot);
    if (_g._pitch._batting == null || _g._pitch._fielding == null){
      buf.append(",A=").append(_g._pitch._teamA._team_name);
      buf.append(",B=").append(_g._pitch._teamB._team_name);
    }
    else {
      buf.append(",A=").append(_g._pitch._batting._team_name);
      buf.append(",B=").append(_g._pitch._fielding._team_name);
    }
    buf.append(",runs=").append( _g._runs);
    buf.append(",balls=").append( _g._balls);
    buf.append(",");
    return buf;
  }

  // based on current.
  protected StringBuffer lastMoveDetails() {
    CricketPresence v[] = _g.getLastMovePlayers();
    if (v == null || v.length == 0) {
      return new StringBuffer("");
    }
    else {
      StringBuffer buf = new StringBuffer().append("last-move=");
      for (int i = 0; i < v.length; i++) {
        if (v[i].team()==null) continue;
          buf.append(v[i].pos());
          buf.append("|").append(v[i].teamName());
          buf.append("|").append(v[i].name()).append("|").
              append(Moves.stringValue(v[i].lastMove()));
          buf.append("|").append(Utils.getRoundedString(v[i].getWorth())).
              append("`");
      }
      buf.deleteCharAt(buf.length()-1);
      buf.append(",");
      return buf;
    }
  }

    protected StringBuffer pitchDetails() {
      return new StringBuffer(_g._pitch.stringValue());
    }

    protected StringBuffer playerStats(CricketPresence p){
      if (p==null)return new StringBuffer("");
      StringBuffer sbuf = new StringBuffer("ps=");
      //sbuf.append(p.player().getDBCricketPlayer().playerStat()).append(",");
      return sbuf;
    }

    protected StringBuffer playerTargetPosition(CricketPresence p) {
      StringBuffer buf = new StringBuffer();
      buf.append("tp=").append(p.pos()).append("|").append(p.teamName()).
          append(",");
      return buf;
    }

    protected StringBuffer setMove(CricketPresence p, int pos, String team, long move,
                                   int duration) {
      StringBuffer buf = new StringBuffer();
      buf.append(pos).append("|");
      buf.append(team).append("|");
      buf.append(p.name()).append("|");
      buf.append(Moves.stringValue(move)).append("|");
      buf.append(duration).append("`");
      _g.initNextMove(p, move, duration);
      _cat.info(p + " Move = " + Moves.stringValue(move));
      return buf;
    }

//@todo string operations and method signatures to have stringbuffer instead of string
    int gameId;
    Cricket _g;
    HashMap _map = new HashMap();
    protected CricketPresence[] _allPlayers;

    Set _observers;
    String _broadcast;
    volatile static int msgGID = 0;
  }
