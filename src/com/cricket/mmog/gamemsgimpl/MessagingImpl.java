package com.cricket.mmog.gamemsgimpl;

import java.util.logging.Logger;
import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.gamemsg.*;
import com.cricket.mmog.resp.*;

public class MessagingImpl
    extends AbstractPlayerMessageBase
    implements Move {

  // set the category for logging
  static Logger _cat = Logger.getLogger(MessagingImpl.class.getName());

  public MessagingImpl(String gameId, CricketPresence p, String s) {
    super(gameId, p);
    this.message = s;
  }

  public String message() {
    return message;
  }

  public byte id() {
    return 9;
  }

  public double amount() {
    return 0;
  }

  public long move() {
    return -11;
  }

  public Response[] interpret() {
    Game g = Game.game(gameId());
    return g.chat(player(), message);
  }

  public synchronized int mesgid() {
    return++mesgId;
  }

  String message;

  static volatile int mesgId;
}
