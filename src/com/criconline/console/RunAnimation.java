package com.criconline.console;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.agneya.util.Configuration;
import com.criconline.ClientConfig;
import com.criconline.Utils;
import com.criconline.anim.Animation;
import com.criconline.exceptions.AnimationOverException;
import com.criconline.pitch.ImageStrip;
import java.util.Vector;
import com.criconline.anim.AnimationSequence;

public class RunAnimation extends JFrame {
  private BorderLayout borderLayout1 = new BorderLayout();
  private Button button1 = new Button();
  private AnimationSequence _as;

  public RunAnimation() {
    _as = new AnimationSequence();
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void update() throws Exception {
    // Rectangle r1 = _anim.getRealCoords();
    // System.out.println(r1);
    _as.update();
    System.out.println(_as + ", " + _as.getCurrentFrame());
    //repaint(r1.x, r1.y, r1.width, r1.height);
    refresh();
  }

  public void refresh() {
    repaint(_as.x(), _as.y(), _as.getIconWidth(), _as.getIconWidth());
  }

  public void paint(Graphics g) {
    ImageIcon ic = Utils.getIcon("images/pitch.jpg");
    ic.paintIcon(this, g, -300, -540);
    _as.paintIcon(this, g);
  }

  private void jbInit() throws Exception {
    Configuration conf = Configuration.instance();
    getContentPane().setLayout(borderLayout1);
    setIconImage(Utils.getIcon(ClientConfig.PW_ICON).getImage());
    Dimension screenSize;
    Dimension frameSize;
    Point framePos;
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frameSize = new Dimension(1000, 700);
    framePos = new Point((screenSize.width - frameSize.width) / 2,
                         (screenSize.height - frameSize.height) / 2);

    setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
    getContentPane().setLayout(new BorderLayout(0, 0));

    button1.setLabel("Run");
    ButtonListener ac = new ButtonListener();
    button1.addActionListener(ac);

    this.getContentPane().add(button1, java.awt.BorderLayout.SOUTH);

    for (int i = 1; i < 10; i++) {
      String name = conf.getProperty("AnimName" + i);
      if (name == null) {
        break;
      }
      String sub_category = conf.getProperty("AnimType" + i);
      int width = conf.getInt("AnimWidth" + i);
      int height = conf.getInt("AnimHeight" + i);
      int frames = conf.getInt("AnimFrames" + i);
      Point start = new Point(conf.getInt("AnimSX" + i),
                              conf.getInt("AnimSY" + i));
      Point end = new Point(conf.getInt("AnimEX" + i), conf.getInt("AnimEY" + i));
      int delay = conf.getInt("AnimDelay" + i);
      ImageStrip is = new ImageStrip(name, sub_category, width, height, frames,
                                     1, start, end, 0, 0, delay, 0, 0, true, 5);
      System.out.println("Adding " + name);
      _as.addAnimation(new Animation(is));
    }

    _as.startAnimation();

    setResizable(false);
    setVisible(true);
  }

  public static void main(String args[]) {
    //PropertyConfigurator.configure(ClientConfig.LOG_PROPERTIES);
    RunAnimation ra = new RunAnimation();
    // Agneya FIX 16
  }


  public class ButtonListener extends TimerTask implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      Timer t = new Timer();
      System.out.println(_as.delay());
      t.scheduleAtFixedRate(this, 0, _as.delay());
    }

    public void run() {
      try {
        System.out.println("Running ");
        update();
      }
      catch (AnimationOverException aoe) {
        System.out.println("Animation Over");
        _as.reset();
      }
      catch (Exception ce) {
        System.out.println("Collision");
        _as.reset();
      }

    }

  }

}
