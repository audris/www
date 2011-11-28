package ExV;

import java.net.*;
import java.io.*;

class DummyThread  extends Thread {
   static DatagramSocket ds;
    //static DatagramPacket dp;
   static int port = 9999;   
    //static byte buf [];
   public static void main (String argv []){
       //create dummy thread to see if things work
      
      try {
         ds = new DatagramSocket (port);
      }catch (SocketException e){
         System.err.println (e .getMessage ());
      }
       //buf = new byte [500];
       //dp = new DatagramPacket (buf, buf.length);
      DummyThread dt = new DummyThread ();
      dt .start ();
   }
            
   public void run () {
      while (true){
         try {
            sleep (300);
            byte buf [] = new byte [200];
            DatagramPacket dp = new DatagramPacket (buf, buf.length);
            try {
               ds .receive (dp);
            } catch (IOException e){
               System.err.println ("Error:"+e);
            }
            if (dp != null) {
               String text = new String (dp .getData ());
               text = text .substring (0, dp.getLength());
               System .out .println (dp.getAddress()+":"+dp.getLength()+":" + text);
            }
         }catch (InterruptedException e) {
            System.err.println ("Exception:"+e);
         }
      }
   }
}




