package com.cricket.mmog;

import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.mmog.cric.util.Team;
import com.cricket.mmog.Presence;

import java.util.logging.Logger;


public class CricketPresence extends Presence {
      // set the category for logging
      transient static Logger _cat = Logger.getLogger(CricketPresence.class.getName());
        public static final int MAX_IDLE_GC = 10;
        public int _idleGC;
        public long _idleTime;
        public long _nextMove;
        public int _nextMoveTO;
        public int length;

        public long _start_wait = -1;
        public int _pos;
        public String _team_name;
        CricketPlayer _gp;
        int _state;
        Team _team;
        int _win_points;
        int _curr_ball_count;
        int _curr_balls_played;
        int _curr_balls_bowled;
        int _curr_runs;


        public CricketPresence(String gid, CricketPlayer p) {
          super(gid, p);
          _gp = p;
          _idleTime = System.currentTimeMillis();
        }

        public void team(Team team) {
          _team = team;
        }

        public Team team() {
          return _team;
        }

        public boolean isReal() {
          return false;
        }

        public void resetNextMove() {
          _nextMove = 0;
          _nextMoveTO = -1;
          unsetResponseReq();
        }

        public void initNextMove(long move, int timeout) {
          _nextMove += move;
          _nextMoveTO = timeout;
          if (Moves.responseRequired(move)) {
            setResponseReq();
          }
          _cat.finest(Moves.stringValue(move) + " expected move = " +
                     Moves.stringValue(_nextMove));
        }

        public boolean checkNextMove(long move) {
          _cat.finest(Moves.stringValue(move) + " expected move = " +
                     Moves.stringValue(_nextMove));
          if ((move & _nextMove) == move) {
            // remove the move and return true
            _nextMove = 0L;
            return true;
          }
          else {
            return false;
          }
        }

        public void chargeFeesAndBuyin(int amt) {
         // _dbPlayer.setPoints(_dbPlayer.getPoints() - amt);
        }

        public void setWin(int p) {
          _win_points = p;
        }

        public void addWin() {
          //_dbPlayer.setPoints(_dbPlayer.getPoints() + _win_points);
          _win_points = 0;
        }



        public void incrIdleGC() {
          _idleGC++;
        }

        public void resetIdleGC() {
          _idleGC = 0;
          _idleTime = System.currentTimeMillis();
        }

        public void resetIdleTime() {
          _idleTime = System.currentTimeMillis();
        }

        public boolean isIdleGCViolated() {
          return _idleGC >= MAX_IDLE_GC /*||
               (System.currentTimeMillis() - _idleTime) > 360000*/
              ;
        }

        public CricketPlayer player() {
          return _gp;
        }


        public void reset() {
          _gp._dbCricketPlayer.bowls += _curr_balls_bowled;
          _gp._dbCricketPlayer.runs += _curr_runs;
          _gp._dbCricketPlayer.balls += _curr_balls_played;
          _curr_balls_bowled = 0;
          _curr_balls_played = 0;
          _curr_runs = 0;
        }

        public void incrBallsBowled() {
          _curr_balls_bowled++;
        }

        public int ballsBowled() {
          return _curr_balls_bowled;
        }

        public void incrBallsPlayed() {
          _curr_balls_played++;
        }

        public int ballsPlayed() {
          return _curr_balls_played;
        }

        public void addRuns(int runs) {
          _curr_runs += runs;
          if (runs == 6) {
            _gp._dbCricketPlayer.sixes++;
          }
          else if (runs == 4) {
            _gp._dbCricketPlayer.fours++;
          }
        }


