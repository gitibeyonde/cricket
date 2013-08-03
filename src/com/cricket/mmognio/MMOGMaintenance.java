package com.cricket.mmognio;

import java.util.*;
import java.util.logging.Logger;

import com.agneya.util.Configuration;
import com.agneya.util.ConfigurationException;
import com.cricket.mmog.Game;
import com.cricket.mmog.CricketPlayer;
import com.cricket.mmog.Moves;
import com.cricket.mmogserver.GamePlayer;
import com.cricket.mmogserver.GameProcessor;
import com.cricket.mmog.resp.Response;
import com.cricket.common.message.ResponseGameEvent;
import com.cricket.mmog.CricketPresence;
import com.cricket.mmog.cric.Cricket;
import com.cricket.mmog.cric.GameDetailsResponse;


/**
 *
 */
class MMOGMaintenance extends TimerTask {
  static Logger _cat = Logger.getLogger(MMOGMaintenance.class.getName());
  int _timeout;
  int _rqs;


  /* Construction ************************************************/
  /**
   * Initializes a server maintenace object.
   * @param cm The ConnectManager to be maintained.
   */
  public MMOGMaintenance(int to, int qSize) throws Exception {
    _timeout = to;
    _rqs = Configuration.instance().getInt("Server.handler.response.queue.size");
  }


  /* Run() *******************************************************/
  /**
   * Main loop wakes up occasionally maintenanceMethod().
   */
  public void run() {
    testForExpiredClients();
  }


