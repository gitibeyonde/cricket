package com.cricket.shills;

import com.cricket.common.message.Command;
import com.cricket.mmog.cric.util.ActionConstants;

import java.util.Comparator;
import java.util.logging.Logger;

public class BotUtils implements ActionConstants {
    // set the category for logging
    static Logger _cat = Logger.getLogger(BotUtils.class.getName());

    public static int getMoveId(String mov) {
        int mov_id = -99;
        if (mov.equals("join")) {
            mov_id = Command.M_JOIN;
        }
        else if (mov.equals("open")) {
            mov_id = Command.M_OPEN;
        }
        else if (mov.equals("leave")) {
            mov_id = Command.M_LEAVE;
        }
        else if (mov.equals("sit-in")) {
            mov_id = Command.M_SIT_IN;
        }
        else if (mov.equals("opt-out")) {
            mov_id = Command.M_OPT_OUT;
        }
        else if (mov.equals("wait")) {
            mov_id = Command.M_WAIT;
        }
        else {
            mov_id = Command.M_ILLEGAL;
        }
        return mov_id;
    }


    public static void main(String args[]){

    }


}
