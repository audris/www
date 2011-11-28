package ExV;

import javax.swing.*;
import java.awt.*;

class JBar extends JPanel {
   public JBar (){}
   public int width=350;
   public int width1=10;
   private int height=30;
   public double pselected;
   public double pBugs = 0;
   private int theight=10;
   public static int incrementPerDeveloper=4;
   private String text;
   private Color bugCol = new Color (1.0f, 0.1f, 0.1f);
   private Color selCol = new Color (0.2f, 0.2f, 1.0f);
   private Color selCol1 = new Color (0.2f, 0.2f, 1.0f);
   Graphics g;

   String chinesesample = "\u4e00";
   FontMetrics fm = null;
   Font f = new Font ("Times", Font .PLAIN, 18);
   public Dimension getMaximumSize (){
       //System.out.println ("getMinSize:" + width + ":" + height);
      return (new Dimension (width, height));
   }
   public Dimension getMinimumSize (){
       //System.out.println ("getMaxSize:" + width + ":" + height);
      return (new Dimension (width, height));
   }
   public Dimension getPreferredSize (){
       //System.out.println ("getPreferredSize:" + width + ":" + height + ":" + text);
      return (new Dimension (width, height));
   }
   public void setSize (int height, int width){      
      this .height = height;
      if (this .height < theight){
         this .height = theight;
      }
      this .width1 = width;
       //System.out.println ("setSize:" + this .width + ":" + this .height + ":" + this .text + ":theight=" + this .theight);
      repaint ();
   }
   public void setText (String text){
      if (text == null){
         theight = 6;
         width1 = 2;
         height = 6;
         width = 2;
         return;
      }
      if (g != null){
         this .text = text;
         if (fm == null) {
            Font[] allfonts = GraphicsEnvironment.
               getLocalGraphicsEnvironment().getAllFonts();
            for (int j = 0; j < allfonts.length; j++) {
               if (allfonts[j].canDisplayUpTo(chinesesample) == -1) {
                  System.out.println(j+"can do Chinese"+allfonts[j]);
                  f = new Font (allfonts[j] .getFamily(), Font .PLAIN, 18);
                  break;
                   //chinesefonts.add(allfonts[j].getFontName());
               }
            }
            
             // in 1.2
            fm = g .getFontMetrics (f);
            System.out.println(fm);
             g .setFont (f);
             setFont (f);
             // in 1.1
             //setFont (f);
            
         }
          // in 1.2 Rectangle r = fm .getStringBounds (text, g) .getBounds ();
          // should but does not work in 1.1

          //in 1.2
          Rectangle r = new Rectangle (fm .stringWidth (text), fm .getMaxAscent () + fm .getMaxDescent ());
          // in 1.1
           //Rectangle r = new Rectangle (text .length () *10, 14);
         if (r.width > width+4)
            width = r.width+4;
         theight=r.height+2;
      }
       //System.out.println ("setText:" + width + ":" + height + ":" + text + ":theight=" + this .theight);
   }
   
   public void paint (Graphics g){
      this .g = g;
      g .setFont (f);
      g .setColor(getBackground());
      g .fillRect (1, 1, width-1, height-2);
      if (pselected < 1){
         g .setColor(selCol);
         int h= (int)((height-1)*Math .abs (pselected));
         if (h < 2) h = 2;
          //if (pselected > 0){ // do developer delta countr from the bottom
            g .fillRect (1, height-1-h, width1, h);
             //} else {
             //g .setColor(selCol1);
             //g .fillRect (1, 2, width1-2, h);
             //}
      }
      g .setColor(getForeground());
      g .drawRect (1, 1, width1, height-2);
      if (pBugs > 0){
         int h = (int)((height-1)*Math .sqrt (Math .abs (pBugs)));
         if (h == 0) h= 1;         
         g .setColor(bugCol);
          //System.out.println(pBugs +" "+ h + " " + height);
         g .fillRect (width1-6, height-1-h, 7, h);
      }
      g .setColor(getForeground());
      
       // plot user grid
      for (int i = incrementPerDeveloper; i < width1; i+=incrementPerDeveloper) {
         g .drawLine (i, height-3, i, height-2);
         if ((i/incrementPerDeveloper) % 10 == 0)
            g .fillRect (i-1, height-4, 2, 4);
      }
       // plot # of delta grid
      int ndelta10 = (height-12)*(height-12);
      if (ndelta10 > 0){
         int inc = (height-2)*10/ndelta10;
         if (inc > 3){
            for (int i = inc; i <= height-2; i+=inc) 
               g .drawLine (width1-1, i, width1, i);
         } else {
            inc = (height-2)*100/ndelta10;
            if (inc > 3){
               for (int i = inc; i <= height-2; i+=inc) 
                  g .fillRect (width1-3, i-1, 4, 2);
            }else{
               inc = (height-2)*1000/ndelta10;
               if (inc > 5){
                  for (int i = inc; i <= height-2; i+=inc) 
                     g .fillRect (width1-5, i-1, 6, 3);
               }else{
                  inc = (height-2)*10000/ndelta10;
                  if (inc > 8){
                     for (int i = inc; i <= height-2; i+=inc) 
                        g .fillRect (width1-7, i-2, 8, 4);
                  }
               }
            }
         }
      }
      
      if (text != null)
         g .drawString (text, 2, theight-4);
      
   }  
}






