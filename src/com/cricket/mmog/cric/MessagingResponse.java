package com.cricket.mmog.cric;

import com.cricket.mmog.CricketPresence;


public class MessagingResponse
    extends CricResponse {

  public MessagingResponse(Cricket g, CricketPresence p, String message) {
    super(g);
    buf.append("type=chat,id=").append(g.name()).append(",player=").append(p.name()).append(
        ",message=").append(message);
    /*
     Assume that all the players on the table will be interested, except those that
       have explicityly opted out.
     */
    //
    broadcast(_allPlayers/*g.table.allCompanions(p)*/, buf.toString());
  }

  public MessagingResponse(Cricket g, String message) {
    super(g);
    buf.append("type=broadcast,id=").append(g.name()).append(
        ",message=").append(message);
    /*
     Assume that all the players on the table will be interested, except those that
       have explicityly opted out.
     */
    //
    broadcast(_allPlayers, buf.toString());
  }

  StringBuffer buf = new StringBuffer();
}
