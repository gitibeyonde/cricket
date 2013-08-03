package com.cricket.mmog.gamemsgimpl;

import com.cricket.mmog.CricketPresence;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.gamemsg.*;

public abstract class AbstractPlayerMessageBase
    extends AbstractMessageBase
    implements PlayerMessage {

  public AbstractPlayerMessageBase(String gameId, CricketPresence p) {
    super(gameId);
    this.p = p;
  }

  public CricketPresence player() {
    return p;
  }

  private CricketPresence p;
}
