package com.criconline.lobby;

import java.util.*;
import java.util.logging.Logger;

import javax.swing.event.*;
import javax.swing.table.*;

import com.criconline.lobby.*;
import com.criconline.models.*;
import com.criconline.SharedConstants;
import com.cricket.mmog.cric.GameState;
import com.criconline.actions.ChatAction;
import com.cricket.common.db.DBPlayer;

/**
 * Abstract class of Lobby's tables. Are basic for any lobby room
 */
public abstract class LobbyListModel
    implements TableModel, LobbyModelsChangeListener {

  static Logger _cat = Logger.getLogger(Lobby.class.getName());

  public int _total_games, _total_players;

  public static final int SIMPLE_TABLE = 0;
  public static final int HOLDEM_TABLE = 1;
  public static final int TOURNAMENT_TABLE = 2;

  protected List rows = new ArrayList();
  private List listeners = new ArrayList();
  private String[] columnNames = null;

  public LobbyListModel(String[] columnNames) {
    this.columnNames = columnNames;
  }

  public int getID() {
    new Exception().printStackTrace();
    return (SIMPLE_TABLE);
  }

  public int getRowCount() {
    return rows.size();
  }

  public final int getColumnCount() {
    return columnNames.length;
  }

  public final String getColumnName(int columnIndex) {
    return columnNames[columnIndex];
  }

  public final Class getColumnClass(int columnIndex) {
    return String.class;
  }

  public final boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  public final void setValueAt(Object aValue, int rowIndex, int columnIndex) {
  }

  public final void addTableModelListener(TableModelListener l) {
    synchronized (listeners) {
      listeners.add(l);
    }
  }

  public final void removeTableModelListener(TableModelListener l) {
    synchronized (listeners) {
      listeners.remove(l);
    }
  }

  protected final void fireModelEvent(int row, int type) {
    TableModelEvent event
        = row >= 0 ? new TableModelEvent(this, row, row,
                                         TableModelEvent.ALL_COLUMNS,
                                         type) :
        new TableModelEvent(this);
    for (Iterator i = listeners.iterator(); i.hasNext(); ) {
      TableModelListener listener = (TableModelListener) i.next();
      listener.tableChanged(event);
    }
  }

  public void clear() {
    rows.clear();
    fireModelEvent( -1, TableModelEvent.DELETE);
  }

  public String getModelIdAt(int n) {
    if (n >= 0 && n < rows.size()) {
      return ( (LobbyTableModel) rows.get(n)).getName();
    }
    return "";
  }

  /**
   * This method called by LobbyServerProxy when application receives
   * changed cricket table states.
   */
  public void tableListUpdated(LobbyTableModel[] changes) {
    if (changes.length > 1){
      _total_games = changes[0]._total_games;
      _total_players = changes[0]._total_players;
    }
  }

  protected boolean isModelMustDelete(int state) {
    return state == GameState.OFFLINE;
  }

  public void adUpdated() {}

  public void chatRcvd(ChatAction o){}

  public void professionalPlayerList(DBPlayer message[]){}
//  ??
  public abstract Object getValueAt(int rowIndex, int columnIndex);

  /**
   * Can the Lobby Table be stored in this Swing Table Model ?
   */
  public abstract boolean isLobbyTableAcceptable(LobbyTableModel table);

  /**
   * Returns Lobby Table Model in specified row.
   */
  public LobbyTableModel getModelAtRow(int rowIndex) {
    return rowIndex < rows.size() ? (LobbyTableModel) rows.get(rowIndex) : null;
  }

  /**
   * Finds Lobby Table Model by its cricket table id.
   */
  public LobbyTableModel getModelByTableId(String tableId) {
    LobbyTableModel result = null;
    for (Iterator i = rows.iterator(); i.hasNext(); ) {
      LobbyTableModel lobbyTable = (LobbyTableModel) i.next();
      if (lobbyTable.getName().equals(tableId)) {
        result = lobbyTable;
        break;
      }
    }
    return result;
  }


  protected String formatMoney(double money) {
    return SharedConstants.moneyToString(money);
  }

}
