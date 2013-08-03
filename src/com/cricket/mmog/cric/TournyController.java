package com.cricket.mmog.cric;

import java.util.*;
import com.cricket.mmog.*;
import com.agneya.util.Configuration;

import com.cricket.common.db.DBException;


public class TournyController {
  Hashtable _map;
  static Object _obj = new Object();
  static TournyController _tc = null;

  public static TournyController instance() {
    if (_tc == null) {
      synchronized (_obj) {
        if (_tc == null) {
          _tc = new TournyController();
          return _tc;
        }
        else {
          return _tc;
        }
      }
    }
    return _tc;
  }

  private TournyController() {
    _map = new Hashtable();
    int timer = 20000;
    try {
      timer = Configuration.instance().getInt("Tourny.Schedular.Interval");
    }
    catch (Exception e) {

    }
    Timer t = new Timer();
    t.schedule(new TournySchedular(_map), 0, timer);
  }

  public Tourny[] listAll() {
    return (Tourny[]) _map.values().toArray(new Tourny[] {});
  }

  public Tourny getTourny(String id) {
    return (Tourny) _map.get(id);
  }

  public boolean tournyOver(String id){
    return ((Tourny) _map.get(id)).tournyOver();
  }

  public String addTourny(String name, int[] schedule, double buy_ins,
                       double fees, int chips, int maxP, int di,
                       int ri,
                       int ji, Observer lob) throws DBException {
    Tourny t = new Tourny(name, schedule, buy_ins, fees, chips, maxP, di, ri, ji, lob);
    String id = t.initNewTourny();
    _map.put(id + "", t);
    return id;
  }

  public String addTourny(Tourny t) {
    _map.put(t._name, t);
    return t._name;
  }

  public void removeTourny(String tid) {
    _map.remove(tid);
  }

  public boolean registerPlayer(String tid, CricketPlayer p) {
    return ( (Tourny) _map.get(tid)).register(p);
  }

}
