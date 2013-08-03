package com.criconline;

import com.agneya.util.BrowserLaunch;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.criconline.util.*;
import com.criconline.exceptions.*;
import com.criconline.models.*;
import com.criconline.pitch.PitchSkin;
import com.criconline.server.ServerProxy;
import java.util.logging.Logger;
import com.criconline.console.ClientCricketController;
import com.criconline.pitch.PitchColorTheme;
import com.criconline.anim.AnimationConstants;

import com.cricket.common.message.GameEvent;

public class ClientRoom
    extends JFrame implements  AnimationConstants {

  static Logger _cat = Logger.getLogger(ClientRoom.class.getName());

  private int waiterCount;
  String _name;
  private ServerProxy _serverProxy;
  private ClientCricketController _cricketController = null;
  private BottomPanel _bottomPanel = null;
  private MessagePanel _messagePanel = null;
  protected ServerProxy _tableServer = null;
  private boolean _isClosed = true;
  public JFrame _gameBoard;
  public boolean lobby_mode=false;

  protected static Dimension screenSize;
  protected static Dimension frameSize;
  protected static Point framePos;

  static {
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frameSize = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT + MESSAGE_HEIGHT);
    framePos = new Point((screenSize.width - frameSize.width) / 2,
                         (screenSize.height - frameSize.height) / 2);
  }

    public ClientRoom(ServerProxy sp,
                      GameEvent ge,
                      JFrame owner) {
      super();
      // Agneya FIX 16
      super.setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
      updateLookAndFeel();
      this._serverProxy = sp;
      this._gameBoard = owner;
      setDefaultCloseOperation(EXIT_ON_CLOSE);

      addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          //forcedCloseRoom(); // unsafe exit
          tryCloseRoom(); // safe exit
          _cat.finest("windowClosing(WindowEvent e)");
          _bottomPanel.leavePlease();
        }

        public void windowActivated(WindowEvent arg0) {
          if (MessageFactory.dialog != null) {
            MessageFactory.dialog.toFront();
          }
        }
      });
      _name = ge.getGameId();

      // --- Room Skin
      PitchSkin skin = new PitchSkin();

      //--- Bottom Panel
      this._bottomPanel = new BottomPanel(_serverProxy, skin, ge.getGameId());
    _messagePanel = new MessagePanel(frameSize);

    //--- Main class
    _cricketController = new ClientCricketController(_serverProxy, skin, _bottomPanel, _messagePanel, this);
    // --- Add exception catch !!!
    getContentPane().setLayout(new BorderLayout(0, 0));
    getContentPane().add(_cricketController, BorderLayout.CENTER);
    getContentPane().add(_messagePanel, BorderLayout.SOUTH);
    _cricketController.setLayout(new BorderLayout(0, 0));
    _cricketController.add(_bottomPanel, BorderLayout.SOUTH);

    setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
    _cat.finest("Creating client room " + ge.getGameState() + ", " +
               ge.getGameId());
    _serverProxy.addWatchOnTable(ge.getGameId(), _cricketController);

    _isClosed = false;
    setResizable(false);
    setVisible(true);
    _cat.finest("CREATED CLIENT ROOM");
  }

  public ClientRoom(ServerProxy serverProxy, LobbyCricketModel lobbyModel,
			JFrame lobbyFrame) {
	  super();
	  
	  String ges="name=" + lobbyModel.getName() + ",max-players=" + lobbyModel.getPlayerCapacity() + ",type=" + lobbyModel.getGameType()
		+ ",min-players=2";
		GameEvent ge = new GameEvent();
		ge.init(ges);
		lobby_mode=true;
	  
      // Agneya FIX 16
      super.setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
      updateLookAndFeel();
      this._serverProxy = serverProxy;
      this._gameBoard = lobbyFrame;
      setDefaultCloseOperation(EXIT_ON_CLOSE);

      addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          //forcedCloseRoom(); // unsafe exit
          tryCloseRoom(); // safe exit
          _cat.finest("windowClosing(WindowEvent e)");
          _bottomPanel.leavePlease();
        }

        public void windowActivated(WindowEvent arg0) {
          if (MessageFactory.dialog != null) {
            MessageFactory.dialog.toFront();
          }
        }
      });
      _name = ge.getGameId();

      // --- Room Skin
      PitchSkin skin = new PitchSkin();

      //--- Bottom Panel
      this._bottomPanel = new BottomPanel(_serverProxy, skin, ge.getGameId());
    _messagePanel = new MessagePanel(frameSize);

    //--- Main class
    _cricketController = new ClientCricketController(_serverProxy, skin, _bottomPanel, _messagePanel, this);
    // --- Add exception catch !!!
    getContentPane().setLayout(new BorderLayout(0, 0));
    getContentPane().add(_cricketController, BorderLayout.CENTER);
    getContentPane().add(_messagePanel, BorderLayout.SOUTH);
    _cricketController.setLayout(new BorderLayout(0, 0));
    _cricketController.add(_bottomPanel, BorderLayout.SOUTH);

    setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
    _cat.finest("Creating client room " + ge.getGameState() + ", " +
               ge.getGameId());
    _serverProxy.addWatchOnTable(ge.getGameId(), _cricketController);

    _isClosed = false;
    setResizable(false);
    setVisible(true);
    _cat.finest("CREATED CLIENT ROOM");
	}

