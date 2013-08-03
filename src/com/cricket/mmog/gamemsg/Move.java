package com.cricket.mmog.gamemsg;

public interface Move
    extends PlayerMessage {

  public long move();

  public double amount();
}