        public void addMP(MoveParams mp) {
          int agr = mp.getAgression();
          int react = mp.getReaction();
          int conf = mp.geConfidence();
          _cat.finest(mp._type + " MoveParams update = " + mp);
          switch (mp._type) {
            case 0:
              _gp._dbCricketPlayer.kagression = (_gp._dbCricketPlayer.kagression + agr) / 2;
              _gp._dbCricketPlayer.kagility = (_gp._dbCricketPlayer.kagility + react) / 2;
              _gp._dbCricketPlayer.kconfidence = (_gp._dbCricketPlayer.kconfidence + conf) / 2;
              _cat.finest(agr + ", " + _gp._dbCricketPlayer.kagression + ", " + react + ", " +
                         _gp._dbCricketPlayer.kagility);
              break;
            case 1:
              _gp._dbCricketPlayer.bagression = (_gp._dbCricketPlayer.bagression + agr) / 2;
              _gp._dbCricketPlayer.bagility = (_gp._dbCricketPlayer.bagility + react) / 2;
              _gp._dbCricketPlayer.bconfidence = (_gp._dbCricketPlayer.bconfidence + conf) / 2;
              _cat.finest(agr + ", " + _gp._dbCricketPlayer.bagression + ", " + react + ", " +
                         _gp._dbCricketPlayer.bagility);
              break;
            case 3:
              _gp._dbCricketPlayer.fagression = (_gp._dbCricketPlayer.fagression + agr) / 2;
              _gp._dbCricketPlayer.fagility = (_gp._dbCricketPlayer.fagility + react) / 2;
              _gp._dbCricketPlayer.fconfidence = (_gp._dbCricketPlayer.fconfidence + conf) / 2;
              _cat.finest(agr + ", " + _gp._dbCricketPlayer.fagression + ", " + react + ", " +
                         _gp._dbCricketPlayer.fagility);
              if (mp.getFieldAction() == mp.BLOCK) {
                _gp._dbCricketPlayer.field++;
              }
              else if (mp.getFieldAction() == mp.CATCH) {
                _gp._dbCricketPlayer.catches++;
              }
              else if (mp.getFieldAction() == mp.MISFIELD) {
                //_dbPlayer.misfield++;
              }
              break;
            default:
              throw new IllegalStateException("Unknown move param type " + mp._type);
          }
        }

        public int runs() {
          return _curr_runs;
        }

        public void lastMove(long mvId) {
          _lastMove = mvId;
        }

        public long lastMove() {
          return _lastMove;
        }

        public void resetLastMove() {
          _lastMove = Moves.NONE;
        }

        public boolean isLastMove() {
          return _lastMove != Moves.NONE;
        }

        public int pos() {
          return _pos;
        }

        public void setPos(int pos) {
          _pos = pos;
        }

        public void setTeamName(String tn) {
          _team_name = tn;
        }

        public String teamName() {
          return _team._team_name;
        }

        public boolean equals(Object o) {
          Presence p = (Presence) o;
          if (p != null && p.name().equals(name()) && p.getGID() == getGID()) {
            return true;
          }
          else {
            return false;
          }
        }

        public void setTournyPresence(String tid) {
          _ps |= PlayerStatus.TOURNY;
          _tid = tid;
        }

        public boolean isTournyPresence() {
          return true;
        }

        public void unsetTournyPresence() {
          _ps &= ~PlayerStatus.TOURNY;
        }

        public void setResponseReq() {
          _ps |= PlayerStatus.RESP_REQ;
          _start_wait = System.currentTimeMillis();
        }

        public void setStartWait() {
          _start_wait = System.currentTimeMillis();
        }

        public boolean isResponseReq() {
          return (_ps & PlayerStatus.RESP_REQ) > 0;
        }

        public void unsetResponseReq() {
          _ps &= ~PlayerStatus.RESP_REQ;
          _start_wait = -1;
        }

        public boolean isNew() {
          return (_ps & PlayerStatus.NEW) > 0;
        }

        public void setNew() {
          _ps |= PlayerStatus.NEW;
        }

        public void unsetNew() {
          _ps &= ~PlayerStatus.NEW;
        }

        public void setRemoved() {
          _ps |= PlayerStatus.REMOVED;
        }

        public boolean isRemoved() {
          return (_ps & PlayerStatus.REMOVED) > 0;
        }

        public void unsetRemoved() {
          _ps &= ~PlayerStatus.REMOVED;
        }

        public void unsetPlayerStatus() {
          _ps &= ~PlayerStatus.M_PLAYING;
        }

        public void setFielder(long i) {
          _ps &= ~PlayerStatus.M_FIELDER;
          _ps |= i;
        }

        public boolean isFielder() {
          return (_ps & PlayerStatus.M_FIELDER) > 0;
        }

        public void unsetFielder() {
          _ps &= ~PlayerStatus.M_FIELDER;
        }

        public void setBatsman() {
          _ps |= PlayerStatus.BATSMAN;
        }

        public boolean isBatsman() {
          return (_ps & PlayerStatus.BATSMAN) > 0;
        }

        public void unsetBatsman() {
          _ps &= ~PlayerStatus.BATSMAN;
        }


        /**public void setRunner() {
          _ps |= PlayerStatus.RUNNER;
             }

             public boolean isRunner() {
          return (_ps & PlayerStatus.RUNNER) > 0;
             }

             public void unsetRunner() {
          _ps &= ~PlayerStatus.RUNNER;
             }**/

        public void setBowler() {
          _ps |= PlayerStatus.BOWLER;
        }

        public boolean isBowler() {
          return (_ps & PlayerStatus.BOWLER) > 0;
        }

        public void unsetBowler() {
          _ps &= ~PlayerStatus.BOWLER;
        }

