package ExV;

/**
  An interface for sorting
  To use: implement it by defining length, compare, and exchange functions
  like in
@see SortString
@see SortDouble
@see SortInt

  <br> to sort use
  <pre>
  MySortable implements Sortable{
  public int length () {...}
  public int compare (int i, int j) {...}
  public void exchange (int i, int j) {...}
  }

  main ()
  {
  MySortable a (..);
  int order [] = QSort .order (a);// Returns order
  QSort .sort (a);                // Sorts inline
  }
  </pre>

  <br> To sort int, double, or string array use:
  <pre>
  int i [];
  double d [];
  String s [];
  QSort .sort (i);
  QSort .sort (d);
  QSort .sort (s);
  </pre>

  <br> To order int, double, or string array use:
  <pre>
  int i [];
  int order [] = QSort .order (i);
  </pre>
  <br> To preserve previous order (perform stable sort)
  for int, double, or string array use:
  <pre>
  int i [100];
  double d [100];
  int previousOrder [] = QSort .order (d);
  int newOrder [] = QSort .order (i, previousOrder);
  </pre>
  <br> To specify order (ascending-true or descending-false)
  and preserve previous order (perform stable sort)
  for int, double, or string array use:
  <pre>
  int i [100];
  double d [100];
  int previousOrder [] = QSort .order (d);
  boolean ascending = false;
  int newOrder [] = QSort .order (i, previousOrder, ascending);
  </pre>
@version        Wed Aug 28 17:58:27 1996
@author         A. Mockus
  */

/**
  A quicksort algorithm. With buble sort and median
  search enhancments (a la McIlroys sort in BSD qsort).

@version        Wed Aug 28 17:58:27 1996
@author         A. Mockus
*/
public class QSort {
   private  static int med3 (Sortable x, int a, int b, int c){
      return x .compare (a, b) < 0 ?
         (x .compare (b, c) < 0 ? b : (x .compare (a, c) < 0 ? c : a))
         : (x .compare (b, c) > 0 ? b : (x .compare (a, c) < 0 ? a : c));
   }
   private static void bsort (Sortable a, int lo, int hi){
      for (int j = hi; j > lo; j--)
         for (int i = lo; i < j; i++)
            if (a .compare (i, i+1) > 0)
               a .exchange (i, i+1);
   }
   private static int partition (Sortable a, int lo, int hi){
      if (hi <= lo) return lo;
      
      int mid = (lo + hi)/2;
      int n = hi-lo;
      if (n > 7){
         int l0 = lo, m0 = mid, h0 = hi;
         if (n > 40){
            int d = n/8;
            l0 = med3 (a, l0, l0+d, l0+2*d);
            m0 = med3 (a, m0-d, m0, m0+d);
            h0 = med3 (a, h0 - 2*d, h0 - d, h0);
         }
         mid = med3 (a, l0, m0, h0);
      }
      a .exchange (mid, lo);
      mid = lo;
      lo++;
      while (true) {
         while (lo < hi && a .compare (lo, mid) <= 0) lo ++;
         while (hi > lo && a .compare (hi, mid) >= 0) hi --;
         if (lo < hi) a .exchange(lo, hi);
         else break;
      }
      if (a .compare (mid, lo) < 0) lo --;
      a .exchange (mid, lo);
      return lo;
   }
   
   private static int partition (Sortable a, int o [], int lo, int hi){
      if (hi <= lo) return lo;
      int mid = lo;
      lo++;
      int T = o [lo];
      o [lo] = o [mid];
      o [mid] = T;
      while (true) {
         while (lo < hi && a .compare (o [lo], o [mid]) <= 0) lo ++;
         while (hi > lo && a .compare (o [hi], o [mid]) >= 0) hi --;
         if (lo < hi){
            T = o [lo];
            o [lo] = o [hi];
            o [hi] = T;
         }else break;
      }
      if (a .compare (o [mid], o [lo]) < 0) lo --;
      T = o [lo];
      o [lo] = o [mid];
      o [mid] = T;
      return lo;
   }
    /**
      Sorts (using compare) values inline (using exchange)
      */
   public static void sort (Sortable a, int lo, int hi){
      if (hi > lo && hi - lo < 7){
         bsort (a, lo, hi);
         return;
      }
      int pivot = partition (a, lo, hi);
      if (pivot > lo) sort (a, lo, pivot - 1);
      if (pivot < hi) sort (a, pivot + 1, hi);
   }
   
   public static void sort (Sortable a){ QSort .sort (a, 0, a .length ()-1); }
   
    /**
      Ordering operations -- return, through o,  array describing order
      */
   public static void order (Sortable a, int o [], int lo, int hi){
      int pivot = partition (a, o, lo, hi);
      if (pivot > lo) order (a, o, lo, pivot - 1);
      if (pivot < hi) order (a, o, pivot + 1, hi);
   }
   
