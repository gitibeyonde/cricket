package com.cricket.mmog;


import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import com.agneya.util.*;

import com.cricket.common.db.DBCricketPlayer;
import com.cricket.mmog.cric.util.Team;
import com.cricket.mmog.Player;
import com.cricket.mmog.cric.util.MoveParams;

import com.cricket.common.db.DBException;
import com.cricket.common.db.DBPlayer;

public class CricketPlayer extends Player implements Serializable {
  // set the category for logging
  transient static Logger _cat = Logger.getLogger(CricketPlayer.class.getName());

  DBCricketPlayer _dbCricketPlayer=null;
  

  public CricketPlayer(String name, DBPlayer dbp) {
      super(name, dbp);
    _name = name;
    _dbPlayer = dbp;
    _dbCricketPlayer = new DBCricketPlayer();
      try {
          _dbCricketPlayer.get(name);
      }
      catch (DBException e){
          // if entry does not exists create it
           try {
              _dbCricketPlayer.save();
           }
           catch (DBException ex){
               ex.printStackTrace();
           }
      }
      
    _presence_registry = new Hashtable();
  }

  public CricketPlayer(String name, int points) {
      super(name, points);
    _name = name;
    _dbPlayer = new DBPlayer();
    _dbCricketPlayer = new DBCricketPlayer();
      try {
          _dbCricketPlayer.get(name);
      }
      catch (DBException e){
          e.printStackTrace();
          // if entry does not exists create it
           try {
              _dbCricketPlayer.save(name);
           }
           catch (DBException ex){
               ex.printStackTrace();
           }
      }
    //_dbPlayer.setPoints(points);
    _presence_registry = new Hashtable();
  }

  public DBCricketPlayer getDBCRicketPlayer(){
      return _dbCricketPlayer;
  }


  public String toString() {
    return "name=" + _name + ", " + (_shill ? "bot" : "real");
  }

}
