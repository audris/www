package ExV;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;


public class ExpertPanel extends javax.swing.JPanel implements ActionListener, TreeSelectionListener, TreeExpansionListener, TreeWillExpandListener{

    /** Creates new form ExpertPanel */
	public ExpertPanel() {
      initComponents ();
	}
	
    // Variables declaration - do not modify
	public ExV applet;

	boolean debug = false;
	String hostname = null;
	DatagramSocket socket = null;
	InetAddress remoteA = null;
	int remotePort = 9999;
	Object lastWidget=null;

	JTree tree, treeOrg;
	Experts tr;
	DefaultListModel model, modelORG, orgsModel;
	MyJList testersList, developersList, orgsList;
	LoginDetail ld;
	ModuleDetail md;
	boolean ignoreEvent = false;
	
	PersonChanged personChanged = new PersonChanged (this);
	OrgChanged orgChanged = new OrgChanged (this);

	private void initComponents () {
      setLayout (new java.awt.BorderLayout ());

	}
	
   public void empty (){
		removeNotify();
		removeAll();

		tr=null;
		
		tree=null;
		treeOrg=null;
		
		model.clear();
		modelORG.clear();
		orgsModel.clear();
		
		testersList=null;
		developersList=null;
		orgsList=null;
		
		ld=null;
		md=null;
		applet=null;
	}
	
		
	public void actionPerformed (ActionEvent e){
      if (ignoreEvent) return;
      if (e.getSource() instanceof JButton){
         String email=((JButton)(e.getSource())) .getText ();
         if (email .startsWith (tr .supervisorLab)){
            email = email .substring (12);
         }
         String ss [] = new String [1];
         ss [0] = email;
         if (applet != null){
            if (email .indexOf ("@") > 0)
               applet .run ("mailto", ss);
            else
               applet .run ("post", ss);
         }
      } else {
         String type = ((MyJComboBox)e.getSource()) .type;
         String login = (String)(((MyJComboBox)e.getSource()) .getSelectedItem ());
         System.out.println (type + ":" + login);
         ListSelectionEvent el = new ListSelectionEvent (e.getSource(), 0, 0, false);

         lastWidget = e .getSource ();

         if (type .equals ("login")){
            personChanged (login);
         }
         if (type .equals ("org")){
            orgChanged (login);
         }
         if (type .equals ("module")){
            moduleChanged (login);
         }
      }
	}

	public void record (String type){
      if (!debug) return;
       // send time, action type, widget, and version
      String res = System .currentTimeMillis() +";"+ type +";"+ lastWidget .getClass () +";"+ 1;
      System .out .println (res);
      byte dpbuf [] = res .getBytes ();
       //for (in i = 0; i < dpbuf1 .length; i++) dpbuf [i] = dpbuf1 [i];
      DatagramPacket dp = new DatagramPacket (dpbuf, dpbuf .length, remoteA, remotePort);
      try {
          //socket .setSendBufferSize (dp.getLength());
          //System .out .println ("buffer size:"+socket .getSendBufferSize());
         socket .send (dp);
         System .out .println ("Sent:"+dp.getLength());
      }catch (Exception e){
         System.err.println ("Exception:" + e);
      }
	}

