package com.cricket.mmog.cric;

import com.cricket.common.db.DBGame;
import com.cricket.common.db.DBTourny;
import com.cricket.common.db.GameSequence;
import com.cricket.common.db.TournySequence;
import com.cricket.common.message.ResponseGameDetail;
import com.cricket.common.message.ResponseGameEvent;
import com.cricket.common.message.ResponseInt;
import com.cricket.common.message.ResponseMessage;
import com.cricket.common.message.ResponseTableOpen;

import com.cricket.mmogserver.GamePlayer;

import java.util.*;
import com.cricket.mmog.*;
import com.cricket.mmog.resp.Response;
import com.cricket.common.interfaces.TournyInterface;
import com.cricket.mmog.gamemsgimpl.*;
import com.cricket.mmog.cric.impl.*;
import java.util.logging.Logger;

import com.agneya.util.Utils;
import com.agneya.util.Base64;
import com.cricket.mmognio.MMOGHandler;
import com.cricket.mmog.cric.util.Team;
import com.agneya.util.Configuration;

import com.cricket.common.message.ResponseString;

import com.cricket.common.db.DBException;

public class Tourny extends Observable implements TournyInterface {
  // set the category for logging
  static Logger _cat = Logger.getLogger(Tourny.class.getName());

  String _name;
  Set _registered;
  CricketTourny[] _ht;
  int _team_size;
  Observer _stateObserver;
  protected int _fees;
  protected int _buyin;
  protected int _chips;
  protected int[] _sc = new int[5];
  protected int _di, _ri, _ji;
  protected int _state = NOEXIST;
  protected Calendar _sd;
  public Vector _winner;
  DBTourny _dbt;
  int _seated_player = 0;
  Vector _seated_player_list;
  int _ifin = 0;
  int _time_delta;


  public Tourny(String name, int[] schedule, double buy_ins, double fees,
                int chips, int ts, int di, int ri, int ji,
                Observer stateObserver) {
    _name = name;
    _stateObserver = stateObserver;
    _sc = schedule;
    _buyin = (int) buy_ins;
    _fees = (int) fees;
    _chips = chips;
    _team_size = ts;
    _di = di;
    _ri = ri;
    _ji = ji;
  }

  public String initNewTourny() throws DBException {
    _name = TournySequence.getNextGameId() + "";
    // init schedule time
    getScheduleDate();
    _cat.finest(this.toString());
    _counter = 0;
    _hand_level = 0;
    _seated_player = 0;
    _seated_player_list = new Vector();
    _dbt = new DBTourny(0, _name, _buyin, _fees, _sc, _di, _ri, _ji);
    _dbt.save();
    _cat.finest(this.toString());
    return _name;
  }

