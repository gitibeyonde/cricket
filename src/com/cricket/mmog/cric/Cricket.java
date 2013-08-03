package com.cricket.mmog.cric;

import java.util.*;
import java.util.logging.Logger;
import com.cricket.mmog.*;
import com.cricket.mmog.GameType;
import com.cricket.mmog.cric.util.Pitch;
import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.mmogserver.GamePlayer;
import com.cricket.common.message.ResponseGameEvent;
import com.agneya.util.Base64;
import com.criconline.anim.AnimationConstants;
import com.cricket.mmog.cric.util.MoveUtils;
import com.agneya.util.Utils;
import com.cricket.mmog.resp.Response;



/** This is the entry point for all black-jack players
 * keep the method synchronized.
 */

public class Cricket extends Game implements Runnable, AnimationConstants {

  // set the category for logging
  static Logger _cat = Logger.getLogger(Cricket.class.getName());


  /**
   * Game interface implementation
   */

  public synchronized Response[] details() {
    _inquirer = null;
    Response[] r = {new GameDetailsResponse(this)};
    return r;
  }

  public synchronized Response[] details(CricketPresence p) {
    _inquirer = p;
    Response[] r = {new GameDetailsResponse(this)};
    return r;
  }


  /**
   * Game interface implementation
   */
  public Response[] summary() {
    _inquirer = null;
    Response[] r = {new GameSummaryResponse(this)};
    return r;
  }

  public synchronized Response[] start() {
    _cat.finest("NEW GAME------------------------");
    setupNewRun();
    if (!reRunCondition()) {
      _cat.finest("Game cannot be started = A" + _pitch.getTeamA() + ", B= " +
                 _pitch.getTeamB());
      _inProgress = false;
      Response[] r = {new GameDetailsResponse(this, true)};
      return r;
    }
    else {
      _state = new GameState(GameState.TOSS);
      Response[] r = {new TossResponse(this, null)};
      return r;
    }
  }

  public void destroy() {
    CricketPresence[] pl = allPlayers(0);
    for (int i = 0; pl != null && i < pl.length; i++) {
      pl[i].setRemoved();
      pl[i].player().removePresence(pl[i]);
    }
    super.remove(this);
  }

  public Response[] observe(CricketPresence p) {
    // check if this presence is already there observe
    for (Iterator i = _observers.iterator(); i.hasNext(); ) {
      Presence op = (Presence) i.next();
      if (op.name().equals(p.name())) {
        _cat.finest("Already an observer " + p + "\n Older = " + op);
        _observers.remove(op);
        break;
      }
    }
    _observers.add(p);
    p.setObserver();

    return details(p);
  }

  public Response[] leaveWatch(CricketPresence p) {
    _cat.finest("Removeing observer " + p);
    _inquirer = p;
    _observers.remove(p);
    p.setRemoved();
    Response[] r = {new CricResponse(this)};
    return r;
  }


  // to promote a poker observer to player status
  public Response[] promoteToPlayer(CricketPresence observer) {
    _observers.remove(observer);
    return join(observer);
  }

