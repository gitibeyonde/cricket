package com.cricket.mmognio;

import com.cricket.common.message.Command;
import com.cricket.common.message.CommandMove;
import com.cricket.common.message.CommandQueue;
import com.cricket.common.message.GameEvent;
import com.cricket.common.message.Response;
import com.cricket.common.message.ResponseGameEvent;
import com.cricket.common.message.ResponseQueue;

import java.util.logging.Logger;

import java.util.*;
import java.net.InetAddress;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.ClosedChannelException;

import com.agneya.util.Rng;
import com.agneya.util.Configuration;
import com.agneya.util.LongOps;

import com.cricket.mmog.CricketPresence;
import com.cricket.mmogserver.GameProcessor;
import com.cricket.mmog.cric.util.MoveParams;

public class MMOGHandler {
  static Logger _cat = Logger.getLogger(MMOGHandler.class.getName());
  private static final int MAXSIZE = 2048;
  public SocketChannel _c;
  public Selector _s;
  ByteBuffer _h, _b, _o;
  public int _wlen = -1, _wseq = -1, _rlen = -1, _rseq = -1;
  protected boolean _dead, _killed, _disconnected;
  protected long _last_read_time;
  protected long _last_write_time;
  protected MMOGClient _attach;
  protected int _reconnect_count;

  protected InetAddress _ia;
  protected Date _con_time;

  public String _id;
  public CommandQueue _com;
  private ResponseQueue _resp;
  private Thread _t;
  private Response _last_response = null;

  protected static Hashtable _registry;

  private int _missed_moves = 0;
  private static final int MAX_MISSED_MOVES = 10;

  private final static byte[] HTTP_HEADER = {71, 69, 84, 32, 47, 32, 72, 84, 84,
                                            80, 47, 49, 46, 48, 32, 10};
  private final static int HTTP_HEADER_SIZE = 16;

  private final static byte[] TERMINATOR = {38, 84, 61, 90, 13, 10, 0};
  private final static int TERMINATOR_SIZE = 6;
  protected static String _ctype;
  private static Rng _rng;

  static {
    _registry = new Hashtable();
    _rng = new Rng();
    // start the server maintenance thread, this will listen to the various network
    // events and advice on removing a client because he is not responding etc.
    try {
      Configuration conf = Configuration.instance();
      int timeout = conf.getInt("Server.maintenance.heartbeat");
      int qSize = conf.getInt("Server.handler.response.queue.size");
      _ctype = (String) conf.get("Client.Type");
      _cat.finest(_ctype);
      Timer t = new Timer();
      t.schedule(new MMOGMaintenance(timeout, qSize), 5000, timeout);
    }
    catch (Exception e) {
      _cat.warning("Static initialization of handler failed");
    }
  }

  public MMOGHandler(SocketChannel c, Selector s, CommandQueue cq) throws
      Exception {
    _last_read_time = System.currentTimeMillis();
    _last_write_time = System.currentTimeMillis();
    _c = c;
    _s = s;
    _wseq = 0;
    _id = _rng.getNewSessionId();
    _registry.put(_id, this);
    _com = cq;
    _resp = new ResponseQueue();
  }

  public static MMOGHandler get(String session) {
    return (MMOGHandler) _registry.get(session);
  }

  public void setDisconnected() {
    _cat.warning("Setting disconnected---" + this);
    _disconnected = true;
    _dead = false;
  }

  public boolean isDisconnected() {
    return _disconnected;
  }

  public void unsetDisconnected() {
    _disconnected = true;
  }

  public boolean isKilled() {
    return _killed;
  }

  public void kill() {
    //new Exception().printStackTrace();
    _dead = true;
    _cat.warning("Killing handler =" + this);
    //new Exception().printStackTrace();
    if (!_disconnected) {
      _cat.warning("Sending disconnect response " + this);
      Response r = new Response(1, Response.R_KILL_HANDLER);
      putResponse(r);
    }
    else {
      killHandler();
    }
  }

  private void killHandler() {
    _killed = true;
    _dead = true;
    _disconnected = true;
    try {
      _cat.warning("Removing handler ..." + this);
      _registry.remove(_id);
      _c.close();
    }
    catch (IOException e) {
      _cat.info("Closing channel "); //+  e);
      //ignore
    }
    if (_attach != null && !_attach.isDead()) {
      _attach.kill();
    }
    _attach = null;
    _resp = null;
    _com = null;
  }