  public void stateSwitch() {
    try {
      Calendar rightNow = Calendar.getInstance();
      rightNow.setTimeZone(TimeZone.getTimeZone("GMT"));
      rightNow.set(Calendar.SECOND, 0);
      _cat.finest("Right now=" + rightNow.getTime().toGMTString() + ", TT=" +
                 getScheduleDate().getTime().toGMTString());
      _time_delta = delta(rightNow, getScheduleDate());

      _cat.finest("T^=" + _time_delta + ", " + this);
      // tournament is created
      switch (_state) {
        case NOEXIST:
          _state = REG;
          _winner = new Vector();
          _registered = Collections.synchronizedSet(new HashSet());
          _cat.finest("DECL " + this);
          break;
        case REG:
          if (_time_delta < _ji) {
            startWait();
            _cat.finest("JOIN " + this);
          }
          break;
        case JOIN:

          // send a message to all the connected clients
          Enumeration e = GamePlayer.getGPList();
          while (e.hasMoreElements()) {
            GamePlayer gp = (GamePlayer) e.nextElement();
            Iterator i = _registered.iterator();
            while (i.hasNext()) {
              String pl_name = (String) i.next();
              if (pl_name.equals(gp.name())) {
                // send the alert
                ResponseString rts = new ResponseString(1, com.cricket.common.message.Response.R_TOURNYSTARTS, _name);
                gp.deliver(rts);
              }
            }
          }
          if (_time_delta < 1) {
            startGame();
            _cat.finest("START " + this);
          }
          break;
        case START:
          _state = RUNNING;
          break;
        case RUNNING:
          _cat.finest("RUNNING START " + this);
          keepRunning();
          _cat.finest("RUNNING END " + this);
          break;
        case END:
          declareWinners();
          _cat.finest("Tournament ended --------------------\n" + this);
          _stateObserver.update(this, GameStateEvent.MTT_OVER);
          break;
        case FINISH:
          _cat.finest("FINISH...");
          if (_ifin == FINISH_CYCLES) {
            TournyController.instance().removeTourny(_name);
            initNewTourny();
            TournyController.instance().addTourny(this);
            _sd = Calendar.getInstance();
            _state = NOEXIST;
            _ifin = 0;
          }
          _ifin++;
          break;
        default:
          _cat.severe("Erroneous tourny state " + _name);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      _cat.warning(ex.getMessage()+ ex);
    }
  }

  protected Calendar getScheduleDate() {
    Calendar sd = Calendar.getInstance();
    sd.setTimeZone(TimeZone.getTimeZone("GMT"));
    sd.set(Calendar.SECOND, 0);
    _cat.finest("Curr time=" + sd.getTime().toGMTString());
    if (_state == NOEXIST) {
      if (_sc[0] != -1 && _sc[1] != -1) {
        sd.set(Calendar.MINUTE, _sc[0]);
        sd.set(Calendar.HOUR_OF_DAY, _sc[1]);
      }
      else if (_sc[1] == -1) {
        //System.out.println("Minute = " + sd.get(Calendar.MINUTE));
        sd.set(Calendar.MINUTE,
               _sc[0] * (sd.get(Calendar.MINUTE) / _sc[0] + 1) + _sc[0]);
        //System.out.println("Minute = " + sd.get(Calendar.MINUTE));
      }
      else {
        sd.set(Calendar.HOUR_OF_DAY, _sc[1]);
      }

      if (_sc[2] == -1) {
        // check if the time is already past
        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("GMT"));
        _cat.finest("Delta=" + delta(now, sd) + ", ji=" + _ji);
        // if (delta(now, sd) < 1 + _ji) {
        // sd.add(Calendar.DATE, 1);
        //}
        _cat.finest("SD=" + sd.getTime().toGMTString());
      }
      else {
        sd.set(Calendar.DATE, _sc[2]);
      }

      if (_sc[3] != -1) {
        sd.set(Calendar.MONTH, _sc[3] - 1);
      }
      if (_sc[4] != -1) {
        sd.set(Calendar.YEAR, _sc[4]);
      }
      _sd = sd;
    }
    else {
      sd = _sd;
    }
    return sd;
  }

  protected void schedule(int[] s) {
    _sc = s;
  }

  protected boolean isValid() {
    return true;
  }

  public boolean register(CricketPlayer p) {
    //_cat.finest("Registering " + p.name() + ", size=" + _registered.size());
    if (_registered.contains(p.name())) {
      return false;
    }
    else {
      _registered.add(p.name());
      return true;
    }
  }

  public boolean register(String name) {
    //_cat.finest("Registering " + name + ", size=" + _registered.size());
    _registered.add(name);
    return true;
  }

  public boolean unRegister(CricketPlayer p) {
    //_cat.finest("Registering " + p.name() + ", size=" + _registered.size());
    if (_registered.contains(p.name())) {
      _registered.remove(p.name());
      return true;
    }
    else {
      return false;
    }
  }

  public Calendar date() {
    return _sd;
  }


  /*
   */

  public void startWait() throws DBException {
    int pnos = _registered.size() / (_team_size * 2) + 1;
    _cat.finest("Registered players = " + _registered.size() +
               " pitches created = " + pnos);
    // add registered players to the games
    Object[] preg = _registered.toArray();
    int pcount = preg.length;
    String[][][] players = new String[pnos][2][];
    int[] pl_size = new int[pnos];

    _cat.finest("Pcount=" + pcount);
    for (int j = 0; j < pnos; j++) {
      players[j][0] = new String[_team_size];
      players[j][1] = new String[_team_size];
      for (int k = 0; pcount > 0 && k < _team_size; k++) {
        players[j][0][k] = (String) preg[--pcount];
        if (pcount == 0) {
          break;
        }
        players[j][1][k] = (String) preg[--pcount];
      }
    }

    // set the players for respective game
    _ht = new CricketTourny[pnos];
    // start the CricketTournys
    for (int i = 0; i < pnos; i++) {
      DBGame dbg = null;
      int id = GameSequence.getNextGameId();
      String aff[] = {"admin"};
      _ht[i] = new CricketTourny(id, _name, 2, _team_size, aff, _stateObserver, this);
      _cat.finest("CREATING PITCH=" + _ht[i]);
      _ht[i].invite(players[i][0]);
      _ht[i].invite(players[i][1]);

      dbg = new DBGame(id, _ht[i]._type.intVal(), "cricket-tourny", "", aff, null,
                       -1, 2, _team_size, 5, 10, 2, 5, 4, 4);
      dbg.save();
    }
    _state = JOIN;
  }