        public void unsetBattingDone() {
          _ps &= ~PlayerStatus.BATTING_DONE;
        }

        public boolean isBattingDone() {
          return (_ps & PlayerStatus.BATTING_DONE) > 0;
        }

        public void setBattingDone() {
          _ps |= PlayerStatus.BATTING_DONE;
        }

        public boolean isBold() {
          return (_ps & PlayerStatus.BOLD) > 0;
        }

        public void setBold() {
          _ps |= PlayerStatus.BOLD;
        }

        public void unsetBold() {
          _ps &= ~PlayerStatus.BOLD;
        }

        public void setCaptain() {
          _ps |= PlayerStatus.CAPTAIN;
        }

        public boolean isCaptain() {
          return (_ps & PlayerStatus.CAPTAIN) > 0;
        }

        public void unsetCaptain() {
          _ps &= ~PlayerStatus.CAPTAIN;
        }

        public void setDisconnected() {
          _ps |= PlayerStatus.DISCONNECTED;
        }

        public boolean isDisconnected() {
          return (_ps & PlayerStatus.DISCONNECTED) > 0;
        }

        public void unsetDisconnected() {
          _ps &= ~PlayerStatus.DISCONNECTED;
        }

        public void setBroke() {
          _ps |= PlayerStatus.BROKE;
        }

        public boolean isBroke() {
          return (_ps & PlayerStatus.BROKE) > 0;
        }

        public void unsetBroke() {
          _ps &= ~PlayerStatus.BROKE;
        }

        public void setWaiting() {
          _ps |= PlayerStatus.WAITING;
        }

        public boolean isWaiting() {
          return (_ps & PlayerStatus.WAITING) > 0;
        }

        public void unsetWaiting() {
          _ps &= ~PlayerStatus.WAITING;
        }

        public void setShill() {
          _ps |= PlayerStatus.SHILL;
        }

        public void resetStatus() {
          _ps &= ~PlayerStatus.M_RESET;
        }

        public boolean isShill() {
          return (_ps & PlayerStatus.SHILL) > 0;
        }

        public boolean isActive() {
          return (_ps & PlayerStatus.M_INACTIVE) == 0; // filter out sitin, new, fold, optout, all-in, removed, broke, sittingout,
        }


        ///////////////////////////LEVEL////////////////////////////////////////////////////////

        public void setMatchPlayer() {
          _ps |= PlayerStatus.MATCH_PLAYER;
        }

        public boolean isMatchPlayer() {
          return (_ps & PlayerStatus.MATCH_PLAYER) > 0;
        }

        public void unsetMatchPlayer() {
          _ps &= ~PlayerStatus.MATCH_PLAYER;
        }

        public void setTournyPlayer() {
          _ps |= PlayerStatus.TOURNY_PLAYER;
        }

        public boolean isTournyPlayer() {
          return (_ps & PlayerStatus.TOURNY_PLAYER) > 0;
        }

        public void unsetTournyPlayer() {
          _ps &= ~PlayerStatus.TOURNY_PLAYER;
        }

        public void setProfessionalPlayer() {
          _ps |= PlayerStatus.PROFESSIONAL_PLAYER;
        }

        public boolean isProfessionalPlayer() {
          return (_ps & PlayerStatus.PROFESSIONAL_PLAYER) > 0;
        }

        public void unsetProfessionalPlayer() {
          _ps &= ~PlayerStatus.PROFESSIONAL_PLAYER;
        }

        public void setTeamManager() {
          _ps |= PlayerStatus.TEAM_MANAGER;
        }

        public boolean isTeamManager() {
          return (_ps & PlayerStatus.TEAM_MANAGER) > 0;
        }

        public void unsetTeamManager() {
          _ps &= ~PlayerStatus.TEAM_MANAGER;
        }

        public void unsetAllStatus() {
          _ps = 0;
        }


        //////////////////////////////////////////////////////////////////////////////////////////////////////

        // Player State
        public int getState() {
          return _state;
        }

        public boolean isObserver() {
          return (_state & OBSERVER) > 0;
        }

        public void setObserver() {
          _state = OBSERVER;
        }

        public boolean isPlayer() {
          return (_state & PLAYER) > 0;
        }

        public void setPlayer() {
          _state = PLAYER;
        }

        final static int OBSERVER = 1;
        final static int PLAYER = 2;

        public String toString() {
          return player() + ", Gid=" + getGID() + ", pos=" + _pos + ", TeamName=" +
              _team_name + " State=" + (_state == 1 ? "observer" : "player") +
              ", PS=" + PlayerStatus.stringValue(_ps);
        }

}
