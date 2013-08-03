package com.criconline;

import java.text.MessageFormat;
import com.cricket.mmog.PlayerPreferences;

/**
 * This class holds info about client preferences (settings) related to
 * appiarence of application, auto play settings and so on.
 * The such info stored in persistent storage on server side.
 * @hibernate.class
 *    table="clientSettings"
 */
public class ClientSettings {

  private int userId;

  private boolean randomDelay = false;

  //@@@@@@@@@@@@@@@@@@   SOUND ON & OFF   @@@@@@@@@@@@@@@@@@@@@@@@@@
  private boolean sound = false;

  /**
   * Empty constructor.
   */

  public ClientSettings() {

  }

  public ClientSettings(int pref) {
  }

  /**
   * Copy constructor.
   */
  public void copy(ClientSettings settings) {
    randomDelay = settings.randomDelay;
    sound = settings.sound;
  }

  public int intVal() {
    int p = 0;

    return p;
  }

  /**
   * Gets the user id to whom this settings belongs.
   * @hibernate.id
   *    column="userId"
   *    generator-class="assigned"
   */
  public int getUserId() {
    return userId;
  }

  /**
   * Sets the user id to whom this settings belongs.
   */
  public void setUserId(int userId) {
    this.userId = userId;
  }


  /**
   * There will be a short, random delay when you are acting using the
   * in-turn choices.
   * @hibernate.property
   */
  public boolean isRandomDelay() {
    return randomDelay;
  }

  public void setRandomDelay(boolean randomDelay) {
    this.randomDelay = randomDelay;
  }

//@@@@@@@@@@@@@@@@@@@@Sound
  public boolean isSound() {
    return sound;
  }

  public void setSound(boolean sound) {
    this.sound = sound;
  }

  public String toString() {
    return MessageFormat.format(
        "autoblind: {0}, waitforblind: {1}, showbest: {2}, mucklosing: {3}",
        new Object[] {
        "" + 0, "" + 0,
        "" + 0, "" + 0
    });
  }

}