public int getWaiterCount() {
    return this.waiterCount;
  }

  public void setWaiterCount(int waiterCount) {
    this.waiterCount = waiterCount;
  }

  public ClientCricketController getClientCricketController() {
    return _cricketController;
  }

  public ClientCricketModel getClientCricketModel() {
    return _cricketController.getModel();
  }

  public double getPlayerPoints() {
      return _serverProxy.points();
  }

  public String getName() {
    return _name;
  }


  public ServerProxy getLobbyServer() {
    return _serverProxy;
  }

  public void tryCloseRoom() {
    closeRoom(); // change comments
    _cricketController.tryExit();
  }

  public void closeRoom() {
    _cat.info("closeRoom");
    if (!_isClosed)
    if (JOptionPane.showConfirmDialog(this, 
                                              "Do you want to leave", 
                                              "Leave", 
                                              JOptionPane.YES_NO_OPTION) == 
                                                JOptionPane.YES_OPTION ) {
      _cat.info("real closeRoom");
      _tableServer = _tableServer == null ? ServerProxy.getInstance() :
          _tableServer;
      _cat.finest("tableserver is not null");
      _tableServer.leaveTable(getName());
      _tableServer.stopWatchOnTable(getName());
      forcedCloseRoom();
    }
  }

  public void forcedCloseRoom() {
    _cat.finest("forcedCloseRoom");
    markWindowAsClosed();
  }

  public void markWindowAsClosed() {
    _isClosed = true;
    dispose();
  }

  public boolean isWindowClosing() {
    return _isClosed;
  }

  /**
   * Update the system color theme.
   */
  protected void updateLookAndFeel() {
    try {
      javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(
          new PitchColorTheme());
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    }
    catch (Exception ex) {
      _cat.warning("Failed loading L&F: (ClientRoom)"+ ex);
    }
  }
  

     public static void main(String[] args) throws Exception {
           JFrame jf = new JFrame();
           if (args.length <= 4) {
               JOptionPane.showMessageDialog(jf, "Illegal number of arguments.", 
                                             "ERROR", JOptionPane.ERROR_MESSAGE);
               _cat.severe("Illegal number of arguments " + args.length);
               System.exit(-1);
           }
           System.setProperty("proxySet", "true");
           System.setProperty("proxyHost", args[0]);
           System.setProperty("proxyPort", args[1]);
           System.setProperty("username", args[2]);
           System.setProperty("password", args[3]);
           System.setProperty("ge", args[4]);

           _cat.finest("Game Event = " + args[4]);
           ServerProxy _serverProxy = 
               ServerProxy.getInstance(args[0], Integer.parseInt(args[1]), jf);
           if (_serverProxy.login(args[2], args[3]) == null) {
               JOptionPane.showMessageDialog(jf, 
                                             "Login Failed for " + args[2] + ".", 
                                             "ERROR", JOptionPane.ERROR_MESSAGE);
               _cat.severe("Login Failed for " + args[2]);
               System.exit(-1);
           }
           GameEvent ge = new GameEvent(args[4]);
           ClientRoom cr = new ClientRoom(_serverProxy, ge, jf);
           _serverProxy.setClientRoom(cr);
       }


}
