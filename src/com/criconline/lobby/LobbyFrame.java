package com.criconline.lobby;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalTheme;

import com.criconline.ClientConfig;
import com.criconline.util.URLOpener;
import com.criconline.util.MessagePopup;

/** Main lobby frame.
 *  Contains all JTables with all game tables.
 */
public class LobbyFrame
    extends JFrame {

  static Logger _cat = Logger.getLogger(LobbyFrame.class.getName());

  public LobbyFrame(LobbyFrameInterface lobbyInterface) {
    super();

// ---  Init ---
    JPanel pane = (JPanel) getContentPane();
    pane.setLayout(null);
    pane.setOpaque(false);
//		---  Init ---

//		---  Create environment ---
    setLookAndFeel(lobbyInterface.getLookAndFeel());
    createBackground(lobbyInterface.getBackground());
    createExitEvent(lobbyInterface.getWindowCloseEvent());
    createPosAndSize();
    setJMenuBar(lobbyInterface.getMenu());
//		---  Create environment ---

//		---  Create components ---
    Object[] component = lobbyInterface.getComponents();
    for (int i = 0; i < component.length; i++) {
      pane.add( (JComponent) component[i]);
//		---  Create components ---

    }
    lobbyInterface.init(this);
    /***try  {
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
    catch (Exception e){
      e.printStackTrace();
    }**/
  }

  private void setLookAndFeel(MetalTheme theme) {
    try {
      if (theme != null) {
        javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(theme);
      }
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    }
    catch (Exception ex) {
      _cat.severe("Failed loading L&F: :-@ " + ex.getMessage());
    }
  }

  private void createBackground(ImageIcon background) {
    final ImageIcon m_image = background;
    final int winc = m_image.getIconWidth();
    final int hinc = m_image.getIconHeight();
    JLabel backlabel = new JLabel("");
    if (m_image.getIconWidth() > 0 && m_image.getIconHeight() > 0) {
      backlabel = new JLabel() {
        public void paintComponent(Graphics g) {
          int w = getParent().getWidth();
          int h = getParent().getHeight();
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
    backlabel.setBounds(0, 12, 5000, 5000);
  }

  private void createPosAndSize() {
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = new Dimension(ClientConfig.
                                        DEFAULT_LOBBY_SCREEN_SIZE_X,
                                        ClientConfig.
                                        DEFAULT_LOBBY_SCREEN_SIZE_Y);
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    setBounds(
        new Rectangle(
        (screenSize.width - frameSize.width) / 2,
        (screenSize.height - frameSize.height) / 2,
        ClientConfig.DEFAULT_LOBBY_SCREEN_SIZE_X,
        ClientConfig.DEFAULT_LOBBY_SCREEN_SIZE_Y));
    setResizable(false);
  }

  private void createExitEvent(WindowListener listener) {
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(listener);
  }
}
