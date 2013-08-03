package com.criconline.lobby;

import java.util.logging.Logger;

import com.criconline.resources.*;
import com.criconline.*;
import com.criconline.models.*;
import javax.swing.event.TableModelEvent;
import com.cricket.common.message.ResponseGameList;
import com.cricket.mmog.cric.util.CricketConstants;

/**
 * Class for managing hold'em table in Lobby.
 */
public class CricketListModel extends LobbyListModel {

  static Logger _cat = Logger.getLogger(CricketListModel.class.getName());
  int _model_type=-1;
  final static int PRACTICE=2;
  final static int PROFESSIONAL=4;
  final static int MADNESS=8;

  protected static String[] columnNames = {
                                          Bundle.getBundle().getString(
                                              "game.id"),
                                          Bundle.getBundle().
                                          getString("game.name"),
                                          Bundle.getBundle().getString("type"),
                                          Bundle.getBundle().getString(
                                              "team.size"),
                                          Bundle.getBundle().getString("team.A"),
                                          Bundle.getBundle().getString("team.B"),
                                          Bundle.getBundle().getString("CanSit")
  };

  /** Constructor TableModel with column names
   *  Some rows are filled automaticly
   */
  public CricketListModel(int model_type) {
    super(columnNames);
    _model_type= model_type;
    _cat.fine("Getting cricket model this");
  }

  public CricketListModel(String[] columnNamesX) {
    super(columnNamesX);
  }

  /**
   * Can the Lobby Table be stored in this Swing Table Model ?
   */
  public boolean isLobbyTableAcceptable(LobbyTableModel table) {
    _cat.fine("Cricket model " + table);
    return table.getGameType().intVal() < 2048;
  }

  /** return value for JTable. Will is changed for Holdem, Tournament, ...
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    LobbyCricketModel one = (LobbyCricketModel) rows.get(rowIndex);
    if (one.getName() == null || "".equals(one.getName())) {
      return "";
    }
    switch (columnIndex) {
      case 0:
        return "  " + one.getName();
      case 1:
        return " " + one.getGameType().toString();
      case 2:
        return "     " + one.getGameType().teamSize() + " [" + one.getBallsPerPlayer() + "]";
      case 3:
        return "  " + one._teamAname + "(" + one._fielding_size + ")";
      case 4:
        return "  " + one._teamBname + "(" + one._batting_size + ")";
      case 5:
        if (one.getGameType().isMadness()){
          return "On Invite";
        }
        else {
          if (one.getGameType().teamSize() * 2 ==
              one._fielding_size + one._batting_size) {
            return "FULL";
          }
          else {
            return "JOIN";
          }
        }
    }
    return "Err";
  }

  /**
   * This method called by LobbyServerProxy when application receives
   * changed cricket table states.
   */
  public void tableListUpdated(LobbyTableModel changes[]) {
    super.tableListUpdated(changes);
    _cat.fine("Cricket list model updated ---");
    synchronized (this) {
      for (int i = 0; i < changes.length; i++) {
        LobbyTableModel lobbyModel = changes[i];
        _cat.fine(isLobbyTableAcceptable(lobbyModel) + ", " +
                   lobbyModel.getGameLimitType() + ", " +
                   CricketConstants.TOURNAMENT);
        if (isLobbyTableAcceptable(lobbyModel)) { //&& lobbyModel.getGameLimitType() != CricketConstants.TOURNAMENT) {
          if ((_model_type == MADNESS && lobbyModel.getGameType().isMadness()) ||(_model_type == PRACTICE && lobbyModel.getGameType().isPractise()) ||
              (_model_type == PROFESSIONAL &&
               lobbyModel.getGameType().isMatch())) {
        	  _cat.fine("Cricket Updated = " + lobbyModel.getName() + ", " + lobbyModel.getGameLimitType());
        	  int index = rows.indexOf(lobbyModel);
        	  boolean forDel = false; //isModelMustDelete(lobbyModel.getState());
	            if (index >= 0) {
	              if (!forDel) {
	                rows.set(index, lobbyModel);
	                fireModelEvent(index, TableModelEvent.UPDATE);
	              }
	              else {
	                rows.remove(index);
	                fireModelEvent(index, TableModelEvent.DELETE);
	              }
	            }
	            else if (!forDel) {
	              //if (changes[i].getState() == LobbyTableModel.ONLINE) {
	              if (rows.add(lobbyModel)) {
	                fireModelEvent(rows.size() - 1, TableModelEvent.INSERT);
	              }
	              //}
	            }
	          }
        }
      }
    }
  }

  public LobbyCricketModel getOneRow(int rowIndex) {
    return ((LobbyCricketModel) rows.get(rowIndex));
  }

  public int getID() {
    new Exception().printStackTrace();
    return (HOLDEM_TABLE);
  }
}
