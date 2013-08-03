package com.cricket.mmog.gamemsgimpl;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.gamemsg.*;
import com.cricket.mmog.resp.*;

/**
 * Created by IntelliJ IDEA. User: aprateek Date: Apr 16, 2004 Time: 11:51:13 AM To
 * change this template use File | Settings | File Templates.
 */
public class ObserverToPlayerImpl
    extends AbstractPlayerMessageBase
    implements ObserverToPlayer {

  public ObserverToPlayerImpl(String gameId, CricketPresence observer) {
    super(gameId, observer);
  }

  public byte id() {
    return 6;
  }

  public Response[] interpret() {
    Game g = Game.game(gameId());
    g.setInquirer(player());
    g._current = player();
    g.addLastMovePresence((CricketPresence) player());
    return g.promoteToPlayer(player());
  }

}
