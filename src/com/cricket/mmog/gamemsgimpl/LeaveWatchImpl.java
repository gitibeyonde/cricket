package com.cricket.mmog.gamemsgimpl;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.gamemsg.*;
import com.cricket.mmog.resp.*;

public class LeaveWatchImpl
    extends AbstractPlayerMessageBase
    implements LeaveWatch {

  public LeaveWatchImpl(CricketPresence p, String gameId) {
    super(gameId, p);
  }

  public byte id() {
    return 5;
  }

  public Response[] interpret() {
    Game g = Game.game(gameId());
    // no marker handling here
    return g.leaveWatch(player());
  }

}
