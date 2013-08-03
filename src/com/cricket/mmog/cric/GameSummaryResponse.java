package com.cricket.mmog.cric;

public class GameSummaryResponse
    extends CricResponse {

  StringBuffer buf = new StringBuffer();

  public GameSummaryResponse(Cricket g) {
    super(g);
    buf.append(header());
    buf.append(pitchDetails());

    //set the details appropriately
    broadcast(null, buf.toString());

  }

}
