package com.cricket.mmog.gamemsgimpl;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.gamemsg.*;
import com.cricket.mmog.resp.*;

/**
 * Created by IntelliJ IDEA. User: aprateek Date: Apr 16, 2004 Time: 11:22:37 AM To
 * change this template use File | Settings | File Templates.
 */
public class ObserveGameImpl
    extends AbstractPlayerMessageBase
    implements PlayerMessage {

  public ObserveGameImpl(CricketPresence p, String gameId) {
    super(gameId, p);
  }

  public byte id() {
    return 4;
  }

  public Response[] interpret() {
    Game g = Game.game(gameId());
    g.setInquirer(player());
    return g.observe(player());
  }

}
