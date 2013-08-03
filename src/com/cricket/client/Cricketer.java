package com.cricket.client;

import com.cricket.common.message.Command;
import com.cricket.common.message.CommandGameDetail;
import com.cricket.common.message.CommandGameList;
import com.cricket.common.message.CommandGetChipsIntoGame;
import com.cricket.common.message.CommandLogin;
import com.cricket.common.message.CommandMove;
import com.cricket.common.message.CommandPlayerShift;
import com.cricket.common.message.CommandRegister;
import com.cricket.common.message.GameEvent;
import com.cricket.common.message.Response;
import com.cricket.common.message.ResponseConfig;
import com.cricket.common.message.ResponseFactory;
import com.cricket.common.message.ResponseGameEvent;
import com.cricket.common.message.ResponseGameList;
import com.cricket.common.message.ResponseLogin;
import com.cricket.common.message.ResponseMessage;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import java.util.logging.Logger;

import com.agneya.util.*;
import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.mmog.GameType;
import com.cricket.mmog.PlayerStatus;
import com.cricket.mmog.cric.util.LineRH;
import com.cricket.mmog.cric.util.Coordinate;


/**
 * Shill client
 *
 */

public class Cricketer implements Runnable {
  // set the category for logging
  static Logger _cat = Logger.getLogger(Cricketer.class.getName());

  Vector _players;
  ResponseGameList _rtd;
  Selector _selector;
  int _density;
  Rng _rng;
  TableMap[] _tm;
  int DELAY_MS = 0;
  int DENSITY = 0;
  Response _responseStack[];
  int _htbt;
  Player _dummy;
  int _tc;

  Configuration _conf = null;
  /**
   * runner listens to responses from the poker server
   */private Thread _runner = null;
  boolean _keepListening;

  public Cricketer() {
    _players = new Vector();
    //initialize the RNG
    _rng = new Rng();
  }

  public void startShills() throws Exception {
    //initialize the vector with player data
    // random for now
    _conf = Configuration.getInstance();
    DELAY_MS = Integer.parseInt((String) _conf.get("Shill.Delay"));
    _density = Integer.parseInt((String) _conf.get("Shill.Density"));
    _htbt = _conf.getInt("Server.maintenance.heartbeat");
    _responseStack = new Response[8 * _density];
    _selector = Selector.open();
    _dummy = new Player("dummy", "dummy");
    // start a heartbeat timer thread
    HeartBeat hb = new HeartBeat();
    Timer t = new Timer();
    t.schedule(hb, 0, _htbt / 2);
    int pn_count = player_names.length;
    _tc = tableCount();
    _cat.finest("Number of matches = " + _tc);
    int pcount = _tc * _density;
    if (pcount > pn_count) {
      _cat.warning(pcount +
                 " Player names not available - Unable to initialize players " +
                 pn_count);
    }
    String temp_pn[] = new String[pn_count];
    pn_count = 0;
    for (int i = 0; i < player_names.length; i++) {
          temp_pn[pn_count++] = player_names[i];
    }

    String pn[] = new String[pn_count];
    for (int i=0;i<pn_count;i++){
      String temp = null;
      int index=0;
      while (temp == null){
        index = _rng.nextIntBetween(0, pn_count);
        temp = temp_pn[index];
      }
      temp_pn[index]=null;
      pn[i] = temp;
    }


    _tm = tableMap();
    if (_tm.length == 0) {
      _cat.warning("There are no cricket games running");
      System.exit( -1);
    }
    
    String gid;

    _cat.finest("Pcount=" + pcount);
    try {
      for (int j = 0; j < _tc; j++) {
        _cat.finest(_tm[j].toString());
        gid = _tm[j]._gname;
        int pc = _tm[j]._teamSize;
        int seat_player=1;
        if (pc == 1){
          seat_player = 1;
        }
        else if (pc == 4 ){
          seat_player = 2;
        }
        else if (pc == 6 ){
          seat_player = 2;
        }
        if (_tm[j]._gtype >= 16 ){
          seat_player ++;
          if (pc==6){
            seat_player ++;
          }
        }
        _cat.finest(seat_player + " To seat players = " + pc);
        for (int k = 0; k < pc; k++) {
          if (pcount == -1) {
            break;
          }
          pcount--;
          addPlayer(pn[pcount], pn[pcount], gid, k, _tm[j]._teamA);

          pcount--;
          seat_player--;
          if (pcount == -1 || seat_player == 0) {
            break;
          }
          addPlayer(pn[pcount], pn[pcount], gid, k, _tm[j]._teamB);
        }

      }
    }
    catch (Exception e) {}

    _runner = new Thread(this);
    _runner.start();
  }