  public Response[] myTable(GamePlayer gp) throws DBException {
    _cat.finest("Trying to seat =" + gp.name());
    Response r[] = null;
    _cat.finest("wait=" + isWaiting() + ", play=" + isPlaying());
    if (isWaiting() || isPlaying()) {
      boolean _already_sitting = false;
      for (int i = 0; i < _ht.length; i++) {
        if (_ht[i].isInvited(gp.name())) {
          CricketPresence p;
          if ((p = (CricketPresence)_ht[i].getPresenceOnPitch(gp.name())) != null) {
            _cat.finest("Found presence " + p);
            _already_sitting = true;
            p.unsetDisconnected();
            gp.movePresence(p, _ht[i].name());
            return _ht[i].details(p);
          }
        }
      }
      for (int i = 0; i < _ht.length; i++) {
        _cat.finest("Seating on = " + _ht[i]);
        if (_ht[i].isInvited(gp.name())) {
          // check if he is already sitting
          CricketPresence p = (CricketPresence)gp.createPresence(_ht[i].name());
          p.setTournyPresence(_name);
          // seat the player on this table
          Team vac_team = _ht[i].getNextVacantTeam();
          _cat.finest("Team=" + vac_team);
          if (vac_team != null) {
            int pos = vac_team.nextVacant();
            _cat.finest("Pos=" + pos);
            if (pos != -1) {
              // seat this person
              //gp.addWatch(p.getGID());
              //Presence p, int pos, String team, double amt
              _cat.finest("Presence=" + p);
              r = gp.addGame(p, pos, vac_team._team_name, 100);
              //DEBUG
              _seated_player++;
              _seated_player_list.add(p);
              _cat.finest("Seating player " + p + " in game " +
                         r[0].getGame().name());
            }
          }
          break;
        }
      }
    }
    return r;
  }

  public void startGame() throws Exception {
    if (_ht.length == 0) {
      _state = END;
      return;
    }
    _state = START;
    int _deb_pc = 0;
    // send the appropriate responses to the players on game start
    for (int k = 0; k < _ht.length; k++) {
      _deb_pc += _ht[k].allPlayers( -1).length;
      _cat.finest("STARTING GAME " + _ht[k].name());
            com.cricket.mmogserver.GameProcessor.deliverResponse(_ht[k].start());
    }
    _cat.finest("Total players=" + _deb_pc);
    _cat.finest("Seated players=" + _seated_player);
  }

