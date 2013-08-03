package com.criconline.lobby;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.util.HashMap;
import java.util.logging.Logger;

import com.cricket.common.message.ResponseGameEvent;
import com.cricket.common.message.GameEvent;
import com.cricket.mmog.PlayerStatus;
import com.cricket.mmog.cric.util.ActionConstants;
import com.cricket.common.message.ResponseGameList;

import com.criconline.*;
import com.criconline.models.*;
import com.criconline.actions.*;
import com.criconline.resources.Bundle;
import com.criconline.util.*;
//import com.criconline.shared.SharedConstants;
//import com.criconline.shared.client.ClientSettings;
import com.criconline.exceptions.AuthenticationException;
import com.criconline.server.ServerProxy;
import com.criconline.pitch.PitchColorTheme;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import com.criconline.console.PractisePitch;
import java.io.IOException;
import com.agneya.util.Base64;
import com.criconline.lobby.tourny.TournyLobby;
import com.cricket.common.db.DBPlayer;
import com.criconline.lobby.tourny.TournamentListModel;


//import com.criconline.adminshared.util.MessagePopup;

public class LobbyUserImp implements LobbyFrameInterface, ListSelectionListener,
    LobbyModelsChangeListener, ActionListener, MouseListener, FocusListener,
    LobbyInfoListener {

  static Logger _cat = Logger.getLogger(LobbyUserImp.class.getName());


////	IDs for automatic open if frame is open to open
  private ResponseGameList _table_list;


  // menu
  protected JMenuBar menuBar;


//	private JMenu jmSettings;

  protected JCheckBoxMenuItem jmiAutoLogin;


//@@@@@@@@@@@@@@@@@@   SOUND ON & OFF   @@@@@@@@@@@@@@@@@@@@@@@@@@
  protected JCheckBoxMenuItem jmiSound;


//---
  protected JTabbedPane tabbedPane = null;
  protected InfoJLabel infoTextArea;
  protected JLabel pingTextArea;
  protected JTextArea adTextArea;


  // CHAT PANE
  public JTextArea _chat_text;
  JTextField _chat_field;

//--- tables
  protected LobbyListModel cricket_model, matches_model;
  protected LobbyListModel tournament_june2008;
//protected LobbyListModel tournament_model;
  //protected LobbyListModel pp_model;
 // protected LobbyListModel manager_model;
 // protected LobbyListModel manager_tourny_model;
  protected TableSorter holdem_sorter;
  protected JTable holdemTable = null;
  protected JTable matchTable = null;
  protected JTable tournament2008 = null;
 // protected JTable ppTable = null;
 // protected JTable managerTable = null;
 // protected JTable managerTournyTable = null;


// settings
  protected LoginSettings loginSettings = new LoginSettings();


// proxy_s
  protected ServerProxy _serverProxy;
  protected LobbyTableModel _latest_changes[];
  protected String selected_tid = "";


// bundle
  protected ResourceBundle bundle;


// components
  protected JToggleButton jbWait;
  protected JButton jbSitTable, jbCashier, jbNetPractice;
  protected Vector components = new Vector();
  ;


// theme
  protected MetalTheme metalTheme;


// background
  protected ImageIcon background;


  // Agneya FIX 16
  protected ImageIcon pwIcon;


//--- Vector with all opened windows
  protected static Vector vClientRooms = new Vector();


//	exit listener
  protected WindowListener windowExitListener;

  protected JFrame frame;

  final ImageIcon tableBackgroundImage = Utils.getIcon(ClientConfig.IMG_TABLE_LIST_BG);

  public LobbyUserImp(String ip, int port) throws Exception {
    prepare();
    init_player(ip, port);

    this.bundle = Bundle.getBundle();
    this.metalTheme = createLookAndFeel();
    this.menuBar = createMenuBar();
    this.background = Utils.getIcon(ClientConfig.IMG_LOBBY_BACKGROUND);

    windowExitListener = createExitListener();
// --- create components
    //initJBWait();
    initJBSitTable();
    //initJBNetPractice();
    //initJBCashier();
    createMainPanelWithContents();
    // --- create components
  }


  // Executed when the players closes the window
  private WindowListener createExitListener() {
    WindowListener windowExitListener = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        _cat.fine("====================== Lobby frame close");
        if (jmiAutoLogin != null) {
          loginSettings.setAutoLogin(jmiAutoLogin.isSelected());
        }
        loginSettings.saveSettings();
        lobbyExit();
      }

      public void windowActivated(WindowEvent arg0) {
        if (MessageFactory.dialog != null) {
          MessageFactory.dialog.toFront();
        }
      }
    };
    return windowExitListener;
  }

  private MetalTheme createLookAndFeel() {
    MetalTheme metalTheme = new PitchColorTheme();
    setLookAndFeel(metalTheme);
    return metalTheme;
  }

  private void prepare() {
    UIManager.put("ScrollBar.width", new Integer(12));
    this.bundle = Bundle.getBundle();
  }

  public void init(JFrame frame) {
    this.frame = frame;
    // Agneya FIX 16
    pwIcon = Utils.getIcon(ClientConfig.PW_ICON);
    frame.setIconImage(pwIcon.getImage());
    createLoginFrameAsNeeded();
  }

  public ImageIcon getBackground() {
    return background;
  }

  public MetalTheme getLookAndFeel() {
    return metalTheme;
  }

  public JMenuBar getMenu() {
    return menuBar;
  }

  public WindowListener getWindowCloseEvent() {
    return windowExitListener;
  }

  public Object[] getComponents() {
    return components.toArray();
  }

  private JMenu jmMyAccount, jmCashier, jmEvents, jmTellFrnd, jmHelp,
      jmSettings;
  protected JMenuBar createMenuBar() {
    JMenuBar menuBar;
    JMenuItem menuItem;

    menuBar = new JMenuBar();

    jmMyAccount = createMyAccountMenu();
    jmCashier = createCashierMenu(true);
    jmEvents = createEventsMenu();
    jmTellFrnd = createTellFrndMenu();
    jmSettings = createSettingsMenu(true);
    jmSettings.setEnabled(false);
    jmHelp = createHelpMenu();

    menuBar.add(jmMyAccount);
    menuBar.add(jmCashier);
    menuBar.add(jmEvents);
    menuBar.add(jmSettings);
    menuBar.add(Box.createHorizontalGlue());
    menuBar.add(jmTellFrnd);
    menuBar.add(jmHelp);

    return menuBar;
  }

  private JMenu createMyAccountMenu() {
    JMenu menuMA;
    JMenuItem menuItem;
    menuMA = new JMenu(bundle.getString("myaccount"));
    menuMA.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "myaccount.desc"));

    menuItem = new JMenuItem(bundle.getString("profile"));
    menuItem.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "profile.desc"));
    menuItem.addActionListener(this);

    menuMA.add(menuItem);

    menuItem = new JMenuItem(bundle.getString("validate"));
    menuItem.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "validate.desc"));
    menuItem.getAccessibleContext().setAccessibleName(bundle.getString(
        "validate"));
    menuItem.addActionListener(this);

    menuMA.addSeparator();
    menuMA.add(menuItem);

    menuItem = new JMenuItem(bundle.getString("exit"));
    menuItem.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "exit.desc"));
    menuItem.getAccessibleContext().setAccessibleName(bundle.getString("exit"));
    menuItem.addActionListener(this);

    menuMA.addSeparator();
    menuMA.add(menuItem);

    return menuMA;
  }

  private JMenu createTellFrndMenu() {
    JMenu menuHelp;
    JMenuItem menuItem;
    menuHelp = new JMenu(bundle.getString("tellfrnd"));
    menuHelp.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "tellfrnd.desc"));
    menuItem = new JMenuItem(bundle.getString("invite"));
    menuItem.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "invite.desc"));
    menuItem.addActionListener(this);
    menuHelp.add(menuItem);
    return menuHelp;
  }


  private JMenu createEventsMenu() {
    JMenu menuHelp;
    JMenuItem menuItem;
    menuHelp = new JMenu(bundle.getString("events"));
    menuHelp.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "events.desc"));
    menuItem = new JMenuItem(bundle.getString("news"));
    menuItem.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "news.desc"));
    menuItem.addActionListener(this);
    menuHelp.add(menuItem);

    menuItem = new JMenuItem(bundle.getString("tourny"));
    menuItem.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "tourny.desc"));
    menuItem.getAccessibleContext().setAccessibleName(bundle.getString("tourny"));
    menuItem.addActionListener(this);
    menuHelp.addSeparator();

    menuHelp.add(menuItem);

    return menuHelp;
  }

  private JMenu createHelpMenu() {
    JMenu menuHelp;
    JMenuItem menuItem;
    menuHelp = new JMenu(bundle.getString("help"));
    menuHelp.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "help.desc"));
    menuItem = new JMenuItem(bundle.getString("help.game"));
    menuItem.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "help.game.desc"));
    menuItem.addActionListener(this);
    menuHelp.add(menuItem);
    return menuHelp;
  }

  protected JMenu createSettingsMenu(boolean hasAutoLogin) {
    //ClientSettings settings = _serverProxy.loadClientSettings(); // TODO get clients setting from the server
    JMenu jmSettings;
    jmSettings = new JMenu(bundle.getString("settings"));
    jmSettings.getAccessibleContext().setAccessibleDescription(bundle.getString(
        "settings.desc"));
    if (hasAutoLogin) {
      jmiAutoLogin = createSettingsMenuItem(bundle.getString("autologin"),
                                            loginSettings.isAutoLogin());
      jmSettings.add(jmiAutoLogin);
      jmSettings.addSeparator();
    }

//@@@@@@@@@@@@@@@@@@   SOUND ON & OFF   @@@@@@@@@@@@@@@@@@@@@@@@@@
    jmiSound = createSettingsMenuItem("Sound", true);
    _cat.fine("createSettingsMenu: " + true);
    jmSettings.add(jmiSound);

    return jmSettings;

  }

  protected JMenu createCashierMenu(boolean hasCashierMenu) {
    JMenu menu;
    JMenuItem menuItem;
    menu = new JMenu(bundle.getString("lobby"));
    menu.getAccessibleContext().setAccessibleDescription("menu item");

    if (hasCashierMenu) {
      menuItem = new JMenuItem(bundle.getString("cashier"));
      menuItem.getAccessibleContext().setAccessibleDescription(bundle.getString(
          "cashier.desc"));
      menuItem.getAccessibleContext().setAccessibleName(bundle.getString(
          "cashier"));
      menuItem.addActionListener(this);
      menu.add(menuItem);
    }

    return menu;
  }


  /** protected void initJBCashier() {
     ActionListener cashierListener = new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         Utils.showUrl(Utils.getCashierUrl(_serverProxy._name,
                                           _serverProxy.getTicket()));
       }
     };
     jbCashier = UsersComponentsFactory.createJBCashier(cashierListener);
     components.add(jbCashier);
   }**/

  protected void initJBSitTable() {
    ActionListener sitTableListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JTable jTable = getSelectedJTable();
        _cat.fine("Got to pitch button clicked, sel table = " + jTable);
        MouseEvent mouseEvent = new MouseEvent(jTable, 0, 0, 0, 0, 0, 2, false);
        mouseClicked(mouseEvent);
      }
    };
    jbSitTable = UsersComponentsFactory.createJBSitTable(sitTableListener);
    components.add(jbSitTable);
  }


  /**protected void initJBNetPractice() {
    jbNetPractice = UsersComponentsFactory.createJBNetPractice(this);
    components.add(jbNetPractice);
     }**/


  protected void initJBWait() {
    jbWait = UsersComponentsFactory.createJBWait(this);
    components.add(jbWait);
  }

  protected JTable getSelectedJTable() {
    JTable jTable = null;
    int tabbePaneSelectedIndex = tabbedPane.getSelectedIndex();
    if (tabbePaneSelectedIndex == 0) {
      jTable = null;
    }
    else if (tabbePaneSelectedIndex == 1) {
      jTable = holdemTable;
    }
    else if (tabbePaneSelectedIndex == 2) {
      jTable = matchTable;
    }
    else if (tabbePaneSelectedIndex == 3) {
      jTable = tournament2008;
    }
    /**else if (tabbePaneSelectedIndex == 4) {
      jTable = ppTable; //TODO
    }
    else if (tabbePaneSelectedIndex == 5) {
      jTable = managerTable; //TODO
    }
    else if (tabbePaneSelectedIndex == 6) {
      jTable = managerTournyTable; //TODO
    }**/
    return jTable;
  }


  /**
   * Listen to server responses --- errors, info updates etc
   * @param e ListSelectionEvent
   */
  public void serverLobbyResponse(Action act) {
    JOptionPane.showMessageDialog(frame, bundle.getString("Server Error " + act),
                                  bundle.getString("error"),
                                  JOptionPane.ERROR_MESSAGE);
  }


  // ListSelectionListener Interface
  public void valueChanged(ListSelectionEvent e) {
    if (e.getSource() instanceof ListSelectionModel &&
        frame.getFocusOwner() instanceof JTable) {
      JTable jTable = (JTable) frame.getFocusOwner();
      if (jTable.getSelectionModel() != e.getSource()) {
        return;
      }
      updateSelectedLobbyTable(jTable);
    }
  }


  // end of ListSelectionListener Interface

  private JCheckBoxMenuItem createSettingsMenuItem(String menuLabel,
      boolean isArmed) {
    JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(menuLabel);
    menuItem.setState(isArmed);
    menuItem.getAccessibleContext().setAccessibleDescription("");
    menuItem.addActionListener(this);
    //fillComponent(menuItem);
    return menuItem;
  }

  public void adUpdated() {
    pingTextArea.setVisible(false);
    adTextArea.setText(_serverProxy.getAd());
    /**adTextArea.append((cricket_model._total_players +
     matches_model._total_players + 3900) + " players playing on " +
                      198 +
                      " play grounds");**/
    //pingTextArea.setText("Ping:   " +
    ///**SharedConstants.doubleToString(_player.
     //getAveragePing())+**/" ms");
    //pingTextArea.setVisible(true);
  }

  private void createMainPanelWithContents() {
    _cat.fine("Creating main panel with content ");
    JScrollPane scrollPane = createInfoTextArea();

    pingTextArea = new JLabel();
    pingTextArea.setBounds(535, -7, 240, 30);
    pingTextArea.setForeground(Color.WHITE);

    adTextArea = new JTextArea();
    adTextArea.setBounds(535, 490, 240, 70);
    adTextArea.setOpaque(false);
    adTextArea.setFont(Utils.boldFont);
    //adTextArea.setForeground(Color.WHITE);

    JPanel main_panel = new JPanel();

    components.add(main_panel);
    components.add(scrollPane);
    components.add(pingTextArea);
    components.add(adTextArea);

    main_panel.setBounds(10, 112, 504, 360);
    main_panel.setLayout(new BorderLayout());

    tabbedPane = new JTabbedPane();
    tabbedPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        _cat.fine("toggle = " + tabbedPane.getSelectedIndex());
        if (tabbedPane.getSelectedIndex() == 0) {
          //chat
          jbSitTable.setVisible(false);
          jbSitTable.setEnabled(false);
        }
        else if (tabbedPane.getSelectedIndex() == 1) {
          jbSitTable.setVisible(true);
          JTable jTable = getSelectedJTable();
          _cat.fine("jTable = " + jTable.getName());
          if (_serverProxy != null && _serverProxy.isLoggedIn()) {
            _serverProxy.setPoll(jTable.getName());
          }
          if (jTable.getSelectedRow() >= 0) {
            focusGained(new FocusEvent(jTable, -1));
          }
          else {
            if (_serverProxy != null) {
              _serverProxy.stopWatchOnTable();
            }
            infoTextArea.setInfo(null);
            jbSitTable.setEnabled(false);
            if (jbWait != null) {
              jbWait.setEnabled(false);
            }
          }
        }
        else if (tabbedPane.getSelectedIndex() == 2) {
          //matches
           _serverProxy.setPoll("Matches");
        }
        else if (tabbedPane.getSelectedIndex() == 3) {
          // cricket madness
           _serverProxy.setPoll("Madness");

          //tournaments
          /**jbSitTable.setVisible(false);
          jbSitTable.setEnabled(false);
          JTable jTable = getSelectedJTable();
          if (_serverProxy != null && _serverProxy.isLoggedIn()) {
            _serverProxy.setPoll(jTable.getName());
          }**/
        }
        else if (tabbedPane.getSelectedIndex() == 4) {
          //unimplemented
          jbSitTable.setVisible(false);
          jbSitTable.setEnabled(false);
          _cat.fine("Professional player space");
          JTable jTable = getSelectedJTable();
          if (_serverProxy != null && _serverProxy.isLoggedIn()) {
            _serverProxy.setPoll(jTable.getName());
          }

        }
        else if (tabbedPane.getSelectedIndex() > 5) {
          //unimplemented
          jbSitTable.setVisible(false);
          jbSitTable.setEnabled(false);
          _cat.fine("team managers space");
          JTable jTable = getSelectedJTable();
          if (_serverProxy != null && _serverProxy.isLoggedIn()) {
            _serverProxy.setPoll(jTable.getName());
          }
        }
      }
    });

    /* Creating tables	*/_cat.fine(
        "createMainPanelWithContents : Creating tables ");
    cricket_model = new CricketListModel(CricketListModel.PRACTICE);
    _serverProxy.addLobbyModelChangeListener(cricket_model);
    matches_model = new CricketListModel(CricketListModel.PROFESSIONAL);
    _serverProxy.addLobbyModelChangeListener(matches_model);
    tournament_june2008 = new CricketListModel(CricketListModel.MADNESS);
    _serverProxy.addLobbyModelChangeListener(tournament_june2008);
    //tournament_model = new TournamentListModel();
    //_serverProxy.addLobbyModelChangeListener(tournament_model);
    //pp_model = new PPListModel();
    //_serverProxy.addLobbyModelChangeListener(pp_model);
    //manager_model = new ManagerListModel();
    //_serverProxy.addLobbyModelChangeListener(manager_model);
    //manager_tourny_model = new ManagerListModel();
    //_serverProxy.addLobbyModelChangeListener(manager_tourny_model);

    //
    tabbedPane.addTab(bundle.getString("lobby.chat"), null, createChat(4),
                      bundle.getString("lobby.chat.desc"));
    tabbedPane.addTab(bundle.getString("team.practice"), null,
                      createJTable(cricket_model, 0),
                      bundle.getString("team.practice.desc"));
    tabbedPane.addTab(bundle.getString("team.cricket"), null,
                      createJTable(matches_model, 1),
                      bundle.getString("team.cricket.desc"));
    tabbedPane.addTab("June 2008 Cricket Madness Tournament", null,
                      createJTable(tournament_june2008, 2),
                      bundle.getString("tournament.desc"));

    /** tabbedPane.addTab(bundle.getString("tournament"), null,
                      createJTable(tournament_model, 2),
                      bundle.getString("tournament.desc"));
    tabbedPane.addTab(bundle.getString("professional.player"), null,
                      createJTable(pp_model, 3),
                      bundle.getString("professional.player.desc"));
    tabbedPane.addTab(bundle.getString("manager"), null,
                      createJTable(manager_model, 4),
                      bundle.getString("manager.desc"));
    tabbedPane.addTab(bundle.getString("managertourny"), null,
                      createJTable(manager_tourny_model, 5),
                      bundle.getString("managertourny.desc")); **/
    tabbedPane.setBackground(Utils.lobbyCellBgrnd);
    tabbedPane.setSelectedIndex(1);
    main_panel.add(tabbedPane, BorderLayout.CENTER);
    //
    main_panel.setOpaque(false);
  }

  private JScrollPane createInfoTextArea() {
    infoTextArea = new InfoJLabel();

    Font infoFont = new Font("Tahoma", Font.TRUETYPE_FONT, 11);
    //infoTextArea.setVerticalAlignment(JLabel.TOP);
    infoTextArea.setFont(infoFont);
    infoTextArea.setOpaque(false);
    JScrollPane scrollPane = new JScrollPane(infoTextArea,
                                             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                             JScrollPane.
                                             HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setOpaque(false);
    //55		scrollPane.setBounds(515, 105, 255, 420);
    scrollPane.setBounds(528, 21, 252, 448);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    return scrollPane;
  }

  private Color tableSelectionBackground;
  private JComponent createJTable(TableModel tableModel, int index) {
    _cat.fine("createJTable : Creating tables ");
    TableSorter sorter = new TableSorter(tableModel);
    tableModelListener tml = new tableModelListener(sorter);
    tableModel.addTableModelListener(tml);
    JTable jTable = new JTable(sorter);
    tml.setJTable(jTable);
    sorter.addMouseListenerToHeaderInTable(jTable);
    switch (index) {
      case 0:
        holdemTable = jTable;
        jTable.setName("Cricket");
        break;
      case 1:
        matchTable = jTable;
        jTable.setName("Matches");
        break;
      case 2:
        tournament2008 = jTable;
        jTable.setName("Cricket Madness");
        break;
      /**case 3:
        managerTable = jTable;
        jTable.setName("Team Manager");
        break;
      case 4:
        managerTournyTable = jTable;
        jTable.setName("Pro Tournaments");
        break;**/
    }
    tableSelectionBackground = jTable.getSelectionBackground();
    UserTableStringCellRenderer cellRenderer = new UserTableStringCellRenderer();
    jTable.getSelectionModel().addListSelectionListener(this);
    jTable.addFocusListener(this);
    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

//		=== Table Headers
// disabling column moving
    jTable.getTableHeader().setReorderingAllowed(false);
// multi line headers
    MultiLineHeaderRenderer renderer = new MultiLineHeaderRenderer();
    Enumeration enumr = jTable.getColumnModel().getColumns();
    while (enumr.hasMoreElements()) {
      ((TableColumn) enumr.nextElement()).setHeaderRenderer(renderer);
    }
    //jTable.getColumnModel().getColumn(0).setPreferredWidth(60);
    //jTable.getColumnModel().getColumn(1).setPreferredWidth(100);
    //jTable.getColumnModel().getColumn(3).setPreferredWidth(120);

    jTable.setRowSelectionAllowed(true);
    jTable.setColumnSelectionAllowed(false);
    jTable.getTableHeader().setResizingAllowed(true);
    jTable.setDragEnabled(false);
    jTable.setDefaultRenderer(String.class, cellRenderer);
    jTable.setOpaque(false);
    // mouse Listener
    jTable.addMouseListener(this);
    //
    JScrollPane scrollPane = new JScrollPane(JScrollPane.
                                             VERTICAL_SCROLLBAR_ALWAYS,
                                             JScrollPane.
                                             HORIZONTAL_SCROLLBAR_NEVER);
//		scrollPane.getVerticalScrollBar().setUI(Utils.getLobbyScrollBarUI());
    scrollPane.setViewport(new JViewport() {
      public void paintComponent(Graphics g) {
        g.drawImage(tableBackgroundImage.getImage(), 0, 0,
                    tableBackgroundImage.getIconWidth(),
                    tableBackgroundImage.getIconHeight(), this);
      }
    });
    scrollPane.setViewportView(jTable);
    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    scrollPane.addMouseListener(this);

    //    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    // add the infoarea as listener for details

    _serverProxy = ServerProxy.getInstance();
    return scrollPane;
  }

  private JComponent createChat(int index) {
    _cat.fine("createChat : creating chat " + index);
    JPanel jp = new JPanel();
    _chat_text = new JTextArea(18, 9);
    _chat_text.setEditable(false);
    _chat_text.setOpaque(false);
    _chat_text.setForeground(Color.YELLOW);
    _chat_text.setFont(Utils.chatFont);
    _chat_field = new JTextField(9);
    _chat_text.setText("\nCricketParty> Please select on the 'Matches' tab to join a game.\nCricketParty> Double click on a game to view that game.");
    _chat_text.append("\nCricketParty>For action you have to press “S”. \nCricketParty>To give power, direction and strength to your action you have to \nuse the arrow keys.\n");
    _chat_field.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        if (_chat_field.getText().trim().length() > 3) {
          ChatAction ca = new ChatAction(_serverProxy._name,
                                         _chat_field.getText());
          _cat.fine(ca.toString());
          try {
            _serverProxy.sendToServer("", ca);
          }
          catch (IOException e) {
            // DISCONNECTED FRM SERVER
            JOptionPane.showMessageDialog(frame,
                                          bundle.getString("server.error"),
                                          bundle.getString("error"),
                                          JOptionPane.ERROR_MESSAGE);

          }

          _chat_field.setText("");
        }
      }
    });
    //
    JScrollPane scrollPane = new JScrollPane(JScrollPane.
                                             VERTICAL_SCROLLBAR_ALWAYS,
                                             JScrollPane.
                                             HORIZONTAL_SCROLLBAR_NEVER);
