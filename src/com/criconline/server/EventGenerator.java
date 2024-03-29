package com.criconline.server;

import com.criconline.lobby.LobbyModelsChangeListener;
import com.criconline.lobby.tourny.TournyMessagesListener;
import com.criconline.models.LobbyTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.criconline.actions.Action;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.Enumeration;
import com.criconline.ServerMessagesListener;
import com.cricket.common.message.TournyEvent;
import com.cricket.mmog.cric.util.ActionConstants;
import com.criconline.actions.ChatAction;
import com.cricket.common.message.ResponsePlayerShift;
import com.cricket.common.message.Command;
import java.io.IOException;

import com.criconline.ClientRoom;

import javax.swing.JFrame;

public abstract class EventGenerator implements ActionConstants {
  static Logger _cat = Logger.getLogger(EventGenerator.class.getName());

  protected Hashtable _action_registry;

  static String _password;
  static public String _name;
  static boolean _authenticated;
  String _poll="Cricket";
  static JFrame _owner;
  static ClientRoom _clientRoom;


  public EventGenerator() {
    _action_registry = new Hashtable();
  }


  void reset(String tid) {
    ((ActionFactory) _action_registry.get(tid)).reset();
  }

  public ActionFactory getActionFactory(String tid) {
    return ((ActionFactory) _action_registry.get(tid));
  }

  public void pumpGeneratedActions() {
    // go thru action registries
    Enumeration en = _action_registry.elements();
    while (en.hasMoreElements()) {
      ActionFactory af = (ActionFactory) en.nextElement();
      fireServerMessagesEvent(af.getGid(), af.fetchAction());
    }
  }

  /************************************************************************
   * ABSTRACT METHODS
   ***********************************************************************/

  public abstract void sendToServer(String tid, Action o) throws IOException ;
  public abstract void sendToServer(Command r) throws IOException ;
  public abstract void joinTable(String gid, int pos, String team, double value);
  public abstract void addWatchOnTable(String tid,ServerMessagesListener changesListener);

  /**************************************************************************
   * Various listeners and related methods
   **************************************************************************/

  protected Hashtable serverMessagesListener = new Hashtable();
  protected List lobbyInfoListener = new ArrayList();
  protected Hashtable tournyMessagesListener = new Hashtable();
  protected List lobbyModelChangeListener = new ArrayList();

  /**
   * Adds LobbyModelsChangeListener.
   */
  public final void addLobbyModelChangeListener(LobbyModelsChangeListener
                                                changesListener) {
    _cat.fine("---------" + changesListener.getClass().toString());
    synchronized (lobbyModelChangeListener) {
      lobbyModelChangeListener.add(changesListener);
    }
  }

  /**
   * Removes LobbyModelsChangeListener.
   */
  public final void removeLobbyModelChangeListener(LobbyModelsChangeListener
      changesListener) {
    synchronized (lobbyModelChangeListener) {
      lobbyModelChangeListener.remove(changesListener);
    }
  }

  /**
   * Notifies all listeners.
   */
  protected void fireLobbyModelChangeEvent(LobbyTableModel changes[]) {
    synchronized (lobbyModelChangeListener) {
      for (Iterator i = lobbyModelChangeListener.iterator(); i.hasNext(); ) {
        LobbyModelsChangeListener listner = (LobbyModelsChangeListener) i.next();
        listner.tableListUpdated(changes);
      }
    }
  }
  /**
     * Notifies all lobby chat listeners.
     */
    protected void fireLobbyModelChangeEvent(ChatAction message) {
      synchronized (lobbyModelChangeListener) {
        for (Iterator i = lobbyModelChangeListener.iterator(); i.hasNext(); ) {
          LobbyModelsChangeListener listner = (LobbyModelsChangeListener) i.next();
          listner.chatRcvd(message);
        }
      }
  }


  public final void addServerMessageListener(String tid,
                                             ServerMessagesListener changesListener) {
    synchronized (serverMessagesListener) {
      serverMessagesListener.put(tid, changesListener);
    }
  }
  

  public final void addTournyMessageListener(String tid,
                                             TournyMessagesListener changesListener) {
    synchronized (tournyMessagesListener) {
      tournyMessagesListener.put(tid, changesListener);
    }
  }


  protected void fireServerMessagesEvent(String tid, Object actions) {
    if (actions == null) {
      return;
    }
    ServerMessagesListener listner = (ServerMessagesListener)
                                     serverMessagesListener.get(tid);
    listner.serverMessageReceived(actions);
  }


  protected void fireTournyMessagesEvent(String tid, Object actions) {
    if (actions == null) {
      return;
    }
    TournyMessagesListener listner = (TournyMessagesListener)
                                     tournyMessagesListener.get(tid);
    listner.tournyMessageReceived(actions);
  }
  
  public void setPoll(String str){
    _cat.finest("Polling ----" + str);
    _poll = str;
    poll();
  }

  public abstract void poll();

}