  public void keepRunning() throws Exception {
    //DEBUG
    int _deb_wins = _winner.size();
    int _deb_pc = 0;

    double total_chips = 0;
    Hashtable _deb_pay_hash = new Hashtable();
    for (int k = 0; k < _ht.length; k++) {
      CricketPresence[] v = _ht[k].allPlayers( -1);
      if (v != null) {
        _deb_pc += v.length;
      }
      for (int j = 0; j < _ht[k].allPlayers( -1).length; j++) {
        _deb_pay_hash.put(_ht[k].allPlayers( -1)[j].name(),
                          _ht[k].allPlayers( -1)[j]);
      }
    }
    _cat.info("Total chips = " + total_chips);

    if ((_deb_wins + _deb_pc) != _seated_player) {
      Hashtable _deb_win_hash = new Hashtable();
      for (int i = 0; i < _deb_wins; i++) {
        _deb_win_hash.put(((CricketPresence) _winner.get(i)).name(),
                          ((CricketPresence) _winner.get(i)));
      }

      for (int m = 0; m < _seated_player_list.size(); m++) {
        CricketPresence p = (CricketPresence) _seated_player_list.get(m);
        if (_deb_win_hash.get(p.name()) == null && _deb_pay_hash.get(p.name()) == null) {
          _cat.warning("MISSING PRESENCE" + p.name());
        }

        if (_deb_win_hash.get(p.name()) != null && _deb_pay_hash.get(p.name()) != null) {
          _cat.warning("EXTRA PRESENCE" + p.name());
        }

      }
    }
    _counter++;
    _hand_level = _counter / 5;
    _hand_level = _hand_level > 12 ? 12 : _hand_level;
    if (_ht.length == 0 || (_ht.length == 1 && !_ht[0].reRunCondition())) { //&& _ht[0].getPlayerCount() <= 1)) {
      _state = END;
      _cat.finest(
          "Marking the end of tourny as one table and it cannot be started ");
      /**if (_ht[0].getPlayerCount() == 1) {
        addWinner(_ht[0].getPlayerList()[0]);
       _cat.finest("Last remaining player players " + _ht[0].getPlayerList()[0]);
             }**/
    }
    else {
      _ht = mergeTablesBFLR(_ht);
      _cat.finest("Merged the tables .....");
      // start the game
      // send the appropriate responses to the players on game start
      for (int k = 0; k < _ht.length; k++) {
        if (_ht[k].handOver()) {
          _cat.finest("STARTING the game after merge " + _ht[k].name());
          Response r = _ht[k].start()[0];
          if (r instanceof GameDetailsResponse) {
            _cat.warning("The table failed to start " + _ht[k]);
            //continue;
            // remove this table
            if (_ht[k].allPlayers( -1).length == 0) {
              CricketTourny[] del_list = new CricketTourny[_ht.length - 1];
              for (int l = 0, m = 0; l < _ht.length - 1; l++, m++) {
                if (m == k) {
                  m++;
                }
                del_list[l] = _ht[m];
              }
              _ht = del_list;
            }
          }
          else {
                        com.cricket.mmogserver.GameProcessor.deliverResponse(r);
          }
          if (k >= _ht.length) {
            break;
          }
        }
        else {
          //_cat.finest("Hand not over " + _ht[k]);
        }
      }
      _state = RUNNING;
    }
  }

  public void addWinner(CricketPresence pr) {
    CricketPlayer p = (CricketPlayer) pr.player();
    if (p instanceof GamePlayer) {
      GamePlayer gp = (GamePlayer) p;
      StringBuffer sbuf = new StringBuffer("type=broadcast").append(",message=").
                          append(Base64.encodeString("Congratulation " + gp.name() +
                                               "! Your position is " +
                                               (_seated_player - _winner.size()) +
                                               " among " + _seated_player +
          " players. Your hand histories will be mailed to you"));
      ResponseMessage rm = new ResponseMessage(1, sbuf.toString());
      gp.deliver(rm);
    }
    _winner.add(pr);
  }

  public void declareWinners() throws Exception {
    assert _ht.length <= 1:
        "There should be only one table remaining after the tournament is over";
    //
    if (_ht.length == 1) {
      CricketPresence[] v = _ht[0]._pitch.allPlayers();

      java.util.Arrays.sort(v, new Comparator() {
        public int compare(Object o1, Object o2) {
          return ((CricketPresence) o2).runs() - ((CricketPresence) o1).runs();
        }
      });

      for (int i = 0; i < v.length; i++) {
        addWinner(v[i]);
        CricketPlayer p = v[i].player();
        _cat.finest("Removing player from last table ..." + v[i]);
        if (p instanceof GamePlayer) {
          GamePlayer gp = (GamePlayer) p;
          StringBuffer buf = new StringBuffer();
          buf.append("type=broadcast,id=").append(_ht[0].name()).append(
              ",message=").append(com.agneya.util.Base64.encodeString(
              "The table is closed or you are removed form the table"));
          gp.deliver(new ResponseMessage(1, buf.toString()));
          //v[i].lastMove(Moves.LEAVE);
          _ht[0].setInquirer(v[i]);
          _ht[0]._current = v[i];
          //SitInResponse gdr = new SitInResponse(_ht[0], -78);
          //gp.deliver(new ResponseGameEvent(1, gdr.getCommand(v[i])));
          //_cat.finest("Sending " + gdr.getCommand(v[i]) + "  to " + v[i]);
        }
      }
      _ht[0].destroyTournyTable();
      _cat.finest("Destroyed .." + _ht[0]);

      // TODO log the winners in DB and
      // add money to the real bankroll
      _cat.info("Winner Count = " + _winner.size());
      Enumeration i = _winner.elements();
      while (i.hasMoreElements()) {
        _cat.info("Winner = " + i.nextElement());
      }
    }
    /**
     * init next run
     */
    getScheduleDate();
    _state = FINISH;
  }

