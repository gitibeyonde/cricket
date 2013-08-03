package com.criconline;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.criconline.resources.Bundle;
import java.util.logging.Logger;
//import com.criconline.shared.SharedConstants;

public class Utils {

  static Logger _cat = Logger.getLogger(Utils.class.getName());

  private static String codebase = "http://www.cricketparty.com";

  public static Font standartFont = new Font("SansSerif", Font.BOLD, 12);
  public static Font smallFont = new Font("Arial", Font.PLAIN, 10);
  public static Font standartButtonFont = new Font("SansSerif", Font.BOLD, 12);
  public static Font bigButtonFont = new Font("Verdana", Font.BOLD, 14);
  public static Font bigFont = new Font("Verdana", Font.BOLD, 18);

  public static Font smallButtonFont = new Font("SansSerif", Font.BOLD, 8);
  public static Font chatFont = new Font("SansSerif", Font.PLAIN, 12);
  public static Font smallChatFont = new Font("SansSerif", Font.PLAIN, 11);

  public static Font sliderFont = new Font("Arial", Font.PLAIN, 11);
  public static Font sliderLabel = new Font("Arial", Font.PLAIN, 11);

  public static Font bubbleFont =new Font("SansSerif", Font.PLAIN, 10);
  public static Font normalFont = new Font("Tahoma", Font.PLAIN, 11);
  public static Font boldFont = new Font("Tahoma", Font.BOLD, 11);

  public static Font nameFont = new Font("Arial", Font.PLAIN, 10);
  public static Font myNameFont = new Font("Verdana", Font.BOLD, 14);
  public static Font chipsFont = new Font("Arial", Font.PLAIN, 8);

  public static Font tipFont = new Font("Arial", Font.ITALIC, 12);

  private static Color colors[][] = {
      {
      new Color(99, 59, 41), new Color(151, 102, 67), new Color(199, 161, 140),
      new Color(100, 54, 44), new Color(116, 50, 54), new Color(35, 84, 54)}
      , {
      new Color(19, 109, 31), new Color(141, 152, 57), new Color(189, 211, 130),
      new Color(90, 104, 34), new Color(106, 100, 44), new Color(25, 134, 44)}
  };

  public static Color greenColor = new Color(100, 100, 100, 100);//158, 210, 114, 196);
  public static Color lobbyCellBgrnd = new Color(121, 121, 255, 196);//158, 210, 114, 196);

  public static ScrollBarUI getRoomScrollBarUI() {
    return new MyScrollBarUI(0);
  }

  public static ScrollBarUI getLobbyScrollBarUI() {
    return new MyScrollBarUI(1);
  }


  /**public static final String getCashierUrl(String userid, String session) {
    try {
      session = URLEncoder.encode(session, "UTF-8");
    }
    catch (Exception e) {}
    return getCodebase() + Bundle.getBundle().getString("cashier.url") + "&" +
        SharedConstants.USERID + "=" + userid;//+ "&" + SharedConstants.SESSIONID + "=" + session;
  }**/

 
 /** public static Rectangle getChipsArea(Chip[] chips) {
    if (chips == null || chips.length == 0) {
      return null;
    }
    Rectangle r = null;
    for (int i = 0; i < chips.length; i++) {
      Rectangle tmp = chips[i].getRealCoords();
      if (tmp != null) {
        if (r == null) {
          r = new Rectangle(tmp);
        }
        else {
          r.add(tmp);
        }
      }
    }
    return r;
  }**/
  
  public static final String getHelpUrl() {
	    return "" + Bundle.getBundle().getString("doc.url");
	  }

	  public static final String getValidateUrl() {
	    return "" + Bundle.getBundle().getString("validate.url");
	  }

	  public static final String getInviteUrl() {
	    return "" + Bundle.getBundle().getString("invite.url");
	  }

	  public static final String getProfileUrl() {
	    return "" + Bundle.getBundle().getString("profile.url") ;
	  }

	  public static boolean showUrl(String url) {
		  return false;
	  }

  static class MyScrollBarUI
      extends BasicScrollBarUI {

    private int no;

    public MyScrollBarUI(int no) {
      super();
      this.no = no;
      thumbColor = colors[no][0];
      thumbDarkShadowColor = colors[no][1];
      thumbHighlightColor = colors[no][2];
      thumbLightShadowColor = colors[no][3];
      trackColor = colors[no][4];
      trackHighlightColor = colors[no][5];
    }

    protected void configureScrollBarColors() {
      LookAndFeel.installColors(scrollbar, "ScrollBar.background",
                                "ScrollBar.foreground");
      thumbColor = colors[no][0];
      thumbDarkShadowColor = colors[no][1];
      thumbHighlightColor = colors[no][2];
      thumbLightShadowColor = colors[no][3];
      trackColor = colors[no][4];
      trackHighlightColor = colors[no][5];
    }

    protected JButton createDecreaseButton(int orientation) {
      return new BasicArrowButton(orientation,
                                  thumbColor,
                                  thumbLightShadowColor,
                                  thumbDarkShadowColor,
                                  thumbHighlightColor);
    }

    protected JButton createIncreaseButton(int orientation) {
      return new BasicArrowButton(orientation,
                                  thumbColor,
                                  thumbLightShadowColor,
                                  thumbDarkShadowColor,
                                  thumbHighlightColor);
    }
  }

  public static ImageIcon getIcon(String path) {
    //System.out.println("path=" + Utils.class.getResource(path));
    return (new ImageIcon(Utils.class.getResource(path)));
  }
}
