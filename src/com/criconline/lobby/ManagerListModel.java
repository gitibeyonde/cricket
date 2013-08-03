package com.criconline.lobby;

import java.util.logging.Logger;

import com.criconline.resources.Bundle;
import javax.swing.event.TableModelEvent;
import com.criconline.models.*;
import com.cricket.common.message.ResponseGameList;
import com.cricket.common.db.DBPlayer;

/**
 * Class for managing hold'em table in Lobby.
 */
public class ManagerListModel
    extends LobbyListModel {

  static Logger _cat = Logger.getLogger(ManagerListModel.class.getName());

  protected static String[] columnNames = {
                                          "Name",
                                          "Wins",
                                          "Ranking",
                                          "   Team    ",
                                          "Next Match"
  };

  /**
   * Constructor TableModel with column names
   * Some rows are filled automaticly
   */
  public ManagerListModel() {
    super(columnNames);
  }

  public ManagerListModel(String[] columnNamesX) {
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
    DBPlayer one = (DBPlayer) rows.get(rowIndex);
    if (one.getDispName() == null || "".equals(one.getDispName())) {
      return "";
    }
    switch (columnIndex) {
      case 0:
        return "  " + one.getDispName();
      case 1:
        return "  " + one.runs;
      case 2:
        return "  " + one.kagility;
      case 3:
        return "  " + one.bagility;
    }
    return "Err";
  }

  /**
   * This method called by LobbyServerProxy when application receives
   * changed cricket table states.
   */
  public void tableListUpdated(LobbyTableModel changes[]) {
  }

  public void professionalPlayerList(DBPlayer changes[]){
     _cat.fine("Professional Player list ===");
     synchronized (this) {
       for (int i = 0; i < changes.length; i++) {
         if (changes[i].getRank() != 3) break;
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


  public DBPlayer getOneRow(int rowIndex) {
    return ( (DBPlayer) rows.get(rowIndex));
  }

  /* (non-Javadoc)
   * @see com.criconline.client.LobbyListModel#getID()
   */
  public int getID() {
    return (TOURNAMENT_TABLE);
  }
}
