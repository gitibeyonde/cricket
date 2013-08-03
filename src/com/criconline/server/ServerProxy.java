package com.criconline.server;

import com.cricket.common.message.Command;
import com.cricket.common.message.CommandBuyChips;
import com.cricket.common.message.CommandGameDetail;
import com.cricket.common.message.CommandGameList;
import com.cricket.common.message.CommandGetChipsIntoGame;
import com.cricket.common.message.CommandInt;
import com.cricket.common.message.CommandLogin;
import com.cricket.common.message.CommandMessage;
import com.cricket.common.message.CommandMove;
import com.cricket.common.message.CommandRegister;
import com.cricket.common.message.CommandString;
import com.cricket.common.message.CommandTournyDetail;
import com.cricket.common.message.CommandTournyMyTable;
import com.cricket.common.message.CommandTournyRegister;
import com.cricket.common.message.GameEvent;
import com.cricket.common.message.Response;
import com.cricket.common.message.ResponseConfig;
import com.cricket.common.message.ResponseFactory;
import com.cricket.common.message.ResponseGameDetail;
import com.cricket.common.message.ResponseGameEvent;
import com.cricket.common.message.ResponseGameList;
import com.cricket.common.message.ResponseGetChipsIntoGame;
import com.cricket.common.message.ResponseInt;
import com.cricket.common.message.ResponseLogin;
import com.cricket.common.message.ResponseMessage;
import com.cricket.common.message.ResponsePlayerShift;
import com.cricket.common.message.ResponseString;
import com.cricket.common.message.ResponseTournyDetail;
import com.cricket.common.message.ResponseTournyList;
import com.cricket.common.message.ResponseTournyMyTable;
import com.cricket.common.message.TournyEvent;

import java.util.*;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import java.io.IOException;
import java.util.logging.Logger;

import com.cricket.mmog.cric.util.ActionConstants;
import com.criconline.actions.*;
import com.criconline.lobby.tourny.TournyMessagesListener;
import com.criconline.models.PlayerModel;
import com.cricket.mmog.GameType;
import com.criconline.models.LobbyCricketModel;
import com.criconline.models.LobbyTableModel;
import com.cricket.mmog.PlayerPreferences;
import com.criconline.ClientSettings;
import com.criconline.ServerMessagesListener;
import com.criconline.models.LobbyTournyModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.criconline.ClientRoom;

import com.cricket.common.db.DBPlayer;

public class ServerProxy extends EventGenerator implements Runnable {

  /***
   * Player's static identity declaration
   */
  static boolean _dead;
  static long _last_read_time;
  static long _last_write_time;
  static int _points;
  static int _preferences = -1;
  static public int _gender = -1;
  static long HTBT_INTERVAL = 20000;
  static ResponseGameList _rtl = null; //last table list update
  protected static String _ctype = "NORMAL";
  private final static byte[] TERMINATOR = {38, 84, 61, 90, 13, 10, 0};
  private final static int TERMINATOR_SIZE = 7;


  // End player info declaration

  /**
   * Connection handling declaration
   */
  static Logger _cat = Logger.getLogger(ServerProxy.class.getName());
  static Object _dummy = new Object();
  static Selector _selector;
  static Command _out;
  static SocketChannel _channel;
  static String _session = null;
  static ByteBuffer _h, _b, _o;
  static int _wlen = -1, _wseq = 0, _rlen = -1, _rseq = -1;
  static String _comstr;
  static Vector _waiters;
  static ClientSettings _settings;
  public static ResponseLogin _lresp;


  // End connection handling declaration

  static ServerProxy _serverProxy;

  public ServerProxy(String ip, int port) throws Exception {
    super();
    _channel = SocketChannel.open(new InetSocketAddress(ip, port));
    _channel.configureBlocking(true);
    _session = connect(_channel);
    _selector = Selector.open();
    _waiters = new Vector();
    _settings = new ClientSettings();
  }

  public static ServerProxy getInstance(String ip, int port, JFrame jf) throws
      Exception {
    if (_serverProxy == null) {
      synchronized (_dummy) {
        if (_serverProxy == null) {
          _serverProxy = new ServerProxy(ip, port);
          _serverProxy.setOwner(jf);
        }
      }
    }
    return _serverProxy;
  }
    
    public void setClientRoom(ClientRoom cr){
        _clientRoom = cr;
    }
    
