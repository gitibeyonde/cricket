package com.cricket.common.message;

import java.util.*;

public class CommandRegister
    extends Command {
  String _name;
  String _password;
  String _email;
  int _gender;
  String _bonus_code;
  String _source;
  String _affiliate;

  public CommandRegister(String session, String name, String pass, String email,
                         int gender, String bc, String sc, String aff) {
    super(session, Command.C_REGISTER);
    _name = name;
    _password = pass;
    _email = email;
    _gender = gender;
    _bonus_code = bc;
    _source = sc;
    _affiliate = aff;
  }

  public CommandRegister(String session, String name, String pass, String email,
                         int gender, String bc, String sc) {
    super(session, Command.C_REGISTER);
    _name = name;
    _password = pass;
    _email = email;
    _gender = gender;
    _bonus_code = bc;
    _source = sc;
    _affiliate = "admin";
  }

  public CommandRegister(String str) {
    super(str);
    _name = (String) _hash.get("UN");
    _password = (String) _hash.get("UP");
    _email = (String) _hash.get("UE");
    _gender = Byte.parseByte( (String) _hash.get("UG"));
    _bonus_code = (String) _hash.get("BC");
    _source = (String) _hash.get("SC");
    _affiliate = (String) _hash.get("AFF");
  }

  public CommandRegister(HashMap str) {
    super(str);
    _name = (String) _hash.get("UN");
    _password = (String) _hash.get("UP");
    _email = (String) _hash.get("UE");
    _gender = Byte.parseByte( (String) _hash.get("UG"));
    _bonus_code = (String) _hash.get("BC");
    _source = (String) _hash.get("SC");
    _affiliate = (String) _hash.get("AFF");
  }

  public String getUserName() {
    return _name;
  }

  public String getPassword() {
    return _password;
  }

  public String getEmail() {
    return _email;
  }

  public String getBonusCode() {
    return _bonus_code;
  }

  public String getSource() {
    return _source;
  }

  public int getGender() {
    return _gender;
  }

  public String getAffiliate() {
    return _affiliate;
  }

  public String toString() {
    StringBuffer str = new StringBuffer(super.toString());
    str.append("&UN=").append(_name).append("&UP=").append(_password).append(
        "&UE=").append(_email).append("&UG=").append(_gender).append("&BC=").
        append(_bonus_code).append("&SC=").append(_source).append("&AFF=").
        append(_affiliate);
    return str.toString();
  }

}
