package com.cricket.mmog.cric;

public class JoinResponse extends CricResponse {

  //ERROR CODES
  // -6 SITIN NORMAL
  // -7 UNINVITED
  // -8 OCCUPIED
  // -9  Already sitting on another team
  // -10 BROKE SO CANNOT SIT
  public JoinResponse(Cricket g, int err_code) {
    super(g);
    buf.append(header());
    buf.append(pitchDetails());
    buf.append("next-move=-1|none|none|wait|").append(err_code).append(",");

    if (err_code < 0) {
      setCommand(g.inquirer(), buf.toString());
    }
    else {
      buf.append(lastMoveDetails());
      for (int j = 0; j < _allPlayers.length; j++) {
        setCommand(_allPlayers[j],
                   playerTargetPosition(_allPlayers[j]).toString());
        setCommand(_allPlayers[j], playerStats(_allPlayers[j]).toString());
      }
      broadcast(g.allPlayers(), buf.toString());
    }
  }

  StringBuffer buf = new StringBuffer();

}