  public synchronized Response[] leave(CricketPresence p, boolean timeout) {
    _cat.finest("Removing " + p);
    _inquirer = p;
    p.lastMove(Moves.LEAVE);
    _observers.remove(p);
    //_waiters.remove(p);
    p.setDisconnected();

    if (!validatePresenceInGame(p)) {
      _cat.finest("Removing player not in game" + _pitch);
      //remove(p);
      //Response[] rs = {new LeaveResponse(this, p.pos())};
      return null;
    }

    if (_nextMovePlayers.contains(p) && _inProgress == true) {
      _cat.info("Next move player " + p);

      Response[] r = null;
      //_cat.finest("Next move = " + Moves.stringValue(p._nextMove));
      //player has timed out on move
      if ((Moves.TOSS & p._nextMove) > 0) {
        p.resetNextMove();
        _nextMovePlayers.remove(p);
        addLastMovePresence(p);
        r = processToss(p, Moves.TOSS, null);
      }
      else if ((Moves.HEAD & p._nextMove) > 0 || (Moves.TAIL & p._nextMove) > 0) {
        p.resetNextMove();
        _nextMovePlayers.remove(p);
        addLastMovePresence(p);

        r = processToss(p, Moves.TAIL, null);
      }
      else if ((Moves.FIELDING & p._nextMove) > 0 ||
               (Moves.BATTING & p._nextMove) > 0) {
        p.resetNextMove();
        _nextMovePlayers.remove(p);
        addLastMovePresence(p);

        r = processFieldingOrBatting(p, Moves.FIELDING, null);
      }
      else if ((Moves.K_BAT & p._nextMove) > 0) {
        p.resetNextMove();
        _nextMovePlayers.remove(p);
        addLastMovePresence(p);

        r = processBatting(p, Moves.K_BAT, new MoveParams(MoveParams.BATS));
      }
      else if ((Moves.B_BOWL & p._nextMove) > 0) {
        p.resetNextMove();
        _nextMovePlayers.remove(p);
        addLastMovePresence(p);

        r = processBowling(p, Moves.B_BOWL, new MoveParams(MoveParams.BOWL));
      }
      else if ((Moves.F_FIELD & p._nextMove) > 0) {
        p.resetNextMove();
        _nextMovePlayers.remove(p);
        addLastMovePresence(p);

        r = processFielding(p, Moves.F_FIELD, new MoveParams(MoveParams.FIELD));
      }
      else {
        //complete all other moves
        throw new IllegalStateException(
            "Time out during a move...no defualt move");
      }
      return r;
    }

    if (_inProgress) {
      _cat.info("Player trying to leave a running game keep him around " + p);
      return null;
    }
    else {
      gameCleanup();
      remove(p);
      Response[] r = {new LeaveResponse(this, p.pos())};
      return r;
    }
  }

  protected void remove(CricketPresence p) {
    _pitch.remove(p);
  }

  public boolean validatePresenceInGame(CricketPresence p) {
    return _pitch.onPitch(p);
  }

  public synchronized CricketPresence[] allPlayers(int startPos) {
    return allPlayers();
  }

  public synchronized CricketPresence[] activePlayers(int startPos) {
    return allPlayers();
  }

  public synchronized CricketPresence[] activePlayers() {
    return allPlayers();
  }

  public synchronized CricketPresence[] inActivePlayers(int startPos) {
    return allPlayers();
  }

  public synchronized CricketPresence[] newPlayers(int startPos) {
    return allPlayers();
  }

  public synchronized void stateObserver(Observer obs) {
    if (obs == null) {
      throw new IllegalArgumentException();
    }
    _stateObserver = obs;
  }

  public Presence getPresenceOnPitch(String name) {
    Presence v[] = allPlayers( -1);
    for (int i = 0; i < v.length; i++) {
      if (v[i].name().equals(name)) {
        return v[i];
      }
    }
    return null;
  }


  /**
   * Game interface implementation ENDS
   */

  public Cricket(String name, int type, String tA, String tB,
                 int ball_per_player, int fees, int buyin,
                 Observer stateObserver) {
    assert stateObserver != null:"State Observer can not be null"; 
    _name = name;
    _stateObserver = stateObserver;
    _keepRunning = true;
    _inProgress = false;
    //generate a pattern
    _type = new GameType(type);
    _state = new GameState(GameState.OPEN);
    _pitch = new Pitch(this, name, tA, tB);
    _ball_per_player = ball_per_player;
    _rq = new Vector();
    _fees = fees;
    _buyin = buyin;
    add(this);
    _cat.finest(this.toString());
  }

  public boolean reRunCondition() {
    return _pitch.getTeamA().eligiblePlayers(0).length >= 1 &&
        _pitch.getTeamB().eligiblePlayers(0).length >= 1;
    // any other validations
  }

  public void gameCleanup() {
    _pitch.reset();
  }

