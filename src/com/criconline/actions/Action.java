package com.criconline.actions;

import java.util.logging.Logger;
import com.criconline.SharedConstants;
import com.criconline.resources.Bundle;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import com.cricket.mmog.cric.util.ActionConstants;
import java.util.Observable;

public abstract class Action extends Observable implements ActionConstants, java.io.Serializable {
  protected long[] guid;
  protected int id;
  protected int type;
  protected int pos;
  protected String team;

  static Logger _cat = Logger.getLogger(Action.class.getName());

  public Action(int id, int type, int pos, String team) {
    guid = new long[] {
           System.currentTimeMillis(),
           Math.round(Math.random() * Long.MAX_VALUE)};
    this.id = id;
    this.type = type;
    this.pos = pos;
    this.team = team;
  }

  public Action(int id, int type) {
    this(id, type, -1, "");
  }

//    public Action(int id) {
//        this(id, ACTION_TYPE_SIMPLE);
//    }

  public long[] getGuid() {
    return guid;
  }

  public void setGuid(long[] guid) {
    this.guid = guid;
  }

  public boolean equalsByGuid(long[] guid) {
    if (guid == null || this.guid == null) {
      return false;
    }
    for (int i = 0; i < guid.length; i++) {
      if (guid[i] != this.guid[i]) {
        return false;
      }
    }
    return true;
  }

  public static String guidToString(long[] guid) {
    StringBuffer s = new StringBuffer();
    for (int i = 0; i < guid.length; i++) {
      s.append(guid[i]);
    }
    return s.toString();
  }

  public int getId() {
    return id;
  }

  public int getType() {
    return type;
  }

  public int getPosition() {
    return pos;
  }

  public String getTeam() {
    return team;
  }

  //public String toMessage(String playerName) {
  //return toMessage(false, playerName);
  //}


  public static String actionToString(Action action) {
    return actionToString(action.id);
  }

  public static String actionToString(int id) {
    switch (id) {
      case MOVE_REQUEST:
        return "move request";
      case START_GAME:
        return "Start game";
      case PLAYER_SHIFT:
        return "Player shift";
      case END_GAME:
        return "End game";
      case UPDATE_GAME:
        return "Update State";
      case START:
        return "START";
      case BOWL:
        return "bowls";
      case FIELD:
        return "fields";
      case BAT:
        return "bats";
      case TOSS:
        return "toss";
      case HEAD:
        return "head";
      case TAIL:
        return "tail";
      case FIELDING:
        return "opts for fielding";
      case BATTING:
        return "opts for batting";
      case BATS_CHANGE:
        return "BATS CHANGE";
      case GAME_OVER:
        return "GAME OVER";

      case WAIT:
        return "WAIT";
      case END:
        return "END";




      case WIN:
        return "Win";
      case TOURNAMENT_WIN:
        return "Tournament win";
      case WAITER_CAN_JOIN:
        return "Waiter can join";
      case CHAT:
        return "Chat";
      case PLAYER_MESSAGE:
        return "Player message";
      case PLAYER_REGISTERED:
        return "Player registered";
      case PLAYER_UNREGISTERED:
        return "Player unregistered";
      case PLAYER_JOIN:
        return "Player join";
      case PLAYER_LEAVE:
        return "Player leave";
      case PLAYER_REJOIN:
        return "Player rejoin";
      case PLAYER_NEEDS_SITOUT:
        return "Player needs sitout";
      case ADD_TO_WAITERS:
        return "Add to waiters";
      case REMOVE_FROM_WAITERS:
        return "Remove from waiters";
      case IMMEDIATE_SHUTDOWN:
        return "Immediate shutdown";
      case SESSION_TIMEOUT:
        return "Session timeout";
      case GRACEFUL_SHUTDOWN:
        return "Graceful shutdown";
      case NO_MORE_WAITING:
        return "No more waiting";
      case STARTUP:
        return "Startup";
      case CHANGE_STATE:
        return "Change state";
      case MANUAL_IMMEDIATE_SHUTDOWN:
        return "Manual immediate shutdown";
      case MANUAL_GRACEFUL_SHUTDOWN:
        return "Manual graceful shutdown";
      case MANUAL_STARTUP:
        return "Manual startup";
      case MANUAL_CHANGE_STATE:
        return "Manual change state";
      case PLACE_OCCUPIED:
        return "Place occupied";
      case UNSUFFICIENT_FUND:
        return "Not enough money to play";
      case DECISION_TIMEOUT:
        return "Decision timeout";
      case SAME_REMOTE_HOST:
        return
            "There is another player at the table connected from this remote address";
      case THERE_IS_CLAIM:
        return "There is a waiter claim on the table !";
      case ALREADY_WAIT:
        return "Player already waits or sits at the table!";
      case TABLE_IS_OFFLINE:
        return "This table is turned off !";
      case CASHIER_UNAVAIBLE:
        return "Cashier function is not avaible for this table !";
      case UNKNOWN_ERROR:
        return "Unknown error !";
      case UNKNOWN_SESSION:
        return "Unknown session id !";
        // Agneya FIX 30
      case PLAYER_NEEDS_SITIN_TRUE:
        return "Player sitting in";
      case PLAYER_NEEDS_SITIN_FALSE:
        return "Player sitting out";
        //case ADD_BOT:
        //    return "Add bot";
        //case DEL_BOT:
        //    return "Del bot";
        //case REPLACE_BOT:
        //    return "Replace bot";
      case IS_ACCEPTING:
        return "Is accepting";
      default:
        return "Unknown id: " + id;
    }
  }

  public String toString() {
    return "pos=" + pos + ", team=" + team + ", action=" + actionToString(this);
  }

  public abstract void handleAction(ActionVisitor v);

}
