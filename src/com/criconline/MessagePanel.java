package com.criconline;

import com.criconline.anim.AnimationConstants;
import javax.swing.JPanel;
import java.awt.event.KeyListener;
import com.criconline.anim.Animator;
import java.util.logging.Logger;
import java.awt.event.ActionListener;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public class MessagePanel
    extends JPanel implements  AnimationConstants {
  static Logger _cat = Logger.getLogger(MessagePanel.class.getName());

  protected JTextArea textArea = null; //new JTextArea(5, 20);


  public MessagePanel(Dimension frameSize) {
    setBackground(new Color(75, 108, 55));
    textArea = new JTextArea(1, 90);
    textArea.setForeground(Color.BLUE);
    textArea.setFont(Utils.chatFont);
    textArea.setBackground(new Color(148, 188, 65));
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setEditable(false);
    textArea.setFocusable(false);
    textArea.setBorder(null);
    textArea.setText("Welcome to CricketParty !! ");

    JScrollPane pane = new JScrollPane(textArea,
                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    pane.getVerticalScrollBar().setUI(Utils.getRoomScrollBarUI());
    pane.getViewport().setOpaque(false);
    pane.setBorder(null);
    System.out.println(frameSize.width + "," +  frameSize.height);
    pane.setBounds(0 , 0, frameSize.width, frameSize.height);
    add(pane);
  }

  public void addCommentary(String html){
    textArea.setText(textArea.getText() + html);
  }


  public static void main(String args[]){
    JFrame pitch = new JFrame();

    pitch.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        //forcedCloseRoom(); // unsafe exit
        _cat.finest("windowClosing(WindowEvent e)");
      }
    });

    Dimension screenSize;
    Dimension frameSize;
    Point framePos;
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frameSize = new Dimension(SCREEN_WIDTH, 120);
    framePos = new Point( (screenSize.width - frameSize.width) / 2,
                         (screenSize.height - frameSize.height) / 2);

    pitch.setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
    MessagePanel mp = new MessagePanel(frameSize);
    pitch.getContentPane().add(mp);
    mp.addCommentary("Welcome to Gachhibowli stadium, the weather is clear ");
    //pitch.setResizable(false);
    pitch.setVisible(true);
  }
}