	public void orgChanged (String org){
      ignoreEvent=true;

      record ("O;"+org);

      unselectNodes ();

      Enumeration en = null;
       // if org list generated the event select appropriate developers
       // and testers in dev and test lists


       /////////////////////////////////////////
       //need to get a root node for any data!!!

//	ModInfo mi = (ModInfo) ((DefaultMutableTreeNode) tr .nodes .get ("/bcfn:0")) .getUserObject();
      ModInfo mi = (ModInfo) ((DefaultMutableTreeNode) tr .bcf .getRoot ()) .getUserObject();
       /////////////////////////////////////////


       //set selections for dev list
      developersList .clearSelection ();
      testersList .clearSelection ();
      int i = 0;
      Vector v = new Vector ();
      for (Enumeration ee = model .elements (); ee .hasMoreElements() ; i++){
         String hrid = mi .hridForLogin ((new StringTokenizer ((String)(ee .nextElement()), ":")) .nextToken ());
         if (hrid != null && tr .hrid2org .get (hrid) != null &&
             ((String) tr .hrid2org .get (hrid)) .compareTo (org) == 0)
            v .addElement (new Integer (i));
      }
      int sel [] = new int [v .size ()];
      for (i =0; i < sel .length; i++) sel [i] = ((Integer)v .elementAt (i)) .intValue ();
      developersList .setSelectedIndices (sel);


       //set selections for tester list
      i = 0;
      v = new Vector ();
      for (Enumeration ee = modelORG .elements (); ee .hasMoreElements() ; i++){
         String hrid = mi .hridForLogin ((new StringTokenizer ((String)(ee .nextElement()), ":")) .nextToken ());
         if (hrid != null && tr .hrid2org .get (hrid) != null &&
             ((String) tr .hrid2org .get (hrid)) .compareTo (org) == 0)
            v .addElement (new Integer (i));
      }
      sel = new int [v .size ()];
      for (i =0; i < sel .length; i++) sel [i] = ((Integer)v .elementAt (i)) .intValue ();
      testersList .setSelectedIndices (sel);


       // finally highlight modules based on which org was selected
      Object oo = tr .org2node .get (org);
      if(oo != null)
         for (en = ((Hashtable)oo) .keys (); en .hasMoreElements ();){
            Object o = en .nextElement ();
            if (o != null){
                //System.err.println (o);
               DefaultMutableTreeNode n = (DefaultMutableTreeNode) o;
               ModInfo mi1 = (ModInfo) n .getUserObject();
               mi1 .orgIsSelected = true;
               mi1 .orgSelected = org;
            }
         }


       // update the org field in the details
      ld .org .setText (org);

      repaint ();

      ignoreEvent=false;
      return;
	}

	private void unselectNodes (){
      Enumeration it = null;
      for (it = tr .nodes .elements ();it .hasMoreElements ();){
         ModInfo mi = (ModInfo) ((DefaultMutableTreeNode) it .nextElement ()) .getUserObject();
         mi .isSelected = false;
         mi .orgIsSelected = false;
      }
      for (it = tr .nodesOrg .elements ();it .hasMoreElements ();){
         ModInfo mi = (ModInfo) ((DefaultMutableTreeNode) it .nextElement ()) .getUserObject();
         mi .isSelected = false;
         mi .orgIsSelected = false;
      }
	}

