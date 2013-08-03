package com.criconline.exceptions;

import com.criconline.anim.Animation;


public class NetworkDelayException extends Exception {

  public NetworkDelayException() {
  }


  public NetworkDelayException(String name) {
    super(name.toString());
  }

}
