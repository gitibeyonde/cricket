package com.criconline.lobby;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

//import com.criconline.client.Utils;
import com.criconline.ClientConfig;
import com.criconline.pitch.ImageStripData;
import com.criconline.SoundManager;

/**
 * This is the client application entry point.
 * @author Kom
 * @version 1.0
 */

public class Lobby {

  static Logger _cat = Logger.getLogger(Lobby.class.getName());
  public static void main(String[] args) throws Exception {

    System.setProperty("proxySet", "true");
    System.setProperty("proxyHost", args[0]);
    System.setProperty("proxyPort", args[1]);
    if (args.length > 2){
      System.setProperty("username", args[2]);
      System.setProperty("password", args[3]);
      System.out.println(args[2] + ", " + args[3]);
    }


    ImageStripData.instance();
    new SplashWindow(null, 10000);

    //SoundManager.playEffectRepeatable(SoundManager.INLOBBY);

    //replaceTrustManager();
    if (args.length > 0) {
      LobbyUserImp lobbyUserImp = new LobbyUserImp(args[0], Integer.parseInt(args[1]));
      _cat.fine("Created lobby user impl" + args[0]);
      LobbyFrame frame = new LobbyFrame(lobbyUserImp);
    }
    else {
      System.err.println("Usage: Lobby URL");
    }
  }
}