	public void personChanged (String name){
      if (ignoreEvent) return;

      ignoreEvent=true;

      record ("P;"+name);

       // first always unselect all developer or testing nodes
      unselectNodes ();

      developersList .selectItem (name);
      testersList .selectItem (name);

       //System.err.println ("personChanged1:"+ignoreEvent);
      ld .login .setText (name + "");
      String hrid = (String) tr .login2hrid .get (name);
      if (hrid != null && hrid .compareTo ("") != 0 && hrid .compareTo ("null") != 0){

         if (tr .hrid2name .get (hrid) != null){
            StringTokenizer st1 = new StringTokenizer ((String) (tr .hrid2name .get (hrid)), ":");
            ld .name .setText (st1.nextToken());
            ld .email .setText (st1.nextToken());
            ld .org .setText (st1.nextToken());
			
            ld .phone .setText ("ph: " + st1.nextToken());
            ld .sid .setText (tr .supervisorLab+": "+st1.nextToken());
            String desc = st1.nextToken();
             //ld .summary .setText (desc .replace ('&','\n'));
            ld .summary .setText (desc);
            ld .location .setText (st1.nextToken());
         }
      }
      { // suspiciously complex
         Enumeration it = tr .nodes .elements ();
         for (;it .hasMoreElements ();){
            ModInfo mi = (ModInfo) ((DefaultMutableTreeNode) it .nextElement ()) .getUserObject();
            if (mi .level == 0){
                //ld .ndelta .setText (mi .ndeltaForLogin (name) + "");
               ld .login .setText (name + "");
            }
//				if (mi .hridForLogin (name) == null || mi .hridForLogin (name) .compareTo ("null") == 0){
//				 ld .name .setText ("null");
//				 ld .email .setText ("null");
//				 ld .org .setText ("null");
//				} else {
//				 if (mi .hridForLogin (name) == null || tr .hrid2name .get (mi .hridForLogin (name)) == null){
//					ld .name .setText ("unknown");
//					ld .email .setText ("unknown");
//					ld .org .setText ("unknown");
//				 } else {
//					StringTokenizer st1 = new StringTokenizer ((String)tr .hrid2name .get (mi .hridForLogin (name)), ":");
//					ld .name .setText (st1.nextToken());
//					ld .email .setText (st1.nextToken());
//					ld .org .setText (st1.nextToken());
//				 }
//				}
         }
      }
	
       //System.err.println ("personChanged2:"+ignoreEvent);ignoreEvent=true;// the org selection sets it to false!!!
      orgsList .selectItem (ld .org .getText ());
       //System.err.println ("personChanged3:"+ignoreEvent);ignoreEvent=true;// the org selection sets it to false!!!


       //select all modules that the developer worked on for the tree nodes
      Enumeration en = null;
       // see if the org or developer list was clicked
      if (lastWidget == testersList){
         en = ((Hashtable) (tr .login2nodeOrg .get (name))) .keys ();
      } else {
         Hashtable hh= (Hashtable) tr.login2node .get (name);
         if (hh == null) en = null;
         else en = hh .keys ();
      }
      if (en != null){
         for (;en .hasMoreElements ();){
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) en .nextElement ();
            ModInfo mi = ((ModInfo) ((DefaultMutableTreeNode)n) .getUserObject());
            mi .isSelected = true;
            mi .loginSelected = name;
         }
      }
       //System .out. println ("repaint");
      repaint ();

