/*
 * Created on Jul 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.criconline.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.criconline.Utils;
import com.criconline.server.ServerProxy;
import com.criconline.resources.Bundle;
import com.criconline.pitch.PitchSkin;

/**
 * @author
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MessageFactory {
	public static JDialog dialog = null;
/**
	public static String getStringWindowOne (RoomSkin skin, String title,
									String message, String initialValue, JFrame frame, ServerProxy lobbyServer) {
		MyInputDialog myInputDialog = new MyInputDialog(skin, title, message, initialValue, frame, true, lobbyServer);
		myInputDialog.setVisible(true);
		dialog = null;
		return myInputDialog.reply;
	}

	public static String getStringWindowOneYesNo (RoomSkin skin, String title,
									String message, String initialValue, JFrame frame, ServerProxy lobbyServer) {
		MyInputDialogYesNo myInputDialogYesNo = new MyInputDialogYesNo(skin, title, message, initialValue, frame, true, lobbyServer);
		myInputDialogYesNo.setVisible(true);
		dialog = null;
		return myInputDialogYesNo.reply;
	}

	public static String getStringWindowMany (RoomSkin skin, String title,
									String message, String initialValue, JFrame frame, ServerProxy lobbyServer) {
		MyInputDialog myInputDialog = new MyInputDialog(skin, title, message, initialValue, frame, false, lobbyServer);
		myInputDialog.setVisible(true);
		return myInputDialog.reply;
	}**/

}

class MyInputDialog extends JDialog implements ActionListener {

	protected com.criconline.server.ServerProxy lobbyServer;
	protected boolean unique;
	protected static Dimension screenSize;
	protected static Dimension frameSize;
	protected static Point framePos;
	static {
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frameSize = new Dimension(400, 175);
		framePos = new Point((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
	}

	ImageIcon icon;
	int hinc = -1;
	int winc = -1;
	JTextField field;
	JButton bOk, bClose, bCashier;

	String reply = null;


	public MyInputDialog (PitchSkin skin, String title,
							String message, String initialValue, JFrame frame, boolean unique, ServerProxy lobbyServer) {
		super (frame);
		this.unique = unique;
		this.lobbyServer    = lobbyServer;
		initComponents();
		setTitle(title);
		setModal(true);
		icon = skin.getBottonPanelBackground();
		hinc = icon.getIconHeight();
		winc = icon.getIconWidth();
		Container pane = getContentPane();

		JPanel tmpPanel = new JPanel(new BorderLayout()) {
			public void paintComponent(Graphics g) {
				int w = getWidth();
				int h = getHeight();
				for (int i = 0; i < h + hinc; i = i + hinc)
					for (int j = 0; j < w + winc; j = j + winc)
						icon.paintIcon(this, g, j, i);
			}
		};

		JLabel label = new JLabel (message);
		label.setForeground(Color.YELLOW);
		tmpPanel.add (label, BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.setOpaque(false);
		if (field != null) {
			field.addActionListener(this);
			field.setText(initialValue);
			field.setSelectionStart(0);
			field.setSelectionEnd(initialValue.length());
			field.setSelectionColor(Color.LIGHT_GRAY);
		}
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		if (field != null)
			panel.add(field, BorderLayout.CENTER);
		panel.add(Box.createHorizontalStrut(20), BorderLayout.WEST);
		panel.add(Box.createHorizontalStrut(20), BorderLayout.EAST);

		southPanel.add(panel, BorderLayout.NORTH);
		panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		panel.setOpaque(false);
		panel.add(bOk);
		panel.add(bClose);
		panel.add(bCashier);
		southPanel.add(panel, BorderLayout.SOUTH);

		tmpPanel.add(southPanel, BorderLayout.SOUTH);
		tmpPanel.add(Box.createHorizontalStrut(20), BorderLayout.WEST);
		tmpPanel.add(Box.createHorizontalStrut(20), BorderLayout.EAST);
		pane.add(tmpPanel);
//---------
		setBounds(framePos.x, framePos.y, frameSize.width, frameSize.height);
		setResizable(false);
//---------
		if (unique)
			MessageFactory.dialog = this;
//////////////////////////////////////////////////////////////////		setVisible(true);
	}

	protected void initComponents() {
		field = new JTextField ();
		field.setOpaque(false);
		field.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		field.setForeground(Color.WHITE);
		ResourceBundle bundle = Bundle.getBundle();

		bCashier = new JButton(bundle.getString("cashier.button"));
		bCashier.setOpaque(true);
		bCashier.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
		bCashier.setForeground(Color.WHITE);
		bCashier.setFocusPainted(false);
//		bOk.setBorderPainted(false);
		bCashier.setContentAreaFilled(false);

		bOk = new JButton(bundle.getString("ok"));
		bOk.setOpaque(true);
		bOk.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
		bOk.setForeground(Color.WHITE);
		bOk.setFocusPainted(false);
//		bOk.setBorderPainted(false);
		bOk.setContentAreaFilled(false);

		bClose = new JButton(bundle.getString("cancel"));
		bClose.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
		bClose.setForeground(Color.WHITE);
		bClose.setFocusPainted(false);
//		bClose.setBorderPainted(false);
		bClose.setContentAreaFilled(false);

		bOk.addActionListener(this);
		bClose.addActionListener(this);
		bCashier.addActionListener(this);
		field.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == bClose)
			reply = null;
		else if (source == bOk || source == field)
			reply = field.getText();
		else if (source == bCashier) {
			reply = null;
			//Utils.showUrl(Utils.getCashierUrl(lobbyServer._name, lobbyServer.getTicket()));
		}
		if (unique)
			MessageFactory.dialog = null;
		dispose();
	}
}

class MyInputDialogYesNo extends MyInputDialog implements ActionListener {

	public MyInputDialogYesNo(PitchSkin skin, String title, String message, String initialValue, JFrame frame, boolean unique, ServerProxy lobbyServer) {
		super(skin, title, message, initialValue, frame, unique, lobbyServer);
		reply = initialValue;
	}

	protected void initComponents() {
		super.initComponents();
		field = null;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == bClose) {
			reply = null;
		} else if (source == bOk || source == field) {
//			reply = reply;
		} else if (source == bCashier) {
			reply = null;
			//Utils.showUrl(Utils.getCashierUrl(lobbyServer._name, lobbyServer.getTicket()));
		}
		if (unique)
			MessageFactory.dialog = null;
		dispose();
	}
}