  public void setOwner(JFrame jf) {
    _owner = jf;
  }

  public static ServerProxy getInstance() {
    assert _serverProxy != null:"Server Proxy is not appropriately initialized"; return
        _serverProxy;
  }

  public void createActionFactory(String tid, int pos, String team) {

    ActionFactory af = getActionFactory(tid);
    if (af == null) {
      // create a action registry entry
      af = new ActionFactory(this, tid);
      af._joined = true;
      af._pos = pos;
      af._team = team;
      _action_registry.put("" + tid, af);
      _cat.warning("Received the details for a table with presence");
    }

  }

  public void createActionFactory(String tid) {

    ActionFactory af = getActionFactory(tid);
    if (af == null) {
      // create a action registry entry
      af = new ActionFactory(this, tid);
      _action_registry.put("" + tid, af);
      _cat.warning("Received the details for a table without presence");
    }

  }

  public String connect(SocketChannel _channel) throws Exception {
    Command ge = new Command("null", Command.C_CONNECT);
    _cat.finest("Connect req " + ge.toString());
    write(ge.toString());
    String s = readFull();
    _cat.finest("Connect resp " + s);
    Response r = new Response(s);
    _authenticated = false;
    return r.session();
  }

  public Response login(String name, String pass) throws Exception {
    _name = name;
    _password = pass;
    _channel.configureBlocking(true);
    CommandLogin ge = new CommandLogin(_session, _name, _password, "0",
                                       "admin");
    _cat.finest(_name + " Login Command " + ge);
    write(ge.toString());
    _last_write_time = System.currentTimeMillis();
    ResponseFactory rf = new ResponseFactory(readFull());
    Response gr = (Response) rf.getResponse();
    _cat.finest(_name + " Login Response " + gr);
    if (gr.getResult() == 1 || gr.getResult() == 12) {
      ResponseLogin lresp = (ResponseLogin) gr;
      _points = lresp.getPoints();
      _gender = lresp.getGender();
      _preferences = lresp.getPreferences();
      _authenticated = true;
      _channel.configureBlocking(false);
      setSelectorForRead();
      // start the event thread
      Thread _runner = new Thread(this);
      _runner.start();
      _serverProxy.startTLThread();
      return gr;
    }
    return null;
  }

  public Response registerUser(String user, String password, String email,
                               int gender, String bc, String sc) throws
      Exception {
    _name = user;
    _password = password;
    _channel.configureBlocking(true);
    CommandRegister ge = new CommandRegister(_session, _name, _password, email,
                                             gender, bc, sc, "admin");
    _cat.finest(_name + " Register Command " + ge);
    write(ge.toString());
    _last_write_time = System.currentTimeMillis();
    ResponseFactory rf = new ResponseFactory(readFull());
    Response gr = (Response) rf.getResponse();
    _cat.finest(_name + " Register Response " + gr);
    if (gr.getResult() == 1) {
        ResponseLogin lresp = (ResponseLogin) gr;
      _gender = ((ResponseLogin) gr).getGender();
      _preferences = ((ResponseLogin) gr).getPreferences();
      _authenticated = true;
      _cat.finest(_name + " Login Response " + gr);

      _channel.configureBlocking(false);
      setSelectorForRead();
      // start the event thread
      Thread _runner = new Thread(this);
      _runner.start();
      _serverProxy.startTLThread();
      if (lresp.getTableCount() > 0) {
          _lresp = lresp;
        }
      return gr;
    }
    return null;
  }

