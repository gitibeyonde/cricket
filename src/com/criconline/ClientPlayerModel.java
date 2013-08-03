package com.criconline;

import java.awt.*;
import javax.swing.event.*;

import com.criconline.models.*;

public class ClientPlayerModel extends PlayerModel {
  /** isSelect */
  protected boolean selected = false;
  /** is Player Muting */
  protected boolean mute = false;
  /** is Player must paint speak icon */
  protected boolean mustPaintSpeaker = false;
  /** if player is active then all wait for he...
   *  most probably - yellow label. But it isn't rule*/

   protected EventListenerList changeListeners = new EventListenerList();

  public ClientPlayerModel(PlayerModel player) {
    super(player);
  }

  public void updateModel(PlayerModel pm) {
    super.updateModel(pm);
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    if (this.selected != selected) {
      this.selected = selected;
    }
  }

  public void update() {
//////////////////////////////////////////////////
//////////////////// -UPDATE- ////////////////////
//////////////////////////////////////////////////
  }

  public boolean isMute() {
    return mute;
  }

  public boolean isMustPaintSpeaker() {
    return mustPaintSpeaker;
  }

  public void setMute(boolean mute) {
    this.mute = mute;
  }

  public void setMustPaintSpeaker(boolean mustPaintSpeaker) {
    this.mustPaintSpeaker = mustPaintSpeaker;
  }

  public String toString() {
    return super.toString() + ", sel=" + selected;
  }

  public boolean isPreJoin() {
    return _player_status.isPreJoin();
  }

  public boolean isJoinRequested() {
    return _player_status.isJoinRequested();
  }
}
