package com.cricket.mmog.cric.impl;

import com.cricket.mmog.*;
import com.cricket.mmog.cric.*;
import com.cricket.mmog.resp.Response;

import java.util.*;

import java.util.logging.Logger;
import com.agneya.util.Utils;
import com.cricket.mmogserver.GamePlayer;
import com.cricket.common.message.ResponseGameEvent;
import com.cricket.mmog.gamemsgimpl.GameDetailsImpl;
import com.cricket.common.message.ResponseTournyDetail;
import com.cricket.common.interfaces.TournyPitchInterface;
import com.cricket.mmogserver.GameProcessor;
import com.cricket.mmog.cric.util.Pitch;
import com.cricket.mmog.cric.util.Team;

public class CricketTourny extends Cricket implements TournyPitchInterface {

  // set the category for logging
  static Logger _cat = Logger.getLogger(CricketTourny.class.getName());

  public synchronized Response[] start() {
    _cat.finest("NEW GAME------------------------" + this);
    _pitch.reset();
    if (!reRunCondition()) {
      _cat.finest("Game " + name() + " cannot be started = A" + _pitch.getTeamA() +
                 ", B= " + _pitch.getTeamB());
      _inProgress = false;
      _tourny_state = MATCH_NOSTART;
      Response[] r = {new GameDetailsResponse(this)};
      return r;
    }
    else {
      setupNewRun();
      _tourny_state = MATCH_RUNNING;
      _state = new GameState(GameState.TOSS);
      Response[] r = {new TossResponse(this, null)};
      return r;
    }
  }

  public CricketTourny(int id, String name, int minPlayers, int maxPlayers,
                       String[] affiliate, Observer stateObserver,
                       Tourny tourny) {
    super(name, GameType.WORLD_CUP, "TEAM_A", "TEAM_B", 2, 0, 0,
          stateObserver);
    _type = new GameType(GameType.WORLD_CUP);
    this.tournyId = tourny.name();
    _t = tourny;
  }


  public boolean reRunCondition() {
    return _pitch.getTeamA().eligiblePlayers(0).length >= 4 &&
        _pitch.getTeamB().eligiblePlayers(0).length >= 4;
    // any other validations
  }


  public synchronized void setupNewRun() {
    _inProgress = true;
    // setup captains
    _pitch.setCaptains();
    //resetInvite();
    _stateObserver.update(this, GameStateEvent.GAME_BEGIN);
    _pitch.setGrid(_grid);
    _cat.finest(this.toString());
  }

  public synchronized Response[] join(CricketPresence p) {
    _cat.finest(" Joining = " + p);
    p.unsetAllStatus();

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
    p.lastMove(Moves.JOIN);
    p.setPlayer();
    p.setNew();
    p.setWaiting();
    p.setGID(_name);
    p.reset();
    _cat.finest("Joined " + p + "\n Team=" + p.team());
    _cat.finest(_inProgress + ", ReRun = " + reRunCondition());
    Response[] r = {new JoinResponse(this, 4)};
    return r;
  }

  public void destroyTournyTable() {
    CricketPresence[] v = allPlayers( -1);
    for (int i = 0; v != null && i < v.length; i++) {
      GamePlayer gp = (GamePlayer) v[i].player();
      gp.deliver(new com.cricket.common.message.ResponseString(1, com.cricket.common.message.Response.R_TABLE_CLOSED, name()));
      _cat.finest("Destroy = " + v[i]);
    }
    for (Iterator i = _observers.iterator(); i.hasNext(); ) {
      CricketPresence p = (CricketPresence) i.next();
      GamePlayer gp = (GamePlayer) p.player();
      gp.deliver(new com.cricket.common.message.ResponseString(1, com.cricket.common.message.Response.R_TABLE_CLOSED, name()));
      //_cat.finest("Destroy Observer = " + p);
    }
    super.remove(this);
  }


  // a player cannot leave a tourny
  public synchronized Response[] leave(CricketPresence p, boolean timeout) {
    p.setDisconnected();
    _cat.finest("Leaving Tourny " + p);
    return null;
  }

  public Response moveToTeam(CricketPresence p, String tid, Team t) {
    _cat.finest("Moving  " + p);
    p.player().movePresence(p, tid);
    if (!(t.canJoin(p))) { // couldnt be added for whatever reason
      return new GameDetailsResponse(this);
    }
    _cat.finest("Moved " + p);
    _inquirer = p;
    p.unsetNew();
    p.lastMove(Moves.MOVE);
    _current = p;
    Response r = new JoinResponse(this, 6);
    _cat.finest(r.getBroadcast());
    return r;
  }


  // @todo : winners on a per pot basis
  public Response[] gameOverResponse(CricketPresence p) {
    _cat.finest("Entering game over response " + p);

    declareWinners();
    postRun();
    Response[] r = {new GameOverResponse(this, null)}; /// for loggin winner

    _inProgress = false;
    _tourny_state = MATCH_OVER;
    postWin();
    return r;
  }

  public void declareWinners() {
    CricketPresence[] v = _pitch.allPlayers();
    // sort the all-in players in descending order of  their hand strength
    java.util.Arrays.sort(v, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((CricketPresence) o2).runs() - ((CricketPresence) o1).runs();
      }
    });

    for (int i = v.length-1; v != null && i >= 3; i--) {
      _t.addWinner(v[i]);
      remove(v[i]);
      _cat.finest("Winner = " + v[i] + ", " + v[i].runs());
    }
  }

  public Team getNextVacantTeam() {
    _cat.finest("Pitch = " + _pitch);
    _cat.finest("A=" + _pitch.getTeamA() + ", B=" + _pitch.getTeamB());
    _cat.finest("A count=" + _pitch.getTeamA().count() + ", B count =" +
               _pitch.getTeamB().count());
    if (_pitch.getTeamA().count() < _pitch.getTeamB().count()) {
      return _pitch.getTeamA();
    }
    else {
      return _pitch.getTeamB();
    }
  }

  public void addWinner(CricketPresence p) {
    _t.addWinner(p);
  }

  public boolean isInWinner(CricketPresence p) {
    return _t._winner.contains(p);
  }

  public boolean started() {
    return _tourny_state == MATCH_RUNNING;
  }

  public boolean handOver() {
    return _tourny_state == MATCH_OVER || _tourny_state == MATCH_NOSTART;
  }

  public boolean tournyOver() {
    return _t.tournyOver();
  }

  public boolean tournyWaiting() {
    return _t.isWaiting();
  }

  public boolean canHandStart() {
    return _tourny_state != MATCH_NOSTART;
  }

  int _tourny_state = MATCH_INIT;
  final static int MATCH_INIT = 1;
  final static int MATCH_OVER = 1;
  final static int MATCH_RUNNING = 2;
  final static int MATCH_NOSTART = 4;

  Tourny _t;

  public String toString() {
    return super.toString() + ", State=" + _state;
  }

}