      ignoreEvent=false;
	}

	public void moduleChanged (String module){
      ignoreEvent=true;

      String modid = module;

      record ("M;"+modid);

      DefaultMutableTreeNode n = (DefaultMutableTreeNode) (tr .nodes .get (modid));
       //System.out.println (modid+":"+ n + ":" + tree .getSelectionPath ());
      if (n != null) {
         tree .setSelectionPath (new TreePath (n .getPath ()));

         ModInfo mi = (ModInfo) ((DefaultMutableTreeNode)n) .getUserObject();

         md .ndelta .setText (mi .ndelta +"");
         md .nLogin .setText (mi .people .length +"");
         md .level .setText (mi .level +"");
         md .module .setText (mi .name ());

          // first select developers for that module
         model .clear ();
         for (int i = 0; i < Math .min(mi .people .length, 700); i++)
            model .addElement (mi .people[i] .login + ":"+
                               mi .people[i] .location +":"+
                               ((mi .people[i] .loginDelta*100/mi .ndelta)/100.));

          // now select testers
          //System.err.println("tr="+tr + ":" + " tr.nodesOrg=" + tr .nodesOrg);
          //System.err.println("mi="+mi + ":" + " mi.name=" + mi .name ());
          // make sure null is dealt with
          if (tr .nodesOrg .get(mi .name ()) != null){    
             mi = (ModInfo) ((DefaultMutableTreeNode)tr .nodesOrg .get(mi .name ())).getUserObject ();
             md .nOrgLogin .setText (mi .people .length +"");
             modelORG .clear ();
          //System.err.println("mi="+mi + ":" + " mi.people=" + mi .people + " mi .people .length=" +  mi .people .length);
         
             for (int i = 0; i < mi .people .length; i++){            
                modelORG .addElement (mi .people[i] .login + ":"+
                                     mi .people[i] .location +":"+
                                     ((mi .people[i] .loginDelta*100/mi .ndelta)/100.));
             }
          } else {
             modelORG .clear ();
          }
         
          // finally select right organizations
          orgsModel .clear ();
          Hashtable tmp = new Hashtable ();
          Hashtable tmp1 = new Hashtable ();
          Hashtable tmp2 = new Hashtable (){
                public synchronized Object put (Object o, Object a){
                   if (get (o) == null) return super .put (o, a);
                   else return super .put (o, new Integer (((Integer) a) .intValue ()+ ((Integer)get(o)) .intValue()));
                }
             };
          // use developers to represent an organizxation
         mi = (ModInfo) ((DefaultMutableTreeNode)n) .getUserObject();
          //System.out.println (mi .name () + ":" + mi .people .length);
         for (int i = 0; i < mi .people .length; i++){
            String org = (String) tr .hrid2org .get (mi .people[i] .hrid);
             //System.out.println (org + ":" + mi .people[i] .login + ":" + mi .people[i] .hrid);
            if (org != null){
               tmp .put (org, org);
               int ndel=mi .people [i] .loginDelta;
                //System.err.println(org+":"+1 +":"+ndel+":"+mi .people[i] .login);
               if (tmp1 .containsKey (org))
                  ndel += ((Integer) tmp1 .get (org)) .intValue ();
               tmp1 .put (org, new Integer (ndel));
            }
         }

         String organizations [] = new String [tmp .size ()];
         int ii = 0;
         for (Enumeration en = tmp .keys (); en .hasMoreElements ();){
            Object o =	en .nextElement ();
            organizations [ii++] = (String) o;
         }
         QSort .sort (organizations);
         for (int i = 0; i < organizations .length; i++){
            String o = organizations [i];
            double d = ((Integer)tmp1 .get (o)) .doubleValue ();
            d = d/mi .ndelta; d = d < .01 ? 0 : d;
             //System.err.println(o+":"+d +":"+mi .ndelta);
            String dd = d + " ";
		 
            orgsModel .addElement (o + ":" + dd .substring (0,4));
             //System.out.println (o);
         }

      }
      ignoreEvent=false;
	}

    /** show developers, testers, and organizations associated with
        particular module*/
	public void valueChanged(TreeSelectionEvent e) {
      if (e .getPath() .getPathComponent(0) == tr .bcf){
          //select right developers
         TreeNode n = (TreeNode) (e .getPath() .getLastPathComponent());
         ModInfo mi = (ModInfo) ((DefaultMutableTreeNode)n) .getUserObject();
         md .level .setText (mi .level +"");
         lastWidget = e .getSource ();
         if (!ignoreEvent)
            moduleChanged (mi .name ());
      }
	}

	public void treeWillExpand (TreeExpansionEvent e){
      System.out.println ("wille:"+e);
	}
	public void treeWillCollapse (TreeExpansionEvent e){
      System.out.println ("willc:"+e);
	}

	public void treeExpanded (TreeExpansionEvent e){
       //System.out.println (e);
       //System.out.println ("a:"+e.getPath());
       //System.out.println ("b:"+e .getPath() .getLastPathComponent());
      TreePath p = e .getPath();
      TreeNode n = (TreeNode) p .getLastPathComponent();
      TreeNode p0 = n .getParent ();
      if (p0 == null) return;
       //System.out.println ("nchildren="+p0 .getChildCount ());
      for (int i = 0; i < p0 .getChildCount (); i++){
         TreeNode n1 = p0 .getChildAt (i);
          //System.out.println (i+":"+n1+":"+n+":"+(n1 != n));
         if (n1 != n){
            TreeNode tpa [] = new TreeNode [2];tpa[0]=p0;tpa[1]=n1;
            tree .expandPath (new TreePath (tpa));
         }
      }
       //tree .expandPath ((javax.swing.tree.TreePath) (e .getPath()));
	}
	public void treeCollapsed (TreeExpansionEvent e){
       //System.out.println (e);
       //System.out.println ("a1:"+e.getPath());
       //System.out.println ("b1:"+e .getPath() .getLastPathComponent());
       //tree .collapsePath ((javax.swing.tree.TreePath) (e .getPath()));
      TreePath p = e .getPath();
      TreeNode n = (TreeNode) p .getLastPathComponent();
      TreeNode p0 = n .getParent ();
      if (p0 == null) return;
       //System.out.println ("nchildren="+p0 .getChildCount ());
      for (int i = 0; i < p0 .getChildCount (); i++){
         TreeNode n1 = p0 .getChildAt (i);
          //System.out.println (i+":"+n1+":"+n+":"+(n1 != n));
         if (n1 != n){
            TreeNode tpa [] = new TreeNode [2];
            tpa[0]=p0;
            tpa[1]=n1;
            tree .collapsePath (new TreePath (tpa));
         }
      }
	}

	public ExpertPanel (TreeCellRenderer renderer, Experts tr, String hostname, boolean debug) {
      this .debug = debug;
      this .hostname = hostname;
      this .tr = tr;
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
       //setLayout (gridbag);
       //setLayout (new BorderLayout ());
      setLayout (new GridLayout (1,2));
      
       //sp = new JScrollPane(treeOrg, JScrollPane .VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane .HORIZONTAL_SCROLLBAR_AS_NEEDED );
       //add(sp, BorderLayout.EAST);
      
       // Put list views in a separate pane
      model = new DefaultListModel ();
      modelORG = new DefaultListModel ();
      orgsModel = new DefaultListModel ();
      
      JPanel leftContainer = new JPanel ();
      if (!tr .nodetail)
         leftContainer .setLayout (new GridLayout (2, 1));
      else
         leftContainer .setLayout (new GridLayout (1, 1));

       //place for all lists
      JPanel listContainer = new JPanel ();
      listContainer .setLayout (new GridLayout (1, 3));

       //Testers
      JScrollPane a = new JScrollPane(testersList = new MyJList (modelORG, true), JScrollPane .VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane .HORIZONTAL_SCROLLBAR_AS_NEEDED);
      a .setColumnHeaderView (new JLabel (tr .supervisorLab));
      a .setToolTipText ("Click on a login to see related code and contact detail");
      c.fill = GridBagConstraints.BOTH;
      c.gridheight = 1;
      c.gridwidth = 1;
      c.weightx = 1.0;
      c.weighty = 1.0;
      c.gridx = 1;
      gridbag .setConstraints(a, c);
       //add(a);
      if (tr .supervisorLab .compareTo ("none") != 0)
         listContainer .add (a);
	
      testersList .addMouseListener (new MyJListMouseAdapter(testersList, this));
      testersList .addListSelectionListener (personChanged);


       // Developers
      a = new JScrollPane(developersList = new MyJList (model, true), JScrollPane .VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane .HORIZONTAL_SCROLLBAR_AS_NEEDED);
      a .setColumnHeaderView (new JLabel (tr .developerLab));
      a .setToolTipText ("Click on a login to see changed code and contact detail");
      developersList .addListSelectionListener (personChanged);
      developersList .addMouseListener (new MyJListMouseAdapter(developersList, this));
      c.gridx = 2;
      gridbag .setConstraints(a, c);
       //add(a);
      if (tr .developerLab .compareTo ("none") != 0)
         listContainer .add (a);

       //Organizations
      a = new JScrollPane(orgsList = new MyJList (orgsModel, true), JScrollPane .VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane .HORIZONTAL_SCROLLBAR_AS_NEEDED);
      a .setColumnHeaderView (new JLabel ("Organizations"));
      orgsList .addListSelectionListener (orgChanged);
      a .setToolTipText ("Click on an org number to see members and changed code.");
      c.gridx = 3;
      gridbag .setConstraints(a, c);
       //add(a);

      if (tr .orgLab .compareTo ("none") != 0)
         listContainer .add (a);
      leftContainer .add (listContainer);

      c.gridwidth = 3;
      c.gridheight = 1;
      c.gridx = 1;
      c.weightx = 1.0;
      c.weighty = 0.0;
      md = new ModuleDetail (this);
      gridbag .setConstraints(md, c);
       //add (md);
	

      ld = new LoginDetail (this);
      c.gridx = 1;
      c.gridwidth = 3;
      gridbag .setConstraints(ld, c);
      if (!tr.nodetail)
         leftContainer .add(ld);
       //add (leftContainer, "West");
      if (!tr.nopanel)
         add (leftContainer);


       // Create tree
      tree = new JTree(tr .bcf);
       //treeOrg = new JTree(tr .bcfOrg);
       //tree .addTreeWillExpandListener (this);
       //tree .addTreeExpansionListener (this);

      tree .addTreeSelectionListener (this);
       // Set line style
      tree .putClientProperty("JTree.lineStyle", "Angled");
       //treeOrg.putClientProperty("JTree.lineStyle", "Angled");

       // Change the cell renderer
      tree .setCellRenderer(renderer);
      tree .setRowHeight (0);
       //treeOrg.setCellRenderer(renderer);
      ToolTipManager.sharedInstance().registerComponent(tree);


      tree .addMouseListener(new MyMouseAdapter(this));


       // Put tree in a scrollable pane
      JScrollPane sp = new JScrollPane ();
      sp .setPreferredSize (new Dimension(300, 400));
      sp .getViewport() .add(tree);
       //JScrollPane sp = new JScrollPane(tree, JScrollPane .VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane .HORIZONTAL_SCROLLBAR_AS_NEEDED );
      sp .setColumnHeaderView (new JLabel (tr .moduleLab));
      sp .setToolTipText ("Click on a module to see organizations, developers, and MR raisers involved");

      c.fill = GridBagConstraints.BOTH;
      c.weightx = 10000;
      c.weighty = 1.0;
      c.gridx = 4;
      c.gridheight = 3;
      c.gridwidth = GridBagConstraints.REMAINDER;
      c.gridwidth = 1;
      gridbag .setConstraints(sp, c);
      add(sp);
       //add(sp, "Center");

       // now create udp connection to notmafia
      if (debug && socket == null){
          //this is for listening locally
          //DummyThread dt = new DummyThread (this);
          //dt .start ();

         try{
            socket = new DatagramSocket ();
            remoteA = InetAddress .getByName (hostname);
         } catch (Exception e){ System.err.println (e); }
         try {
            byte ss [] = ("init:" + System .currentTimeMillis()) .getBytes ();
            DatagramPacket dp = new DatagramPacket (ss, ss .length, remoteA, remotePort);
            socket .send (dp);
         } catch (IOException e){ System.err.println (e); }
      }
      tree .setRowHeight (0);
	}
	public void start (String s){
		System.out.println ("rowa:"+tree .getRowCount ());
      tree .setRowHeight (0);
      for (int i = tree .getRowCount () - 1; i > 0; i--){
         tree .expandRow (i);
         tree .collapseRow (i);
         tree .setRowHeight (i);
         System.out.println (" collapsing row:"+i);
      }
      tree .collapseRow (0);
      tree .setRowHeight (0);
      tree .expandRow (0);
      
      if (!debug || s == null) return;
      try {
         byte ss [] = ("start:" + System .currentTimeMillis() + ":" + s) .getBytes ();
         DatagramPacket dp = new DatagramPacket (ss, ss .length, remoteA, remotePort);
         socket .send (dp);
      } catch (IOException e){ System.err.println (e); }
	
	}
	public void stop (){
      if (!debug) return;
      try {
         byte ss [] = ("stop:" + System .currentTimeMillis()) .getBytes ();
         DatagramPacket dp = new DatagramPacket (ss, ss .length, remoteA, remotePort);
         socket .send (dp);
      } catch (IOException e){ System.err.println (e); }
	}
	public void destroy (){
      if (!debug) return;
      try {
         byte ss [] = ("destroy:" + System .currentTimeMillis()) .getBytes ();
         DatagramPacket dp = new DatagramPacket (ss, ss .length, remoteA, remotePort);
         socket .send (dp);
      } catch (IOException e){ System.err.println (e); }
	}
}

