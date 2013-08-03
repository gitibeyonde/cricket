package com.criconline.console;

import java.awt.*;
import javax.swing.*;

import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import com.criconline.resources.*;
import com.criconline.*;
import com.criconline.actions.*;
import com.criconline.actions.Action;
import com.criconline.models.*;
import com.criconline.pitch.PitchSkin;
import com.cricket.common.message.GameEvent;
import com.cricket.mmog.PlayerStatus;
import com.criconline.util.*;
import javax.swing.JOptionPane;
import com.cricket.mmog.cric.util.ActionConstants;
import com.criconline.pitch.PitchSkin;
import com.cricket.mmog.GameType;
import com.cricket.mmog.cric.GameState;
import com.criconline.server.ServerEventPlugin;
import com.criconline.server.EventGenerator;
import com.criconline.anim.AnimationManager;
import com.criconline.anim.AnimationConstants;

public class PractisePitch
    extends CricketController implements PlayersConst, AnimationConstants {
  static Logger _cat = Logger.getLogger(PractisePitch.class.getName());

  public PractisePitch(EventGenerator seg, PitchSkin skin,
                       BottomPanel bottomPanel, MessagePanel messagePanel, JFrame selPitch, String gid) {
    super(seg, skin, bottomPanel, messagePanel, gid);
    _selPitch = selPitch;
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(new BorderLayout());
    addMouseMotionListener(this);
    addMouseListener(this);
  }

  protected ClientCricketModel newClientCricketModel(CricketModel cricModel) {
    return new ClientCricketModel(cricModel, _skin, this, _bottomPanel);
  }

  public void paintComponent(Graphics g) {
    //_cat.finest("PractisePitch paintComponent  start");
    super.paintComponent(g);
    //_cat.finest("PractisePitch paintComponent  end");
  }

  public Dimension getPreferredSize() {
    return new Dimension(_background.getIconWidth(), _background.getIconHeight());
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }


  public static void initPracticePitch(){
    JFrame pitch = new JFrame("Practise Pitch");
    BottomPanel _bottomPanel ;
    MessagePanel _messagePanel;

    pitch.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        //forcedCloseRoom(); // unsafe exit
        tryCloseRoom(); // safe exit
        _cat.finest("windowClosing(WindowEvent e)");
      }
    });

    // Agneya FIX 16
    pitch.setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());

    Dimension screenSize;
    Dimension frameSize;
    Point framePos;
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frameSize = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT + MESSAGE_HEIGHT);
    framePos = new Point( (screenSize.width - frameSize.width) / 2,
                         (screenSize.height - frameSize.height) / 2);

   SoundManager sm = new SoundManager();

    // --- Room Skin
    PitchSkin skin = new PitchSkin();

    //-- Game Event Processor
    ServerEventPlugin sep = new ServerEventPlugin();

    //--- Bottom Panel
    _bottomPanel = new BottomPanel(sep, skin, "Cric");
    _messagePanel = new MessagePanel(frameSize);

    PractisePitch pp = new PractisePitch(sep, skin, _bottomPanel, _messagePanel, pitch, "Cric");
    sep.addWatchOnTable("Cric", pp);
    pp.setVisible(true);

    PlayerModel[] pm = new PlayerModel[new GameType(GameType.FOUR_PLAYER).
                                       teamSize()];
    _cat.finest(pm[0].toString());
    CricketModel cm = new CricketModel("Cric", 123, "Cric",
                                       new GameType(GameType.FOUR_PLAYER),
                                       new GameState(GameState.OPEN),
                                       new GameType(GameType.FOUR_PLAYER).
                                       teamSize(), pm, pm, "Eagles", "Tigers",
                                       0, 0, 2, 5, 7, 9);
    pp.setCricketModel(cm);

    pitch.setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
    pitch.getContentPane().setLayout(new BorderLayout(0, 0));
    pitch.getContentPane().add(pp, BorderLayout.CENTER);
    pitch.getContentPane().add(_messagePanel, BorderLayout.SOUTH);
    pp.setLayout(new BorderLayout(0, 0));
    pp.add(_bottomPanel, BorderLayout.SOUTH);

    pitch.setResizable(false);
    pitch.setVisible(true);
  }


  public static void main(String[] args) {
    initPracticePitch();
  }

  public static void tryCloseRoom(){
    _cat.finest("Exiting");
  }

}
