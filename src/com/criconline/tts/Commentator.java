package com.criconline.tts;

import java.util.logging.Logger;

public class Commentator {
  private static Logger _cat = Logger.getLogger(Commentator.class.getName());
 // protected static SpeakHandler _sh = null;

  public Commentator() {
    //_sh = SpeakHandler.instance();
   // _sh = new SpeakHandler();
    _cat.finest("create commentator");
    speak(
        "Welcome to Cricket Party, Please, click on an player outline to join the game.");
  }

  public void speak(String text) {
    _cat.finest("Speak" + text);
    //_sh.add(text);
  }
}
