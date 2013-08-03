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
import java.awt.Component;
import java.awt.Dialog;
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
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.criconline.ClientConfig;
import com.criconline.Utils;
import com.criconline.resources.Bundle;
import com.criconline.exceptions.AuthenticationException;
/**
 * <p>Title: Register User</p>
 *
 * <p>Description: Register User</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Agneya</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RegisterFrame extends JDialog implements ActionListener{

    static Logger _cat = Logger.getLogger(RegisterFrame.class.getName());

    private LobbyFrameInterface lobbyFrameInterface;
    private JLabel labelUsername;
    private JLabel labelPassword;
    private JLabel labelEmail;
    private JLabel labelConfirmPass;
    private JLabel labelGender;
    private JLabel labelMale;
    private JLabel labelFemale;
    private JLabel labelBonuscode;
    private JLabel labelHearfrom;
    private JLabel imageCheckM;
    private JLabel imageCheckF;

    private JTextField field;
    private JTextField email;
    private JTextField bonuscode;
    private JPasswordField fieldPass;
    private JPasswordField confirmFieldPass;
    private JLabel status;
    private JComboBox hearChoices;
    private JButton btnRegister;
    private ImageIcon iconChecked;
    private ImageIcon iconUnchecked;

    private Composite composite = null;
    private ResourceBundle bundle;
    private LoginFrame parent;
    private LoginSettings settings;
    private boolean isMale;


    public RegisterFrame(LoginFrame parent,LobbyFrameInterface lobbyFrameInterface,LoginSettings settings) {

        super(parent, true);
        isMale = true;
        this.parent = parent;
        this.settings = settings;
        this.lobbyFrameInterface = lobbyFrameInterface;

        bundle = Bundle.getBundle();
        parent.setVisible(false);
        parent.setEnabled(false);

        labelUsername = new JLabel(bundle.getString("prompt.username"));
        labelPassword = new JLabel(bundle.getString("prompt.password"));
        labelConfirmPass = new JLabel(bundle.getString("prompt.password2"));
        labelEmail = new JLabel(bundle.getString("prompt.email"));
        labelGender = new JLabel(bundle.getString("prompt.gender"));
        labelMale = new JLabel(bundle.getString("prompt.male"));
        labelFemale = new JLabel(bundle.getString("prompt.female"));
        labelBonuscode = new JLabel(bundle.getString("prompt.bonus"));
        labelHearfrom = new JLabel(bundle.getString("prompt.how_hear"));

        iconChecked = Utils.getIcon(ClientConfig.IMG_CHECK_DE);
        iconUnchecked = Utils.getIcon(ClientConfig.IMG_CHECK_EN);

        imageCheckM = new JLabel(iconChecked);
        imageCheckF = new JLabel(iconUnchecked);

        imageCheckM.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (isMale) {
                    isMale=false;
                    imageCheckM.setIcon(iconUnchecked);
                    imageCheckF.setIcon(iconChecked);
                }
                else {
                    isMale = true;
                    imageCheckM.setIcon(iconChecked);
                    imageCheckF.setIcon(iconUnchecked);
                }
            }
        });
        imageCheckF.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (isMale) {
                    isMale=false;
                    imageCheckM.setIcon(iconUnchecked);
                    imageCheckF.setIcon(iconChecked);
                }
                else {
                    isMale = true;
                    imageCheckM.setIcon(iconChecked);
                    imageCheckF.setIcon(iconUnchecked);
                }
            }
        });


        labelUsername.setHorizontalAlignment(SwingConstants.LEFT);
        labelPassword.setHorizontalAlignment(SwingConstants.LEFT);
        labelConfirmPass.setHorizontalAlignment(SwingConstants.LEFT);
        labelEmail.setHorizontalAlignment(SwingConstants.LEFT);
        labelGender.setHorizontalAlignment(SwingConstants.LEFT);
        imageCheckM.setHorizontalAlignment(SwingConstants.CENTER);
        labelMale.setHorizontalAlignment(SwingConstants.LEFT);
        imageCheckF.setHorizontalAlignment(SwingConstants.CENTER);
        labelFemale.setHorizontalAlignment(SwingConstants.LEFT);
        labelBonuscode.setHorizontalAlignment(SwingConstants.LEFT);
        labelHearfrom.setHorizontalAlignment(SwingConstants.LEFT);

        String hChoices[] = {bundle.getString("prompt.select"),bundle.getString("prompt.how_hear1"),
                             bundle.getString("prompt.how_hear2"),bundle.getString("prompt.how_hear3"),
                             bundle.getString("prompt.how_hear4"),bundle.getString("prompt.how_hear5"),
                             bundle.getString("prompt.how_hear6"),bundle.getString("prompt.how_hear7"),
                             bundle.getString("prompt.how_hear8"),bundle.getString("prompt.how_hear9")};

        hearChoices = new JComboBox(hChoices);
        hearChoices.setMaximumRowCount(10);
        hearChoices.setBackground(new Color(165,125,90));
        hearChoices.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus)
                {
                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if(isSelected)
                        c.setBackground(new Color(124,82,54));

                    return c;
                }
            });


        setTitle("User Registration");
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                RegisterFrame.this.setVisible(false);
                RegisterFrame.this.parent.setVisible(true);
                RegisterFrame.this.parent.setEnabled(true);
            }
        });

        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize =
                new Dimension(ClientConfig.DEFAULT_REGISTER_SCREEN_SIZE_X,
                              ClientConfig.DEFAULT_REGISTER_SCREEN_SIZE_Y);
        setBounds(new Rectangle((screenSize.width - frameSize.width) / 2,
                                (screenSize.height - frameSize.height) / 2,
                                ClientConfig.DEFAULT_REGISTER_SCREEN_SIZE_X,
                                ClientConfig.DEFAULT_REGISTER_SCREEN_SIZE_Y));
        setResizable(false);

        field = new JTextField(15);
        field.setBackground(new Color(255, 255, 255, 80));
        field.addActionListener(this);
        getContentPane().add(field);

        fieldPass = new JPasswordField(15);
        fieldPass.setBackground(new Color(255, 255, 255, 80));
        fieldPass.addActionListener(this);
        getContentPane().add(fieldPass);

        confirmFieldPass = new JPasswordField(15);
        confirmFieldPass.setBackground(new Color(255, 255, 255, 80));
        confirmFieldPass.addActionListener(this);
        getContentPane().add(confirmFieldPass);

        email = new JTextField(15);
        email.setBackground(new Color(255, 255, 255, 80));
        email.addActionListener(this);
        getContentPane().add(email);

        bonuscode = new JTextField(15);
        bonuscode.setBackground(new Color(255, 255, 255, 80));
        getContentPane().add(bonuscode);

        status = new JLabel("");
        status.setBackground(new Color(255, 255, 255, 80));
        getContentPane().add(status);
        status.setBounds(25, 106, 300, 20);

        JButton button = createButton(Utils.getIcon((ClientConfig.
                IMG_LOGIN_REGISTER_1)),
                                      Utils.getIcon((ClientConfig.
                IMG_LOGIN_REGISTER_2)),
                                      Utils.getIcon((ClientConfig.
                IMG_LOGIN_REGISTER_3)));
        button.addActionListener(this);
        button.setLocation(89, 446);
        getContentPane().add(button);

        button = createButton(Utils.getIcon( (ClientConfig.IMG_LOGIN_CANCEL_1)),
                          Utils.getIcon( (ClientConfig.IMG_LOGIN_CANCEL_2)),
                          Utils.getIcon( (ClientConfig.IMG_LOGIN_CANCEL_3)));

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                RegisterFrame.this.setVisible(false);
                RegisterFrame.this.parent.setVisible(true);
                RegisterFrame.this.parent.setEnabled(true);
            }
        });
        button.setLocation(179, 446);
        getContentPane().add(button);


        getContentPane().setLayout(null);
        ((JPanel) getContentPane()).setOpaque(false);

        labelUsername.setBounds(25, 135, 60, 20);
        field.setBounds(150, 135, 150, 20);
        labelPassword.setBounds(25, 170, 60, 30);
        fieldPass.setBounds(150, 170, 150, 20);
        labelConfirmPass.setBounds(25, 205, 150, 30);
        confirmFieldPass.setBounds(150, 205, 150, 20);
        labelEmail.setBounds(25, 240, 150, 20);
        email.setBounds(150, 240, 150, 20);
        labelGender.setBounds(25, 275, 60, 20);
        imageCheckM.setBounds(140, 279, 20, 14);
        labelMale.setBounds(163, 275, 60, 20);
        imageCheckF.setBounds(206, 279, 20, 14);
        labelFemale.setBounds(229, 275, 100, 20);
        labelBonuscode.setBounds(25, 310, 150, 20);
        bonuscode.setBounds(150, 310, 150, 20);
        labelHearfrom.setBounds(25, 340, 200, 20);
        hearChoices.setBounds(150, 360, 150, 20);

        getContentPane().add(labelUsername);
        getContentPane().add(labelPassword);
        getContentPane().add(labelConfirmPass);
        getContentPane().add(labelEmail);
        getContentPane().add(labelGender);
        getContentPane().add(imageCheckM);
        getContentPane().add(labelMale);
        getContentPane().add(imageCheckF);
        getContentPane().add(labelFemale);
        getContentPane().add(labelPassword);
        getContentPane().add(labelUsername);
        getContentPane().add(labelBonuscode);
        getContentPane().add(labelHearfrom);
        getContentPane().add(hearChoices);

        field.setOpaque(false);
        fieldPass.setOpaque(false);
        confirmFieldPass.setOpaque(false);
        email.setOpaque(false);
        bonuscode.setOpaque(false);
        hearChoices.setOpaque(false);

        final ImageIcon m_image =
                Utils.getIcon(ClientConfig.IMG_REGISTER_BACKGROUND);

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

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        boolean bool = false;

        if (field.getText().length() <= 0) {
            setStatusText("<font face=\"Arial\" color=\"#950000\">Please Enter Username</font>");
            bool = true;
        } else if (fieldPass.getPassword().length <= 0) {
            setStatusText("<font face=\"Arial\" color=\"#950000\">Please Enter Password</font>");
            bool = true;
        } else if (!new String(fieldPass.getPassword()).equals(new String(confirmFieldPass.getPassword()))) {
            setStatusText(
                    "<font face=\"Arial\" color=\"#950000\">Password and confirm password should be same</font>");
            bool = true;
        } else if (email.getText().length() <= 0) {
            setStatusText("<font face=\"Arial\" color=\"#950000\">Email should not be empty</font>");
            bool = true;
        }

        if (bool)
            Toolkit.getDefaultToolkit().beep();
        else {

             new Thread(new Runnable() {
                 public void run() {
                     settings.setLogin(field.getText());
                     settings.setPassword(new String(fieldPass.getPassword()));
                     setStatusText(bundle.getString("connecting"));
                     try {
                         int g = 0;
                         if(isMale)
                             g = 1;
                         String hearfrm = (String)hearChoices.getSelectedItem();
                         if(hearfrm.equals("Please Select"))
                             hearfrm = "";

                         lobbyFrameInterface.userRegister(field.getText(),
                                 new String(fieldPass.getPassword()), email.getText(),g,bonuscode.getText(),hearfrm);

                         setStatusText(bundle.getString("loading.tables"));
                         RegisterFrame.this.setVisible(false);

                         ((RegisterFrame.this.parent)).parent.setVisible(true);
                         ((RegisterFrame.this.parent)).parent.setEnabled(true);
                     }
                     catch (AuthenticationException aex) {
                         setStatusText("<font face=\"Arial\" color=\"#950000\">User already exists. Please change the Username.</font>");
                     }
                     catch (Exception ex) {
                         String message = ex.getMessage();
                         if (message == null || "".equals(message)) {
                             setStatusText(bundle.getString("fail.access"));
                             _cat.severe("Failed Registration" + ex.getMessage());
                         } else {
                             setStatusText(ex.getMessage());
                         }
                     }

                 }
             }).start();
        }
    }

    public void setStatusText(String s) {
        status.setText("<html>" + s + "</html>");
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
