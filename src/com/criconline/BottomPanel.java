package com.criconline;

import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import com.criconline.resources.*;
import com.criconline.util.*;
import com.criconline.*;
import com.criconline.actions.*;
import com.criconline.actions.Action;
import com.criconline.models.*;
import com.criconline.server.EventGenerator;
import java.util.logging.Logger;
import com.cricket.mmog.PlayerStatus;
import com.cricket.mmog.cric.util.ActionConstants;
import com.criconline.pitch.PitchSkin;
import com.criconline.anim.Animator;
import com.criconline.anim.AnimationEvent;
import com.cricket.mmog.cric.util.MoveParams;
import com.criconline.anim.AnimationManager;
import com.criconline.exceptions.AnimationOverException;
import com.criconline.anim.Animation;
import com.criconline.pitch.PitchColorTheme;
import com.criconline.anim.AnimationConstants;
import com.criconline.anim.MoveBuffer;
import com.criconline.console.ClientCricketController;
import com.criconline.anim.Gse;
import com.criconline.anim.BallView;
import java.io.IOException;
import com.criconline.anim.BallAnimation;
import javax.swing.DesktopManager;
import java.net.URL;

public class BottomPanel extends JPanel implements ActionListener,
    /**ItemListener,**/
    KeyListener, Animator, AnimationConstants {
  static Logger _cat = Logger.getLogger(BottomPanel.class.getName());
  private PitchSkin _skin;
  private AnimationEvent _anim_event;

  private Animation _meter_timer;
  private boolean _move_made;
  private long _move_start_time, _move_accomp_time;
  private int _current_move;

  private int _steps = 0;
  MoveBuffer _mb;
  AnimationManager _am;
  //--------------------------
  //  ...... /\/ ......
  //--------------------------
  private ImageIcon m_image = null;
  private int panel_icon_width = -1;
  private int panel_icon_height = -1;


  //--------------------------
  protected ResourceBundle bundle;

  protected JButton buttonOk = null;
  protected JButton buttonCancel = null;
  private JPanel chatPanel;
  private JPanel cardDisplayPanel;
  private CardLayout cardLayout;
  public EventGenerator _eventGen;
  public CricketController _cricketController;

  public static final String CARD_BUTTON = "CARD_BUTTON";


  //public static final String CARD_INPUTDIALOG = "CARD_INPUTDIALOG";
  public static final String CARD_INFO = "CARD_INFO";
  private int flag = -1;
  private static final int CARD_BTN = 0;
  private static final int CARD_INF = 1;

  private static String getInfoLabel(String value) {
    return value + ": ";
  }

  protected JButton[] buttons = {new JButton(), new JButton()};

  protected JButton speakButton = null;
  protected JButton helpButton = null;

  protected JToggleButton muteButton = null;

  protected JTextField chatField = new JTextField(20);
  protected JTextArea textArea = null;

  protected JLabel runs = createInfoLabel("runs");
  protected JLabel balls = createInfoLabel("balls");
  protected JLabel strike_rate = createInfoLabel("bowled");

  protected JLabel infoLabel = createLabel("sit.your.player");


  //protected JLabel inputLabel = null;
  //protected JTextField inputField = new JTextField(20);
  //protected JLabel errorLabel = null;
  protected JCheckBox checkScrollList = null;
  protected Vector logVector = new Vector();

  protected JLabel handId = createLabel("hand.id");

  protected double defaultCallValue, defaultRaiseValue;
  protected double minBet = 0, maxBet = 0;
  protected double amount, amount_limit;
  protected MoveRequestAction _brq;
  protected long[] guid = null;

  public BottomPanel(EventGenerator eg, PitchSkin skin, String _gid) {
    try {
      _skin = skin;
      _eventGen = eg;
      bundle = Bundle.getBundle();
      m_image = skin.getBottonPanelBackground();
      panel_icon_width = m_image.getIconWidth();
      panel_icon_height = m_image.getIconHeight();
      _mb = MoveBuffer.instance(_gid);
      _am = AnimationManager.instance(_gid);
      panelInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    _meter_timer = _skin.getMeter("timer");
    _meter_timer.setInvalid();
    _anim_event = getDefaultAnimation();
  }

  private void panelInit() throws Exception {
    speakButton = createChatButton();
    helpButton = createHelpButton();
    muteButton = createMuteButton();

    checkScrollList = createCheckBox(Bundle.getBundle().getString("auto.scroll"));
    checkScrollList.setOpaque(false);
    checkScrollList.getModel().setSelected(true);
    checkScrollList.setBounds(0, 70, 70, 20);

    for (int i = 0; i < buttons.length; i++) {
      buttons[i] = createButton1();
    }
    //inputLabel = new JLabel(" ", SwingConstants.CENTER);
    //errorLabel = new JLabel(" ", SwingConstants.CENTER);
    Color color = new Color(196, 196, 196);
    //inputLabel.setForeground(color);

    for (int i = 0; i < buttons.length; i++) {
      buttons[i].setVisible(false);
      buttons[i].addActionListener(this);
    }

    //KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.
    //  getCurrentKeyboardFocusManager();

    // --- leftButtonPanel
    //JPanel leftButtonPanel = new JPanel(new GridLayout(4, 1));
    //leftButtonPanel.setOpaque(false);

    setLayout(null);
    //JPanel checks = new JPanel(new GridLayout(2, 3));
    //checks.setOpaque(false);

    JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
    button_panel.setOpaque(false);
    button_panel.add(infoLabel);
    for (int i = 0; i < buttons.length; i++) {
      button_panel.add(buttons[i]);
    }

    /**JPanel info = new JPanel(new GridLayout(1, 4));
         info.setOpaque(false);
         info.add(handId);
         info.add(runs);
         info.add(balls);
         info.add(strike_rate);**/

    //	--- intPanel
    JPanel intPanel = new JPanel(new BorderLayout());
    intPanel.setOpaque(false);
    intPanel.setBounds(0, 0, 350 + 50, 50);
    button_panel.setBounds(0, 0, 350 + 50, 50);
    //checks.setBounds(10, 0, 350 + 50 - 10, 50);
    //intPanel.add(checks);
    intPanel.add(button_panel);

    chatPanel = new JPanel(null); /*new FlowLayout(FlowLayout.RIGHT)*/
    chatPanel.setOpaque(false);
    chatField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        if (speakButton.isVisible()) {
          speakButton.doClick();
        }
      }
    });

    chatField.setOpaque(false);
    chatField.setBorder(BorderFactory.createLineBorder(Color.CYAN));
    chatField.setForeground(Color.white);
    chatField.setBounds(0, 0, CHAT_PANEL_WIDTH - 20, CHAT_PANEL_HEIGHT);
    chatField.setFont(Utils.chatFont);
    chatPanel.add(chatField);
    speakButton.setLocation(CHAT_PANEL_WIDTH - 20, 0);
    speakButton.addActionListener(new chatAdapter());
    chatPanel.add(speakButton);

    //
    cardLayout = new CardLayout();
    cardDisplayPanel = new JPanel(cardLayout);
    cardDisplayPanel.setOpaque(false);

    JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    infoPanel.setOpaque(false);
    infoPanel.add(infoLabel);
    cardDisplayPanel.add(infoPanel, CARD_INFO);
    cardDisplayPanel.add(intPanel, CARD_BUTTON);

    //JPanel ppp = new JPanel(null);
    //ppp.setOpaque(false);

    //	--- centerPanel
    JPanel centerPanel = new JPanel(null); /*new BorderLayout()*/
    centerPanel.setOpaque(true);
    //1	centerPanel.add(intPanel, BorderLayout.CENTER);
    cardDisplayPanel.setBounds(0, 0, SCREEN_WIDTH / 2, BOTTOM_PANEL_HEIGHT);
    chatPanel.setBounds(SCREEN_WIDTH / 2,
                        BOTTOM_PANEL_HEIGHT - CHAT_PANEL_HEIGHT,
                        CHAT_PANEL_WIDTH, CHAT_PANEL_HEIGHT);

    //info.setBounds(20, 0, 400 - 5, 20);
    muteButton.setBounds(297, 0, 16, 16);
    helpButton.setBounds(320, 0, 16, 16);
    centerPanel.add(cardDisplayPanel);
    //centerPanel.add(chatPanel);
    //centerPanel.add(info);
    centerPanel.add(muteButton);
    centerPanel.add(helpButton);

    textArea = new JTextArea();
    textArea.setForeground(Color.white);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setEditable(false);
    textArea.setFocusable(false);
    textArea.setOpaque(false);
    textArea.setBorder(null);

    JScrollPane pane = new JScrollPane(textArea,
                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    pane.getVerticalScrollBar().setUI(Utils.getRoomScrollBarUI());
    pane.getViewport().setOpaque(false);
    pane.setOpaque(false);
    pane.setBorder(null);
    pane.setBounds(SCREEN_WIDTH * 3 / 4, 0, SCREEN_WIDTH / 4,
                   BOTTOM_PANEL_HEIGHT);

    centerPanel.setBounds(0, 0, SCREEN_WIDTH * 3 / 4, BOTTOM_PANEL_HEIGHT);

    //leftButtonPanel.setOpaque(false);
    //this.add(leftButtonPanel); /*, BorderLayout.WEST*/
    centerPanel.setOpaque(false);
    this.add(centerPanel); /*, BorderLayout.CENTER*/
    pane.setOpaque(false);
    this.add(pane); /*, BorderLayout.EAST*/
    this.add(chatPanel);
    this.setOpaque(false);
    this.setBorder(null);

    //chatPanelSetVisible(false);
  }


  /** public void logItsYourTurn(String clientName) {
     appendLog(MessageFormat.format(bundle.getString("do.invited"),
                                    new Object[] {clientName}));
   }**/

