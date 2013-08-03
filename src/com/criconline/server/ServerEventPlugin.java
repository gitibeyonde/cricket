package com.criconline.server;

import com.criconline.actions.Action;
import java.util.logging.Logger;
import com.criconline.models.PlayerModel;
import com.cricket.common.message.GameEvent;
import com.cricket.common.message.Response;
import com.cricket.common.message.ResponsePlayerShift;
import com.criconline.actions.*;
import com.criconline.ServerMessagesListener;
import com.cricket.mmog.cric.util.ActionConstants;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Vector;
import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.common.message.Command;

public class ServerEventPlugin extends EventGenerator implements Runnable,
    ActionConstants {
  static Logger _cat = Logger.getLogger(EventGenerator.class.getName());
  static String _gid = "";
  static int _grid = 1000;
  Vector _delay;
  Vector _ge;
  public int _play;

  //  wide on leg side =bowl|-1|1|1750336695|532|5.32|230|-3|4|30|0
  //  pitch point on extreme right =bowl|-1|1|1729820523|530|1.40|230|0|2|2|0
  // pitch point slightly to right to the wicket =bowl|-1|1|1729675820|516|5.94|230|0|4|0|1000

  // wide on the off side bowl|-1|1|1729511757|468|1.15|230|3|2|31|0|468|230
//straight ball "bowl|-1|1|1729675820|516|5.94|230|0|4|0|1000|500|230";
  String bmd = "bowl|-1|1|1729675820|516|5.94|230|0|4|0|1000|500|230";


  public ServerEventPlugin() {
    ActionFactory af;
    // create a action registry entry
    af = new ActionFactory(this, "Cric");
    _action_registry.put("" + 1, af);
    Timer t = new Timer();
    t.schedule(new ActionProcessor(), 0, 500);
    _ge = new Vector();
    _delay = new Vector();
    _play = BAT;
    if (_play == BAT) {
      _name = "abhido";
    }
    else if (_play == BOWL) {
      _name = "comp1";
    }
    else if (_play == FIELD) {
      _name = "comp2";
    }
    else {
      _name = "comp6";
    }
  }

  public void addWatchOnTable(String tid, ServerMessagesListener changesListener) {
    addServerMessageListener(tid, changesListener);
    if (_play == -1) {
      String sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,pot=20,state=4,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," +
                   "fielding=0|Tigers|comp1|1048576|1|0|0|0|0`1|Tigers|comp2|1048576|1|0|0|0|0`2|Tigers|comp3|1048576|1|0|0|0|0," +
                   "batting=0|Eagles|abhido|1048576|1|0|0|0|0,";
      sge += "md=,next-move=-1|none|none|wait|5,last-move=1|Tigers|comp2|join|0.00,,tp=5|Tigers";
      GameEvent ge = new GameEvent();
      ge.init(sge);

      processGameEvent(ge, 0);

      String com_sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," + "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                       "batting=0|Eagles|abhido|1073741825|1|0|0|0|0,";
      sge = com_sge + ",md=,next-move=0|Tigers|comp1|bowl|0.00,,tp=5|Tigers";

      ge = new GameEvent();
      ge.init(sge);
      processGameEvent(ge, 500);


      sge = com_sge + ",md=bowl|8|14|2333|4|4|0|6|0|4|100,last-move=0|Tigers|comp1|bowl|5,next-move=0|Eagles|abhido|bat|0.00,,tp=5|Tigers";

      ge = new GameEvent();
      ge.init(sge);
      processGameEvent(ge, 1000);

      sge = com_sge + ",next-move=1|Tigers|comp2|field|5,tp=5|Tigers,last-move=0|Eagles|abhido|bat|5,md=bat|8|3|2333|4|5.3|0|3|4|10|500|0|0,";
      ge = new GameEvent();
      ge.init(sge);
      tid = ge.getGameId();

      processGameEvent(ge, 8000);

      sge = com_sge +
            "last-move=1|Tigers|comp2|field|5,md=field|10|8|2333|4|4|0|6|0|10|500,";
      ge = new GameEvent();
      ge.init(sge);
      tid = ge.getGameId();

      processGameEvent(ge, 14000);

      sge = com_sge + ",next-move=0|Tigers|comp1|bowl|5,tp=5|Tigers";
      ge = new GameEvent();
      ge.init(sge);
      tid = ge.getGameId();

      processGameEvent(ge, 20000);

      sge = com_sge + ",md=,next-move=0|Eagles|abhido|bat|5,last-move=0|Tigers|comp1|bowl|0.00,,tp=5|Tigers";

      ge = new GameEvent();
      ge.init(sge);
      tid = ge.getGameId();

      processGameEvent(ge, 25000);


    }
  }

 

  public void joinTable(String tid, int pos, String team, double value) {
    _cat.finest("Joining gid=" + tid + ", grid=" + _grid + ", pos=" + pos +
               ", team=" + team);
    _grid = -1;
    if (_play == -1) {
      return;
    }

    String sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=4,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," +
                 "fielding=0|Tigers|comp1|1048576|1|0|0|0|0`1|Tigers|comp2|1048576|1|0|0|0|0`2|Tigers|comp3|1048576|1|0|0|0|0," +
                 "batting=0|Eagles|abhido|1048576|1|0|0|0|0,";
    if (_play == BAT) {
      sge += "md=,next-move=0|Tigers|comp1|bowl|5,last-move=0|Eagles|abhido|join|0.00,,tp=0|Eagles";
    }
    else if (_play == BOWL) {
      sge += "md=,next-move=-1|none|none|wait|5,last-move=0|Tigers|comp1|join|0.00,,tp=0|Tigers";
    }
    else if (_play == FIELD) {
      sge += "md=,next-move=-1|none|none|wait|5,last-move=1|Tigers|comp2|join|0.00,,tp=1|Tigers";
    }

    GameEvent ge = new GameEvent();
    ge.init(sge);

    processGameEvent(ge, 500);


    sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," + "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
          "batting=0|Eagles|abhido|1073741825|1|0|0|0|0,";
    if (_play == BAT) {
      sge += ",md=" + bmd  + ",next-move=0|Eagles|abhido|bat|5,last-move=0|Tigers|comp1|bowl|0.00,,tp=0|Eagles";
    }
    else if (_play == BOWL) {
      sge += "md=,next-move=0|Tigers|comp1|bowl|5,last-move=0|Tigers|comp1|join|0.00,,tp=0|Tigers";
    }
    else if (_play == FIELD) {
      sge += ",md=" + bmd + ",next-move=0|Eagles|abhido|bat|5,last-move=0|Tigers|comp1|bowl|0.00,,tp=1|Tigers";
    }
    ge = new GameEvent();
    ge.init(sge);
    processGameEvent(ge, 2000);


    if (_play == FIELD) {

      sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," +
            "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
            "batting=0|Eagles|abhido|1073741825|1|0|0|0|0,";

      sge += ",md=" + bmd + ",last-move=0|Eagles|abhido|bat|5,next-move=1|Tigers|comp2|field|0.00,,tp=1|Tigers";
    ge = new GameEvent();
    ge.init(sge);
    processGameEvent(ge, 8000);

    }


  }

  public void sendToServer(Command c){
  }

  public void sendToServer(String tid, Action o) {
    if (o instanceof MoveAction) {
      MoveAction ba = (MoveAction) o;
      MoveParams mp = ba.getMoveParams();
    _cat.finest("Game=" + tid + ", action=" + o + ", MP= " + mp);
      switch (ba.getId()) {
        case BOWL:
          String sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,pot=20,state=8,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," + "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                       "batting=0|Eagles|abhido|1073741825|1|0|0|0|0," +
                       ",last-move=0|Tigers|comp1|bowl|5,md=" +
                       mp.stringValue() + ",next-move=0|Eagles|abhido|bat|5,,tp=0|Tigers";
          GameEvent ge = new GameEvent();
          ge.init(sge);

          processGameEvent(ge, 1000);

          mp = MoveParams.generate(MoveParams.BATS);
          sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," + "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                "batting=0|Eagles|abhido|1073741825|1|0|0|0|0," +
                ",last-move=0|Eagles|abhido|bat|5,md=" +
                mp.stringValue() + ",next-move=1|Tigers|comp2|field|5,,tp=0|Tigers";
          ge = new GameEvent();
          ge.init(sge);

          processGameEvent(ge, 1100);

          ResponsePlayerShift rps = new ResponsePlayerShift(_gid, "LF1", 5, 0, 0);
          processPlayerShift(rps, 1220);
          rps = new ResponsePlayerShift(_gid, "LF1", 40, 0, 0);
          processPlayerShift(rps, 1520);
          rps = new ResponsePlayerShift(_gid, "LF1", 20, 0, 0);
          processPlayerShift(rps, 4020);
          rps = new ResponsePlayerShift(_gid, "LF1", 20, 0, 0);
          processPlayerShift(rps, 4520);
          rps = new ResponsePlayerShift(_gid, "LF1", 20, 0, 0);
          processPlayerShift(rps, 5020);

          mp = MoveParams.generate(MoveParams.FIELD);
          mp.setX(310);
          mp.setY(200);
          //next-move=-1|none|none|wait|5
          sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," +
                "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                "batting=0|Eagles|abhido|1073741825|1|0|0|0|0," +
                ",last-move=1|Tigers|comp2|field|5,md=" + mp.stringValue() + ",next-move=-1|none|none|wait|5,tp=0|Tigers";
          ge = new GameEvent();
          ge.init(sge);

          processGameEvent(ge, 10000);

          sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," + "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
              "batting=0|Eagles|abhido|1073741825|1|0|0|0|0," +
              ",next-move=0|Tigers|comp1|bowl|5,,tp=0|Tigers";
        ge = new GameEvent();
        ge.init(sge);

        processGameEvent(ge, 20000);


          break;
        case BAT: // END

          sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," +
                "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                "batting=0|Eagles|abhido|1073741825|1|0|0|0|0," +
                "md=" + bmd + ",tp=0|Eagles," +
                //"next-move=1|Tigers|comp2|field|5" +
                "next-move=1|Pitch|Empire|none|5," +
                "last-move=0|Eagles|abhido|bat|5,md=" +
                mp.stringValue();
          ge = new GameEvent();
          ge.init(sge);
          tid = ge.getGameId();

          processGameEvent(ge, 4000);

          sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," + "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                "batting=0|Eagles|abhido|1073741825|1|0|0|0|0," +
                "md=bowl|8|4|2333|4|0|6|0|10|500,tp=0|Eagles" +
                "";//last-move=1|Tigers|comp2|field|5,md=field|10|8|4|4|0|6|0,";
          ge = new GameEvent();
          ge.init(sge);
          tid = ge.getGameId();

          processGameEvent(ge, 5000);

          sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," +
                "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                "batting=0|Eagles|abhido|1073741825|1|0|0|0|0," +
                ",next-move=0|Tigers|comp1|bowl|5,tp=0|Eagles";
          ge = new GameEvent();
          ge.init(sge);
          tid = ge.getGameId();

          processGameEvent(ge, 8000);

          sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," + "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                "batting=0|Eagles|abhido|1073741825|1|0|0|0|0," +
                ",md=" + bmd + ",next-move=0|Eagles|abhido|bat|5,last-move=0|Tigers|comp1|bowl|0.00,,tp=0|Eagles";

          ge = new GameEvent();
          ge.init(sge);
          tid = ge.getGameId();

          processGameEvent(ge, 10000);
          break;

        case FIELD:
          sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," +
              "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                "batting=0|Eagles|abhido|1073741825|1|0|0|0|0,";

          sge += ",md=field|8|14|2333|4|4|0|6|0|10|500|0|0,last-move=1|Tigers|comp2|field|5,next-move=0|Tigers|comp1|bowl|0.00,,tp=1|Tigers";

          ge = new GameEvent();
          ge.init(sge);
          processGameEvent(ge, 6000);

          sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," +
              "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                "batting=0|Eagles|abhido|1073741825|1|0|0|0|0," +
                ",md=" + bmd + ",next-move=0|Eagles|abhido|bat|5,last-move=0|Tigers|comp1|bowl|0.00,,tp=1|Tigers";

          ge = new GameEvent();
          ge.init(sge);
          tid = ge.getGameId();
          processGameEvent(ge, 16000);

          sge = "id=" + _gid + ",grid=" + _grid + ",name=Warm Up,type=2,state=8,pot=20,bpp=2,fees=5,buyin=10,runs=0,A=Eagles,B=Tigers,balls=0," +
              "fielding=0|Tigers|comp1|2|1|0|0|0|0`1|Tigers|comp2|4|1|0|0|0|0`2|Tigers|comp3|8|1|0|0|0|0," +
                "batting=0|Eagles|abhido|1073741825|1|0|0|0|0," +
                ",md=bat|8|3|2333|4|5.8|5|3|5|10|500|0|0,next-move=1|Tigers|comp2|field|5,last-move=0|Eagles|abhido|bat|0.00,,tp=1|Tigers";

          ge = new GameEvent();
          ge.init(sge);
          tid = ge.getGameId();
          processGameEvent(ge, 20000);

          break;
        default:
          _cat.finest("Unknown move received from the client" + ba);
      }
    }
    else if (o instanceof ChatAction) {
      ChatAction ca = (ChatAction) o;
      fireServerMessagesEvent(tid, ca);
    }
  }

  public void run() {
    try {
      long del = Long.parseLong((String) _delay.remove(0));
      Object o = _ge.remove(0);
      if (o instanceof GameEvent){
        GameEvent ge = (GameEvent)o;
        Thread.currentThread().sleep(del);
        _cat.finest(ge.toString());
        String tid = ge.getGameId();
        ActionFactory af = getActionFactory(tid);
        af.processGameEvent(ge, Response.R_MOVE);
      }
      else if (o instanceof ResponsePlayerShift){
        ResponsePlayerShift ge = (ResponsePlayerShift)o;
        Thread.currentThread().sleep(del);
        _cat.finest(ge.toString());
        String tid = ge.getGid();
        ActionFactory af = getActionFactory(tid);
        af.processPlayerShift(ge);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void processGameEvent(GameEvent ge, long delay) {
    _delay.add(delay + "");
    _ge.add(ge);
    Thread th = new Thread(this);
    th.start();
  }

  public void processPlayerShift(ResponsePlayerShift rps, long delay) {
    _delay.add(delay + "");
    _ge.add(rps);
    Thread th = new Thread(this);
    th.start();
  }

  public void poll() {
  }

  public class ActionProcessor extends TimerTask {

    public void run() {
      pumpGeneratedActions();
    }
  }

}
