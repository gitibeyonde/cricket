package com.cricket.mmog.cric.test;

import java.util.*;
import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.resp.*;

/**
 * Created by IntelliJ IDEA. User: aprateek Date: May 24, 2004 Time: 2:48:47 PM To
 * change this template use File | Settings | File Templates.
 */
public class Test {

  public static void p(String s) {
    System.out.print(s + "\n");
  }

  public static void p(StringBuffer buf) {
    p(buf.toString());
  }

  public static void p(String tid, Response r, CricketPresence p) {
    if (r == null) {
      return;
    }
    if (r instanceof com.cricket.mmog.cric.IllegalReqResponse) {
      throw new IllegalStateException();
    }
    String clazz = r.getClass().getName();
    p(System.currentTimeMillis() + "-------- Move by: " +
      (p == null ? "NULL" : p.name()));
    p("-------- Move: " + (p == null ? "NONE" : Moves.stringValue(p.lastMove())));
    p(clazz);
    CricketPresence[] pl = (CricketPresence[]) r.recepients();
    for (int i = 0; i < pl.length; i++) {
      p("player : name=" + pl[i].name() + ", " + r.getCommand(pl[i]));
    }

  }

  public static void p(Response r) {
    if (r instanceof com.cricket.mmog.cric.IllegalReqResponse) {
      throw new IllegalStateException();
    }
    String clazz = r.getClass().getName();
    p("--------");
    p(clazz);
    CricketPresence[] pl = (CricketPresence[]) r.recepients();
    for (int i = 0; i < pl.length; i++) {
      p("player : name=" + pl[i].name() + ", " + r.getCommand(pl[i]));
    }
    pl = (CricketPresence[]) r.observers();
    p("broadcast: " + r.getBroadcast());
    pl = (CricketPresence[]) r.observers();
    for (int i = 0; i < pl.length; i++) {
      p("Observers: " + pl[i].name());
    }
  }

  public static void p(String tid, Response[] r, CricketPresence p){
    if (r==null)return;
    for (int i=0;i<r.length;i++){
      p(tid, r[i], p);
    }
  }

  public static void p(Response []r){
    for (int i=0;i<r.length;i++){
      p(r[i]);
    }
  }

  public static void pp(Response r, CricketPresence p) {
    p(r);
  }

  public static void pp(Response []r, CricketPresence p){
      for (int i=0;i<r.length;i++){
        pp(r[i], p);
      }
    }

  public void pp(Response r) {
    // do nothing
  }

}