  public synchronized void setupNewRun() {
    reset();
    _inProgress = true;
    _pitch.reset();
    for (Iterator i = _observers.iterator(); i.hasNext(); ) {
      Presence pi = (Presence) i.next();
      if (pi.isDisconnected()) {
        _observers.remove(pi);
        i = _observers.iterator();
      }
    }
    if (!reRunCondition()) {
      _grid = -1;
      _inProgress = false;
      return;
    }
    _pot = 0;
    _runs = 0;
    _balls = 0;
    _fvote = 0;
    _bvote = 0;
    _pitch.unsetNew();
    // setup captains
    _pitch.setCaptains();
    _stateObserver.update(this, GameStateEvent.GAME_BEGIN);
    _rq = new Vector(); // remove all the responses
    _pitch.setGrid(_grid);
    _cat.finest(this.toString());
  }

  public void postRun() {
    _stateObserver.update(this, GameStateEvent.GAME_OVER);
    _cat.info("Game Over -----------------------------");
  }

  public void postWin() {
    //
  }


  public synchronized Response[] join(CricketPresence p) {
    _cat.finest(" Joining = " + p);

    if (_pitch.onPitch(p)) {
      _cat.info("Player is already there on the pitch " + p);
      p.lastMove(Moves.NONE);
      Response[] r = {new JoinResponse(this, -9)};
      return r;
    }

    if (!(_pitch.canJoinTeamA(p) || _pitch.canJoinTeamB(p))) {
      // couldn't be added for whatever reason
      p.lastMove(Moves.NONE);
      Response[] r = {new JoinResponse(this, -8)};
      return r;
    }
    p.unsetAllStatus();
    p.lastMove(Moves.JOIN);
    p.setPlayer();
    p.setNew();
    p.setWaiting();
    p.setGID(_name);
    p.reset();
    _cat.finest("Joined " + p + "\n Team=" + p.team());
    if (_inProgress || !reRunCondition()) {
      _cat.finest(_inProgress + ", ReRun = " + reRunCondition());
      Response[] r = {new JoinResponse(this, 4)};
      return r;
    }
    else {
      _inquirer = p;
      return start();
    }
  }

  // @todo : winners on a per pot basis
  public Response[] gameOverResponse(Presence p) {
    _cat.finest("Entering game over response " + p);

    declareWinners();
    Response[] r = {new GameOverResponse(this, null)}; /// for loggin winner

    postRun();
    _inProgress = false;
    //prepareForNewRun(); // if the game can be started else mark all as new
    //Response r = new GameStartResponse(this, showdown_lv);
    //postWin();
    return r;
  }

  public Response[] processToss(CricketPresence p, long move, MoveParams md) {
    //check the move
    _inquirer = p;
    _cat.finest("Next Move size=" + _nextMovePlayers.size() + " Rcvd from " + p +
               ", move=" + move);
    if (_nextMovePlayers.size() > 0) {
      // still waiting for some responses
      return null;
    }
    // TOSS IS OVER
    _cat.finest("TOSS--COMPLETE---sending fielding batting choice to winner");
    _state = new GameState(GameState.TOSS);
    Response[] r = {new TossChoiceResponse(this, md)};

    putDelay(new QResp(QResp.RESPONSE, 3000, r[0]));
    return null;
  }

  public Response[] processFieldingOrBatting(CricketPresence p, long move,
                                             MoveParams md) {
    //check the move
    _inquirer = p;

    if (move == Moves.FIELDING) {
      _fvote++;
    }
    else {
      _bvote++;
    }
    if (_nextMovePlayers.size() > 0) {
      // still waiting for some responses
      return null;
    }
    _cat.finest("All players have voted for fielding/batting");
    _pitch.setFieldingTeamABattingTeamB();
    _state = new GameState(GameState.FIRST_INNING);
    Response[] r = {new BowlResponse(this, md)};
    return r;
  }

  public Response[] processBowling(CricketPresence p, long move, MoveParams md) {
    p.addMP(md);
    //check the move
    _inquirer = p;
    _bowl_md = md;
    _bowl_p = p;
    _cat.info("Bowl params = " + md);
    CricketPresence[] bats = _pitch.getBatsman();
    if (bats == null || bats.length == 0) {
      // bats left
      Response[] r = {new GameDetailsResponse(this)};
      _balls++;
      p.incrBallsBowled();
      return r;

    }
    else {
      Response[] r = {new BatsResponse(this, md, bats[0])};
      _balls++;
      p.incrBallsBowled();
      return r;
    }
  }

