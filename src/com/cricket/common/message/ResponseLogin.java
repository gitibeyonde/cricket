package com.cricket.common.message;

import java.util.*;
import com.agneya.util.*;

public class ResponseLogin
    extends Response {
  private int _ug;
  private int _pts;
  private int _prf;
  private int _rank;
  private double _loss_limit;
  private Vector _tidv;
  private Vector _posv;
  private Vector _teamv;

  public ResponseLogin(int result, int ug, int points, int prf, int rank, double rll) {
    super(result, R_LOGIN);
    _ug = ug;
    _pts = points;
    _prf = prf;
    _rank = rank;
    _loss_limit = rll;
  }

  public ResponseLogin(int result, int ug, int points, int prf, int rank, double rll, Vector tidv,
                       Vector posv, Vector teamv) {
    super(result, R_LOGIN);
    _ug = ug;
    _pts = points;
    _prf = prf;
    _rank = rank;
    _tidv = tidv;
    _posv = posv;
    _teamv = teamv;
    _loss_limit = rll;
  }

  /**
   * This constructor is used to create a register response
   */
  public ResponseLogin(boolean isReg, int result, int ug, int points, int prf, int rank, double rll) {
    super(result, R_REGISTER);
    _ug = ug;
    _pts = points;
    _prf = prf;
    _rank = rank;
    _loss_limit = rll;
  }

  public ResponseLogin(HashMap str) {
    super(str);
    if (getResult() == 1 ) {
      _ug = Integer.parseInt( (String) _hash.get("UG"));
      _pts = Integer.parseInt( (String) _hash.get("POINTS"));
      _prf = Integer.parseInt( (String) _hash.get("PRF"));
      _rank = Integer.parseInt( (String) _hash.get("RANK"));
      _loss_limit = Double.parseDouble( (String) _hash.get("RLL"));
      String tcnt =  (String) _hash.get("TCNT");
      if (tcnt!=null){
        int cnt =  Integer.parseInt(tcnt);
        _tidv = new Vector();
        _posv = new Vector();
        _teamv = new Vector();
        for (int i=0;i<cnt;i++){
          String tstr = (String) _hash.get("TABLE" + i);
          String[] tdet = tstr.split("\\|");
          _tidv.add(tdet[0]);
          _posv.add(tdet[1]);
          _teamv.add(tdet[2]);
        }
      }
    }
  }

  public int getPoints() {
    return _pts;
  }

  public int getGender() {
    return _ug;
  }

  public int getPreferences() {
    return _prf;
  }

  public int getRank() {
    return _rank;
  }

  public double getRealLossLimit() {
    return _loss_limit;
  }

  public int getTableCount(){
    return _posv.size();
  }

  public int getPos(int i){
    return Integer.parseInt((String)_posv.elementAt(i));
  }

  public String getTeam(int i){
    return (String)_teamv.elementAt(i);
  }

  public String getTid(int i){
    return (String)_tidv.elementAt(i);
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&UG=").append(_ug);
    str.append("&POINTS=").append(_pts);
    str.append("&PRF=").append(_prf);
    str.append("&RANK=").append(_rank);
    str.append("&RLL=").append(Utils.getRoundedString(_loss_limit));
    if (_tidv != null) {
      str.append("&TCNT=").append(_tidv.size());
      for (int i = 0; _tidv != null && i < _tidv.size(); i++) {
        str.append("&TABLE" +
            i).append("=").append(_tidv.get(i)).append("|").append(_posv.get(i)).append("|").append(_teamv.get(i));
      }
    }
    return str.toString();
  }

  public boolean equal(ResponseLogin r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
