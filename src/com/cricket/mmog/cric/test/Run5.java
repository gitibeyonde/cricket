package com.cricket.mmog.cric.test;

import java.io.*;
import java.util.logging.Logger;
import com.agneya.util.*;
import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.*;
import com.cricket.mmog.gamemsgimpl.*;
import com.cricket.mmog.resp.*;
import com.cricket.mmog.cric.util.MoveParams;

public class Run5
    extends Test {

  CricketPlayer pl0, pl1, pl2, pl3, pl4, pl5, pl6, pl7, pl8, pl9;
  CricketPresence p0, p1, p2, p3, p4, p5, p6, p7, p8, p9;

  public void initTable() throws IOException {
    // message to create a new poker with id 1
    // @todo : max rounds chaged to 2 from 4
    Cricket bj = new Cricket("Cricket", 4, "Eagles", "Dudes", 5, 10, 5, null);
    // Game.created
    Response r;
    pl0 = new CricketPlayer("P0", 1000);
    p0 = (CricketPresence)pl0.createPresence("Cricket");
    p0.setPos(0);
    p0.setTeamName("Eagles");
    //p0.setTournyWorth("Cricket"000);
    pl1 = new CricketPlayer("P1", 1000);
    p1 = (CricketPresence)pl1.createPresence("Cricket");
    p1.setPos(1);
    p1.setTeamName("Eagles");
    //p1.setTournyWorth("Cricket"000);
    pl2 = new CricketPlayer("P2", 1000);
    p2 = (CricketPresence)pl2.createPresence("Cricket");
    p2.setPos(2);
    p2.setTeamName("Eagles");
    //p2.setTournyWorth(500);
    pl3 = new CricketPlayer("P3", 1000);
    p3 = (CricketPresence)pl3.createPresence("Cricket");
    p3.setPos(3);
    p3.setTeamName("Eagles");
    //p3.setTournyWorth("Cricket"00);
    pl4 = new CricketPlayer("P4", 1000);
    p4 = (CricketPresence)pl4.createPresence("Cricket");
    p4.setPos(4);
    p4.setTeamName("Eagles");
    //p4.setTournyWorth(400);


    pl5 = new CricketPlayer("P5", 1000);
    p5 = (CricketPresence)pl5.createPresence("Cricket");
    p5.setPos(0);
    p5.setTeamName("Dudes");
    //p5.setTournyWorth("Cricket"00);
    pl6 = new CricketPlayer("P6", 1000);
    p6 = (CricketPresence)pl6.createPresence("Cricket");
    p6.setPos(1);
    p6.setTeamName("Dudes");
    //p6.setTournyWorth("Cricket"00);
    pl7 = new CricketPlayer("P7", 1000);
    p7 = (CricketPresence)pl7.createPresence("Cricket");
    p7.setPos(2);
    p7.setTeamName("Dudes");
    //p7.setTournyWorth("Cricket"00);
    pl8 = new CricketPlayer("P8", 1000);
    p8 = (CricketPresence)pl8.createPresence("Cricket");
    p8.setPos(3);
    p8.setTeamName("Dudes");
    //p8.setTournyWorth("Cricket"00);
    pl9 = new CricketPlayer("P9", 1000);
    p9 = (CricketPresence)pl9.createPresence("Cricket");
    p9.setPos(4);
    p9.setTeamName("Dudes");
    //p9.setTournyWorth("Cricket"00);

    pp(Game.handle(new ObserveGameImpl(p0, "Cricket")), p0);
    pp(Game.handle(new ObserveGameImpl(p1, "Cricket")), p1);
    pp(Game.handle(new ObserveGameImpl(p2, "Cricket")), p2);
    pp(Game.handle(new ObserveGameImpl(p3, "Cricket")), p3);
    pp(Game.handle(new ObserveGameImpl(p4, "Cricket")), p4);
    pp(Game.handle(new ObserveGameImpl(p5, "Cricket")), p5);
    pp(Game.handle(new ObserveGameImpl(p6, "Cricket")), p6);
    pp(Game.handle(new ObserveGameImpl(p7, "Cricket")), p7);
    pp(Game.handle(new ObserveGameImpl(p8, "Cricket")), p8);
    pp(Game.handle(new ObserveGameImpl(p9, "Cricket")), p9);

    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p0)), p0);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p1)), p1);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p2)), p2);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p3)), p3);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p4)), p4);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p5)), p5);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p6)), p6);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p7)), p7);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p8)), p8);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p9)), p9);

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.TOSS, 1.0)), p0);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p5, Moves.HEAD, 1.0)), p5);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p5, Moves.TOSS, 1.0)), p5);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.HEAD, 1.0)), p0);
    }

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.FIELDING, 1.0)), p0);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.BATTING, 1.0)), p1);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p2, Moves.FIELDING, 1.0)), p2);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p3, Moves.BATTING, 1.0)), p3);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p4, Moves.FIELDING, 1.0)), p4);
    }
    catch (IllegalStateException e) {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p5, Moves.FIELDING, 1.0)), p5);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p6, Moves.BATTING, 1.0)), p6);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p7, Moves.FIELDING, 1.0)), p7);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p8, Moves.BATTING, 1.0)), p8);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p9, Moves.FIELDING, 1.0)), p9);
    }

    try {
      MoveParams mp = MoveParams.generate(MoveParams.BOWL);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p5, Moves.B_BOWL, 1.0, mp)), p5);
    }
    catch (IllegalStateException e) {
      MoveParams mp = MoveParams.generate(MoveParams.BOWL);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.B_BOWL, 1.0, mp)), p0);
    }

    try {
      MoveParams mp = MoveParams.generate(MoveParams.BATS);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.K_BAT, 1.0, mp)), p0);
    }
    catch (IllegalStateException e) {
      MoveParams mp = MoveParams.generate(MoveParams.BATS);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p5, Moves.K_BAT, 1.0, mp)), p5);
    }

    try {
      //p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.F_FIELD, 1.0)), p0);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.F_FIELD, 1.0)), p1);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p2, Moves.F_FIELD, 1.0)), p2);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p3, Moves.F_FIELD, 1.0)), p3);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p4, Moves.F_FIELD, 1.0)), p4);
    }
    catch (IllegalStateException e) {
      //p("Cricket", Game.handle(new MoveImpl("Cricket", p5, Moves.F_FIELD, 1.0)), p5);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p6, Moves.F_FIELD, 1.0)), p6);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p7, Moves.F_FIELD, 1.0)), p7);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p8, Moves.F_FIELD, 1.0)), p8);
      p("Cricket", Game.handle(new MoveImpl("Cricket", p9, Moves.F_FIELD, 1.0)), p9);
    }
    try {
        MoveParams mp = MoveParams.generate(MoveParams.BOWL);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p5, Moves.B_BOWL, 1.0, mp)), p5);
      }
      catch (IllegalStateException e) {
        MoveParams mp = MoveParams.generate(MoveParams.BOWL);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.B_BOWL, 1.0, mp)), p0);
      }

      try {
        MoveParams mp = MoveParams.generate(MoveParams.BATS);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.K_BAT, 1.0, mp)), p0);
      }
      catch (IllegalStateException e) {
        MoveParams mp = MoveParams.generate(MoveParams.BATS);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p5, Moves.K_BAT, 1.0, mp)), p5);
      }

      try {
        //p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.F_FIELD, 1.0)), p0);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.F_FIELD, 1.0)), p1);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p2, Moves.F_FIELD, 1.0)), p2);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p3, Moves.F_FIELD, 1.0)), p3);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p4, Moves.F_FIELD, 1.0)), p4);
      }
      catch (IllegalStateException e) {
        //p("Cricket", Game.handle(new MoveImpl("Cricket", p5, Moves.F_FIELD, 1.0)), p5);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p6, Moves.F_FIELD, 1.0)), p6);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p7, Moves.F_FIELD, 1.0)), p7);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p8, Moves.F_FIELD, 1.0)), p8);
        p("Cricket", Game.handle(new MoveImpl("Cricket", p9, Moves.F_FIELD, 1.0)), p9);
      }

  }

  public static void main(String argv[]) throws Exception {
   
    new Run5().initTable();
  }

}
