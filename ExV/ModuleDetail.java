package ExV;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

class ModuleDetail extends JPanel {
   public int width=100;
   private int height=50;
   
   public JLabel level = new JLabel ();
   public JLabel ndelta = new JLabel (), nSelDelta = new JLabel ();
   public JLabel nfix = new JLabel ();
   public JLabel nLogin = new JLabel (), nOrgLogin = new JLabel ();
   public MyJComboBox module;
   public ModuleDetail (ExpertPanel l){
      module = new MyJComboBox ("module", l, false);
      module .setToolTipText ("Select one of the modules");
      Border b = BorderFactory .createEtchedBorder ();
      ndelta .setBorder (b);
      nSelDelta .setBorder (b);
      nfix .setBorder (b);
      nLogin .setBorder (b);
      level .setBorder (b);
      nOrgLogin .setBorder (b);


      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      GridBagConstraints c1 = new GridBagConstraints();
      setLayout (gridbag);
       //setLayout (new GridLayout (4, 3));
      
      c.fill = GridBagConstraints.BOTH;c.weightx = 0.5;
      c1.fill = GridBagConstraints.BOTH;c1.weightx = 1.0;
      c1.gridwidth = GridBagConstraints.REMAINDER;

      JLabel a = new JLabel ("Number of Delta:");
      gridbag .setConstraints(a, c); add (a);
      gridbag .setConstraints(ndelta, c1);add (ndelta);
      a = new JLabel ("Number of Fixes:");
      gridbag .setConstraints(a, c); add (a);
      gridbag .setConstraints(nfix, c1);add (nfix);
       //add (new JLabel ("Selected Delta"));add (nSelDelta);
      
        //add (new JLabel ("Module:"));
      module .setFont (f);gridbag .setConstraints(module, c1);add (module);

      a = new JLabel ("Number of Developers:");
       gridbag .setConstraints(a, c); add (a);
       gridbag .setConstraints(nLogin, c1); add (nLogin);

      a = new JLabel ("Number of Fixers:");
      gridbag .setConstraints(a, c); add (a);
      gridbag .setConstraints(nOrgLogin, c1);add (nOrgLogin);
      
       //add (new JLabel ("Level"));
       //add (level);
      setFont (f);
      setBorder (b);
   }
   Font f = new Font ("Times", Font .PLAIN, 10);
}





