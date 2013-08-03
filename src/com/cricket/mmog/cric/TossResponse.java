package com.cricket.mmog.cric;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.util.MoveParams;

public class TossResponse extends CricResponse {

  public TossResponse(Cricket g, MoveParams md) {
    super(g);
    g.reset();
    buf.append(header());
    buf.append(pitchDetails());
    buf.append("next-move=");
    if (g._rng.nextIntBetween(1, 2) ==1){
      CricketPresence cap1 = g._pitch.getTeamACaptain();
      buf.append(setMove(cap1, cap1.pos(), cap1.teamName(), Moves.TOSS, 5));
      CricketPresence cap2 = g._pitch.getTeamBCaptain();
      buf.append(setMove(cap2, cap2.pos(), cap2.teamName(), Moves.HEAD, 5));
      buf.append(setMove(cap2, cap2.pos(), cap2.teamName(), Moves.TAIL, 5));
    }
    else {
      CricketPresence cap2 = g._pitch.getTeamACaptain();
      buf.append(setMove(cap2, cap2.pos(), cap2.teamName(), Moves.TOSS, 5));
      CricketPresence cap1 = g._pitch.getTeamBCaptain();
      buf.append(setMove(cap1, cap1.pos(), cap1.teamName(), Moves.HEAD, 5));
      buf.append(setMove(cap1, cap1.pos(), cap1.teamName(), Moves.TAIL, 5));
    }
    buf.deleteCharAt(buf.length()-1);
    buf.append(",");

    buf.append(lastMoveDetails());
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j], playerTargetPosition(_allPlayers[j]).toString());
      _allPlayers[j].chargeFeesAndBuyin(_g._fees + _g._buyin);
      _g._pot += _g._buyin;
      setCommand(_allPlayers[j], playerStats(_allPlayers[j]).toString());
    }
    broadcast(_allPlayers, buf.toString());

  }

  StringBuffer buf = new StringBuffer();
}
