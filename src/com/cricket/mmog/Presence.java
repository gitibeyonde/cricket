package com.cricket.mmog;

import com.cricket.common.db.DBPlayer;
import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.mmog.cric.util.Team;

import java.util.logging.Logger;

public class Presence {
    // set the category for logging
    transient static Logger _cat = Logger.getLogger(Presence.class.getName());

    public static final int MAX_IDLE_GC = 10;
    public String _gid = "";
    public long _grid = -1;
    public String _tid = "";
    public int _idleGC;
    public long _idleTime;
    public long _nextMove;
    public int _nextMoveTO;


    //poker specific
    int _ps = 0;
    long _lastMove = Moves.NONE;
    volatile long[] _strength = new long[2];
    public long _start_wait = -1;
    public int _pos;
    public String _team_name;
    Player _gp;
    int _state;
    Team _team;
    int _win_points;
    int _curr_ball_count;
    int _curr_balls_played;
    int _curr_balls_bowled;
    int _curr_runs;
    protected DBPlayer _dbPlayer;


    public Presence(String gid, Player p) {
        _gid = gid;
        _gp = p;
        _dbPlayer = _gp._dbPlayer;
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
        _dbPlayer.setPoints(_dbPlayer.getPoints() - amt);
    }

    public void setWin(int p) {
        _win_points = p;
    }

    public void addWin() {
        _dbPlayer.setPoints(_dbPlayer.getPoints() + _win_points);
        _win_points = 0;
    }


    public int getWorth() {
        return _gp.points();
    }

    public void setGID(String gid) {
        _gid = gid;
    }

    public String getGID() {
        return _gid;
    }

    public long getGRID() {
        return _grid;
    }

    public void setGRID(long grid) {
        if (grid < 0) {
            return;
        }
        _grid = grid;
    }

    public String name() {
        return _gp._name;
    }

    public int gender() {
        return _gp._gender;
    }

    public int status() {
        return _ps;
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

    public Player player() {
        return _gp;
    }


    public void reset() {
        _dbPlayer.bowls += _curr_balls_bowled;
        _dbPlayer.runs += _curr_runs;
        _dbPlayer.balls += _curr_balls_played;
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
            _dbPlayer.sixes++;
        }
        else if (runs == 4) {
            _dbPlayer.fours++;
        }
    }


    public void addMP(MoveParams mp) {
        int agr = mp.getAgression();
        int react = mp.getReaction();
        int conf = mp.geConfidence();
        _cat.finest(mp._type + " MoveParams update = " + mp);
        switch (mp._type) {
            case 0:
                _dbPlayer.kagression = (_dbPlayer.kagression + agr) / 2;
                _dbPlayer.kagility = (_dbPlayer.kagility + react) / 2;
                _dbPlayer.kconfidence = (_dbPlayer.kconfidence + conf) / 2;
                _cat.finest(agr + ", " + _dbPlayer.kagression + ", " + react + ", " +
                        _dbPlayer.kagility);
                break;
            case 1:
                _dbPlayer.bagression = (_dbPlayer.bagression + agr) / 2;
                _dbPlayer.bagility = (_dbPlayer.bagility + react) / 2;
                _dbPlayer.bconfidence = (_dbPlayer.bconfidence + conf) / 2;
                _cat.finest(agr + ", " + _dbPlayer.bagression + ", " + react + ", " +
                        _dbPlayer.bagility);
                break;
            case 3:
                _dbPlayer.fagression = (_dbPlayer.fagression + agr) / 2;
                _dbPlayer.fagility = (_dbPlayer.fagility + react) / 2;
                _dbPlayer.fconfidence = (_dbPlayer.fconfidence + conf) / 2;
                _cat.finest(agr + ", " + _dbPlayer.fagression + ", " + react + ", " +
                        _dbPlayer.fagility);
                if (mp.getFieldAction() == mp.BLOCK) {
                    _dbPlayer.field++;
                }
                else if (mp.getFieldAction() == mp.CATCH) {
                    _dbPlayer.catches++;
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
        if (p != null && p.name().equals(_gp._name) && p._gid == _gid) {
            return true;
        }
        else {
            return false;
        }
    }

    public int getPlayerStatus() {
        return _ps;
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
        return player() + ", Gid=" + _gid + ", pos=" + _pos + ", TeamName=" +
                _team_name + " State=" + (_state == 1 ? "observer" : "player") +
                ", PS=" + PlayerStatus.stringValue(_ps) + " , pts=" + _gp.points();
    }

}

