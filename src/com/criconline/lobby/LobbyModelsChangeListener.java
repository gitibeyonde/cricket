package com.criconline.lobby;

import com.criconline.models.LobbyTableModel;
import com.criconline.actions.ChatAction;
import com.cricket.common.db.DBPlayer;

/**
 * It is a lobby tables change subscriber interface.
 * @author Abhi
 */

public interface LobbyModelsChangeListener {

  public void tableListUpdated(LobbyTableModel[] changes);

  public void adUpdated();

  public void chatRcvd(ChatAction chat);

  public void professionalPlayerList(DBPlayer []v);
}
