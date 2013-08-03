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

import java.util.Observable;
import java.util.Observer;


public class HeadsUp
    extends Test {

  CricketPlayer pl0, pl1;
  CricketPresence p0, p1;

  public void initTable() throws IOException {
    // message to create a new poker with id 1
    // @todo : max rounds chaged to 2 from 4
    Cricket bj = new Cricket("Cricket", 32,"Eagles", "Dudes", 1, 2, 10, new LoggingObserver());
    // Game.created
    Response r;
    pl0 = new CricketPlayer("pahela", 1000);
    p0 = (CricketPresence)pl0.createPresence("Cricket");
    p0.setPos(0);
    p0.setTeamName("Eagles");
    //p0.setTournyWorth(1000);


    pl1 = new CricketPlayer("dusera", 1000);
    p1 = (CricketPresence) pl1.createPresence("Cricket");
    p1.setPos(0);
    p1.setTeamName("Dudes");
    //p1.setTournyWorth(1000);


    pp(Game.handle(new ObserveGameImpl(p0, "Cricket")), p0);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p0)), p0);

    pp(Game.handle(new ObserveGameImpl(p1,"Cricket")), p1);
    p("Cricket", Game.handle(new ObserverToPlayerImpl("Cricket", p1)), p1);

    p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.TOSS, 1.0)), p0);
    p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.TAIL, 1.0)), p1);

    try {
      p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.FIELDING, 1.0)), p1);
    }
    catch (Exception e){
      p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.FIELDING, 1.0)), p0);
    }

    MoveParams mp = MoveParams.generate(MoveParams.BOWL);
    p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.B_BOWL, 1.0, mp)), p0);

    mp = MoveParams.generate(MoveParams.BATS);
    p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.K_BAT, 1.0, mp)), p1);

    mp = MoveParams.generate(MoveParams.BOWL);
    p("Cricket", Game.handle(new MoveImpl("Cricket", p1, Moves.B_BOWL, 1.0, mp)), p1);

    mp = MoveParams.generate(MoveParams.BATS);
    p("Cricket", Game.handle(new MoveImpl("Cricket", p0, Moves.K_BAT, 1.0, mp)), p0);

    /********

    MoveParams mp = MoveParams.generate(MoveParams.BAT);
    p(1, Game.handle(new MoveImpl(1, p0, Moves.K_BAT, 1.0, mp.stringValue())), p0);

    p(1, Game.handle(new LeaveGameImpl(1, p0, false)), p0);

    pp(Game.handle(new ObserveGameImpl(p0, 1)), p0);
    p(1, Game.handle(new ObserverToPlayerImpl(1, p0)), p0);

    p(1, Game.handle(new MoveImpl(1, p0, Moves.START, 1.0)), p0);
    mp = MoveParams.generate(MoveParams.BAT);
    p(1, Game.handle(new MoveImpl(1, p0, Moves.K_BAT, 1.0, mp.stringValue())), p0);
***********/
  }

  public static void main(String argv[]) throws Exception {
    
    new HeadsUp().initTable();
  }

}
class LoggingObserver
    implements Observer {

  public void update(Observable o, Object arg) {
    //System.out.println( "update o-p" );
    System.out.println(o.getClass().getName() + " -- " + arg.getClass().getName());
    System.out.println(GameStateEvent.stringVal( (GameStateEvent) arg));
  }
}