  /**
   * Locates and removes expired clients.
   */
  public final void testForExpiredClients() {
    try {
      _cat.finest("Maintenance thread " + _timeout + " Number of handlers = " +MMOGHandler._registry.size());
      Enumeration e = MMOGHandler._registry.elements();
      while (e.hasMoreElements()) {
        boolean kill = false;
        MMOGHandler c = (MMOGHandler) e.nextElement();
        MMOGClient pc = c.attachment();
        GamePlayer gp = null;
        if (pc instanceof GamePlayer) {
          gp = (GamePlayer) pc;
        }

        long timeSinceRead = System.currentTimeMillis() - c._last_read_time;
        long timeSinceWrite = System.currentTimeMillis() - c._last_write_time;

        if ((c._dead)) { // Forget about the clint...
          _cat.warning("Client " + c + " killed");
          c.kill();
          c.setDisconnected();
          continue;
        } // if state died // Forget about the clint...

        if (timeSinceRead > 35 * _timeout) {
          _cat.info("Client " + c + " expired...read=" + (timeSinceRead / 1000));
        }
        else if (timeSinceRead > 40 * _timeout) {
          // Forget about the client...
          _cat.warning("Client " + c + " overdue... read=" + (timeSinceRead / 1000));
          kill = true;
        }

        if (timeSinceWrite > 180 * _timeout) {
          // Forget about the client...
          _cat.warning("Client " + c + " expired... write=" +
                    (timeSinceRead / 1000));
          try {
                        GameProcessor.broadcast(gp.session(),
                com.agneya.util.Base64.encodeString("Connection is being closed because of long inactivity"));
          }
          catch (Throwable t) {}

          // Remove connection and signal the player to terminate and
          // destroy the session
          kill = true;
        }
        else if (timeSinceWrite > 90 * _timeout) {
          _cat.info("Client " + c + " overdue... write=" +
                    (timeSinceRead / 1000));
        }

        if (c.comQSize() == _rqs) {
          _cat.info("Client " + c + " command queue ready to burst");
        }
        else if (c.comQSize() > _rqs) {
          _cat.warning("Client " + c + " command queue burst " + c);
          //_cat.warning(c._com);
          //c.setDisconnected();
          kill = true;
        }

        if (c.respQSize() == _rqs) {
          _cat.info("Client " + c + " response queue ready to burst");
        }
        else if (c.respQSize() > _rqs) {
          _cat.warning("Client " + c + " response queue burst");
          //c.setDisconnected();
          kill = true;
        }

        if (c.isDisconnected()) {
          _cat.finest("The handler is disconnected " + c._id);
          kill = true;
        }


        boolean gridOver = true; // Whether GP has a tourny presence
        if (gp != null && kill) {
          // check if there is a presence on tourny
          for (Enumeration enumz = gp.getPresenceList(); enumz.hasMoreElements(); ) {
            CricketPresence p = (CricketPresence) enumz.nextElement();
            p.setDisconnected();
            _cat.finest("Presence = " + p);
            _cat.finest("Tourny presence " + p);
            Game g = Game.game(p.getGID());
            if (g != null && !g.isGRIDOver(p.getGRID())) {
              gridOver = false;
            }
            if (!gridOver) {
              if (!c.isDisconnected()) {
                c.setDisconnected();
                _cat.info(p + "has presence on tourny ");
              }
            }
            else {
              _cat.info("Marking tourny client as dead as tourny is over " + p);
              gp.kill(p, true);
            }
          }
        }


        //check if the attached gameplayer is active
        if (gp == null) {
          _cat.info("GP == null killing=" + c);
          c.kill();
          continue;
        }
        else if (kill && (gp.presenceCount() <= 0)) { // check if there is any presence
          _cat.info("No presence killing=" + gp);
          gp.kill();
          c.kill();
          continue;
        }
        else if (kill) {
          _cat.info("Presence remaining " + gp);
        }

        // check if presence is making moves on time
        for (Enumeration enumz = gp.getPresenceList(); enumz.hasMoreElements(); ) {
          CricketPresence t = (CricketPresence) enumz.nextElement();
          _cat.finest("Loop all presence " + t);
          if (t.isObserver()) {
            continue;
          }
          Game g = Game.game(t.getGID());
          if (!(g instanceof Cricket)) {
            continue;
          }
          Cricket pg = (Cricket) g;
          if (g == null && !t.isTournyPresence()) {
            _cat.finest("Removing presence " + t);
            gp.removePresence(t);
            continue;
          }
          /**if (t.isIdleGCViolated() && !t.isTournyPresence() && !t.isShill() &&
              pg.allPlayers(0).length == pg.maxPlayers()) {
            _cat.warn("Violated idle game count " + t);
            GameProcessor.deliverResponse(gp.leaveGameOnly(t, false));
            gp.removePresence(t);
            continue;
                     }**/
          if (t.isResponseReq()) {
            _cat.info(_timeout + " Checking if time exceeded for move " + gp);
            if ((System.currentTimeMillis() - t._start_wait) > _timeout ||
                t.isDisconnected()) {
              _cat.info("Time exceeded for move " + gp);
              if (t.getGID().length() > 0) {
                _cat.finest(gp + "  playing on   " + t.getGID());
                long move = t._nextMove;
                //long move = g._nextMove;
                double[][] amt = g._nextMoveAmt;
                if ((move & Moves.JOIN) > 0) {
                  // the waiter did not respond to the join move
                  //pg.alertNextWaiter();
                }
                else if (move > 0) {
                  _cat.info(" Move = " + Moves.stringValue(move) + ", All moves=" + g._nextMove);
                  if (g.type().isTourny()) {
                    gp.handler().makeMove(t.getGID(), g.grid(), move, amt);
                  }
                }
              }
              else {
                _cat.info("Invalid game id " + gp);
              }
              if (!g.type().isTourny()) {
                Response[] r = gp.leaveGameOnly(t, true);
                                GameProcessor.deliverResponse(r);
                gp.deliver(new ResponseGameEvent(1, r[0].getBroadcast()));
              }
            } // response required exceeded timeout
          } // response required
        } // loop all presences

        Enumeration pl = gp.getPresenceList();
        while (pl.hasMoreElements()) {
          _cat.finest(pl.nextElement().toString());
        }
      } //END MAIN WHILE LOOP

      /** DEBUG
             Enumeration pl = GamePlayer.getGPList();
             while (pl.hasMoreElements()) {
        _cat.finest(pl.nextElement());
             }
       **/
      // Game maintenance

      Game g[] = Game.listAll();
      for (int i = 0; i < g.length; i++) { // LOOP THRU ALL GAMES
        if (g[i] instanceof Cricket) {
          Cricket pg = (Cricket) g[i];
          CricketPresence[] v = pg.allPlayers( -1);

          /**if (g[i].type().isMTTTourny()) {
            TournyTableInterface tti = (TournyTableInterface) g[i];
            if (tti.tournyWaiting()) {
              Response r = new GameDetailsResponse(pg, true);
              _cat.finest("Sending game details response " + r.getBroadcast());
              GameProcessor.deliverResponse(r);
            }
                     }
           else { // remove players with null handlers if not tourny players**/for (int
              j = 0; j < v.length; j++) {
            _cat.finest("Presence = " + v[j]);
            if (v[j].player() instanceof GamePlayer) {
              GamePlayer gp = (GamePlayer) v[j].player();
              if (gp.handler() == null || gp.handler().isKilled() || gp.isDead()) {
                v[j].setDisconnected();
                _cat.warning("Found player with null handler " + gp);
                gp.kill();
                pg.leave(v[j], false);
              }
            }
          }
          //}

          /**Presence[] vw = pg.getWaiters();
                     int response_awaited = 0;
                     for (int j = 0; j < vw.length; j++) {
            _cat.finest("Waiter Presence = " + vw[j]);
            if (vw[j].player() instanceof GamePlayer) {
              GamePlayer gp = (GamePlayer) vw[j].player();
              if (vw[j].isResponseReq()) {
                _cat.finest("Waiting for join response from " + vw[j]);
                response_awaited++;
              }
              if (gp.handler() == null || gp.handler().isKilled()) {
                _cat.warning("Found waiter with null handler " + gp);
                pg.waiterRemove(vw[j]);
                gp.kill();
                pg.leave(vw[j], false);
              }
            }
                     }
           if (pg.allPlayers( -1).length + response_awaited < pg.maxPlayers()) {
            // send the join to next waiter
            pg.alertNextWaiter();
                     }**/

          CricketPresence[] vo = pg.getObservers();
          for (int j = 0; j < vo.length; j++) {
            _cat.finest("Observer Presence = " + vo[j]);
            if (vo[j].player() instanceof GamePlayer) {
              GamePlayer gp = (GamePlayer) vo[j].player();
              if (gp.handler() == null || gp.handler().isKilled()) {
                _cat.warning("Found observer with null handler " + gp);
                //pg.waiterRemove(vo[j]);
                gp.kill();
                pg.leave(vo[j], false);
              }
            }
          }

          if (g[i].lastMoveDelta() > 10 * _timeout) {
            // refresh table
            Response r = new GameDetailsResponse(pg, true);
            _cat.finest("Sending game details response " + r.getBroadcast());GameProcessor.deliverResponse(r);
            pg.setTimeDelta();
          }
          else if (g[i].lastMoveDelta() > 30 * _timeout) {
            CricketPresence[] pv = pg.allPlayers( -1);
            if (pg.reRunCondition()) {
              _cat.warning("Game stuck " + g[i]);
              // try to restart
              pg.start();
              for (int k = 0; k < pv.length; k++) {
                _cat.warning("Players stuck=" + pv[k]);
              }
            }
            else if (pv.length > 0) {
              // refresh table
              Response r = new GameDetailsResponse(pg, true);
              _cat.finest("Sending game details response " + r.getBroadcast());GameProcessor.deliverResponse(r);
              pg.setTimeDelta();
            }
          }
          else if (g[i].lastMoveDelta() > 30 * _timeout) {
            _cat.warning("Destroying game " + g[i]);
            pg.destroy();
          }

        }
      } // END LOOP ALL GAMES

      /**
       if ( (System.currentTimeMillis() - g[i]._last_move_ts) > 30 * _timeout ) {
        _cat.warning("Stopping the game " + g[i].id() +
       " as it was stopped for some time and there are no players");
       } **/

      System.gc();
    }
    catch (Throwable e) {
      // MUST catch Exception, otherwise the main-thread will
      // stop processing incoming requests!!!
      _cat.warning("Unexpected Throwable while running maintenance thread "+  e);
    }

  } //testforExpiredClients
}
