package com.criconline.exceptions;

import com.criconline.anim.Animation;


public class WicketCollisionException extends CollisionException {
  Animation _anim;

  public WicketCollisionException() {
  }


  public WicketCollisionException(Animation name) {
    super(name);
  }

}
