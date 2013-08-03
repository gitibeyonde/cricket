package com.cricket.mmog.cric.test;

import java.io.*;
import java.util.logging.Logger;
import com.agneya.util.*;
import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.*;
import com.cricket.mmog.gamemsgimpl.*;
import com.cricket.mmog.resp.*;

import java.util.Observable;
import java.util.Observer;

public class SimpleRun
    extends Test {

  CricketPlayer pl0, pl1;
  CricketPresence p0, p1;

  public void initTable() throws IOException {
    // message to create a new poker with id 1
    // @todo : max rounds chaged to 2 from 4
    Cricket bj = new Cricket("Cricket", 2, "Eagles", "Dudes", 1, 0, 0, null);
    // Game.created
    Response r;
    pl0 = new CricketPlayer("P0", 1000);
    pl1 = new CricketPlayer("P1", 1000);
    p0 = (CricketPresence)pl0.createPresence("Cricket");
    p0.setPos(0);
    //p0.setTeam(Team.TEAM_A);
    //p0.setTournyWorth("Cricket"000);
    p1 = (CricketPresence)pl1.createPresence("Cricket");
    p1.setPos(0);
    //p1.setTeam(Team.TEAM_B);
    //p1.setTournyWorth("Cricket"000);

    pp(Game.handle(new ObserveGameImpl(p0, "Cricket")), p0);
    pp(Game.handle(new ObserveGameImpl(p1, "Cricket")), p1);

    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p0)), p0);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p1)), p1);

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.TOSS, 1.0)), p0);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.HEAD, 1.0)), p1);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.TOSS, 1.0)), p1);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.HEAD, 1.0)), p0);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.FIELDING, 1.0)), p0);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.FIELDING, 1.0)), p1);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.B_BOWL, 1.0)), p1);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.B_BOWL, 1.0)), p0);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.K_BAT, 1.0)), p0);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.K_BAT, 1.0)), p1);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.F_FIELD, 1.0)), p0);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.F_FIELD, 1.0)), p1);
    }

    System.out.print("\n\n\n\n\n another round ");

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.B_BOWL, 1.0)), p1);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.B_BOWL, 1.0)), p0);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.K_BAT, 1.0)), p0);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.K_BAT, 1.0)), p1);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.F_FIELD, 1.0)), p0);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.F_FIELD, 1.0)), p1);
    }

    System.out.print("\n\n\n\n\n DRINKS ");
    p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.DRINKS, 1.0)), p0);
    p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.DRINKS, 1.0)), p1);
    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.B_BOWL, 1.0)), p1);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.B_BOWL, 1.0)), p0);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.K_BAT, 1.0)), p0);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.K_BAT, 1.0)), p1);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.F_FIELD, 1.0)), p0);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.F_FIELD, 1.0)), p1);
    }

    System.out.print("\n\n\n\n\n another round ");

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.B_BOWL, 1.0)), p1);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.B_BOWL, 1.0)), p0);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.K_BAT, 1.0)), p0);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.K_BAT, 1.0)), p1);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.F_FIELD, 1.0)), p0);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.F_FIELD, 1.0)), p1);
    }

  }

  public static void main(String argv[]) throws Exception {
   
    new SimpleRun().initTable();
  }

}