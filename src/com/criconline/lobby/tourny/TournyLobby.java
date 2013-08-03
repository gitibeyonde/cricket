package com.criconline.lobby.tourny;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.criconline.util.*;
import com.criconline.exceptions.*;
import com.criconline.models.*;
import com.criconline.pitch.PitchSkin;
import com.criconline.server.ServerProxy;
import com.criconline.console.ClientCricketController;
import com.criconline.pitch.PitchColorTheme;
import javax.swing.table.DefaultTableModel;
import com.criconline.lobby.UsersComponentsFactory;
import com.criconline.lobby.UserTableStringCellRenderer;
import com.criconline.ClientConfig;
import com.criconline.Utils;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Enumeration;
import javax.swing.table.TableColumn;
import javax.swing.border.EmptyBorder;
import com.criconline.lobby.TableSorter;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import com.criconline.ClientRoom;
import java.util.Hashtable;
import java.util.logging.Logger;

import com.cricket.common.message.GameEvent;

public class TournyLobby extends JFrame implements ListSelectionListener,
    FocusListener {
  static Logger _cat = Logger.getLogger(TournyLobby.class.getName());

  public ServerProxy _serverProxy;
  private TournyController _tournyController = null;
  private LobbyTournyModel _tournyTableModel = null;
  public JFrame _lobbyFrame;
  public String _tid;
  protected DefaultTableModel _pitchListModel, _playerListModel, _regListModel;
  private Hashtable _tes;
  protected JComponent _pitchList, _playerList, _regList;
  protected String _focusedTable;
  public JButton _regButton, _myPitchButton, _openPitchButton, _refreshButton;


  protected static Dimension screenSize;
  protected static Dimension frameSize;
  protected static Point framePos;

  static {
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frameSize = new Dimension(795 + 1, 560 + 14 + 1);
    framePos = new Point((screenSize.width - frameSize.width) / 2,
                         (screenSize.height - frameSize.height) / 2);
  }

  public TournyLobby(ServerProxy lobbyServer, LobbyTournyModel tournyTableModel,
                     JFrame owner) {
    super();
    super.setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
    updateLookAndFeel();
    _tournyTableModel = tournyTableModel;
    _tid = tournyTableModel.getName();
    _serverProxy = lobbyServer;
    _lobbyFrame = owner;
    _tes = new Hashtable();
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);


    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        tryCloseRoom(); // safe exit
        dispose();
        _cat.fine("windowClosing(WindowEvent e)");
      }
    });

    _pitchListModel = new DefaultTableModel(null, new String[] {"Id", "Name",
                                            "Level"});
    _playerListModel = new DefaultTableModel(null, new String[] {"S No", "Name",
                                             "Points"});
    _regListModel = new DefaultTableModel(null, new String[] {"S No", "Name",
                                          "Points"});

    //pitch list jtable
    _pitchList = createJTable(_pitchListModel, 0);
    _playerList = createJTable(_playerListModel, 1);
    _regList = createJTable(_regListModel, 2);

    // --- Room Skin
    PitchSkin skin = new PitchSkin();
    _tournyController = new TournyController(this, tournyTableModel,
                                             lobbyServer, skin, _tid);
    _serverProxy.addWatchOnTourny(_tid, _tournyController);


    // --- Add exception catch !!!
    getContentPane().setLayout(new BorderLayout(0, 0));
    getContentPane().add(_tournyController, BorderLayout.CENTER);

    _tournyController.add(_pitchList);
    _pitchList.setOpaque(false);
    _pitchList.setBounds(10, 250, 287, 260);
    _pitchListModel.addRow(new String[] {"Id", "Name", "Level"});

    _tournyController.add(_playerList);
    _playerList.setBounds(314, 250, 262, 260);
    _playerListModel.addRow(new String[] {"S No", "Name", "Points"});

    _tournyController.add(_regList);
    _regList.setBounds(590, 150, 180, 300);
    _regListModel.addRow(new String[] {"S No", "Name", "Points"});


    ActionListener buttonListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JButton j = (JButton) e.getSource();
        _cat.fine(j.getName());
        if (j.getName().equals(_openPitchButton.getName())) {
          // open pitch
        }
        else if (j.getName().equals(_regButton.getName())) {
          doTournyRegistration();
        }
        else if (j.getName().equals(_myPitchButton.getName())) {
          myPitch();
        }
        else if (j.getName().equals(_refreshButton.getName())) {
          refresh();
        }
      }
    };

    _openPitchButton = UsersComponentsFactory.createJBSitTable(buttonListener);
    _tournyController.add(_openPitchButton);
    _openPitchButton.setEnabled(false);
    _openPitchButton.setName("open_pitch");

    _regButton = UsersComponentsFactory.createTournyRegButton(buttonListener);
    _tournyController.add(_regButton);
    _regButton.setEnabled(false);
    _regButton.setName("register");

    _myPitchButton = UsersComponentsFactory.createMyPitchButton(buttonListener);
    _tournyController.add(_myPitchButton);
    _myPitchButton.setEnabled(false);
    _myPitchButton.setName("my_pitch");

    _refreshButton = UsersComponentsFactory.createRefreshButton(buttonListener);
    _tournyController.add(_refreshButton);
    _refreshButton.setEnabled(true);
    _refreshButton.setName("refresh");


    setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);

    setResizable(false);
    setVisible(true);
    _cat.fine("TOURNY LOBBY CREATED");

    repaint();
  }

  public LobbyTableModel getLobbyTableModel() {
    return _tournyTableModel;
  }


  public JComponent createJTable(DefaultTableModel tableModel, int index) {
    _cat.fine("createJTable : Creating tables ");
    JTable jtable = new JTable(tableModel);
    switch (index) {
      case 0:
        jtable.setName("Games");
        break;
      case 1:
        jtable.setName("Game_Players");
        break;
      case 2:
        jtable.setName("Player_Listing");
        break;
      default:
        throw new IllegalStateException("Bad table");
    }

    jtable.getSelectionModel().addListSelectionListener(this);
    jtable.addFocusListener(this);
    jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jtable.setRowSelectionAllowed(true);
    jtable.setSelectionBackground(Utils.lobbyCellBgrnd);
    jtable.setColumnSelectionAllowed(false);
    jtable.setCellSelectionEnabled(false);
    jtable.setBackground(Utils.lobbyCellBgrnd);
    jtable.getTableHeader().setResizingAllowed(false);
    jtable.setDragEnabled(false);
    jtable.setOpaque(false);


    JScrollPane scrollPane = new JScrollPane(JScrollPane.
                                             VERTICAL_SCROLLBAR_ALWAYS,
                                             JScrollPane.
                                             HORIZONTAL_SCROLLBAR_NEVER);
    //		scrollPane.getVerticalScrollBar().setUI(Utils.getLobbyScrollBarUI());

    scrollPane.setViewportView(jtable);
    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

    //    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    // add the infoarea as listener for details

    return jtable;
  }


  // ListSelectionListener Interface
  public void valueChanged(ListSelectionEvent e) {
    _cat.fine("Foucs owner " + _focusedTable);
    if (e.getSource() instanceof DefaultListSelectionModel) {
      DefaultListSelectionModel lsm = (DefaultListSelectionModel) e.getSource();
      int index = lsm.getLeadSelectionIndex();
      _cat.fine("Selection=" + lsm.getLeadSelectionIndex());
      if (_focusedTable.equals("Games")) {
        _cat.fine("Game list selected " + _pitchListModel.getValueAt(index, 0));
         GameEvent ge = (GameEvent) _tes.get((String)_pitchListModel.getValueAt(index, 0));
         if (ge != null){
           LobbyCricketModel lobbyModel = new LobbyCricketModel(ge);
           // create a action registry entry
           _serverProxy.createActionFactory(ge.getGameId(), -1, "");
           ClientRoom cr = new ClientRoom(_serverProxy, lobbyModel, _lobbyFrame);
         }
         else {
           throw new IllegalStateException("missing ge " + _pitchListModel.getValueAt(index, 0));
         }
      }
      else if (_focusedTable.equals("Game_Players")) {

      }
      else if (_focusedTable.equals("Player_Listing")) {
        _cat.fine("Player list selected ");
      }

    }
  }


  public void focusGained(FocusEvent e) {
    if (e.getSource() instanceof JTable) {
      JTable st = (JTable) e.getSource();
      _focusedTable = st.getName();
    }
  }

  public void focusLost(FocusEvent e) {
  }

  public void doTournyRegistration() {
    try {
      _serverProxy.tournyRegister(_tid);
    }
    catch (Exception e) {
      _cat.severe("Unable to regiseter " +  e.getMessage());
    }
    _cat.fine("Register " + _tid);
  }


  public void myPitch() {
    try {
      _serverProxy.tournyMyPitch(_tid);
    }
    catch (Exception e) {
      _cat.severe("Unable to goto my pitch " + e.getMessage());
    }

    _cat.fine("My Pitch " + _tid);
  }

  public void refresh() {
    try {
      _serverProxy.tournyDetails(_tid);
    }
    catch (Exception e) {
      _cat.severe("Unable to get tourny details " + e.getMessage());
    }

    _cat.fine("My Pitch " + _tid);
  }

  public void addPitch(String id, String name, String level, GameEvent te) {
    _pitchListModel.addRow(new String[] {id, name, level});
    _tes.put(id, te);
  }

  public void resetPitch() {
    for (int i = _pitchListModel.getRowCount() - 1; i > 0; i--) {
      _pitchListModel.removeRow(i);
    }
  }

  public void addPlayer(String id, String name, String points) {
    _playerListModel.addRow(new String[] {id, name, points});
  }

  public void resetPlayer() {
    for (int i = _playerListModel.getRowCount() - 1; i > 0; i--) {
      _playerListModel.removeRow(i);
    }
  }

  public void addRegPlayer(String id, String name, String points) {
    _regListModel.addRow(new String[] {id, name, points});
  }

  public void resetRegPlayer() {
    for (int i = _regListModel.getRowCount() - 1; i > 0; i--) {
      _regListModel.removeRow(i);
    }
  }

  public void tryCloseRoom() {
    _cat.info("closeRoom");
  }


  /**
   * Update the system color theme.
   */
  protected void updateLookAndFeel() {
    try {
      javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new
          PitchColorTheme());
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    }
    catch (Exception ex) {
      _cat.severe("Failed loading L&F: (ClientRoom)" + ex.getMessage());
    }
  }


}
