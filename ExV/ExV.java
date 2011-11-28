package ExV;

import java.applet.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import netscape.javascript.JSObject;


public class ExV extends JApplet {
   Experts frame;
   JSObject win = null;
   Thread killer;
   boolean startKiller=false;
   
   public void run (String cmd, String a []){
      try{
         if (win == null) win = JSObject .getWindow (this);
         if (win != null) win.call(cmd, a);
      } catch (Exception e){
          // Don't throw exception information away, print it.
         e.printStackTrace();
      }
   }
   
   public void init () {
      try{
         if (win == null) win = JSObject .getWindow (this);
      }catch (Exception e){
          // Don't throw exception information away, print it.
         e.printStackTrace();
      }
      
      getContentPane () .setLayout (new BorderLayout ());
      try {
         URL url = getDocumentBase ();
         String options = null;
         boolean debug = false;
         if (getParameter ("debug") != null && getParameter ("debug") .compareTo ("true") == 0)
            debug = true;
         if (getParameter ("options") != null)
            options = getParameter ("options");
            
         frame = new Experts ("Who does what?",
                              new BufferedReader(new InputStreamReader (new java.util.zip.GZIPInputStream ((new URL(url, getParameter ("sources"))).openConnection() .getInputStream ()))),
                              new BufferedReader(new InputStreamReader ((new URL(url, getParameter("org"))).openConnection() .getInputStream ())),
                              url .getHost (), debug, options);
         getContentPane () .add (frame .tp, BorderLayout .CENTER);
         getContentPane () .doLayout();
         frame .tp .applet = this;
      }catch (MalformedURLException e){
         System.err.println (e);
      }catch (IOException e){
         System.err.println (e);
      }
   }

   public void start (){
      super .start ();
      frame .tp .start (null);
      System.out.println ("getting started");
      JSObject loc = null;
      try{
         if (win == null) win = JSObject .getWindow (this);
         if (win != null) loc = (JSObject) win .getMember ("location");
      } catch (Exception e){
          // Don't throw exception information away, print it.
         e.printStackTrace();
      }
      System.out.println ("getting started:"+loc);
      if (loc != null) {
         Object href = loc .getMember ("href");
         System.out.println ("href:"+href);
         System.out.println ("base:"+getDocumentBase());
         if (href != null){
            String url = href.toString ();
      
            frame .tp .start ("url="+url+";sources="+getParameter("sources")+";org="+getParameter ("org"));
         }
      }else frame .tp .start (null);
      System.out.println ("started");

      if (startKiller && killer == null) {
         killer = new Thread("killer");
         killer.setPriority(Thread.MAX_PRIORITY); 
         killer.start(); 
      }
   }
   public void stop (){
      System.out.println ("getting stopped");
      super .stop ();
      frame .tp .stop ();
      System.gc();
      System.out.println ("stopped. Used:"+ ((java.lang.Runtime.getRuntime().totalMemory()-java.lang.Runtime.getRuntime().freeMemory())/1000)+"Kb");
   }
   private static void findGroups(ThreadGroup g) {
      if (g == null) {return;}
      else {
         
         int numThreads = g.activeCount();
         int numGroups = g.activeGroupCount();
         System.out.println(g.getName()+" ntreads:"+numThreads+ " ngrp:"+numGroups);
         boolean kil=false;
         if (g.getName() .startsWith ("applet"))
            kil=true;
         Thread[] threads = new Thread[numThreads];
         ThreadGroup[] groups = new ThreadGroup[numGroups];
         g.enumerate(threads, false);
         g.enumerate(groups, false);
         for (int i = 0; i < numThreads; i++)
            killOneThread(threads[i],kil); 
         for (int i = 0; i < numGroups; i++)
            findGroups(groups[i]); 
      } 
   }
   private static void killOneThread(Thread t, boolean kill) {
      if (t == null || t.getName().equals("killer")
          ||  t.getName().equals("AppletEventDispatcher")) {return;} 
      else {
         System.out.println(t.getName() + ":" + kill);
         if (kill) t.stop();
      } 
   }
   
   public void destroy (){
      System.out.println ("getting destroyed");
      super .stop ();
      if (frame .tp != null){
         getContentPane () .remove (frame .tp);
         frame .tp .stop ();
         frame .tp .destroy ();
      }
      if (frame != null) frame .empty();
      frame = null;
      win = null;

      if (startKiller){
         ThreadGroup thisGroup; 
         ThreadGroup topGroup;
         ThreadGroup parentGroup;
         thisGroup = Thread.currentThread().getThreadGroup();
         topGroup  = thisGroup;
         
         boolean stop=false;
         try {
            topGroup .getParent() .checkAccess ();
         }catch (SecurityException e){
            stop = true;
         }
         if (stop)
            parentGroup = topGroup;
         else
            parentGroup = topGroup.getParent();
         while(parentGroup != null && !stop) {
            try {
               parentGroup .checkAccess ();
            }catch (SecurityException e){
               stop = true;
            }
            topGroup  = parentGroup;
            parentGroup = parentGroup.getParent();
         }
         findGroups(topGroup);
      }

      java.lang.Runtime.getRuntime().gc();
      System.out.println ("destroyed. Used:"+ ((java.lang.Runtime.getRuntime().totalMemory()-java.lang.Runtime.getRuntime().freeMemory())/1000)+"Kb");
      super .destroy ();
   }
   
  public ExV () {
      super ();
      System.out.println ("getting created. Used:"+ ((java.lang.Runtime.getRuntime().totalMemory()-java.lang.Runtime.getRuntime().freeMemory())/1000)+"Kb" );
      JRootPane rp = getRootPane ();
      rp.putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
//not in jdk1.3 plugin
//      netscape.applet.Control.setAppletPruningThreshold(1);
   }
    
}

