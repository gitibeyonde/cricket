package com.cricket.mmog.cric;

import com.cricket.mmog.*;
import com.cricket.mmog.cric.impl.ScheduledMatch;
import com.agneya.util.Utils;

public class GameDetailsResponse extends CricResponse {

  StringBuffer buf = new StringBuffer();

  public GameDetailsResponse(Cricket g) {
    super(g);
    buf.append(header());
    buf.append(pitchDetails());
    buf.append(playerStats(g.inquirer()));
    
    if (g.inquirer() == null) {
      broadcast(null, buf.toString());
    }
    else {
      setCommand(g.inquirer(), buf.toString());
    }

  }

  public GameDetailsResponse(Cricket g, boolean to_all) {
    super(g);
    buf.append(header());
    buf.append(pitchDetails());
   

    broadcast(_allPlayers, buf.toString());
  }
}