class MyJListMouseAdapter extends MouseAdapter {
	public MyJList list;
	public ExpertPanel ep;

	MyJListMouseAdapter (MyJList list, ExpertPanel ep){
      this .list = list;
      this .ep = ep;
	}
	public void mouseClicked(MouseEvent e) {
      int index = list .locationToIndex(e.getPoint());
      if ((e .getModifiers () & MouseEvent .BUTTON1_MASK) > 0) {
         System.out.println("left clicked on Item " + index);
      } else {
         System.out.println("right clicked on Item " + index);
         String ss [] = new String [1];
         StringTokenizer st = new StringTokenizer ((String)(list .getModel () .getElementAt (index)), ":");
         if (st != null){
            ss [0] = st .nextToken ();
            ep .applet .run ("mrs", ss);
            ep .record ("MR;"+ss[0]);
         }
      }
	}
}


class MyMouseAdapter extends MouseAdapter {
	public ExpertPanel ep;

	MyMouseAdapter (ExpertPanel ep){
      this .ep = ep;
	}

	public void mouseClicked(MouseEvent e) {
      int selRow = ep .tree .getRowForLocation(e.getX(), e.getY());
      TreePath selPath = ep .tree .getPathForLocation(e.getX(), e.getY());
      if(selRow != -1) {
         if ((e .getModifiers () & MouseEvent .BUTTON1_MASK) > 0) {
         } else {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) (selPath .getLastPathComponent());
             //if (n.getChildCount () == 0){
            if (ep .applet != null){
               ModInfo mi = (ModInfo) n .getUserObject();
               System.out.println ("show files:"+mi.getModule ());
               String ss [] = new String [1];
               ss [0] = mi.getModule ();
               ep .applet .run ("files", ss);
               ep .record ("MF;"+ss[0]);
            }
             //}
         }
      }
	}
}

