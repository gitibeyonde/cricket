package com.cricket.mmog.gamemsgimpl;

import com.cricket.mmog.gamemsg.*;

public abstract class AbstractMessageBase
    implements Message {

  public AbstractMessageBase(String gameId) {
    this.gameId = gameId;
  }

  public String gameId() {
    return gameId;
  }

  String gameId;

}
