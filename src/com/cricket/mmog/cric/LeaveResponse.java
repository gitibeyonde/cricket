package com.cricket.mmog.cric;

import com.cricket.mmog.CricketPlayer;

public class LeaveResponse
    extends CricResponse {

  public LeaveResponse(Cricket g, int pos) {
    super(g);
    buf.append(header());
    buf.append(pitchDetails());
    buf.append(lastMoveDetails());
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j],
                  buf.toString() + "next-move=0|Pitch|Empire|leave|0," + playerTargetPosition(_allPlayers[j]).toString());
    }
  }

  public LeaveResponse(Cricket g) {
    super(g);
    buf.append(header());
    // get the first waiter and ask him to join

    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j],
                 playerTargetPosition(_allPlayers[j]).toString());
    }
  }

  StringBuffer buf = new StringBuffer();
}
