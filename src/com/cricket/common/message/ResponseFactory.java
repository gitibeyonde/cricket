package com.cricket.common.message;

public class ResponseFactory {
  public String _com;

  public ResponseFactory(String str) {
    _com = str;
  }

  public Response getResponse() {
    if (_com == null) {
      return null;
    }
    Response r = new Response(_com);
    switch (r._response_name) {
      case Response.R_GAMELIST:
        return new ResponseGameList(r.getNVHash());
      case Response.R_GAMEDETAIL:
        return new ResponseGameDetail(r.getNVHash());
      case Response.R_TOURNYLIST:
        return new ResponseTournyList(r.getNVHash());
      case Response.R_MOVE:
        if (r._result == Response.E_BROKE ){
          return new ResponseInt(r.getNVHash());
        }
        else {
          return new ResponseGameEvent(r.getNVHash());
        }
      case Response.R_MESSAGE:
        return new ResponseMessage(r.getNVHash());
      case Response.R_TOURNYDETAIL:
        return new ResponseTournyDetail(r.getNVHash());
      case Response.R_TOURNYMYTABLE:
        return new ResponseTournyMyTable(r.getNVHash());
      case Response.R_CONFIG:
        return new ResponseConfig(r.getNVHash());
      case Response.R_REGISTER:
      case Response.R_LOGIN:
        return new ResponseLogin(r.getNVHash());
      case Response.R_BUYCHIPS:
        return new ResponseBuyChips(r.getNVHash());
      case Response.R_GET_CHIPS_INTO_GAME:
        return new ResponseGetChipsIntoGame(r.getNVHash());
      case Response.R_TOURNYREGISTER:
      case Response.R_WAITER:
        return new ResponseString(r.getNVHash());
      case Response.R_PLAYERSTATS:
        return new ResponseString(r.getNVHash());
      case Response.R_PING:
        return new ResponsePing(r.getNVHash());
      case Response.R_PLAYERSHIFT:
        return new ResponsePlayerShift(r.getNVHash());
      case Response.R_PROFESSIONAL_PLAYERS:
      case Response.R_TEAM_MANAGER:
        return new ResponseString(r.getNVHash());



      default:
        return r;
    }
  }

}
