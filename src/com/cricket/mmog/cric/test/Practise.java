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

public class Practise
    extends Test {

  CricketPlayer pl0;
  CricketPresence p0;

  public void initTable() throws IOException {
    // message to create a new poker with id 1
    // @todo : max rounds chaged to 2 from 4
    Cricket bj = new Cricket( "Cricket", 1, "Eagles", "Dudes", 5,0,0, null);
    // Game.created
    Response r;
    pl0 = new CricketPlayer("P0", 1000);
    p0 = (CricketPresence)pl0.createPresence("Cricket");
    p0.setPos(0);
    //p0.setTournyWorth("Cricket"000);

    pp(Game.handle(new ObserveGameImpl(p0, "Cricket")), p0);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p0)), p0);

    p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.START, 1.0)), p0);
    MoveParams mp = MoveParams.generate(MoveParams.BATS);
    p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.K_BAT, 1.0, mp)), p0);


    p("Cricket", Game.handle(new LeaveGameImpl("Cricket", p0, false)), p0);

    pp(Game.handle(new ObserveGameImpl(p0, "Cricket")), p0);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p0)), p0);

    p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.START, 1.0)), p0);
    mp = MoveParams.generate(MoveParams.BATS);
    p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.K_BAT, 1.0, mp)), p0);

  }

  public static void main(String argv[]) throws Exception {
   
    new Practise().initTable();
  }

}
