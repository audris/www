package ExV;

public class pInf {
   public String login;
   public int loginDelta;
   public String location;
   public String hrid;
   public pInf (String st [], int off){
      login =  st [off];
      loginDelta = (int)(Double .parseDouble (st [off+1]));
      location = st [off+2];
      hrid = st [off+3];
       //System .err.println (login +":"+ loginDelta + ":" + location);
   }
}
   
