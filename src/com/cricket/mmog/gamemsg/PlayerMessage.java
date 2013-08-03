package com.cricket.mmog.gamemsg;

import com.cricket.mmog.CricketPresence;
import com.cricket.mmog.Player.*;

public interface PlayerMessage
    extends Message {

  public CricketPresence player();

}
