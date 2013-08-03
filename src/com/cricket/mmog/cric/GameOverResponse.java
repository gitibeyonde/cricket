package com.cricket.mmog.cric;

import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.util.MoveParams;

public class GameOverResponse extends CricResponse {

  public GameOverResponse(Cricket g, MoveParams md) {
    super(g);
    buf.append(header());
    buf.append(pitchDetails());
    buf.append("next-move=0|Pitch|Empire|GameOver|0,");
    buf.append(g.getWinnersString());
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j], playerTargetPosition(_allPlayers[j]).toString());
    }
    broadcast(_allPlayers, buf.toString());
  }

  StringBuffer buf = new StringBuffer();
}