//		scrollPane.getVerticalScrollBar().setUI(Utils.getLobbyScrollBarUI());
    scrollPane.setViewport(new JViewport() {
      public void paintComponent(Graphics g) {
        g.drawImage(tableBackgroundImage.getImage(), 0, 0,
                    tableBackgroundImage.getIconWidth(),
                    tableBackgroundImage.getIconHeight(), this);
      }
    });
    scrollPane.setViewportView(_chat_text);

    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

    scrollPane.addMouseListener(this);

    //    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);

    // add the infoarea as listener for details

    jp.setLayout(new BorderLayout());

    jp.add(scrollPane, BorderLayout.NORTH);

    jp.add(_chat_field, BorderLayout.SOUTH);

    return jp;
  }


  public void chatRcvd(ChatAction ca) {
    if (ca.getSendersName() != null) {
      _chat_text.append(ca.getSendersName() + ">" + ca.getChatString() + "\n");
    }
    else {
      String mesg = new String(Base64.decode(ca.getChatString()));
      _cat.fine("Broadcast mesg=" + mesg);

      Thread t = new Thread(new MessagePopup(mesg));
      t.start();
    }
  }


  public LobbyListModel getPracticeCricketModel() {
    return (cricket_model);
  }

  public LobbyListModel getCricketMatchesModel() {
    return (matches_model);
  }

  public LobbyListModel getTournamentModel() {
  return tournament_june2008;
  //return (tournament_model);
  }

  public int getActivePage() {
    return (tabbedPane.getSelectedIndex());
  }

  private boolean isErrorOccured = false;
  public void serverErrorOccured(String info) {
    new Exception().printStackTrace();
    if (!isErrorOccured) {
      jmSettings.setEnabled(false);
      JOptionPane.showMessageDialog(frame, bundle.getString("server.error"),
                                    bundle.getString("error"),
                                    JOptionPane.ERROR_MESSAGE);
      isErrorOccured = true;
      cricket_model.clear();
      matches_model.clear();
      //pp_model.clear();
      //manager_model.clear();
      //manager_tourny_model.clear();
      tournament_june2008.clear();
    //tournament_model.clear();
      infoTextArea.setInfo(null);
      //new PingAndConnect(this);
    }
  }


