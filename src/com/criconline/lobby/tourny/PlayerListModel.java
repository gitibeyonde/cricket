package com.criconline.lobby.tourny;

import java.util.logging.Logger;

import com.criconline.resources.Bundle;
import javax.swing.event.TableModelEvent;
import com.criconline.models.*;
import com.cricket.common.message.ResponseGameList;
import com.criconline.lobby.LobbyListModel;
import com.criconline.models.tourny.TournyPlayerListModel;

/**
 * Class for managing hold'em table in Lobby.
 */
public class PlayerListModel
    extends LobbyListModel {

  static Logger _cat = Logger.getLogger(PlayerListModel.class.getName());

  protected static String[] columnNames = {
      Bundle.getBundle().getString("game.name"),
      Bundle.getBundle().getString("game.points")
  };

  /**
   * Constructor TableModel with column names
   * Some rows are filled automaticly
   */
  public PlayerListModel() {
    super(columnNames);
  }

  public PlayerListModel(String[] columnNamesX) {
    super(columnNamesX);
  }

  /**
   * Can the Lobby Table be stored in this Swing Table Model ?
   */
  public boolean isLobbyTableAcceptable(LobbyTableModel table) {
      return (table instanceof TournyPlayerListModel) ;
  }

  /**
   * return value for JTable. Will is changed for Holdem, Tournament, ...
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    TournyPlayerListModel one = (TournyPlayerListModel) rows.get(rowIndex);
    if (one.getName() == null || "".equals(one.getName())) {
      return "";
    }
    switch (columnIndex) {
      case 0:
        return "  " + one.getName();
      case 1:
        return "  " + one.getPoints();
    }
    return "Err";
  }

  /**
   * This method called by LobbyServerProxy when application receives
   * changed cricket table states.
   */
  public void tableListUpdated(TournyPlayerListModel changes[]) {
    _cat.fine("Tourny ===");
    synchronized (this) {
      for (int i = 0; i < changes.length; i++) {
        if (changes[i] instanceof TournyPlayerListModel) {
          _cat.fine("TOURNAMENT player list Updated = " + changes[i].getName());
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

  public TournyPlayerListModel getOneRow(int rowIndex) {
    return ( (TournyPlayerListModel) rows.get(rowIndex));
  }

  /* (non-Javadoc)
   * @see com.criconline.client.LobbyListModel#getID()
   */
  public int getID() {
    return (TOURNAMENT_TABLE);
  }

}
