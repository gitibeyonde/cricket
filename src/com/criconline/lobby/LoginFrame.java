package com.criconline.lobby;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.criconline.ClientConfig;
import com.criconline.Utils;
import com.criconline.resources.Bundle;
import com.criconline.exceptions.AuthenticationException;

public class LoginFrame
    extends JDialog
    implements ActionListener {

  static Logger _cat = Logger.getLogger(LoginFrame.class.getName());

  private LobbyFrameInterface lobbyFrameInterface;
  private JLabel labelLogin;
  private JLabel labelPassword;
  private JTextField field;
  private JPasswordField fieldPass;
  public JFrame parent;
  private JLabel status;
  private ResourceBundle bundle;

  //- begin codec's code --------------------------------------------------------------------
  private LoginSettings settings;
  private ImageIcon iconChecked;
  private ImageIcon iconUnchecked;
  private JLabel imageRemmPass;
  private JLabel labelRemmPass;
  private JButton btnLogin;

  //- end of codec's code -------------------------------------------------------------------

  private Composite composite = null;

  public LoginFrame(JFrame parent, LobbyFrameInterface lobbyFrameInterface,
                    LoginSettings settings, String title, boolean needRegister,
                    boolean isRememberPassword) {
    super( (JFrame) parent, true);
    this.parent = parent;
    this.settings = settings;
    this.lobbyFrameInterface = lobbyFrameInterface;

    bundle = Bundle.getBundle();
    parent.setVisible(false);
    parent.setEnabled(false);

    labelLogin = new JLabel(bundle.getString("login"));
    labelPassword = new JLabel(bundle.getString("password"));
    labelLogin.setHorizontalAlignment(SwingConstants.RIGHT);
    labelPassword.setHorizontalAlignment(SwingConstants.RIGHT);

    //- begin codec's code --------------------------------------------------------------------
    iconChecked = Utils.getIcon(ClientConfig.IMG_CHECK_DE);
    iconUnchecked = Utils.getIcon(ClientConfig.IMG_CHECK_EN);
    if (isRememberPassword) {
      if (settings.isRememberPassword()) {
        imageRemmPass = new JLabel(iconChecked);
      }
      else {
        imageRemmPass = new JLabel(iconUnchecked);
      }
      labelRemmPass = new JLabel(bundle.getString("remmpass"));
    }
    //- end of codec's code -------------------------------------------------------------------

    setTitle(title);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize =
        new Dimension(ClientConfig.DEFAULT_LOGIN_SCREEN_SIZE_X,
                      ClientConfig.DEFAULT_LOGIN_SCREEN_SIZE_Y);
    setBounds(new Rectangle( (screenSize.width - frameSize.width) / 2,
                            (screenSize.height - frameSize.height) / 2,
                            ClientConfig.DEFAULT_LOGIN_SCREEN_SIZE_X,
                            ClientConfig.DEFAULT_LOGIN_SCREEN_SIZE_Y));
    setResizable(false);

    field = new JTextField(15);
    field.setBackground(new Color(255, 255, 255, 80));
    field.addActionListener(this);
    getContentPane().add(field);

    fieldPass = new JPasswordField(15);
    fieldPass.setBackground(new Color(255, 255, 255, 80));
    fieldPass.addActionListener(this);
    getContentPane().add(fieldPass);

    status = new JLabel("<html>" + bundle.getString("not.logged.in") +
                        "</html>");
    status.setBackground(new Color(255, 255, 255, 80));
    getContentPane().add(status);
    status.setBounds(80, 145, 150, 60);

    Class clas = getClass();
    JButton button = createButton(Utils.getIcon( (ClientConfig.
                                                  IMG_LOGIN_LOGIN_1)),
                                  Utils.getIcon( (ClientConfig.
                                                  IMG_LOGIN_LOGIN_2)),
                                  Utils.getIcon( (ClientConfig.
                                                  IMG_LOGIN_LOGIN_3)));
    button.addActionListener(this);
    button.setLocation(10, 228);
    getContentPane().add(button);

    button = createButton(Utils.getIcon( (ClientConfig.IMG_LOGIN_CANCEL_1)),
                          Utils.getIcon( (ClientConfig.IMG_LOGIN_CANCEL_2)),
                          Utils.getIcon( (ClientConfig.IMG_LOGIN_CANCEL_3)));
    btnLogin = button;
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        System.exit(0);
      }
    });
    button.setLocation(85, 228);
    getContentPane().add(button);

    if (needRegister) {
      button = createButton(Utils.getIcon( (ClientConfig.IMG_LOGIN_REGISTER_1)),
                            Utils.getIcon( (ClientConfig.IMG_LOGIN_REGISTER_2)),
                            Utils.getIcon( (ClientConfig.IMG_LOGIN_REGISTER_3)));
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          //Utils.showUrl(Utils.getRegisterUrl());
          //_cat.debug("Register " + Utils.getRegisterUrl());
          new RegisterFrame(LoginFrame.this,LoginFrame.this.lobbyFrameInterface,LoginFrame.this.settings);
        }
      });
      button.setLocation(160, 228);
      getContentPane().add(button);
    }

    getContentPane().setLayout(null);
    ( (JPanel) getContentPane()).setOpaque(false);

    field.setBounds(80, 60, 150, 20);
    fieldPass.setBounds(80, 90, 150, 20);
    labelLogin.setBounds(16, 58, 60, 20);
    getContentPane().add(labelLogin);
    labelPassword.setBounds(16, 56 + 30, 60, 30);
    getContentPane().add(labelPassword);

    //- begin codec's code --------------------------------------------------------------------
    if (isRememberPassword) {
      imageRemmPass.setBounds(80, 120, 20, 14);
      imageRemmPass.setHorizontalAlignment(SwingConstants.LEFT);
      getContentPane().add(imageRemmPass);
      labelRemmPass.setBounds(100, 120, 130, 14);
      getContentPane().add(labelRemmPass);

      imageRemmPass.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (LoginFrame.this.settings.isRememberPassword()) {
            LoginFrame.this.settings.setRememberPassword(false);
            imageRemmPass.setIcon(iconUnchecked);
          }
          else {
            LoginFrame.this.settings.setRememberPassword(true);
            imageRemmPass.setIcon(iconChecked);
          }
        }
      });
      labelRemmPass.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (LoginFrame.this.settings.isRememberPassword()) {
            LoginFrame.this.settings.setRememberPassword(false);
            imageRemmPass.setIcon(iconUnchecked);
          }
          else {
            LoginFrame.this.settings.setRememberPassword(true);
            imageRemmPass.setIcon(iconChecked);
          }
        }
      });
    }
    else {
      settings.setRememberPassword(false);
    }
    //- end of codec's code -------------------------------------------------------------------

    field.setOpaque(false);
    fieldPass.setOpaque(false);

    final ImageIcon m_image =
        Utils.getIcon(ClientConfig.IMG_LOGIN_BACKGROUND);
    final int winc = m_image.getIconWidth();
    final int hinc = m_image.getIconHeight();
    JLabel backlabel = new JLabel("");
    if (m_image.getIconWidth() > 0 && m_image.getIconHeight() > 0) {
      backlabel = new JLabel() {
        public void paintComponent(Graphics g) {
          int w = getParent().getWidth();
          int h = getParent().getHeight();
          /**if (composite != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(composite);
          }**/
          for (int i = 0; i < h + hinc; i = i + hinc) {
            for (int j = 0; j < w + winc; j = j + winc) {
              m_image.paintIcon(this, g, j, i);
            }
          }
        }

        public Dimension getPreferredSize() {
          return new Dimension(super.getSize());
        }

        public Dimension getMinimumSize() {
          return getPreferredSize();
        }
      };
    }

    getLayeredPane().add(backlabel, new Integer(Integer.MIN_VALUE));
    backlabel.setBounds(0, 0, 5000, 5000);

    //----------- Reading login settings ---------------
    if (isRememberPassword) {
      if (settings.isRememberPassword()) {
        imageRemmPass.setIcon(iconChecked);
        field.setText(settings.getLogin());
        fieldPass.setText(settings.getPassword());
      }
      else {
        imageRemmPass.setIcon(iconUnchecked);
      }
    }

    setVisible(true);
    //Halt         setModal(true);
  }

  public void setStatusText(String s) {
    status.setText("<html>" + s + "</html>");
  }

  public void actionPerformed(ActionEvent e) {
    new Thread(new Runnable() {
      public void run() {
        if ( (field.getText().length() > 0)
            && (fieldPass.getPassword().length > 0)) {
          settings.setLogin(field.getText());
          settings.setPassword(new String(fieldPass.getPassword()));
          setStatusText(bundle.getString("connecting"));
          try {
            lobbyFrameInterface.login(
                field.getText(),
                fieldPass.getPassword());
            setStatusText(bundle.getString("loading.tables"));
            //this.setModal(false);
            LoginFrame.this.setVisible(false);
            LoginFrame.this.parent.setVisible(true);
            LoginFrame.this.parent.setEnabled(true);
            //this.parent.requestFocus();
            //this.dispose();
          }
          catch (AuthenticationException aex) {
            setStatusText(aex.getMessage());
          }
          catch (Exception ex) {
        	 ex.printStackTrace();
            String message = ex.getMessage();
            if (message == null || "".equals(message)) {
              setStatusText(bundle.getString("fail.access"));
              _cat.severe("Failed login " + ex.getMessage());
            }
            else {
              setStatusText(ex.getMessage());
            }
          }
        }
        else {
          setStatusText(bundle.getString("empty.login"));
          Toolkit.getDefaultToolkit().beep();
        }
      }
    }).start();

  }

  private JButton createButton(ImageIcon icon1, ImageIcon icon2,
                               ImageIcon icon3) {
    JButton button = new JButton(icon1);
    int w = icon1.getIconWidth();
    int h = icon1.getIconHeight();

    button.setPressedIcon(icon3);

    button.setRolloverEnabled(true);
    button.setRolloverIcon(icon2);

    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setSize(w, h);

    return button;
  }
}
