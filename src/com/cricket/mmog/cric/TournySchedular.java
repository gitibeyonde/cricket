package com.cricket.mmog.cric;

import java.util.*;

public class TournySchedular
extends TimerTask
{
  Hashtable _map;

  public TournySchedular(Hashtable map) {
    _map=map;
  }

  public void run(){
    Enumeration trn = _map.elements();
    while(trn.hasMoreElements() ){
      ((Tourny)trn.nextElement()).stateSwitch();
    }
  }

}
