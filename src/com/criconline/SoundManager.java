package com.criconline;

import java.applet.Applet;
import java.applet.AudioClip;

import java.util.logging.Logger;

public class SoundManager {

  static Logger _cat = Logger.getLogger(SoundManager.class.getName());

  private static int loopNo;
  private static AudioClip[] audioClips;
  public static int MISSED_MOVE = 0;
  public static int CARDS_FOLDING = 1;
  public static int CHECK = 2;
  public static int APPLAUSE = 3;
  public static int TOSS = 4;
  public static int CLICK_SOUND = 5;
  public static int INLOBBY = 6;
  public static int PIG = 7;
  public static int SHUFFLE_DECK = 8;
  public static int WAKE_UP = 9;
  public static int YOUR_TURN = 10;
  public static int PIG_PIG_PIG = 11;
  public static int BAT_HIT = 12;
  public static int BOWLER_RUN = 13;
  public static int START_MOVE = 14;
  public static int OUT_WICKET = 15;
  public static int WAIT1 = 16;
  public static int STUMPS_FALL = 17;


  public SoundManager() {
  }

  static {
    audioClips = new AudioClip[20];
    Class clas = SoundManager.class;
    try {
      audioClips[0] = Applet.newAudioClip(clas.getResource("sound/bounce.wav"));
      audioClips[1] = Applet.newAudioClip(clas.getResource("sound/default.wav"));
      audioClips[2] = Applet.newAudioClip(clas.getResource("sound/default.wav"));
      audioClips[3] = Applet.newAudioClip(clas.getResource("sound/firework.wav"));
      audioClips[4] = Applet.newAudioClip(clas.getResource("sound/toss.wav"));
      audioClips[5] = Applet.newAudioClip(clas.getResource(
          "sound/click_sound.wav"));
      audioClips[6] = Applet.newAudioClip(clas.getResource(
          "sound/spacemusic.au"));
      audioClips[7] = Applet.newAudioClip(clas.getResource("sound/pig.wav"));
      audioClips[8] = Applet.newAudioClip(clas.getResource("sound/default.wav"));
      audioClips[9] = Applet.newAudioClip(clas.getResource("sound/wake_up.wav"));
      audioClips[10] = Applet.newAudioClip(clas.getResource(
          "sound/your_turn.wav"));
      audioClips[11] = Applet.newAudioClip(clas.getResource("sound/pig_rep.wav"));
      audioClips[12] = Applet.newAudioClip(clas.getResource("sound/bat.wav"));
      audioClips[13] = Applet.newAudioClip(clas.getResource("sound/bowl.wav"));
      audioClips[14] = Applet.newAudioClip(clas.getResource("sound/fielder.wav"));
      audioClips[15] = Applet.newAudioClip(clas.getResource(
          "sound/out_wicket.wav"));
      audioClips[16] = Applet.newAudioClip(clas.getResource(
          "sound/trippygaia1.mid"));
      audioClips[17] = Applet.newAudioClip(clas.getResource(
          "sound/ball_throw.wav"));
    }
    catch (Exception e) {
      _cat.warning("Unable to load"+  e);
    }
  }


  public static void playEffect(int num) {
    if (num == -1) {
      return;
    }
    try {
      loopTest();
      if (audioClips[num] != null) {
        audioClips[num].play();
      }
    }
    catch (Exception e) {
      _cat.warning("Unable to load"+  e);
    }
  }

  public static void playEffectRepeatable(int num) {
    try {
      loopTest();
      if (audioClips[num] != null) {
        audioClips[num].loop();
      }
      loopNo = num;
    }
    catch (Exception e) {
      _cat.warning("Unable to load"+  e);
    }
  }

  protected static void loopTest() {
    try {
      if (loopNo != -1) {
        audioClips[loopNo].stop();
        loopNo = -1;
      }
    }
    catch (Exception e) {
      _cat.warning("Unable to load"+  e);
    }
  }

  protected static void stopBackground() {
    audioClips[WAIT1].stop();
  }

  protected static void startBackground() {
    playEffectRepeatable(WAIT1);
  }

}
