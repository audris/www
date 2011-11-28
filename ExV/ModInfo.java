package ExV;

import java.util.*;

public class ModInfo {
   public int level;
   public String type;
   public String module;
   public int ndelta;

   public Hashtable login2index = new Hashtable ();
   public int ndeltaForLogin (String name){
      Integer off = (Integer)login2index .get (name);
      if (off != null)
         return people [off .intValue ()] .loginDelta;
      else
         return 0;
   }
   public String locationForLogin (String name){
      Integer off = (Integer)login2index .get (name);
      if (off != null)
         return people [off .intValue ()] .location;
      else
         return null;
   }
   public String hridForLogin (String name){
      Integer off = (Integer)login2index .get (name);
       //System.out.println (name + ":" + off);
      if (off != null)
         return people [off .intValue ()] .hrid;
      else
         return null;
   }
   
   public boolean isSelected = false;
   public String loginSelected = "";

   public boolean orgIsSelected = false;
   public String orgSelected = "";
   
   public String [] itemize (String s, char ARRAY_SEPARATOR){
      char ARRAY_ESCAPE = '\\';
      final int l = s.length();
      int ct = 1;
      for (int i = 0; i < l; i++) {
         char c = s.charAt(i);
         if (c == ARRAY_ESCAPE) {
            i++;
         } else if (c == ARRAY_SEPARATOR) {
            ct++;
         }
      }
      String r[] = new String[ct];
      ct = 0;
      r[ct] = new String("");
      for (int i = 0; i < l; i++) {
         char c = s.charAt(i);
         if (c == ARRAY_SEPARATOR) {
            ct++;
            if (ct < r.length) r[ct] = new String("");
         } else {
            if (c == ARRAY_ESCAPE) {
               i++;
               if (i < l) c = s.charAt(i);
            }
            r[ct] += c;
         }
      }
      return r;
   }
   
   public pInf people [];   
   
   public String getModule () {
      if (module != null) return module;
      return "null";
   }
   
   public String name (){
      return module  + ":" +  level;
   }
   
   public ModInfo (String line){      
      String st [] = itemize (line, ';');
      people = new pInf [(st .length - 3) / 4];
      
      String level0 = st [0];
      module = st [1];
      ndelta = (int)(Double .parseDouble (st [2]));
      
      for (int i = 0; i < people .length; i++){
         people [i] = new pInf (st, 3+i*4);
         login2index .put (people [i] .login, new Integer (i));
      }
      
      StringTokenizer st1 = new StringTokenizer (level0, ":");
      level = Integer .parseInt (st1 .nextToken ());
      type = "delta";
      if (st1 .hasMoreElements ())
         type = st1 .nextToken ();
       //System .err .println (line);
   }  
}


