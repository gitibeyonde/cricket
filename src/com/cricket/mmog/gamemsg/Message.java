package com.cricket.mmog.gamemsg;

import java.io.*;
import com.cricket.mmog.resp.*;

public interface Message
    extends Serializable {

  public byte id();

  public String gameId();

  public Response[] interpret();

}