class PersonChanged implements ListSelectionListener
{
	public ExpertPanel ep;

	PersonChanged (ExpertPanel ep){
      this .ep = ep;
	}

	public void valueChanged (ListSelectionEvent e) {
      if (e .getValueIsAdjusting() || ep .ignoreEvent) {
          //System.err.println (e .getValueIsAdjusting() + ":" + ignoreEvent);
         return;
      }

       //System.err.println ("personChanged:"+e .getSource ());
      
      StringTokenizer st = null;
   
       //from developrt and tester list
      if (e .getSource () instanceof JList){
         JList l = (JList) e .getSource ();
         st = new StringTokenizer ((String)(l .getModel () .getElementAt (l .getSelectedIndex ())), ":");
      }
      
       //		//from login combo box
       //		if (e .getSource () instanceof JComboBox){
       //			JComboBox l = (JComboBox) e .getSource ();
       //			st = new StringTokenizer ((String)(l .getSelectedItem ()), ":");
       //		}

      ep .lastWidget = e .getSource ();
      System.err.println ("personChanged:"+e .getSource ());
      if (st != null)
         ep .personChanged (st .nextToken ());
	}
}

class OrgChanged implements ListSelectionListener
{
	public ExpertPanel ep;

	OrgChanged (ExpertPanel ep){
      this .ep = ep;
	}

	public void valueChanged (ListSelectionEvent e) {
      if (e .getValueIsAdjusting() || ep .ignoreEvent) {
          //System.err.println (e .getValueIsAdjusting() + ":" + ignoreEvent);
         return;
      }
      
      
       //System.err.println ("orgChanged:"+e .getSource ());
      
      StringTokenizer st = null;

       //from org list
      if (e .getSource () instanceof JList){
         JList l = (JList) e .getSource ();
         st = new StringTokenizer ((String)(l .getModel () .getElementAt (l .getSelectedIndex ())), ":");
      }
      
	//		//from login combo box
	//		if (e .getSource () instanceof JComboBox){
	//			JComboBox l = (JComboBox) e .getSource ();
	//			st = new StringTokenizer ((String)(l .getSelectedItem ()), ":");
	//		}

      ep .lastWidget = e .getSource ();
      if (st != null)
         ep .orgChanged (st .nextToken ());
	}
}
