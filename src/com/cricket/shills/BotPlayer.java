package com.cricket.shills;

import com.cricket.client.Cricketer;
import com.cricket.mmog.Player;

import java.util.logging.Logger;


public class BotPlayer extends Player {
    // set the category for logging
    static Logger _cat = Logger.getLogger(BotPlayer.class.getName());


    public int _life=-1;
    public BotGame _bg;
    public double _initialChips;


    public BotPlayer(String name){
        super(name, 0);
        this._shill = true;
        _life =  (BotData.randomInt(BotData.LIFE) + 10);
    }

    public void attach(BotGame bg){
        _bg = bg;
    }

    public void release(){
        _life =  -1 * BotData.randomInt(BotData.LIFE);
        _cat.finest("Releasing player " + this);
        BotList.putBack(this);
    }

    public static void release(String name){
        BotPlayer bp = BotList.find(name);
        bp.release();
    }


    public String toString(){
        return super.toString() + " life=" + _life;
    }


}