//////////////// JPanel parameters ////////////////
  public void paintComponent(Graphics g) {
    int w = getWidth();
    int h = getHeight();
    for (int i = 0; i < h + panel_icon_height; i = i + panel_icon_height) {
      for (int j = 0; j < w + panel_icon_width; j = j + panel_icon_width) {
        m_image.paintIcon(this, g, j, i);
      }
    }
    if (_meter_timer != null) {
      _meter_timer.paintIcon(this, g);
    }
  }

  public void paint(JComponent c, Graphics g) {
    _cat.finest("BottomPanel: paint start");
    _cat.finest("BottomPanel: paint end");
  }

  public void refresh() {
    repaint(_meter_timer.x(), _meter_timer.y(), _meter_timer.getIconWidth(),
            _meter_timer.getIconWidth());
  }

  public Dimension getPreferredSize() {
    return new Dimension(ClientConfig.DEFAULT_BOTTON_PANEL_SIZE_W,
                         ClientConfig.DEFAULT_BOTTON_PANEL_SIZE_H);
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }


//////////////// JPanel parametrs ////////////////
  public boolean isMuting() {
    return muteButton.getModel().isSelected();
  }

  public void normalizePanel() {
    if (flag == CARD_BTN) {
      cardShowButton();
    }
    else if (flag == CARD_INF) {
      cardShowInfo();
    }
  }

  public void cardShowInfo() {
    flag = CARD_INF;
    cardLayout.show(cardDisplayPanel, CARD_INFO);
  }

  public void cardShowButton() {
    flag = CARD_BTN;
    cardLayout.show(cardDisplayPanel, CARD_BUTTON);
    chatPanelSetVisible(true);
  }

  protected boolean inviteToSeat(int pos, String team, double bankRoll) {
    chatPanelSetVisible(false);
    _cat.finest("BottomPanel invite to sit.....pos=" + pos + ", team=" + team);
    _eventGen.joinTable(_cricketController.getGameId(), pos, team, 0); // depedning on the type of game server will buy real
    return true;
  }

  private void frameToFront() {
    JFrame frame = _cricketController._selPitch;
    if (frame.getState() != Frame.NORMAL) {
      frame.setState(Frame.NORMAL);
    }
    frame.toFront();
  }

  public void moveRequest(MoveRequestAction brq) {
    _cat.finest("Bet request action = " + brq);
    _steps = 0;
    _brq = brq;
    int actionsMas = brq.getAction();
    double amt = brq.getAmount();
    int position = brq.getPosition();
    String team = brq.getTeam();
    int i = brq.getButtonPosition();

    MoveAction ba = null;

    amount = brq.getAmount();
    amount_limit = brq.getAmountLimit();
    chatPanelSetVisible(false);
    frameToFront();
    cardLayout.show(cardDisplayPanel, CARD_BUTTON);
    _current_move = 0;

    _cat.finest("Bet request action >>>>>>>>>>> = " + actionsMas);
    switch (actionsMas) {
      case ActionConstants.START:
        buttons[i].setIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_START));
        buttons[i].setPressedIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_START_R));
        buttons[i].setName(bundle.getString("start"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setVisible(true);
        buttons[i].setEnabled(true);
        startTimingMeters();
        break;
      case ActionConstants.BAT:
        buttons[i].setIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_BAT));
        buttons[i].setPressedIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_BAT_R));
        buttons[i].setName(bundle.getString("bat"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setToolTipText(bundle.getString("bat"));
        buttons[i].setVisible(true);
        buttons[i].setEnabled(false);
        startBattingMeters();
        _current_move = ActionConstants.BAT;
        break;
      case ActionConstants.BOWL:
        buttons[i].setIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_BOWL));
        buttons[i].setPressedIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_BOWL_R));
        buttons[i].setName(bundle.getString("bowl"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setToolTipText(bundle.getString("bowl"));
        buttons[i].setVisible(true);
        buttons[i].setEnabled(false);
        startBowlingMeters();
        _current_move = ActionConstants.BOWL;
        break;
      case ActionConstants.FIELD:
        buttons[i].setIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_FIELD));
        buttons[i].setPressedIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_FIELD_R));
        buttons[i].setName(bundle.getString("field"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setToolTipText(bundle.getString("field"));
        buttons[i].setVisible(true);
        buttons[i].setEnabled(false);
        startTimingMeters();
        _current_move = ActionConstants.FIELD;
        break;

      case ActionConstants.TOSS:
        buttons[i].setIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_TOSS));
        buttons[i].setPressedIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_TOSS_R));
        buttons[i].setName(bundle.getString("toss"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setToolTipText(bundle.getString("toss"));
        buttons[i].setVisible(true);
        buttons[i].setEnabled(true);
        getKeyFocus();
        startTimingMeters();
        break;
      case ActionConstants.HEAD:
        buttons[i].setIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_HEAD));
        buttons[i].setPressedIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_HEAD_R));
        buttons[i].setName(bundle.getString("head"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setToolTipText(bundle.getString("head"));
        buttons[i].setVisible(true);
        buttons[i].setEnabled(true);
        startTimingMeters();
        getKeyFocus();
        break;

      case ActionConstants.TAIL:
        buttons[i].setIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_TAIL));
        buttons[i].setPressedIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_TAIL_R));
        buttons[i].setName(bundle.getString("tail"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setToolTipText(bundle.getString("tail"));
        buttons[i].setVisible(true);
        buttons[i].setEnabled(true);
        getKeyFocus();
        startTimingMeters();
        break;
      case ActionConstants.FIELDING:
        buttons[i].setIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_FIELDING));
        buttons[i].setPressedIcon(Utils.getIcon(ClientConfig.
                                                IMG_BUTTON_FIELDING_R));
        buttons[i].setName(bundle.getString("fielding"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setToolTipText(bundle.getString("fielding"));
        buttons[i].setVisible(true);
        buttons[i].setEnabled(true);
        getKeyFocus();
        startFieldingMeters();
        break;
      case ActionConstants.BATTING:
        buttons[i].setIcon(Utils.getIcon(ClientConfig.IMG_BUTTON_BATTING));
        buttons[i].setPressedIcon(Utils.getIcon(ClientConfig.
                                                IMG_BUTTON_BATTING_R));
        buttons[i].setName(bundle.getString("batting"));
        buttons[i].setMargin(new Insets( -5, -5, -5, -5));
        buttons[i].setBorderPainted(false);
        buttons[i].setOpaque(false);
        buttons[i].setToolTipText(bundle.getString("batting"));
        buttons[i].setVisible(true);
        buttons[i].setEnabled(true);
        getKeyFocus();
        startTimingMeters();
        break;
      default:
        buttons[i].setVisible(false);
        break;
    }
    showButtons(i);
    _cat.finest("Move Request Starting ----");
    _move_made = false;
  }

  public void showButtons(int ibuts) {
    for (int i = ibuts + 1; i < buttons.length; i++) {
      buttons[i].setVisible(false);
      buttons[i].setEnabled(false);
    }
  }

  public void actionPerformed(ActionEvent action) {
    _cat.finest("Action = " + action);
    SoundManager.loopTest();
    _move_accomp_time = System.currentTimeMillis();
    _anim_event = null;
    MoveAction ba = null;
    String buttonName = ((JButton) action.getSource()).getName();

    int localPlayerNo = _cricketController.getThisPlayer()._pos;
    String localTeamNo = _cricketController.getThisPlayer()._team;
    if (buttonName.equalsIgnoreCase(bundle.getString("start"))) {
      ba = new MoveAction(ActionConstants.START, localPlayerNo, localTeamNo);
      _cat.finest("START: >>>>>>>>> START Pressed" + ba);
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("toss"))) {
      ba = new MoveAction(ActionConstants.TOSS, localPlayerNo, localTeamNo);
      _cat.finest("BAT: >>>>>>>>> TOSS Pressed ");
      LastMoveAction lm = new LastMoveAction(ActionConstants.TOSS,
                                             localPlayerNo, localTeamNo, null, true);
      _mb.actOnLastMove(TOSS, lm);

    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("head"))) {
      ba = new MoveAction(ActionConstants.HEAD, localPlayerNo, localTeamNo);
      _cat.finest("HEAD: >>>>>>>>> HEAD Pressed " + ba);
      LastMoveAction lm = new LastMoveAction(ActionConstants.HEAD,
                                             localPlayerNo, localTeamNo, null, true);
      _mb.actOnLastMove(HEAD, lm);
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("tail"))) {
      ba = new MoveAction(ActionConstants.TAIL, localPlayerNo, localTeamNo);
      _cat.finest("TAIL: >>>>>>>>> TAIL Pressed " + ba);
      LastMoveAction lm = new LastMoveAction(ActionConstants.TAIL,
                                             localPlayerNo, localTeamNo, null, true);
      _mb.actOnLastMove(TAIL, lm);
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("batting"))) {
      ba = new MoveAction(ActionConstants.BATTING, localPlayerNo, localTeamNo);
      _cat.finest("BATTING: >>>>>>>>> BATTING Pressed " + ba);
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("fielding"))) {
      ba = new MoveAction(ActionConstants.FIELDING, localPlayerNo, localTeamNo);
      _cat.finest("FIELDING: >>>>>>>>> FIELDING Pressed " + ba);
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("bat"))) {
      ClientPlayerController ccc = _cricketController._cricketModel.getPlayer(
          localTeamNo, localPlayerNo);
      BatsmanPlayerView pv = (BatsmanPlayerView) ccc._playerView;
      // if (pv._bats_state != SELECT_SHOT) {
      //  return;
      //}

      MoveParams mp = MoveParams.generate(MoveParams.BATS);
      mp.setKeyMap(pv._km);
      mp._timing = _meter_timer.getRemainingFrame();
      mp._abs_timing = (int) (_move_accomp_time - _move_start_time);
      mp._spin = pv.spin();
      mp._direction = Math.atan2(pv._km.dirX(), pv._km.dirY());
      mp._direction = mp._direction < 0 ? mp._direction + 6.285 : mp._direction;
      _cat.info("X=" + pv._km.dirX() + ",  Y=" + pv._km.dirY() + ", Direction=" +
                mp._direction);
      mp._force = Math.abs(pv._km.dirX()) / 3 + Math.abs(pv._km.dirY()) / 2 + 1; // at a time 50 keys can be pressed
      _cat.finest(" BAT: >>>>>>>>>" + mp);

      ba = new MoveAction(ActionConstants.BAT, localPlayerNo, localTeamNo, mp);
      _mb.setMoveMade(BATS, ba);
      LastMoveAction lm = new LastMoveAction(ActionConstants.BAT, localPlayerNo,
                                             localTeamNo, mp, true);
      _mb.actOnLastMove(BATS, lm);
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("bowl"))) {
      MoveParams mp = MoveParams.generate(MoveParams.BOWL);
      ClientPlayerController ccc = _cricketController._cricketModel.getPlayer(
          localTeamNo, localPlayerNo);

      BowlerPlayerView pv = (BowlerPlayerView) ccc._playerView;
      if (pv._move_state != FD) {
        return;
      }

      BallView bv = _cricketController._cricketModel._ball;
      _cat.finest(bv._km.toString());
      bv._bowl_state = NEUTRAL;
      mp.setKeyMap(bv._km);
      mp._timing = _meter_timer.getRemainingFrame();
      mp._abs_timing = (int) (_move_accomp_time - _move_start_time);
      _cat.finest("BOWL: >>>>>>>>>" + mp);
      ba = new MoveAction(ActionConstants.BOWL, localPlayerNo, localTeamNo, mp);
      LastMoveAction lm = new LastMoveAction(ActionConstants.BOWL,
                                             localPlayerNo, localTeamNo, mp, true);
      _mb.actOnLastMove(BOWL, lm);
    }
    else if (buttonName.equalsIgnoreCase(bundle.getString("field"))) {
      MoveParams mp = MoveParams.generate(MoveParams.FIELD);
      ClientPlayerController ccc = _cricketController._cricketModel.getPlayer(
          localTeamNo, localPlayerNo);
      /** if (ccc._playerView instanceof FielderPlayerView) {
         FielderPlayerView pv = (FielderPlayerView) ccc._playerView;
         if (pv._move_state != FD) {
           return;
         }
       }
       else if (ccc._playerView instanceof BowlerPlayerView) {
         BowlerPlayerView pv = (BowlerPlayerView) ccc._playerView;
         if (pv._move_state != FD) {
           return;
         }
       }**/

      BallView bv = _cricketController._cricketModel._ball;
      BallAnimation ball = bv._ball;
      //mp.setX(ball.x());
      //mp.setY(ball.y());
      mp.setX(ccc._playerView._player.x() + 40);
      mp.setY(ccc._playerView._player.y());
      if (action.getActionCommand().equals("S")) {
        mp.setFieldAction(mp.BLOCK);
      }
      else if (action.getActionCommand().equals("C")) {
        mp.setFieldAction(mp.CATCH);
      }
      else {
        mp.setFieldAction(mp.BLOCK);
      }
      ba = new MoveAction(ActionConstants.FIELD, localPlayerNo, localTeamNo, mp);

      LastMoveAction lm = new LastMoveAction(ActionConstants.FIELD,
                                             localPlayerNo, localTeamNo, mp, true);
      _cat.finest("LMA=" + lm);
      _mb.actOnLastMove(FIELD, lm);
      _cat.finest("FIELD: >>>>>>>>> FIELD Pressed " + ba);
    }
    if (ba != null) {
      try {
        sendToServer(ba);
      }
      catch (IOException e) {
        // DISCONNECTED FRM SERVER
        JOptionPane.showMessageDialog(this, bundle.getString("server.error"),
                                      bundle.getString("error"),
                                      JOptionPane.ERROR_MESSAGE);

      }
    }

    cardShowInfo();
    chatPanelSetVisible(true);
    _move_made = true;
    _cat.finest("Removing keyboard focus-----------");
    removeKeyListener(this);
  }

  protected void chatPanelSetVisible(boolean chatVisible) {
    // chatPane.setVisible replaced by chatPaneSetVisible
    chatPanel.setVisible(chatVisible);
  }

  public void sendToServer(MoveAction ba) throws IOException {
    if (ba != null) {
      _eventGen.sendToServer(_cricketController.getGameId(), ba);
    }
    _move_made = true;
  }

  public void setInfoStrip(Action action, double atTable) {
    InfoAction ia = (InfoAction) action;
    runs.setText(getInfoLabel(bundle.getString("runs")) + ia.getBet());
    balls.setText(getInfoLabel(bundle.getString("balls")) + ia.getPot());
    strike_rate.setText(getInfoLabel(bundle.getString("strike.rate")) +
                        ia.getRake());
  }

  int sepCount = 0;
  java.util.logging.Logger logger;

  public void appendLog(String str) {
    _cat.finest(str + "\n");
    textAreaAppend(str + "\n", Utils.smallChatFont);
  }

  public void appendChat(String str) {
    logVector.add("c>>>" + str + "\n");
    textAreaAppend(">" + str + "\n", Utils.chatFont);
  }

  public void appendUserChat(ChatAction ca) {
    textAreaAppend(ca.getSendersName() + ">" + ca.getChatString() + "\n",
                   Utils.chatFont);
  }

  public void appendUserChat(LastMoveAction ca) {
    /**MoveParams mp = ca.getMoveParams();
     ClientPlayerController ccc = _cricketController._cricketModel.getPlayer(ca.
        getTeam(), ca.getPosition());
         textAreaAppend(ccc.getPlayerName() + " " +
                   LastMoveAction.actionToString(ca.getId()) + "\n",
                   Utils.chatFont);**/
    /** + " {" +
                 mp._timing + ", " + mp._direction + " ," + mp._spin + " }\n",
                                    Utils.chatFont);**/
  }

  private void textAreaAppend(String text, Font f) {
    textArea.setFont(f);
    if (checkScrollList.getModel().isSelected()) {
      textArea.append(text);
      textArea.setCaretPosition(textArea.getText().length());
    }
    else {
      int carretPos = textArea.getCaretPosition();
      textArea.append(text);
      textArea.setCaretPosition(carretPos);
    }
  }


  /**
   *
   */
  public void leavePlease() {
    _cat.finest("leavePlease()");
//    if (!leftButtonLeave.getModel().isSelected()) {
//      leftButtonLeave.doClick();
//    }
  }

  public void updatePlayerState(PlayerStatus state) {
    _cat.info("updatePlayerState(int " + state);
    setInfoLabelText(state.toString());

    SoundManager.loopTest();
  }

  protected void leaveIfNeeded() {
    // if (leftButtonLeave.getModel().isSelected()) {
    _cricketController.tryExit();
    // }
  }

  protected JButton createButton(String label) {
    try {
      JButton button = new MyJButton(label,
                                     Utils.getIcon(ClientConfig.IMG_BUTTON_EN),
                                     Utils.getIcon(ClientConfig.IMG_BUTTON_PR), null,
                                     Utils.getIcon(ClientConfig.IMG_BUTTON_DE));
      button.addKeyListener(this);
      return button;
    }
    catch (Exception e) {
      _cat.warning("BottonPanel.createButton" + e);
    }
    return null;
  }

  protected JButton createChatButton() {
    try {
      JButton button = new MyJButton("",
                                     Utils.getIcon(ClientConfig.IMG_SPEAK_BUTTON),
                                     Utils.getIcon(ClientConfig.
          IMG_SPEAK_BUTTON), null, Utils.getIcon(ClientConfig.IMG_SPEAK_BUTTON));
      button.addKeyListener(this);
      return button;
    }
    catch (Exception e) {
      _cat.warning("BottonPanel.createButton"+ e);
    }
    return null;
  }

  protected JButton createHelpButton() {
    try {
      JButton button = new MyJButton("",
                                     Utils.getIcon(ClientConfig.IMG_HELP_BUTTON),
                                     Utils.getIcon(ClientConfig.IMG_HELP_BUTTON), null,
                                     Utils.getIcon(ClientConfig.IMG_HELP_BUTTON));
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          StringBuffer help = new StringBuffer();
          help.append(
              "<body><table width=800  bgcolor='#446530'><font color='#FFFF00' size=5>");
          help.append("<tr><td align='justify' valign='top' class='bodyText' scope='row'><p align='left'>");
          help.append("You will see the game running if the two teams present on the pitch have at least one player each. The vacant places are shown as a player outline.");
          help.append("You can click in the outline to join the respective team. Once you have joined a team you will see yourself waiting near the boundary, till the innings are");
          help.append(" over. You will see the action in the next match. While playing your name and your points will be displayed near your avatar on screen. You can move the mouse curser over the");
          help.append("avatars to get more details about them like there scores, points and city. The sounds during move will guide you in playing your move well so listen ");
          help.append("carefully to the timer sound.</p>");
          help.append("<p align='left'><span style='font-family: Times New Roman'>The</span><span style='font-family: Times New Roman'>game is simple to play. </span></p>");
          help.append("<p align='left'><span style='font-family: Times New Roman'><u><b>Batting:</b></u>&nbsp; Look for the pitch point, the position and direction of the arrow will tell you where the ball is going to pitch and how it is going ");
          help.append("to spin. Use the arrows during first part of the timer to position yourself accordingly. Next part of the timer use arrow keys for shot selection and press &quot;S&quot; to initiate the shot.</span></p>");
          help.append("<p align='left'><span style='font-family: Times New Roman'><u><b>Bowling:</b></u> Use arrow key initially to set the position of the pitch point. Next you can select the spin of the ball. A &quot;S&quot; will start the bowling movement.</span></p>");
          help.append("<p align='left'><span style='font-family: Times New Roman'><u><b>Fielding:</b></u> Use arrow keys to go close to the ball. Press &quot;S&quot; or &quot;C&quot; to field or catch the ball respectively.</span></p>");
          help.append("<p align='left'><span style='font-family: Times New Roman'>For the above moves mouse button is inactive. For Toss, Fielding and Batting choices you can use the mouse or can use the &quot;S&quot; and &quot;C&quot; keys. &quot;S&quot; and &quot;C&quot; will ");
          help.append("act on the first or the second button respectively.</span></p><p>&nbsp;</p></td> </tr></font></table></body>");

          Thread t = new Thread(new MessagePopup(help.toString()));
          t.start();

        }
      });
      return button;
    }
    catch (Exception e) {
      _cat.warning("BottonPanel.createhelpButton"+ e);
    }
    return null;
  }

  protected MyJToggleButton createMuteButton() {
    try {
      MyJToggleButton button = new MyJToggleButton("",
          Utils.getIcon(ClientConfig.IMG_BUTTON_MUTE_ON),
          Utils.getIcon(ClientConfig.IMG_BUTTON_MUTE_OFF), null, null);
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (isMuting()) {
            SoundManager.stopBackground();
          }
          else {
            SoundManager.startBackground();
          }
        }
      });
      return button;
    }
    catch (Exception e) {
      _cat.warning("BottonPanel.createButton"+ e);
    }
    return null;
  }

  protected JButton createButton1() {
    try {
      JButton button = new JButton();
      button.setOpaque(false);
      button.addKeyListener(this);
      return button;
    }
    catch (Exception e) {
      _cat.warning("BottonPanel.createButton"+ e);
    }
    return null;
  }

  protected JToggleButton createToggleButton(String label) {
    try {
      JToggleButton button = new MyJToggleButton(label,
                                                 Utils.getIcon(ClientConfig.
          IMG_BUTTON_EN), Utils.getIcon(ClientConfig.IMG_BUTTON_PR), null,
                                                 Utils.getIcon(ClientConfig.
          IMG_BUTTON_DE));
      button.addKeyListener(this);
      return button;
    }
    catch (Exception e) {
      _cat.warning("BottonPanel.createToggleButton"+ e);
    }
    return null;
  }

  private JCheckBox createCheckBox(String label) {
    try {
      JCheckBox checkBox = new MyJCheckBox(label,
                                           Utils.getIcon(ClientConfig.IMG_CHECK_EN),
                                           Utils.getIcon(ClientConfig.
          IMG_CHECK_DE));
      checkBox.addKeyListener(this);
      checkBox.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
          JCheckBox box = (JCheckBox) e.getSource();
          if (box.isSelected()) {

          }
          else {

          }
        }
      });
      return checkBox;
    }
    catch (Exception e) {
      _cat.warning("BottonPanel.createToggleButton"+ e);
    }
    return null;
  }

  private static JLabel createLabel(String boundleLabel) {
    ResourceBundle bundle = Bundle.getBundle();
    String text = bundle.getString(boundleLabel);
    JLabel label = new JLabel(text);
    if (text.equalsIgnoreCase(bundle.getString("sit.your.player"))) {
      label.setForeground(Color.YELLOW);
    }
    else {
      label.setForeground(new Color(196, 196, 196));
    }
    return label;
  }

  private static JLabel createInfoLabel(String boundleLabel) {
    JLabel label = new JLabel(getInfoLabel(Bundle.getBundle().getString(
        boundleLabel)));
    label.setForeground(new Color(196, 196, 196));
    return label;
  }

  protected void updateLookAndFeel() {
    try {
      javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new
          PitchColorTheme());
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    }
    catch (Exception ex) {
      _cat.warning("Failed loading L&F: (BottonPanel)"+ ex);
    }
  }

  protected void userSendChat(int playerNo, String team, String name) {
    ChatAction ca = new ChatAction(playerNo, team, name, chatField.getText());
    _cat.finest(ca.toString());
    try {
      _eventGen.sendToServer(_cricketController.getGameId(), ca);
    }
    catch (IOException e) {
      // DISCONNECTED FRM SERVER
      JOptionPane.showMessageDialog(this, bundle.getString("server.error"),
                                    bundle.getString("error"),
                                    JOptionPane.ERROR_MESSAGE);

    }

  }

  class chatAdapter implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Object o = e.getSource();
      if (!(o instanceof JButton)) {
        return;
      }
      if (chatField.getText().trim().length() < 2) {
        return;
      }
      JButton button = (JButton) o;
      if (button == speakButton) {
        /**int localPlayerNo = _cricketController.getThisPlayer()._pos;
                 String localTeamNo = _cricketController.getThisPlayer()._team;
                 String name = _cricketController.getThisPlayer()._name;**/
        _cat.finest(_cricketController.getThisPlayer().toString());
        //userSendChat(localPlayerNo, localTeamNo, name);
        userSendChat(0, "1", "hero");
        chatField.setText("");
        button.requestFocusInWindow();
      }
    }
  }

  public void keyTyped(KeyEvent ke) {
    _cat.finest("Key pressed " + ke.getKeyCode());
    int ch = ke.getKeyCode();
    if (ch == KeyEvent.VK_S && buttons[0].isVisible()) {
      buttons[0].doClick();
    }
    else if (ch == KeyEvent.VK_C && buttons[1].isVisible()) {
      buttons[1].doClick();
    }
    else if (ch == KeyEvent.VK_V && buttons[2].isVisible()) {
      buttons[2].doClick();
    }
    else if (ch == KeyEvent.VK_B && buttons[3].isVisible()) {
      buttons[3].doClick();
    }
    else if (!buttons[0].isVisible()) {
      chatField.requestFocusInWindow();
      chatField.setForeground(Color.yellow);
      chatField.setText(chatField.getText() + ch);
      chatField.setForeground(Color.white);
    }
  }

  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
  }


