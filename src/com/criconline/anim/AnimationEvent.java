package com.criconline.anim;

import java.util.Observable;
import javax.swing.JPanel;
import com.cricket.mmog.cric.util.MoveParams;
import com.criconline.actions.Action;
import com.criconline.actions.MoveRequestAction;
import java.awt.Point;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Iterator;

public class AnimationEvent
    extends Observable {
  JPanel _owner;
  EventQueue _eq;
  Observable _attach;
  public String _action;
  public int _type;
  public int _seq;
  public int _start_delay;
  public Point _start;

  public AnimationEvent(AnimationEvent ev) {
    if (ev == null) {
      return;
    }
    _action = ev._action;
    _type = ev._type;
    _start_delay = ev._start_delay;
    _seq = ev._seq;
    _owner = ev._owner;
    _attach = ev._attach;
    _eq = ev._eq;
    _start = new Point(ev._start);
  }

  public AnimationEvent(String action, int type, int start_delay) {
    _action = action;
    _type = type;
    _start_delay = start_delay;
    _eq = new EventQueue();
  }

  public void setOwner(JPanel owner) {
    _owner = owner;
  }

  public void setNextEvent(Observable o, Gse event, int time) {
    _eq.addEvent(o, event, time);
  }

  public AEvent getEvent(int skip_count) {
    return _eq.getEvent(skip_count);
  }


  public void invokeRemainingEvents(AnimationManager am) {
    _eq.invokeAll(am);
  }

  public void setAttach(Observable obs) {
    _attach = obs;
  }

  public Observable getAttach() {
    return _attach;
  }

  public void setStart(Point o) {
    _start = new Point(o);
  }

  public String toString() {
    return "AE { action=" + _action + ", type=" + _type + ", start=" + _start +
        " }";
  }

  public class EventQueue {
    Hashtable _hook_delay;

    public EventQueue() {
      _hook_delay = new Hashtable();
    }

    public void addEvent(Observable obs, Gse ne, int hd) {
      _hook_delay.put(new Integer(hd), new AEvent(ne, obs));
    }

    public AEvent getEvent(int i) {
      for (Enumeration e = _hook_delay.keys(); e.hasMoreElements(); ) {
        Integer HD = (Integer) e.nextElement();
        int hd = HD.intValue();
        if (hd <= i) {
          return (AEvent) _hook_delay.remove(HD);
        }
      }
      return null;
    }

    public boolean checkEvent(Gse gse){
      for (Enumeration e = _hook_delay.keys(); e.hasMoreElements(); ) {
        Integer HD = (Integer) e.nextElement();
        int hd = HD.intValue();
        AEvent ae = (AEvent)_hook_delay.get(HD);
        if (ae._nextEvent == gse){
          return true;
        }
      }
      return false;
    }

    public void invokeAll(AnimationManager am) {
      for (Iterator e = _hook_delay.values().iterator(); e.hasNext(); ) {
        AEvent ae = (AEvent) e.next();
        am.update(ae._attach, ae._nextEvent);
      }
    }

  }

  public class AEvent {
    public Gse _nextEvent;
    public Observable _attach;

    public AEvent(Gse ne, Observable obs) {
      _nextEvent = ne;
      _attach = obs;
    }
  };

}