  public Response fetchResponse() throws Exception {
    return (Response) _resp.fetch();
  }

  public void putResponse(Response r) {
    if (_disconnected) {
      writeProxy(r);
      return;
    }
    try {
      _resp.put(r);
      _c.register(_s, SelectionKey.OP_WRITE | SelectionKey.OP_READ, this);
    }
    catch (ClosedChannelException e) {
      //_dead = true;
      _disconnected = true;
      _cat.info(_id + " Marking client as dead as the channed is closed");
    }
    catch (Exception e) {
      //_dead = true;
      _disconnected = true;
      _cat.warning(_id + " Marking client as dead as the channed is closed"+  e);
    }

  }

  public Command fetchCommand() throws Exception {
    return (Command) _com.fetch();
  }

  public void putCommand(Command e) {
    _com.put(e);
  }

  public boolean readHeader() throws IOException {
    if (_ctype.equals("FLASH")) {
      return true;
    }
    else if (_ctype.equals("HTTP")) {
      int r = 0;
      if (_rlen != -1) {
        return true;
      }
      if (_h == null) {
        _h = ByteBuffer.allocate(HTTP_HEADER_SIZE);
        _h.clear();
      }
      r = _c.read(_h);
      if (_h.hasRemaining()) {
        _cat.finest(_id + "  Partial header read " + r);
        if (r == -1) {
          //_dead = true;
          _disconnected = true;
          _cat.info(_id + " Marking the client dead as the channel is closed  ");
        }
        return false;
      }
      _h.flip();
      for (int i = 0; i < HTTP_HEADER_SIZE; i++) {
        byte b = _h.get();
        _cat.finest(Byte.toString(b));
      }
      _rlen = HTTP_HEADER_SIZE;
      _h = null;
      return true;
    }
    else {
      int r = 0;
      if (_rlen != -1) {
        return true;
      }
      if (_h == null) {
        _h = ByteBuffer.allocate(8);
        _h.clear();
      }
      r = _c.read(_h);
      if (_h.hasRemaining()) {
        _cat.finest(_id + "  Partial header read " + r);
        if (r == -1) {
          //_dead = true;
          _disconnected = true;
          _cat.info(_id + " Marking the client dead as the channel is closed  ");
        }
        return false;
      }
      _h.flip();
      _rseq = _h.getInt();
      _rlen = _h.getInt();
      _h = null;
      if (_rlen > MAXSIZE) {
        _cat.warning("Max length exceeded " + _rlen + ", " + this);
        killHandler();
        return false;
      }
      return true;
    }
  }

  public boolean readBody() throws IOException {
    int r = 0;
    String comstr = null;
    if (_ctype.equals("FLASH")) {
      _b = ByteBuffer.allocate(1);
      _b.clear();
      byte[] buf = new byte[1024];
      int i = 0;
      r = 0;
      _cat.finest("Starting the read loop");
      while ((r = _c.read(_b)) != -1) {
        if ((buf[i] = _b.array()[0]) == (byte) 0) {
          break;
        }
        _cat.finest(String.valueOf(buf[i]));
        i++;
        _b.clear();
      }
      if (i > 0) {
        comstr = new String(buf, 0, i - 2, "UTF-8");
      }
    }
    else if (_ctype.equals("HTTP")) {
      _b = ByteBuffer.allocate(1);
      _b.clear();
      byte[] buf = new byte[1024];
      int i = 0;
      r = 0;
      _cat.finest("Starting the read loop");
      while ((r = _c.read(_b)) != -1) {
        if ((buf[i] = _b.array()[0]) == (byte) 0) {
          break;
        }
        _cat.finest(String.valueOf(buf[i]));
        i++;
        _b.clear();
      }
      if (i > 0) {
        comstr = new String(buf, 0, i, "UTF-8");
      }
    }
    else {
      if (_b == null) {
        _b = ByteBuffer.allocate(_rlen);
        _b.clear();
      }
      r = _c.read(_b);
      if (_b.hasRemaining()) {
        if (r == -1) {
          //_dead = true;
          _disconnected = true;
        }
        return false;
      }
      _b.flip(); // read complete
      comstr = new String(_b.array());
    }

    // form the command
    resetRead();
    _cat.finest(_last_read_time + ">" + _rseq + " READ = " + comstr);
    Command com = new Command(comstr);
    if (com.session() == null) {
      _cat.info(_id + " session null " + comstr);
    }
    // queue the command for processing
    if (com.session().equals("null") || com.session().equals("")) {
      com.session(_id);
    }
    else if (!com.session().equals(_id)) {
      // reconnect
      _cat.info("Reconnected old session=" + com.session() + " new session=" +
                _id);
      swap(com.session());
      com.session(_id);
    }
    com.handler(this);
    _com.put(com);
    return true;
  }

