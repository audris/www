package ExV;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class Experts {

   private static class CellRenderer implements TreeCellRenderer {
      private JBar comp;
      private Experts tr;
      
      public void empty (){
         tr = null;
        comp = null;
      }
      CellRenderer (Experts tr) {
         this .tr = tr;
         comp = new JBar ();
      }

      public Component  getTreeCellRendererComponent(
         JTree tree, Object value, boolean isSelected,
         boolean expanded, boolean leaf, int row,
         boolean hasFocus) {
         
          // Get background color based on selected state
         Color background = (isSelected ? Color.lightGray : Color.white);
         comp .setBackground(background);         
         if ((leaf || !leaf)) {
            if (!tr .nopanel) comp .setToolTipText ("Click on a module to see organizations, developers, and MR raisers involved");
             
            ModInfo mi = ((ModInfo) ((DefaultMutableTreeNode)value) .getUserObject());

            DefaultMutableTreeNode parent = (DefaultMutableTreeNode)((DefaultMutableTreeNode)value) .getParent();
            String base = "/";
            if (parent != null)
               base = ((ModInfo) parent .getUserObject()) .module + "/";
            int h = (int) (Math .sqrt(mi .ndelta/10.0+0.0)+12);
            int w = (int) ((mi .people .length+0.0))*JBar .incrementPerDeveloper;
            if (tr .wdelta) w = (int)(mi .ndelta/5.0+1);
            comp .setSize (h, w);
            String text = mi .module .substring (base .length());
            if (tr .nopanel&&mi .ndelta>1000)  text = text + " Lines=" + mi .ndelta + "K";
            comp .setText (text);
             //
             // (int) (Math .sqrt(mi .people .length+0.0))*5+20);
             //show what org worked on
            if (mi .orgIsSelected){
               comp .pselected = 0;
               for (int i = 0; i < mi .people .length; i++){
                  String org = (String) tr .hrid2org .get (mi .people [i] .hrid);
                  if (org != null && mi .orgSelected .compareTo (org) == 0)
                     comp .pselected += (mi .people [i] .loginDelta+0.0)/ mi .ndelta;
               }
                //System .err.println( comp .pselected + ":" + mi .orgSelected);
               if (comp .pselected == 1) comp .pselected = .9999;
            } else {
                // show developer part of the work
                // see if org data is used
               boolean useOrg = false;
               Object o = tr .nodesOrg .get (mi .name ());
               if (o != null){
                  ModInfo mi0 = ((ModInfo) ((DefaultMutableTreeNode)o) .getUserObject());
                  if (mi0 .isSelected){
                     mi = mi0;
                     useOrg = true;
                  }
               }
               if (mi .isSelected){
                  comp .pselected =  (mi .ndeltaForLogin (mi .loginSelected)+0.0)/ mi .ndelta;
                  if (comp .pselected == 1) comp .pselected = .9999;
                  if (useOrg) comp .pselected = -comp .pselected;
               } else 
                  comp .pselected = 1;
            }
            if (comp .pselected != 100){
               comp .setVisible (true);
                //System.out.println(comp.pselected+":"+mi .module .substring (base .length()));
               h = (int) (Math .sqrt(mi .ndelta/10.0+0.0)+12);
               w = (int) ((mi .people .length+0.0))*JBar .incrementPerDeveloper;
               if (tr .wdelta) w = (int)(mi .ndelta/5.0)+1;
               comp .setSize (h, w);
               text = mi .module .substring (base .length());
               if (tr .nopanel&&mi .ndelta>1000)  text = text + " Lines=" + mi .ndelta + "K";
               comp .setText (text);
            } else {
               System.out.println(mi .module .substring (base .length()));
               comp .setVisible (false);
               comp .setText (null);
               comp .setText (null);
            }  
         }
         return comp;
      }  
   }
   // The initial width and height of the frame
   int WIDTH = 800;
   int HEIGHT = 600;
   Hashtable nodes = new Hashtable ();
   Hashtable nodesOrg = new Hashtable ();
   Hashtable login2hrid = new Hashtable ();
   Hashtable login2node = new Hashtable ();
   Hashtable login2nodeOrg = new Hashtable ();
   Hashtable org2node = new Hashtable ();
   Hashtable org2nodeOrg = new Hashtable ();

   Hashtable hrid2name = new Hashtable ();


   ExpertPanel tp;
   boolean wdelta = false;  //map width as ndelta
   boolean nopanel = false; //show left panel
   boolean nodetail = false; //show left panel
   boolean nobgread = false; //dont read data in the background
   String supervisorLab = "Supervisor";
   String developerLab = "Developer";
   String orgLab = "Organization";
   String moduleLab = "Module";
   
   DefaultMutableTreeNode bcf, bcfOrg;
   public Hashtable orgs = new Hashtable ();
   public Hashtable hrid2org = new Hashtable ();
    
   private CellRenderer cell;
     
   public void empty (){
      for (Enumeration it = nodes .elements ();it .hasMoreElements ();){
         DefaultMutableTreeNode n = (DefaultMutableTreeNode) it .nextElement ();
         n.removeAllChildren();
         n.removeFromParent();
         n.setUserObject(null);
      }
      for (Enumeration it = nodesOrg .elements ();it .hasMoreElements ();){
         DefaultMutableTreeNode n = (DefaultMutableTreeNode) it .nextElement ();
         n.removeAllChildren();
         n.removeFromParent();
         n.setUserObject(null);
      }

      bcf=null;
      bcfOrg=null;

      hrid2org.clear();
      orgs.clear();
      hrid2name.clear();
      org2nodeOrg.clear();
      org2node.clear();
      login2nodeOrg.clear();
      login2node.clear();
      login2hrid.clear();
      nodesOrg.clear();
      nodes.clear();
      
      tp .empty();
      tp=null;
      cell .empty();
   }       
   
   private void calcLogin2node (Hashtable h, DefaultMutableTreeNode n){
      ModInfo mi = (ModInfo) n .getUserObject ();
      for (int i=0; i < mi .people .length; i++){
         Hashtable h1 = (Hashtable) h .get (mi .people [i] .login);
         if (h1 == null) h .put (mi .people [i] .login, h1 = new Hashtable ());
         h1 .put (n, new Integer (mi .people [i] .loginDelta));
         login2hrid .put (mi .people [i] .login, mi .people [i] .hrid);
      }
   }
   private void calcOrg2node (Hashtable h, DefaultMutableTreeNode n){
       //System.err.println ("here");
      ModInfo mi = (ModInfo) n .getUserObject ();      
       //System.err.println ("here1");
      for (int i=0; i < mi .people .length; i++){
         if (mi .people [i] .hrid == null) mi .people [i] .hrid = "null";
          //System.err.println ("here2:"+i+":"+mi .people [i] .hrid);
         String org = (String) hrid2org .get(mi .people [i] .hrid);if (org == null) org = "null";
         Hashtable h1 = (Hashtable) h .get (org);
         if (h1 == null) {
            h .put (org, h1 = new Hashtable ());
         }
          //System.err.println ("here3:"+i+":"+(h1==null) + ":"+ h1 .get (n));
         Integer ndel = (Integer) h1 .get (n);
         if (ndel == null) h1 .put (n, ndel = new Integer (0));
         h1 .put (n, new Integer (ndel .intValue () + mi .people [i] .loginDelta));
      }
   }

   public void continueReading (BufferedReader br, String tmp) throws IOException {
      int clevel = 1;
      do {
         ModInfo mod = new ModInfo (tmp);
         if (mod .level > clevel){
            long fmem = java.lang.Runtime.getRuntime().freeMemory();
            long tmem = java.lang.Runtime.getRuntime().totalMemory();
            if (tmem > 10000000l & fmem < 3000000l){
               System .out .println ("stopping at level:"+clevel+" due to lack of memory");
               break;
            } else {
               System.out.println (tmem + ":"+fmem+":"+((tmem-fmem)/1000)+"Kb");
               clevel=mod .level;
            }
         }         
         String name = mod .name ();
         DefaultMutableTreeNode node = new DefaultMutableTreeNode(mod);
         if (mod .type .compareTo ("delta") == 0){
            if (mod .level == 0){
               nodes .put (name, node);
               bcf = node;
               calcLogin2node (login2node, node);
               calcOrg2node (org2node, node);
            }
            if (mod .level > 0){
               DefaultMutableTreeNode pnode = findParent (mod, nodes);
               ModInfo pmod = ((ModInfo) (pnode .getUserObject()));
               if (pmod .module .compareTo (mod .module) != 0){
                  pnode .add (node);
                  nodes .put (name, node);
                  calcLogin2node (login2node, node);
                  calcOrg2node (org2node, node);
               }
            }
         } else {
            if (mod .type .compareTo ("ORG") == 0){
               if (mod .level == 0){
                  nodesOrg .put (name, node);
                  bcfOrg = node;
                  calcLogin2node (login2nodeOrg, node);
                  calcOrg2node (org2nodeOrg, node);
               }
               if (mod .level > 0){
                  DefaultMutableTreeNode pnode = findParent (mod, nodesOrg);
                  ModInfo pmod = ((ModInfo) (pnode .getUserObject()));
                  if (pmod .module .compareTo (mod .module) != 0){
                     pnode .add (node);
                     nodesOrg .put (name, node);
                     calcLogin2node (login2nodeOrg, node);
                     calcOrg2node (org2nodeOrg, node);
                  }
               }
            }
         }
      }while ((tmp = br .readLine ()) != null);
   }
   
   public Experts (String lab,
                   String hostname,
                   boolean debug,
                   String options) {
      BufferedReader in = null;
      BufferedReader inl = null;
      System.err.println("Starting");
      try {
         in = new BufferedReader(new InputStreamReader (new java.util.zip.GZIPInputStream (this.getClass().getResourceAsStream("/onex"))));
         inl = new BufferedReader(new InputStreamReader (this.getClass().getResourceAsStream("/locs")));
      }catch (IOException e){
         System.err.println(e);
      }  
      Experts ex = new Experts ("Who does what?",
                  in,
                  inl,
               "localhost", false, options);
      ex .mainHelper ();
   }
   
   public Experts (String lab,
                   BufferedReader br,
                   BufferedReader br1,
                   String hostname,
                   boolean debug,
                   String options) {

      if (options != null){
         StringTokenizer st = new StringTokenizer (options, ":");
         String opt = null ;
         try {
            while ((opt = st .nextToken ())!=null){
               if (opt.compareTo ("wdelta") == 0){
                  System.out.println("Ndelta is mapped into with of the node");
                  wdelta = true;
               }
               if (opt.compareTo ("nopanel") == 0){
                  System.out.println("No left panel");
                  nopanel = true;
               }
               if (opt.compareTo ("nodetail") == 0){
                  System.out.println("No detail");
                  nodetail = true;
               }
               if (opt.compareTo ("nobgread") == 0){
                  System.out.println("don't read in the background");
                  nobgread = true;
               }               
               if (opt.substring(0,4).compareTo ("sLab") == 0){
                  System.out.println("supervisor label");
                  StringTokenizer st1 = new StringTokenizer (opt, "=");
                  st1 .nextToken ();
                  supervisorLab=st1 .nextToken ();                  
               }
               if (opt.substring(0,4).compareTo ("dLab") == 0){
                  System.out.println("developer label");
                  StringTokenizer st1 = new StringTokenizer (opt, "=");
                  st1 .nextToken ();
                  developerLab=st1 .nextToken ();                  
               }
               if (opt.substring(0,4).compareTo ("oLab") == 0){
                  System.out.println("org label");
                  StringTokenizer st1 = new StringTokenizer (opt, "=");
                  st1 .nextToken ();
                  orgLab=st1 .nextToken ();                  
               }
               if (opt.substring(0,4).compareTo ("mLab") == 0){
                  System.out.println("module label");
                  StringTokenizer st1 = new StringTokenizer (opt, "=");
                  st1 .nextToken ();
                  moduleLab=st1 .nextToken ();                  
               }
            }
         }catch(NoSuchElementException e){
         }  
      }
      
      cell = new CellRenderer(this);
      
       // read the tree data from a file
      try {     
         String tmp;
         while ((tmp = br1 .readLine ()) != null) {
            StringTokenizer st = new StringTokenizer (tmp, ";");
            String login = st .nextToken ();
            String hrid = st .nextToken ();
            String name = st .nextToken ();
            String location = st .nextToken ();
            String email = st .nextToken ();
            String phone = st .nextToken ();
            String supervisor = st .nextToken ();
            String org = st .nextToken ();
            hrid2org .put (hrid, org);
            Hashtable h = (Hashtable)orgs .get (org);
            if (h == null) {
               orgs .put (org, h = new Hashtable ());
                //System.out.println (org);
            }
            h .put (hrid, name);
            hrid2name .put (hrid, name +":"+ email +":"+ org +":"+ phone +":"+ supervisor +":"+ org + ":" + location);
         }
         
         while ((tmp = br .readLine ()) != null) {
            ModInfo mod = new ModInfo (tmp);
            if (mod.level == 3 && !nobgread){
               System.out.println("starting reading thread");
               ReadThread rt = new ReadThread (this, br, tmp);
               System.out.println("started reading thread");
               //continueReading(br, tmp);
               break;
            }               
            String name = mod .name ();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(mod);
            if (mod .type .compareTo ("delta") == 0){
               if (mod .level == 0){
                  nodes .put (name, node);
                  bcf = node;
                  calcLogin2node (login2node, node);
                  calcOrg2node (org2node, node);
               }
               if (mod .level > 0){
                  DefaultMutableTreeNode pnode = findParent (mod, nodes);
                  ModInfo pmod = ((ModInfo) (pnode .getUserObject()));
                  if (pmod .module .compareTo (mod .module) != 0){
                     pnode .add (node);
                     nodes .put (name, node);
                     calcLogin2node (login2node, node);
                     calcOrg2node (org2node, node);
                  }
               }
            } else {
               if (mod .type .compareTo ("ORG") == 0){
                  if (mod .level == 0){
                     nodesOrg .put (name, node);
                     bcfOrg = node;
                     calcLogin2node (login2nodeOrg, node);
                     calcOrg2node (org2nodeOrg, node);
                  }
                  if (mod .level > 0){
                     DefaultMutableTreeNode pnode = findParent (mod, nodesOrg);
                     ModInfo pmod = ((ModInfo) (pnode .getUserObject()));
                     if (pmod .module .compareTo (mod .module) != 0){
                        pnode .add (node);
                        nodesOrg .put (name, node);
                        calcLogin2node (login2nodeOrg, node);
                        calcOrg2node (org2nodeOrg, node);
                     }
                  }
               }
            }   
         }
      }catch (IOException e) {
         System.out.println (e);
      }
      
       // Pass the renderer on to the ExpertPanel constructor
      tp = new ExpertPanel(cell, this, hostname, debug);      
   }
   private DefaultMutableTreeNode findParent (ModInfo mod, Hashtable nodes){
      String name = mod .module  + ":" +  mod .level;      
      if (mod .level == 0){
         return (DefaultMutableTreeNode) nodes .get (name);
      } else {
         for (int k = 1; k <= mod .level; k++){
            String pname = mod .module  + ":" +  (mod .level-k);
            DefaultMutableTreeNode res = (DefaultMutableTreeNode) nodes .get (pname);
            if (res != null) return res;
            StringTokenizer st = new StringTokenizer (mod .module, "/");
            for (int n = st .countTokens () - 1; n >= 0; n--){
               pname="";
               for (int i = 0; i < n; i++)
                  pname += "/" + st .nextToken ();
               pname += ":" +  (mod .level-k);
               res = (DefaultMutableTreeNode) nodes .get (pname);
               if (res != null) {
                   //System .err .println ("found parent:" + pname + " for:" + name);
                  return res;
               } else {
                  st = new StringTokenizer (mod .module, "/");
               }
            }
         }
         System .err .println ("could not get parent for:" + name);
         return null;
      }
   }
   public void mainHelper () {
      JFrame frame = new JFrame ("Who does what?");
      frame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {System.exit(0);}
      });      
      frame .setSize(WIDTH+20, HEIGHT+20);
      frame .getContentPane () .add (tp, BorderLayout .CENTER);
      frame .setVisible(true);
      tp .start(null);
      
   }
   public static void main(String args[]) throws MalformedURLException, FileNotFoundException, IOException {
      String options = null;
      if (args .length > 2){
         options = args[2];
      }
      if (args .length >= 2){
         Experts ex = new Experts ("Who does what?",
                                new BufferedReader(new InputStreamReader (new java.util.zip.GZIPInputStream (new FileInputStream (args [0])))),
                                new BufferedReader(new FileReader (args [1])),
                                   "localhost", false, options);
         ex .mainHelper ();
      } else {
         Experts ex = new Experts ("Who does what?", "localhost",
                                   false, options);
          //ex .mainHelper ();
      }
   }
}

