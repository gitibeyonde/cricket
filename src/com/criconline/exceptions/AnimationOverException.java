package com.criconline.exceptions;


public class AnimationOverException extends Exception {
  String _anim_name;

  public AnimationOverException() {
  }


  public AnimationOverException(String name) {
    _anim_name=name;
  }

  public String toString(){
    return _anim_name + " animation complete";
  }

}
