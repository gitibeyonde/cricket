package com.criconline;

import com.criconline.actions.Action;
import com.cricket.common.message.ResponsePlayerShift;


/**
 * It is a Table Server message subscriber interface.
 * @author Kom
 */

public interface ServerMessagesListener {
  public void serverMessageReceived(Object actions);
}
