package com.cricket.mmog.cric;

import com.cricket.mmog.*;

public class IllegalReqResponse
    extends CricResponse {

  public IllegalReqResponse(Cricket g) {
    super(g);
    buf.append(header()).append("illegal-move=").append(Moves.
        stringValue(g.inquirer().lastMove()));
    setCommand(g.inquirer(), buf.toString());
  }

  StringBuffer buf = new StringBuffer();
}