  public boolean swap(String osid) {
    MMOGHandler oh = (MMOGHandler) _registry.get(osid);

    if (oh == null) {
      _cat.info("Reconnect failed for the client as session has expired " +
                osid);
      //kill();
      return false;
    }
    else {
      _last_read_time = System.currentTimeMillis();
      _last_write_time = System.currentTimeMillis();

      // move the old responses to new handlers queue
      _resp = oh._resp;
      _wseq = oh._wseq;
      _t = oh._t;

      MMOGClient oc = oh.attachment();
      oc.handler(this);
      attachment(oc);
      _cat.info("Reconnect attaching the old client " + oc);
      _registry.remove(osid);
      _registry.remove(_id);
      _id = osid;
      _registry.put(_id, this);
      _dead = false;
      _disconnected = false;
      return true;
    }
  }

  public boolean read() {
    try {
      if (_dead || _disconnected) {
        return false;
      }
      _last_read_time = System.currentTimeMillis();
      return (readHeader() && readBody()) ? true : false;
    }
    catch (IOException e) {
      _disconnected = true;
      _cat.info(" Marking client as dead " + this);
      //_dead = true;
      resetRead();
      return false;
    }
    catch (NumberFormatException e) {
      _disconnected = true;
      _cat.info(this +" Garbled command "); //+  e);
      //_dead = true;
      //ignore and reset read buffers
      resetRead();
      return false;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      _disconnected = true;
      _cat.info(this +" Garbled command "); //+  e);
      //_dead = true;
      //ignore and reset read buffers
      resetRead();
      return false;
    }

    catch (Exception e) {
      _cat.info(this +" Garbled command "); //+  e);
      //_dead = true;
      _disconnected = true;
      //ignore and reset read buffers
      //resetRead();
      return false;
    }

  }

  private void resetRead() {
    _h = null;
    _b = null;
    _rlen = -1;
    _rseq++;
  }

  public boolean write(String str) {
    try {
      if (_ctype.equals("FLASH")) {
        _o = ByteBuffer.allocate(str.length() + TERMINATOR_SIZE);
        _o.put(str.getBytes());
        _o.put(TERMINATOR);
      }
      else if (_ctype.equals("HTTP")) {
        _o = ByteBuffer.allocate(HTTP_HEADER_SIZE + str.length() + 1);
        _o.put(HTTP_HEADER);
        _o.put(str.getBytes());
        _o.put((byte) 0);
      }
      else {
        _o = ByteBuffer.allocate(8 + str.length());
        _o.putInt(_wseq++);
        _o.putInt(str.length());
        _o.put(str.getBytes());
      }
      _o.flip();
      int l = _c.write(_o);
      if (_o.remaining() != 0) {
        _cat.finest(this +"  Unable to write the full message  " + l);
        return false;
      }
      _o = null;
      if (_resp.size() <= 0) {
        _c.register(_s, SelectionKey.OP_READ | SelectionKey.OP_WRITE, this);
      }
      _cat.finest(_last_write_time + ">" + _wseq + " writing " + str);
    }
    catch (IOException e) {
      //_dead = true;
      _disconnected = true;
      _cat.info(_id +
                " Marking client as dead because of IOException during read");
      return false;
    }
    return true;
  }

  public boolean write(Response resp) {
    if (_disconnected) {
      return writeProxy(resp);
    }
    else {
      resp.session(_id);
      _last_response = resp;
      return write(resp.toString());
    }
  }

