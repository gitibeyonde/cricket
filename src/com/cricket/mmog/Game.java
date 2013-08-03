package com.cricket.mmog;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import com.agneya.util.*;

import com.cricket.mmog.gamemsg.*;
import com.cricket.mmog.resp.*;
import com.cricket.mmog.cric.GameState;
import com.cricket.mmog.cric.util.MoveParams;

public abstract class Game
    extends Observable
    implements Serializable {

  static {
    try {
      Configuration conf = Configuration.instance();
      int timeout = conf.getInt("Server.maintenance.heartbeat");
      Timer t = new Timer();
      t.schedule(new GameSummary(), 0, timeout / 2);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // set the category for logging
  static Logger _cat = Logger.getLogger(Game.class.getName());

  public abstract Response[] details();

  public abstract Response[] details(CricketPresence p);

  public abstract Response[] join(CricketPresence p);

  public abstract Response[] start();

  public abstract void destroy();

  public abstract Response[] leave(CricketPresence p, boolean timeout);

  public abstract Response[] observe(CricketPresence p);

  public abstract Response[] leaveWatch(CricketPresence p);

  // to promote a poker observer to player status
  public abstract Response[] promoteToPlayer(CricketPresence observer);

  public abstract CricketPresence[] allPlayers(int startPos);

  public abstract CricketPresence[] activePlayers(int startPos);

  public abstract CricketPresence[] activePlayers();

  public abstract Response[] summary();

  public abstract CricketPresence[] inActivePlayers(int startPos);

  public abstract CricketPresence[] newPlayers(int startPos);

  public abstract Response[] chat(CricketPresence p, String message);

  public Game() {
    _rng = new Rng();
  }

  public String name() {
    return _name;
  }

  public long grid() {
    return _grid;
  }

  public void grid(long grid) {
    _grid = grid;
    _responseId = 0;
  }

  public Calendar startTime() {
    return _start;
  }


  public void startTime(Calendar d) {
    _start = d;
  }

  public static Game game(String gameId) {
    return (Game) map.get(gameId);
  }

  public static Game[] listAll() {
    return (Game[]) map.values().toArray(new Game[] {});
  }

  public static Game remove(String gameId) {
    synchronized (Game.class) {
      return (Game) map.remove(gameId);
    }
  }

  public static Game remove(Game g) {
    String id = g.name();
    return Game.remove(id);
  }

  public static void add(Game g) {
    g._last_move_ts = System.currentTimeMillis();
    synchronized (Game.class) {
      map.put(g.name(), g);
    }
  }

  public void setTimeDelta(){
    _last_move_ts = System.currentTimeMillis();
  }

  public long lastMoveDelta(){
    return System.currentTimeMillis() - _last_move_ts;
  }

  public static Response[] handle(Message[] m) {
    Vector v=new Vector();
    for (int i=0;i<m.length;i++){
      Response r[]=handle(m[i]);
      for (int j=0;j<r.length;j++){
        v.add(r[j]);
      }
    }
    return (Response [])v.toArray(new Response[v.size()]);
  }

  public static Response[] handle(Message m) {
    Game g = (Game) map.get(m.gameId());
    if (g == null) {
      _cat.severe(m.gameId() + " game does not exists ");
      return null;
    }
    synchronized (g) {
      g._last_move_ts = System.currentTimeMillis();
      return m.interpret();
    }
  }

  public void setInquirer(CricketPresence player) {
    _inquirer = player;
  }

  public void setMarker(CricketPresence player) {
    _marker = player;
  }

  public CricketPresence marker() {
    return _marker;
  }

  public void stateObserver(Observer obs) {
    if (obs == null) {
      throw new IllegalArgumentException();
    }
    _stateObserver = obs;
  }

  public Observer stateObserver() {
    return _stateObserver;
  }

  public synchronized int responseId() {
    return++_responseId;
  }

  public GameType type() {
    return _type;
  }

  public GameState state(){
    return _state;
  }

  /*
     kill signals to immediately end the game and remove it from list of hosted games.
     If a run is in progress, participants are refunded bets.
   */
  public static synchronized void kill(Game g) {
    synchronized (g) {
      Game.remove(g);
      g.destroy();
    }

  }

  public static synchronized void killAll() {
    Game[] games = Game.listAll();
    for (int i = 0; i < games.length; i++) {
      Game.kill(games[i]);
    }
  }

  public void suspend() {
    _keepRunning = false;
  }

  public Response[] resume() {
    _keepRunning = true;
    return this.start();
  }

  public boolean running() {
    return _keepRunning;
  }

  public String tournyId() {
    return tournyId;
  }

  public void initNextMove(CricketPresence p, long move, int timeout) {
    _nextMovePlayers.add(p);
    _nextMove += move;
    p.initNextMove(move, timeout);
  }

  public boolean checkNextMove(long move, CricketPresence p) {
    for (Iterator i = _nextMovePlayers.iterator(); i.hasNext(); ) {
      _cat.finest("Moves expected from " + i.next());
    }
    _cat.finest("Moved " + p);
    if (_nextMovePlayers.contains(p)) {
      // return the presence from the set
      if (p.checkNextMove(move)) {
        _cat.finest("Valid move " + Moves.stringValue(move));
        p.resetNextMove();
        _nextMovePlayers.remove(p);
        return true;
      }
      else {
        return false;
      }
    }
    else {
      return false;
    }
  }

  public String[] affiliate() {
    return (String[]) _affiliate.toArray(new String[_affiliate.size()]);
  }

  public StringBuffer affiliateString() {
    StringBuffer sb = new StringBuffer();
    for (Iterator i = _affiliate.iterator(); i.hasNext(); ) {
      sb.append(i.next()).append("|");
    }
    return sb.length() > 1 ? sb.deleteCharAt(sb.length() - 1) : sb;
  }

  public void initAffiliate(String[] aff) {
    for (int i = 0; i < aff.length; i++) {
      _affiliate.add(aff[i]);
    }
  }

  public boolean isAffiliate(String aff) {
    for (Iterator i = _affiliate.iterator(); i.hasNext(); ) {
      if (i.next().equals(aff)) {
        return true;
      }
    }
    return false;
  }

  public String[] partner() {
    return (String[]) _partner.toArray(new String[_partner.size()]);
  }

  public StringBuffer partnerString() {
    StringBuffer sb = new StringBuffer();
    for (Iterator i = _partner.iterator(); i.hasNext(); ) {
      sb.append(i.next()).append("|");
    }
    return sb.length() > 1 ? sb.deleteCharAt(sb.length() - 1) : sb;
  }

  public void initPartner(String[] frnd) {
    for (int i = 0; frnd != null && i < frnd.length; i++) {
      _partner.add(frnd[i]);
    }
  }

  public boolean isPartner(String part) {
    for (Iterator i = _partner.iterator(); i.hasNext(); ) {
      if (i.next().equals(part)) {
        return true;
      }
    }
    return false;
  }

  public boolean isInvited(CricketPresence p) {
    if (_partner.size() == 0) {
      return true; // can play uninvited
    }
    else {
      _cat.info("Should be a invited player");
      return _partner.contains(p.name());
    }
  }

  public boolean isInvited(String name) {
    if (_partner.size() == 0) {
      return true; // can play uninvited
    }
    else {
      _cat.info("Should be a invited player");
      return _partner.contains(name);
    }
  }

  public Set invited() {
    return _partner;
  }

  public void invite(String[] p) {
    for (int i = 0; i < p.length; i++) {
      _partner.add(p[i]);
    }
  }

  public void addInvite(CricketPresence p) {
    _partner.add(p.name());
  }

  public void setRank(int rank) {
    _rank = rank;
  }

  public boolean isGRIDOver(long grid){
    return (_grid != grid) || !_inProgress;
  }

  public void addLastMovePresence(CricketPresence p){
    _lastMovePlayer.add(p);
  }

  public CricketPresence[] getLastMovePlayers(){
    return (CricketPresence [])_lastMovePlayer.toArray(new CricketPresence[_lastMovePlayer.size()]);
  }

  public void resetLastMovePlayer(){
    _lastMovePlayer.removeAllElements();
  }

  public void removeInvite(CricketPresence p) {
    _partner.remove(p.name());
  }

  public void resetInvite() {
    _partner = Collections.synchronizedSet(new HashSet());
  }

  public void reset(){
    _nextMove=0;
    _nextMoveAmt=null;
    _nextMovePlayers.clear();
  }

  public boolean isRunning(){
    return _inProgress;
  }

  public Rng _rng;
  public String _name="";
  protected long _grid;
  protected Calendar _start;
  protected long _creation_time;
  public long _last_move_ts;
  public int _fees;
  public int _buyin;

  protected CricketPresence _inquirer;
  public CricketPresence _current;
  protected Vector _lastMovePlayer=new Vector();
  public CricketPresence _nextPlayer;
  public long _nextMove;
  public double[][] _nextMoveAmt = new double[64][2];

  public Set _nextMovePlayers = Collections.synchronizedSet(new HashSet());
  public GameType _type;
  public GameState _state = new GameState(GameState.OPEN);
  public static HashMap map = new HashMap();
  protected Observer _stateObserver;
  private CricketPresence _marker; // player *next* to marker will play next

  public boolean _keepRunning = true; // by default a game will run
  public volatile boolean _inProgress;
  protected String tournyId;

  private Set _affiliate = Collections.synchronizedSet(new HashSet());
  private Set _partner = Collections.synchronizedSet(new HashSet());

  // game level
  private int _rank = -1;

  // debug var
  volatile int _responseId;

  public static void main(String[] args) {
    double amt1 = 10.020000003;
    double amt2 = 10.02;

    System.out.println( (Utils.getRounded(amt1) == Utils.getRounded(amt2) ?
                         "True" : "False"));

  }

}
