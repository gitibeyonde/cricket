package com.cricket.mmog.cric;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.util.MoveParams;

public class BowlResponse extends CricResponse {

  public BowlResponse(Cricket g, MoveParams md) {
    super(g);
    g.reset();
    buf.append(header());
    buf.append(pitchDetails());
    if (md!=null){
      buf.append(lastMoveDetails());
      buf.append("md=").append(md.stringValue()).append(",");
    }
    buf.append("next-move=");
    CricketPresence p = g._pitch.getBowler();
    p.resetNextMove();
    buf.append(setMove(p, p.pos(), p.teamName(), Moves.B_BOWL, 5));
    buf.deleteCharAt(buf.length() - 1);
    buf.append(",");

    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j], playerTargetPosition(_allPlayers[j]).toString());
      setCommand(_allPlayers[j], playerStats(_allPlayers[j]).toString());
    }
    broadcast(_allPlayers, buf.toString());

  }

  StringBuffer buf = new StringBuffer();
}