  public void logout() throws Exception {
    Command ge = new Command(_session, Command.C_LOGOUT);
    _cat.finest(_name + " Logout Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void run() {
    _cat.finest("Starting the run loop");
    boolean keepLooking = true;
    ServerProxy p = null;
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
            p = (ServerProxy) key.attachment();
            String response = p.read();
            if (response == null) {
              continue;
            }
            ResponseFactory rf = new ResponseFactory(response);
            Response r = rf.getResponse();
            //_cat.finest("Response = " + r);
            if (r._response_name == Response.R_GAMELIST) {
              _rtl = (ResponseGameList) r;
              Vector lpm = new Vector(); //LobbyPractiseModel
              Vector lcm = new Vector(); //LobbyCricketModel
              for (int v = 0; v < _rtl.getGameCount(); v++) {
                GameEvent ge = _rtl.getGameEvent(v);
                _cat.finest("Cricket " + ge);
                GameType gt = new GameType(ge.getType());
                LobbyTableModel ltm;

                ltm = new LobbyCricketModel(ge);
                ltm._total_games = _rtl.getGameCount();
                ltm._total_players = _rtl.getPlayerCount();
                lcm.add(ltm);


              }
              fireLobbyModelChangeEvent((LobbyCricketModel[]) lcm.toArray(new LobbyCricketModel[lcm.size()]));
              // fire lobby updated listeners
            }
            else if (r._response_name == Response.R_GAMEDETAIL) {
              ResponseGameDetail rtd = (ResponseGameDetail) r;
              GameEvent ge = new GameEvent();
              ge.init(rtd.getGameEvent());
              // check if user has presence on this table
              String tid = ge.getGameId();
              ActionFactory af = getActionFactory(tid);
              if (af == null) {
                // create a action registry entry
                af = new ActionFactory(this, tid);
                _action_registry.put("" + tid, af);
                _cat.warning("Received the details for a table without presence");
              }
              af.processGameEvent(ge, Response.R_GAMEDETAIL);
            }
            else if (r._response_name == Response.R_TOURNYLIST) {
              ResponseTournyList tl = (ResponseTournyList) r;
              _cat.info(_name + " TournyList Response " + tl);
              Vector lcm = new Vector(); //LobbyCricketModel
              for (int v = 0; v < tl.getTournyCount(); v++) {
                TournyEvent ge = tl.getTourny(v);
                lcm.add(new LobbyTournyModel(ge));
                _cat.finest("Tourny " + ge);
              }
              fireLobbyModelChangeEvent((LobbyTournyModel[]) lcm.toArray(new LobbyTournyModel[lcm.size()]));
            }

            else if (r._response_name == Response.R_PROFESSIONAL_PLAYERS) {
              ResponseString rs = (ResponseString) r;
              String[] pd = rs.getStringVal().split("\\|");
              DBPlayer v[] = new DBPlayer[pd.length];
              for (int j = 0; j < pd.length && pd[j].length() > 10; j++) {
                //v[j] = new DBPlayer(pd[j], 2);
                new Exception().printStackTrace();
              }
              //fireLobbyModelChangeEvent(v);
            }

            else if (r._response_name == Response.R_TEAM_MANAGER) {
              ResponseString rs = (ResponseString) r;
              String[] pd = rs.getStringVal().split("\\|");
              DBPlayer v[] = new DBPlayer[pd.length];
              for (int j = 0; j < pd.length && pd[j].length() > 10; j++) {
                new Exception().printStackTrace();
                //v[j] = new DBPlayer(pd[j], 2);
              }
              //fireLobbyModelChangeEvent(v);

            }

            else if (r._response_name == Response.R_TOURNYDETAIL) {
              ResponseTournyDetail rtd = (ResponseTournyDetail) r;
              TournyEvent ge = new TournyEvent(rtd.getTournyEvent());
              // check if user has presence on this table
              try {
                //fireTournyMessagesEvent(ge.id(), rtd);
              }catch (Exception e){
                // THE tourny does not exists
                JOptionPane.showMessageDialog(_owner,
                                              "The tourny is over !.", "ERROR",
                                      JOptionPane.ERROR_MESSAGE);
              }
            }
            else if (r._response_name == Response.R_TOURNYREGISTER) {
              ResponseString rtd = (ResponseString) r;
              // check if user has presence on this table
              fireTournyMessagesEvent(rtd.getStringVal(), rtd);
            }
            else if (r._response_name == Response.R_TOURNYMYTABLE) {
              ResponseTournyMyTable rtd = (ResponseTournyMyTable) r;
              // check if user has presence on this table
              fireTournyMessagesEvent(rtd.getTid(), rtd);
            }
            else if (r._response_name == Response.R_MOVE) {
              _cat.info(r.toString());
              if (r.getResult() == Response.E_BROKE) {
                ResponseString rint = (ResponseString) r;
                String tid = rint.getStringVal();
                ActionFactory af = getActionFactory(tid);
                ErrorAction a = new ErrorAction(ActionConstants.
                                                UNSUFFICIENT_FUND);
                if (af != null) {
                  fireServerMessagesEvent(tid, a);
                }
              }
              else {
                _cat.finest("Response = " + r);
                ResponseGameEvent rge = (ResponseGameEvent) r;
                GameEvent ge = new GameEvent();
                ge.init(rge.getGameEvent());
                String tid = ge.getGameId();
                ActionFactory af = getActionFactory(tid);
                if (af != null) {
                  af.processGameEvent(ge, Response.R_MOVE);
                }
              }
            }
            // move
            else if (r._response_name == Response.R_PLAYERSHIFT) {
              ResponsePlayerShift rps = (ResponsePlayerShift) r;
              String tid = rps.getGid();
              ActionFactory af = getActionFactory(tid);
              if (af != null) {
                af.processPlayerShift(rps);
              }
              //CSID=2117703131567713056&RNAME=37&CR=1&gid=1051&player=null&dx=5&dy=0
            }
            else if (r._response_name == Response.R_MESSAGE) {
              ResponseMessage rm = (ResponseMessage) r;
              _cat.finest(" Message Received " + rm.getMessage());
              Object o = new Object[1];
              if (rm.getType().equals("chat")) {
                o = new ChatAction( -1, "", "", "");
                ((ChatAction) o).setGM(rm.getGM()); 
                String tid = rm.getGameId();
                fireServerMessagesEvent(tid, o);
              }
              else if (rm.getType().equals("broadcast")) {
                GameEvent cge = rm.getGM();
                ChatAction ca = new ChatAction(cge.get("name"),
                                               cge.get("message"));
                //fireLobbyModelChangeEvent(ca);
              }
              else {
                o = new MessageAction(rm.getMessage());
                String tid = rm.getGameId();
                fireServerMessagesEvent(tid, o);
              }
            }
            else if (r._response_name == Response.R_GET_CHIPS_INTO_GAME) {
              ResponseGetChipsIntoGame rbc = (ResponseGetChipsIntoGame) r;
              String tid = rbc.getGameId();
              ActionFactory af = getActionFactory(tid);
              //af._chips = rbc.getAmount();
              CashierAction ca = new CashierAction(af._pos, "", rbc.getAmount());
            }
            else if (r._response_name == Response.R_LOGOUT) {
              _cat.finest(" Logged out " + p);
              ActionFactory af = getActionFactory(null);
              ErrorAction a = new ErrorAction(ActionConstants.REMOVED);
              if (af != null) {
                //fireLobbyInfoListener(a);
              }

            }
            else if (r._response_name == Response.R_WAITER) {
              ResponseInt rint = (ResponseInt) r;
              _cat.warning(" Waiting response " + rint);
              if (rint.getResult() == Response.E_PARTIALLY_FILLED) {
                _cat.warning("Seats are available");
                //fireLobbyInfoListener(new ErrorAction(ActionConstants.
                  //  PLAYER_SEAT_AVAILABLE));
              }
              else {
                _cat.warning("Successfuly added to the waiting list");
                //fireLobbyInfoListener(new ErrorAction(ActionConstants.
                  //  ADD_TO_WAITERS));
              }
            }
            else if (r._response_name == Response.R_PLAYERSHIFT) {
              //pass to players
              ResponsePlayerShift rps = (ResponsePlayerShift) r;
              fireServerMessagesEvent(rps.getGid(), rps);
            }
            else {
              _cat.warning(
                  "SHILL: FATAL: Unknown event received from the server " +
                  r._response_name);
            }
          }
        }
        // go thru action registries
        pumpGeneratedActions();
        Thread.currentThread().sleep(10);
      }
      catch (Throwable ex) {
        ex.printStackTrace();
        _cat.warning("Exceptional condition, will continue "+ ex);
        // ignore
      }
    }
  }

  public void stopWatchOnTable(String tid) {
    try {
      removeObserver(tid);
      _action_registry.remove("" + tid);
    }
    catch (Exception e) {
      _cat.warning("Unable to stop watch on table"+ e);
    }
  }

  public void stopWatchOnTable() {
    try {
      for (Enumeration enumer = _action_registry.elements();
                                enumer.hasMoreElements(); ) {
        ActionFactory af = (ActionFactory) enumer.nextElement();
        removeObserver(af._id);
        _action_registry.remove("" + af._id);
      }
    }
    catch (Exception e) {
      _cat.warning("Unable to stop watch on table"+  e);
    }
  }

  public void addWatchOnTable(String tid, ServerMessagesListener changesListener) {
    try {
      addObserver(tid);
      addServerMessageListener(tid, changesListener);
    }
    catch (Exception e) {
      _cat.warning("Unable to start watch on table"+  e);
    }
  }

  public void addWatchOnTourny(String tid, TournyMessagesListener changesListener) {
    try {
      tournyDetails(tid);
      addTournyMessageListener(tid, changesListener);
    }
    catch (Exception e) {
      _cat.warning("Unable to start watch on table"+  e);
    }
  }


  /***************************************************************************
   * NEW METHODS TO BE RESOLVED
   ***************************************************************************/
  /**
   * Load client settings from persistent store.
   * @return ClientSettings - player preferences.
   */
  public ClientSettings loadClientSettings() {
    _cat.finest("Client Settiongs preferences " +
               PlayerPreferences.stringValue(_preferences));
    _settings = new ClientSettings(_preferences);
    return _settings;
  }


  /**
   * Store client settings in persistent store.
   * @param settings - player preferences.
   */
  public void storeClientSettings(ClientSettings settings) throws IOException {
    updateClientSettingsAtServer(settings.intVal());
    _settings = settings;
  }

  public int points() {
    return _points;
  }

  public String getTicket() {
    return _session;
  }

  public String getAd() {
    return "    Advertise @ CricketParty\n\n";
  }

  public boolean isWait(String tid) {
    if (_waiters.contains(tid)) {
      return true;
    }
    else {
      return false;
    }
  }

  public boolean isLoggedIn() {
    return _authenticated;
  }

  public boolean isRegistered() {
    new Exception().printStackTrace();
    return true;
  }

  public void leaveTable(String tid) {
    try {
      //new Exception().printStackTrace();
      CommandMove ge = new CommandMove(_session, CommandMove.M_LEAVE, 0, tid);
      _cat.finest(_name + " Leave Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
    }
    catch (IOException e) {
      _cat.warning("Already disconnected");
    }
  }

  public void waitTable(String tid) throws IOException {
    //new Exception().printStackTrace();
    CommandString ge = new CommandString(_session, CommandMove.C_WAITER, tid);
    _cat.finest(_name + " Wait Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void joinTable(String tid, int pos, String team, double value) {
    _cat.finest("Joining " + tid);
    try {
      join(tid, pos, team, value);
    }
    catch (Exception e) {
      _cat.warning("Unable to join game "+  e);
    }
  }

  public void sitOutTable(int tid) throws IOException {
    CommandInt ge = new CommandInt(_session, Command.C_SIT_OUT, tid);
    _cat.finest(_name + " Move Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void sitInTable(int tid) throws IOException {
    CommandInt ge = new CommandInt(_session, Command.C_SIT_IN, tid);
    _cat.finest(_name + " Move Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void sendToServer(Command c) throws IOException {
    _cat.info(c.toString());
    c.session(_session);
    write(c.toString());
  }

  public void sendToServer(String tid, Action o) throws IOException {
    _cat.finest("Action received " + o);
    if (o instanceof MoveAction) {
      MoveAction ba = (MoveAction) o;
      CommandMove ge = new CommandMove(_session, ActionFactory.getMove(ba.getId()),
                                       0, tid, 0, ba.getMoveParams());
      ge.setPlayerPosition(ba.getPosition());
      _cat.finest(_name + " Move Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
    }
    else if (o instanceof ChatAction) {
      if (o.getId() == CHAT) {
        CommandMessage cm = new CommandMessage(_session,
                                               ((ChatAction) o).getChatString(),
                                               tid);
        _cat.finest(_name + " Chat Command " + cm);
        _last_write_time = System.currentTimeMillis();
        write(cm.toString());
      }
      else if (o.getId() == LOBBY_CHAT) {
        CommandMessage cm = new CommandMessage(_session,
                                               ((ChatAction) o).getChatString(),
                                               "");
        _cat.finest(_name + " Chat Command " + cm);
        _last_write_time = System.currentTimeMillis();
        write(cm.toString());
      }
    }
    else if (o instanceof CashierAction) {
      /*
          new CashierAction(clientPokerController.getPlayerNo(), value)
       */
      CommandGetChipsIntoGame ge = new CommandGetChipsIntoGame(_session, tid,
          ((CashierAction) o).getAmount());
      _cat.finest(_name + " get Chips  Command " + ge);
      _last_write_time = System.currentTimeMillis();
      write(ge.toString());
    }
  }

  public static void removeFromWaiters() {
    new Exception().printStackTrace();

  }

  public static void removeAllWaiters() {
    //new Exception().printStackTrace();
  }

  public void startTLThread() {
    // start a heartbeat timer thread
    TLThread hb = new TLThread();
    Timer t = new Timer();
    t.schedule(hb, 0, HTBT_INTERVAL);
  }

  public void addToWaiters(String tid) throws IOException {
    waitTable(tid);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("Player=[Name=");
    sb.append(_name);
    sb.append(", Session=");
    sb.append(_session);
    sb.append(", Game count=");
    sb.append(_action_registry.size());
    sb.append(", Worth=");
    sb.append(_points);
    return sb.toString();
  }


  /********************************************************************************
   * Repetitive tasks
   *****************************************************************************/

  public class TLThread extends TimerTask {

    public void run() {
      try {
        // Update the table list at regular intervals
        poll();
        heartBeat();
      }
      catch (Exception ex) {
        //do nothing
        _cat.warning("Exception" + ex);
      }
    }
  } // end HeartBeat class


  /********************************************************************************
   * Requests to the server
   *****************************************************************************/

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
    _channel.configureBlocking(true);
    CommandRegister ge = new CommandRegister(_session, _name, _password,
                                             _name + "@test.com", (byte) 0, "",
                                             "", "admin");
    _cat.finest(_name + " Register Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
    Response gr = new Response(readFull());
    _cat.finest(_name + " Register Response " + gr);
    return gr;
  }

  public void addObserver(String tid) throws Exception {
    CommandGameDetail ge = new CommandGameDetail(_session, tid);
    _cat.finest(_name + " Add Observer Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void professionalPlayersDetails() throws Exception {
    Command ge = new Command(_session, Command.C_PROFESSIONAL_PLAYERS);
    _cat.finest(_name + " GETTING PROFESSIONAL PLAYERS DETAILS " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void tournyDetails(String tid) throws Exception {
    CommandTournyDetail ge = new CommandTournyDetail(_session, tid);
    _cat.finest(_name + " GETTING TOURNY DETAILS " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void tournyRegister(String tid) throws Exception {
    CommandTournyRegister ge = new CommandTournyRegister(_session, tid);
    _cat.finest(_name + " TOURNY REGISTRATION " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void tournyMyPitch(String tid) throws Exception {
    CommandTournyMyTable ge = new CommandTournyMyTable(_session, tid);
    _cat.finest(_name + " TOURNY MY TABLE " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void removeObserver(String tid) throws Exception {
    CommandString ge = new CommandString(_session, Command.C_TURN_DEAF, tid);
    _cat.finest(_name + " Remove Observer Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void join(String tid, int pos, String team, double value) throws
      Exception {
    CommandMove ge = new CommandMove(_session, Command.M_SIT_IN, value, tid);
    ge.setPlayerPosition(pos);
    ge.setPlayerTeam(team);
    _cat.finest(_name + " Join Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void buyChips(double playchips, double realChips) throws IOException {
    CommandBuyChips ge;
    ge = new CommandBuyChips(_session, playchips, realChips);
    //_requested_chips = value;
    _cat.finest(_name + " Buy Chips  Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public Response getPlayerStats(String userid) throws IOException {
    CommandString cs = new CommandString(userid);
    write(cs.toString());
    Response gr = new Response(readFull());
    _cat.finest(_name + " Get Chips Response " + gr);
    return gr;
  }

  public Response getMoneyIntoGame(String tid, double value) throws IOException {
    CommandGetChipsIntoGame ge;
    ge = new CommandGetChipsIntoGame(_session, tid, value);
    _cat.finest(_name + " Get Chips  Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
    Response gr = new Response(readFull());
    _cat.finest(_name + " Get Chips Response " + gr);
    return gr;
  }

  public void updateClientSettingsAtServer(int settings) throws IOException {
    CommandInt ci = new CommandInt(_session, Command.C_PREFERENCES, settings);
    //_cat.finest(PlayerPreferences.stringValue(settings));
    write(ci.toString());
  }

  public void setSelectorForRead() throws Exception {
    _channel.configureBlocking(false);
    _channel.register(_selector, SelectionKey.OP_READ, this);
  }

  public void getGameList(int type) throws IOException {
    CommandGameList ge = new CommandGameList(_session, type);
    _cat.finest("TableList Command " + ge);
    _last_write_time = System.currentTimeMillis();
    write(ge.toString());
  }

  public void getTournyList() throws Exception {
    _channel.configureBlocking(false);
    Command ge = new Command(_session, Command.C_TOURNYLIST);
    _cat.finest("TournyList Command " + ge);
    write(ge.toString());
  }

  public void getProPlayers() throws Exception {
    _channel.configureBlocking(false);
    Command ge = new Command(_session, Command.C_PROFESSIONAL_PLAYERS);
    _cat.finest("Professional Players list " + ge);
    write(ge.toString());
  }

  public void getTeamManagers() throws Exception {
    _channel.configureBlocking(false);
    Command ge = new Command(_session, Command.C_TEAM_MANAGER);
    _cat.finest("Team Manager list " + ge);
    write(ge.toString());
  }

  public void heartBeat() throws Exception {
    if (_session != null) {
      Command ge = new Command(_session, Command.C_HTBT);
      _cat.finest("HTBT" + ge.toString());
      write(ge.toString());
    }
  }

  public void poll() {
    try {
      // Update the table list at regular intervals
      if (_poll.equals("Cricket")) {
        getGameList(0xF); // get tables
      }
      else if (_poll.equals("Matches")) {
        getGameList(0xF0); // get tables
      }
      else if (_poll.equals("Madness")) {
             getGameList(0xF00); // get tables
      }
      else if (_poll.equals("Tournaments")) {
        getTournyList();
      }
      else if (_poll.equals("Professional Player")) {
        getProPlayers();
      }
      else if (_poll.equals("Team Manager")) {
        getTeamManagers();
      }
      else if (_poll.equals("Pro Tournaments")) {
        _cat.warning("UNIMPLEMENTED");
      }
      else {
        throw new IllegalStateException("Unknown poll");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  /********************************************************************************
   * Channel read write methods
   *****************************************************************************/

  public boolean readHeader() throws IOException {
    if (_ctype.equals("FLASH")) {
      return true;
    }
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
      //log.fatal(_name + " Partial header read " + r);
      if (r == -1) {
        _dead = true;
        _cat.warning(_name +
                   " Marking the client dead as the channel is closed  ");
      }
      return false;
    }
    _h.flip();
    _rseq = _h.getInt();
    _rlen = _h.getInt();
    _h = null;
    return true;
  }

  public boolean readBody() throws IOException {
    int r = 0;
    if (_ctype.equals("FLASH")) {
      _b = ByteBuffer.allocate(1);
      _b.clear();
      byte[] buf = new byte[1024];
      int i = 0;
      r = 0;
      while ((r = _channel.read(_b)) != -1) {
        if ((buf[i] = _b.array()[0]) == (byte) 0) {
          break;
        }
        i++;
        _b.clear();
      }
      if (r == -1) {
        _dead = true;
      }
      if (i > 0) {
        _comstr = new String(buf, 0, i - 2, "UTF-8");
      }
      resetRead();
    }
    else {
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
    }

    return true;
  }

  public String readFull() {
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
      _cat.warning(_name + " Garbled command "+  e);
      _dead = true;
      return null;
    }
    return null;
  }

  private void resetRead() {
    _h = null;
    _b = null;
    _rlen = -1;
    _rseq = -1;
  }

  public synchronized boolean write(String str) throws IOException {
    try {
      if (_ctype.equals("FLASH")) {
        _o = ByteBuffer.allocate(str.length() + TERMINATOR_SIZE);
        _o.put(str.getBytes());
        _o.put(TERMINATOR);
      }
      else {
        _o = ByteBuffer.allocate(8 + str.length());
        _o.putInt(_wseq++);
        _o.putInt(str.length());
        _o.put(str.getBytes());
      }

      _o.flip();
      int l = _channel.write(_o);
      while (_o.remaining() != 0) {
        _channel.write(_o);
      }
      _o = null;
      _dead = false;
    }
    catch (IOException e) {
      if (!_dead) {
        _cat.warning(_name +
                  " Marking client as dead because of IOException during write");
        JOptionPane.showMessageDialog(_owner,
            "There is a disconnection ! Restart to play.", "ERROR",
                                      JOptionPane.ERROR_MESSAGE);
      }
      _dead = true;
      throw e;
    }
    return true;
  }

} // end class Player
