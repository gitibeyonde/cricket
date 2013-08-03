package com.criconline.lobby.tourny;

import java.util.logging.Logger;

import com.criconline.resources.Bundle;
import javax.swing.event.TableModelEvent;
import com.criconline.models.*;
import com.cricket.common.message.ResponseGameList;
import com.criconline.lobby.LobbyListModel;

/**
 * Class for managing hold'em table in Lobby.
 */
public class ManagerTournyListModel
    extends LobbyListModel {

  static Logger _cat = Logger.getLogger(TournamentListModel.class.getName());

  protected static String[] columnNames = {
      Bundle.getBundle().getString("game.name"),
      Bundle.getBundle().getString("game.date"),
      Bundle.getBundle().getString("game.type"),
      Bundle.getBundle().getString("game.fees"),
      Bundle.getBundle().getString("game.state"),
      Bundle.getBundle().getString("game.level")
  };

  /**
   * Constructor TableModel with column names
   * Some rows are filled automaticly
   */
  public ManagerTournyListModel() {
    super(columnNames);
  }

  public ManagerTournyListModel(String[] columnNamesX) {
    super(columnNamesX);
  }

  /**
   * Can the Lobby Table be stored in this Swing Table Model ?
   */
  public boolean isLobbyTableAcceptable(LobbyTableModel table) {
// DEBUG !
    return (table instanceof LobbyCricketModel) &&
        table.isTournamentGame();
  }

  /**
   * return value for JTable. Will is changed for Holdem, Tournament, ...
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    LobbyTournyModel one = (LobbyTournyModel) rows.get(rowIndex);
    if (one.getName() == null || "".equals(one.getName())) {
      return "";
    }
    switch (columnIndex) {
      case 0:
        return "  " + one.getName();
      case 1:
        return "  " + one.getDate();
      case 2:
        return " TOURNY";
      case 3:
        return "  " + Integer.toString(one.getPlayerCount()) + "/" +
            Integer.toString(one.getTeamSize());
      case 4:
        return " " + one.getStateString();
      case 5:
        return " " + one.getLevel();
    }
    return "Err";
  }

  /**
   * This method called by LobbyServerProxy when application receives
   * changed cricket table states.
   */
  public void tableListUpdated(LobbyTableModel changes[]) {
    _cat.fine("Tourny ===");
    synchronized (this) {
      for (int i = 0; i < changes.length; i++) {
        if (changes[i] instanceof LobbyTournyModel) {
          _cat.fine("TOURNAMENT Updated = " + changes[i].getName()  + ", " + changes[i].getGameLimitType());
          int index = rows.indexOf(changes[i]);
          if (index >= 0) {
            rows.set(index, changes[i]);
            fireModelEvent(index, TableModelEvent.UPDATE);
          }
          else {
            if (rows.add(changes[i])) {
              fireModelEvent(rows.size() - 1, TableModelEvent.INSERT);
            }
          }
        }
      }
    }
  }

  public String getId(int row_number) {
    if ( (rows.size() <= row_number) || (row_number < 0)) {
      return "";
    }
    return ( (LobbyTableModel) rows.get(row_number)).getName();
  }

  public LobbyCricketModel getOneRow(int rowIndex) {
    return ( (LobbyCricketModel) rows.get(rowIndex));
  }

  /* (non-Javadoc)
   * @see com.criconline.client.LobbyListModel#getID()
   */
  public int getID() {
    return (TOURNAMENT_TABLE);
  }
}
