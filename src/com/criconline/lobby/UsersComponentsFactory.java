package com.criconline.lobby;

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import com.criconline.ClientConfig;
import com.criconline.Utils;

/** Factory for creating all user's component on Lobby
 *
 * @author Halt
 */
public class UsersComponentsFactory {

  public static JButton createJBCashier(ActionListener listener) {
	ImageIcon anImage;
	JButton jbCashier;

	anImage =
		Utils.getIcon(ClientConfig.IMG_LOBBY_CASHIER_UP);
	jbCashier = new JButton(anImage);

	jbCashier.setRolloverEnabled(true);
	anImage =
		Utils.getIcon(ClientConfig.IMG_LOBBY_CASHIER_PRESSED);
	jbCashier.setPressedIcon(anImage);
	anImage =
		Utils.getIcon(ClientConfig.IMG_LOBBY_CASHIER_DOWN);
	jbCashier.setRolloverIcon(anImage);
	anImage =
		Utils.getIcon(ClientConfig.IMG_LOBBY_CASHIER_DE);
	jbCashier.setDisabledIcon(anImage);
	jbCashier.setFocusPainted(false);
	jbCashier.setBorderPainted(false);
	jbCashier.setContentAreaFilled(false);
	jbCashier.setMargin(new Insets(0, 0, 0, 0));
	jbCashier.addActionListener(listener);
	jbCashier.setBounds(400-47, 504-11, 167, 48);

	return jbCashier;
  }

  /**public static JButton createJBNetPractise(ActionListener listener) {
                ImageIcon anImage;
                JButton jbSitTable;

                anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_UP);
                jbSitTable = new JButton(anImage);
                jbSitTable.setRolloverEnabled(true);
                anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_PRESSED);
                jbSitTable.setPressedIcon(anImage);
                anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_DOWN);
                jbSitTable.setRolloverIcon(anImage);
                anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_DE);
                jbSitTable.setDisabledIcon(anImage);
                jbSitTable.setFocusPainted(false);
                jbSitTable.setBorderPainted(false);
                jbSitTable.setContentAreaFilled(false);
                jbSitTable.setMargin(new Insets(0, 0, 0, 0));
                jbSitTable.addActionListener(listener);
                jbSitTable.setBounds(353, 504 - 11, 168, 49);
                jbSitTable.setEnabled(true);

                return jbSitTable;
        }**/


	public static JButton createJBSitTable(ActionListener listener) {
		ImageIcon anImage;
		JButton jbSitTable;

		anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_UP);
		jbSitTable = new JButton(anImage);
		jbSitTable.setRolloverEnabled(true);
		anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_PRESSED);
		jbSitTable.setPressedIcon(anImage);
		anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_DOWN);
		jbSitTable.setRolloverIcon(anImage);
		anImage = Utils.getIcon(ClientConfig.IMG_LOBBY_SIT_DE);
		jbSitTable.setDisabledIcon(anImage);
		jbSitTable.setFocusPainted(false);
		jbSitTable.setBorderPainted(false);
		jbSitTable.setContentAreaFilled(false);
		jbSitTable.setMargin(new Insets(0, 0, 0, 0));
		jbSitTable.addActionListener(listener);
		jbSitTable.setBounds(9, 504 - 11, 168, 49);
		jbSitTable.setEnabled(false);

		return jbSitTable;
	}

	public static JToggleButton createJBWait(ActionListener listener) {
		ImageIcon anImage;
		JToggleButton jbWait;

		anImage = Utils.getIcon(ClientConfig.IMG_WAIT_EN);
		jbWait = new JToggleButton(anImage);
		jbWait.setRolloverEnabled(true);
		anImage = Utils.getIcon(ClientConfig.IMG_WAIT_MO);
		jbWait.setRolloverIcon(anImage);
		anImage = Utils.getIcon(ClientConfig.IMG_WAIT_PR);
		jbWait.setPressedIcon(anImage);
		jbWait.setSelectedIcon(anImage);
		anImage = Utils.getIcon(ClientConfig.IMG_WAIT_DE);
		jbWait.setDisabledIcon(anImage);
		jbWait.setFocusPainted(false);
		jbWait.setBorderPainted(false);
		jbWait.setContentAreaFilled(false);
		jbWait.setMargin(new Insets(0, 0, 0, 0));
		jbWait.setBounds(181, 504 - 11, 168, 49);
		jbWait.addActionListener(listener);
		jbWait.setEnabled(false);

		return jbWait;
	}


        public static JButton createTournyRegButton(ActionListener listener) {
                ImageIcon anImage;
                JButton jb;

                anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_REG_UP);
                jb = new JButton(anImage);
                jb.setRolloverEnabled(true);
                anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_REG_PRESSED);
                jb.setPressedIcon(anImage);
                anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_REG_DOWN);
                jb.setRolloverIcon(anImage);
                anImage = Utils.getIcon(ClientConfig.IMG_TLOBBY_REG_DE);
                jb.setDisabledIcon(anImage);
                jb.setFocusPainted(false);
                jb.setBorderPainted(false);
                jb.setContentAreaFilled(false);
                jb.setMargin(new Insets(0, 0, 0, 0));
                jb.addActionListener(listener);
                jb.setBounds(209, 493, 168, 49);
                jb.setEnabled(false);

                return jb;
        }


        public static JButton createMyPitchButton(ActionListener listener) {
                ImageIcon anImage;
                JButton jb;

                anImage = Utils.getIcon(ClientConfig.IMG_MYPITCH_UP);
                jb = new JButton(anImage);
                jb.setRolloverEnabled(true);
                anImage = Utils.getIcon(ClientConfig.IMG_MYPITCH_PRESSED);
                jb.setPressedIcon(anImage);
                anImage = Utils.getIcon(ClientConfig.IMG_MYPITCH_DOWN);
                jb.setRolloverIcon(anImage);
                anImage = Utils.getIcon(ClientConfig.IMG_MYPITCH_DE);
                jb.setDisabledIcon(anImage);
                jb.setFocusPainted(false);
                jb.setBorderPainted(false);
                jb.setContentAreaFilled(false);
                jb.setMargin(new Insets(0, 0, 0, 0));
                jb.addActionListener(listener);
                jb.setBounds(409, 493, 168, 49);
                jb.setEnabled(false);

                return jb;
        }

        public static JButton createRefreshButton(ActionListener listener) {
                ImageIcon anImage;
                JButton jb;

                anImage = Utils.getIcon(ClientConfig.IMG_REFRESH_UP);
                jb = new JButton(anImage);
                jb.setRolloverEnabled(true);
                anImage = Utils.getIcon(ClientConfig.IMG_REFRESH_PRESSED);
                jb.setPressedIcon(anImage);
                anImage = Utils.getIcon(ClientConfig.IMG_REFRESH_DOWN);
                jb.setRolloverIcon(anImage);
                anImage = Utils.getIcon(ClientConfig.IMG_REFRESH_DE);
                jb.setDisabledIcon(anImage);
                jb.setFocusPainted(false);
                jb.setBorderPainted(false);
                jb.setContentAreaFilled(false);
                jb.setMargin(new Insets(0, 0, 0, 0));
                jb.addActionListener(listener);
                jb.setBounds(609, 493, 168, 49);
                jb.setEnabled(true);

                return jb;
        }

}
