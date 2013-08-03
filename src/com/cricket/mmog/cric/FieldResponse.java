package com.cricket.mmog.cric;

import com.cricket.mmog.*;
import com.cricket.mmog.Player.*;
import com.cricket.mmog.cric.util.MoveParams;
import com.cricket.mmog.cric.util.MoveUtils;
import com.criconline.anim.AnimationConstants;

public class FieldResponse extends CricResponse implements AnimationConstants {

  public FieldResponse(Cricket g, MoveParams md, CricketPresence fld) {
    super(g);
    g.reset();
    buf.append(header());
    buf.append(pitchDetails());
    if (md!=null){
      buf.append(lastMoveDetails());
      buf.append("md=").append(md.stringValue()).append(",");
    }
    if (fld != null) {
      buf.append("next-move=");
      fld.resetNextMove();
      buf.append(setMove(fld, fld.pos(), fld.teamName(), Moves.F_FIELD, 5));
      buf.append(",");
    }

    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j], playerTargetPosition(_allPlayers[j]).toString());
      setCommand(_allPlayers[j], playerStats(_allPlayers[j]).toString());
    }
    broadcast(_allPlayers, buf.toString());
    _cat.finest("Field move " + buf);
  }

  public FieldResponse(Cricket g, MoveParams md) {
    super(g);
    g._nextMovePlayers.clear();
    buf.append(header());
    buf.append(pitchDetails());
    if (md!=null){
      buf.append(lastMoveDetails());
      buf.append("md=").append(md.stringValue()).append(",");
    }

    CricketPresence[] pf = g._pitch.getBowlerAndFielders();

    buf.append("next-move=");
    for (int i=0;i<pf.length;i++) {
      pf[i].resetNextMove();
      buf.append(setMove(pf[i], pf[i].pos(), pf[i].teamName(), Moves.F_FIELD, 5));
    }
    buf.deleteCharAt(buf.length()-1);
    buf.append(",");


    for (int j = 0; j < _allPlayers.length; j++) {
      setCommand(_allPlayers[j], playerTargetPosition(_allPlayers[j]).toString());
      setCommand(_allPlayers[j], playerStats(_allPlayers[j]).toString());
    }
    broadcast(_allPlayers, buf.toString());
    _cat.warning("Field move " + buf);
  }

  StringBuffer buf = new StringBuffer();
}
