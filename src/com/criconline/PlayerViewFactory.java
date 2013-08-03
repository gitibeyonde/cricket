package com.criconline;

import javax.swing.JComponent;
import com.criconline.pitch.PitchSkin;
import com.cricket.mmog.PlayerStatus;
import com.criconline.anim.AnimationManager;

public class PlayerViewFactory {

  private PlayerViewFactory() {
  }


  public static PlayerView newPlayerView(PitchSkin skin,
                                         ClientPlayerModel model,
                                         CricketController owner,
                                         AnimationManager am, int ts, int num) {
    PlayerStatus ps = model._player_status;
    if (ps.isBatsman()) {
      return new BatsmanPlayerView(skin, model, owner, am, ts, num);
    }
    else if (ps.isRunner()) {
      return new RunnerPlayerView(skin, model, owner, am, ts, num);
    }
    else if (ps.isBowler()) {
      return new BowlerPlayerView(skin, model, owner, am, ts, num);
    }
    else if (ps.isFielder()) {
      return new FielderPlayerView(skin, model, owner, am, ts, num);
    }
    else if (ps.isWicketKeeper()) {
      return new WKPlayerView(skin, model, owner, am, ts, num);
    }
    else if (ps.isPreJoin() || ps.isWaiting()) {
      return new PlayerView(skin, model, owner, am, ts, num);
    }

   else {
      throw new IllegalStateException("Unknown state for a player " + ps.toString());
    }

  }
}