  public boolean write() {
    if (_disconnected) {
      return false;
    }
    _last_write_time = System.currentTimeMillis();
    try {
      if (_o != null && _o.remaining() != 0) {
        // check if the previous write request was complete
        int l = _c.write(_o);
        _cat.finest(_wseq + " Id=" + _id + "  Writing remaining .." + l);
        if (_o.remaining() != 0) {
          return false;
        }
        else {
          _o = null;
        }
      }
      Response resp = _resp.fetch();
      while (resp != null) {
        if (resp._response_name == Response.R_KILL_HANDLER) {
          killHandler();
          _cat.info("Processed disconnect response " + this);
          break;
        }
        else if (!write(resp)) {
          break;
        }
        resp = _resp.fetch();
      }
    }
    catch (NoSuchElementException e) {
      //ignore, nothing to write
      try {
        if (_resp.size() <= 0) {
          _c.register(_s, SelectionKey.OP_READ, this);
        }
      }
      catch (ClosedChannelException cc) {
        //ignore, nothing to write
        //_dead = true;
        _disconnected = true;
        _cat.info(this +
            " Marking client as dead because the channel is closed during write ");
        return false;
      }
    }
    catch (IOException e) {
      //_dead = true;
      _disconnected = true;
      _cat.info(_id + " Marking client as dead because of IOException ");
      return false;
    }
    return true;
  }

  public void setWriteTime() {
    _last_write_time = System.currentTimeMillis();
  }

  public void attachment(MMOGClient o) {
    _attach = o;
  }

  public MMOGClient attachment() {
    return _attach;
  }

  public static Hashtable registry() {
    return _registry;
  }

  public int respQSize() {
    return _resp == null ? 0 : _resp.size();
  }

  public int comQSize() {
    return _com == null ? 0 : _com.size();
  }

  public void inetAddress(InetAddress a) {
    _ia = a;
  }

  public InetAddress inetAddress() {
    return _ia;
  }

  public boolean equals(Object obj) {
    MMOGHandler hand = (MMOGHandler) obj;
    return _id.equals(hand._id);
  }


  /**
    protected void finalize() throws Throwable {
   if (_attach !=null && _attach instanceof com.cricket.server.GamePlayer )
            _cat.warn(" Garbage = " + _id + "  Name=" + ((com.cricket.server.GamePlayer)_attach).name());
          else
            _cat.warn("Garbage = " + _id);
          super.finalize();
      }
   **/

  /***
   * Proxy methods
   */


  public boolean makeMove(String gid, long grid, long move, double[][] amount) {
    int mv = 0;
    double amt = 0;
    MoveParams md = null;
    //_cat.warn("move=" + move);
    if ((com.cricket.mmog.Moves.K_BAT & move) > 0) {
      md = MoveParams.generate(MoveParams.BATS);
      mv = Command.M_BAT;
    }
    else if ((com.cricket.mmog.Moves.B_BOWL & move) > 0) {
      md = MoveParams.generate(MoveParams.BOWL);
      mv = Command.M_BOWL;
    }
    else if ((com.cricket.mmog.Moves.F_FIELD & move) > 0) {
      md = MoveParams.generate(MoveParams.FIELD);
      mv = Command.M_FIELD;
    }
    else if ((com.cricket.mmog.Moves.TOSS & move) > 0) {
      mv = Command.M_TOSS;
    }
    else if ((com.cricket.mmog.Moves.HEAD & move) > 0) {
      mv = Command.M_HEAD;
    }
    else if ((com.cricket.mmog.Moves.TAIL & move) > 0) {
      mv = Command.M_TAIL;
    }
    else if ((com.cricket.mmog.Moves.FIELDING & move) > 0) {
      mv = Command.M_FIELDING;
    }
    else if ((com.cricket.mmog.Moves.BATTING & move) > 0) {
      mv = Command.M_BATTING;
    }

    assert mv != 0:mv + "  is selected from " + move;
    CommandMove cmdm = new CommandMove(_id, mv, amt, gid, (int) grid, md);
    Command cmd = new Command(cmdm.toString());
    cmd.handler(this);
    _com.put(cmd);
    _dead = false;
    _missed_moves++;
    _cat.info("Proxy making a move " + cmdm);
    return true;
  }

