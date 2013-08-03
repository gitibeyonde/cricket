package com.cricket.common.message;

public class CommandFactory {

  public synchronized static Command getCommand(Command c) {
    try {
      Command tc;
      switch (c.getCommandName()) {
        case Command.C_REGISTER:
          tc = new CommandRegister(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_LOGIN:
          tc = new CommandLogin(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_GAMELIST:
          tc = new CommandGameList(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_GAMEDETAIL:
          tc = new CommandGameDetail(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_TOURNYREGISTER:
          tc = new CommandTournyRegister(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_TOURNYDETAIL:
          tc = new CommandTournyDetail(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_TOURNYMYTABLE:
          tc = new CommandTournyMyTable(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_MOVE:
          tc = new CommandMove(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_MESSAGE:
          tc = new CommandMessage(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_BUYCHIPS:
          tc = new CommandBuyChips(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_GET_CHIPS_INTO_GAME:
          tc = new CommandGetChipsIntoGame(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_PREFERENCES:
        case Command.C_SIT_IN:
        case Command.C_WAITER:
        case Command.C_SIT_OUT:
        case Command.C_TURN_DEAF:
          tc = new CommandString(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_PLAYERSTATS:
          tc = new CommandString(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_CARD:
          tc = new CommandChargeCard(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_VOTE:
          tc = new CommandVote(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_PLAYERSHIFT:
          tc = new CommandPlayerShift(c.getNVHash());
          tc.handler(c.handler());
          return tc;

        case Command.C_PROFESSIONAL_PLAYERS:
        case Command.C_TEAM_MANAGER:

        default:
          return c;
      }
    }
    catch (Throwable e) {
      e.printStackTrace();
      System.out.println(c);
      return new Command(c.session(), Command.A_ILLEGAL);
    }
  }

}
