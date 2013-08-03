package com.cricket.mmog.cric.impl;

import java.util.*;
import java.util.logging.Logger;
import com.cricket.mmog.*;
import com.cricket.mmog.GameType;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.resp.*;
import com.cricket.mmog.cric.util.Pitch;
import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.mmogserver.GamePlayer;
import com.cricket.common.message.ResponseGameEvent;
import com.agneya.util.Base64;
import com.criconline.anim.AnimationConstants;
import com.cricket.mmog.cric.util.MoveUtils;
import com.agneya.util.Utils;
import com.cricket.mmog.resp.Response;
import com.cricket.mmog.cric.Cricket;
import com.cricket.mmog.cric.LastMoveResponse;
import com.cricket.mmog.cric.InningOverResponse;
import com.cricket.mmog.cric.BatsChangeResponse;
import com.cricket.mmog.cric.GameState;
import com.cricket.mmog.cric.BowlResponse;
import com.cricket.mmog.cric.FieldResponse;


/** This is the entry point for all black-jack players
 * keep the method synchronized.
 */

public class CricketMatch extends Cricket implements Runnable,
    AnimationConstants {

  // set the category for logging
  static Logger _cat = Logger.getLogger(CricketMatch.class.getName());


  public CricketMatch(int id, String name, int type, String tA, String tB,
                      int ball_per_player, int fees, int buyin,
                      Observer stateObserver) {
    super(name, type, tA, tB, ball_per_player, fees, buyin, stateObserver);
    _cat.finest(this.toString());
  }

  public boolean reRunCondition() {
    _cat.finest("Size="+  _pitch.getTeamA().eligiblePlayers(0).length );
    return _pitch.getTeamA().eligiblePlayers(0).length >= 2 &&
        _pitch.getTeamB().eligiblePlayers(0).length >= 2 ;
    // any other validations
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
    else if (_bowl_md.isBold()){
      // the player is out
      _bats_p.setBold();
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
    if (_bats_p.isBold()) {
      _cat.finest("------------BATSMAN BOLD=");
      _bats_p.unsetBatsman();
      _cat.info("CHANGING BATSMAN OLD = " + _bats_p);
      if (!_pitch.advanceBatsman()) {
        _cat.info("advance batsman " + _pitch);
        _pitch.toggleBattingFielding();
        _cat.info("toggling " + _pitch);
        CricketPresence[] check_bats = _pitch.getBatsman();
        ////////////////// GAME OVER
        if (check_bats == null || check_bats.length == 0) {
          Response[] r = gameOverResponse(p);
          putDelay(new QResp(QResp.RESPONSE, 16000, r[0]));
          _cat.info("<<<<<<<<<<<<GAME OVER = >>>>>>>>>>>>>>>>>>");
          putDelay(new QResp(QResp.NEW_GAME_START, 16000, null));
          return last_move;
        }
        else {
          _cat.info("----------CHANGE = -------------" + inning_over[0]);
          _state = new GameState(GameState.SECOND_INNING);

          _balls = 0;
          _runs = 0;
          if (move == Moves.K_BAT) {
            _cat.info("No field response ");
            putDelay(new QResp(QResp.RESPONSE, 4000, inning_over[0]));
            putDelay(new QResp(QResp.RESPONSE, 8000, new BowlResponse(this, null)));
            return last_move;
          }
          else {
            _cat.info("Fields response ");
            putDelay(new QResp(QResp.RESPONSE, 4000, inning_over[0]));
            putDelay(new QResp(QResp.RESPONSE, 8000, new BowlResponse(this, null)));
            return last_move;
          }
        }
      }
      else {
        putDelay(new QResp(QResp.RESPONSE, 4000, bats_change[0]));
        putDelay(new QResp(QResp.RESPONSE, 8000, new BowlResponse(this, null)));
        _cat.info("BATS CHANGES " + bats_change[0]);
        return last_move;
      }
    }
    else { // NO PITCH CHANGES
      _cat.finest("-----------NO PITCH CHANGE");
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
        putDelay(new QResp(QResp.RESPONSE, 8000, new BowlResponse(this, null)));
        return last_move;
      }
      else {
        _cat.info("FIELD MOVE " + _pitch);
        putDelay(new QResp(QResp.RESPONSE, 8000, new BowlResponse(this, null)));
        return last_move;
      }
    }
  }




}
