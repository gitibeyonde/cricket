package com.cricket.common.message;

public interface CommandProcessor
    extends Runnable {
  public void wakeUp();

  public void startProcessor(CommandQueue com) throws Exception;

  public void stopProcessor() throws Exception;
}