//	protected void fillComponent (JComponent component) {
//00		component.setForeground(ClientConfig.mainColor);
//00		component.setBackground(ClientConfig.secColor);
//	}
  protected LobbyTableModel getSelectedTableByJTable(JTable jTable) {
    if (jTable.getModel() instanceof TableSorter) {
      TableSorter sorter = (TableSorter) jTable.getModel();
      LobbyListModel lobbyListModel = (LobbyListModel) (sorter.getModel());
      int visibleNo = jTable.getSelectedRow();
      _cat.fine("No=" + visibleNo);
      if (visibleNo < 0) {
        return null;
      }
      int realNo = sorter.getRealNo(visibleNo);
      if (realNo >= 0) {
        LobbyTableModel table = lobbyListModel.getModelAtRow(realNo);
        return table;
      }
    }
    return null;
  }


// MouseListner Interface :
  public void mouseClicked(MouseEvent e) {
    _cat.fine("mouse click generated ");
    if (e.getSource() instanceof JTable) {
      JTable jTable = (JTable) e.getSource();
      LobbyTableModel table = getSelectedTableByJTable(jTable);
      _cat.fine("Click count=" + e.getClickCount() + ", Sel table = " + table);
      if (e.getClickCount() > 1 && table != null) {
        doTableRegistration(table);
      }
    }
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
    if (e.isPopupTrigger()) {
      popup(getSelectedTable(), e);
    }
  }

  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      popup(getSelectedTable(), e);
    }
  }

  protected void popup(LobbyTableModel model, MouseEvent e) {

  }

  protected LobbyTableModel getSelectedTable() {
    return getSelectedTableByJTable(getSelectedJTable());
  }

  public void focusGained(FocusEvent e) {
    if (e.getSource() instanceof JTable) {
      updateSelectedLobbyTable((JTable) e.getSource());
    }
  }

  public void focusLost(FocusEvent e) {
  }

  private void updateSelectedLobbyTable(JTable jTable) {
    LobbyTableModel table = getSelectedTableByJTable(jTable);
    if (table == null) {
      return;
    }
    String tid = table.getName();
    stopCheckOnTable();
    if (table != null) {
      checkOnTable(tid);
      jbSitTable.setEnabled(true);
      updateJBWait(table); //TODO implement waiting list
    }
    else {
      jbSitTable.setEnabled(false);
    }

    for (int i = 0; i < _latest_changes.length; i++) {
      LobbyTableModel lobbyModel = new LobbyTableModel(_latest_changes[i]);
      if (selected_tid.equals(lobbyModel.getName())) {
        infoTextArea.lobbyInfoUpdated(lobbyModel);
      }
    }

  }

  private void updateJBWait(LobbyTableModel table) {
    if (jbWait != null) {
      jbWait.setEnabled(!table.isAcceptingPlayers());
      jbWait.setSelected(_serverProxy != null &&
                         _serverProxy.isWait(table.getName()));
    }
  }


  /**
   *
   */
  public synchronized void waitMessageRecieved(ServerProxy proxy,
                                               Object message) {
    _cat.fine(">>>> MESSAGE: " + message);
    if (message instanceof com.criconline.actions.Action) {
      com.criconline.actions.Action action = (com.criconline.actions.Action)
                                             message;
      if (action.getType() == ActionConstants.ACTION_TYPE_ERROR) {
        JOptionPane.showMessageDialog(frame, action.toString(),
                                      bundle.getString("waiting.list.error"),
                                      JOptionPane.ERROR_MESSAGE);
        if (jbWait != null) {
          jbWait.getModel().setSelected(false);
        }
        return;
      }
    }
    else {
      return;
    }
    LobbyTableModel lobbyTable = null;
    Component[] tabs = tabbedPane.getComponents();
    for (int i = 0; i < tabs.length; i++) {
      if (tabs[i] instanceof JScrollPane) {
        JScrollPane jScrollPane = (JScrollPane) tabs[i];
        JTable jTable = (JTable) jScrollPane.getViewport().getView();
        if (jTable.getModel() instanceof TableSorter) {
          TableSorter sorter = (TableSorter) jTable.getModel();
          if (sorter.getModel() instanceof LobbyListModel) {
            LobbyListModel lobbyList = (LobbyListModel) sorter.getModel();
            //lobbyTable =
            //lobbyList.getModelByTableId(proxy.getTableId());
            if (lobbyTable != null) {
              break;
            }
          }
        }
      }
    }
    if (lobbyTable == null) {
      _cat.fine(">>>> REMOVE 0");
      proxy.removeFromWaiters();
      return;
    }
    String dialogMessage = bundle.getString("thereis.vacant.places") + " \"" +
                           lobbyTable.getName() + "\".\n" +
                           bundle.getString("you.have.minute") + ".";

    frame.setState(Frame.NORMAL);
    frame.toFront();
    if (proxy != null && proxy.isRegistered()) {

      ClientRoom room = findClientRoom(lobbyTable);
      JFrame messageOwner = (room == null) ? frame : room;

      if (room != null) {
        room.setWaiterCount(0);
        _cat.fine("room.setWaiterCount(0)");
      }

      if (messageOwner.getState() == Frame.ICONIFIED) {
        messageOwner.setState(Frame.NORMAL);
      }
      messageOwner.toFront();

      JOptionPane.showMessageDialog(messageOwner, dialogMessage);

    }
    else {

      if (frame.getState() == Frame.ICONIFIED) {
        frame.setState(Frame.NORMAL);

      }
      frame.toFront();

      dialogMessage += "\n" + bundle.getString("would.you.like.register");
      int result = JOptionPane.showConfirmDialog(frame, dialogMessage,
                                                 bundle.getString("proposition"),
                                                 JOptionPane.YES_NO_OPTION);

      if (result == JOptionPane.YES_OPTION) {
        lobbyTable.setWaiterCount(0);
        doTableRegistration(lobbyTable);
      }
      else {
        _cat.fine(">>>> REMOVE 1");
        proxy.removeFromWaiters();
      }
    }
    if (jbWait != null) {
      jbWait.setSelected(false);
    }
  }

  private ClientRoom findClientRoom(LobbyTableModel lobbyTable) {
    for (Iterator iter = vClientRooms.iterator(); iter.hasNext(); ) {
      ClientRoom element = (ClientRoom) iter.next();
      if (element.isWindowClosing() == false &&
          lobbyTable.getName().equals(element.getName())) {
        return element;
      }
    }
    return null;
  }

  protected void createLoginFrameAsNeeded() {
   /** try {
      Runtime.getRuntime().exec(
          "explorer.exe http://www.cricketparty.com/tip.html");
      //new URLOpener("http://www.cardsownroom.com/launch.jsp");
      //new URLOpener("http://www.cricketparty.com/tip.html");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }**/

    //check if user password are available from system properties
    String user = System.getProperty("username");
    String pass = System.getProperty("password");
    if (user != null && pass != null && user.length() > 2 && pass.length() > 2) {
      try {
        login(user, pass.toCharArray());
        //this.setModal(false);
        frame.setVisible(false);
        frame.setVisible(true);
        frame.setEnabled(true);
        //frame.requestFocusInWindow();
      }
      catch (AuthenticationException e) {
        newLoginFrame();
      }
    }
    else {
      _cat.fine("Creating login frame ");
      if (isAutoLogin()) {
        try {
          this.login(loginSettings.getLogin(),
                     loginSettings.getPassword().toCharArray());
          frame.setVisible(true);
        }
        catch (Exception ex) {
          newLoginFrame();
        }
      }
      else {
        newLoginFrame();
      }
    }
  }

  protected boolean isAutoLogin() {
    return loginSettings.isAutoLogin();
  }

  protected void newLoginFrame() {
    new LoginFrame(frame, this, loginSettings, bundle.getString("please.login"), true, true);
  }


  /**
   * Opens the window with play table.
   */
  private void doTableRegistration(LobbyTableModel lobbyTable) {
    _cat.fine("Table registration " + lobbyTable.getName());
    int openRoomCount = 0;
    try {
      for (Iterator iter = vClientRooms.iterator(); iter.hasNext(); ) {
        ClientRoom element = (ClientRoom) iter.next();
        if (!element.isWindowClosing()) {
          openRoomCount++;
        }
        if (element.isWindowClosing() == false &&
            lobbyTable.getName().equals(element.getName())) {
          if (element.getState() == Frame.ICONIFIED) {
            element.setState(Frame.NORMAL);
          }
          element.toFront();
          _cat.fine("try push table to front");
          return;
        }
        //throw new Exception(bundle.getString("table.already.opened"));
      }
      if (openRoomCount < 2) {
        _cat.fine("try call openTable openRoomCount = " + openRoomCount);
        openTable(lobbyTable);
      }
      else {
        JOptionPane.showMessageDialog(frame,
                                      bundle.getString("too.many.tables.open"),
                                      bundle.getString("error"),
                                      JOptionPane.ERROR_MESSAGE);
        // END DEBUG !!!
      }
    }
    catch (Exception ex) {
      _cat.severe("exception in do table registration " + ex.getMessage());
      JOptionPane.showMessageDialog(frame,
                                    ex.getMessage() == null ||
                                    ex.getMessage().length() == 0 ? "" :
                                    ex.getMessage(), bundle.getString("error"),
                                    JOptionPane.ERROR_MESSAGE);
    }
  }

  private void openTable(LobbyTableModel lobbyTable) {
    _cat.fine("Creating a client room--" + lobbyTable);
    if (lobbyTable instanceof LobbyCricketModel) {
      ClientRoom cr = createClientRoom((LobbyCricketModel) lobbyTable);
      vClientRooms.add(cr);
    }
    else if (lobbyTable instanceof LobbyTournyModel) {
      _cat.fine("Open the tourny lobby");
      TournyLobby cr = createTournyLobby((LobbyTournyModel) lobbyTable);
    }
    else {
      throw new IllegalStateException(lobbyTable.toString());
    }
  }

  protected ClientRoom createClientRoom(LobbyCricketModel lobbyTable) {
    _serverProxy = _serverProxy == null ? ServerProxy.getInstance() :
                   _serverProxy;
    ClientRoom cr = new ClientRoom(_serverProxy, lobbyTable, frame);
    return cr;
  }

  protected TournyLobby createTournyLobby(LobbyTournyModel lobbyTable) {
    _serverProxy = _serverProxy == null ? ServerProxy.getInstance() :
                   _serverProxy;
    TournyLobby cr = new TournyLobby(_serverProxy, lobbyTable, frame);
    return cr;
  }

  public void login(String login, char[] password) throws
      AuthenticationException {
    if (_serverProxy.isLoggedIn()) {
      try {
        _serverProxy.logout();
      }
      catch (Exception e) {}
    }
    wantLogin(login, password);
    if (_serverProxy.isLoggedIn()) {
      jmSettings.setEnabled(true);
      frame.setTitle(login + " - " + bundle.getString("lobby"));
      _cat.fine("!!!_____connected_____!!!");

      //ClientSettings storedSettings = _serverProxy.loadClientSettings();
      //if (storedSettings != null) {
      //  updateSettingsMenuItems(storedSettings);
      //}

    }
    isErrorOccured = false;
  }


  public void userRegister(String user, String password, String email,
                           int gender, String bc, String sc) throws
      AuthenticationException {
    if (_serverProxy.isLoggedIn()) {
      try {
        _serverProxy.logout();
      }
      catch (Exception e) {}
    }
    int r = 0;
    try {
      r = _serverProxy.registerUser(user, new String(password), email, gender,
                                    bc, sc).getResult();
    }
    catch (Exception e) {
      _cat.severe("Register failed " + e.getMessage());
    }
    if (r != 1) {
      throw new AuthenticationException("Register Failed");
    }

    _cat.fine("+++_____We_connected_____+++");

    if (_serverProxy.isLoggedIn()) {
      jmSettings.setEnabled(true);
      frame.setTitle(user + " - " + bundle.getString("lobby"));
      _cat.fine("!!!_____connected_____!!!");

      ClientSettings storedSettings = _serverProxy.loadClientSettings();
      if (storedSettings != null) {
        //updateSettingsMenuItems(storedSettings);
      }
    }
  }


  public String getLoginMessage() {
    return null;
  }

  protected void wantLogin(String login, char[] password) throws
      AuthenticationException {
    int r = 0;
    try {
      r = _serverProxy.login(login, new String(password)).getResult();
    }
    catch (Exception e) {
    	e.printStackTrace();
      _cat.severe("Login failed " + login + ", " + e.getMessage());
    }

    if (r != 1) {
      throw new AuthenticationException("Login Failed");
    }
    try {
      _serverProxy.getGameList(0xFFFFFF);
    }
    catch (Exception e) {
      _cat.severe("Getting Table list " + e.getMessage());
    }

    _cat.fine("+++_____We_connected_____+++");
  }


  /***
    private void updateSettingsMenuItems(ClientSettings settings) {
      //jmiBigSymbols.setState(settings.isBigSymbols());
      jmiAutoBigBlind.setState(settings.isAutoPostBlind());
      jmiWaitBigBlind.setState(settings.isWaitForBigBlind());
      jmiShowBestCards.setState(settings.isShowBestCards());
      jmiMuckLoosingCards.setState(settings.isMuckLosingCards());
      _cat.fine("updateSettingsMenuItems: " + settings.isMuckLosingCards());
//@@@@@@@@@@@@@@@@@@   SOUND ON & OFF   @@@@@@@@@@@@@@@@@@@@@@@@@@
      jmiSound.setState(settings.isSound());
      _cat.fine("updateSettingsMenuItems: " + settings.isSound());
      //   jmiRandomDelay.setState(settings.isRandomDelay());
      jmSettings.setEnabled(true);
    }
   **/

  private void lobbyExit() {
    ClientRoom cr;
    boolean flag = true;

    for (Iterator i = vClientRooms.iterator(); i.hasNext(); ) {
      Object object = i.next();
      if (object instanceof ClientRoom) {
        cr = (ClientRoom) object;
        cr.tryCloseRoom();
        if (cr.isWindowClosing() == false) {
          flag = false;
        }
      }
    }
    if (flag) {
      _serverProxy = _serverProxy == null ? ServerProxy.getInstance() :
                     _serverProxy;
      if (cricket_model != null) {
        _serverProxy.removeLobbyModelChangeListener(cricket_model);
      }
      if (matches_model != null) {
        _serverProxy.removeLobbyModelChangeListener(matches_model);
      }
     /** if (tournament_model != null) {
        _serverProxy.removeLobbyModelChangeListener(pp_model);
      }

      if (tournament_model != null) {
        _serverProxy.removeLobbyModelChangeListener(manager_model);
      }

      if (tournament_model != null) {
        _serverProxy.removeLobbyModelChangeListener(manager_tourny_model);
      }

      if (tournament_model != null) {
        _serverProxy.removeLobbyModelChangeListener(tournament_model);
      }**/

      if (tournament_june2008 != null){
        _serverProxy.removeLobbyModelChangeListener(tournament_june2008);
      }
      try {
        _serverProxy.logout();
      }
      catch (Exception e) {}

      if (_serverProxy != null) {
        _serverProxy.stopWatchOnTable();
      }
      ServerProxy.removeAllWaiters();
      System.exit(0);
    }
    else {
      JOptionPane.showMessageDialog(frame, bundle.getString("close.all.tables"),
                                    bundle.getString("proposition"),
                                    JOptionPane.INFORMATION_MESSAGE);
    }
  }

  protected void init_player(String ip, int port) throws MalformedURLException {
    try {
      _serverProxy = ServerProxy.getInstance(ip, port, frame);
    }
    catch (Exception e) {
      _cat.severe("Unable to connect to " + ip + ", " + port);
      JOptionPane.showMessageDialog(frame, bundle.getString("error.connection"),
                                    bundle.getString("error"),
                                    JOptionPane.ERROR_MESSAGE);
      System.exit( -1);
    }
    _serverProxy.addLobbyModelChangeListener(this);
  }


  /**
   * Wait list button pressed.
   */
  private void waitingButtonPressed() {
    if (_serverProxy == null) {
      _serverProxy = ServerProxy.getInstance();
    }
    // check if we already waiting for this table ..
    if (!_serverProxy.isWait(this.getSelectedTable().getName())) {
      try {
        _serverProxy.addToWaiters(this.getSelectedTable().getName());
      }
      catch (IOException e) {
        // DISCONNECTED FRM SERVER
        JOptionPane.showMessageDialog(frame, bundle.getString("server.error"),
                                      bundle.getString("error"),
                                      JOptionPane.ERROR_MESSAGE);

      }

      JOptionPane.showMessageDialog(frame,
                                    "Waiting on table " +
                                    this.getSelectedTable().getName(), "OK",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    else {
      JOptionPane.showMessageDialog(frame,
                                    "Already waiting on this table " +
                                    this.getSelectedTable().getName(), "ERROR",
                                    JOptionPane.ERROR_MESSAGE);

    }
  }


  /**
   * Net Practice button list button pressed.
   */
  private void netPracticeButtonPressed() {
    PractisePitch.initPracticePitch();
  }

  private void updateSettings(JCheckBoxMenuItem menuItem) {
    /***
         ClientSettings settings = _serverProxy.loadClientSettings();
         if (menuItem == jmiAutoBigBlind) {
      settings.setAutoPostBlind(menuItem.getState());
         }
         else if (menuItem == jmiWaitBigBlind) {
      settings.setWaitForBigBlind(menuItem.getState());
         }
         else if (menuItem == jmiShowBestCards) {
      settings.setShowBestCards(menuItem.getState());
         }
         else if (menuItem == jmiMuckLoosingCards) {
      settings.setMuckLosingCards(menuItem.getState());
         }
//@@@@@@@@@@@@@@@@@@   SOUND ON & OFF   @@@@@@@@@@@@@@@@@@@@@@@@@@
         else if (menuItem == jmiSound) {
      settings.setSound(menuItem.getState());
         }
//    else if (menuItem == jmiRandomDelay) {
//      settings.setRandomDelay(menuItem.getState());
//    }
         _serverProxy.storeClientSettings(settings);**/
  }

  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source instanceof JCheckBoxMenuItem) {
      updateSettings((JCheckBoxMenuItem) source);
      return;
    }
    else if (source instanceof JToggleButton) {
      if (source == jbWait && jbWait != null) {
        waitingButtonPressed();
        return;
      }
    }
    else if (source instanceof JButton) {
      if (source == jbNetPractice && jbNetPractice != null) {
        netPracticeButtonPressed();
        return;
      }
    }

    String command = ((JMenuItem) e.getSource()).getAccessibleContext().
                     getAccessibleName();
    if (command == null) {
      return;
    }
    if (command.equals(bundle.getString("login"))) {
      newLoginFrame();
    }
    else if (command.equals(bundle.getString("logout"))) {
      infoTextArea.setInfo(null);
      try {
        _serverProxy.logout();
      }
      catch (Exception ex) {}
    }

    else if (command.equals(bundle.getString("profile"))) {
      _cat.fine(">>>>>>>>>>" + Utils.getProfileUrl() + _serverProxy._name);
      Utils.showUrl(Utils.getProfileUrl() + _serverProxy._name);
    }

    else if (command.equals(bundle.getString("validate"))) {
      Utils.showUrl(Utils.getValidateUrl() + _serverProxy._name);
    }

    else if (command.equals(bundle.getString("exit"))) {
      if (jmiAutoLogin != null) {
        loginSettings.setAutoLogin(jmiAutoLogin.isSelected());
      }
      loginSettings.saveSettings();
      lobbyExit();
    }

    else if (command.equals(bundle.getString("cashier"))) {
      _cat.info(">>> Call to cashier <<<");
      //Utils.showUrl(Utils.getCashierUrl(_serverProxy._name,
      //                                  _serverProxy.getTicket()));
    }

    else if (command.equals(bundle.getString("news"))) {
      _cat.info(">>> Call to News <<<");
    }

    else if (command.equals(bundle.getString("tourny"))) {
      _cat.info(">>> Call to Tourny <<<");
    }

    else if (command.equals(bundle.getString("invite"))) {
      Utils.showUrl(Utils.getInviteUrl() + _serverProxy._name);
    }

    else if (command.equals(bundle.getString("help.game"))) {
      Utils.showUrl(Utils.getHelpUrl());
    }
  }

  private void openTablesAsNeed(LobbyTableModel changes[]) {
    if (_table_list != null && _table_list.getGameCount() > 0) {
      _cat.fine("Opening tables");
      for (int i = 0; i < changes.length; i++) {
        LobbyTableModel lobbyModel = changes[i];
        for (int idsIndex = 0; idsIndex < _table_list.getGameCount(); idsIndex++) {
          if (_table_list.getGameEvent(idsIndex) != null &&
              lobbyModel.getName() ==
              _table_list.getGameEvent(idsIndex).getGameId()) {
            openTable(lobbyModel);
            //_table_list.getGameEvent(idsIndex) = -1;
          }
        }
      }
    }
  }

  public void professionalPlayerList(DBPlayer[] changes) {
    _cat.severe("RCVD professional players list " + changes.length);
    if (changes.length == 0) {
      return;
    }

  }

  public void tableListUpdated(LobbyTableModel changes[]) {
    _cat.fine("LobbyUser Impl tableList update " + changes.length);
    if (changes.length == 0) {
      return;
    }
    // check if any tables need to be opened
    if (ServerProxy._lresp != null) {
      for (int l = 0; l < ServerProxy._lresp.getTableCount(); l++) {
        String tid = ServerProxy._lresp.getTid(l);
        int pos = ServerProxy._lresp.getPos(l);
        String team = ServerProxy._lresp.getTeam(l);
        _cat.fine("OPENING TABLE " + tid);
        for (int i = 0; i < changes.length; i++) {
          LobbyTableModel lobbyModel = changes[i];
          _cat.fine("CHECKING " + lobbyModel);
          if (lobbyModel.getName().equals(tid)) {
            // create a action registry entry
            _serverProxy.createActionFactory(tid, pos, team);
            openTable(lobbyModel);
          }
        }
      }
      ServerProxy._lresp = null;
    }
    _latest_changes = changes;
    openTablesAsNeed(changes);
    for (int i = 0; i < changes.length; i++) {
      //_cat.fine("Tourny " + changes[i]);
      LobbyTableModel lobbyModel = changes[i];
      /***if (lobbyModel.getGameState().isOpen()) {
        if (jbWait != null) {
          jbWait.setEnabled(!lobbyModel.isAcceptingPlayers());
        }
             }***/
      // update the info panel if required
      if (selected_tid.equals(lobbyModel.getName())) {
        infoTextArea.lobbyInfoUpdated(lobbyModel);
      }
      //_cat.fine(lobbyModel);
    }
    adUpdated();
  }

  class tableModelListener implements TableModelListener {

    private TableSorter sorter;
    private JTable jTable = null;

    tableModelListener(TableSorter sorter) {
      this.sorter = sorter;
    }

    public void setJTable(JTable jTable) {
      _cat.fine("Setting table ....");
      this.jTable = jTable;
    }

    public void tableChanged(TableModelEvent e) {
      sorter.sort(this);
      if (jTable != null) {
        jTable.repaint();
      }
    }
  }

  private void setLookAndFeel(MetalTheme theme) {
    try {
      if (theme != null) {
        javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(theme);
      }
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    }
    catch (Exception ex) {
      _cat.severe("Failed loading L&F: :-@" + ex.toString());
    }
  }


  /**
   * Agneya NEW CLASS
   * FIX 6
   */

  public void stopCheckOnTable() {
    selected_tid = "";
  }

  public void checkOnTable(String tid) {
    selected_tid = tid;
  }

  private class InfoJLabel extends JPanel {

    protected JFrame frame;
    protected JPanel topPane, bottomPane;
    protected JPanel mainPane;
    protected DefaultTableModel activeTableModel; //, waitingTableModel;
    protected JTable activeTable; //, waitingTable;
    protected JTextArea tipTextArea;
    protected JLabel tableNameLabel;

    public InfoJLabel() {
      super(new BorderLayout());
      super.setOpaque(false);
      //the main JPanel where u add all the components
      mainPane = new JPanel(new BorderLayout());
      mainPane.setOpaque(false);

      activeTableModel = new DefaultTableModel(null, new String[] {"Pos",
                                               "Team", "Name"});
      // waitingTableModel = new DefaultTableModel(null, new String[] {"S No",
      //                                         "Name"});

      tableNameLabel = new JLabel();
      tableNameLabel.setOpaque(false);

      //active jtable
      activeTable = new JTable(activeTableModel);
      activeTable.setOpaque(false);
      activeTable.setEnabled(false);
      activeTable.setBackground(Utils.lobbyCellBgrnd);
      //activeTable.setGridColor(Color.GREEN);
      activeTable.getTableHeader();

      //waiting list JTable
      //waitingTable = new JTable(waitingTableModel);
      //waitingTable.setOpaque(false);
      //waitingTable.setEnabled(false);
      //waitingTable.setBackground(Utils.lobbyCellBgrnd);
      //waitingTable.getTableHeader();
      tipTextArea = new JTextArea();
      tipTextArea.setOpaque(false);
      tipTextArea.setFont(Utils.tipFont);

      tipTextArea.setText(
          "   To participate in team building you\nneed to score 500 runs.\n");
      tipTextArea.append(
          "  On scoring 500 runs you can go to\ncommunity tab on website and join\n");
      tipTextArea.append("a team.\n");
      tipTextArea.append(
          "  After you score additional 500 \nruns in professional games you become\n");
      tipTextArea.append("eligible to start your own team.");

      //add the active table in toppane
      topPane = new JPanel(new BorderLayout());
      topPane.setOpaque(false);
      topPane.setPreferredSize(new Dimension(150, 150));
      topPane.add(activeTable);
      //setting title for the top pane
      topPane.setBorder(new TitledBorder(new EtchedBorder(5), "Active Players"));
      //add waiting table in bottonpane
      bottomPane = new JPanel(new BorderLayout());
      bottomPane.setOpaque(false);
      bottomPane.setPreferredSize(new Dimension(150, 150));
      //bottomPane.add(new JScrollPane(waitingTable));
      //bottomPane.add(waitingTable);
      bottomPane.add(tipTextArea);
      //setting title for the bottom pane
      bottomPane.setBorder(new TitledBorder(new EtchedBorder(5), "Cricket Tip"));
      //add both the panes to the mainpane
      mainPane.add(tableNameLabel, BorderLayout.NORTH);
      mainPane.add(topPane, BorderLayout.CENTER);
      mainPane.add(bottomPane, BorderLayout.SOUTH);
      mainPane.setOpaque(false);
      add(mainPane);
    }


    /*
      //whwnever we want the table data to be changed call the setModel() and createDefaultColumnsFromModel()
      //methods of jtable
        waitingTableModel = new DefaultTableModel(getData(),tableCols)
        waitingTable.setModel( waitingTableModel );
        waitingTable.createDefaultColumnsFromModel();
     */

    //for getting waiting table data
    public Object[][] getData() {
      Vector rVec = new Vector();
      Object[][] data = new Object[rVec.size()][3]; ;
      for (int i = 0; i < rVec.size(); i++) {
        Vector tempVec = (Vector) rVec.elementAt(i);
        data[i][0] = tempVec.elementAt(0);
        data[i][1] = tempVec.elementAt(1);
        data[i][2] = tempVec.elementAt(2);
      }
      return data;
    }

    public void actionPerformed(ActionEvent evt) {

    }

    public void createAndShowGUI() {
      //Make sure we have nice window decorations.
      JFrame.setDefaultLookAndFeelDecorated(true);
      //Create and set up the window.
      frame = new JFrame("TablePanel");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //Create and set up the content pane.
      JComponent newContentPane = new InfoJLabel();
      newContentPane.setOpaque(false);
      //content panes must be opaque
      frame.setContentPane(newContentPane);
      //Display the window.
      frame.pack();
      frame.setVisible(true);
    }


    /**
     * Recieves and redraws detailed info about selected cricket table.
     */
    public void lobbyInfoUpdated(LobbyTableModel ge) {
      setInfo(ge);
    }

    public void setInfo(LobbyTableModel ge) {
      setVisible(false);

      // clear the table
      int rc = activeTableModel.getRowCount();
      for (int i = 0; i < rc; i++) {
        try {
          activeTableModel.removeRow(0);
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
          //ignore
        }

      }

      /** rc = waitingTableModel.getRowCount();
       for (int i = 0; i < rc; i++) {
         try {
           waitingTableModel.removeRow(0);
         }
         catch (java.lang.ArrayIndexOutOfBoundsException e) {
           //ignore
         }
       }**/

      if (ge == null) {
        setVisible(true);
        return;
      }
      _cat.fine(ge.toString());

      if (ge.getName() != null) {
        _cat.fine("  " + ge.getName() + "    Match");
        tableNameLabel.setText("  " + ge.getName() + "    Match");
      }
      String[][] pl = ge.getFieldersDetails();
      activeTableModel.addRow(new String[] {"Pos", "Team", "Name"});
      if (pl != null) {
        for (int i = 0; i < pl.length; i++) {
          activeTableModel.addRow(new String[] {pl[i][0], pl[i][1], pl[i][2]});
        }
      }
      pl = ge.getBattersDetails();
      if (pl != null) {
        for (int i = 0; i < pl.length; i++) {
          activeTableModel.addRow(new String[] {pl[i][0], pl[i][1], pl[i][2]});
        }
      }

      /**String[] wt = ge.getWaitersDetails();
             if (wt != null) {
        waitingTableModel.addRow(new String[] {"S. No", "Name"});
        for (int i = 0; i < wt.length; i++) {
          waitingTableModel.addRow(new String[] {"" + i, wt[i]});
        }
             }**/

      setVisible(true);
    }
  }


// END CLASS

}
