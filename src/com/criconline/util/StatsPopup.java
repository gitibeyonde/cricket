package com.criconline.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.text.rtf.*;
import java.io.*;
import com.criconline.ClientRoom;
import com.criconline.ClientPlayerController;
import com.criconline.ClientPlayerModel;

public class StatsPopup
    extends JDialog
    implements FocusListener {

  protected JPanel panel;
  protected JTextPane editor;
  private String inputText;
  static int x, y;

  public StatsPopup(ClientPlayerModel plr, Point ref) {
    super();
    editor = new JTextPane();
    panel = new JPanel();

    editor.setEditable(false);
    editor.setContentType("text/html");
    String html = "<h3 align=center>" + plr.getName() + "</h3>" +
                  "<img src=\"http://www.cricketparty.com/images/client_images/" + plr.getName() + ".png\"></img><h4>Points: " +
                  plr.getBankRoll() + "<br>" + plr._runs + " runs off " +
                  plr._ballsPlayed + " balls.<br>Bowled " +
                  plr._ballsBowled + " balls.</h4>";
    editor.setText(html);
    setSize(160, 200);
    x = ref.x + 1020;
    y=ref.y + 250;
    setLocation(x, y);
    //editor.addFocusListener(this);
    editor.setBackground(Color.WHITE);
    panel.setBackground(Color.WHITE);

    panel.setLayout(new FlowLayout());
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(panel, BorderLayout.SOUTH);
    this.getContentPane().add(editor, BorderLayout.CENTER);
    setTitle("Player Stats");
    //pack();
    setVisible(true);
    setFocusable(false);
  }

  public void update(ClientPlayerModel plr){
    String html = "<h3 align=center>" + plr.getName() + "</h3>" +
                  "<img src=\"http://www.cricketparty.com/images/client_images/" + plr.getName() + ".png\"></img><h4>Points: " +
                  plr.getBankRoll() + "<br>" + plr._runs + " runs off " +
                  plr._ballsPlayed + " balls.<br>Bowled " +
                  plr._ballsBowled + " balls.</h4>";
    editor.setText(html);
      setVisible(true);
  }


  public void focusGained(FocusEvent e) {
    if (e.getSource() == editor) {
      //System.out.println("Dialog Focus Gained");
    }
  }

  public void focusLost(FocusEvent e) {
    if (e.getSource() == editor) {
      //System.out.println("Dialog Focus Lost");
      //this.dispose();
      setVisible(false);
    }
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
  }
}