///////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////// requestes //////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
  abstract class Request {
    abstract boolean ok();

    abstract void cancel();
  }

  Request request;

  private void setInfoLabelText(String text) {
    infoLabel.setText(text);
    if (text.equalsIgnoreCase(bundle.getString("sit.your.player"))) {
      infoLabel.setForeground(Color.YELLOW);
    }
    else {
      infoLabel.setForeground(new Color(196, 196, 196));
    }
  }

  protected void userPressCancel() {
    if (request != null) {
      request.cancel();
    }
    //leftButtonSit.requestFocusInWindow();
  }


  /**
   *
   */
  protected void userPressOk() {
    //	if (request != null && request.ok())
    //		cardShowInfo();
    if (request != null) {
      request.ok();
    }
    //leftButtonSit.requestFocusInWindow();
  }

  ArrayList requests = new ArrayList();


  public int getZOrder() {
    return _meter_timer.getZOrder();
  }

  public Animation getAnimation() {
    return _meter_timer;
  }

  public void collision(Animation a) {
    //collision detected. stop the ball
    throw new IllegalStateException("Bottom Panel....cannot collide");
  }

  public void animate(AnimationEvent ev, int frame) {
    if (ev._type == PitchSkin.BOTTOMPANEL) {
      _anim_event = ev;
      _cat.finest("ANIMATE BOTTOM " + ev);
      int pos = _cricketController.getThisPlayer().getPosition();
      String team = _cricketController.getThisPlayer().getTeam();
      if (_anim_event._action.equals("ball_over")) {
        MoveAction ba = new MoveAction(ActionConstants.END, -1, "");
        //sendToServer(ba);_bowl_move
      }
      else if (_anim_event._action.equals("bat_move")) {
        MoveRequestAction mr = new MoveRequestAction(_cricketController.
            getGameId(), pos, team, ActionConstants.BAT, 0, 0);
        moveRequest(mr);
      }
      else if (_anim_event._action.equals("bowl_move")) {
        MoveRequestAction mr = new MoveRequestAction(_cricketController.
            getGameId(), pos, team, ActionConstants.BOWL, 0, 0);
        moveRequest(mr);
      }
      else if (_anim_event._action.equals("field_move")) {
        MoveRequestAction mr = new MoveRequestAction(_cricketController.
            getGameId(), pos, team, ActionConstants.FIELD, 0, 0);
        moveRequest(mr);
      }
      else if (_anim_event._action.equals("toss")) {
        MoveRequestAction mr = new MoveRequestAction(_cricketController.
            getGameId(), pos, team, ActionConstants.TOSS, 0, 0);
        moveRequest(mr);
      }
    }
  }

  public int delay() {
    return (_meter_timer._delay);
  }

  public void run() {
    timers(_steps++);

    if (_anim_event == null) {
      return;
    }
    Rectangle r1 = null;
    try {
      r1 = _meter_timer.getRealCoords();
      _meter_timer.update();
      repaint(r1);
      refresh();
    }
    catch (AnimationOverException e) {
      _cat.info("Animation Complete " + _anim_event);
      _anim_event = null;
    }
    catch (Exception e) {
      _cat.warning("Animation Complete"+e);
      animate(getDefaultAnimation(), 0);
    }
  }

  public AnimationEvent getDefaultAnimation() {
    return null;
  }

  public String getType() {
    return "bottompanel";
  }

  public void shift(int x, int y, int z) {
  }

  public void startTimingMeters() {
    _meter_timer.setValid();
    _meter_timer.reset();
    _meter_timer._delay = _meter_timer._init_delay * 2;
    refresh();
    _move_made = false;
  }


  public void getKeyFocus() {
    _cat.finest("Setting keyboard focus-----------");
    addKeyListener(this);
    setFocusable(true);
    requestFocus();
    requestFocusInWindow();
  }


  public void startFieldingMeters() {
    _meter_timer.setValid();
    _meter_timer.reset();
    _meter_timer._delay = _meter_timer._init_delay * 2;
    refresh();
    _move_made = false;
  }

  public void startBattingMeters() {
    _meter_timer.setValid();
    _meter_timer.reset();
    _meter_timer._delay = _meter_timer._init_delay;
    refresh();
    _move_made = false;
  }

  public void startBowlingMeters() {
    _meter_timer.setValid();
    _meter_timer.reset();
    _meter_timer._delay = _meter_timer._init_delay;
    refresh();
    _move_made = false;
  }

  public void stopMeters() {
    _cat.finest("Setting meters invalid");
    if (_meter_timer != null) {
      _meter_timer.setInvalid();
      Rectangle r1 = _meter_timer.getRealCoords();
      repaint(r1);
    }
    refresh();
  }

  public void timers(int frame) {
    //_cat.finest("Timers enabled = " + _move_made + " ," + frame);
    if (frame == 0 && !_move_made) {
      _cricketController.tryPlayEffect(SoundManager.PIG);
    }
    //_cat.finest("Bet request action " + _brq);
    int actions = _brq.getAction();
    int position = _brq.getPosition();
    String team = _brq.getTeam();
    PlayerView pv = _cricketController.getPlayer(team, position)._playerView;
    Rectangle fr = pv.getBounds();

    if (frame == 10 && !_move_made) {
      _cat.finest("Timer 300 fired");
      _move_start_time = System.currentTimeMillis();
      _cricketController.tryPlayEffect(SoundManager.START_MOVE);
    }
    else if (frame == 20 && !_move_made) {
      _cat.finest("Timer 400 fired " + _current_move);
      if (_current_move == ActionConstants.BAT) {
        _am.update(null, Gse.BATS_MOVE_IN_HIT_MODE);
      }
      else if (_current_move == ActionConstants.BOWL) {
        _am.update(null, Gse.BOWLER_MOVE_BEGIN);
      }
      _cricketController.tryPlayEffectRep(SoundManager.PIG_PIG_PIG);
      //logItsYourTurn(_cricketController._cricketModel.getThisPlayerName());
    }
    else if (frame == 30 && !_move_made) {
      _cat.finest("Timer 800 fired " + actions);
      _cricketController.tryPlayEffect(SoundManager.MISSED_MOVE);
      MoveParams mp = null;
      if (actions == ActionConstants.BAT) {
        mp = MoveParams.generate(MoveParams.BATS);
        mp._direction = Math.PI;
        mp._timing = 0;
        LastMoveAction lm = new LastMoveAction(ActionConstants.BAT, position,
                                               team, mp, true);
        _mb.actOnLastMove(BATS, lm);
      }
      else if (actions == ActionConstants.BOWL) {

        mp = MoveParams.generate(MoveParams.BOWL);

        BallView bv = _cricketController._cricketModel._ball;
        _cat.finest(bv._km.toString());
        bv._bowl_state = NEUTRAL;
        mp.setKeyMap(bv._km);
        mp._timing = _meter_timer.getRemainingFrame();
        mp._abs_timing = (int) (_move_accomp_time - _move_start_time);
        _cat.finest("MISSED BOWL: >>>>>>>>>" + mp);
        LastMoveAction lm = new LastMoveAction(ActionConstants.BAT, position,
                                               team, mp, true);
        _mb.actOnLastMove(BOWL, lm);
      }
      else if (actions == ActionConstants.FIELD) {
        mp = MoveParams.generate(MoveParams.FIELD);
        mp.setX(fr.x + 50);
        mp.setY(fr.y + 100);
        mp.setFieldAction(54);
        LastMoveAction lm = new LastMoveAction(ActionConstants.FIELD, position,
                                               team, mp, true);
        _mb.actOnLastMove(FIELD, lm);
      }
      else if (actions == ActionConstants.TOSS) {
        LastMoveAction lm = new LastMoveAction(ActionConstants.TOSS, position,
                                               team, null, true);
        _mb.actOnLastMove(TOSS, lm);
      }
      else if (actions == HEAD) {
        LastMoveAction lm = new LastMoveAction(ActionConstants.HEAD, position,
                                               team, null, true);
        _mb.actOnLastMove(HEAD, lm);
      }
      else if (actions == ActionConstants.TAIL) {
        LastMoveAction lm = new LastMoveAction(ActionConstants.TAIL, position,
                                               team, null, true);
        _mb.actOnLastMove(TAIL, lm);
      }
      else if (actions == ActionConstants.FIELDING) {
        LastMoveAction lm = new LastMoveAction(ActionConstants.FIELDING,
                                               position, team, null, true);
        _mb.actOnLastMove(FIELDING, lm);
      }
      else if (actions == ActionConstants.BATTING) {
        LastMoveAction lm = new LastMoveAction(ActionConstants.BATTING,
                                               position, team, null, true);
        _mb.actOnLastMove(BATTING, lm);
      }
      MoveAction ba = new MoveAction(actions, position, team, mp);
      _cat.finest("timer3 :: actionPerformed() " + actions);
      _mb.setMoveMade(actions, ba);
      try {
        sendToServer(ba);
      }
      catch (IOException e) {
        // DISCONNECTED FRM SERVER
        JOptionPane.showMessageDialog(this, bundle.getString("server.error"),
                                      bundle.getString("error"),
                                      JOptionPane.ERROR_MESSAGE);

      }

      setInfoLabelText("");
      cardShowInfo();
      chatPanelSetVisible(true);
      _cat.finest("timer3 :: JOptionPane.showMessageDialog)");
      //new MessagePopup(Bundle.getBundle().getString("timeout"));
    }
    else if (frame == 40) {
      _cat.finest("Timer 1200 fired action=" + actions);
      stopMeters();
      SoundManager.playEffect(SoundManager.WAKE_UP);
      if (actions == ActionConstants.FIELD) {
        if (pv instanceof FielderPlayerView) {
          ((FielderPlayerView) pv)._move_state = DT; ((FielderPlayerView) pv).
              animate(new AnimationEvent("default", FIELD, 0), 0);
        }
        else if (pv instanceof BowlerPlayerView) {
          ((BowlerPlayerView) pv)._move_state = DT; ((BowlerPlayerView) pv).
              animate(new AnimationEvent("default", FIELD, 0), 0);
        }
        else {
          _cat.finest("Not a fielder...." + pv);
        }
      }
    }
  }
}