  public CricketTourny[] mergeTablesBFLR(CricketTourny[] iht) throws Exception {
    _cat.finest("Start merge...");
    if (iht.length <= 1) {
      // no need to merge
      _cat.finest("Single Table remaining " + iht[0]);
      return iht;
    }

    int total_players_in_game = 0;

    for (int k = 0; k < iht.length; k++) {
      _cat.info("Game State = " + iht[k]);
      total_players_in_game += 0; //iht[k].getPlayerCount();
      //_cat.info("Total Presence on " + iht[k].id() + " is " +
      //          iht[k].getPlayerCount());
      /** Presence v[] = iht[k].getPlayerList();
       for (int l = 0; l < v.length; l++) {
         //_cat.finest("Presence = " + v[l]);
         if (v[l].player() == null) {
           _cat.warning("Player null");
         }
       }**/
    }
    _cat.finest("Total players " + total_players_in_game);

    /**
     * Then remove the tables with lowest number of players, if these can
     * be accomodated on other tables
     */int _tables_required = (total_players_in_game + _team_size - 1) /
                              _team_size;
    _cat.finest("Tables required " + _tables_required);

    Vector _relocated_players = new Vector();
    int k = iht.length - 1;
    if (_tables_required < iht.length) {
      _cat.finest((iht.length - _tables_required) +
                 " TABLES NEED TO BE ELIMINATED");
      /** find out the players who will move **/for (; k >= _tables_required; k--) {
        CricketTourny _to_be_removed = iht[k];
        if (!_to_be_removed.handOver()) {
          _cat.finest("Hand not over on " + _to_be_removed.name());
          continue;
        }
        _cat.finest("Hand over removing " + _to_be_removed.name());
        CricketPresence[] r = null; // TODO iht[k].getPlayerList();
        for (int l = 0; r != null && l < r.length; l++) {
          _cat.finest("Relocating player " + r[l]);
          _relocated_players.add(r[l]);
        }
        _cat.finest("Destroying Game " + iht[k].name());
        _to_be_removed.destroyTournyTable();
        iht[k] = null;
      }

      // accomodate them on remaining tables in ascending order of player strength
      for (int j = 0; j < _relocated_players.size(); j++) {
        _cat.finest("Tourny Moving " + _relocated_players.get(j));
        boolean _deb_seated = false;
        for (int i = 0; i < _tables_required; i++) {
          CricketPresence p = (CricketPresence) _relocated_players.get(j);
          // try to accomodate this player on the table
          Team vac_team = iht[i].getNextVacantTeam();
          int pos = vac_team.nextVacant();
          _cat.finest("Vacant position on " + vac_team + " is " + pos);
          if (pos == -1) {
            continue;
          }
          else {
            // seat this person
            iht[i].addInvite(p);
            p.setPos(pos);
            ((GamePlayer) p.player()).deliver(new ResponseTableOpen(iht[i].name(),
                p.pos())); ((GamePlayer) p.player()).deliver(new
                ResponseGameDetail(1, iht[i].details(p)[0].getCommand(p)));
                Response r = iht[i].moveToTeam(p, iht[i].name(), vac_team);
            ((GamePlayer) p.player()).deliver(new ResponseGameEvent(1,
                r.getCommand(p))); _cat.finest("Tourny Moved " + p + " sending " +
                                              r.getCommand(p));
            _deb_seated = true;
          }
          break;
        }
        if (!_deb_seated) {
          new Exception("Unable to seat player ").printStackTrace();
        }
      }
      // Return the remaining tables
      Vector vht = new Vector();
      for (int i = 0; i < iht.length; i++) {
        if (iht[i] != null) {
          if (iht[i].allPlayers( -1).length > 10) {
            new Exception(iht[i].toString()).printStackTrace();
            System.exit( -1);
          }
          vht.add(iht[i]);
        }
      }
      return (CricketTourny[]) vht.toArray(new CricketTourny[vht.size()]);
    }
    else {
      return iht;
    }

  }

  public boolean isPlaying() {
    return _state == START || _state == RUNNING;
  }

  public boolean isRegOpen() {
    return _state == REG;
  }

  public boolean isWaiting() {
    return _state == JOIN;
  }

  public boolean tournyOver() {
    return _state > END;
  }

  public Game getGame(CricketPresence p) {
    int i = 0;
    /**for (i = 0; i < _ht.length; i++) {
      if (_ht[i].isInvited(p.name())) {
        break;
      }
         }**/
    return _ht[i];
  }

  public CricketTourny[] listAll() {
    return _ht;
  }

