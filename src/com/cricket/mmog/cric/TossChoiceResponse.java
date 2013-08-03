package com.cricket.mmog.cric;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.util.MoveParams;

public class TossChoiceResponse extends CricResponse {

  public TossChoiceResponse(Cricket g, MoveParams md) {
    super(g);
    g.reset();
    buf.append(header());
    buf.append(pitchDetails());

    CricketPresence[] tm2;
    if (g._rng.nextIntBetween(1, 2) ==1){
      tm2 = g._pitch.getTeamA().allPlayers(0);
    }
    else {
      tm2 = g._pitch.getTeamB().allPlayers(0);
    }
    buf.append("next-move=");
    for (int i = 0; i < tm2.length; i++) {
      tm2[i].resetNextMove();
      buf.append(setMove(tm2[i], tm2[i].pos(), tm2[i].teamName(), Moves.FIELDING, 5));
      buf.append(setMove(tm2[i], tm2[i].pos(), tm2[i].teamName(), Moves.BATTING, 5));
    }
    buf.deleteCharAt(buf.length()-1);
    buf.append(",");


    buf.append(lastMoveDetails());
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j], playerTargetPosition(_allPlayers[j]).toString());
      setCommand(_allPlayers[j], playerStats(_allPlayers[j]).toString());
    }
    broadcast(_allPlayers, buf.toString());

  }

  StringBuffer buf = new StringBuffer();
}
