package com.criconline.actions;
/**
 * Agneya NEW CLASS
 *
 * this action will deliver the message to the appropriate client
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class MessageAction
    extends StageAction {

  private String _cm;

  public MessageAction(int pos, String team, String cm) {
    super(PLAYER_MESSAGE, pos, team);
    this._cm = cm;
  }

  public MessageAction(String cm) {
    super(PLAYER_MESSAGE, -1, "");
    this._cm = cm;
  }

  public String getClientMessage() {
    return _cm;
  }

  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("MESSAGE: ").append(_cm).
        append(" > ").append(pos);
    return s.toString();
  }

}
