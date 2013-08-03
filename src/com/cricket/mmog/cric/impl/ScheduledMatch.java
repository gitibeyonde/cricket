package com.cricket.mmog.cric.impl;

import java.util.Observer;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import com.cricket.mmog.resp.Response;
import com.cricket.mmog.cric.JoinResponse;
import com.cricket.mmog.Moves;
import com.cricket.mmog.CricketPlayer;
import com.cricket.mmog.cric.Cricket;
import com.cricket.mmog.cric.GameDetailsResponse;
import com.cricket.mmog.cric.GameState;

import com.cricket.mmog.cric.TossResponse;

import com.cricket.mmogserver.GamePlayer;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import java.util.logging.Logger;

public class ScheduledMatch extends Cricket {
  static Logger _cat = Logger.getLogger(ScheduledMatch.class.getName());

  public ScheduledMatch(Date d, String[] ta, String[] tb, int id, String name, int type, String tA, String tB,
                     int ball_per_player, int fees, int buyin,
                     Observer stateObserver) {
   super(name, type, tA, tB, ball_per_player, fees, buyin, stateObserver);
   _sch_date = d;
   _ta = ta;
   _tb = tb;
   _cat.finest(this.toString());
   Calendar rightNow = Calendar.getInstance();
    rightNow.set(Calendar.SECOND, 0);
   rightNow.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
   Calendar cal=Calendar.getInstance();
   cal.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
   cal.setTime(_sch_date);
   _cat.finest("Right now=" + rightNow.getTime() + ", schedule date=" + cal.getTime());
   _delta=delta(rightNow, cal);
   _cat.finest("Delta = " + _delta);
   _tstate = GameState.DECLARATION;
   _mr = new MatchRunner();
   Timer t = new Timer();
   //t.schedule( _mr, cal.getTime(), 60000);
    t.schedule( _mr, 0, 60000);
 }

  public synchronized Response[] start() {
    _cat.finest("NEW GAME------------------------");
    setupNewRun();
    if (!reRunCondition()) {
      _cat.finest("Game cannot be started = A" + _pitch.getTeamA() + ", B= " +
                 _pitch.getTeamB());
      _inProgress = false;
      Response[] r = {new GameDetailsResponse(this, true)};
      return r;
    }
    else {
      _state = new GameState(GameState.TOSS);
      _tstate = GameState.STARTED;
      Response[] r = {new TossResponse(this, null)};
      _mr.running();
      return r;
    }
  }

 public boolean reRunCondition() {
   Calendar rightNow = Calendar.getInstance();
    rightNow.set(Calendar.SECOND, 0);
   rightNow.setTimeZone(TimeZone.getTimeZone("GMT"));
   Calendar cal=Calendar.getInstance();
   cal.setTime(_sch_date);
    _delta = delta(rightNow, cal);
   _cat.finest("Size="+  _pitch.getTeamA().eligiblePlayers(0).length + ", Delta=" + _delta);
   return _pitch.getTeamA().eligiblePlayers(0).length >= 1 &&
       _pitch.getTeamB().eligiblePlayers(0).length >= 1 && _delta <= 1;
   // any other validations
 }
 
/**
//allow only invited player
 public synchronized Response[] join(Presence p) {
   _cat.finest(" Joining = " + p);
   
   if (_delta > 1){
       _cat.finest("Match not yet started " + _delta);
        Response[] r = {new JoinResponse(this, -57)};
   }
   
   if (_pitch.onPitch(p)) {
     _cat.info("Player is already there on the pitch " + p);
     p.lastMove(Moves.NONE);
     Response[] r = {new JoinResponse(this, -9)};
     return r;
   }

   if (!(_pitch.canJoinTeamA(p) || _pitch.canJoinTeamB(p))) {
     // couldn't be added for whatever reason
     p.lastMove(Moves.NONE);
     Response[] r = {new JoinResponse(this, -8)};
     return r;
   }
   // check if invited
   if (p._team_name.equals(_pitch._teamA._team_name) && inTeamA(p) || p._team_name.equals(_pitch._teamB._team_name) && inTeamB(p)){   
    p.unsetAllStatus();
     p.lastMove(Moves.JOIN);
     p.setPlayer();
     p.setNew();
     p.setWaiting();
     p.setGID(_id);
     p.reset();
     _cat.finest("Joined " + p + "\n Team=" + p.team());
     if (_inProgress || !reRunCondition()) {
       _cat.finest(_inProgress + ", ReRun = " + reRunCondition());
       Response[] r = {new JoinResponse(this, 4)};
       return r;
     }
     else {
       _inquirer = p;
       return start();
     }
   }
   else {
     // cannot join as uninvited
     p.lastMove(Moves.NONE);
     Response[] r = {new JoinResponse(this, -56)};
     return r;
   }
 }

 public boolean inTeamA(Presence p){
   for (int i=0;i<_ta.length;i++){
     if (_ta[i].endsWith(p.name())) return true;
   }
   return false;
 }

 public boolean inTeamB(Presence p){
   for (int i=0;i<_tb.length;i++){
     if (_tb[i].endsWith(p.name())) return true;
   }
   return false;
 }**/

 public int delta(Calendar d2, Calendar d1) {
     int delta = 0;

     _cat.finest(d1.get(Calendar.YEAR) + ", "+ d2.get(Calendar.YEAR));
     _cat.finest(d1.get(Calendar.DAY_OF_YEAR) + ", "+ d2.get(Calendar.DAY_OF_YEAR));
     _cat.finest(d1.get(Calendar.HOUR_OF_DAY) + ", "+ d2.get(Calendar.HOUR_OF_DAY));
     _cat.finest(d1.get(Calendar.MINUTE) + ", "+ d2.get(Calendar.MINUTE));

     delta =
             518400 * (d1.get(Calendar.YEAR) - d2.get(Calendar.YEAR)) + 1440 *
             (d1.get(Calendar.DAY_OF_YEAR) - d2.get(Calendar.DAY_OF_YEAR)) +
             60 *
             (d1.get(Calendar.HOUR_OF_DAY) - d2.get(Calendar.HOUR_OF_DAY)) +
             d1.get(Calendar.MINUTE) - d2.get(Calendar.MINUTE);
     return delta;
 }

 public Calendar getScheduleDate(){
   Calendar cal=Calendar.getInstance();
   cal.setTimeZone(TimeZone.getTimeZone("GMT"));
   cal.setTime(_sch_date);
   return cal;
 }


 public int _tstate=0;
 Date _sch_date;
 public int _delta;
 public String[] _ta, _tb;
 MatchRunner _mr;
 
 
 public class MatchRunner extends TimerTask {
    boolean running=false;
  
    public void run(){
        _cat.finest("Running timer task----");
        try {
            if (!running){
                    com.cricket.mmogserver.GameProcessor.deliverResponse(start());
            }
            else {
               Calendar rightNow = Calendar.getInstance();
                rightNow.set(Calendar.SECOND, 0);
               rightNow.setTimeZone(TimeZone.getTimeZone("GMT"));
               Calendar cal=Calendar.getInstance();
               cal.setTime(_sch_date);
                _delta = delta(rightNow, cal); 
            }
        }
        catch (Exception e){
            _cat.warning(e.getMessage());
        }
    }
    
    public void running(){
        running=true;
    }
     
 }

}