  public Response[] processBatting(CricketPresence p, long move, MoveParams md) {
    p.addMP(md);
    //check the move
    _inquirer = p;
    _cat.info("Bats params = " + md);
    _bats_md = md;
    _bats_p = p;

    // check if the timing was correct
    CricketPresence fielder = null;
    if (_bats_md.isBatTimingCorrect(_bowl_md)==HIT) {
      CricketPresence[] pf = _pitch.getBowlerAndFielders();
      for (int i = 0; i < pf.length; i++) {
        long fielder_type = MoveUtils.getFielderMove(_type, md,
            pf[i].status());
        if (fielder_type != NONE) {
          fielder = pf[i];
          break;
        }
      }
    }
    _cat.finest("The fielder is " + fielder);

    if (fielder != null) {
      Response[] r = {new FieldResponse(this, md, fielder)};
      _cat.info("FIELDING RESPONSE " + r[0]);
      return r;
    }
    else {
      //no fielding move
      for (Iterator e = _nextMovePlayers.iterator(); e.hasNext(); ) {
        CricketPresence nmp = (CricketPresence) e.next();
        _cat.finest("NMP = " + nmp);
        nmp.resetNextMove();
      }
      _nextMovePlayers.clear();
      //putDelay(new QResp(QResp.RESPONSE, 4000, new LastMoveResponse(this, md)));
      _cat.info("XXXXXXXXXXX   NO FIELDING RESPONSE ");
      return processFielding(p, move, md);
    }
  }


  public Response[] processBatsRun(CricketPresence p, long move, MoveParams md) {
    p.addMP(md);
    //check the move
    _inquirer = p;

    if (_nextMovePlayers.size() > 0) {
      // still waiting for some responses
      return null;
    }

    _cat.info("Run params = " + md);
    _runs_md = md;
    // response none ???
    return null;
  }


  public Response[] processFielding(CricketPresence p, long move, MoveParams md) {
    //check the move
    _inquirer = p;
    if (_nextMovePlayers.size() > 0) {
      // still waiting for some responses
      return null;
    }

    Response[] last_move = {new LastMoveResponse(this, md)};

    if (move == Moves.K_BAT) {
      int runs = MoveUtils.calculateRun(_bowl_md, _bats_md, null);
      _runs += runs;
      _bats_p.incrBallsPlayed();
      _bats_p.addRuns(runs);
    }
    else {
      p.addMP(md);
      int runs = MoveUtils.calculateRun(_bowl_md, _bats_md, md);
      _runs += runs;
      _bats_p.incrBallsPlayed();
      _bats_p.addRuns(runs);
    }

    Response[] inning_over = {new InningOverResponse(this, md)};
    Response[] bats_change = {new BatsChangeResponse(this, md)};

    _cat.info("Fielders move received bowler adv balls played=" +
              _bats_p.ballsPlayed() + ", ts=" +
              _pitch._teamA.activePlayers(0).length + ", bp=" +
              _ball_per_player);
    if (_bats_p.ballsPlayed() >=
        (_pitch._teamA.activePlayers(0).length * _ball_per_player)) {
      _bats_p.unsetBatsman();
      _cat.info("CHANGING BATSMAN OLD = " + _bats_p);
      if (!_pitch.advanceBatsman()) {
        _cat.info("advance batsman " + _pitch);
        _pitch.toggleBattingFielding();
        _cat.info("toggling " + _pitch);
        Presence[] check_bats = _pitch.getBatsman();
        ////////////////// GAME OVER
        if (check_bats == null || check_bats.length == 0) {
          Response[] r = gameOverResponse(p);
          putDelay(new QResp(QResp.RESPONSE, 12000, r[0]));
          _cat.info("<<<<<<<<<<<<GAME OVER = >>>>>>>>>>>>>>>>>>");
          putDelay(new QResp(QResp.NEW_GAME_START, 12000, null));
          return last_move;
        }
        else {
          _cat.info("----------CHANGE = -------------" + inning_over[0]);
          _state = new GameState(GameState.SECOND_INNING);

          _balls = 0;
          _runs = 0;
          if (move == Moves.K_BAT) {
            _cat.info("No field response ");
            putDelay(new QResp(QResp.RESPONSE, 3000, inning_over[0]));
            putDelay(new QResp(QResp.RESPONSE, 6000, new BowlResponse(this, null)));
            return last_move;
          }
          else {
            _cat.info("Fields response ");
            putDelay(new QResp(QResp.RESPONSE, 3000, inning_over[0]));
            putDelay(new QResp(QResp.RESPONSE, 4000, new BowlResponse(this, null)));
            return last_move;
          }
        }
      }
      else {
        putDelay(new QResp(QResp.RESPONSE, 3000, bats_change[0]));
        putDelay(new QResp(QResp.RESPONSE, 6000, new BowlResponse(this, null)));
        _cat.info("BATS CHANGES " + bats_change[0]);
        return last_move;
      }
    }
    else { // NO PITCH CHANGES
      // check if bowler needs to be changed
      if (_bowl_p.ballsBowled() >= _ball_per_player) {
        if (!_pitch.advanceBowler()) {
          // there is no one to bowl
          _cat.info("There is no one to bowl");
          start();
        }
      }
      if (move == Moves.K_BAT) {
        _cat.info("NO FIELD MOVE " + _pitch);
        putDelay(new QResp(QResp.RESPONSE, 6000, new BowlResponse(this, null)));
        return last_move;
      }
      else {
        _cat.info("FIELD MOVE " + _pitch);
        putDelay(new QResp(QResp.RESPONSE, 6000, new BowlResponse(this, null)));
        return last_move;
      }
    }
  }