  public void run() {
    boolean keepLooking = true;
    Player p = null;
    SelectionKey key = null;
    while (keepLooking) {
      try {
        _selector.selectNow();
        // Now we deal with our incoming data / completed writes...
        Set keys = _selector.selectedKeys();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
          key = (SelectionKey) i.next();
          i.remove();
          if (key.isReadable()) {
            int top = 0;
            p = (Player) key.attachment();
            String response = p.read();
            if (response == null) {
              continue;
            }
            ResponseFactory rf = new ResponseFactory(response);
            Response r = rf.getResponse();
            if (r.getResult() != 1) {
              continue;
            }
            if (r._response_name == Response.R_MOVE) {
              p._rge.add((ResponseGameEvent) r);
              if (!p.running) {
                //_cat.info("CREATING NEW THREAD..."+p._name);
                new Thread(p).start();
              }
            }
            else if (r._response_name == Response.R_MESSAGE) {
              ResponseMessage rm = (ResponseMessage) r;
              _cat.info("Message =" + rm.getMessage());
            }
            else if (r._response_name == Response.R_LOGOUT) {
              _cat.warning(" Logged out " + p);
              key.cancel();
              _players.remove(p);
            }
            else if (r._response_name == Response.R_GAMEDETAIL) {
              if (r instanceof ResponseGameList) {
                ResponseGameList rm = (ResponseGameList) r;
                _cat.info("Message =" + rm);
              }
            }
            else if (r._response_name == Response.R_PING) {
              // ping received
            }
            else if (r._response_name == Response.R_PLAYERSHIFT) {
              // ping received
            }

            else {
              _cat.warning(
                  "SHILL: FATAL: Unknown event received from the server " + r);
            }
          }
        }
        Thread.currentThread().sleep(200);
      }
      catch (IOException ex) {
        _cat.warning(" Removing player " +ex);
        key.cancel();
        _players.remove(p);
      }
      catch (Exception ex) {
        _cat.warning("Logging out the errant player "+ ex);
        key.cancel();
        _players.remove(p);
        try {
          p.logout();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Player class
   *
   */
  public class Player implements Runnable {
    String _password;
    String _name;
    PlayerStatus _status;
    public String _tid = "";
    public int _grid = -99;
    public int _pos = -99;
    public String _team = "";
    Command _out;
    SocketChannel _channel;
    String _session = null;
    ByteBuffer _h, _b, _o;
    public int _wlen = -1, _wseq = 0, _rlen = -1, _rseq = -1;
    String _comstr;
    protected boolean _dead;
    protected long _last_read_time;
    protected long _last_write_time;
    double _points = 0;
    boolean running = false;
    Vector _rge = new Vector();

    public Player(String name, String pass) throws Exception {
      Configuration _conf = Configuration.getInstance();
      int _port = Integer.parseInt((String) _conf.get("Network.port"));
      String _server = (String) _conf.get("Network.server.ip");
      _channel = SocketChannel.open(new InetSocketAddress(_server, _port));
      _channel.configureBlocking(true);
      _session = connect();
      _name = name;
      _password = pass;
    }

    public void run() {
      ResponseGameEvent rge = null;
      running = true;
      while (_rge.size() > 0) {
        //_cat.finest("MOVE FOR : "+_name+" Vector size is "+_rge.size());
        try {
          rge = ((ResponseGameEvent) _rge.remove(0));
          processMove(rge);
        }
        catch (Exception e) {
          running = false;
          //_cat.warning("Invalid game event "+e);
          e.printStackTrace();
          return;
        }
      }
      //_cat.info("Closing Thread...."+_name);
      running = false;
    }

    public String connect() throws Exception {
      //System.out.println("Connecting ....");
      Command ge = new Command(null, Command.C_CONNECT);
      _cat.finest("Connect req " + ge.toString());
      write(ge.toString());
      String s = readFull();
      _cat.finest("Connect resp " + s);
      Response r = new Response(s);
      return r.session();
    }

    public String reconnect() throws Exception {
      //System.out.println("ReConnecting ....");
      Command ge = new Command(_session, Command.C_CONNECT);
      _cat.finest("Connect req " + ge.toString());
      write(ge.toString());
      String s = readFull();
      _cat.finest("Connect resp " + s);
      Response r = new Response(s);
      return r.session();
    }

    public String readFull() {
      //_cat.finest("Read full");
      String s = read();
      while (s == null) {
        s = read();
        try {
          Thread.currentThread().sleep(200);
        }
        catch (Exception e) {
          //ignore
        }
      }
      return s;
    }

    public synchronized String read() {
      try {
        if (_dead) {
          return null;
        }
        _last_read_time = System.currentTimeMillis();
        if (readHeader() && readBody()) {
          return _comstr;
        }
      }
      catch (IOException e) {
        _cat.warning(_name + " Marking client as dead ");
        _dead = true;
        return null;
      }
      catch (Exception e) {
        _cat.warning(_name + " Garbled command "+ e);
        _dead = true;
        return null;
      }
      return null;
    }

    public boolean readHeader() throws IOException {
      int r = 0;
      if (_rlen != -1) {
        return true;
      }
      if (_h == null) {
        _h = ByteBuffer.allocate(8);
        _h.clear();
      }
      r = _channel.read(_h);
      if (_h.hasRemaining()) {
        _cat.finest(_name + "  Partial header read " + r);
        if (r == -1) {
          _dead = true;
          _cat.warning(_name +
                     " Marking the client dead as the channel is closed  ");
          new Exception().printStackTrace();
        }
        return false;
      }
      _h.flip();
      _rseq = _h.getInt();
      _rlen = _h.getInt();
      _h = null;
      //_cat.finest(" Len = " + _rlen);
      return true;
    }

    public boolean readBody() throws IOException {
      int r = 0;
    
        if (_b == null) {
          _b = ByteBuffer.allocate(_rlen);
          _b.clear();
        }
        r = _channel.read(_b);
        if (_b.hasRemaining()) {
          if (r == -1) {
            _dead = true;
          }
          return false;
        }
        _b.flip(); // read complete
        _comstr = new String(_b.array());
        resetRead();
      

      return true;
    }

    private void resetRead() {
      _h = null;
      _b = null;
      _rlen = -1;
      _rseq = -1;
    }

    public synchronized boolean write(String str) {
      //_cat.finest("In write");
      //_cat.finest("Writing = " + str);
      try {
       
      _o = ByteBuffer.allocate(8 + str.length());
      _o.putInt(_wseq++);
      _o.putInt(str.length());
      _o.put(str.getBytes());
        
        _o.flip();
        int l = _channel.write(_o);
        while (_o.remaining() != 0) {
          _channel.write(_o);
        }
        //_cat.finest("Written = " + str);
        _o = null;
        _dead = false;
      }
      catch (IOException e) {
        _dead = true;
        _cat.warning(_name +
                  " Marking client as dead because of IOException during write");
        e.printStackTrace();
        return false;
      }
      return true;
    }

    public Response login() throws Exception {
      _channel.configureBlocking(true);
      CommandLogin ge = new CommandLogin(_session, _name, _password, "0.0",
                                         "admin");
      _cat.finest(_name + " Login Command " + ge);
      write(ge.toString());
      _last_write_time = System.currentTimeMillis();
      ResponseFactory rf = new ResponseFactory(readFull());
      Response gr = (Response) rf.getResponse();
      if (gr.getResult() == 1) {
        _points = ((ResponseLogin) gr).getPoints();
      }
      _cat.finest(_name + " Login Response " + gr);
      return gr;
    }

    public void logout() throws Exception {
      Command ge = new Command(_session, Command.C_LOGOUT);
      _cat.finest(_name + " Logout Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
    }

    public void turnDeaf() throws Exception {
      Command ge = new Command(_session, Command.C_TURN_DEAF);
      _cat.finest(_name + " TURN DEAF " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
    }

    public Response config() throws Exception {
      _channel.configureBlocking(true);
      Command ge = new Command(_session, Command.C_CONFIG);
      _cat.finest(_name + " Config Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
      ResponseFactory rf = new ResponseFactory(readFull());
      ResponseConfig gr = (ResponseConfig) rf.getResponse();
      _cat.finest(_name + " Config Response " + gr);
      return gr;
    }

    public Response register() throws Exception {
      CommandRegister ge = new CommandRegister(_session, _name, _password,
                                               _name + "@test.com", (byte) 0,
                                               "", "");
      _cat.finest(_name + " Register Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
      Response gr = new Response(readFull());
      _cat.finest(_name + " Register Response " + gr);
      return gr;
    }

    public Response addObserver(String tid) throws Exception {
      _channel.configureBlocking(true);
      CommandGameDetail ge = new CommandGameDetail(_session, tid);
      _cat.finest(_name + " Add Observer Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
      Response gr = new Response(readFull());
      _cat.finest(_name + " Add Observer Response " + gr);
      return gr;
    }

    public void join(String tid, int pos, String team) throws Exception {
      _tid = tid;
      _pos = pos;
      _team = team;
      CommandMove ge = new CommandMove(_session, Command.M_SIT_IN, _points, tid,
                                       _grid);
      ge.setPlayerPosition(pos);
      ge.setPlayerTeam(team);
      _cat.finest(_name + " Join Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
    }

    public Response getMoneyIntoGame(String tid) {
      CommandGetChipsIntoGame ge;
      ge = new CommandGetChipsIntoGame(_session, tid, 5000);
      _cat.finest(_name + " Get Chips  Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
      Response gr = new Response(readFull());
      _cat.finest(_name + " Get Chips Response " + gr);
      return gr;

    }

    public void move(String mov, MoveParams omv, double amt) throws Exception {
      int mov_id = -99;
      MoveParams mv = null;
      if (mov.equals("join")) {
        mov_id = Command.M_JOIN;
      }
      else if (mov.equals("toss")) {
        mov_id = Command.M_TOSS;
      }
      else if (mov.equals("head")) {
        mov_id = Command.M_HEAD;
      }
      else if (mov.equals("tail")) {
        mov_id = Command.M_TAIL;
      }
      else if (mov.equals("bowl")) {
        mv = MoveParams.generate(MoveParams.BOWL);
        mov_id = Command.M_BOWL;
      }
      else if (mov.equals("fielding")) {
        mov_id = Command.M_FIELDING;
      }
      else if (mov.equals("batting")) {
        mov_id = Command.M_BATTING;
      }
      else if (mov.equals("bat")) {
        mv = MoveParams.generate(MoveParams.BATS);
        _cat.info("Batting=" + mv);
        if (omv != null) {
          mv._spin = omv._spin;
        }
        else {
          mv._spin = 0;
        }
        if (Math.random() < 0.1) {
          mv._timing = 0;
        }
        if (Math.random() > 0.90) {
          mv._timing = 7;
        }
        mov_id = Command.M_BAT;
      }
      else if (mov.equals("field")) {
        _cat.info("Dir=" + omv._direction + "player status " + _status);
        LineRH l = new LineRH(new Coordinate(507, 140, 0), omv._direction);
        int fx = 310;
        int fy = 300;
        if ((_status.getPlayerType() & PlayerStatus.LF1) > 0) {
          fx = 160;
          fy = 200;
        }
        else if ((_status.getPlayerType() & PlayerStatus.LF2) > 0) {
          fx = 50;
          fy = 500;
        }
        else if ((_status.getPlayerType() & PlayerStatus.LF3) > 0) {
          fx = 150;
          fy = 650;
        }
        else if ((_status.getPlayerType() & PlayerStatus.LF4) > 0) {
          fx = 200;
          fy = -70;
        }
        else if ((_status.getPlayerType() & PlayerStatus.LF5) > 0) {
          fx = 400;
          fy = -250;
        }
        else if ((_status.getPlayerType() & PlayerStatus.RF1) > 0) {
          fx = 800;
          fy = 200;
        }
        else if ((_status.getPlayerType() & PlayerStatus.RF2) > 0) {
          fx = 900;
          fy = 500;
        }
        else if ((_status.getPlayerType() & PlayerStatus.RF3) > 0) {
          fx = 800;
          fy = 650;
        }
        else if ((_status.getPlayerType() & PlayerStatus.RF4) > 0) {
          fx = 760;
          fy = -50;
        }
        else if ((_status.getPlayerType() & PlayerStatus.BOWLER) > 0) {
          fx = 490;
          fy = 240;
        }
        fy += 75;
        fx += 50;

        // find the closest contact point
        Coordinate cc = l.closestPoint(new Coordinate(fx, fy, 0));
        if (cc._y < 145 && (_status.getPlayerType() & PlayerStatus.BOWLER) > 0) {
          cc._y = 160;
        }
        int dx = (int) (cc._x - fx);
        int dy = (int) (cc._y - fy);

        _cat.info("Fx=" + fx + ", Fy=" + fy);
        _cat.info("CCx=" + cc._x + ", CCy=" + cc._y + ", Dx=" + dx + ", Dy=" +
                  dy);
        CommandPlayerShift cps;

        if (dy > 5) {
          while (dy > 0) {
            cps = new CommandPlayerShift(_session, _tid,
                                         _status.getPlayerTypeString(), 0, 5, 0);
            _cat.finest(cps.toString());
            Thread.currentThread().sleep(20);
            write(cps.toString());
            dy -= 5;
          }
        }


        if (dx < -5) {
          while (dx < 0) {
            cps = new CommandPlayerShift(_session, _tid,
                                         _status.getPlayerTypeString(), -5, 0,
                                         0);
            _cat.finest(cps.toString());
            Thread.currentThread().sleep(20);
            write(cps.toString());
            dx += 5;
          }
        }


        if (dx > 5) {
          while (dx > 0) {
            cps = new CommandPlayerShift(_session, _tid,
                                         _status.getPlayerTypeString(), 5, 0, 0);
            _cat.finest(cps.toString());
            Thread.currentThread().sleep(20);
            write(cps.toString());
            dx -= 5;
          }
        }

        if (dy < -5) {
          while (dy < 0) {
            cps = new CommandPlayerShift(_session, _tid,
                                         _status.getPlayerTypeString(), 0, -5,
                                         0);
            _cat.finest(cps.toString());
            Thread.currentThread().sleep(20);
            write(cps.toString());
            dy += 5;
          }
        }
        mv = MoveParams.generate(MoveParams.FIELD);
        mv.setX((int) cc._x);
        mv.setY((int) cc._y - 50);
        if (Math.random() < 0.8) {
          mv.setFieldAction(54);
        }
        else {
          mv.setFieldAction(50); // CATCH=FIELD
        }
        _cat.finest("FIELD=" + mv);
        mov_id = Command.M_FIELD;
      }
      else if (mov.equals("drinks")) {
        mov_id = Command.M_DRINKS;
      }
      else {
        mov_id = Command.M_ILLEGAL;
      }
      if (mov_id != Command.M_ILLEGAL) {
        _cat.info(this._name + " Making a move = " + mov + ", id = " + mov_id +
                  ", amt = " + amt + ", tid = " + _tid);
        CommandMove cm = new CommandMove(_session, mov_id, amt, _tid, 0, mv);
        cm.setPlayerPosition(_pos);
        _last_write_time = System.currentTimeMillis();
        write(cm.toString());
      }
      else {
        _cat.warning("ILLEGAL MOVE");
      }
    }

    public void processMove(ResponseGameEvent rge) throws Exception {
      _cat.finest("RECEIVED " + this +"\n ResponseGameEvent=" + rge);
      GameEvent ge = new GameEvent();
      ge.init(rge.getGameEvent());
      /**
       * check if the last move was join, if yes update the position and gid
       */String[] last_move = ge.getLastMoveString().split("\\|");
      if (last_move.length > 4 && last_move[2].equals("leave") &&
          last_move[1].equals(_name)) {
        _pos = -1;
        _tid = "";
        _cat.info("Left table " + this +ge);
        return;
      }
      if (last_move.length > 4 && last_move[2].equals("join") &&
          last_move[1].equals(_name)) {
        _pos = Integer.parseInt(last_move[0]);
        _tid = ge.getGameId();
        _cat.info("Changed position " + this +ge);
      }

      String tp = ge.get("tp") != null ? ge.get("tp") : "-1|-1";
      String tpv[] = tp.split("\\|");
      int target_pos = Integer.parseInt(tpv[0]);
      String target_team = tpv[1];
      //_cat.finest("tp = " + target_pos + ", tt=" + target_team);
      if (target_pos != _pos && !target_team.equals(_team)) {
        return;
      }
      //_cat.finest("This pos=" + _pos + ", team=" + _team + ", " +
      //           ge.get("next-move"));

      String pd[][] = ge.getFeildingPlayersDetails();

      for (int i = 0; pd != null && i < pd.length; i++) {
        if (pd[i][2].equals(_name)) {
          _status = new PlayerStatus(Long.parseLong(pd[i][3]));
          _cat.finest(_name + " status is " + _status);
          break;
        }
      }

      //moves
      String all_moves[][] = ge.getNextMove();
      if (all_moves == null) {
        _cat.warning("Moves are null " + ge);
        return;
      }

      Vector v = new Vector();
      for (int i = 0; i < all_moves.length; i++) {
        //_cat.finest("Moves=" + all_moves[i][3]);
        if (all_moves[i][3] == null || all_moves[i][3].equals("wait")) {
          return;
        }
        if (all_moves[i][0].equals(tpv[0]) && all_moves[i][1].equals(tpv[1])) {
          v.add(all_moves[i]);
        }
      }

      if (v.size() == 0) {
        return;
      }

      String[][] moves = (String[][]) v.toArray(new String[v.size()][]);

      for (int i = 0; i < moves.length; i++) {
        _cat.finest(" this moves = " + moves[i][3]);
      }

      if (moves != null && moves.length > 0) {
        // take the first move
        int pos = Integer.parseInt(moves[0][0]);
        String team = moves[0][1];
        if (pos > -1) {
          _cat.finest("pos=" + pos + ", _pos=" + _pos);
          _cat.finest("Team=" + team + ", _team=" + _team);
          if (pos == _pos && _team.equals(team)) {
            _tid = ge.getGameId();
            _grid = ge.getGameRunId();
            // calculate the hand strength
            //int strength = ge.getHandStrength();
            //_cat.finest("Hand strength for " + _name + " is " + strength);
            int random_move = _rng.nextIntLessThan(100);
            int mi = 0;
            if (moves.length <= 2) { // No opt out
              mi = 0;
            }
            else if (moves.length == 3) {
              if (random_move < 50) {
                mi = 0;
              }
              else if (random_move < 100) {
                mi = 1;
              }
              else {
                mi = 2;
              }
            }
            else if (moves.length == 4) { // All-in in NL/PL games
              if (random_move < 40) {
                mi = 0;
              }
              else if (random_move < 50) {
                mi = 1;
              }
              else {
                mi = 2;
              }
            }
            else {
              mi = _rng.nextIntLessThan(5);
            }

            String mov = moves[mi][3];
            String amt_str = "0";
            double amt = -1;
            _cat.info(" Move by " + _name + " is " + mov + ", " + amt_str +
                      ", game=" + _tid + "-" + _grid + ", position=" + _pos +
                      " ," + ", team=" + _team);
            if (amt_str != null && amt_str.length() > 0) {
              int index = amt_str.indexOf("-");
              if (index == -1) {
                amt = Double.parseDouble(amt_str);
              }
              else if (index == 0) {
                _cat.warning("Illegal move amount Move by " + _name + " is " +
                           mov + ", " + amt_str + ", game=" + _tid +
                           ", position=" + _pos + " ," + ge);
              }
              else {
                _cat.finest("Range's first = " + amt_str.substring(0, index));
                amt = Double.parseDouble(amt_str.substring(0, index));
              }
            }
            if (mov.equals("toss") || mov.equals("fielding") ||
                mov.equals("batting") || mov.equals("head") ||
                mov.equals("tail")) {
              //Thread.currentThread().sleep(1000);
            }
            else if (mov.equals("field")) {
              Thread.currentThread().sleep(DELAY_MS);
            }
            else {
              Thread.currentThread().sleep(DELAY_MS);
            }
            String mp = ge.get("md");
            if (mp == null || mp.equals("null")) {
              move(mov, null, amt);
            }
            else {
              move(mov, new MoveParams(mp), amt);
            }
          }
        }
        else if (moves[0][2].equals("wait")) {
          _cat.info("Wait received");
        }
        else {
          _cat.warning("Illegal position received from the server");
        }
      }
    }

    public void setSelectorForRead() throws Exception {
      _channel.configureBlocking(false);
      _channel.register(_selector, SelectionKey.OP_READ, this);
    }

    public ResponseGameList getTableList() throws Exception {
      CommandGameList ge = new CommandGameList(_session, 0x0FF);
      _cat.finest("TableList Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
      ResponseFactory rf = new ResponseFactory(readFull());
      ResponseGameList gr = (ResponseGameList) rf.getResponse();
      _cat.info(_name + " TableList Response " + gr.getGameCount());
      return gr;
    }

    public void heartBeat() throws Exception {
      if (_session != null) {
        //Command ge = new Command(_session, Command.C_HTBT);
        //write(ge.toString());
        Command ge = new Command(_session, Command.C_PING);
        write(ge.toString());
      }
    }

    public void ping() throws Exception {
      if (_session != null) {
      }
    }

    public String toString() {
      StringBuffer sb = new StringBuffer("Player=[Name=");
      sb.append(_name);
      sb.append(", Session=");
      sb.append(_session);
      sb.append(", Game=");
      sb.append(_tid);
      sb.append(", Position=");
      sb.append(_pos);
      sb.append(", Worth=");
      sb.append(_points);
      return sb.toString();
    }

  } // end class Player

  public class HeartBeat extends TimerTask {

    public void run() {
      try {
        Vector to_remove = new Vector();
        // send a heartbeat message
        Enumeration e = _players.elements();
        for (; e.hasMoreElements(); ) {
          Player p = (Player) e.nextElement();
          if (!p._dead) {
            p.heartBeat();
          }
          else {
            //remove the player
            to_remove.add(p);
          }
          long currTime = System.currentTimeMillis();
          if (currTime - p._last_read_time > 400000 &&
              currTime - p._last_write_time > 400000) {
            //to_remove.add(p);
          }
        }
        e = to_remove.elements();
        for (; e.hasMoreElements(); ) {
          Player p = (Player) e.nextElement();
          _cat.info("Disconnecting idle player " + p);
          p.logout();
          //remove the player
          _players.remove(p);
        }
      }
      catch (Exception ex) {
        //do nothing
        _cat.warning("Exception"+ ex);
      }
    }
  } // end HeartBeat class

  public static void main(String[] args) throws Exception {
    // create the singleton configuration object
    Configuration conf = Configuration.instance();
    // BasicConfigurator replaced with PropertyConfigurator.
    String propFile = (String) conf.get("Log4j.propertyFile");
    //PropertyConfigurator.configure(propFile );
   
    Cricketer shill = new Cricketer();
    shill.startShills();
  }

  private static String[] player_names = {"mike", "tom", "saw", "ross", "mark",
                                         "micheal", "peter", "james", "shane",
                                         "andrew", "kevin", "douglas", "harry",
                                         "daily", "robbie", "babbe", "lisa",
                                         "samanta", "rosy", "veronica", "veann",
                                         "lily", "shafted", "hunk", "bossini",
                                         "deisel", "ibebe", "timepass",
                                         "daneal", "jonathan", "gerber", "gary",
                                         "jamie", "parasol", "jason", "alexbaik",
                                         "ravi", "brinda", "bonney", "kapil",
                                         "kambli", "pandu", "nisha", "tasha",
                                         "renu", "sandy", "rohit", "shrisha",
                                         "madhu", "neeral", "neena", "prateek",
                                         "krissh", "kishna", "amit", "lodha",
                                         "ronit", "habib", "sameer", "deo",
                                         "maharaj", "jai", "vinod", "binand",
                                         "suraj", "veeru", "debu", "prerna",
                                         "bajaj", "godfather", "rumbha",
                                         "vikrant", "jaanu", "kallu", "sheena",
                                         "shalu", "dobby", "bens", "manoj",
                                         "abhinav", "shanthi", "shoba",
                                         "sangeetha", "pari", "leela", "kantha",
                                         "savitha", "maya", "ruby", "yakesh",
                                         "nixon", "khandu", "bulund", "kajol",
                                         "devgan", "dutta", "savant",
                                         "bahgmare", "kulkarni", "aniket",
                                         "nikesh", "deol", "karishma", "singh",
                                         "lapat", "lulla", "hamid", "ali",
                                         "phulla", "Srinivas", "dhave", "nag",
                                         "raju", "ranjini", "rushi",
                                         "sucharita", "anurag", "dikshit",
                                         "garg", "mulayam", "koli", "larry",
                                         "john", "bestgirl", "coolcat",
                                         "hamilton", "bobby", "tommy",
                                         "johhney", "sweety", "sasha",
                                         "wildbill", "bill", "bushy", "ruth",
                                         "Scott", "tiger", "roger", "Herd",
                                         "amanda", "loverboy", "dick", "alice",
                                         "shaw", "blackie", "carol", "lady",
                                         "Mavarick", "puller", "cat", "sandra",
                                         "doug", "denise", "oliver", "amy",
                                         "spiderman", "barbie", "shaina",
                                         "lewis", "headly", "batgirl", "danny",
                                         "fanny", "clinton", "lovergirl",
                                         "taxidriver", "pilot", "margarita",
                                         "deadly", "inafix", "maria", "cool",
                                         "bush", "olli", "polli", "cinderella",
                                         "tony", "pony", "rolly", "jellyfish",
                                         "coolcow"
  };


  public boolean seatVacant(int[] tm) {
    for (int i = 1; i < tm[9]; i++) {
      if (tm[i] != -1) {
        return true;
      }
    }
    return false;
  }

  public int tableCount() throws Exception {
    ResponseGameList rtl = null;
    rtl = _dummy.getTableList();
    return rtl.getGameCount();
  }

  private boolean addPlayer(String name, String password, String gid, int pos,
                            String team) throws Exception {
    if (name==null) return false;
    Player p = new Player(name, password);
    _cat.finest("Adding " + p + ", gid=" + gid + ", pos=" + pos + ", team=" +
               team);
    Response r = p.login();
    if (r.getResult() == 29 || r.getResult() == 2) {
      // try to register the player
      r = p.register();
      if (r.getResult() == 0) {
        _cat.warning("Registration failed " + p);
      }
    }
    //else if (r.getResult() != 1 || r.getResult() !=25 /* remove later*/) {
    //  return false;
    //}
    _players.add(p);
    r = p.addObserver(gid);
    p.setSelectorForRead();
    p.join(gid, pos, team);
    return true;
  }

  private boolean addObserver(String name, String password, String gid) throws
      Exception {
    Player p = new Player(name, password);
    Response r = p.login();
    if (r.getResult() == 2) {
      // try to register the player
      r = p.register();
      if (r.getResult() == 0) {
        _cat.warning("Registration failed " + p);
      }
    }
    //else if (r.getResult() != 1 || r.getResult() !=25 /* remove later*/) {
    //  return false;
    //}
    _players.add(p);
    r = p.addObserver(gid);
    p.setSelectorForRead();
    return true;
  }

  private void consoleLoop() {
    System.out.print(
        "GameController application.  Type a command the the '>' prompt,\n" +
        "'quit' to end the server, or 'help' to get a list of commands.\n");
    // Drop into the loop.
    java.io.DataInputStream dis = new java.io.DataInputStream(System.in);
    boolean ok = true;
    while (ok) {
      try {
        String s;
        System.out.print("> ");
        System.out.flush();
        s = dis.readUTF(); // readLine();
        if (s.equals("quit")) {
          ok = false;
        }
        else if (s.startsWith("add")) {

        }
        else if (s.startsWith("remove")) {

        }
        else if (s.startsWith("list")) {

        }
        else if (s.length() > 0) {
          System.out.print("Help for GameServer Controller Commands:\n\n" +
              "add name passord <game-id> <position> - add a shill on specified game\n" +
              "remove name|<game-id>|all - remove shill from specified game\n" +
              "list <game-id>|all - list shills playing on a particular table\n" +
                           "quit    - Exits the server killing all games\n");
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

      public TableMap[] tableMap() throws Exception {
        ResponseGameList rtl = null;
        rtl = _dummy.getTableList();
        //p.config();
        // revise the table count
        int tc = rtl.getGameCount();
        TableMap[] tm = new TableMap[tc];
        for (int i = 0; i < tc; i++) {
          GameEvent ge = rtl.getGameEvent(i);
          tm[i] = new TableMap(ge);
          //System.out.println(tm[i]);
        }
        return tm;
      }
                
    public class TableMap {
        public String _gname;
        public String _teamA, _teamB;
        public int _gtype, _teamSize;
        public GameType _gameType;
        public int _maxPlayer, _minPlayer;
        public double _minbet, _maxbet;
        public int[] _playerDetails;
        public boolean _isReal, _isPrivate;
        public int _bots;
        
        
        public TableMap(GameEvent ge){
             //System.out.println(ge.toString());
              
            //_minbet = new Double(Double.parseDouble(ge.get("ante")));                
            //_maxbet = new Double(Double.parseDouble(ge.get("ante")));
            _gtype = ge.getType();
            _gameType = new GameType(_gtype);
            _teamSize=_gameType.teamSize();
              int maxP = ge.getMaxPlayers();
              _bots = (int) (Math.random() * maxP);
              
            _gname = ge.getGameId();
            _maxPlayer = ge.getMaxPlayers();
            _teamA = ge.get("A");
            _teamB = ge.get("B");
              System.out.println("Max players=" + _maxPlayer);
              _playerDetails =  new int[_maxPlayer];
              String pd[][] = ge.getBattingPlayersDetails();
              if (pd != null) {
                for (int j = 0; j < pd.length; j++) {
                  int pp = Integer.parseInt(pd[j][0]);
                  _cat.finest(pd[j][1] + "==" + pd[j][0]);
                  _playerDetails[pp] = -1; // set it to occupied
                }
              }
        }
        
                   
       public String toString(){
           return _gname + ", _bots=" + _bots + ", teamSize=" + _teamSize + ", gt=" + _gtype;
       }

    }
    
      
}
