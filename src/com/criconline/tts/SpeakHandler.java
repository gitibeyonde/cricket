package com.criconline.tts;

import java.util.Vector;
import java.util.logging.Logger;

//import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

import java.util.Locale;

//import javax.speech.EngineList;
//import javax.speech.EngineCreate;
//import javax.speech.synthesis.Synthesizer;
//import javax.speech.synthesis.SynthesizerModeDesc;


class SpeakHandler {
  /**private static Logger _cat = Logger.getLogger(SpeakHandler.class.getName());
  protected Synthesizer synthesizer;
  private static SpeakHandler _sh = null;
  static Object _dummy = new Object();
  static boolean _keepalive = true;

  public static SpeakHandler instance() {
    if (_sh == null) {
      synchronized (_dummy) {
        if (_sh == null) {
          _sh = new SpeakHandler();
        }
      }
    }
    return _sh;
  }


  public SpeakHandler() {
    createSynthesizer();
  }


  public void createSynthesizer() {

        try {
            SynthesizerModeDesc desc =
                new SynthesizerModeDesc(null,
                                        "general",
                                        Locale.US,
                                        Boolean.FALSE,
                                        null);

            FreeTTSEngineCentral central = new FreeTTSEngineCentral();
            EngineList list = central.createEngineList(desc);
            System.out.println("Got engine list = " + list.size());

            if (list.size() > 0) {
                EngineCreate creator = (EngineCreate) list.get(0);
                synthesizer = (Synthesizer) creator.createEngine();
            }
            if (synthesizer == null) {
                System.err.println("Cannot create synthesizer");
                System.exit(1);
            }
            synthesizer.allocate();
            synthesizer.resume();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


  public void add(String command) {
    System.out.println("Speaking " + command);
     synthesizer.speakPlainText( command, null);
  }


  public static void main(String args[]){
    SpeakHandler sh = new SpeakHandler();
    System.out.println("Got speak handler ");
    sh.add("Hello World");
    sh.add("Hello World");
    sh.add("Hello World");
  }
**/
}
