package com.criconline.models.tourny;

import com.criconline.Copyable;
import java.util.logging.Logger;
import com.cricket.common.message.GameEvent;
import com.cricket.mmog.GameType;
import com.criconline.Validable;
import com.cricket.mmog.cric.GameState;
import com.cricket.mmog.cric.util.CricketConstants;
import com.cricket.common.message.TournyEvent;
import com.criconline.models.LobbyTableModel;

/**
 * The model representing a pitch or tournament entry on the main lobby
 *
 * @author not attributable
 * @version 1.0
 */

public class TournyPlayerListModel extends LobbyTableModel
    implements java.io.Serializable, CricketConstants {
  static Logger _cat = Logger.getLogger(TournyPlayerListModel.class.getName());

  public int  _state, _points;


  public TournyPlayerListModel() {
  }


  public TournyPlayerListModel(TournyPlayerListModel model) {
    name = model.name;
  }


  public final int getPoints(){
    return _points;
  }

  public boolean equalsByFields(Object obj) {
    if (obj instanceof TournyPlayerListModel) {
      TournyPlayerListModel table = (TournyPlayerListModel) obj;
      return
          name.equals(name);
    }
    else {
      return false;
    }
  }

  /**
   * Tables are equal when they table ids are equal.
   */
  public boolean equals(Object obj) {
    if (obj instanceof TournyPlayerListModel) {
      return name == ( (TournyPlayerListModel) obj).getName();
    }
    else {
      return false;
    }
  }


  public String toString(){
    return  name;
  }

}

