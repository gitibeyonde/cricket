package com.criconline.anim;

import java.util.*;

/**
 * Client queue is singleton. Clients which have received a GameEvent are
 * lined up in this queue for processing.
 * ClientQueue is created when the Poker server is started
 *
 **/

public class AnimationQueue {
  //private static Logger _cat = Logger.getLogger(ResponseQueue.class.getName());
  String _name; // queue name
  private LinkedList _items;

  public AnimationQueue() {
    _name = "Animation Queue";
    _items = new LinkedList();
  }

  public synchronized int size() {
    return _items.size();
  }

  // add message to queue
  public synchronized void put(AnimationEvent c) {
    //_cat.finest("Adding to queue= " + c);
    _items.add(c);
  }

  // get the first message in the queue in FIFO order
  public synchronized AnimationEvent[] fetch() throws NoSuchElementException {
    //_cat.finest("Removing from queue= " + _items.getFirst());
    if (_items.size()==0)return null;
    Vector aeList = new Vector();
    AnimationEvent ae = (AnimationEvent) _items.get(0);
    int seq = ae._seq;
    while(seq==ae._seq){
      aeList.add(_items.remove(0));
      if (_items.size()==0){
        break;
      }
      ae = (AnimationEvent)_items.get(0);
    }
    return (AnimationEvent [])aeList.toArray(new AnimationEvent[aeList.size()]);
  }

  // get the first message in the queue in FIFO order
  public synchronized AnimationEvent look() throws NoSuchElementException {
    //_cat.finest("Removing from queue= " + _items.getFirst());
    try {
      return (AnimationEvent) _items.get(0);
    }catch (IndexOutOfBoundsException e){
      throw new NoSuchElementException();
    }
  }
  public String getName() {
    return _name;
  }

  public void setName(String name) {
    this._name = name;
  }

  public String toString() {
    String str = _name + ": " + size() + ":\n";
    Object cl[] = _items.toArray();
    for (int i = 0; i < cl.length; i++) {
      str += (AnimationEvent) cl[i];
    }
    return str;
  }

}
