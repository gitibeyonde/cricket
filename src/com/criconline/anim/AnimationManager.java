package com.criconline.anim;

import java.util.NoSuchElementException;
import java.awt.Rectangle;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;
import com.criconline.pitch.PitchSkin;
import java.util.Observer;
import java.util.Observable;
import java.awt.Point;
import java.util.logging.Logger;
import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.mmog.cric.util.ActionConstants;
import com.criconline.actions.MoveAction;
import com.cricket.mmog.PlayerStatus;
import com.criconline.SoundManager;
import com.criconline.actions.MoveRequestAction;
import com.criconline.models.PlayerModel;
import com.criconline.actions.LastMoveAction;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.SwingUtilities;
import com.criconline.BatsmanPlayerView;
import java.util.Comparator;
import com.cricket.mmog.CricketPlayer;
import com.criconline.CricketController;
import com.cricket.mmog.cric.util.MoveUtils;
import com.criconline.ClientPlayerController;
import com.criconline.FielderPlayerView;

public class AnimationManager extends TimerTask implements Observer,
    AnimationConstants {
  static Logger _cat = Logger.getLogger(AnimationManager.class.getName());

  static Object _dummy = new Object();
  private static Hashtable _am_registry = null;
  String _gid;

  private Hashtable _animators_registry = null;
  int _anim_seq;
  AnimationQueue _queue;
  boolean _keepservicing = true;
  Hashtable _fielders_registry = null;
  public boolean _detect_collision = false;
  PlayerModel _pm = null;
  MoveBuffer _mb;
  MovePoller _mp;
  public double _speed_factor = 1;
  public double _ball_speed_factor = 1;
  private int _frame_count;
  private int _frame_rate;
  private CricketController _cc;
  private boolean _enableCameraMove, _applause;

  private int _cX, _cY;

  static {
    _am_registry = new Hashtable();
  }

  public static AnimationManager instance(String tid) {
    if (_am_registry.get(tid + "") == null) {
      synchronized (_dummy) {
        if (_am_registry.get(tid + "") == null) {
          _am_registry.put(tid + "", new AnimationManager(tid));
        }
      }
    }
    return (AnimationManager) _am_registry.get(tid + "");
  }

  private AnimationManager(String tid) {
    assert tid.length() > 0:tid + " Game id is valid !!";
    _animators_registry = new Hashtable();
    _queue = new AnimationQueue();
    _mb = MoveBuffer.instance(tid);
    _mb.setAnimationManager(this);
    _gid = tid;
    _frame_rate = 10;
    _frame_count = 0;
    Timer t = new Timer();
    //t.scheduleAtFixedRate(this, 0, _frame_rate);
    t.schedule(this, 0, _frame_rate);
  }

  public void update(Observable o, Object arg) {
    _cat.info("-----------------" + _gid + "-----------------------" + arg);
    try {
      if (arg == Gse.TOSS) {
        camReset();
        LastMoveAction lma = (LastMoveAction) o;
        AnimationEvent toss_coin = new AnimationEvent("toss_coin", MISC, 0);
        animate(toss_coin);
        SoundManager.playEffect(SoundManager.TOSS);
        AnimationEvent speak = new AnimationEvent(lma.getTeam() + " Toss",
                                                  SPEAKS, 0);
        animate(speak);
      }
      else if (arg == Gse.NM_TOSS) {
        camReset();
        AnimationEvent toss = new AnimationEvent("toss", BOTTOMPANEL, 0);
        animate(toss);
        AnimationEvent fe = new AnimationEvent("get_focus", BOTTOMPANEL, 0);
        animate(fe);

      }
      else if (arg == Gse.HEAD) {
        camReset();
        LastMoveAction lma = (LastMoveAction) o;
        AnimationEvent speak = new AnimationEvent(lma.getTeam() + " Head",
                                                  SPEAKS, 0);
        animate(speak);
      }
      else if (arg == Gse.TAIL) {
        camReset();
        LastMoveAction lma = (LastMoveAction) o;
        AnimationEvent speak = new AnimationEvent(lma.getTeam() + " Tail",
                                                  SPEAKS, 0);
        animate(speak);
      }

      /////////BOWLER//////////////////BOWLER/////////////BOWLER////////////////////////
      else if (arg == Gse.B_NM_BOWL) {
        camReset();
        speedUpForPlay();

        AnimationEvent bats = new AnimationEvent("reset", BOWL, 0);
        animate(bats);

        // show the bowl button on bottom panel
        AnimationEvent bowl_move = new AnimationEvent("bowl_move", BOTTOMPANEL,
            0);
        animate(bowl_move);

        AnimationEvent pp = new AnimationEvent("enable_pitch", BALL, 0);
        animate(pp);
      }
      else if (arg == Gse.BOWLER_MOVE_BEGIN) { // Prev: B_NM_BOWL
        AnimationEvent pp = new AnimationEvent("disable_pitch", BALL, 0);
        animate(pp);
        // enable the spin
      }
      else if (arg == Gse.B_BOWL) {
        update(o, Gse.BOWLER_RUN);
      }
      else if (arg == Gse.B_NM_FIELD) {
        camReset();
        speedUpForPlay();
        speedUpBallForPlay();
        AnimationEvent pp = new AnimationEvent("enable_fielder_move", BOWL, 0);
        animate(pp);

        AnimationEvent bowl_move = new AnimationEvent("field_move", BOTTOMPANEL,
            0);
        animate(bowl_move);
      }
      else if (arg == Gse.B_LM_FIELD) {
        setNormal();
        setNormalBall();

        // show field move only if I am not the fielder
        LastMoveAction lma = (LastMoveAction) _mb.getLastMove(FIELD);
        MoveParams mp = lma.getMoveParams();
        ClientPlayerController fielder = _cc.getPlayer(lma.getTeam(),
            lma.getPosition());
        _cat.finest(fielder.toString());
        _cat.finest(lma.toString());
        _cat.finest(_pm.toString());

        MoveRequestAction nm = _mb.removeNextMove(FIELD);
        _cat.finest("Got field next move " + nm);
        if (nm != null) {
          if (isObserver() || _pm.isBats() || (_pm.isFielder() && !_pm.isBowl()) ||
              _pm.isWaiting()) {
            _cat.finest("Setting next move fielding for bowler/bats/observer ");
            update(o, Gse.FIELDING_START);
          }
          else if (_pm.isBowl()) {
            if (_pm.getPlayerStatus().getPlayerType() !=
                fielder.getState().getPlayerType()) {
              _cat.finest("Setting nextmove field for other fielders " +
                         _pm.getPlayerStatus());
              update(o, Gse.FIELDING_START);
            }
          }
          else {
            throw new IllegalStateException(_pm.toString());
          }
        }
        else {
          AnimationEvent fd = new AnimationEvent("default", FIELD, 0);
          animate(fd);
        }
      }
      else if (arg == Gse.B_LM_BAT) {
        setNormal();
        setNormalBall();
        //update(o, Gse.BATSMAN_HIT_START); BATSMAN HIT will automatically kick in after 40 frames
      }

      //////BATSMAN///////////////BATSMAN///////////////BATSMAN//////////////////
      else if (arg == Gse.K_NM_BOWL) {
        // do nothing
      }
      else if (arg == Gse.K_LM_BOWL) {
        setNormal();
        setNormalBall();
        AnimationEvent wkr = new AnimationEvent("reset", BOWL, 0);
        animate(wkr);

        update(o, Gse.BOWLER_RUN);
      }
      else if (arg == Gse.K_NM_BAT) {
        camReset();
        speedUpForPlay();
        speedUpBallForPlay();
        AnimationEvent bowl_move = new AnimationEvent("bat_move", BOTTOMPANEL,
            0);
        animate(bowl_move);
        AnimationEvent bats = new AnimationEvent("ready", BATS, 0);
        animate(bats);
        AnimationEvent pp = new AnimationEvent("enable_bats_move", BATS, 0);
        animate(pp);
      }
      else if (arg == Gse.BATS_MOVE_IN_HIT_MODE) {
        AnimationEvent pp = new AnimationEvent("enable_bats_shots", BATS, 0);
        animate(pp);
      }
      else if (arg == Gse.K_BAT) {
        update(o, Gse.BATSMAN_HIT_START);
      }
      else if (arg == Gse.K_LM_FIELD) {
        setNormal();
        setNormalBall();
        update(o, Gse.FIELDING_START);
      }

      ////////FIELDER//////////////////////FIELDER///////////////////FIELDER////////////////
      else if (arg == Gse.F_LM_BOWL) {
        setNormal();
        setNormalBall();
        update(o, Gse.BOWLER_RUN);
      }
      else if (arg == Gse.F_NM_FIELD) {
        camReset();
        speedUpForPlay();
        speedUpBallForPlay();
        AnimationEvent pp = new AnimationEvent("enable_fielder_move", FIELD, 0);
        animate(pp);

        AnimationEvent bowl_move = new AnimationEvent("field_move", BOTTOMPANEL,
            0);
        animate(bowl_move);
      }
      else if (arg == Gse.F_FIELD) {
        AnimationEvent pp = new AnimationEvent("disable_fielder_move", FIELD, 0);
        animate(pp);
        AnimationEvent pp1 = new AnimationEvent("disable_fielder_move", BOWL, 0);
        animate(pp1);

        update(o, Gse.FIELDING_START);
      }
      else if (arg == Gse.F_LM_BAT) {
        setNormal();
        setNormalBall();
      }
      else if (arg == Gse.F_LM_FIELD) {
        setNormal();
        setNormalBall();

        // show field move only if I am not the fielder
        LastMoveAction lma = (LastMoveAction) _mb.getLastMove(FIELD);
        MoveParams mp = lma.getMoveParams();

        ClientPlayerController fielder = _cc.getPlayer(lma.getTeam(),
            lma.getPosition());
        _cat.finest(fielder.toString());
        _cat.finest(lma.toString());

        MoveRequestAction nm = _mb.removeNextMove(FIELD);
        _cat.finest("Got field next move " + nm);
        if (nm != null) {
          if (isObserver() || _pm.isBats() || _pm.isWaiting()) {
            _cat.finest("Setting next move fielding for bowler/bats/observer ");
            update(o, Gse.FIELDING_START);
          }
          else if (_pm.isFielder()) {
            if (_pm.getPlayerStatus().getPlayerType() !=
                fielder.getState().getPlayerType()) {
              _cat.finest("Setting nextmove field for other fielders " +
                         _pm.getPlayerStatus());
              update(o, Gse.FIELDING_START);
            }
          }
          else {
            throw new IllegalStateException(_pm.toString());
          }
        }
      }

      /********************************BOWLING ACTION*******************************/

      else if (arg == Gse.BOWLER_RUN) { // Prev: K_LM_BOWL, B_BOWL, F_LM_BOWL
        // clear all the last moves
        if (isObserver() || !_pm.isBowl()) { // if not a bowler reset the speeds
          camReset();
          AnimationEvent ballStop = new AnimationEvent("ball_stop", BALL, 25);
          animate(ballStop);
        }
        _mb.removeLastMove(FIELD);
        _mb.removeLastMove(BATS);
        _mb.removeNextMove(FIELD);
        _cat.finest("This Player Model=" + _pm);

        AnimationEvent field = new AnimationEvent("reset", FIELD, 0);
        animate(field);

        AnimationEvent bats = new AnimationEvent("ready", BATS, 0);
        animate(bats);

        AnimationEvent wkr = new AnimationEvent("ready", WICKET_KEEPER, 0);
        animate(wkr);

        AnimationEvent runup = new AnimationEvent("runup", BOWL, 0);
        runup.setNextEvent(o, Gse.BALL_THROW, 26);
        animate(runup);

        SoundManager.playEffect(SoundManager.BOWLER_RUN);
      }
      else if (arg == Gse.BALL_THROW) { // Prev: BOWLER_RUN
        AnimationEvent ball_throw = new AnimationEvent("ball_throw", BALL, 0);
        ball_throw.setAttach(o);
        if (isObserver() || !_pm.isBats()) {
          _cat.finest("Setting move batsman_hit_start");
          ball_throw.setNextEvent(o, Gse.BATSMAN_HIT_LOOKUP, 0);
          ball_throw.setNextEvent(o, Gse.BATSMAN_HIT_START, 74); // ball pos when the hit starts
        }
        animate(ball_throw);
      }

      /********************************BATSMAN HIT*******************************/
      else if (arg == Gse.BATSMAN_HIT_LOOKUP) { // Prev: BALL_THROW
        // start a thread to look for last move bat
        int hook_count = -1;
        _cat.finest(">>>>>>>>>>SF=" + _speed_factor + " HC=" + hook_count);
        _mp = new MovePoller(this, _mb, BATS, Gse.UNKNOWN, 200, hook_count);
        _mp.setSlowFactor(10);
        _cat.finest(
            "Starting thread to look for bat last move ------------------------------" +
            _mp);
        addAnimator(_mp);
      }
      else if (arg == Gse.BATSMAN_HIT_START) { // Prev: K_BAT
        removeAnimator(_mp);
        AnimationEvent pp = new AnimationEvent("disable_bats_move", BATS, 0);
        animate(pp);
        setNormal();
        setNormalBall();
        LastMoveAction lma = (LastMoveAction) _mb.getLastMove(BATS);
        if (lma == null) {
          update(lma, Gse.BATSMAN_MISS);
          return;
        }
        MoveParams mp = lma.getMoveParams();
        MoveParams bp = _mb.getLastMove(BOWL).getMoveParams();
        _cc.addCommentary(MoveParams.getAnalysis(bp, mp));
        _cat.info("Ball=" + bp + "\nBat=" + mp);
        int btim = mp.isBatTimingCorrect(bp);
        if (btim == HIT) {
          AnimationEvent bathit = MoveUtils.getBatsAnimation(mp);
          _cat.info(bathit.toString());
          bathit.setAttach(lma);
          bathit.setNextEvent(lma, Gse.BATSMAN_HIT, 8); // delays ball hit
          animate(bathit);
        }
        else if (btim == EARLY_HIT || btim == LATE_HIT) {
          update(lma, Gse.BATSMAN_MISS);
        }
        else if (btim == WIDE) {
          update(lma, Gse.BATSMAN_MISS_WIDE);
        }
      }
      else if (arg == Gse.BATSMAN_HIT) { // Prev: BATSMAN_HIT_START
        SoundManager.playEffect(SoundManager.BAT_HIT);
        LastMoveAction lma = (LastMoveAction) o;
        _cat.finest("Last move " + lma);
        AnimationEvent ball_flight = new AnimationEvent("ball_flight", BALL, 7);
        ball_flight.setAttach(lma);
        _detect_collision = true;
        _enableCameraMove = true;

        // start looking for field move
        MoveRequestAction nm = _mb.getNextMove(FIELD);
        _cat.finest("Got field next move " + nm);
        _cat.finest("Fielder = " +
                   MoveUtils.whichFielderStr(_cc.getGameType(),
                                             lma.getMoveParams()));
        if (nm != null) {
          if (isObserver() || _pm.isBats() || _pm.isWaiting()) {
            _cat.finest("Setting move fielding lookup start ");
            ball_flight.setNextEvent(o, Gse.FIELDING_LOOKUP, 0);
          }
          else if (_pm.isFielder() || _pm.isBowl()) {
            ClientPlayerController fielder = _cc.getPlayer(nm.getTeam(),
                nm.getPosition());
            _cat.finest(fielder.toString());
            if (_pm.getPlayerStatus().getPlayerType() !=
                fielder.getState().getPlayerType()) {
              _cat.finest("Setting move fielding lookup start fielding " +
                         _pm.getPlayerStatus());
              ball_flight.setNextEvent(o, Gse.FIELDING_LOOKUP, 0);
            }
          }
          else {
            throw new IllegalStateException(_pm.toString());
          }
        }
        else if (_pm != null && _pm.isBats() &&
                 _cc.isFielderPresent(MoveUtils.whichFielder(_cc.getGameType(), lma.getMoveParams()))) {
          _cat.finest("Setting move fielding lookup start ");
          ball_flight.setNextEvent(o, Gse.FIELDING_LOOKUP, 0);
          // next move is null, this is the batsman....have not yet recieved the field next move
        }
        animate(ball_flight);
        AnimationEvent wkr = new AnimationEvent("default", WICKET_KEEPER, 0);
        animate(wkr);
      }
      else if (arg == Gse.BATSMAN_MISS) { // Prev: BATSMAN_HIT_START
        // remove the field next move if batsman misses
        MoveRequestAction nm = _mb.removeNextMove(FIELD);


        AnimationEvent latehit = new AnimationEvent("miss", BATS, 0);
        animate(latehit);
        AnimationEvent wcblock = new AnimationEvent("block", WICKET_KEEPER, 0);
        animate(wcblock);
        AnimationEvent ballStop = new AnimationEvent("ball_stop", BALL, 25);
        animate(ballStop);
        // check if the ball will hit the stunmp
        MoveParams bp = _mb.getLastMove(BOWL).getMoveParams();
        _cat.finest("BATSMAN MISS  " + bp);
        if (bp.isBold()) {
          update(null, Gse.BATSMAN_OUT);
        }
      }
      else if (arg == Gse.BATSMAN_MISS_WIDE) { // Prev: BATSMAN_HIT_START
        // remove the field next move if batsman misses
        MoveRequestAction nm = _mb.removeNextMove(FIELD);
        AnimationEvent latehit = new AnimationEvent("wide", BATS, 0);
        animate(latehit);
        AnimationEvent wcblock = new AnimationEvent("block", WICKET_KEEPER, 0);
        animate(wcblock);
        AnimationEvent ballStop = new AnimationEvent("ball_stop", BALL, 25);
        animate(ballStop);

      }
      else if (arg == Gse.BATSMAN_OUT) {
        SoundManager.playEffect(SoundManager.OUT_WICKET);
        AnimationEvent stumps = new AnimationEvent("stumps_fall", MISC, 5);
        animate(stumps);
      }
      else if (arg == Gse.BATSMAN_DEFAULT) {
        AnimationEvent pp = new AnimationEvent("remove_pitch", BALL, 0);
        animate(pp);
        AnimationEvent bats = new AnimationEvent("reset", BOWL, 0);
        animate(bats);
        AnimationEvent bowl = new AnimationEvent("ready", BATS, 0);
        animate(bowl);
        AnimationEvent field = new AnimationEvent("reset", FIELD, 0);
        animate(field);
        AnimationEvent wkr = new AnimationEvent("default", WICKET_KEEPER, 0);
        animate(wkr);
        camReset();
      }

      /********************FIELDING******************************************************/
      else if (arg == Gse.FIELDING_LOOKUP) {
        int hook_count = -1;
        _cat.finest(">>>>>>>>>>SF=" + _speed_factor + " HC=" + hook_count);
        _mp = new MovePoller(this, _mb, FIELD, Gse.UNKNOWN, 60, hook_count);
        _mp.setSlowFactor(5);
        _cat.finest(
            "Starting thread to look for field last move ------------------------------" +
            _mp);
        addAnimator(_mp);
      }
      else if (arg == Gse.FIELDING_RCVD) {
        if (_mp != null) {
          _mp.cancel();
          removeAnimator(_mp);
        }
        setNormal();
        setNormalBall();
        slowDown();
        slowDownBall();
        //update(null, Gse.FIELDING_START);
      }
      else if (arg == Gse.FIELDING_START) {
        if (_mp != null) {
          _mp.cancel();
          removeAnimator(_mp);
        }
        setNormal();
        setNormalBall();
        LastMoveAction lma = (LastMoveAction) _mb.removeLastMove(FIELD);
        if (lma == null) {
          AnimationEvent field = new AnimationEvent("default", FIELD, 0);
          animate(field);
          //update(lma, Gse.FIELDING_MISS);
          return;
        }
        MoveParams field = lma.getMoveParams();
        LastMoveAction lmb = (LastMoveAction) _mb.getLastMove(BATS);
        MoveParams bats = null;
        if (lmb != null) {
          bats = lmb.getMoveParams();
        }
        //_cc.addCommentary(MoveParams.getAnalysis(bp, mp));
        _cat.info("Bats=" + bats + "\nField=" + field);
        ClientPlayerController fielder = _cc.getPlayer(lma.getTeam(),
            lma.getPosition());
        _cat.finest(fielder.toString());
        _cat.finest(lma.toString());

        if (MoveUtils.doFielding(field, bats)) {
          _cat.finest(FielderPlayerView.getAnimConstantValue(fielder.getState().
              getPlayerType()) + "");
         if (field.getFieldAction() == CATCH) {
            AnimationEvent fthr = new AnimationEvent("catch",
                FielderPlayerView.
                getAnimConstantValue(fielder.getState().getPlayerType()), 0);
            fthr.setNextEvent(lma, Gse.BALL_FLIGHT_RETURN, 15);
            animate(fthr);
          }
          else {
            AnimationEvent fthr = new AnimationEvent("throw",
                FielderPlayerView.
                getAnimConstantValue(fielder.getState().getPlayerType()), 0);
            fthr.setNextEvent(lma, Gse.BALL_FLIGHT_RETURN, 15);
            animate(fthr);
          }
          AnimationEvent ball_stop = new AnimationEvent("ball_stop", BALL, 0);
          animate(ball_stop);
        }
        else {
           update(lma, Gse.FIELDING_MISS);
        }
      }
      else if (arg == Gse.FIELDER_MOVE_BEGIN) {
        AnimationEvent bowl_move = new AnimationEvent("field_move", BOTTOMPANEL,
            0);
        animate(bowl_move);
      }
      else if (arg == Gse.FIELDING_MISS) {
        AnimationEvent pp = new AnimationEvent("default", FIELD, 0);
        animate(pp);
        AnimationEvent pp1 = new AnimationEvent("default", BOWL, 0);
        animate(pp1);
      }
      else if (arg == Gse.BALL_FLIGHT_RETURN) {
        LastMoveAction lma = (LastMoveAction) _mb.getLastMove(FIELD);
        _cat.finest(lma.toString());
        AnimationEvent ball_return = new AnimationEvent("ball_return", BALL, 0);
        if (lma == null) {
          ball_return.setAttach(o);
        }
        else {
          ball_return.setAttach(lma);
        }
        animate(ball_return);
      }
      else if (arg == Gse.BALL_FLIGHT_OVER) {
        AnimationEvent wcblock = new AnimationEvent("catch", WICKET_KEEPER, 0);
        animate(wcblock);

        AnimationEvent ball_default = new AnimationEvent("ball_default", BALL,
            0);
        animate(ball_default);

        AnimationEvent field = new AnimationEvent("reset", FIELD, 0);
        animate(field);

        AnimationEvent bowler_d = new AnimationEvent("reset", BOWL, 0);
        animate(bowler_d);
      }
      else if (arg == Gse.FIELDER_DEFAULT) {
        AnimationEvent field = new AnimationEvent("default", FIELD, 0);
        animate(field);
      }
      else if (arg == Gse.GAME_BEGIN) {
      }
      else if (arg == Gse.BOWLER_MOVE_BEGIN) {
      }
      else if (arg == Gse.BOWLER_MOVE_END) {
      }
      else if (arg == Gse.BATSMAN_HIT_OVER) {
      }
      else if (arg == Gse.FIELDER_MOVE_END) {
      }
      else if (arg == Gse.BALL_NEAR_FIELDER) {
      }
      else if (arg == Gse.BALL_INTERRUPTED) {
        //happens when a fielder fields a ball
      }

      /********************UMPIRE*************************************************/
      else if (arg == Gse.PITCH_CHANGE) {
        AnimationEvent speak = new AnimationEvent("Bats Change", SPEAKS, 0);
        animate(speak);
      }
      else if (arg == Gse.INNING_OVER) {
        AnimationEvent speak = new AnimationEvent("Inning Over", SPEAKS, 0);
        animate(speak);
      }
      else if (arg == Gse.GAME_OVER) {
        AnimationEvent speak = new AnimationEvent("Game Over", SPEAKS, 0);
        animate(speak);
      }
      else if (arg == Gse.WIDE_BALL) {
        AnimationEvent speak = new AnimationEvent("Wide Ball", SPEAKS, 0);
        animate(speak);
      }
      //\\//\\//\\ ERROR CONDITIONS//\\//\\//\\
      else if (arg == Gse.MOVE_NOT_FOUND) {
        setNormal();
        setNormalBall();
      }
    }
    catch (Throwable t) {
      _cat.warning("Exception received during animation "+ t);
    }
    System.gc();
  }

  public void run() {
    _frame_count++;
    AnimationEvent[] c = null;
    c = _queue.fetch();
    for (int i = 0; c != null && i < c.length; i++) {
      process(c[i], _frame_count);
    }
    try {
      Animator[] v = getAnimatorsZOrdered();
      for (int i = 0; v != null && i < v.length; i++) {
        if (v[i] == null || v[i].getAnimation() == null ||
            v[i].getAnimation()._invalid) {
          //_cat.finest("Unable to invoke " + v[i] + ", Anim=" + v[i].getAnimation());
          continue;
        }
        int delay = (int) v[i].delay();
        delay = delay == 0 ? 2 : delay;
        if (_frame_count % delay == 0) {
          //_cat.finest("Invoke later -- " + v[i]);
          SwingUtilities.invokeLater(v[i]);
        }
      }
    }
    catch (Throwable e) {
      _cat.warning("Problem in animation thread "+  e);
    }
  }

  public void process(AnimationEvent anmt, int frame) {
    Animator[] v = getAnimatorsZOrdered();
    for (int i = 0; i < v.length; i++) {
      //_cat.finest(v[i]);
      v[i].animate(anmt, frame);
    }
  }

  public void animate(AnimationEvent anmt) {
    anmt._seq = _anim_seq++;
    _queue.put(anmt);
  }

  public void speedUpForPlay() {
    _speed_factor = NORMAL;
    _speed_factor *= VFAST;
    _cat.finest("speedUpForPlay = " + _speed_factor);
  }

  public void speedUp() {
    _speed_factor *= FAST;
    //_cat.finest("SPEED UP = " + _speed_factor);
  }

  public void slowDown() {
    _speed_factor *= SLOW;
    //_cat.finest("SLOW DOWN = " + _speed_factor);
  }

  public void setNormal() {
    _speed_factor = NORMAL;
    //_cat.finest("SET NORMAL = " + _speed_factor);
  }

  public void speedUpBallForPlay() {
    _ball_speed_factor = NORMAL;
    _ball_speed_factor *= VFAST;
    //_cat.finest("BALL speedUpForPlay = " + _ball_speed_factor);
  }

  public void speedUpBall() {
    _ball_speed_factor *= FAST;
    //_cat.finest("BALL SPEED UP = " + _ball_speed_factor);
  }

  public void slowDownBall() {
    _ball_speed_factor *= SLOW;
    //_cat.finest("BALL SLOW DOWN = " + _speed_factor);
  }

  public void setNormalBall() {
    _ball_speed_factor = NORMAL;
    _cat.finest("BALL SET NORMAL = " + _ball_speed_factor);
  }

  public void animate(AnimationEvent[] anmt) {
    _anim_seq++;
    for (int i = 0; i < anmt.length; i++) {
      anmt[i]._seq = _anim_seq;
      _queue.put(anmt[i]);
    }
  }

  public void resetAnimators() {
    // remove only clientplayerview types
    _animators_registry = new Hashtable();
    _fielders_registry = new Hashtable();
  }

  public Animator getAnimator(String type) {
    return (Animator) _animators_registry.get(type);
  }

  public Animator[] getAnimators() {
    Animator[] av = (Animator[]) _animators_registry.values().toArray(new
        Animator[_animators_registry.size()]);
    return av;
  }

  public Animator[] getAnimatorsZOrdered() {
    Animator[] av = (Animator[]) _animators_registry.values().toArray(new
        Animator[_animators_registry.size()]);
    java.util.Arrays.sort(av, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Animator) o2).getZOrder() - ((Animator) o1).getZOrder();
      }
    });
    return av;
  }

  public Animator[] getFielders() {
    return (Animator[]) _fielders_registry.values().toArray(new Animator[
        _fielders_registry.size()]);
  }

  public Animator getBall() {
    return (Animator) _animators_registry.get("ball");
  }

  public void addAnimator(Animator anim) {
    _animators_registry.put(anim.getType(), anim);
  }

  public void removeAnimator(Animator anim) {
    if (anim == null) {
      return;
    }
    _animators_registry.remove(anim.getType());
  }

  public void addAnimator(Animator anim, PlayerStatus ps) {
    _cat.finest(ps.getPlayerTypeString() + " Putting " + anim + " Size=" +
               _animators_registry.size());
    _animators_registry.put(ps.getPlayerTypeString(), anim);
    if (ps.isFielder()) {
      _fielders_registry.put(ps.getPlayerTypeString(), anim);
    }
  }

  public void setThisPlayerModel(PlayerModel pm) {
    _pm = pm;
  }

  public boolean isThisMyPlayer(PlayerModel pm) {
    return _pm.equals(pm);
  }

  public boolean isObserver() {
    return _pm == null;
  }

  public int frameCount() {
    return _frame_count;
  }

  public void camReset() {
    setNormal();
    setNormalBall();

    _applause = true;
    _cat.finest("RESET++++++++++++++++++++++++" + _cX + ", " + _cY);
    _cX = DEFAULT_POS_X;
    _cY = DEFAULT_POS_Y;
    _cc.updateUI();
    _enableCameraMove = false;
  }

  public int dispX() {
    return DEFAULT_POS_X - _cX;
  }

  public int dispY() {
    return DEFAULT_POS_Y - _cY;
  }

  public int cX() {
    return _cX;
  }

  public int cY() {
    return _cY;
  }

  public void setCricketController(CricketController cc) {
    _cc = cc;
  }

  public void moveCamera(int x, int y) {
    if (_enableCameraMove) {
      moveCameraX(x, y, 100);
      moveCameraY(x, y, 100);
    }
  }

  public void moveCameraX(int x, int y, int interv) {
    if (_cX > 0 && _cX < PITCH_WIDTH) {
      if (x < interv) { //_cX - 5) {
        //_cat.finest("x=" + x + ", cX=" + _cX);
        _cX -= 4;
        _cX = _cX <= 0 ? 0 : _cX;
        //_enableCameraMove = x == 0 ? false : true;
        _cc.updateUI();
        //_cat.finest("FcX=" + _cX );
      }
      else if (x > SCREEN_WIDTH - interv) {
        //_cat.finest("x=" + x + ", cX=" + _cX);
        _cX += 4;
        _cX = _cX >= RANGE_X ? RANGE_X : _cX;
        //_enableCameraMove = x == RANGE_X ? false : true;
        _cc.updateUI();
        //_cat.finest("FcX=" + _cX);
      }
    }
    else { //if (_applause){
      SoundManager.playEffect(SoundManager.APPLAUSE);
      _applause = false;
    }
  }

  public void moveCameraY(int x, int y, int interv) {
    if (_cY > 0 && _cY < PITCH_HEIGHT) {
      if (y < interv) { //_cY - 5) {
        //_cat.finest("y=" + y + "cY=" + _cY);
        _cY -= 4;
        _cY = _cY <= 0 ? 0 : _cY;
        //_enableCameraMove = y == 0 ? false : true;
        _cc.updateUI();
        //_cat.finest("FcY=" + _cY);
      }
      else if (y > SCREEN_HEIGHT - interv) {
        //_cat.finest("y=" + y + "cY=" + _cY);
        _cY += 4;
        _cY = _cY >= RANGE_Y ? RANGE_Y : _cY;
        //_enableCameraMove = y == RANGE_Y ? false : true;
        _cc.updateUI();
        //_cat.finest("FcY=" + _cY);
      }
    }
    else { //if (_applause){
      SoundManager.playEffect(SoundManager.APPLAUSE);
      _applause = false;
    }

  }

  public void playerShift(String type, int x, int y, int z) {
    Animator a = getAnimator(type);
    if (_pm == null || !_pm.getPlayerStatus().getPlayerTypeString().equals(type)) { //if watching or playing
      _cat.info("Shifting " + type + ", x=" + x + ", y=" + y);
      if (a != null && (x != 0 || y != 0)) {
        a.shift(x, y, z);
      }
    }
  }

  public String toString() {
    return "AM=" + _gid;
  }

}
