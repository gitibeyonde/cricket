package com.criconline.lobby;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.criconline.resources.Bundle;
import com.criconline.Utils;

/** Cell renderer for user table at lobby
 * @author Halt
 */
public class UserTableStringCellRenderer
    extends DefaultTableCellRenderer {

  private ResourceBundle bundle = Bundle.getBundle();

//	fonts

  public UserTableStringCellRenderer() {
    super();

  }

  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus, int row,
                                                 int column) {
    Object o=null;

    if (table.getColumnCount()>= 7){
      o = table.getValueAt(row, 6);
    }

    if (isSelected) {
      if (o != null && o.equals("N")) {
        return new simplLabelSel(value.toString());
      }
      else {
        return new boldLabelSel(value.toString());
      }
    }
    if (o != null && o.equals("N")) {
      return new simplLabel(value.toString());
    }
    else {
      return new boldLabel(value.toString());
    }
  }

  protected class simplLabelSel
      extends JLabel {
    simplLabelSel(String title) {
      super(title);
      setFont(Utils.normalFont);
      setForeground(Color.BLUE);
    }

    public void paint(Graphics g) {
      g.setColor(Utils.greenColor);
      g.fillRect(0, 0, getWidth(), getHeight());
      super.paint(g);
    }
  }

  protected class boldLabelSel
      extends JLabel {
    boldLabelSel(String title) {
      super(title);
      setFont(Utils.boldFont);
      setForeground(Color.GREEN);
    }

    public void paint(Graphics g) {
      g.setColor(Utils.greenColor);
      g.fillRect(0, 0, getWidth(), getHeight());
      super.paint(g);
    }
  }

  protected class simplLabel
      extends JLabel {
    simplLabel(String title) {
      super(title);
      setFont(Utils.normalFont);
      setForeground(Color.CYAN);
    }
  }

  protected class boldLabel
      extends JLabel {
    boldLabel(String title) {
      super(title);
      setFont(Utils.boldFont);
      setForeground(Color.GREEN);
    }
  }

}
