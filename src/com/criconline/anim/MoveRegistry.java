package com.criconline.anim;

import com.cricket.mmog.cric.util.MoveParams;
import java.util.Hashtable;

public class MoveRegistry {
  Hashtable _moves;

  public MoveRegistry() {
    _moves = new Hashtable();
  }

  public MoveParams getMove(int move_type) {
    return (MoveParams) _moves.get("" + move_type) == null ?
        MoveParams.generate(move_type) :
        (MoveParams) _moves.remove("" + move_type);
  }

  public void setMove(MoveParams move) {
    _moves.put("" + move._type, move);
  }
}
