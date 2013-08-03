package com.cricket.mmog.resp;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;

public interface Response {

  public Game getGame();

  void addRecepient(CricketPresence p);

  void addRecepients(CricketPresence[] p);

  void addObservers(CricketPresence[] p);

  CricketPresence[] observers();

  CricketPresence removeRecepient(CricketPresence p);

  public boolean recepientExists(CricketPresence p);

  void broadcast(CricketPresence[] p, String command);

  void setCommand(CricketPresence p, String command);

  public String getBroadcast();

  public String getCommand(CricketPresence p);

  public CricketPresence[] recepients();

}
