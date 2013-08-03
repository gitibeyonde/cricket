package com.cricket.mmog.cric;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.util.MoveParams;

public class BatsResponse extends CricResponse {

  public BatsResponse(Cricket g, MoveParams md, CricketPresence p) {
    super(g);
    buf.append(header());
    buf.append(pitchDetails());
    if (md!=null){
      buf.append(lastMoveDetails());
      buf.append("md=").append(md.stringValue()).append(",");
    }
    buf.append("next-move=");
    p.resetNextMove();
    buf.append(setMove(p, p.pos(), p.teamName(), Moves.K_BAT, 5));
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
