package ExV;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

class LoginDetail extends JPanel {
   public int width=100;
   private int height=50;
   
   public MyJComboBox login, org;
   public JButton name = new JButton ();
   public JLabel location = new JLabel ();
   public JButton email = new JButton ();
   public JLabel phone = new JLabel ();
   public JButton sid = new JButton ();
   public JTextArea summary = new JTextArea ();
   
   public LoginDetail (ActionListener l){
      login = new MyJComboBox ("login", l, true);
      login .setToolTipText ("Select or type a login");
      org = new MyJComboBox ("org", l, true);
      org .setToolTipText ("Select or type an org number");
      
      Border b = BorderFactory .createEtchedBorder ();
       //ndelta .setBorder (b);
      name .setBorder (b);name.addActionListener (l);
      name .setToolTipText("Click this button to see POST entry.");

      email .setBorder (b);email.addActionListener (l);
      email.setHorizontalTextPosition(AbstractButton.LEFT);
      email.setVerticalTextPosition(AbstractButton.CENTER);
      email.setToolTipText("Click this button to compose email.");

      org .setBorder (b);
      location .setBorder (b);         
      
      setLayout (new BorderLayout ());
      summary.setFont (fL);
      summary.setEditable (false);
      
      add (summary, BorderLayout .CENTER);
      JPanel tmp0 = new JPanel ();
      tmp0 .setLayout (new GridLayout (7, 1));
      tmp0 .add (name);
      tmp0 .add (email);
      JPanel tmp = new JPanel ();
      phone .setBorder (b);
      sid .setBorder (b);sid.addActionListener (l);sid.setToolTipText("Click this button to see POST entry.");
      tmp0 .add (phone);
      tmp0 .add (sid);
      tmp = new JPanel ();
      
       //tmp .setLayout (new GridLayout (1, 2)); tmp . add (new JLabel ("Number of Delta:"));tmp .add (ndelta);
      tmp .setLayout (new GridLayout (1, 2)); tmp . add (new JLabel ("Login:"));tmp .add (login);
      tmp0 .add (tmp);tmp = new JPanel ();
      tmp .setLayout (new GridLayout (1, 2)); tmp . add (new JLabel ("Location:"));tmp .add (location);
      tmp0 .add (tmp);tmp = new JPanel ();
      tmp .setLayout (new GridLayout (1, 2)); tmp . add (new JLabel ("Organization:"));tmp .add (org);
      tmp0 .add (tmp);
      add (tmp0, BorderLayout .SOUTH);

      setFont (f);
      setBorder (b);
   }
   
   Font f = new Font ("Times", Font .PLAIN, 12);
   Font fL = new Font ("Times", Font .BOLD, 14);
}






