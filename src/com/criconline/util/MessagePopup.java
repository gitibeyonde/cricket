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

public class MessagePopup
    extends JDialog implements FocusListener, Runnable, ActionListener {

  protected JPanel panel;
  protected JTextPane editor;
  protected JButton okButton;
  private String inputText;

  public MessagePopup(String text) {
    super();
    editor = new JTextPane();
    panel = new JPanel();
    okButton = new JButton("Ok");

    setSize(320, 140);
    setLocation(250, 250);
    //editor.addFocusListener(this);
    editor.setBackground(Color.lightGray);
    panel.setBackground(Color.lightGray);

    editor.setEditable(false);
    editor.setContentType("text/html");
    editor.setText("<html><center><b>" + text + "</b></center></html>");

    panel.setLayout(new FlowLayout());
    panel.add(okButton);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(panel, BorderLayout.SOUTH);
    this.getContentPane().add(editor, BorderLayout.CENTER);
    setTitle("Message");
    pack();
    setVisible(true);
    setFocusable(false);
    this.requestFocusInWindow();
    okButton.addActionListener(this);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == okButton) {
      System.out.println("OKButton pressed");
      this.dispose();
    }
  }

  public void focusGained(FocusEvent e) {
    if (e.getSource() == editor) {
      System.out.println("Dialog Focus Gained");
    }
  }

  public void focusLost(FocusEvent e) {
    if (e.getSource() == editor) {
      System.out.println("Dialog Focus Lost");
      this.dispose();
    }
  }

  public void run() {
    try {
      for (int i = 1; i < 25; i++) {
        Thread.currentThread().sleep(1000);
        okButton.setText("OK (" + i + ")");
      }
    }
    catch (Exception e) {
      //
    }
    this.dispose();
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
  }
}
