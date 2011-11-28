package ExV;
import java.io.*;

public class ReadThread extends Thread {
	Experts e;
   BufferedReader br;
   String tmp;

	ReadThread (Experts e, BufferedReader br, String tmp){
		super ();
      this.e = e;
      this.br = br;
      this.tmp=tmp;
		start ();
	}

	public void run () {
		try {
			sleep (10000);
         try {
            System.out.println("started continue reading");
            e .continueReading (br, tmp);
            System.out.println("finished continue reading");
         }catch (IOException eio){
            System.out.println (eio);
         }
		}catch (InterruptedException e) {
			System.out.println ("continue read interrupted:"+e);
		};
	}
};
