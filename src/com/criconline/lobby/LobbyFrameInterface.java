package com.criconline.lobby;

import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.plaf.metal.MetalTheme;

import com.criconline.exceptions.AuthenticationException;

public interface LobbyFrameInterface {

  /** background for frame */
  public ImageIcon getBackground();

  /** LookAndFeel for frame */
  public MetalTheme getLookAndFeel();

  /** menu for frame */
  public JMenuBar getMenu();

  /** all other visual components for frame */
  public Object[] getComponents();

  /**  window clising event  */
  public WindowListener getWindowCloseEvent();

  /** call after constructor */
  public void init(JFrame frame);

  /** for login */
  public void login(String login, char[] password) throws
      AuthenticationException;

  /** for new user register*/
  public void userRegister(String user, String password, String email,
                           int gender, String bc, String sc) throws
      AuthenticationException;


  public String getLoginMessage();


}
