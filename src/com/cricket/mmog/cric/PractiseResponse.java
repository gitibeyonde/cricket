package com.cricket.mmog.cric;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.util.MoveParams;

public class PractiseResponse
    extends CricResponse {

  public PractiseResponse(Cricket g, MoveParams md) {
    super(g);
    buf.append(header());
    buf.append(pitchDetails());
    CricketPresence p = g.inquirer();
    // rig the last move to a bowl
    //buf.append(lastMoveDetails());
    buf.append(",last-move=0|2|").append(p.name()).append("|bowl|5,");
    if (md!=null){
      buf.append("md=").append(md.stringValue()).append(",");
    }
    buf.append("next-move=0|1|bat|5,");
    _g.initNextMove(p, Moves.K_BAT, 5);
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j],
                 playerTargetPosition(_allPlayers[j]).toString());
      setCommand(_allPlayers[j], playerStats(_allPlayers[j]).toString());
    }
    broadcast(_allPlayers, buf.toString());

  }

  StringBuffer buf = new StringBuffer();
}