  public StringBuffer getWinnersString() {
    CricketPresence[] v = _pitch.allPlayers();
    java.util.Arrays.sort(v, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((CricketPresence) o2).runs() - ((CricketPresence) o1).runs();
      }
    });
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("winners=");
    for (int i = 0; v != null && i < v.length; i++) {
      sbuf.append(v[i].name()).append("|");
      v[i].setWin((int) (_pot * getWinnerFraction(i)));
      sbuf.append((int) (_pot * getWinnerFraction(i))).append("|");
      sbuf.append(v[i].runs()).append("`");
    }
    return sbuf.deleteCharAt(sbuf.length() - 1).append(",");
  }

  public void declareWinners() {
    CricketPresence[] v = _pitch.allPlayers();
    // sort the all-in players in descending order of  their hand strength
    java.util.Arrays.sort(v, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((CricketPresence) o2).runs() - ((CricketPresence) o1).runs();
      }
    });

    for (int i = 3; v != null && i < v.length; i++) {
      v[i].addWin();
      _cat.finest("Winner = " + v[i] + ", " + v[i].runs());
    }
  }

  public double getWinnerFraction(int i) {
    if (_type.isOnePlayer()) {
      switch (i) {
        case 0:
          return 1;
        default:
          return 0;
      }
    }
    else {
      switch (i) {
        case 0:
          return .5;
        case 1:
          return .3;
        case 2:
          return .2;
        default:
          return 0;
      }
    }
  }

  public void sendAll(Response gdr, boolean broadcast) {
    CricketPresence[] v = _pitch.allPlayers();
    for (int i = 0; v != null && i < v.length; i++) {
      if (v[i].isResponseReq()) {
        v[i].setStartWait();
      }
      CricketPlayer p = (CricketPlayer) v[i].player();
      if (p instanceof GamePlayer) {
        GamePlayer gp = (GamePlayer) p;
        gp.deliver(new ResponseGameEvent(1,
                                         broadcast ? gdr.getBroadcast() :
                                         gdr.getCommand(v[i])));
        /**_cat.finest(gp.name() + "-->" +
                   (broadcast ? gdr.getBroadcast() : gdr.getCommand(v[i])) +
                   "  to " + p);**/

      }
    }

    v = (CricketPresence[]) _observers.toArray(new Presence[_observers.size()]);
    for (int i = 0; v != null && i < v.length; i++) {
      CricketPlayer p = (CricketPlayer) v[i].player();
      if (p instanceof GamePlayer) {
        GamePlayer gp = (GamePlayer) p;
        gp.deliver(new ResponseGameEvent(1, gdr.getBroadcast()));
        //_cat.finest(gp.name() + "--> " + gdr.getBroadcast() + "  to " + p);
      }
    }
  }

  public Response[] processDrinks(CricketPresence p, long move, MoveParams md) {
    //check the move
    _inquirer = p;
    if (_nextMovePlayers.size() > 0) {
      // still waiting for some responses
      return null;
    }
    Response[] r = {null};
    return r;
  }

  public Response[] startPractise(CricketPresence p, long move, MoveParams md) {
    //check the move
    _inquirer = p;
    p.setBatsman();
    // TOSS IS OVER
    _cat.finest("START " + p);
    Response[] r = {new PractiseResponse(this,
                                         MoveParams.generate(MoveParams.BOWL))};
    return r;
  }

  public Response[] illegalMove(CricketPresence p, long move) {
    p.lastMove(move);
    _inquirer = p;
    Response[] r = {new IllegalReqResponse(this)};
    return r;
  }


  //public presence

  public synchronized Response[] chat(CricketPresence p, String message) {
    _cat.finest(message);
    _cat.finest(Base64.decode(message).toString());
    if (p == null) {
      Response[] r = {new MessagingResponse(this, message)};
      return r;
    }
    else {
      Response[] r = {new MessagingResponse(this, p, message)};
      return r;
    }
  }

  public int getPlayerCount() {
    return _pitch.allPlayers().length;
  }

  public Set observers() {
    return _observers;
  }

  public CricketPresence[] getObservers() {
    return (CricketPresence[]) _observers.toArray(new CricketPresence[_observers.size()]);
  }

  public String name() {
    return _name;
  }

  public CricketPresence inquirer() {
    return _inquirer;
  }

  public CricketPresence[] allPlayers() {
    return _pitch.allPlayers();
  }

  public void setInquirer(CricketPresence player) {
    _inquirer = player;
  }

  public void mvId(int mvId) {
    this.mvId = mvId;
  }

  public boolean inProgress() {
    return _inProgress;
  }

  public String _name; //= "Some-Blackjack";

  protected Set<CricketPresence> _observers = Collections.synchronizedSet(new HashSet<CricketPresence>());

  protected Pitch _pitch;
  protected int _toss_winner = -1;
  private boolean _replay = false;
  protected int _ball_per_player = 0;
  protected int _balls, _runs;
  protected int _fvote, _bvote;
  protected int _pot;
  protected MoveParams _bats_md, _bowl_md, _runs_md;
  protected CricketPresence _bats_p, _bowl_p;

  protected boolean runAgain;


  // debug var
  public volatile int mvId;
  public int expPos;


  // @todo : make true random

  public String toString() {
    return "grid=" + _grid + ", name=" + _name + ", type=" +
        _type + ", pitch=" + _pitch + ", balls/plr=" + _ball_per_player;
  }

  public void run() {
    QResp qr = (QResp) _rq.remove(0);
    long del = qr._delay;
    int type = qr._type;
    Response resp = qr._r;
    try {
      _cat.finest("Before sleep-------------------------------");
      Thread.currentThread().sleep(del);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    switch (type) {
      case QResp.RESPONSE:
        sendAll(resp, false);
        break;

      case QResp.NEW_GAME_START:
        sendAll(start()[0], false);
        break;

      default:
        _cat.warning("Delay type not recognized .........." + _type);
    }
    _cat.finest("After send -------------------------------------");
  }

  public synchronized void putDelay(QResp qr) {
    //new Exception(qr._r.getBroadcast().toString()).printStackTrace();
    _rq.add(qr);
    Thread t = new Thread(this);
    t.start();
  }


  // timed moves
  public Vector _rq;
  public class QResp {
    public Response _r = null;
    public long _delay = -1;
    public int _type = -1;
    public final static int NEW_GAME_START = 1;
    public final static int RESPONSE = 2;

    public QResp(int type, long delta, Response r) {
      _type = type;
      _delay = delta;
      _r = r;
    }
  }

}
