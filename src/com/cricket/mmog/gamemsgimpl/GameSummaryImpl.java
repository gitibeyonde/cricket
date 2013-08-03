package com.cricket.mmog.gamemsgimpl;

import com.cricket.mmog.Game;
import com.cricket.mmog.CricketPlayer;
import com.cricket.mmog.resp.Response;
import com.cricket.mmog.gamemsg.GameDetails;
import com.cricket.mmog.resp.Response;
import com.cricket.mmog.CricketPresence;


public class GameSummaryImpl
    extends AbstractPlayerMessageBase
    implements GameDetails {

  public GameSummaryImpl(String gameId, CricketPresence p) {
    super(gameId, p);
  }

  public byte id() {
    return 1;
  }

  public Response[] interpret() {
    Game g = Game.game(gameId());
    g.setInquirer(player());
    return g.summary();
  }

}
