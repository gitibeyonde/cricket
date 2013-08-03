package com.cricket.mmog.cric;

import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.util.MoveParams;

public class LastMoveResponse extends CricResponse {

  public LastMoveResponse(Cricket g, MoveParams md) {
    super(g);
    buf.append(header());
    buf.append(pitchDetails());
    if (md!=null){
      buf.append("md=").append(md.stringValue()).append(",");
      buf.append(lastMoveDetails());
    }
    buf.append("next-move=-1|none|none|wait|0,");
    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j], playerTargetPosition(_allPlayers[j]).toString());
      setCommand(_allPlayers[j], playerStats(_allPlayers[j]).toString());
    }
    broadcast(_allPlayers, buf.toString());

  }

  StringBuffer buf = new StringBuffer();
}