  public boolean writeProxy(Response r) {
    _cat.info("WriteProxy " + r);

    _last_read_time = System.currentTimeMillis();
    _last_write_time = System.currentTimeMillis();

    if (r._response_name == Response.R_MOVE) {
      ResponseGameEvent rge = (ResponseGameEvent) r;
      GameEvent ge = new GameEvent();
      ge.init(rge.getGameEvent());
      String gid = ge.getGameId();
      int grid = ge.getGameRunId();

      String tps = ge.get("tp");
      if (tps == null) {
        return false;
      }
      String nms = ge.getNextMoveString();
      String[] tpa = tps.split("\\|");
      int tp = Integer.parseInt(tpa[0]);
      String tteam = tpa[1];
      //_cat.info(tp + ", " + tteam + ", Next move = " + nms);

      com.cricket.mmogserver.GamePlayer gp = (com.cricket.mmogserver.GamePlayer) attachment();
      //check if the game is play/reals
      //_cat.info( gp + " gP or presence " + gp.presence(gid) + " is null");
      if (gp == null || gp.presence(gid) == null) {
        _dead = true;
        return false;
      }
      //_cat.info( gp.presence(gid).pos() + ", " + gp.presence(gid).teamName() + " , " + gp.presence(gid).isResponseReq());
      if (gp.presence(gid).pos() != tp ||
          !((CricketPresence)gp.presence(gid)).teamName().equals(tteam) ||
          !gp.presence(gid).isResponseReq()) {
        return true;
      }
      com.cricket.mmog.Game g = com.cricket.mmog.Game.game(gid);
      _cat.info(g.type().toString());
      if (!g.type().isCricket()) {
        _dead = true;
        _cat.info("Non cricket game " + r);
        return true;
      }

      String[][] move = ge.getNextMove();

      MoveParams md = null;

      if (g.type().isTourny() && move != null) {
        _cat.info("Cricket game " + r);
        // check if that option is available
        int mv = 0;
        double amount = 0;
        for (int i = 0; i < move.length; i++) {
          int mov_pos = Integer.parseInt(move[i][0]);
          String mov_team = move[i][1];
          //_cat.info(mov_pos + " , " + mov_team);
          if (mov_pos != tp || !mov_team.equals(tteam)) {
            continue;
          }
          //_cat.info("Selecting move " + move[i][3]);
          if (move[i][3].equals("bat")) {
            mv = Command.M_BAT;
            md = MoveParams.generate(MoveParams.BATS);
            //if (g.type().isMatch()){
            // make sure this player is out now
            md._timing = 0;
            amount = 0;
            break;
          }
          if (move[i][3].equals("bowl")) {
            mv = Command.M_BOWL;
            md = MoveParams.generate(MoveParams.BOWL);
            amount = 0;
            break;
          }
          if (move[i][3].equals("field")) {
            mv = Command.M_FIELD;
            md = MoveParams.generate(MoveParams.FIELD);
            md.setFieldAction(54); // MISFIELD=FIELD
            amount = 0;
            break;
          }
          if (move[i][3].equals("toss")) {
            mv = Command.M_TOSS;
            amount = 0;
            continue;
          }
          if (move[i][3].equals("head")) {
            mv = Command.M_HEAD;
            amount = 0;
            break;
          }
          if (move[i][3].equals("tail")) {
            //check
            mv = Command.M_TAIL;
            amount = 0;
            break;
          }
          if (move[i][3].equals("fielding")) {
            //check
            mv = Command.M_FIELDING;
            amount = 0;
            break;
          }
          if (move[i][3].equals("batting")) {
            //check
            mv = Command.M_BATTING;
            amount = 0;
            break;
          }

        }
        if (mv == 0) {
          _cat.info("Did not get a move -- Ignoring ");
          // also check if the game is running..if not kill the player
          //_dead = true;
          return true;
        }
        else {
          CommandMove cmdm = new CommandMove(_id, mv, amount, gid, grid, md);
          Command cmd = new Command(cmdm.toString());
          _cat.info("Proxy making a move " +
                    ((com.cricket.mmogserver.GamePlayer) attachment()).name() +
                    "-> " + cmdm);
          cmd.handler(this);
          _t = new ProxyDelayElement(cmd);
          _t.start();
          _dead = false;
          return true;
        }
      }
    }
    else {
      _cat.info("No impl" + r);
    }
    return false;

  }

  public String toString() {
    return _id + ", Disc=" + _disconnected + ", Dead=" + _dead + ", Killed=" +
        _killed;
  }

  public static void main(String args[]) {
    String HTTP_HEADER = "GET / HTTP/1.0\n";
    byte hh[] = HTTP_HEADER.getBytes();

    for (int i = 0; i < hh.length; i++) {
      System.out.print(hh[i] + "-");
    }

  }


  public class ProxyDelayElement extends Thread {
    private Command _cm;

    public ProxyDelayElement(Command c) {
      _cm = c;
    }

    public void run() {
      try {
        Thread.currentThread().sleep(3000);
      }
      catch (Exception e) {
        //ignore
      }
      _com.put(_cm);
    }

  }
}
