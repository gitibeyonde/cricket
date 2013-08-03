package com.criconline.models;

import com.criconline.SharedConstants;
import com.criconline.ValidationConstants;
import com.cricket.common.message.GameEvent;
import java.util.logging.Logger;
import com.cricket.mmog.GameType;
import com.cricket.common.message.TournyEvent;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.cricket.common.interfaces.TournyInterface;
import com.cricket.common.message.PPDetailsResponse;

/**
 * Lobby table model for Cricket game
 * @hibernate.subclass
 *    discriminator-value="1"
 */

public class LobbyPPModel
    extends LobbyTableModel {

  static Logger _cat = Logger.getLogger(LobbyPPModel.class.getName());


  public String ppId;
  public int pTournyPlayed;
  public int pTRuns;
  public int pTBalls;
  public int pTScoringRate;
  public int pTCatches;
  public int pWickets;
  public int agression;
  public int confidence;
  public int strength;
  public int agility;
  public int consistency;
  public int ranking;
  public int rating;
  public int enabled;
  public java.util.Date creation;
  public int hired;
  public String hirerId;
  public java.util.Date hireDate;


  public LobbyPPModel(PPDetailsResponse ge) {
  }


  public void update(PPDetailsResponse te){
  }


  public boolean equalsByFields(Object obj) {
    if (obj instanceof LobbyPPModel) {
      LobbyPPModel table = (LobbyPPModel) obj;
      return
          super.equalsByFields(table);
    }
    else {
      return false;
    }
  }


}
