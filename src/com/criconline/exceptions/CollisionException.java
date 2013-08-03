package com.criconline.exceptions;

import com.criconline.anim.Animation;


public class CollisionException extends Exception {
  Animation _anim;

  public CollisionException() {
  }


  public CollisionException(Animation name) {
    super(name.toString());
    _anim=name;
  }

}
