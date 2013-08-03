package com.criconline.exceptions;

import com.criconline.anim.Animation;


public class FielderCollisionException extends CollisionException {

  public FielderCollisionException() {
  }


  public FielderCollisionException(Animation name) {
    super(name);
  }

}
