package com.cricket.common.message;

import java.util.*;

/**
 * Client queue is singleton. Clients which have received a GameEvent are
 * lined up in this queue for processing.
 * ClientQueue is created when the Poker server is started
 *
 **/

public class CommandQueue {
  String _name; // queue name
  private CommandProcessor _wq;
  private LinkedList _items;

  public CommandQueue(String wq) throws Exception {
    _name = "Command Queue";
    _items = new LinkedList();
    _wq = (CommandProcessor) Class.forName(wq).newInstance();
    _wq.startProcessor(this);
  }

  public void stopProcessor() throws Exception {
    _wq.stopProcessor();
  }

  public synchronized int size() {
    return _items.size();
  }

  // add message to queue
  public synchronized void put(Command c) {
    //System.out.println("Adding to queue= " + c);
    _items.add(c);
    // wake up the worker
    _wq.wakeUp();
  }

  // get the first message in the queue in FIFO order
  public synchronized Command fetch() throws NoSuchElementException {
    //System.out.println("Removing from queue= " + _items.getFirst());
    return (Command) _items.removeFirst();
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
      str += (Event) cl[i];
    }
    return str;
  }

}
