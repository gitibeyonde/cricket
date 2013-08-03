package com.cricket.mmognio;

public interface MMOGClient {

  public boolean isDead();

  public void kill();

  public void handler(MMOGHandler h);

}
