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
import java.net.URL;

public class URLOpener
    extends JDialog  {

  protected JEditorPane editor;

  public URLOpener(String text) {
    super();
    try {
      editor = new JEditorPane();

      setSize(600, 400);
      setLocation(250, 250);

      editor.setPage(new URL(text));

      this.getContentPane().add(editor, BorderLayout.CENTER);
      setVisible(true);
      setFocusable(false);
    }catch (Exception e){
      e.printStackTrace();
      //ignore
    }
  }



  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
  }
}