   public static int [] order (Sortable a){
      int orderPerm [] = new int [a .length()];
      for(int i = 0; i < a .length (); i++)
         orderPerm [i] = i;
      QSort .order (a, orderPerm, 0, a .length ()-1);
      return orderPerm;
   }

/** Stable ordering -- sort, but preserve the existing order */
   public static int [] order (Sortable a, int ord [], boolean ascending){
      int orderPerm [] = new int [a .length()];
      int invOrder [] = new int [a .length()];
      for(int i = 0; i < a .length (); i++){
         orderPerm [i] = i;
         invOrder [ord [i]] = i;
      }
      StableSortable tmp = new StableSortable (a, invOrder, ascending);
      QSort .order (tmp, orderPerm, 0, a .length ()-1);
      return orderPerm;
   }
   
/** Stable ordering -- sort, but preserve the existing order  */
   public static int [] order (Sortable a, int ord []){ return order (a, ord, true); }
   
    /** Order integer arrays **/
   public static int [] order (int a []){ return QSort .order (new SortInt (a)); }
    /** Stable order (using existing order) of integer array **/
   public static int [] order (int a [], int o []){ return QSort .order (new SortInt (a), o); }
    /** Stable order (using existing order) of integer array **/
   public static int [] order (int a [], int o [], boolean ascending){ return QSort .order (new SortInt (a), o, ascending); }

    /** Sort integer arrays **/
   public static void sort (int a []){ QSort .sort (new SortInt (a)); }
    /** Order double arrays **/
   public static int [] order (double a []){ return QSort .order(new SortDouble (a)); }
    /** Stable order (using existing order) of double array **/
   public static int [] order (double a [], int o []){ return QSort .order (new SortDouble (a), o); }
    /** Stable order (using existing order) of double array **/
   public static int [] order (double a [], int o [], boolean ascending){ return QSort .order (new SortDouble (a), o, ascending); }
    /** Sort double arrays **/
   public static void sort (double a []){ QSort.sort (new SortDouble (a)); }
    /** Order string arrays **/
   public static int [] order (String a []){ return QSort .order(new SortString (a)); }
    /** Stable order (using existing order) of string array **/
   public static int [] order (String a [], int o []){ return QSort .order (new SortString (a), o); }
    /** Stable order (using existing order) of string array **/
   public static int [] order (String a [], int o [], boolean ascending){ return QSort .order (new SortString (a), o, ascending); }
    /** Sort string arrays **/
   public static void sort (String a []){ QSort .sort (new SortString (a)); }

/** invert of a sort **/
	public static int [] invert (int a []){
		int l = a .length;
		int t [] = new int [l];
		for  (int i = 0; i < l; i++){
			t [a [i]] = i;
		}
		return t;
	}


}

/**  Stable sort -- when two elements are the same, use the existing ordering to decide how to arrange them **/
class StableSortable implements Sortable {
   Sortable a;
   int o [];
   boolean ascending;
   StableSortable (Sortable a, int order [], boolean ascending){
      this .a = a;
      this .o = order;
      this .ascending = ascending;
   }
   public int length () { return a.length ();}
   public int compare (int i, int j) {
      int res = a .compare (i, j);
      return res == 0 ?
         o [i] - o [j] :
         (ascending ? res : -res);
   }
   public void exchange (int i, int j){
      a .exchange (i, j);
   }
}

// Classes hidden within this file
class SortString implements Sortable {
	String a [];
	SortString (String a []) { this .a = a; }
   public int length () 	{ return a.length; }
   public int compare (int i, int j) {
      return a [i] .compareTo (a [j]);    // Should use Collator for internationalization
   }

   public void exchange (int left, int right){
      String T = a [left];
      a [left] = a [right];
      a [right] = T;
   }
}

class SortDouble implements Sortable {
   double  a [];

   public int length ()	{ return a .length; }

   public SortDouble (double a []) {this .a = a;}

   public int compare (int i, int j){
      double left  = a [i];
      double right = a [j];
      
      if (left < right) return -1;
      else if (left > right) return 1;
      return 0;
   }
   
   public void exchange (int left, int right){
      double t = a [left];
      a [left] = a [right];
      a [right] = t;
   }
}

class SortInt implements Sortable {
   private int  a [];
   
   public SortInt (int a []) {this.a = a;}
   public int length ()	{ return a.length; }
   public int compare (int i, int j){ return a [i] - a [j]; }
   
   public void exchange (int left, int right){
      int t= a [left];
      a [left] = a [right];
      a [right] = t;
   }
}


