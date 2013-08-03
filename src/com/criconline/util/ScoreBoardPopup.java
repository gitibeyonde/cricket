package com.criconline.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.text.rtf.*;
import java.io.*;
import com.criconline.ClientCricketModel;
import com.criconline.models.PlayerModel;

public class ScoreBoardPopup
    extends JDialog
    implements FocusListener {

  protected JPanel panel;
  protected JTextPane editor;
  private String inputText;
  static int x, y;

  public ScoreBoardPopup(ClientCricketModel cm, Point ref) {
    super();
    editor = new JTextPane();
    panel = new JPanel();

    editor.setEditable(false);
    editor.setContentType("text/html");
    StringBuffer html = new StringBuffer();
    html.append("<h3 align=center>").append(cm.getName()).append("</h3>");
    html.append("<h3 align=center> Id = ").append(cm.getGameId()).append("/").append(cm.getGameRunId()).append("</h3>");
    PlayerModel pm[] = cm.getBatters();
    for (int i=0;i<pm.length;i++){
      html.append(pm[i].getName()).append(pm[i]._runs).append(cm._balls).append("<br>");
    }
    pm = cm.getFielders();
    for (int i=0;i<pm.length;i++){
      html.append(pm[i].getName()).append(cm._balls).append("<br>");
    }

    editor.setText(html.toString());
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
    setTitle("Score Card");
    //pack();
    setVisible(true);
    setFocusable(false);
  }

  public void update(ClientCricketModel cc){
    String html = "<h3 align=center>" + "</h3>" ;
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
