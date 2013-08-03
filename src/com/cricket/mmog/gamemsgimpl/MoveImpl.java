package com.cricket.mmog.gamemsgimpl;

import java.util.logging.Logger;
import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.*;
import com.cricket.mmog.gamemsg.*;
import com.cricket.mmog.resp.*;
import com.cricket.mmog.cric.util.MoveParams;

public class MoveImpl extends AbstractPlayerMessageBase implements Move {

  // set the category for logging

  static Logger _cat = Logger.getLogger(MoveImpl.class.getName());

  public MoveImpl(String gameId, CricketPresence p, long move, double amt) {
    super(gameId, p);
    this.move = move;
    this.amt = amt;
  }

  public MoveImpl(String gameId, int grid, CricketPresence p, long move, double amt) {
    super(gameId, p);
    this.move = move;
    this.amt = amt;
    this.grid = grid;
  }

  public MoveImpl(String gameId, CricketPresence p, long move, double amt, MoveParams md) {
    super(gameId, p);
    this.move = move;
    this.amt = amt;
    this.move_details = md;
  }

  public long move() {
    return move;
  }

  public double amount() {
    return amt;
  }

  public MoveParams moveDetails() {
    return move_details;
  }

  public byte id() {
    return 2;
  }

  public synchronized Response[] interpret() {
    Game g = Game.game(gameId());
    Response[] return_response = null;

    // cricket moves
    if ((Moves.CRIC_MASK & move) > 0) {
      Cricket cric = (Cricket) g;


      if (!player().isResponseReq()) {
        _cat.severe("Move not expected from this client " + player());
        g.resetLastMovePlayer();
        return cric.illegalMove(player(), move);
      }

      if (grid > 0 && this.grid != cric.grid()) {
        _cat.severe("Move not expected from this client for this hand " + player());
        g.resetLastMovePlayer();
        return cric.illegalMove(player(), move);
      }

      if (!g.checkNextMove(move, player())) {
        _cat.warning("Illegal MoveVal=" +
                   Moves.stringValue(move == 0 ? Moves.NONE : move) + " : " +
                   " amt: " + amt + " Player=" + player());
        g.resetLastMovePlayer();
        return cric.illegalMove(player(), move);
      }

      _cat.info("Move=" + (Moves.stringValue(move == 0 ? Moves.NONE : move)) +
                ", MD=" + move_details);
      g.setMarker(player());
      g._current = (CricketPresence) player();

      //the player has responded in stipulated time with a right move reset resp req
      player().unsetResponseReq();

      if (move == Moves.START) {
        g.addLastMovePresence((CricketPresence) player());
        player().lastMove(move);
        return_response = cric.startPractise(player(), move, move_details);
      }
      else if (move == Moves.TOSS) {
        g.addLastMovePresence((CricketPresence) player());
        player().lastMove(move);
        return_response = cric.processToss(player(), move, move_details);
      }
      else if (move == Moves.HEAD || move == Moves.TAIL) {
        g.addLastMovePresence((CricketPresence) player());
        player().lastMove(move);
        return_response = cric.processToss(player(), move, move_details);
      }
      else if (move == Moves.FIELDING || move == Moves.BATTING) {
        g.addLastMovePresence((CricketPresence) player());
        player().lastMove(move);
        return_response = cric.processFieldingOrBatting(player(), move,
            move_details);
      }
      else if (move == Moves.B_BOWL) {
        g.addLastMovePresence((CricketPresence) player());
        player().lastMove(move);
        return_response = cric.processBowling(player(), move, move_details);
      }
      else if (move == Moves.K_BAT) {
        g.addLastMovePresence((CricketPresence) player());
        player().lastMove(move);
        return_response = cric.processBatting(player(), move, move_details);
      }
      else if (move == Moves.K_RUN) {
        g.addLastMovePresence((CricketPresence) player());
        player().lastMove(move);
        return_response = cric.processBatsRun(player(), move, move_details);
      }
      else if (move == Moves.F_FIELD) {
        g.addLastMovePresence((CricketPresence) player());
        player().lastMove(move);
        return_response = cric.processFielding(player(), move, move_details);
      }
      else if (move == Moves.DRINKS) {
        g.addLastMovePresence((CricketPresence) player());
        player().lastMove(move);
        return_response = cric.processDrinks(player(), move, move_details);
      }
      g.resetLastMovePlayer();
      return return_response;
    }
    else {
      throw new IllegalArgumentException("Illegal move: " + move);
    }
  }

  public synchronized int mvid() {
    return++mvId;
  }

  double amt;
  MoveParams move_details;

  long move;
  int grid = -1;

  static volatile int mvId;
}