  public int delta(Calendar d2, Calendar d1) {
    int delta = 0;

    delta = 518400 * (d1.get(Calendar.YEAR) - d2.get(Calendar.YEAR)) +
            1440 * (d1.get(Calendar.DAY_OF_YEAR) - d2.get(Calendar.DAY_OF_YEAR)) +
            60 * (d1.get(Calendar.HOUR_OF_DAY) - d2.get(Calendar.HOUR_OF_DAY)) +
            d1.get(Calendar.MINUTE) - d2.get(Calendar.MINUTE);
    return delta;
  }


  public String stringValue() {
    StringBuffer buf = new StringBuffer();
    buf.append("name=").append(_name);
    buf.append(",date=").append(_sd.getTime().toGMTString());
    buf.append(",buy-in=").append(_buyin);
    buf.append(",fees=").append(_fees);
    buf.append(",level=").append(_hand_level);
    buf.append(",team_size=").append(_team_size);
    buf.append(",chips=").append("800");
    buf.append(",intervals=").append(_di).append("|").append(_ri).append("|").
        append(_ji);
    buf.append(",state=").append(_state).append(",");
    if (isRegOpen() || isWaiting()) {
      if (_registered.size() > 0) {
        buf.append("player=");
        Iterator enumm = _registered.iterator();
        while (enumm.hasNext()) {
          buf.append(((String) enumm.next())).append("|");
        }
        buf.deleteCharAt(buf.length() - 1);
      }
    }
    else if (isPlaying()) {
      if (_seated_player_list.size() > 0) {
        CricketPresence v[] = (CricketPresence[]) _seated_player_list.toArray(new CricketPresence[
            _seated_player_list.size()]);
        // sort the all-in players in descending order of  their hand strength
        java.util.Arrays.sort(v, new Comparator() {
          public int compare(Object o1, Object o2) {
            return (int) (((CricketPresence) o2).runs() - ((CricketPresence) o1).runs());
          }
        });
        buf.append("player=");
        for (int i = 0; i < v.length; i++) {
          buf.append(v[i].name()).append("|");
        }
        buf.deleteCharAt(buf.length() - 1);
      }
    }
    else if (tournyOver()) {
      if (_winner.size() > 0) {
        buf.append("mttwinners=");
        for (int i = _winner.size() - 1, j = 1; i >= 0; i--, j++) {
          buf.append(((CricketPresence) _winner.get(i)).name()).append("`").append(
              prize(j)).append("|");
        }
        buf.deleteCharAt(buf.length() - 1);
      }
    }
    return buf.toString();
  }

  public String toString() {
    return stringValue();
  }

  public DBTourny dbTourny() {
    return _dbt;
  }

  public int buyIn() {
    return _buyin;
  }

  public int fees() {
    return _fees;
  }

  public String name() {
    return _name;
  }


/// INTERFACE METHODS
  public CricketPresence[] winners() {
    CricketPresence[] v = new CricketPresence[_winner.size()];
    for (int i = _winner.size() - 1; i >= 0; i--) {
      v[i] = ((CricketPresence) _winner.get(i));
    }
    return v;
  }

  public int state() {
    return _state;
  }

  public int chips() {
    return 800;
  }

  public double prize(int i) {
    if (i == 1) {
      return Utils.getRounded(_winner.size() * _buyin * 0.5);
    }
    else if (i == 2) {
      return Utils.getRounded(_winner.size() * _buyin * 0.3);
    }
    else if (i == 3) {
      return Utils.getRounded(_winner.size() * _buyin * 0.2);
    }
    else {
      return 0.00;
    }
  }

  int _counter;
  public int _hand_level;
  int[] _prize_distribution = {50, 30, 20};
  public int[][] _level_limits = { {20, -1}
                                 , {30, -1}
                                 , {50, -1}
                                 , {100, -1}
                                 , {200, -1}
                                 , {300, -1}
                                 , {400, -1}
                                 , {500, -1}
                                 , {600, -1}
                                 , {1200, -1}
                                 , {2400, -1}
                                 , {4800, -1}
                                 , {9600, -1}
  };

  public static void main(String args[]) throws Exception {
    //String name, int[] schedule, double buy_ins, double fees,  int chips, int ts, int di, int ri, int ji, Observer stateObserver


    int[] sch = {2, -1, -1, -1, -1};
    Tourny t = new Tourny("chully", sch, 2, 2, 800, 5, 1, 1, 1, null);

    Calendar c = t.getScheduleDate();
    System.out.println(c);

  }

}
