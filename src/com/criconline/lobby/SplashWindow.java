package com.criconline.lobby;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import com.criconline.ClientConfig;
import com.criconline.Utils;

class SplashWindow
    extends JWindow {

  public SplashWindow(Frame f, int waitTime) {
    super(f);
    final ImageIcon m_image =
        Utils.getIcon(ClientConfig.IMG_SPLASH);

    JLabel l = new JLabel("");
    l = new JLabel() {
      public void paintComponent(Graphics g) {
        m_image.paintIcon(this, g, 0, 0);
      }
    };
    getContentPane().add(l);
    setSize(500, 300);
    Dimension screenSize =
        Toolkit.getDefaultToolkit().getScreenSize();
    Dimension labelSize = l.getPreferredSize();
    setLocation(screenSize.width / 2 - 250,
                screenSize.height / 2 - 150);
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        setVisible(false);
        dispose();
      }
    });
    final int pause = waitTime;
    final Runnable closerRunner = new Runnable() {
      public void run() {
        setVisible(false);
        dispose();
      }
    };
    Runnable waitRunner = new Runnable() {
      public void run() {
        try {
          Thread.sleep(pause);
          SwingUtilities.invokeAndWait(closerRunner);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    setVisible(true);
    Thread splashThread = new Thread(waitRunner, "SplashThread");
    splashThread.start();
  }

}
