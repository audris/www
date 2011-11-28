package ExV;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


class MyCellRenderer extends JComponent implements ListCellRenderer {
   int height = 10, width=170;
   double percent = 0;
   String location = "";
   boolean isSelected = false;
   String text = "", dispText = "";
   static Color india = new Color (0f,.6f,0f);
   static Color ireland = new Color (0.7f,1f,0.7f);
   static Color france = new Color (0.5f,0.5f,1f);
   static Color australia = new Color (1f,0.5f,0.5f);
   static Color us = Color .cyan;
   static int fontlength = 21;

   private int fontOffset (int height){
      int res = (height-10)/2;
      res = fontlength <= res ? fontlength-1 : res;
      res = res < 0 ? 0 : res;
      return res;
   }
   
      
   
   public Component getListCellRendererComponent(
      JList list,
      Object value,            // value to display
      int index,               // cell index
      boolean isSelected,      // is the cell selected
      boolean cellHasFocus)    // the list and the cell have the focus
   {
      String s = (String)(value);
      text = s;
      
      Color background = (isSelected ? Color.lightGray : Color.white);
      setBackground(background);
      this .isSelected = isSelected;
      
      StringTokenizer st = new StringTokenizer (s, ":");
      if (st .countTokens () == 3){//login:location:percent
         String login = st .nextToken ();
          //if (login .compareTo ("bcfbld") == 0) System.err.println (s +" is selected " + isSelected);
         location = st .nextToken ();            
         String prc = st .nextToken () .trim ();
          //System.err.println ("1Double .valueOf (prc) .doubleValue ()="+prc+":");
         percent = Double .valueOf (prc) .doubleValue ();
         height = (int)(12 + percent * 20);                 
         dispText = login;
      } else {
         String org = st .nextToken ();
         String prc = st .nextToken () .trim ();
          //System.err.println ("2Double .valueOf (prc) .doubleValue ()="+prc+":");
         percent = Double .valueOf (prc) .doubleValue ();
         height = (int)(10 + percent * 20);               
         dispText = org;
      }
      return this;
   }
   public void paint (Graphics g){
       //
       // Let's color each location based on locations first two letters
      
      if (location .startsWith ("dr")||location .startsWith ("co")){
         setBackground(Color .orange);
      }      
      if (location .startsWith ("ho")||location .startsWith ("nj")){
         setBackground(Color .red);
      }
      if (location .startsWith ("cb")){
         setBackground(us);
      }
      if (location .startsWith ("ca")){
         setBackground(Color .green);
      }
      if (location .startsWith ("ma")){
         setBackground(Color .gray);
      }
      if (location .startsWith ("ir")){
         setBackground(ireland);
      }
      if (location .startsWith ("ii")){
         setBackground(india);
      }
      if (location .startsWith ("au")){
         setBackground(australia);
      }
      if (location .startsWith ("tx")){
         setBackground(Color .yellow);
      }
      
      g .setColor(getBackground ());
      g .fillRect (1, 1, width-1, height-1);
      if (isSelected){
         g .setColor(Color.lightGray);
         g .fillRect (3, 3, width-6, height-4);
      }
      g .setColor(Color .black);
      g .setFont (MyJList .fonts [fontOffset(height)]);
      g .drawString (dispText, 1, height-2);
   }
   public Dimension getPreferredSize (){
      return (new Dimension (width, height));
   }
   public Dimension getMinimumSize (){
      return (new Dimension (width, height));
   }
   public Dimension getMaximumSize (){
      return (new Dimension (width, height));
   }
   public Rectangle getBounds (){
      return (new Rectangle (1, 1, width, height));
   }
}

public class MyJList extends JList {
   Font f = new Font ("Times", Font .PLAIN, 12);
   public DefaultListModel model;
   
   static int fontsize = 10;
   static Font fonts [] = new Font[MyCellRenderer.fontlength];
   {
      for(int i=0;i<fonts.length;i++)
         fonts[i] = new Font("TimesRoman",Font.PLAIN,(int)(1.8*i+12));
   }
   
   public MyJList (DefaultListModel model, boolean useRenderer){
      super (model);
      this .model = model;
      setFont (f);
      if (useRenderer) setCellRenderer(new MyCellRenderer());
   }
      
   public void selectItem (String s){
      clearSelection ();
      if (s == null) return;
      int i = 0;
      for (Enumeration ee = model .elements (); ee .hasMoreElements() ; i++)
         if (s .compareTo ((new StringTokenizer ((String)(ee .nextElement()), ":")) .nextToken ()) == 0)
            setSelectedIndex (i);
   }
   
   
   public Dimension getMaximumSize() {
      return new Dimension(400, super.getMaximumSize().height);
   }
}





