package com.cricket.mmog;

import com.cricket.common.db.DBPlayer;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import com.agneya.util.*;

import com.cricket.mmog.cric.util.Team;
//import com.cricket.mmog.Player;
import com.cricket.mmog.cric.util.MoveParams;

public class Player implements Serializable {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(Player.class.getName());

  protected String _name;
  protected int _gender;
  boolean _isAuthenticated = false;
  protected Hashtable _presence_registry;
  protected DBPlayer _dbPlayer;
  protected boolean _shill = false;

  public Player(String name, DBPlayer dbp) {
    _name = name;
    _dbPlayer = dbp;
    _presence_registry = new Hashtable();
  }

  public Player(String name, int points) {
    _name = name;
    _dbPlayer = new DBPlayer();
    _dbPlayer.setPoints(points);
    _presence_registry = new Hashtable();
  }

  public void shill(boolean isbot) {
    _shill = isbot;
  }

  public boolean shill() {
    return _shill;
  }

  public DBPlayer getDBPlayer() {
    return _dbPlayer;
  }

  public void setDBPlayer(DBPlayer p) {
    _dbPlayer = p;
  }

  public Presence presence(String tid) {
    return (Presence) _presence_registry.get(tid);
  }

  public Presence createPresence(String tid) {
    Presence p = (Presence) _presence_registry.get(tid);
    if (p == null) {
      p = new Presence(tid, this);
    }
    _presence_registry.put(tid, p);
    return p;
  }

  public void movePresence(Presence p, String tid) {
    removePresence(p);
    p._gid = tid;
    _presence_registry.put(p.getGID(), p);
  }

  public void removePresence(Presence p) {
    _presence_registry.remove(p.getGID());
  }

  public int presenceCount() {
    return _presence_registry.size();
  }

  public int points() {
    return _dbPlayer.getPoints();
  }

  public void addPoints(int win) {
    _dbPlayer.setPoints(_dbPlayer.getPoints() + win);
  }

  public void points(int amt) {
    _dbPlayer.setPoints(amt);
  }

  public String name() {
    return _name;
  }

  public void name(String name) {
    this._name = name;
  }

  public void gender(int gender) {
    _gender = gender;
  }

  public int gender() {
    return _gender;
  }

  public String toString() {
    return "name=" + _name + ", " + (_shill ? "bot" : "real");
  }


}
