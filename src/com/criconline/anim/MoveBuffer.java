package com.criconline.anim;

import java.util.logging.Logger;
import com.criconline.actions.MoveRequestAction;
import java.util.Hashtable;
import com.criconline.actions.LastMoveAction;
import com.criconline.actions.MoveAction;
import java.util.TimerTask;
import java.util.Observable;

public class MoveBuffer implements AnimationConstants {
  static Logger _cat = Logger.getLogger(MoveBuffer.class.getName());
  private static Object _dummy = new Object();
  private static MoveBuffer _mb = null;
  private static Hashtable _mb_registry = null;


  AnimationManager _am;
  String _gid;
  private Hashtable _nm_register;
  private Hashtable _lm_register;
  private Hashtable _mm_register;

  static {
      _mb_registry = new Hashtable();
    }

    public static MoveBuffer instance(String tid) {
      if (_mb_registry.get(tid) == null) {
        synchronized (_dummy) {
          if (_mb_registry.get(tid + "") == null) {
            _mb_registry.put(tid + "", new MoveBuffer(tid));
          }
        }
      }
      return (MoveBuffer) _mb_registry.get(tid + "");
  }

  private MoveBuffer(String tid) {
    _nm_register = new Hashtable();
    _lm_register = new Hashtable();
    _mm_register = new Hashtable();
    _gid = tid;
  }

  public void setAnimationManager(AnimationManager am) {
    _am = am;
  }

  public void setNextMove(boolean mine, int type, MoveRequestAction nm) {
    _cat.finest(type + " NEXT MOVE " + nm );
    _nm_register.put(type + "", nm);
    if (!mine)return;
    // if this player is an observer do nothing on next moves
    if (_am.isObserver() || _am._pm.isWaiting()) {
        switch (type) {
          case TOSS:
            _am.update(nm, Gse.TOSS);
            break;
          default:
            _cat.warning("Unknown type");
        }
      return;
    }
    switch (type) {
      case BOWL:
        if (_am._pm.isBats()) {
          _am.update(nm, Gse.K_NM_BOWL);
        }
        else if (_am._pm.isBowl()) {
          _am.update(nm, Gse.B_NM_BOWL);
        }
        else if (_am._pm.isFielder()) {
          _am.update(nm, Gse.F_NM_BOWL);
        }
        else {
          throw new IllegalStateException(_am._pm.toString());
        }
        break;
      case BATS:
        if (_am._pm.isBats()) {
          _am.update(nm, Gse.K_NM_BAT);
        }
        else if (_am._pm.isBowl()) {
          _am.update(nm, Gse.B_NM_BAT);
        }
        else if (_am._pm.isFielder()) {
          _am.update(nm, Gse.F_NM_BAT);
        }
        else {
          throw new IllegalStateException(_am._pm.toString());
        }
        break;
      case FIELD:
        if (_am._pm.isBats()) {
          _am.update(nm, Gse.K_NM_FIELD);
        }
        else if (_am._pm.isBowl()) {
          _am.update(nm, Gse.B_NM_FIELD);
        }
        else if (_am._pm.isFielder()) {
          _am.update(nm, Gse.F_NM_FIELD);
        }
        else {
          throw new IllegalStateException(_am._pm.toString());
        }
        break;
      default:
        throw new IllegalStateException(type + "");
    }
  }

  public void setLastMove(int type, LastMoveAction lm) {
    _cat.finest(_gid + " LAST MOVE " + lm);
   _cat.finest(type + " obs=" + _am.isObserver());
    _lm_register.put(type + "", lm);
    if (_am.isObserver() || _am._pm.isWaiting()){
      switch (type) {
        case TOSS:
          _am.update(lm, Gse.TOSS);
          break;
        case BOWL:
            _am.update(lm, Gse.BOWLER_RUN);
          break;
        case BATS:
          // do nothing the animation manager will poll for it;
          break;
        case FIELD:
          // fielding should start when ball is close to the fielder
          _am.update(lm, Gse.FIELDING_RCVD);
          break;
        case HEAD:
          _am.update(lm, Gse.HEAD);
          break;
        case TAIL:
          _am.update(lm, Gse.TAIL);
          break;
        case FIELDING:
          _am.update(lm, Gse.FIELDING);
          break;
        case BATTING:
          _am.update(lm, Gse.BATTING);
          break;

        default:
          _cat.finest("Unhandled type " + type + "");
      }
    }
    else {
      switch (type) {
        case BOWL:
          if (_am._pm.isBats()) {
            _am.update(lm, Gse.K_LM_BOWL);
          }
          else if (_am._pm.isBowl()) {
            _am.update(lm, Gse.B_LM_BOWL);
          }
          else if (_am._pm.isFielder()) {
            _am.update(lm, Gse.F_LM_BOWL);
          }
          else {
            throw new IllegalStateException(_am._pm.toString());
          }
          break;
        case BATS:
          if (_am._pm.isBats()) {
            _am.update(lm, Gse.K_LM_BAT);
          }
          else if (_am._pm.isBowl()) {
            _am.update(lm, Gse.B_LM_BAT);
          }
          else if (_am._pm.isFielder()) {
            _am.update(lm, Gse.F_LM_BAT);
          }
          else {
            throw new IllegalStateException(_am._pm.toString());
          }
          break;
        case FIELD:
          if (_am._pm.isBats()) {
            _am.update(lm, Gse.K_LM_FIELD);
          }
          else if (_am._pm.isBowl()) {
            _am.update(lm, Gse.B_LM_FIELD);
          }
          else if (_am._pm.isFielder()) {
            _am.update(lm, Gse.F_LM_FIELD);
          }
          else {
            throw new IllegalStateException(_am._pm.toString());
          }
          break;
        default:
          _cat.finest("Unhandled type " + type + "");
      }
    }
  }


  public void actOnLastMove(int type, LastMoveAction lm) {
    _cat.finest(type + " ACTING ON MOVE MADE " + lm);
    _lm_register.put(type + "", lm);
    switch (type) {
      case TOSS:
        _am.update(lm, Gse.TOSS);
        break;
      case HEAD:
        _am.update(lm, Gse.HEAD);
        break;
      case TAIL:
        _am.update(lm, Gse.TAIL);
        break;
      case BOWL:
          _am.update(lm, Gse.B_BOWL);
        break;
      case BATS:
          _am.update(lm, Gse.K_BAT);
        break;
      case FIELD:
        _am.update(lm, Gse.F_FIELD);
        break;
      case FIELDING:
          _am.update(lm, Gse.FIELDING);
        break;
      case BATTING:
        _am.update(lm, Gse.BATTING);
        break;

      default:
        throw new IllegalStateException(type + "");
    }
  }

  public MoveRequestAction getNextMove(int type) {
    return (MoveRequestAction) _nm_register.get(type + "");
  }

  public MoveRequestAction removeNextMove(int type) {
    return (MoveRequestAction) _nm_register.remove(type + "");
  }

  public LastMoveAction getLastMove(int type) {
    return (LastMoveAction) _lm_register.get(type + "");
  }

  public LastMoveAction removeLastMove(int type) {
    return (LastMoveAction) _lm_register.remove(type + "");
  }

  public void setMoveMade(int type, MoveAction nm) {
    _mm_register.put(type + "", nm);
  }

  public MoveAction getMoveMade(int type) {
    new Exception(type+"").printStackTrace();
    return (MoveAction) _mm_register.remove(type + "");
  }
}
