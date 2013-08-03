package com.criconline.lobby.tourny;

import com.criconline.actions.Action;
import com.cricket.common.message.TournyEvent;


/**
 * It is a Table Server message subscriber interface.
 * @author Kom
 */

public interface TournyMessagesListener {
  public void tournyMessageReceived(Object actions);

}
