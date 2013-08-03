package com.cricket.mmog.gamemsgimpl;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.gamemsg.*;
import com.cricket.mmog.resp.*;

public class GameDetailsImpl
    extends AbstractPlayerMessageBase
    implements GameDetails {

  public GameDetailsImpl(String gameId, CricketPresence p) {
    super(gameId, p);
  }

  public byte id() {
    return 1;
  }

  public Response[] interpret() {
    Game g = Game.game(gameId());
    g.setInquirer(player());
    return g.details(player());
  }

}
