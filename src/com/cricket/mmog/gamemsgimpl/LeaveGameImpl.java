package com.cricket.mmog.gamemsgimpl;

import com.cricket.mmog.Game;
import com.cricket.mmog.resp.Response;
import com.cricket.mmog.gamemsg.LeaveGame;
import com.cricket.mmog.gamemsg.LeaveGame;
import com.cricket.mmog.Game;
import com.cricket.mmog.CricketPresence;

public class LeaveGameImpl
    extends AbstractPlayerMessageBase
    implements LeaveGame {

  public LeaveGameImpl(String gameId, CricketPresence p, boolean timeout) {
    super(gameId, p);
    _timeout = timeout;
  }

  public byte id() {
    return 3;
  }

  public Response[] interpret() {
    Game g =  Game.game(gameId());
    g._current = player(); // only the current changes. marker does not.
    // marker not set here
    return g.leave(player(), _timeout);
  }

  private boolean _timeout;
}
